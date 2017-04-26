package com.example.myapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.TextAdapter;
import com.example.myapplication.model.NotesModle;
import com.example.myapplication.model.RecyclerItemClickListener;
import com.example.myapplication.utils.SdCardUtils;
import com.soundcloud.android.crop.Crop;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NotesListActivity extends AppCompatActivity {
    private static final String TAG = "NotesListActivity";
    private Toolbar toolbar;
    private FloatingActionButton fb;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private List<NotesModle> notes;
    private TextAdapter adapter;
    private ExecutorService executorSingle;

    private long parent_id = -1;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Intent i = getIntent();
        parent_id = i.getLongExtra("parent_id", -1);
        type = i.getStringExtra("type");
        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.text_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        // footViewUpdata();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goEditActivity(notes.get(position).getId(), position, false);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showCustomDialog(position);
            }
        }));

        refresh = (SwipeRefreshLayout) findViewById(R.id.text_refreshLayout);
        //设置下拉刷新的按钮的颜色
        refresh.setColorSchemeResources(R.color.colorPrimary);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_title_notes);
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_bg));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        fb = (FloatingActionButton) findViewById(R.id.creat_bt);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewNotes();
            }
        });
    }

   

    private void initData() {
        executorSingle = Executors.newSingleThreadExecutor();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        executorSingle.execute(new Runnable() {
            @Override
            public void run() {
                notes = DataSupport.where("parent_id like ?", "" + parent_id).find(NotesModle.class);
                if (notes == null) {
                    notes = new ArrayList<>();
                }
                adapter = new TextAdapter(NotesListActivity.this, notes);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });


    }


    private void createNewNotes() {
        NotesModle newNotes = new NotesModle();
        newNotes.setParent_id(parent_id);
        newNotes.setCreate_data(System.currentTimeMillis());
        newNotes.setChange_data(System.currentTimeMillis());
        newNotes.setColor(0);
        newNotes.setText_size(12);
        if (newNotes.save()) {
            NotesModle findNote = DataSupport.findLast(NotesModle.class);
            notes.add(findNote);
            adapter.notifyItemInserted(notes.size() - 1);
            goEditActivity(findNote.getId(), notes.size() - 1, true);
        } else {
            TipShow("creat fail");
        }

    }

    private int editPosition = -1;

    private void goEditActivity(long note_id, int position, boolean canEdit) {
        editPosition = position;
        Intent i = new Intent(NotesListActivity.this, NoteActivity.class);
        i.putExtra("id", note_id);
        i.putExtra("edit", canEdit);
        startActivityForResult(i, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 1000:
                if ( resultCode == RESULT_OK) {
                    //adapter.notifyItemChanged();
                    long editNoteId = data.getLongExtra("resultCode_id", -1);
                    NotesModle editModle = DataSupport.find(NotesModle.class, editNoteId);
                    if (editModle == null) {
                        return;
                    }
                    notes.remove(editPosition);
                    notes.add(editPosition, editModle);
                    adapter.notifyItemChanged(editPosition);
                }
                break;
            case Crop.REQUEST_PICK:
                beginCrop(data.getData());
                break;
            case Crop.REQUEST_CROP:
                handleCrop(resultCode, data);
            break;
            default:
                break;
        }

        if (requestCode == 1000 && resultCode == RESULT_OK) {
            //adapter.notifyItemChanged();
            long editNoteId = data.getLongExtra("resultCode_id", -1);
            NotesModle editModle = DataSupport.find(NotesModle.class, editNoteId);
            if (editModle == null) {
                return;
            }
            notes.remove(editPosition);
            notes.add(editPosition, editModle);
            adapter.notifyItemChanged(editPosition);
        }
    }




    private void TipShow(String tip) {
        Snackbar.make(fb, tip, Snackbar.LENGTH_SHORT).show();
    }

    private void loadMore() {
    }


    /*上拉加载更多*/
    private void footViewUpdata() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                //停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取最后一个完全显示的item position
                    // int lastVisibleItem = manager.findLastCompletelyVisibleItemPositions(new int[2]);
                    int lastVisibleItem = manager.findLastVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    // 判断是否滚动到底部并且是向下滑动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        loadMore();
                    }
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isSlidingToLast = dy > 0;
                // 隐藏或者显示fab
                if (dy > 0) {
                    fb.hide();
                } else {
                    fb.show();
                }
            }
        });
    }


    private int positionForImage=-1;
    private void showCustomDialog(final int position) {
        positionForImage=position;
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_note_list, (ViewGroup) findViewById(R.id.dialog_notelist));
        TextView tv_delete= (TextView) v.findViewById(R.id.dialog_notelist_tv1);
        TextView tv_img= (TextView) v.findViewById(R.id.dialog_notelist_tv2);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();
        setDialogAttributes(dialog);

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(position);
                dialog.dismiss();
            }
        });
    }

    private void pickImage(int position) {
        //Crop.of(inputUri, outputUri).asSquare().start(activity)修改一个图片
        Crop.pickImage(this);
    }
    private void beginCrop(Uri source) {
        File file =new File(SdCardUtils.getNormalCardPath()+SdCardUtils.textDirName+
                File.separator+"imagePicked") ;
        if(!file.exists()){
              file.mkdirs();
        }
        Uri destination = Uri.fromFile(new File(file,"img"+System.currentTimeMillis()));
        Crop.of(source, destination).asSquare().start(this);

    }
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
          //   resultView.setImageURI(Crop.getOutput(result));
            NotesModle modle=notes.get(positionForImage);
            modle.setImg_url(Crop.getOutput(result).toString());
            modle.update(modle.getId());
        } else if (resultCode == Crop.RESULT_ERROR) {
            TipShow(Crop.getError(result).getMessage());
        }
    }

    private void setDialogAttributes(AlertDialog dialog) {
        //放在show()之后，不然有些属性是没有效果的，比如height和width
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        DisplayMetrics dm = new DisplayMetrics();//获取当前屏幕的宽高用
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //设置高度和宽度
        p.width = (int) (dm.widthPixels * 0.8); // 宽度设置为屏幕的0.6
        p.height=dialogWindow.getAttributes().height;
        //设置位置
        // p.gravity = Gravity.BOTTOM;
        //设置透明度
        //p.alpha = 0.5f;
        dialogWindow.setAttributes(p);

    }
}

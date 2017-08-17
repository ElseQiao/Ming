package com.example.myapplication.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.TextAdapter;
import com.example.myapplication.filepicker.FilePickerActivity;
import com.example.myapplication.filepicker.Utils;
import com.example.myapplication.model.NotesModle;
import com.example.myapplication.model.RecyclerItemClickListener;
import com.example.myapplication.utils.ExportUtil;
import com.example.myapplication.utils.ImportUtil;
import com.example.myapplication.utils.SdCardUtils;
import com.soundcloud.android.crop.Crop;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class NotesListActivity extends AppCompatActivity {
    private static final String TAG = "NotesListActivity";
    private final int noteList_requestCode=1000;
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
        footViewUpdata();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position>=notes.size()){
                    return;
                }
                goEditActivity(notes.get(position).getId(), position, true);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if(position>=notes.size()){
                    return;
                }
                showCustomDialog(position);
            }
        }));

        refresh = (SwipeRefreshLayout) findViewById(R.id.text_refreshLayout);
        //设置下拉刷新的按钮的颜色
        refresh.setColorSchemeResources(R.color.colorPrimary);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("文章列表("+type+")");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_bg));
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.action_importnote){
                    onNotePicker();
                }
                return false;
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

    private int limit=8;
    private int offset=0;

    private void initData() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        notes = DataSupport.where("parent_id like ?", "" + parent_id).order("id desc")
                .limit(limit).offset(offset).find(NotesModle.class);
        if (notes == null) {
                    notes = new ArrayList<>();
        }
        offset=offset+notes.size();
        adapter = new TextAdapter(NotesListActivity.this, notes);
        recyclerView.setAdapter(adapter);
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
//            adapter.notifyItemInserted(notes.size() - 1);
//            goEditActivity(findNote.getId(), notes.size() - 1, false);
            adapter.notifyItemInserted(0);
            goEditActivity(findNote.getId(), 0, false);
        } else {
            TipShow("creat fail");
        }

    }

    private int editPosition = -1;

    private void goEditActivity(long note_id, int position, boolean onlyRead) {
        editPosition = position;
        Intent i=null;
        if(onlyRead){
            i = new Intent(NotesListActivity.this, NoteActivity.class);
        }else{
            i = new Intent(NotesListActivity.this, NoteEditActivity.class);
        }
        i.putExtra("id", note_id);
        startActivityForResult(i, noteList_requestCode);
    }


    private static final int FILE_CODE =2000;
    private void onNotePicker() {
        // This always works
        Intent i = new Intent(this, FilePickerActivity.class);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case noteList_requestCode:
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
                if ( resultCode == RESULT_OK) {
                    beginCrop(data.getData());
                }
                break;
            case Crop.REQUEST_CROP:
                if ( resultCode == RESULT_OK) {
                    handleCrop(resultCode, data);
                }
            break;
            case FILE_CODE:
                if ( resultCode == RESULT_OK) {
                List<Uri> uris = Utils.getSelectedFilesFromResult(data);
                ImportUtil.saveNotes(uris,parent_id);
                refreshList();
                }
                break;
            default:
                break;
        }
    }

    private void refreshList() {
        offset=0;
        List<NotesModle> newNotes=DataSupport.where("parent_id like ?", "" + parent_id).order("id desc")
                .limit(limit).offset(offset).find(NotesModle.class);
        if (newNotes == null || newNotes.size() == 0) {
            return;
        }

        notes.clear();
        notes.addAll(newNotes);
        offset=offset+notes.size();

        adapter.notifyDataSetChanged();
    }


    private void TipShow(String tip) {
        Snackbar.make(fb, tip, Snackbar.LENGTH_SHORT).show();
    }

    private void loadMore() {
        List<NotesModle> moreNotes= DataSupport.where("parent_id like ?", "" + parent_id).order("id desc")
                .limit(limit).offset(offset).find(NotesModle.class);
        if (moreNotes == null||moreNotes.size()==0) {
            return;
        }
        int fromStart=notes.size()+1;
        offset=offset+moreNotes.size();//计算下一次偏移量
        notes.addAll(moreNotes);
        adapter.notifyItemRangeInserted(fromStart,moreNotes.size());
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
                // fb.show();//停止滚动显示
                //fb.hide();//滚动时隐藏
            }
        });
    }


    private int positionForImage=-1;
    private void showCustomDialog(final int position) {
        positionForImage=position;
        //1.我们想要的view样式
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_note_list, (ViewGroup) findViewById(R.id.dialog_notelist));
        TextView tv_delete= (TextView) v.findViewById(R.id.dialog_notelist_tv1);
        TextView tv_img= (TextView) v.findViewById(R.id.dialog_notelist_tv2);
        TextView tv_save= (TextView) v.findViewById(R.id.dialog_notelist_tv3);

        //2.创建dialog
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();
        setDialogAttributes(dialog);

        //3.事件处理
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesModle nm=notes.get(position);
                DataSupport.delete(NotesModle.class, nm.getId());
                notes.remove(position);
                adapter.notifyItemRangeRemoved(position,1);
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

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotesModle nm=notes.get(position);
                if(nm!=null){
                    TipShow(ExportUtil.exportNote(nm));
                }
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
       // Crop.of(source, destination).withAspect(9,16).withMaxSize(720,1280).start(this);
    }
   //
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
          //   resultView.setImageURI(Crop.getOutput(result));
            NotesModle modle=notes.get(positionForImage);
            modle.setImg_url(Crop.getOutput(result).toString());
            modle.update(modle.getId());
            adapter.notifyItemChanged(positionForImage,modle);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notelist_menu,menu);
        return true;
    }
}

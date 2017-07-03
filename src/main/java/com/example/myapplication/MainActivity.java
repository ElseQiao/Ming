package com.example.myapplication;

import android.app.Activity;
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
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import com.example.myapplication.activity.LockActivity;
import com.example.myapplication.activity.NotesListActivity;
import com.example.myapplication.adapter.DirsAdapter;
import com.example.myapplication.filepicker.FilePickerActivity;
import com.example.myapplication.filepicker.Utils;
import com.example.myapplication.fragment.DirsDialog;
import com.example.myapplication.model.DirsModle;
import com.example.myapplication.model.NotesModle;
import com.example.myapplication.model.RecyclerItemClickListener;
import com.example.myapplication.utils.ExportUtil;
import com.example.myapplication.utils.ImportUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author qyl
 * @deprecated xi
 * */

public class MainActivity extends AppCompatActivity {
    private static final int FILE_CODE =1000;
    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private FloatingActionButton fb;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private List<DirsModle> dirs;
    private DirsAdapter adapter;
    private int limit=12;
    private int offset=0;
    private ExportUtil exportUtil;
   // private ExecutorService singleExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }



    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager g = new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(g);
        recyclerView.setLayoutManager(g);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==dirs.size()){
                    return;
                }
                Intent i=new Intent(MainActivity.this, NotesListActivity.class);
                i.putExtra("type",dirs.get(position).getTitle());
                i.putExtra("parent_id",dirs.get(position).getId());
                startActivity(i);
            }
            @Override
            public void onItemLongClick(View view, int position) {
                if(position>=dirs.size()){
                    return;
                }
                showCustomDialog(position);
            }
        }));

       // footViewUpdata();

        refresh = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        //设置下拉刷新的按钮的颜色
        refresh.setColorSchemeResources(R.color.colorPrimary);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("文件分类");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.color_bg));
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_main);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TipShow("nothing");
//            }
//        });

        fb = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirsDialog(false,-1);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_export:
                        if(exportUtil==null){
                            exportUtil=new ExportUtil();
                        }
                        exportUtil.oneKeyExport(new ExportUtil.ExportListen() {
                            @Override
                            public void resultOfExport(String result) {
                                TipShow(result);
                            }
                        });
                        break;
                    case R.id.action_import:
                        onDirPicker();
                        break;
                    case R.id.action_lock:
                        Intent lockIntent=new Intent(MainActivity.this, LockActivity.class);
                        String intentType="lock_type";
                        lockIntent.putExtra(intentType,0);
                        startActivity(lockIntent);
                     break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private void initData() {
       // singleExecutor= Executors.newSingleThreadExecutor();
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        dirs = DataSupport.order("id desc").find(DirsModle.class);
        if (dirs == null || dirs.size() == 0) {
              createDirs("new", "默认文件");
        }
        adapter = new DirsAdapter(MainActivity.this, dirs);
        recyclerView.setAdapter(adapter);

    }

    private void createDirs(String title, String introduce) {
        DirsModle m = new DirsModle();
        m.setTitle(title);
        m.setIntroduce(introduce);
        m.setColor(new Random().nextInt(10));
        if (m.save()) {
            dirs.add(0,m);
            if(adapter!=null){
                adapter.notifyDataSetChanged();
            }
        } else {
            TipShow("创建文件失败！！！");
        }
    }





    private void TipShow(String tip) {
        Snackbar.make(fb, tip, Snackbar.LENGTH_SHORT).show();
    }
    private void showDirsDialog(final boolean isEdit,final int editPostion) {
        DirsDialog d = new DirsDialog();
        Bundle bundle=new Bundle();
        if(isEdit&&editPostion<dirs.size()){
            bundle.putString("title",dirs.get(editPostion).getTitle());
            bundle.putString("name",dirs.get(editPostion).getIntroduce());
        }else{
            bundle.putString("title","");
            bundle.putString("name","");
        }
        d.setArguments(bundle);
        d.show(getFragmentManager(), "dirs");
        d.setOnDirsDialogBtClick(new DirsDialog.OnDirsDialogBtClick() {
            @Override
            public void setDirs(String title, String text) {
                if(isEdit){
                    DirsModle modle=dirs.get(editPostion);
                    modle.setTitle(title);
                    modle.setIntroduce(text);
                    modle.update(modle.getId());
                    //实时更新
                    adapter.notifyItemChanged(editPostion);
                }else{
                    createDirs(title, text);
                }
            }
        });
    }


    /*上拉加载更多*/
    private void footViewUpdata() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;
            int[] lastPositions = null;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
                //停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    if (lastPositions == null) {
                        lastPositions = new int[manager.getSpanCount()];
                    }
                    manager.findLastVisibleItemPositions(lastPositions);
                    int lastVisibleItem = findMax(lastPositions);
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

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    private void onDirPicker() {
        // This always works
        Intent i = new Intent(this, FilePickerActivity.class);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            // Use the provided utility method to parse the result
            List<Uri> uris = Utils.getSelectedFilesFromResult(data);
            // Do something with the result...
            ImportUtil i=new ImportUtil();
            i.saveDirs(uris, new ImportUtil.ImportResultListener() {
                @Override
                public void success() {
                    importMore();
                }
            });
        }
    }

    private void importMore() {
        refresh.setRefreshing(true);
        List<DirsModle> newDirs=DataSupport.order("id desc").find(DirsModle.class);
        if (newDirs == null || newDirs.size() == 0) {
            return;
        }
        dirs.clear();
        dirs.addAll(newDirs);
        adapter.notifyDataSetChanged();
        refresh.setRefreshing(false);
    }

    private void loadMore() {


    }


    private int positionForImage=-1;
    private void showCustomDialog(final int position) {
        positionForImage=position;
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_dirs_list, (ViewGroup) findViewById(R.id.dialog_notelist));
        TextView tv_delete= (TextView) v.findViewById(R.id.dialog_dirlist_tv1);
        TextView tv_edit= (TextView) v.findViewById(R.id.dialog_dirlist_tv2);
        TextView tv_save= (TextView) v.findViewById(R.id.dialog_dirlist_tv3);

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setView(v);
        final AlertDialog dialog = builder.create();
        dialog.show();
        setDialogAttributes(dialog);

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long parentId = dirs.get(position).getId();
                DataSupport.deleteAllAsync(NotesModle.class, "parent_id = ?", parentId + "");
                DataSupport.delete(DirsModle.class, parentId);
                dirs.remove(position);
                adapter.notifyItemRemoved(position);
                dialog.dismiss();
            }
        });
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirsDialog(true,position);
                dialog.dismiss();
            }
        });
        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(exportUtil==null){
                    exportUtil=new ExportUtil();
                }
                DirsModle dirsModle = dirs.get(position);
                List<DirsModle> l=new ArrayList<DirsModle>();
                l.add(dirsModle);
                exportUtil.exportDir(l, new ExportUtil.ExportListen() {
                    @Override
                    public void resultOfExport(String result) {
                        TipShow(result);
                    }
                });
                dialog.dismiss();
            }
        });
    }

    private void setDialogAttributes(AlertDialog dialog) {
        //放在show()之后，不然有些属性是没有效果的，比如height和width
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值

        DisplayMetrics dm = new DisplayMetrics();//获取当前屏幕的宽高用
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //设置高度和宽度
        p.width = (int) (dm.widthPixels * 0.9); // 宽度设置为屏幕的0.6
        p.height=dialogWindow.getAttributes().height;
        //设置位置
        // p.gravity = Gravity.BOTTOM;
        //设置透明度
        //p.alpha = 0.5f;
        dialogWindow.setAttributes(p);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  recyclerView.removeOnItemTouchListener();
        if(exportUtil!=null){
            exportUtil.stopExport();
        }
    }
}

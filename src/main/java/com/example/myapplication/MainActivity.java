package com.example.myapplication;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.myapplication.activity.TextActivity;
import com.example.myapplication.adapter.DirsAdapter;
import com.example.myapplication.fragment.DirsDialog;
import com.example.myapplication.model.DirsModle;
import com.example.myapplication.model.RecyclerItemClickListener;
import com.example.myapplication.utils.ExportUtil;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.Random;
/**
 * @author qyl
 * @deprecated xi
 * */

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private FloatingActionButton fb;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private List<DirsModle> dirs;
    private DirsAdapter adapter;
    private int pageCount = 8;
    private int fromId = 1;

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
        StaggeredGridLayoutManager g = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(g);
        // new LinearLayoutManager(this)
        recyclerView.setLayoutManager(g);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(position==dirs.size()){
                    return;
                }
                Intent i=new Intent(MainActivity.this, TextActivity.class);
                i.putExtra("type",dirs.get(position).getTitle());
                i.putExtra("parent_id",dirs.get(position).getId());
                startActivity(i);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        footViewUpdata();

        refresh = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        //设置下拉刷新的按钮的颜色
        refresh.setColorSchemeResources(R.color.colorPrimary);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("文件分类");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_main);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TipShow("nothing");
            }
        });

        fb = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirsDialog();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_export:
                        new ExportUtil().oneKeyExport();
                        break;
                }
                return false;
            }
        });
    }



    private void initData() {
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        dirs = DataSupport.where("id between ? and ?", "" + fromId, "" + (pageCount + fromId)).find(DirsModle.class);
        if (dirs == null || dirs.size() == 0) {
            createDirs("武侠", "武侠内容");
        }
        adapter = new DirsAdapter(this, dirs);
        recyclerView.setAdapter(adapter);
    }



    private void createDirs(String title, String introduce) {
        DirsModle m = new DirsModle();
        m.setTitle(title);
        m.setIntroduce(introduce);
        m.setColor(new Random().nextInt(10));
        if (m.save()) {
            dirs.add(m);
            adapter.notifyDataSetChanged();
        } else {
            TipShow("创建文件失败！！！");
        }
    }



    private void loadMore() {
        fromId = pageCount + fromId;
        List<DirsModle> dirsNew = DataSupport.where("id between ? and ?", "" + fromId, "" + (pageCount + fromId)).find(DirsModle.class);

        if (dirsNew==null||dirsNew.size() == 0) {
            fromId = fromId - pageCount;
            TipShow("没有更多数据了");
            return;
        }
        dirs.addAll(dirsNew);
        adapter.notifyDataSetChanged();
    }

    private void TipShow(String tip) {
        Snackbar.make(fb, tip, Snackbar.LENGTH_SHORT).show();
    }
    private void showDirsDialog() {
        DirsDialog d = new DirsDialog();
        d.show(getFragmentManager(), "dirs");
        d.setOnDirsDialogBtClick(new DirsDialog.OnDirsDialogBtClick() {
            @Override
            public void setDirs(String title, String text) {
                createDirs(title, text);
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
}

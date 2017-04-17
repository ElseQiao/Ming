package com.example.myapplication.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.adapter.DirsAdapter;
import com.example.myapplication.adapter.TextAdapter;
import com.example.myapplication.fragment.DirsDialog;
import com.example.myapplication.model.DirsModle;
import com.example.myapplication.model.NotesModle;
import com.example.myapplication.model.RecyclerItemClickListener;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TextActivity extends AppCompatActivity {
    private static final String TAG = "TextActivity";
    private Toolbar toolbar;
    private FloatingActionButton fb;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private List<NotesModle> notes;
    private TextAdapter adapter;
    private int pageCount = 8;
    private int fromId = 1;

    private long parent_id=-1;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        Intent i=getIntent();
        parent_id=i.getLongExtra("parent_id",-1);
        type=i.getStringExtra("type");
        Log.d(TAG, "onCreate:---- "+parent_id+type);
        initView();
        initData();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.text_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        footViewUpdata();
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                goEditActivity(notes.get(position).getId(),position);
            }
            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        refresh = (SwipeRefreshLayout) findViewById(R.id.text_refreshLayout);
        //设置下拉刷新的按钮的颜色
        refresh.setColorSchemeResources(R.color.colorPrimary);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_title_notes);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.color_bg));
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
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(false);
            }
        });
        notes = DataSupport.where("id between ? and ? and parent_id like ?", "" + fromId, "" + (pageCount + fromId),""+parent_id).find(NotesModle.class);
        if (notes == null) {
            notes=new ArrayList<>();
        }
        adapter = new TextAdapter(this, notes);
        recyclerView.setAdapter(adapter);
    }



    private void createNewNotes() {
        NotesModle newNotes=new NotesModle();
        newNotes.setParent_id(parent_id);
        newNotes.setCreate_data(System.currentTimeMillis());
        newNotes.setColor(0);
        newNotes.setText_size(12);
        if(newNotes.save()){
            NotesModle findNote=DataSupport.findLast(NotesModle.class);
            notes.add(findNote);
            adapter.notifyItemInserted(notes.size()-1);
            goEditActivity(findNote.getId(),notes.size()-1);
        }else {
            TipShow("creat fail");
        }

    }

    private int editPosition=-1;
    private void goEditActivity(long note_id,int position) {
        editPosition=position;
        Intent i=new Intent(TextActivity.this,NoteActivity.class);
        i.putExtra("id",note_id);
        startActivityForResult(i,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1000&&resultCode==1001){
            //adapter.notifyItemChanged();
            long editNoteId=data.getLongExtra("resultCode_id",-1);
            NotesModle editModle=DataSupport.find(NotesModle.class,editNoteId);
            if (editModle==null){
                return;
            }
            notes.remove(editPosition);
            notes.add(editPosition,editModle);
            adapter.notifyItemChanged(editPosition);
        }
    }

    private void TipShow(String tip) {
        Snackbar.make(fb, tip, Snackbar.LENGTH_SHORT).show();
    }

    private void loadMore() {
        fromId = pageCount + fromId;
        List<NotesModle> notesNew = DataSupport.where("id between ? and ? and parent_id like ?", "" + fromId, "" + (pageCount + fromId),""+parent_id).find(NotesModle.class);

        if (notesNew==null||notesNew.size() == 0) {
            fromId = fromId - pageCount;
            TipShow("没有更多数据了");
            return;
        }
        notes.addAll(notesNew);
        adapter.notifyDataSetChanged();
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

}

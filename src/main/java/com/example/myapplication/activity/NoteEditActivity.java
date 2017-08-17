package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.myapplication.R;
import com.example.myapplication.custom.LineEditText;
import com.example.myapplication.custom.SoftInputRelativeLayout;
import com.example.myapplication.model.NotesModle;
import com.example.myapplication.utils.LogTest;

import org.litepal.crud.DataSupport;

public class NoteEditActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private SoftInputRelativeLayout edit_SoftInputRelativeLayout;
    private EditText et_title;
    private LineEditText et_content;
    private NotesModle editNote;
    private FloatingActionButton fb;
    private NestedScrollView ns;
    //private boolean canEdit=true;
    private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_show);
        Intent i=getIntent();
        id=i.getLongExtra("id",-1);
        editNote= DataSupport.find(NotesModle.class,id);
        edit_SoftInputRelativeLayout= (SoftInputRelativeLayout) findViewById(R.id.edit_SoftInputRelativeLayout);
        initView();
        initData();
    }
    private void initData() {
        if(editNote==null){
            return;
        }

        et_title.setText(editNote.getTitle());
        et_content.setText(editNote.getContent());
        // appendContent();
        //  tv.setText(s);

    }

    private int prepareLoad=300;
    private long waitTime =100;
    private Handler handler=new Handler();
    private void appendContent() {
        String content=editNote.getContent();
        if(content.length()<prepareLoad){
            et_content.setText(content);
            return;
        }

        et_content.setText(content.substring(0,prepareLoad));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                et_content.append(editNote.getContent().substring(prepareLoad,editNote.getContent().length()));
            }
        }, waitTime);
    }
    private void initView() {
        SharedPreferences sp=getSharedPreferences("note_style", Context.MODE_PRIVATE);
        edit_SoftInputRelativeLayout.setBackgroundColor(sp.getInt("bgColor", Color.parseColor("#ffc456")));
        et_title=(EditText)findViewById(R.id.title_et);
        et_content=(LineEditText)findViewById(R.id.content_et);
        et_content.setTextSize(sp.getFloat("font_size",18));
        ns= (NestedScrollView) findViewById(R.id.note_show_nestedScrollView);
        fb= (FloatingActionButton) findViewById(R.id.bottom_bt);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ns.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        ns.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(v.getScrollY()>=v.getChildAt(0).getHeight()-v.getHeight()){
                    LogTest.D("NestedTest---------"+"in");
                    fb.hide();
                }else{
                    if(!fb.isShown()){
                        fb.show();
                    }
                }

                LogTest.D("NestedTest---------"+"getScrollY:"+v.getScrollY()+
                ".....getHeight:"+v.getHeight()+".....getHeight:"+v.getChildAt(0).getHeight());
            }
        });

        toolbar = (Toolbar) findViewById(R.id.note_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(true);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_save:
                        saveNote(true);
                        break;
                }
                return false;
            }
        });

        //设置透明状态栏
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
           // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            toolbar.setPadding(0, getStatusBarHeight(this), 0, 0);    //给Toolbar设置paddingTop
        }
    }

    //通过反射获取状态栏高度，默认25dp
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = dip2px(context, 25);
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    //根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveNote(false);
    }

    private void saveNote(boolean goBack) {
        if(editNote==null){
            return;
        }

        String title=et_title.getText().toString();
        String content=et_content.getText().toString();
        editNote.setTitle(title);
        editNote.setContent(content);
        editNote.setChange_data(System.currentTimeMillis());
        editNote.update(id);

        if(goBack){
            Intent backIntent=new Intent();
            backIntent.putExtra("resultCode_id",id);
            backIntent.putExtra("title",title);
            backIntent.putExtra("content",content);
            setResult(RESULT_OK,backIntent);
            onBackPressed();
            // TipShow("保存失败！");
        }
    }

    private void TipShow(String tip) {
        Snackbar.make(et_content, tip, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note,menu);
        return true;
    }

}

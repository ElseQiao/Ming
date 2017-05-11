package com.example.myapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.R;
import com.example.myapplication.custom.LineEditText;
import com.example.myapplication.model.NotesModle;

import org.litepal.crud.DataSupport;

public class NoteShowActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText et_title;
    private LineEditText et_content;
    private NotesModle editNote;
    //private boolean canEdit=true;
    private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_show);
        Intent i=getIntent();
        id=i.getLongExtra("id",-1);
        editNote= DataSupport.find(NotesModle.class,id);
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
        et_title=(EditText)findViewById(R.id.title_et);
        et_content=(LineEditText)findViewById(R.id.content_et);

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

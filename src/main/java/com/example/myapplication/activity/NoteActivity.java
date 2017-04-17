package com.example.myapplication.activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.myapplication.R;
import com.example.myapplication.model.NotesModle;

import org.litepal.crud.DataSupport;

public class NoteActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText et_title;
    private EditText et_content;
    private NotesModle editNote;
    private long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
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
    }

    private void initView() {
        et_title=(EditText)findViewById(R.id.title_et);
        et_content=(EditText)findViewById(R.id.content_et);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolba_title_note);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.color_bg));
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
                switch (item.getItemId()){
                    case R.id.action_save:
                        saveNote();
                        break;
                }
                return false;
            }
        });



//        fb = (FloatingActionButton) findViewById(R.id.creat_bt);
//        fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createNewNotes();
//            }
//        });
    }

    private void saveNote() {
        if(editNote==null){
            return;
        }

        String title=et_title.getText().toString();
        String content=et_content.getText().toString();
        editNote.setTitle(title);
        editNote.setContent(content);
        editNote.setChange_data(System.currentTimeMillis());
        editNote.update(id);

        Intent backIntent=new Intent();
        backIntent.putExtra("resultCode_id",id);
        setResult(1001,backIntent);
        onBackPressed();
          // TipShow("保存失败！");

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

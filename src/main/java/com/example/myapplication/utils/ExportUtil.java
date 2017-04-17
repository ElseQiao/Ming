package com.example.myapplication.utils;

import com.example.myapplication.model.DirsModle;
import com.example.myapplication.model.NotesModle;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by User on 2017/4/14.
 */

public class ExportUtil {
    private String textDirName="yishu"+File.separator;
    private ExecutorService fixPool;
    public ExportUtil(){
        fixPool= Executors.newFixedThreadPool(3);
    }

    public boolean oneKeyExport(){
        List<DirsModle> all = DataSupport.findAll(DirsModle.class);
        for (DirsModle dirsModle:all) {
            exportDir(dirsModle);
        }
        return true;
    }


    public boolean exportDir(final DirsModle dir){
        fixPool.execute(new Runnable() {
            @Override
            public void run() {
                String savePath=SdCardUtils.getNormalCardPath()+textDirName+dir.getTitle();
                List<NotesModle> allNotes = DataSupport.where("parent_id like ?",dir.getId()+"").find(NotesModle.class);
                for (NotesModle note:allNotes) {
                    exportNotes(note,savePath);
                }
            }
        });
        return false;
    }

    public boolean exportNotes(NotesModle notesModle,String savePath) {
        File file=new File(savePath,notesModle.getTitle()+".txt");
        if (!file.getParentFile().exists()){
                file.mkdirs();
        }
        FileWriter f= null;
        try {
            f = new FileWriter(file,false);
            f.write(notesModle.getTitle()+"\n"+notesModle.getContent());
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void stopExport(){
        fixPool.shutdown();
    }
}

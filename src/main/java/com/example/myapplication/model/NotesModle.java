package com.example.myapplication.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by User on 2017/4/6.
 */

public class NotesModle extends DataSupport {
    @Column(defaultValue = "无名")
    private String title;
    @Column(defaultValue = "书写新篇章。。。")
    private String content;
    @Column(defaultValue = "1")
    private int color;
    @Column(defaultValue = "12")
    private int text_size;

    private long create_data;
    private long change_data;
    @Column(defaultValue = "-1")
    private String img_url;
    private long id;
    private long parent_id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getText_size() {
        return text_size;
    }

    public void setText_size(int text_size) {
        this.text_size = text_size;
    }

    public long getCreate_data() {
        return create_data;
    }

    public void setCreate_data(long create_data) {
        this.create_data = create_data;
    }

    public long getChange_data() {
        return change_data;
    }

    public void setChange_data(long change_data) {
        this.change_data = change_data;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}

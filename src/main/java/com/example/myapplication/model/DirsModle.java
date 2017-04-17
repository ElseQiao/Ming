package com.example.myapplication.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by User on 2017/3/30.
 */

public class DirsModle extends DataSupport{
    @Column(defaultValue = "新增文件夹")
    private String title;
    @Column(defaultValue = "")
    private String introduce;

    private int color;
    @Column(defaultValue = "#ffffff")
    private String color_form;

    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getColor_form() {
        return color_form;
    }

    public void setColor_form(String color_form) {
        this.color_form = color_form;
    }
}

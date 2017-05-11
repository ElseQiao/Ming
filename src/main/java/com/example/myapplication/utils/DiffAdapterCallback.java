package com.example.myapplication.utils;

import android.support.v7.util.DiffUtil;

import com.example.myapplication.model.NotesModle;

import java.util.List;

/**
 * Created by User on 2017/5/2.
 */

public class DiffAdapterCallback extends DiffUtil.Callback {
    private List<NotesModle> oldData,newData;

    public DiffAdapterCallback(List<NotesModle> oldData,List<NotesModle> newData){
              this.oldData=oldData;
              this.newData=newData;
    }

    @Override
    public int getOldListSize() {
        return oldData!=null?oldData.size():0;
    }

    @Override
    public int getNewListSize() {
        return newData!=null?newData.size():0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldData.get(oldItemPosition).getId()==newData.get(newItemPosition).getId();
    }
    /**
     * Called by the DiffUtil when it wants to check whether two items have the same data.
     * 被DiffUtil调用，用来检查 两个item是否含有相同的数据
     * DiffUtil uses this information to detect if the contents of an item has changed.
     * DiffUtil用返回的信息（true false）来检测当前item的内容是否发生了变化
     * DiffUtil uses this method to check equality instead of {@link Object#equals(Object)}
     * DiffUtil 用这个方法替代equals方法去检查是否相等。
     * so that you can change its behavior depending on your UI.
     * 所以你可以根据你的UI去改变它的返回值
     * For example, if you are using DiffUtil with a
     * {@link android.support.v7.widget.RecyclerView.Adapter RecyclerView.Adapter}, you should
     * return whether the items' visual representations are the same.
     * 例如，如果你用RecyclerView.Adapter 配合DiffUtil使用，你需要返回Item的视觉表现是否相同。
     * This method is called only if {@link #areItemsTheSame(int, int)} returns
     * {@code true} for these items.
     * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
     * @param oldItemPosition The position of the item in the old list
     * @param newItemPosition The position of the item in the new list which replaces the
     *                        oldItem
     * @return True if the contents of the items are the same or false if they are different.
     */
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//        TestBean beanOld = mOldDatas.get(oldItemPosition);
//        TestBean beanNew = mNewDatas.get(newItemPosition);
//        if (!beanOld.getDesc().equals(beanNew.getDesc())) {
//            return false;//如果有内容不同，就返回false
//        }
        //一般需要作判断内容的更改，但是我们这里只是用作添加新的item，
        //并没有数据修改，不考虑数据问题
        return true;
    }
}

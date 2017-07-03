package com.example.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.DirsModle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017/3/29.
 */

public class DirsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<DirsModle> dirs=new ArrayList<>();
    private int[] cardColors={R.color.color1,R.color.color2,R.color.color3,R.color.color4,R.color.color5,
            R.color.color1,R.color.color2,R.color.color2,R.color.color4,R.color.color5};
    private final int  NORMAL_TYPE=0;
    private final int FOOT_TYPE=1;
    private LayoutInflater layoutInflater;
    public DirsAdapter(Context context,List<DirsModle> dirs){
        this.context=context;
        this.dirs=dirs;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case NORMAL_TYPE:
                return  new DirsViewHolder(layoutInflater.inflate(R.layout.dirslist,parent,false));
            case FOOT_TYPE:
                return  new FootViewHolder(layoutInflater.inflate(R.layout.list_footer,parent,false));

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  DirsViewHolder){
            ((DirsViewHolder)holder).tvTitle.setText(dirs.get(position).getTitle());
            ((DirsViewHolder)holder).tvIntroduce.setText(dirs.get(position).getIntroduce());
            int colorNow=dirs.get(position).getColor();
            if(colorNow<cardColors.length){
                ((DirsViewHolder)holder).tvIntroduce.setBackgroundResource(cardColors[colorNow]);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dirs.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==dirs.size()){
            return FOOT_TYPE;
        }
        return NORMAL_TYPE;
    }

    public class DirsViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private TextView tvIntroduce;
        public DirsViewHolder(View itemView) {
            super(itemView);
            tvTitle= (TextView) itemView.findViewById(R.id.dirs_title);
            tvIntroduce= (TextView) itemView.findViewById(R.id.dirs_tvtitle);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder{
        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }
    //判断是否footview 是footview则展示满屏
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            p.setFullSpan(dirs.size()==holder.getLayoutPosition());//true  的时候会执行
    }


}

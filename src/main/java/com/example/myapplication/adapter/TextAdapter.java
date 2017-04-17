package com.example.myapplication.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.model.NotesModle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 2017/4/7.
 */

public class TextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<NotesModle> noteLists;
    private Context context;
    private int TYPE_NORMAL=0;
    private int TYPE_FOOT=1;
    private LayoutInflater layoutInflater;

    public TextAdapter(Context context,List<NotesModle> noteLists){
        this.noteLists=noteLists;
        this.context=context;
        init();
    }

    private void init() {
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       if(viewType==TYPE_NORMAL){
           return new NoteViewHolder(layoutInflater.inflate(R.layout.noteslist,parent,false));
       }else{
           return new FootViewHolder(layoutInflater.inflate(R.layout.list_footer,parent,false));
       }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof NoteViewHolder){
          ((NoteViewHolder) holder).title.setText(noteLists.get(position).getTitle());
            ((NoteViewHolder) holder).note_content.setText(noteLists.get(position).getContent());
        }

    }

    @Override
    public int getItemCount() {
        return noteLists.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==noteLists.size()){
           return TYPE_FOOT;
        }
        return TYPE_NORMAL;
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView note_content;
        public NoteViewHolder(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.note_title);
            note_content= (TextView) itemView.findViewById(R.id.note_content);
        }
    }

    public class FootViewHolder extends RecyclerView.ViewHolder{
        public FootViewHolder(View itemView) {
            super(itemView);
        }
    }

    private String getCustomTime() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(System.currentTimeMillis()));
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager ){
           ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
               @Override
               public int getSpanSize(int position) {
                   if (position==noteLists.size()){
                       return ((GridLayoutManager) layoutManager).getSpanCount();
                   }
                   return 1;
               }
           });
        }

    }
}

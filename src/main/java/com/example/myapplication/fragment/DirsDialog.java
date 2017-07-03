package com.example.myapplication.fragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication.R;




/**
 * Created by User on 2017/3/30.
 */

public class DirsDialog extends DialogFragment implements View.OnClickListener {


    TextView textView;
    private EditText dirdialogEditTextTitle;
    private  EditText dirdialogEditTextText;
    private Button dirdialogCreate;
    private Button dirdialogCancel;

    private String title;
    private String introduce;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title=getArguments().getString("title");
        introduce=getArguments().getString("name");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_dirs, container, false);
         getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView(v);
        return v;
    }

    private void initView(View v) {
        dirdialogCreate= (Button) v.findViewById(R.id.dirdialog_create);
        dirdialogCancel= (Button) v.findViewById(R.id.dirdialog_cancel);
        dirdialogEditTextTitle= (EditText) v.findViewById(R.id.dirdialog_editText_title);
        dirdialogEditTextText= (EditText) v.findViewById(R.id.dirdialog_editText_text);
        dirdialogCreate.setOnClickListener(this);
        dirdialogCancel.setOnClickListener(this);
        if(!"".equals(introduce)||!"".equals(title)){
            dirdialogEditTextTitle.setText(title);
            dirdialogEditTextText.setText(introduce);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wh=(dm.widthPixels)*4/5;
        getDialog().getWindow().setLayout(wh,getDialog().getWindow().getAttributes().height);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dirdialog_create:
                if (onDirsDialogBtClick != null) {
                    onDirsDialogBtClick.setDirs(dirdialogEditTextTitle.getText().toString(),
                            dirdialogEditTextText.getText().toString());
                }
                DirsDialog.this.dismiss();
                break;
            case R.id.dirdialog_cancel:
                DirsDialog.this.dismiss();
                break;
        }
    }

    private OnDirsDialogBtClick onDirsDialogBtClick;

    public void setOnDirsDialogBtClick(OnDirsDialogBtClick onDirsDialogBtClick) {
        this.onDirsDialogBtClick = onDirsDialogBtClick;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public interface OnDirsDialogBtClick {
        void setDirs(String title, String text);
    }

    ;


}

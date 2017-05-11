package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LockActivity extends AppCompatActivity {
    private static final String TAG = "LockActivity";
    private PatternLockView mPatternLockView;
    private int modle;
    private final int CREATE_LOCK=0;
    private final int CHANGE_LOCK=1;
    private final int CRACK_LOCK=2;
    private String spName="lock";
    private String lockName="save_lock";
    private String intentType="lock_type";
    private SharedPreferences sp;

    private TextView tv_tip;
    private Button bt_clean;
    //private Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        Intent intent=getIntent();
        modle=intent.getIntExtra(intentType,0);
        sp=getSharedPreferences(spName, Context.MODE_PRIVATE);
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        tv_tip=(TextView)findViewById(R.id.lock_texttip);
        bt_clean=(Button)findViewById(R.id.lock_button_clean);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
    }

    private void initLockType(){
        if(modle==CREATE_LOCK){
            //如果是新建，先判断是否已有lock，直接修改
            if("".equals(sp.getString(lockName,""))){
                //“”表示不存在lock 本次为创建
                tv_tip.setText("输入你的手势！");
                return;
            }
            //表示已经存在，本次为修改
            modle=CHANGE_LOCK;
            tv_tip.setText("确认已经保存的图案！");
            return;
        }
        //本次为解锁过程
        tv_tip.setText("芝麻开门");
    }

    private String firstLock;
    private void dealPattern(String patternString) {
        switch (modle){
            case CREATE_LOCK:
                if(firstLock==null){
                    firstLock=patternString;
                    tv_tip.setText("再次确认图案");
                    return;
                }
                if(!firstLock.equals(patternString)){
                    firstLock=patternString;
                    tv_tip.setText("两次输入的图案不同！");
                    return;
                }
                sp.edit().putString(lockName,patternString).apply();
                TipShow("保存成功");
                goMain();
                break;
            case CHANGE_LOCK:
                if(sp.getString(lockName,"").equals(patternString)){
                    modle=CREATE_LOCK;
                    tv_tip.setText("输入你的手势！");
                }else {
                    TipShow("保存手势不同");
                }

                break;
            case CRACK_LOCK:
                if(sp.getString(lockName,"").equals(patternString)){
                    goMain();
                }else{
                    TipShow("手势有误");
                }
                break;
            default:
                break;
        }
    }

    private void goMain() {
        Intent i=new Intent(LockActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPatternLockView.removePatternLockListener(mPatternLockViewListener);
    }

    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
            dealPattern(PatternLockUtils.patternToString(mPatternLockView, pattern));
            mPatternLockView.clearPattern();
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };

    private void TipShow(String tip) {
        Snackbar.make(tv_tip, tip, Snackbar.LENGTH_SHORT).show();
    }
}

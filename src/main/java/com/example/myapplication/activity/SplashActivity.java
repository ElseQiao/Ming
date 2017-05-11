package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends AppCompatActivity {
     private Handler handler=new Handler();
     private SharedPreferences sp;
     private String spName="lock";
     private String lockName="save_lock";
     private String intentType="lock_type";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sp=getSharedPreferences(spName, Context.MODE_PRIVATE);

    }

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            String lockString=sp.getString(lockName,"");
            Intent i;
            if("".equals(lockString)){
                i=new Intent(SplashActivity.this, MainActivity.class);
            }else{
                i=new Intent(SplashActivity.this, LockActivity.class);
                i.putExtra(intentType,2);
            }
            startActivity(i);
            finish();
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable,2000);
    }
}

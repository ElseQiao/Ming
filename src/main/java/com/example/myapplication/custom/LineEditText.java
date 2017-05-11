package com.example.myapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

import com.example.myapplication.R;

/**
 * Created by User on 2017/5/3.
 */

public class LineEditText extends AppCompatEditText {
    private Rect mRect;
    private Paint mPaint;
    private Context context;

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    private void init() {
        mRect=new Rect();
        mPaint=new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setColor(ContextCompat.getColor(context,R.color.colorPrimary));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int count=getLineCount();

        if(mRect!=null){
            for(int i=0;i<count;i++){
                int baseline=getLineBounds(i,mRect);
                canvas.drawLine(mRect.left,baseline+1,mRect.right,baseline+1,mPaint);
            }
        }

        super.onDraw(canvas);
    }
}

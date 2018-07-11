package cn.piesat.waterconservation.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by sen.luo on 2018/6/22.
 */

public class NoTouchLinearLayout extends LinearLayout{

  public   boolean isTouch=true;

    public NoTouchLinearLayout(Context context) {
        super(context);
    }

    public NoTouchLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NoTouchLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isTouch;
    }
}

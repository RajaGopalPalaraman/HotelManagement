package com.edot.hotelmanagement;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class RegisterTextView extends TextView {

    public RegisterTextView(Context context) {
        super(context);
    }

    public RegisterTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RegisterTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setTextColor(Color.BLUE);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        setTextColor(Color.argb(0xff,0x33,0xb5,0xe5));
        return super.performClick();
    }
}

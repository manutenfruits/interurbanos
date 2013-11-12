package com.manutenfruits.interurbanos.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

/**
 * Created by manutenfruits on 29/10/13.
 */
public class FavoriteMenuButton extends ToggleButton {

    public FavoriteMenuButton(Context context, AttributeSet attrs){
        super(context, attrs);
        setText(null);
        setTextOn(null);
        setTextOff(null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, heightMeasureSpec);
    }
}

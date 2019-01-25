package com.demokiller.resource;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.demokiller.library.UIinterface;

public class UIutils implements UIinterface {

    @Override
    public Drawable getBackground(Context context) {
        return context.getResources().getDrawable(R.drawable.background);
    }
}
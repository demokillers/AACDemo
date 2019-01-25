package com.demokiller.resource;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class UIutils {

    public static Drawable getImageDrawable(Context ctx) {
        return ctx.getResources().getDrawable(R.drawable.background);
    }

}
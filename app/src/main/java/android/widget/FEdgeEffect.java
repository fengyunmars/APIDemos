package android.widget;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import android.content.Context;
import android.graphics.Canvas;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.FInterpolator;
import android.view.animation.Interpolator;

/**
 * Created by fengyun on 2017/10/31.
 */

public class FEdgeEffect {

    static final double ANGLE = Math.PI / 6;
    static final float SIN = (float) Math.sin(ANGLE);
    static final float COS = (float) Math.cos(ANGLE);

    static final int STATE_IDLE = 0;



    float mRadius;
    float mBaseGlowScale;
    final Rect mBounds = new Rect();

    public void setSize(int width, int height) {
        final float r = width * 0.75f / SIN;
        final float y = COS * r;
        final float h = r - y;
        final float or = height * 0.75f / SIN;
        final float oy = COS * or;
        final float oh = or - oy;

        mRadius = r;
        mBaseGlowScale = h > 0 ? Math.min(oh / h, 1.f) : 1.f;

        mBounds.set(mBounds.left, mBounds.top, width, (int) Math.min(height, h));
    }

}

package android.widget;
import android.content.Intent;

import com.fengyun.android.view.animation.FInterpolator;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Arrays;

/**
 * Created by prize on 2017/9/25.
 */

public class AJunitTest{

    @Test
    public void testViscousFluidInterpolator(){
        System.out.println(FScroller.FViscousFluidInterpolator.viscousFluid(1));
        System.out.println(FScroller.FViscousFluidInterpolator.VISCOUS_FLUID_NORMALIZE);
        System.out.println(FScroller.FViscousFluidInterpolator.VISCOUS_FLUID_OFFSET);
        System.out.println("0.1f ---->" + new FScroller.FViscousFluidInterpolator().getInterpolation(0.1f));
        System.out.println("0.2f ---->" + new FScroller.FViscousFluidInterpolator().getInterpolation(0.2f));
        System.out.println("0.5f ---->" + new FScroller.FViscousFluidInterpolator().getInterpolation(0.5f));
        System.out.println("0.8f ---->" + new FScroller.FViscousFluidInterpolator().getInterpolation(0.8f));
    }

    @Test
    public void testSPLINE_POSITION(){
        String s1 = Arrays.toString(FScroller.SPLINE_POSITION);
        String s2 = Arrays.toString(FScroller.SPLINE_TIME);
        System.out.println("SPLINE_POSITION = " + s1);
        System.out.println("SPLINE_TIME = " + s2);
        System.out.println("DECELERATION_RATE = " + FScroller.DECELERATION_RATE);
    }

}

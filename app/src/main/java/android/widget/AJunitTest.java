package android.widget;


import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by fengyun on 2017/9/25.
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

    @Test
    public void testCreateIndex(){
        Integer[] array = {0,1,2,2,3,4,4,6,8,8,10,2,2,3,3,8};
        System.out.println(Arrays.toString(array));

        int[] res = FGridLayout.FPackedMap.createIndex(array);
        System.out.println(Arrays.toString(res));

        Integer[] compact = FGridLayout.FPackedMap.compact(array,res);
        System.out.println(Arrays.toString(compact));

        FGridLayout.FPackedMap map = new FGridLayout.FPackedMap(array,array);
        Integer[] values = new Integer[array.length];
        for(int i = 0; i < array.length; i ++)
            values[i] = (Integer) map.getValue(i);
        System.out.println(Arrays.toString(values));
    }

    @Test
    public void testTopologicalSort(){
        FGridLayout grid = new FGridLayout();
        FGridLayout.FAxis axis = grid.new FAxis();
        Random random = new Random();
        FGridLayout.FArc[] arcs = new FGridLayout.FArc[20];
        for(int i = 0; i < 20; i ++){
            int min = (int)random.nextInt(100);
            int max = (int)random.nextInt(100);
//            while(max < min){
//                max = (int)random.nextInt(100);
//            }
            FGridLayout.FInterval span = new FGridLayout.FInterval(min, max);
            FGridLayout.FMutableInt value = new FGridLayout.FMutableInt(random.nextInt(100));
            arcs[i] = new FGridLayout.FArc(span, value);
        }
        FGridLayout.FArc[] sorted = axis.topologicalSort(arcs);
        System.out.println(Arrays.toString(arcs));
        System.out.println(Arrays.toString(sorted));
    }

    @Test
    public void testEdgeEffect(){
        System.out.println("FEdgeEffect.ANGLE = "+ FEdgeEffect.ANGLE);
        System.out.println("FEdgeEffect.SIN = "+ FEdgeEffect.SIN);
        System.out.println("FEdgeEffect.COS = "+ FEdgeEffect.COS);
    }
}

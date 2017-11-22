package com.fengyun.app;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FGridLayout;

import com.example.android.apis.R;
import com.fengyun.view.BaseViewCustom;
import com.fengyun.view.CoordinateGraph;


import java.util.Arrays;
import java.util.Random;

/**
 * Created by prize on 2017/10/13.
 */

public class CustomViewActivity extends Activity{

    CoordinateGraph coordinateGraph;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drawBaseViewCustom();
    }

    private void drawBaseViewCustom(){
        setContentView(new BaseViewCustom(this));
    }

    private void drawCoordinateSystem(){
        setContentView(R.layout.customview);
        coordinateGraph = (CoordinateGraph)findViewById(R.id.coordinateGraph);
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
//            coordinateGraph.getmDirectionLines().add(FArcToDirectionLine(arcs[i]));
            coordinateGraph.getPoints().add(new PointF(span.min, span.max));
        }
        FGridLayout.FArc[] sorted = axis.topologicalSort(arcs);
        for(FGridLayout.FArc arc : sorted){
            coordinateGraph.getSortedPoints().add(new PointF(arc.span.min, arc.span.max));
        }
        System.out.println(Arrays.toString(arcs));
        Log.d("arcs before -->", Arrays.toString(arcs));
        System.out.println(Arrays.toString(sorted));
        Log.d("arcs after --->", Arrays.toString(sorted));

    }
}

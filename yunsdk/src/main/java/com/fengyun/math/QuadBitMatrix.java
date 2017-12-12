package com.fengyun.math;

import java.util.Arrays;
import java.util.Collections;

import Jama.Matrix;

/**
 * Created by prize on 2017/12/12.
 */

public class QuadBitMatrix extends RotateMatrix {

    public QuadBitMatrix(double[][] A) {
        super(A);
        if(getRowDimension() != getColumnDimension()){
            throw new IllegalArgumentException("rows count must equals columns count .");
        }
        double[] a = getRowPackedCopy();
        for(int i = 0; i < a.length; i ++){
            if(a[i] != 0 && a[i] != 1){
                throw new IllegalArgumentException("all num must be 0 or 1 .");
            }
        }
    }

    public QuadBitMatrix reverse(){
        double[][] arr = getArrayCopy();
        for(int i = 0; i < arr.length; i ++) {
            Collections.reverse(Arrays.asList(arr[i]));
        }
        return new QuadBitMatrix(arr);
    }

    public QuadBitMatrix transposeCustom(){
        Matrix matrix = super.transpose();
        QuadBitMatrix quadBitMatrix = new QuadBitMatrix(matrix.getArray());
        return quadBitMatrix;
    }
    public QuadBitMatrix rotateAnticlockwise90(){
        return reverse().transposeCustom();
    }
}

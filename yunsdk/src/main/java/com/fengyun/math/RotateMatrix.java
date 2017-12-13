package com.fengyun.math;

import java.util.Arrays;
import java.util.Collections;

import Jama.Matrix;

/**
 * Created by prize on 2017/12/12.
 */

public class RotateMatrix extends Matrix{

    public RotateMatrix(int m, int n) {
        super(m, n);
    }

    public RotateMatrix(int m, int n, double s) {
        super(m, n, s);
    }

    public RotateMatrix(double[][] A) {
        super(A);
    }

    public RotateMatrix(double[][] A, int m, int n) {
        super(A, m, n);
    }

    public RotateMatrix(double[] vals, int m) {
        super(vals, m);
    }

    public RotateMatrix reverse(){
        double[][] arr = getArrayCopy();
        for(int i = 0; i < arr.length; i ++) {
            Collections.reverse(Arrays.asList(arr[i]));
        }
        return new RotateMatrix(arr);
    }

    public RotateMatrix reverseEquals(){
        double[][] arr = getArray();
        for(int i = 0; i < arr.length; i ++) {
            Collections.reverse(Arrays.asList(arr[i]));
        }
        return this;
    }

    public RotateMatrix transposeCustom(){
        Matrix matrix = super.transpose();
        RotateMatrix rotateMatrix = new RotateMatrix(matrix.getArray());
        return rotateMatrix;
    }
    public RotateMatrix rotateAnticlockwise90(){
        return reverse().transposeCustom();
    }

    public RotateMatrix cloneCustom(){
        return new RotateMatrix(getArrayCopy());
    }

    public RotateMatrix projection(RotateMatrix canvasMatrix, int x, int y){
        RotateMatrix result = new RotateMatrix(canvasMatrix.getRowDimension(), canvasMatrix.getColumnDimension());
        for(int i = y; i < y + getRowDimension(); i ++)
            for(int j = x; j < x + getColumnDimension(); j ++){
                result.set(i,j,get(i - y, j - x));
            }
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < getRowDimension(); i ++){
            sb.append("[");
            for (int j = 0; j < getColumnDimension(); j ++){
                double d = get(i, j);
                sb.append(d);
                if(j < getColumnDimension() - 1)
                    sb.append(", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }
}

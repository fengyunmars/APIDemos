package com.fengyun.math;

import com.fengyun.util.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;

import Jama.Matrix;

/**
 * Created by fengyun on 2017/12/12.
 */

public class QuadBitMatrix extends RotateMatrix {

    public QuadBitMatrix(int m, int n){
        super(m, n);
    }

    public QuadBitMatrix(double[][] A) {
        super(A);
//        if(getRowDimension() != getColumnDimension()){
//            throw new IllegalArgumentException("rows count must equals columns count .");
//        }
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
            ArrayUtils.reverse(arr[i]);
        }
        return new QuadBitMatrix(arr);
    }

    public QuadBitMatrix transposeCustom(){
        Matrix matrix = this.copy().transpose();
        QuadBitMatrix quadBitMatrix = new QuadBitMatrix(matrix.getArrayCopy());
        return quadBitMatrix;
    }
    public QuadBitMatrix rotateAnticlockwise90(){
        QuadBitMatrix reverse = reverse();
        QuadBitMatrix transpose = reverse.transposeCustom();
        return transpose;
    }

    public QuadBitMatrix rotateClockwise90(){
        QuadBitMatrix transpose = transposeCustom();
        QuadBitMatrix reverse = transpose.reverse();
        return reverse;
    }

    public QuadBitMatrix cloneCustom(){
        return new QuadBitMatrix(getArrayCopy());
    }

    public QuadBitMatrix projection(QuadBitMatrix canvasMatrix, int x, int y){
        QuadBitMatrix result = new QuadBitMatrix(canvasMatrix.getRowDimension(), canvasMatrix.getColumnDimension());
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
                int d = (int)get(i, j);
                sb.append(d);
                if(j < getColumnDimension() - 1)
                    sb.append(", ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public boolean hasOverlap() {
        for(int i = getRowDimension() - 1; i >= 0; i --)
            for(int j = 0; j < getColumnDimension(); j ++){
                if(get(i, j) == 2)
                    return true;
            }
        return false;
    }
}

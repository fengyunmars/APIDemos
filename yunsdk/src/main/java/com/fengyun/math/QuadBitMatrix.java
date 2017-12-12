package com.fengyun.math;

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
            if(a[i] != 0 || a[i] != 1){
                throw new IllegalArgumentException("all num must be 0 or 1 .");
            }
        }
    }
}

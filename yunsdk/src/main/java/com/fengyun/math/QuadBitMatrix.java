package com.fengyun.math;

import android.support.v7.graphics.Palette;

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

    public boolean hasFillLine(int lineStart, int lineEnd, int columnStart, int columnEnd, boolean[] fillLines) {
        int sum = 0;
        boolean found = false;
        for(int i = lineStart; i < lineEnd; i ++) {
            for (int j = columnStart; j < columnEnd; j++) {
                sum += get(i, j);
            }
            if(sum == columnEnd - columnStart){
                fillLines[i] = true;
                found = true;
            }
            sum = 0;
        }
        return found;
    }

    public boolean hasFillLine(int start, int end, boolean[] fillLines) {
        return hasFillLine(4,24, start, end,fillLines);
    }

    public boolean hasFillLine(boolean[] fillLines) {
        return hasFillLine(4, 14,fillLines);
    }

    public QuadBitMatrix removeLines(boolean[] fillLines,int lineStart, int lineEnd, int columnStart, int columnEnd) {
        QuadBitMatrix quadBitMatrix = cloneCustom();
        for(int i = lineStart; i < lineEnd; i ++) {
            if (fillLines[i] == true) {
                for (int j = columnStart; j < columnEnd; j++) {
                    quadBitMatrix.set(i, j, 0);
                }
            }else {
                continue;
            }
        }
        return quadBitMatrix;
    }

    public QuadBitMatrix removeLines(boolean[] fillLines, int columnStart, int columnEnd) {
       return removeLines(fillLines,4, 24, columnStart, columnEnd);
    }

    public QuadBitMatrix removeLines(boolean[] fillLines) {
        return removeLines(fillLines,4, 14);
    }

    public QuadBitMatrix removeLinesEquals(boolean[] fillLines, int lineStart, int lineEnd, int columnStart, int columnEnd) {
        int index = 0;
        for(int i = lineStart; i < lineEnd; i ++) {
            if (fillLines[i] == true) {
                for (int j = columnStart; j < columnEnd; j++) {
                    set(i, j, 0);
                }
            }else {
                continue;
            }
        }
        return this;
    }

    public QuadBitMatrix removeLinesEquals(boolean[] fillLines, int columnStart, int columnEnd) {
       return removeLinesEquals(fillLines, 4, 24, columnStart, columnEnd);
    }

    public QuadBitMatrix removeLinesEquals(boolean[] fillLines) {
        return removeLinesEquals(fillLines, 4, 14);
    }

    public void fallDown(boolean[] fillLines) {
        fallDown(fillLines, 4, 14);
    }

    public void fallDown(boolean[] fillLines, int columnStart, int columnEnd) {
        fallDown(fillLines, 4, 24, columnStart, columnEnd);
    }

    public void fallDown(boolean[] fillLines, int lineStart, int lineEnd, int columnStart, int columnEnd) {
        int count = 0;
        removeLinesEquals(fillLines, lineStart, lineEnd, columnStart, columnEnd);
        for(int i = lineEnd - 1; i >= lineStart; i --){
            if (fillLines[i]){
                count ++;
            }else {
                fallDownLine(i, count, columnStart, columnEnd);
            }
        }
    }

    public void fallDownLine(int line, int count) {
        fallDownLine(line, count, 4, 14);
    }

    public void fallDownLine(int line, int count, int columnStart, int columnEnd) {
        for(int j = columnStart; j < columnEnd; j ++){
            set(line + count, j ,  get(line, j));
        }
    }

    public QuadBitMatrix shiftRight() {
        for(int j = getColumnDimension() - 2; j >= 0 ; j --)
            for(int i = 0; i < getRowDimension(); i ++){
                set(i, j + 1 , get(i, j));
            }
        for(int i = 0; i < getRowDimension(); i ++)
            set(i, 0, 0);
        return this;
    }

    public QuadBitMatrix shiftLeft() {
        for(int j = 1; j <= getColumnDimension() - 1; j ++)
            for(int i = 0; i < getRowDimension(); i ++){
                set(i, j - 1, get(i, j));
            }
        for(int i = 0; i < getRowDimension(); i ++)
            set(i, getColumnDimension() - 1, 0);
        return this;
    }

    public QuadBitMatrix setExist(int value) {
        for(int i = 0; i < getRowDimension(); i ++)
            for(int j = 0; j < getColumnDimension(); j ++){
                if(get(i,j) == 1){
                    set(i, j, value);
                }
            }
        return this;
    }
}

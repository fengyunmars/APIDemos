package com.fengyun.russiacell.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.fengyun.math.QuadBitMatrix;
import com.fengyun.model.game.cell.spirit.GameButton;
import com.fengyun.russiacell.model.spirit.Cell;
import com.fengyun.russiacell.model.spirit.GameButtonImage;
import com.fengyun.russiacell.model.spirit.Palette;
import com.fengyun.russiacell.model.spirit.PaletteImage;
import com.fengyun.russiacell.model.spirit.Tetris;
import com.fengyun.russiacell.model.spirit.TetrisMountain;
import com.fengyun.util.ImageUtils;
import com.fengyun.view.game.SurfaceGameView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengyun on 2017/12/9.
 */

public class RussiaGameView extends SurfaceGameView{
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public static final String TAG = RussiaGameView.class.getSimpleName();
    public static final int CELL_LAND_BITMAP_INT = 0;
    public static final int CELL_CLASSIC_GREENDRAK_INT = 1;
    private static final int MAX_TETRIS_SIZE = 4;
    public static Bitmap CELL_CLASSIC_GREENDRAK_BITMAP;
    public static Bitmap CELL_LAND_BITMAP;
    Palette mPalette;
    Bitmap background;
    public Bitmap mLandCellBitmap;
    Tetris currentTetris;
    public List<GameButton> mGameButtonList = new ArrayList<>();
    public GameButton buttonMoveLeft;
    public GameButton buttonMoveRight;
    public GameButton buttonMoveDown;
    public GameButton buttonMoveUp;
    public GameButton buttonTransformation;

    public QuadBitMatrix landMatrix = new QuadBitMatrix(GameViewConfig.PALETTE_CELL_ARRAY);
    public static final QuadBitMatrix emptyMatrix = new QuadBitMatrix(28,18);

    public RussiaGameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        background = ImageUtils.getAssetBitmap("scene/capture_hollow.png");
        mPalette = new PaletteImage(300, 280 , ImageUtils.getAssetBitmap("scene/palette_new.png"));
        CELL_LAND_BITMAP = ImageUtils.getAssetBitmap("cell/cell_classic_yellowlight.png", Palette.CELLWIDTH_DEFAULT - 2);
        CELL_CLASSIC_GREENDRAK_BITMAP = ImageUtils.getAssetBitmap("cell/cell_classic_greendark.png", Palette.CELLWIDTH_DEFAULT - 2);
        currentTetris = new TetrisMountain(0, 0, 0, CELL_CLASSIC_GREENDRAK_INT);
        initButtons();
//        Bitmap bitmap = ImageUtils.getAssetBitmap("scene/capture_new.png");
//        File path = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        Bitmap ne = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 2030);
//        ImageUtils.saveBitmap(ne, new File(path, "cature_new.png"));
//        ImageUtils.saveBitmap(ImageUtils.hollowOutBitmap(bitmap, 300, 280, 756, 1499, path), new File(path, "capture_hollow.png"));
    }

    private void initButtons() {
        buttonMoveDown = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return handleTetrisMoveDown(currentTetris);
            }
        };
        buttonMoveDown.setLocation(GameViewConfig.BUTTON_FIVE_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        buttonMoveUp = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return handleTetrisMoveUp(currentTetris);
            }
        };
        buttonMoveUp.setLocation(GameViewConfig.BUTTON_ONE_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        buttonMoveLeft = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return handleTetrisMoveLeft(currentTetris);
            }
        };
        buttonMoveLeft.setLocation(GameViewConfig.BUTTON_TWO_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        buttonMoveRight = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return handleTetrisMoveRight(currentTetris);
            }
        };
        buttonMoveRight.setLocation(GameViewConfig.BUTTON_FOUR_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);

        buttonTransformation = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return handleTetrisTransformation(currentTetris);
            }
        };
        buttonTransformation.setLocation(GameViewConfig.BUTTON_THREE_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        mGameButtonList.add(buttonMoveDown);
        mGameButtonList.add(buttonMoveUp);
        mGameButtonList.add(buttonMoveLeft);
        mGameButtonList.add(buttonMoveRight);
        mGameButtonList.add(buttonTransformation);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
    }

    @Override
    public void updatePulse() {
        if(currentTetris.getStatus() == Tetris.STATUS_FALL){
            handleTetrisFall(currentTetris);
        }
    }

    public boolean tetrisCanFall(Tetris tetris) {
        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix, tetris.cx + MAX_TETRIS_SIZE,
                tetris.cy + MAX_TETRIS_SIZE + 1);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            return false;
        }
        return true;
    }

    public void handleTetrisFall(Tetris tetris) {
        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix,
                tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE + 1);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            handleTetrisLand(tetris);
        }else {
            synchronized (lock) {
                tetris.moveDown();
            }
        }
    }

    public void handleTetrisLand(Tetris tetris) {
        tetris.setStatus(Tetris.STATUS_LAND);
        currentTetris = generateRandomTetris();
        QuadBitMatrix currentProjection = tetris.getMatrix().projection(emptyMatrix,
                tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE);
        synchronized (lock) {
            landMatrix.plusEquals(currentProjection);
        }
        boolean[] fillLines = new boolean[28];
        if(landMatrix.hasFillLine(fillLines)){
            synchronized (lock) {
                landMatrix.fallDown(fillLines);
            }
        }
    }

    public boolean handleTetrisTransformation(Tetris tetris) {

        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix transform = tetrisMatrix.rotateClockwise90();
        QuadBitMatrix nextProjection = transform.projection(emptyMatrix,
                tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            Log.d("dingxiaoquan", TAG + "tetris transform with  overlap!");
        }else {
            synchronized (lock) {
                tetris.transformation();
            }
        }
        return true;
    }
    public boolean handleTetrisMoveDown(Tetris tetris) {

        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix,
                tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE + 1);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            handleTetrisLand(tetris);
        }else {
            synchronized (lock) {
                tetris.moveDown();
            }
        }
        return true;
    }
    public boolean handleTetrisMoveUp(Tetris tetris) {

        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix,
                tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE - 1);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            Log.d("dingxiaoquan", TAG + "tetris move up reach boundary !");
        }else {
            synchronized (lock) {
                tetris.moveUp();
            }
        }
        return true;
    }
    public boolean handleTetrisMoveLeft(Tetris tetris) {

        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix, tetris.
                cx + MAX_TETRIS_SIZE - 1, tetris.cy + MAX_TETRIS_SIZE);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            Log.d("dingxiaoquan", TAG + "tetris move left reach boundary !");
        }else {
            synchronized (lock) {
                tetris.moveLeft();
            }
        }
        return true;
    }
    public boolean handleTetrisMoveRight(Tetris tetris) {
        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        int cx = tetris.cx;

//        QuadBitMatrix shiftMatrix = tetrisMatrix.cloneCustom().shiftRight();
//        QuadBitMatrix shiftNextProjection = shiftMatrix.projection(emptyMatrix, tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE);
//        QuadBitMatrix shiftSum = (QuadBitMatrix) shiftNextProjection.plusEquals(landMatrix);
//        boolean shiftOverlap = shiftSum.hasOverlap();

        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix, tetris.
                cx + MAX_TETRIS_SIZE + 1, tetris.cy + MAX_TETRIS_SIZE);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        boolean overlap = sum.hasOverlap();

        boolean overRight = cx + tetrisMatrix.getColumnDimension() > Palette.CELLSIZE_WIDTH - 1;
        if(overRight){
            if(!overlap){
                synchronized (lock) {
                    tetrisMatrix.shiftRight();
                }
            }
        }else if(overlap){
            Log.d("dingxiaoquan", TAG + "tetris move right reach boundary !");
        }else {
            synchronized (lock) {
                tetris.moveRight();
            }
        }
        return true;
    }

    public Tetris generateRandomTetris() {
        Tetris tetris = new TetrisMountain(0, 0,0,CELL_CLASSIC_GREENDRAK_INT);
        return tetris;
    }

    @Override
    protected void onGameDraw(Canvas canvas) {
        drawScene(canvas);
        mPalette.onDraw(canvas);
        currentTetris.onDraw(canvas);
        drawLandTetris();
        repaint = true;
    }

    public void drawLandTetris() {
        for(int i = MAX_TETRIS_SIZE; i < Palette.CELLSIZE_HEIGHT + MAX_TETRIS_SIZE; i ++)
            for(int j = MAX_TETRIS_SIZE; j < Palette.CELLSIZE_WIDTH + MAX_TETRIS_SIZE ; j ++){
                if(landMatrix.get(i, j) == 1){
                    drawCell(CELL_LAND_BITMAP_INT,j - MAX_TETRIS_SIZE, i - MAX_TETRIS_SIZE);
                }
            }
    }

    public static void drawCell(int ibitmap, int cx, int cy) {
        canvas.drawBitmap(getBitmapByInt(ibitmap), Palette.cellXToPix(cx), Palette.cellYToPix(cy), null);
    }

    private void drawScene(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
    }

    public static Bitmap getBitmapByInt(int ibitmap) {
        switch (ibitmap){
            case CELL_LAND_BITMAP_INT:
                return CELL_LAND_BITMAP;
            case CELL_CLASSIC_GREENDRAK_INT:
                return CELL_CLASSIC_GREENDRAK_BITMAP;
        }
        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for(int i = 0; i < mGameButtonList.size(); i ++){
            mGameButtonList.get(i).onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        Log.d("dingxiaoquan", TAG + " x = " + x + ", y = " + y);
        Log.d("dingxiaoquan", TAG + " rawX = " + rawX + ", rawY = " + rawY);

        return super.onTouchEvent(event);
    }

}

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
import com.fengyun.russiacell.model.spirit.TetrisImageMountain;
import com.fengyun.util.ImageUtils;
import com.fengyun.view.game.SurfaceGameView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prize on 2017/12/9.
 */

public class RussiaGameView extends SurfaceGameView{
    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public static final String TAG = RussiaGameView.class.getSimpleName();
    public static final int CELL_CLASSIC_GREENDRAK_INT = 0;
    public static Bitmap CELL_CLASSIC_GREENDRAK_BITMAP;
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

    public QuadBitMatrix landMatrix = new QuadBitMatrix(23,10);
    public static final QuadBitMatrix emptyMatrix = new QuadBitMatrix(23,10);

    public RussiaGameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        background = ImageUtils.getAssetBitmap("scene/capture_hollow.png");
        mLandCellBitmap = ImageUtils.getAssetBitmap("cell/cell_ring_yellowlight.png");
        mPalette = new PaletteImage(300, 280 , ImageUtils.getAssetBitmap("scene/palette_new.png"));
        CELL_CLASSIC_GREENDRAK_BITMAP = ImageUtils.getAssetBitmap("cell/cell_classic_greendark.png", Palette.CELLWIDTH_DEFAULT - 2);
        currentTetris = new TetrisImageMountain(0, 0, CELL_CLASSIC_GREENDRAK_INT);
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
                return currentTetris.moveDown();
            }
        };
        buttonMoveDown.setLocation(GameViewConfig.BUTTON_FIVE_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        buttonMoveUp = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return currentTetris.moveUp();
            }
        };
        buttonMoveUp.setLocation(GameViewConfig.BUTTON_ONE_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        buttonMoveLeft = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return currentTetris.moveLeft();
            }
        };
        buttonMoveLeft.setLocation(GameViewConfig.BUTTON_TWO_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);
        buttonMoveRight = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return currentTetris.moveRight();
            }
        };
        buttonMoveRight.setLocation(GameViewConfig.BUTTON_FOUR_X_DEFAULT, GameViewConfig.BUTTON_Y_DEFAULT,
                GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT);

        buttonTransformation = new GameButtonImage(){
            @Override
            public boolean onClick() {
                return currentTetris.transformation();
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
    protected void updatePulse() {
        if(currentTetris.getStatus() == Tetris.STATUS_FALL){
            handleTetrisFall(currentTetris);
        }
    }

    public boolean tetrisCanFall(Tetris tetris) {
        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix, tetris.cx, tetris.cy + 1);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap()){
            return false;
        }
        return true;
    }

    public void handleTetrisFall(Tetris tetris) {

        QuadBitMatrix tetrisMatrix = tetris.getMatrix();
        QuadBitMatrix nextProjection = tetrisMatrix.projection(emptyMatrix, tetris.cx, tetris.cy + 1);
        QuadBitMatrix sum = (QuadBitMatrix) nextProjection.plusEquals(landMatrix);
        if(sum.hasOverlap() || tetris.cy == 19){
            QuadBitMatrix currentProjection = tetrisMatrix.projection(emptyMatrix, tetris.cx, tetris.cy);
            landMatrix.plusEquals(currentProjection);
            tetris.setStatus(Tetris.STATUS_LAND);
            currentTetris = generateRandomTetris();
        }else {
            tetris.moveDown();
        }
    }

    public Tetris generateRandomTetris() {
        Tetris tetris = new TetrisImageMountain(0, 0, CELL_CLASSIC_GREENDRAK_INT);
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
        for(int i = 0; i < landMatrix.getRowDimension(); i ++)
            for(int j = 0; j < landMatrix.getColumnDimension(); j ++){
                if(landMatrix.get(i, j) == 1){
                    drawCell(i, j);
                }
            }
    }

    public void drawCell(int i, int j) {
        canvas.drawBitmap(mLandCellBitmap, Palette.cellXToPix(i), Palette.cellYToPix(j), null);
    }

    private void drawScene(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
    }

    public static Bitmap getBitmapByInt(int ibitmap) {
        switch (ibitmap){
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

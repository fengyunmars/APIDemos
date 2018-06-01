package com.fengyun.russiacell.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.fengyun.util.ImageUtils;
import com.fengyun.math.QuadBitMatrix;
import com.fengyun.game.spirit.GameButton;
import com.fengyun.russiacell.model.spirit.Palette;
import com.fengyun.russiacell.model.spirit.Tetris;
import com.fengyun.russiacell.model.spirit.TetrisL;
import com.fengyun.russiacell.model.spirit.TetrisLLeft;
import com.fengyun.russiacell.model.spirit.TetrisLine;
import com.fengyun.russiacell.model.spirit.TetrisMountain;
import com.fengyun.russiacell.model.spirit.TetrisSquare;
import com.fengyun.russiacell.model.spirit.TetrisStairsLeft;
import com.fengyun.russiacell.model.spirit.TetrisStairsRight;
import com.fengyun.util.AppUtils;
import com.fengyun.util.DebugUtils;
import com.fengyun.view.game.SurfaceGameView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    public static Bitmap[][] cellBitmapArray = new Bitmap[23][12];

    public static int CELL_LAND_BITMAP_SHAPE_DEFAULT = GameViewConfig.SHAPE_CLASSIC;
    public static int CELL_LAND_COLOR_DEFAULT = GameViewConfig.COLOR_GREEN_DARK;

    public static int screen_width;
    public static int screen_height;

    static Palette mPalette;
    Bitmap background;
    Tetris currentTetris;
    Context context;

    public List<GameButton> mGameButtonList = new ArrayList<>();
    public GameButton buttonMoveLeft;
    public GameButton buttonMoveRight;
    public GameButton buttonMoveDown;
    public GameButton buttonMoveUp;
    public GameButton buttonTransformation;
    public GameButton buttonNavigation;

    public QuadBitMatrix landMatrix = new QuadBitMatrix(GameViewConfig.PALETTE_CELL_ARRAY);
    public QuadBitMatrix landShapeMatrix = new QuadBitMatrix(28,18);
    public QuadBitMatrix landColorMatrix = new QuadBitMatrix(28,18);
    public static final QuadBitMatrix emptyMatrix = new QuadBitMatrix(28,18);

    public Random mRandom = new Random();

    public RussiaGameView(Context context) {
        super(context);
        init();
    }

    private void init() {
        context = getContext();
        int[] screenSize = AppUtils.getDisplayDimensions(getContext());
        screen_width = screenSize[0];
        screen_height = screenSize[1];
        background = ImageUtils.getAssetBitmap(context,"scene/capture_hollow.png");
        float x = Palette.X_DEFAULT * screen_width;
        float y = Palette.Y_DEFAULT * screen_height;
        mPalette = new Palette(Palette.X_DEFAULT * screen_width,
                Palette.Y_DEFAULT * screen_height, context) {
            @Override
            public void onDraw(Canvas canvas) {
                Bitmap bitmap = ImageUtils.getAssetBitmap(context,"scene/palette_new.png");
                canvas.drawBitmap(bitmap, mx, my, null);
            }
        };
        for(int i = 0; i < cellBitmapArray.length; i ++) {
            String shapeString = GameViewConfig.getShapeString(i);
            for (int j = 0; j < cellBitmapArray[0].length; j++) {
                String colorString = GameViewConfig.getColorString(j);
                String file = "cell/cell_" + shapeString + "_" + colorString + ".png";
                Bitmap bitmap = ImageUtils.getAssetBitmap(context,file);
                float cellWidth = mPalette.getCellWidth();
                float f = mPalette.getCellWidth() * screen_width;
                cellBitmapArray[i][j] = ImageUtils.zoomBitmapByHeight(bitmap,
                        mPalette.getCellWidth() * screen_width);
            }
        }
        currentTetris = new TetrisLLeft(1, 1,context, mPalette, GameViewConfig.COLOR_BLUE_LIGHT, GameViewConfig.SHAPE_BOX, 0);
        initButtons();
//        Bitmap bitmap = ImageUtils.getAssetBitmap("scene/capture_new.png");
//        File path = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        Bitmap ne = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), 2030);
//        ImageUtils.saveBitmap(ne, new File(path, "cature_new.png"));
//        ImageUtils.saveBitmap(ImageUtils.hollowOutBitmap(bitmap, 300, 280, 756, 1499, path), new File(path, "capture_hollow.png"));
    }

    private void initButtons() {
        buttonMoveDown = new GameButton(GameViewConfig.BUTTON_FIVE_X_DEFAULT,GameViewConfig.BUTTON_Y_DEFAULT,
        context, GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT,background){
            @Override
            public boolean onClick() {
                return handleTetrisMoveDown(currentTetris);
            }
        };
        buttonMoveUp = new GameButton(GameViewConfig.BUTTON_ONE_X_DEFAULT,GameViewConfig.BUTTON_Y_DEFAULT,
                context, GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT,background){
            @Override
            public boolean onClick() {
                return handleTetrisMoveUp(currentTetris);
            }
        };
        buttonMoveLeft = new GameButton(GameViewConfig.BUTTON_TWO_X_DEFAULT,GameViewConfig.BUTTON_Y_DEFAULT,
                context, GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT,background){
            @Override
            public boolean onClick() {
                return handleTetrisMoveLeft(currentTetris);
            }
        };
        buttonMoveRight = new GameButton(GameViewConfig.BUTTON_FOUR_X_DEFAULT,GameViewConfig.BUTTON_Y_DEFAULT,
                context, GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT,background){
            @Override
            public boolean onClick() {
                return handleTetrisMoveRight(currentTetris);
            }
        };

        buttonTransformation = new GameButton(GameViewConfig.BUTTON_THREE_X_DEFAULT,GameViewConfig.BUTTON_Y_DEFAULT,
                context, GameViewConfig.BUTTON_WIDTH_DEFAULT, GameViewConfig.BUTTON_HEIGHT_DEFAULT,background){
            @Override
            public boolean onClick() {
                return handleTetrisTransformation(currentTetris);
            }
        };

//        buttonNavigation = new GameButtonImage(){
//            @Override
//            public boolean onClick() {
//                Intent intent = new Intent(RussiaGameView.this.getContext(), TouchEventTestActivity.class);
//                RussiaGameView.this.getContext().startActivity(intent);
//                return true;
//            }
//        };
//        buttonNavigation.setLocation(GameViewConfig.BUTTON_NAVIGATION_X_DEFAULT, GameViewConfig.BUTTON_NAVIGATION_Y_DEFAULT,
//                GameViewConfig.BUTTON_NAVIGATION_WIDTH_DEFAULT, GameViewConfig.BUTTON_NAVIGATION_HEIGHT_DEFAULT);
        mGameButtonList.add(buttonMoveDown);
        mGameButtonList.add(buttonMoveUp);
        mGameButtonList.add(buttonMoveLeft);
        mGameButtonList.add(buttonMoveRight);
        mGameButtonList.add(buttonTransformation);
//        mGameButtonList.add(buttonNavigation);
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
//        QuadBitMatrix currentProjection = tetris.getMatrix().projection(emptyMatrix,
//                MAX_TETRIS_SIZE, MAX_TETRIS_SIZE);

        QuadBitMatrix colorProjection = tetris.getColorMatrix().projection(emptyMatrix, tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE);
//        QuadBitMatrix colorProjection = tetris.getColorMatrix().projection(emptyMatrix, MAX_TETRIS_SIZE, MAX_TETRIS_SIZE);
        QuadBitMatrix shapeProjection = tetris.getShapeMatrix().projection(emptyMatrix, tetris.cx + MAX_TETRIS_SIZE, tetris.cy + MAX_TETRIS_SIZE);
//        QuadBitMatrix shapeProjection = tetris.getShapeMatrix().projection(emptyMatrix, MAX_TETRIS_SIZE, MAX_TETRIS_SIZE);
        synchronized (lock) {
            landMatrix.plusEquals(currentProjection);
            landColorMatrix.plusEquals(colorProjection);
            landShapeMatrix.plusEquals(shapeProjection);
        }

        boolean[] fillLines = new boolean[28];
        if(landMatrix.hasFillLine(fillLines)){
            synchronized (lock) {
                landMatrix.fallDown(fillLines);
                landShapeMatrix.fallDown(fillLines);
                landColorMatrix.fallDown(fillLines);
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

        boolean overRight = cx + tetrisMatrix.getColumnDimension() > Palette.CELL_SIZE_WIDTH_DEFAULT - 1;
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
        int type = mRandom.nextInt(7);
        int bitmapShape = mRandom.nextInt(23);
        int color = mRandom.nextInt(12);
        int cx = mRandom.nextInt(4);
        int cy = 0;
        cx = cx + 3;
        Tetris tetris = null;
        switch (type){
            case Tetris.TYPE_L:
                tetris = new TetrisL(cx, cy, context, mPalette, bitmapShape, color, 0);
            case Tetris.TYPE_L_LEFT:
                tetris = new TetrisLLeft(cx, cy, context, mPalette, bitmapShape, color, 0);
            case Tetris.TYPE_LINE:
                tetris = new TetrisLine(cx, cy, context, mPalette, bitmapShape, color, 0);
            case Tetris.TYPE_MOUNTAIN:
                tetris = new TetrisMountain(cx, cy, context, mPalette, bitmapShape, color, 0);
            case Tetris.TYPE_SQUARE:
                tetris = new TetrisSquare(cx, cy, context, mPalette, bitmapShape, color, 0);
            case Tetris.TYPE_STAIRS_LEFT:
                tetris = new TetrisStairsLeft(cx, cy, context, mPalette, bitmapShape, color, 0);
            case Tetris.TYPE_STAIRS_RIGHT:
                tetris = new TetrisStairsRight(cx, cy, context, mPalette, bitmapShape, color, 0);
        }
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
        for(int i = MAX_TETRIS_SIZE; i < Palette.CELL_SIZE_HEIGHT_DEFAULT + MAX_TETRIS_SIZE; i ++)
            for(int j = MAX_TETRIS_SIZE; j < Palette.CELL_SIZE_WIDTH_DEFAULT + MAX_TETRIS_SIZE ; j ++){
                if(landMatrix.get(i, j) == 1){
                    drawCell((int)landShapeMatrix.get(i, j), (int)landColorMatrix.get(i, j ),j - MAX_TETRIS_SIZE, i - MAX_TETRIS_SIZE);
                }
            }
    }

    public static void drawCell(int bitmapShape, int color, int cx, int cy) {
        canvas.drawBitmap(getBitmapByShapeAndColor(bitmapShape, color), mPalette.cellXToPix(cx), mPalette.cellYToPix(cy), null);
    }

    private void drawScene(Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
    }

    public static Bitmap getBitmapByShapeAndColor(int bitmapShape, int color) {
        Bitmap bitmap = cellBitmapArray[bitmapShape][color];
        if(bitmap == null){
            Log.d("dingxiaoquan", "getBitmapByShapeAndColor bitmapShape" + bitmapShape + " color" + color + " with null");
        }
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        DebugUtils.printMotionEvent(event, TAG);

        for(int i = 0; i < mGameButtonList.size(); i ++){
            mGameButtonList.get(i).onTouchEvent(event);
        }
        return true;
    }

}

package com.ldesp.zonaclou;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * view representing the game
 */
public class GameView extends View {
    private static int STATE_RUNNING = 0;
    private static int STATE_LOSS = 1;
    private static int STATE_SUCCESS = 2;
    private static int STATE_WAITING = 3;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Rect mRect = new Rect();
    private ScaledStone[] mTileArray;
    private int mNumFullSites;
    private int mNumFlag = 0;
    private GameData mGameD = null;
    private TextView mText = null;
    private ImageView mImage = null;
    private Activity mActivity = null;
    private boolean mTraining = false;
    private boolean mAlert = false;

    private int gameState = STATE_WAITING;


    public GameView(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
    }

    public GameView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public GameView(Context c) {
        super(c);
    }

    public void configGame(Bundle saveB, Activity acti, boolean flag, boolean flag2, int id1, int id2, int nsites) {
        mActivity = acti;
        mText = (TextView) mActivity.findViewById(R.id.textv1);
        mImage = (ImageView) mActivity.findViewById(R.id.cycleview);

        int[] params = getResources().getIntArray(id1);
        TypedArray tiles = getResources().obtainTypedArray(id2);
        Bitmap[] bm = new Bitmap[tiles.length()];
        Bitmap dummyTile = BitmapFactory.decodeResource(getResources(), R.drawable.cycleperdu);
        for (int i = 0; i < tiles.length(); i++) {
            int id = tiles.getResourceId(i, -1);
            if (id > 0)
                bm[i] = BitmapFactory.decodeResource(getResources(), id);
            else
                bm[i] = dummyTile;
        }

        mNumFullSites = nsites / 6;
        mGameD = new GameData(nsites, bm, params);

        tiles = getResources().obtainTypedArray(R.array.tiles_specific);
        mTileArray = new ScaledStone[tiles.length()];
        for (int itile = 0; itile < tiles.length(); itile++) {
            int id = tiles.getResourceId(itile, -1);
            if (id > 0)
                mTileArray[itile] = new ScaledStone(BitmapFactory.decodeResource(getResources(), id));
            else
                mTileArray[itile] = new ScaledStone(dummyTile);
        }
        if (saveB == null) {
            restart(flag, flag2);
        } else {
            gameState = saveB.getInt("state", STATE_RUNNING);
            mNumFlag = mGameD.fillSites(mNumFullSites, saveB.getByteArray("flags"));
            if (gameState == STATE_RUNNING) {
                mImage.setImageResource(R.drawable.cycleplay);
                setTraining(flag);
                setAlert(flag2);
            }
        }
    }

    public void saveConfigGame(Bundle saveB) {
        saveB.putByteArray("flags", mGameD.getFlags());
        saveB.putInt("state", gameState);
    }

    public void restart(boolean flag, boolean flag2) {
        mNumFlag = 0;
        gameState = STATE_WAITING;
        mGameD.resetFlags();
        mImage.setImageResource(R.drawable.cycleplay);
        setTraining(flag);
        setAlert(flag2);
        if (mBitmap == null)
            return;
        reDraw();
        invalidate();
    }

    public void setTraining(boolean flag) {
        mTraining = flag;
        mText.setVisibility(View.VISIBLE);
        if (flag) {
            mImage.setImageResource(R.drawable.cycletrain);
            mText.setText("  " + mNumFlag + "/" + mNumFullSites);
        } else {
            mImage.setImageResource(R.drawable.cycleplay);
            mText.setText("  " + mNumFlag + "/" + mNumFullSites);
        }
    }

    public void setAlert(boolean flag) {
        mAlert = flag;
    }

    public boolean isGameNotRunning() {
        return (gameState != STATE_RUNNING);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw,
                                 int oldh) {

        Log.i("DisplayTouch", "Size changed   w=" + w + "  h=" + h);

        float scale = mGameD.scaleGame(w, h);

        for (int k = 0; k < mTileArray.length; k++) {
            mTileArray[k].scale(scale);
        }

        Bitmap newBitmap = Bitmap.createBitmap((int) (scale * (mGameD.getW())), (int) (scale * (mGameD.getH())), Bitmap.Config.ARGB_8888);
        Canvas newCanvas = new Canvas();
        newCanvas.setBitmap(newBitmap);
        if (mBitmap != null) {
            newCanvas.drawBitmap(mBitmap, 0, 0, null);
        }
        mBitmap = newBitmap;
        mCanvas = newCanvas;

        Paint pnt = new Paint();
        Rect rct = new Rect(0, 0, (int) (scale * (mGameD.getW())), (int) (scale * (mGameD.getH())));
        pnt.setStrokeWidth(8);
        pnt.setStrokeJoin(Paint.Join.ROUND);
        pnt.setARGB(255, 128, 0, 128);
        pnt.setStyle(Paint.Style.FILL);
        mCanvas.drawRect(rct, pnt);
        pnt.setARGB(255, 255, 0, 255);
        pnt.setStyle(Paint.Style.STROKE);
        mCanvas.drawRect(rct, pnt);

        reDraw();

        if ((gameState == STATE_RUNNING) || (gameState == STATE_WAITING))
            return;

        if (gameState == STATE_LOSS)
            endOfGame(false);
        else
            endOfGame(true);

        return;
    }

    private void reDraw() {
        GameSite[] sites = mGameD.getSites();
        for (int k = 0; k < sites.length; k++) {
            GameSite s1 = sites[k];
            if (s1 == null)
                continue;
            mCanvas.drawBitmap(s1.tile.bmp, s1.x, s1.y, null);
            redrawPoint(s1);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            drawPoint(event.getX(), event.getY(), mAlert);
        }
        return true;
    }

    private void drawTileA(Bitmap bmp, int x, int y) {
        mCanvas.drawBitmap(bmp, x, y, null);
        mRect.set(x - 2, y - 2, x + getWidth() + 2, y + getHeight() + 2);
        invalidate(mRect);
    }

    private void drawTileB(Bitmap bmp, int x, int y) {
        int w2 = bmp.getWidth() / 2;
        int h2 = bmp.getHeight() / 2;
        mCanvas.drawBitmap(bmp, x - w2, y - h2, null);
        mRect.set(x - 2 - w2, y - 2 - h2, x + 2 + w2, y + 2 + h2);
        invalidate(mRect);
    }

    private boolean handleTouch(GameSite s1, boolean flag) {
        boolean isOK = true;
        s1.setHidden(false);

        if (s1.isFlag()) {
            s1.setHidden(true);
            s1.setFlag(false);
            mNumFlag -= 1;
            mText.setText("  " + mNumFlag + "/" + mNumFullSites);
            drawTileA(s1.tile.bmp, s1.x, s1.y);
        } else {
            s1.setHidden(false);
            if (flag) {
                s1.setFlag(true);
                mNumFlag += 1;
                mText.setText("  " + mNumFlag + "/" + mNumFullSites);
                drawTileB(mTileArray[10].bmp, s1.x + s1.tile.xb, s1.y + s1.tile.yb);
            } else {
                if (s1.isEmpty()) {
                    drawTileB(mTileArray[s1.nFullNb].bmp, s1.x + s1.tile.xb, s1.y + s1.tile.yb);
                } else {
                    if (mTraining) {
                        s1.setFlag(true);
                        s1.setErrorFlag(true);
                        mNumFlag += 1;
                        mText.setText("  " + mNumFlag + "/" + mNumFullSites);
                        drawTileB(mTileArray[11].bmp, s1.x + s1.tile.xb, s1.y + s1.tile.yb);
                    } else {
                        isOK = false;
                    }
                }
            }
        }
        return (isOK);
    }

    public void drawPoint(float x, float y, boolean flag) {
        GameSite s1;
        Bitmap bmp;
        if (mBitmap == null)
            return;
        // Log.i("DisplayTouch", "  xtouch= " + (int) x + "  ytouch= "+ (int)y  + "  flag= "+ flag);


        if (gameState == STATE_WAITING) {
            s1 = mGameD.fillSites(mNumFullSites);
            gameState = STATE_RUNNING;
        }  else {
            s1 = mGameD.findNextSite((int) x, (int) y);
        }

        if (s1 == null)
            return;

        ArrayDeque<GameSite> mList2 = mGameD.findEmptySites(s1);

        if ((mList2.size() > 0) && !flag) {
            Iterator<GameSite> itr = mList2.iterator();
            while (itr.hasNext()) {
                s1 = itr.next();
                s1.setHidden(false);
                drawTileB(mTileArray[s1.nFullNb].bmp, s1.x + s1.tile.xb, s1.y + s1.tile.yb);
            }
            mText.setText("  " + mNumFlag + "/" + mNumFullSites);
            if (mGameD.checkEndOfGame())
                endOfGame(true);
        } else {
            if (s1.isHidden() || s1.isFlag()) {
                boolean isOK = handleTouch(s1, flag);
                if (isOK) {
                    if (mGameD.checkEndOfGame())
                        endOfGame(true);
                } else
                    endOfGame(false);
            }
        }
    }

    private void redrawPoint(GameSite s1) {
        if (s1.isHidden())
            return;
        Bitmap bmp = (s1.isEmpty()) ? mTileArray[s1.nFullNb].bmp : mTileArray[10].bmp;
        int x = s1.x + s1.tile.xb - (bmp.getWidth() / 2);
        int y = s1.y + s1.tile.yb - (bmp.getHeight() / 2);
        mCanvas.drawBitmap(bmp, x, y, null);
    }

    private void endOfGame(boolean flag) {
        GameSite[] sites = mGameD.getSites();
        for (int k = 0; k < sites.length; k++) {
            GameSite s1 = sites[k];
            s1.setHidden(false);
            s1.setFlag(false);
            if (flag)
                continue;
            Bitmap bmp = (s1.isEmpty()) ? mTileArray[s1.nFullNb].bmp : mTileArray[11].bmp;
            int x = s1.x + s1.tile.xb - (bmp.getWidth() / 2);
            int y = s1.y + s1.tile.yb - (bmp.getHeight() / 2);
            mCanvas.drawBitmap(bmp, x, y, null);
        }

        if (flag) {
            gameState = STATE_SUCCESS;
            int terr = mGameD.countErrors();
            if (terr > 0) {
                mText.setText("  " + (mNumFullSites - terr) + "/" + mNumFullSites);
                mImage.setImageResource(R.drawable.tile_fall1);
            } else {
                mText.setText("");
                mImage.setImageResource(R.drawable.cyclegagne);
            }
        } else {
            invalidate();
            gameState = STATE_LOSS;
            mText.setText("");
            mImage.setImageResource(R.drawable.cycleperdu);
        }
        mActivity.findViewById(R.id.coverbutton).setVisibility(View.INVISIBLE);
        mActivity.findViewById(R.id.fallbutton).setVisibility(View.INVISIBLE);
        mActivity.findViewById(R.id.replaybutton).setVisibility(View.VISIBLE);
    }
}




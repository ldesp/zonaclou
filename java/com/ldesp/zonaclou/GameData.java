package com.ldesp.zonaclou;


import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayDeque;
import java.util.Random;


/**
 * Demonstrates the handling of touch screen and trackball events to
 * implement a simple painting app.
 */
public class GameData {

    private static final Random RNG = new Random();
    private static final int EPSILON = 3;
    private GameSite[] mSiteArray;
    private ScaledStone[] mPattern;
    private ArrayDeque<GameSite> mList1;
    private ArrayDeque<GameSite> mList2;

    private int hmax;
    private int wmax;
    public float mDist2;

    public GameData(int nSites, final Bitmap[] bmp, final int[] param) {

        createGameData(nSites, bmp, param);
    }


    private void createGameData(int nSites, final Bitmap[] bmp, final int[] params) {

        Log.i("DisplayTouch", "  ntiles=" + bmp.length + "  dx =" + params[0] +
                "  dy=" + params[1] + "   offx=" + params[2]);
        mPattern = new ScaledStone[bmp.length];
        int index = 5;
        mDist2 = 0;
        for (int i = 0; i < bmp.length; i++) {
            mPattern[i] = new ScaledStone(bmp[i], params, index);
            if (mDist2 < mPattern[i].d2max)
                mDist2 = mPattern[i].d2max;
            index += (2 * params[index] + params[4]);
        }
        createSites(nSites, params[0], params[1], params[2], params[3]);
    }


    private GameSite findSite(int x, int y) {
        GameSite s1 = null;
        int k;
        for (k = 0; k < mSiteArray.length; k++) {
            s1 = mSiteArray[k];
            if ((Math.abs(s1.x + s1.tile.xb - x) < EPSILON) &&
                    (Math.abs(s1.y + s1.tile.yb - y) < EPSILON)) break;
        }
        if (k == mSiteArray.length)
            return (null);
        else
            return (s1);
    }

    private boolean check_bounds(int x, int y, Rect rtrans, Rect rmax) {
        boolean res = false;
        if (rmax.contains(x + rtrans.left, y + rtrans.top) &&
                rmax.contains(x + rtrans.right, y + rtrans.bottom))
            res = true;
        return (res);
    }

    private void createSites(int nSites, int dx, int dy, int offx, int margin) {
        if ((mPattern == null) || (mPattern.length == 0))
            return;
    /* compute the max width in order to approximate a square */
        // wmax = (int) (dx + Math.sqrt((1.0f*dx*dy*nSites)/mPattern.length));
        wmax = (int) ((dx + Math.sqrt(1.0f * dx * dy * nSites)) / Math.sqrt(1.0f * mPattern.length));
        Rect boundmax = new Rect(0, 0, wmax, 10 * wmax);
    /* compute the number of sites in order to approximate a square */
        int ns = 0;
        ScaledStone s1 = null;
        int iy = -2 * dy;
        int off = 0;
        while (ns < nSites) {
            for (int itile = 0; itile < mPattern.length; itile++) {
                s1 = mPattern[itile];
                for (int ix = -dx + off; (ix < wmax); ix += dx) {
                    if (check_bounds(ix, iy, s1.bounds, boundmax))
                        ns++;
                }
            }
            iy += dy;
            off = (off == 0) ? offx : 0;
        }
        mSiteArray = new GameSite[ns];
    /* create  the sites */
        int index = 0;
        iy = -2 * dy;
        off = 0;
        while (index < ns) {
            for (int itile = 0; itile < mPattern.length; itile++) {
                s1 = mPattern[itile];
                for (int ix = -dx + off; (ix < wmax); ix += dx) {
                    if (check_bounds(ix, iy, s1.bounds, boundmax))
                        mSiteArray[index++] = new GameSite(s1, ix, iy);
                }
            }
            iy += dy;
            off = (off == 0) ? offx : 0;
        }
    /* find the max height and the max width */
        hmax = 0;
        wmax = 0;
        GameSite s2, s3;
        for (int k = 0; k < mSiteArray.length; k++) {
            s2 = mSiteArray[k];
            if (s2.tile.bounds.bottom + s2.y > hmax)
                hmax = s2.tile.bounds.bottom + s2.y;
            if (s2.tile.bounds.right + s2.x > wmax)
                wmax = s2.tile.bounds.right + s2.x;
        }
        hmax += margin;
        wmax += margin;

    /* create the list of neighbors */
        for (int k = 0; k < mSiteArray.length; k++) {
            s3 = mSiteArray[k];
            s3.nnb = 0;
            for (int j = 0; j < s3.tile.nnb; j++) {
                s2 = findSite(s3.x + s3.tile.nbArray[2 * j], s3.y + s3.tile.nbArray[2 * j + 1]);
                if (s2 != null)
                    s3.nbSiteArray[s3.nnb++] = s2;
            }
        }
        Log.i("DisplayTouch", "  nSites = " + mSiteArray.length);
    }

    private void findEmptySites() {
    /* find empty sites  to size the lists */
        int count = 0;
        GameSite s3;
        for (int k = 0; k < mSiteArray.length; k++) {
            s3 = mSiteArray[k];
            if (s3.isEmpty() && (s3.nFullNb == 0))
                count++;
        }
        mList1 = new ArrayDeque<GameSite>(count);
        mList2 = new ArrayDeque<GameSite>(count);
    }


    public void resetFlags() {
        Log.i("DisplayTouch", "RESET GAME");
    /* reset the flags */
        for (int k = 0; k < mSiteArray.length; k++)
            mSiteArray[k].resetFlags();
    }

    public void fillSites(int nfull, GameSite s1) {
        Log.i("DisplayTouch", "FILL GAME");
    /* select the filled  sites */
        int n = nfull;
        if (nfull > mSiteArray.length)
            n = mSiteArray.length;
        int count = 0;
        GameSite s3;
        for (int k = 0; k < mSiteArray.length; k++)
            mSiteArray[k].nFullNb = 0;
        while (count < n) {
            s3 = mSiteArray[RNG.nextInt(mSiteArray.length)];
            if (s3 == s1)
                continue;
            if (!s3.isEmpty())
                continue;
            s3.setEmpty(false);
            for (int j = 0; j < s3.nnb; j++)
                s3.nbSiteArray[j].nFullNb++;
            count++;
        }
        findEmptySites();
    }


    public int fillSites(int nfull, byte[] bflagA) {

        int count = 0;
        if ((bflagA == null) || (bflagA.length != mSiteArray.length)) {
            resetFlags();
            fillSites(nfull, mSiteArray[0]);
        } else {
            Log.i("DisplayTouch", "RESTAURE GAME");
        /* fill the sites */
            GameSite s3;

            for (int k = 0; k < mSiteArray.length; k++)
                mSiteArray[k].nFullNb = 0;

            for (int k = 0; k < mSiteArray.length; k++) {
                s3 = mSiteArray[k];
                s3.flags = bflagA[k];
                if (s3.isFlag())
                    count += 1;
                if (s3.isEmpty())
                    continue;
                for (int j = 0; j < s3.nnb; j++)
                    s3.nbSiteArray[j].nFullNb++;
            }
            findEmptySites();
        }
        return (count);
    }

    public GameSite findNextSite(int x, int y) {
        int d, dmin, k, ks;
        GameSite s1 = null;

        for (k = 0; k < mSiteArray.length; k++) {
            s1 = mSiteArray[k];
            if ((s1 != null) && s1.tile.bounds.contains(x - s1.x, y - s1.y)) break;
        }

        if (k == mSiteArray.length)
            return (null);

        ks = k;
        dmin = ((x - s1.x - s1.tile.xb) * (x - s1.x - s1.tile.xb));
        dmin += ((y - s1.y - s1.tile.yb) * (y - s1.y - s1.tile.yb));

        for (k = ks + 1; k < mSiteArray.length; k++) {
            s1 = mSiteArray[k];
            if ((s1 != null) && s1.tile.bounds.contains(x - s1.x, y - s1.y)) {
                d = ((x - s1.x - s1.tile.xb) * (x - s1.x - s1.tile.xb));
                d += ((y - s1.y - s1.tile.yb) * (y - s1.y - s1.tile.yb));
                if (d < dmin) {
                    ks = k;
                    dmin = d;
                }
            }
        }
        s1 = mSiteArray[ks];
        // Log.i("DisplayTouch", "  ks="+ ks + "  dmin=" + dmin +"  xb="+ (s1.x+s1.tile.xb) + "  yb="+(s1.y+s1.tile.yb));

        return (mSiteArray[ks]);

    }

    public float scaleGame(int w, int h) {

        float scw = (1.0f * w) / (wmax);
        float sch = (1.0f * h) / (hmax);
        float sc = (scw < sch) ? scw : sch;
        Log.i("DisplayTouch", " new Scale=" + sc);
    /* CAUTION SCALE PATTERN FIRST */
        mDist2 = 0;
        for (int k = 0; k < mPattern.length; k++) {
            mPattern[k].scale(sc);
            if (mDist2 < mPattern[k].d2max)
                mDist2 = mPattern[k].d2max;
        }

        for (int k = 0; k < mSiteArray.length; k++) {
            mSiteArray[k].scale();
        }
        return (sc);
    }

    public int getNumSites() {
        if (mSiteArray != null)
            return (mSiteArray.length);
        else
            return (0);
    }

    public int getW() {
        return (wmax);
    }

    public int getH() {
        return (hmax);
    }


    public GameSite[] getSites() {
        return (mSiteArray);
    }

    public byte[] getFlags() {
        byte[] bA = new byte[mSiteArray.length];
        for (int k = 0; k < mSiteArray.length; k++)
            bA[k] = mSiteArray[k].flags;
        return (bA);
    }


    public ArrayDeque<GameSite> findEmptySites(GameSite s1) {
        GameSite s2, s3;
        mList2.clear();
        mList1.clear();
        if ((s1.nFullNb == 0) && s1.isHidden() && s1.isEmpty()) {
            mList1.push(s1);
            while (!mList1.isEmpty()) {
                s2 = mList1.pop();
                mList2.push(s2);
                for (int j = 0; j < s2.nnb; j++) {
                    s3 = s2.nbSiteArray[j];
                    if (!s3.isHidden())
                        continue;
                    if (mList2.contains(s3))
                        continue;
                    if (s3.nFullNb > 0)
                        mList2.push(s3);
                    else
                        mList1.push(s3);
                }
            }
        }
        return (mList2);
    }

    public boolean checkEndOfGame() {
        GameSite s1 = null;
        int k;
        for (k = 0; k < mSiteArray.length; k++) {
            s1 = mSiteArray[k];
            if (s1.isHidden())
                break;
            if ((s1.isEmpty()) && (s1.isFlag()))
                break;
            if ((!s1.isEmpty()) && (!s1.isFlag()))
                break;
        }

        if (k == mSiteArray.length)
            return true;
        else
            return false;

    }

    public int countErrors() {
        int cnt = 0;
        for (int k = 0; k < mSiteArray.length; k++) {
            if (mSiteArray[k].isErrorFlag())
                cnt += 1;
        }
        return (cnt);
    }

}



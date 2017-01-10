package com.ldesp.zonaclou;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Stone {
    public int xb;
    public int yb;
    public Rect bounds;
    public int nnb;
    public int nbArray[];
    public int surf;
    public int d2max;
    public Bitmap bmp;

    public Stone(Bitmap bmp1, int param[], int index1) {

        bmp = bmp1;
        int index = index1;
        nnb = param[index++];
        xb = param[index++];
        yb = param[index++];
        surf = param[index++];
        d2max = param[index++];

        bounds = new Rect();
        bounds.set(param[index + 2], param[index + 3], param[index], param[index + 1]);
        index += 4;

        nbArray = new int[2 * nnb];
        for (int i = 0; i < 2 * nnb; i++)
            nbArray[i] = param[index + i];
    }

    public Stone(Bitmap bmp1) {

        bmp = bmp1;
        int h = bmp1.getHeight();
        int w = bmp1.getWidth();

        xb = 0;
        yb = 0;

        surf = w * h;
        d2max = (w < h) ? h * h : w * w;

        bounds = new Rect();
        bounds.set(0, 0, w, h);

        nnb = 0;
        nbArray = null;
    }
}



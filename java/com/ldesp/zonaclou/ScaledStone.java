package com.ldesp.zonaclou;


import android.graphics.Bitmap;

public class ScaledStone extends Stone {
    public float scale;
    private Stone ustone;

    public ScaledStone(Bitmap bmp1, int[] param, int index1) {
        super(bmp1, param, index1);
        ustone = new Stone(bmp1, param, index1);
        scale = 1f;
    }

    public ScaledStone(Bitmap bmp1) {
        super(bmp1);
        ustone = new Stone(bmp1);
        scale = 1f;
    }

    public void scale(float scale_in) {

        scale = scale_in;
        bmp = Bitmap.createScaledBitmap(ustone.bmp,
                (int) (ustone.bmp.getWidth() * scale),
                (int) (ustone.bmp.getHeight() * scale),
                false);
        xb = (int) (scale * ustone.xb);
        yb = (int) (scale * ustone.yb);

        surf = (int) (scale * scale * ustone.surf);
        d2max = (int) (scale * scale * ustone.d2max);

        bounds.set((int) (scale * ustone.bounds.left), (int) (scale * ustone.bounds.top),
                (int) (scale * ustone.bounds.right), (int) (scale * ustone.bounds.bottom));

        for (int i = 0; i < 2 * nnb; i++)
            nbArray[i] = (int) (scale * ustone.nbArray[i]);
    }
}




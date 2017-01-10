package com.ldesp.zonaclou;


public class GameSite {
    private static byte HIDDENFLAG = 1;
    private static byte EMPTYFLAG = 2;
    private static byte CAUTIONFLAG = 4;
    private static byte ERRORFLAG = 8;
    public int x;
    public int y;
    private int ux;
    private int uy;
    public int nnb;
    public ScaledStone tile;
    public GameSite[] nbSiteArray;
    public byte flags;
    public int nFullNb;

    public GameSite(ScaledStone t1, int x1, int y1) {
        ux = x1;
        uy = y1;
        x = (int) (t1.scale * ux);
        y = (int) (t1.scale * uy);
        tile = t1;
        nnb = t1.nnb;
        nbSiteArray = new GameSite[nnb];

        for (int i = 0; i < nnb; i++)
            nbSiteArray[i] = null;

        nFullNb = 0;

        resetFlags();
    }

    public void resetFlags() {
        flags = (byte) (HIDDENFLAG | EMPTYFLAG);
    }

    public boolean isEmpty() {
        return ((flags & EMPTYFLAG) != 0);
    }

    public boolean isHidden() {
        return ((flags & HIDDENFLAG) != 0);
    }

    public boolean isFlag() {
        return ((flags & CAUTIONFLAG) != 0);
    }

    public boolean isErrorFlag() {
        return ((flags & ERRORFLAG) != 0);
    }

    public void setEmpty(boolean flag) {
        if (flag)
            flags |= EMPTYFLAG;
        else
            flags &= (~EMPTYFLAG);
    }

    public void setHidden(boolean flag) {
        if (flag)
            flags |= HIDDENFLAG;
        else
            flags &= (~HIDDENFLAG);
    }

    public void setFlag(boolean flag) {
        if (flag)
            flags |= CAUTIONFLAG;
        else
            flags &= (~CAUTIONFLAG);
    }

    public void setErrorFlag(boolean flag) {
        if (flag)
            flags |= ERRORFLAG;
        else
            flags &= (~ERRORFLAG);
    }

    public void scale() {
        x = (int) (tile.scale * ux);
        y = (int) (tile.scale * uy);
    }

}



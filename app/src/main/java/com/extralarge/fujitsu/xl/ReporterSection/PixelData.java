package com.extralarge.fujitsu.xl.ReporterSection;

import android.hardware.Camera.Size;

/**
 * Created by lue on 22-05-2017.
 */

public class PixelData {
    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    private Size size;



    public String getPiexelName() {
        return piexelName;
    }

    public void setPiexelName(String piexelName) {
        this.piexelName = piexelName;
    }

    private String piexelName="";

    public PixelData(Size size, String piexelName)
    {
        this.size=size;
        this.piexelName=piexelName;
    }

}

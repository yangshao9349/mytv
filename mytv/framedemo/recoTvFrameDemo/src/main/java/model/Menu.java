package model;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Administrator on 2017-08-07.
 */

public class Menu implements Serializable {
    public String code;
    public String name;
    public String imgurl;
    public byte[] img;

    public Bitmap bitmap;

}

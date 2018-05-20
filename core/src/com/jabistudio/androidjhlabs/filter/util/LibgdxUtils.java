package com.jabistudio.androidjhlabs.filter.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

import java.text.Normalizer;

/**
 * Created by Paul on 6/6/2016.
 */
public class LibgdxUtils {
    public static Pixmap intToPixmap(int[] in, int w, int h) {
        Pixmap out = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int inVal = in[j * w + i];
                int a = (int)( (inVal & 0xff000000)>>24 )&0xff;
                int r = (int)( (inVal & 0x00ff0000)>>16 )&0xff;
                int b = (int)( (inVal & 0x0000ff00)>>8  )&0xff;
                int g = (int)(  inVal & 0x000000ff)&0xff      ;
                out.drawPixel(i, j, r<<24|g<<16|b<<8|a);
            }
        }
        return out;
    }

    public static int[] pixmapToInt(Pixmap in) {
        int[] out = new int[in.getWidth() * in.getHeight()];
        int cnt = 0;
        int w = in.getWidth();
        int h = in.getHeight();
        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                int inVal = in.getPixel(i, j);
                int r = (int)( (inVal & 0xff000000)>>24 )&0xff;
                int g = (int)( (inVal & 0x00ff0000)>>16 )&0xff;
                int b = (int)( (inVal & 0x0000ff00)>>8  )&0xff;
                int a = (int)(  inVal & 0x000000ff) &0xff     ;
                out[j * w + i] =a<<24|r<<16|b<<8|g;
            }
        }
        return out;

    }

    public static void test(){
        Pixmap pix=new Pixmap(1,1, Pixmap.Format.RGBA8888 );
        int co=Color.rgba8888(Color.WHITE);
        pix.drawPixel(0,0,co);
        int[] a=pixmapToInt(pix);
        Pixmap b=intToPixmap(a,1,1);
        int g=b.getPixel(0,0);
        int[] c=pixmapToInt(b);
        System.out.println(a[0]+" = "+c[0]+" : "+g+" = "+co);
    }

    public static int[] scale(int[] src, int srcWidth, int srcHeight,int srcX,int srcY, int dstWidth, int dstHeight) {
        Pixmap srcPixmap=intToPixmap(src,srcWidth,srcHeight);
        Pixmap dstPixmap=new Pixmap(dstWidth,dstHeight, Pixmap.Format.RGBA8888);
        srcPixmap.setFilter(Pixmap.Filter.BiLinear);
        dstPixmap.setFilter(Pixmap.Filter.BiLinear);
        dstPixmap.drawPixmap(srcPixmap,srcX,srcY,dstWidth,dstHeight,0,0,dstWidth,dstHeight);
        int[] out= pixmapToInt(dstPixmap);
        srcPixmap.dispose();
        dstPixmap.dispose();
        return out;
    }



}

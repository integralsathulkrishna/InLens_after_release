package integrals.inlens.Helper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BitmapConfig {
    private  Bitmap bitmap;
    public int LANDSCAPE=0;
    public int POTRAIT=1;
    public int SQUARE=2;

    public BitmapConfig(Bitmap bitmap) {
        this.bitmap = bitmap;
    }


    public int GetPhotoType()
    {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        if(width>height)
        {// landscape
            return 0;
        }
        else if(width<height)
        {// portrait
            return 1;
        }
        else
        {// bitmap is square
            return 2;
        }

    }



    public Bitmap AdjustedContrast(double value)
    {// image size
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // create output bitmap
        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, bitmap.getConfig());
        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);
        // draw bitmap to bmOut from bitmap bitmap so we can modify it
        c.drawBitmap(bitmap, 0, 0, new Paint(Color.BLACK));
        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = bitmap.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }





    public Bitmap SharpenBitmap() {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        int red, green, blue;
        int a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Bitmap bmpBlurred = Bitmap.createBitmap(width, height,bitmap.getConfig());

        Canvas canvas = new Canvas(bmpBlurred);

        canvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {

                a1 = bitmap.getPixel(i - 1, j - 1);
                a2 = bitmap.getPixel(i - 1, j);
                a3 = bitmap.getPixel(i - 1, j + 1);
                a4 = bitmap.getPixel(i, j - 1);
                a5 = bitmap.getPixel(i, j);
                a6 = bitmap.getPixel(i, j + 1);
                a7 = bitmap.getPixel(i + 1, j - 1);
                a8 = bitmap.getPixel(i + 1, j);
                a9 = bitmap.getPixel(i + 1, j + 1);

                red = (Color.red(a1) + (Color.red(a2))*(-1) + Color.red(a3) +
                        (Color.red(a4))*(-1) + (Color.red(a6))*(-1) + Color.red(a7) +
                        (Color.red(a8))*(-1) + Color.red(a9))    + Color.red(a5)*5 ;
                if(red < 0 )
                    red = 0;
                if(red > 255)
                    red = 255;

                green = (Color.green(a1) + (Color.green(a2)) *(-1) + Color.green(a3) +
                        (Color.green(a4)) *(-1) + (Color.green(a6))*(-1) + Color.green(a7) +
                        (Color.green(a8))*(-1) + Color.green(a9)) + Color.green(a5)*5 ;
                if(green < 0 )
                    green = 0;
                if(green > 255)
                    green = 255;

                blue = (Color.blue(a1) + (Color.blue(a2))*(-1) + Color.blue(a3) +
                        (Color.blue(a4))*(-1) + (Color.blue(a6))*(-1) + Color.blue(a7) +
                        (Color.blue(a8)) *(-1) + Color.blue(a9))   + Color.blue(a5)*5 ;
                if(blue < 0 )
                    blue = 0;
                if(blue > 255)
                    blue = 255;
                bmpBlurred.setPixel(i, j, Color.rgb(red, green, blue));
            }
        }
        return bmpBlurred;
    }



}

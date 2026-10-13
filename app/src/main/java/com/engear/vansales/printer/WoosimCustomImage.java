package com.engear.vansales.printer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class WoosimCustomImage {
    private static final String TAG = "WoosimCustomImage";
    private static final int DITHERING_NO = 0;
    private static final int DITHERING_RANDOM_THRESHOLD = 1;
    private static final int DITHERING_FLOYD_STEINBERG = 2;
    private static final int DITHERING_FLOYD_STEINBERG_OLD = 3;
    private static final byte CAN = 24;
    private static final byte ESC = 27;
    private static final byte GS = 29;
    private static final byte[] cmd_ESCFF = new byte[]{27, 12};
    private static final int MAX_RLE_LENGTH = 62;

    public WoosimCustomImage() {
    }

    public static byte[] printStoredImage(int var0) {
        if (var0 >= 1 && var0 <= 60) {
            return new byte[]{27, 102, (byte)(var0 - 1), 12};
        } else {
            Log.e("WoosimCustomImage", "Invalid stored image number: " + var0);
            return null;
        }
    }

    public static byte[] printBitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printImage(var0, var1, var2, var3, var4, false, 0, false);
    }

    public static byte[] printBitmapLandscape(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printImage(var0, var1, var2, var3, var4, false, 0, true);
    }

    public static byte[] printCompressedBitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printImage(var0, var1, var2, var3, var4, true, 0, false);
    }

    public static byte[] printCompressedBitmapLandscape(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printImage(var0, var1, var2, var3, var4, true, 0, true);
    }

    public static byte[] printColorBitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printImage(var0, var1, var2, var3, var4, true, 2, false);
    }

    public static byte[] printColorBitmap(int var0, int var1, int var2, int var3, Bitmap var4, int var5) {
        return printImage(var0, var1, var2, var3, var4, true, var5, false);
    }

    public static byte[] printColorBitmapLandscape(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printImage(var0, var1, var2, var3, var4, true, 2, true);
    }

    private static byte[] printImage(int var0, int var1, int var2, int var3, Bitmap var4, boolean var5, int var6, boolean var7) {
        if (var2 <= 0) {
            var2 = var4.getWidth();
        }

        if (var3 <= 0) {
            var3 = var4.getHeight();
        }

        ByteArrayOutputStream var8;
        ByteArrayOutputStream var10001 = var8 = new ByteArrayOutputStream(1024);
     //   var8.<init>(1024);
        byte var15 = (byte)(var0 & 255);
        byte var17 = (byte)(var0 >> 8 & 255);
        byte var9 = (byte)(var1 & 255);
        byte var10 = (byte)(var1 >> 8 & 255);
        byte var11 = (byte)(var2 & 255);
        byte var12 = (byte)(var2 >> 8 & 255);
        byte var13 = (byte)(var3 & 255);
        byte var14 = (byte)(var3 >> 8 & 255);
        byte[] var10002 = new byte[]{27, 87, var15, var17, var9, var10, var11, var12, var13, var14};
        var10001.write(var10002, 0, var10002.length);
        byte[] var27;
        if (var7) {
            var27 = new byte[]{27, 84, 3};
            var8.write(var27, 0, var27.length);
        }

        byte[] var16 = convertBMPtoX4image(var4, var6);
        int var10000 = var4.getWidth();
        var1 = var4.getHeight();
        int var21 = var10000 / 8;
        byte var23;
        if (var10000 % 8 == 0) {
            var23 = 0;
        } else {
            var23 = 1;
        }

        var21 += var23;
        if (var7) {
            if (var1 > var2) {
                var1 = var2;
            }
        } else if (var1 > var3) {
            var1 = var3;
        }

        byte[] var10003;
        byte[] var10004;
        byte var22;
        byte[] var26;
        for(var2 = 0; var2 < var1 / 255; var8.write(var26, 0, var26.length)) {
            if (var5) {
                byte[] var20;
                var10003 = var20 = new byte[5];
                var20[0] = 27;
                var20[1] = 88;
                var20[2] = 51;
                var10003[3] = (byte)var21;
                var10003[4] = -1;
                byte[] var24 = convertImageX4toX3(var16, (var6 = var21 * 255) * var2, var6);
                var27 = var26 = new byte[4];
                var26[0] = 27;
                var26[1] = 88;
                var26[2] = 50;
                var26[3] = -1;
                var8.write(var20, 0, var20.length);
                var8.write(var24, 0, var24.length);
                var8.write(var27, 0, var27.length);
            } else {
                var10004 = new byte[]{27, 88, 52, (byte)var21, -1};
                var8.write(var10004, 0, var10004.length);
                var8.write(var16, (var3 = var21 * 255) * var2, var3);
            }

            int var28 = ++var2 * 255;
            var22 = (byte)(var28 & 255);
            byte var25 = (byte)(var28 >> 8 & 255);
            var27 = var26 = new byte[6];
            var27[0] = 27;
            var27[1] = 79;
            var27[2] = 0;
            var27[3] = 0;
            var27[4] = var22;
            var27[5] = var25;
            if (var7) {
                var26[2] = var22;
                var26[3] = var25;
                var26[4] = 0;
                var26[5] = 0;
            }
        }

        if ((var1 %= 255) != 0) {
            if (var5) {
                var27 = var16;
                var10003 = var16 = new byte[5];
                var16[0] = 27;
                var16[1] = 88;
                var16[2] = 51;
                var16[3] = (byte)var21;
                var22 = (byte)var1;
                var10003[4] = var22;
                byte[] var18 = convertImageX4toX3(var27, var21 * 255 * var2, var21 * var1);
                byte[] var19;
                var27 = var19 = new byte[4];
                var19[0] = 27;
                var19[1] = 88;
                var19[2] = 50;
                var19[3] = var22;
                var8.write(var16, 0, var16.length);
                var8.write(var18, 0, var18.length);
                var8.write(var27, 0, var27.length);
            } else {
                var27 = var16;
                var10004 = var16 = new byte[5];
                var16[0] = 27;
                var16[1] = 88;
                var16[2] = 52;
                var16[3] = (byte)var21;
                var10004[4] = (byte)var1;
                var8.write(var10004, 0, var10004.length);
                var8.write(var27, var21 * 255 * var2, var21 * var1);
            }
        }

        var10002 = cmd_ESCFF;
        var8.write(var10002, 0, var10002.length);
        return var8.toByteArray();
    }

    private static byte[] convertBMPtoX4image(Bitmap var0, int var1) {
        switch (var1) {
            case 0:
                return makeX4imageBlackWhite(var0);
            case 1:
                return makeX4imageRandomThreshold(var0);
            case 2:
                return makeX4imageFloydSteinberg(var0);
            case 3:
                return makeX4imageFloydSteinbergOld(var0);
            default:
                return null;
        }
    }

    private static byte[] makeX4imageBlackWhite(Bitmap var0) {
        int var1;
        int var10000 = var1 = var0.getWidth();
        int[] var2;
        int var10;
        var0.getPixels(var2 = new int[(var10 = var0.getHeight()) * var1], 0, var1, 0, 0, var1, var10);
        int var3 = var10000 / 8;
        byte var4;
        if (var10000 % 8 == 0) {
            var4 = 0;
        } else {
            var4 = 1;
        }

        byte[] var11;
        Arrays.fill(var11 = new byte[(var3 += var4) * var10], (byte)0);

        for(int var5 = 0; var5 < var10; ++var5) {
            for(int var6 = 0; var6 < var1; ++var6) {
                int var7 = var6 / 8;
                int var8 = var6 % 8;
                int var9 = var2[var5 * var1 + var6];
                var7 += var5 * var3;
                if (Color.red(var9) + Color.green(var9) + Color.blue(var9) < 702 && var9 != 0) {
                    var11[var7] = (byte)(var11[var7] | 1 << 7 - var8);
                }
            }
        }

        return var11;
    }

    private static byte[] makeX4imageRandomThreshold(Bitmap var0) {
        int var1;
        int var10000 = var1 = var0.getWidth();
        int[] var2;
        int var12;
        var0.getPixels(var2 = new int[(var12 = var0.getHeight()) * var1], 0, var1, 0, 0, var1, var12);
        int var3 = var10000 / 8;
        byte var4;
        if (var10000 % 8 == 0) {
            var4 = 0;
        } else {
            var4 = 1;
        }

        byte[] var13;
        Arrays.fill(var13 = new byte[(var3 += var4) * var12], (byte)0);
        Random var5 = new Random();
        double[] var6;
        double[] var14 = var6 = new double[45];
        var14[0] = 0.25;
        var14[1] = 0.26;
        var14[2] = 0.27;
        var14[3] = 0.28;
        var14[4] = 0.29;
        var14[5] = 0.3;
        var14[6] = 0.31;
        var14[7] = 0.32;
        var14[8] = 0.33;
        var14[9] = 0.34;
        var14[10] = 0.35;
        var14[11] = 0.36;
        var14[12] = 0.37;
        var14[13] = 0.38;
        var14[14] = 0.39;
        var14[15] = 0.4;
        var14[16] = 0.41;
        var14[17] = 0.42;
        var14[18] = 0.43;
        var14[19] = 0.44;
        var14[20] = 0.45;
        var14[21] = 0.46;
        var14[22] = 0.47;
        var14[23] = 0.48;
        var14[24] = 0.49;
        var14[25] = 0.5;
        var14[26] = 0.51;
        var14[27] = 0.52;
        var14[28] = 0.53;
        var14[29] = 0.54;
        var14[30] = 0.55;
        var14[31] = 0.56;
        var14[32] = 0.57;
        var14[33] = 0.58;
        var14[34] = 0.59;
        var14[35] = 0.6;
        var14[36] = 0.61;
        var14[37] = 0.62;
        var14[38] = 0.63;
        var14[39] = 0.64;
        var14[40] = 0.65;
        var14[41] = 0.66;
        var14[42] = 0.67;
        var14[43] = 0.68;
        var14[44] = 0.69;

        for(int var7 = 0; var7 < var12; ++var7) {
            for(int var8 = 0; var8 < var1; ++var8) {
                int var9 = var8 / 8;
                int var10 = var8 % 8;
                int var11 = var2[var7 * var1 + var8];
                var9 += var7 * var3;
                if ((double)(((float)Color.red(var11) * 0.21F + (float)Color.green(var11) * 0.71F + (float)Color.blue(var11) * 0.07F) / 255.0F) <= var6[var5.nextInt(var6.length)] && var11 != 0) {
                    var13[var9] = (byte)(var13[var9] | 1 << 7 - var10);
                }
            }
        }

        return var13;
    }

    private static byte[] makeX4imageFloydSteinberg(Bitmap var0) {
        int var1;
        int var10000 = var1 = var0.getWidth();
        int var2;
        int[] var3;
        var0.getPixels(var3 = new int[(var2 = var0.getHeight()) * var1], 0, var1, 0, 0, var1, var2);
        int var4 = var10000 / 8;
        byte var5;
        if (var10000 % 8 == 0) {
            var5 = 0;
        } else {
            var5 = 1;
        }

        byte[] var15;
        Arrays.fill(var15 = new byte[(var4 += var5) * var2], (byte)0);
        C3[] var6;
        C3[] var10002 = var6 = new C3[2];
        C3 var7;
        var7 = new C3(0, 0, 0);
        var10002[0] = var7;
        var7 = new C3(255, 255, 255);

        var10002[1] = var7;
        C3[][] var16 = new C3[var2][var1];

        int var8;
        int var9;
        for(var8 = 0; var8 < var2; ++var8) {
            for(var9 = 0; var9 < var1; ++var9) {
                var16[var8][var9] = new C3(var0.getPixel(var9, var8));
            }
        }

        int var10;
        for(int var14 = 0; var14 < var2; ++var14) {
            for(var8 = 0; var8 < var1; var8 = var10) {
                var9 = var8 / 8;
                var10 = var8 % 8;
                int var11 = var3[var14 * var1 + var8];
                var9 += var14 * var4;
                C3 var12;
                C3 var13;
                if ((var13 = findClosestPaletteColor(var12 = var16[var14][var8], var6)).toRGB() == -16777216 && var11 != 0) {
                    var15[var9] = (byte)(var15[var9] | 1 << 7 - var10);
                }

                C3 var17 = var12.sub(var13);
                if ((var10 = var8 + 1) < var1) {
                    var16[var14][var10] = var16[var14][var10].add(var17.mul(0.4375));
                }

                int var18;
                if ((var11 = var8 - 1) >= 0 && (var18 = var14 + 1) < var2) {
                    var16[var18][var11] = var16[var18][var11].add(var17.mul(0.1875));
                }

                if ((var11 = var14 + 1) < var2) {
                    var16[var11][var8] = var16[var11][var8].add(var17.mul(0.3125));
                }

                if (var10 < var1 && var11 < var2) {
                    var16[var11][var10] = var16[var11][var10].add(var17.mul(0.0625));
                }
            }
        }

        return var15;
    }

    private static C3 findClosestPaletteColor(C3 var0, C3[] var1) {
        C3 var2 = var1[0];
        int var3 = var1.length;

        C3 var5;
        for(int var4 = 0; var4 < var3; var2 = var5) {
            if ((var5 = var1[var4]).diff(var0) >= var2.diff(var0)) {
                var5 = var2;
            }

            ++var4;
        }

        return var2;
    }

    private static byte[] makeX4imageFloydSteinbergOld(Bitmap var0) {
        int var1;
        int var10000 = var1 = var0.getWidth();
        int var12 = var0.getHeight();
        int[] var2;
        convertGrayScale(var0).getPixels(var2 = new int[var12 * var1], 0, var1, 0, 0, var1, var12);
        int var3 = var10000 / 8;
        byte var4;
        if (var10000 % 8 == 0) {
            var4 = 0;
        } else {
            var4 = 1;
        }

        byte[] var13;
        Arrays.fill(var13 = new byte[(var3 += var4) * var12], (byte)0);

        int var7;
        for(int var5 = 0; var5 < var12; ++var5) {
            for(int var6 = 0; var6 < var1; var6 = var7) {
                var7 = var6 / 8;
                int var8 = var6 % 8;
                int var9;
                int var10 = var2[var9 = var5 * var1 + var6];
                var7 += var5 * var3;
                int var11;
                if (Color.blue(var10) < 128) {
                    var11 = 0;
                } else {
                    var11 = 255;
                }

                short var14 = (short)var11;
                var11 = Color.blue(var10) - var11;
                if (var14 == 0 && var10 != 0) {
                    var13[var7] = (byte)(var13[var7] | 1 << 7 - var8);
                }

                if ((var7 = var6 + 1) < var1 && var2[var8 = var9 + 1] != 0) {
                    var2[var8] += var11 * 7 >> 4;
                }

                if (var5 + 1 != var12) {
                    if (var6 > 0 && var2[var6 = var9 + var1 - 1] != 0) {
                        var2[var6] += var11 * 3 >> 4;
                    }

                    if (var2[var6 = var9 + var1] != 0) {
                        var2[var6] += var11 * 5 >> 4;
                    }

                    if (var7 < var1 && var2[++var6] != 0) {
                        var2[var6] += var11 >> 4;
                    }
                }
            }
        }

        return var13;
    }

    private static Bitmap convertGrayScale(Bitmap var0) {
        int var1 = var0.getHeight();
        int var2 = var0.getWidth();
        Rect var3 = new Rect(0, 0, var2, var1);

        Bitmap var5 = Bitmap.createBitmap(var2, var1, Bitmap.Config.ARGB_8888);
        Canvas var10001 = new Canvas(var5);
        Paint var4 = new Paint();
        ColorMatrix var6 = new ColorMatrix();
        var6.setSaturation(0.0F);
        var4.setColorFilter(new ColorMatrixColorFilter(var6));
        var10001.drawBitmap(var0, null, var3, var4);

        return var5;
    }


    private static byte[] convertImageX4toX3(byte[] var0, int var1, int var2) {
        ByteArrayOutputStream var3 = new ByteArrayOutputStream(1024);

        byte var4 = var0[var1];
        int var5 = 0;
        int var6 = 1;

        byte var9;
        for(int var7 = 0; var7 < var2; var4 = var9) {
            int var8;
            if (var4 == (var9 = var0[var8 = var1 + var7])) {
                if (++var5 >= 3 && var6 > 1) {
                    var3.write(var6 + 128 - 1);
                    var3.write(var0, var8 - var6 - 1, var6 - 1);
                    var6 = 1;
                }

                if (var5 > 62) {
                    var3.write(254);
                    var3.write(var4);
                    var5 = 1;
                }
            } else {
                ++var6;
                if (var5 >= 3) {
                    var3.write(var5 + 192);
                    var3.write(var4);
                    --var6;
                } else if (var5 == 2) {
                    ++var6;
                }

                if (var6 > 62) {
                    var3.write(190);
                    var3.write(var0, var8 - var6 + 1, 62);
                    var6 -= 62;
                }

                var5 = 1;
            }

            ++var7;
        }

        if (var5 >= 3) {
            var3.write(var5 + 192);
            var3.write(var4);
        } else {
            if (var5 == 2) {
                ++var6;
            }

            var3.write(var6 + 128);
            var3.write(var0, var1 + var2 - var6, var6);
        }

        return var3.toByteArray();
    }

    public static byte[] fastPrintBitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        if (var2 <= 0) {
            var2 = var4.getWidth();
        }

        if (var3 <= 0) {
            var3 = var4.getHeight();
        }

        ByteArrayOutputStream var13 = new ByteArrayOutputStream(1024);

        byte var14 = (byte)(var0 & 255);
        byte var15 = (byte)(var0 >> 8 & 255);
        byte var5 = (byte)(var1 & 255);
        byte var6 = (byte)(var1 >> 8 & 255);
        byte var7 = (byte)(var2 & 255);
        byte var8 = (byte)(var2 >> 8 & 255);
        byte[] var9 = convertBMPtoX4image(var4, 0);
        int var10000 = var4.getWidth();
        int var16 = var4.getHeight();
        int var10 = var10000 / 8;
        byte var11;
        if (var10000 % 8 == 0) {
            var11 = 0;
        } else {
            var11 = 1;
        }

        var10 += var11;
        boolean var17 = true;
        if (var16 > var3) {
            var16 = var3;
        }

        var13.write(24);

        byte[] var10001;
        byte[] var21;
        byte[] var22;
        for(var3 = 0; var3 < var16 / 255; ++var3) {
            byte[] var12;
            var10001 = var12 = new byte[10];
            var10001[0] = 27;
            var10001[1] = 87;
            var10001[2] = var14;
            var10001[3] = var15;
            var10001[4] = 0;
            var10001[5] = 0;
            var10001[6] = var7;
            var10001[7] = var8;
            var10001[8] = -1;
            var10001[9] = 0;
            if (var17) {
                var12[4] = var5;
                var12[5] = var6;
                var17 = false;
            }

            var13.write(var12, 0, var12.length);
            var22 = new byte[]{27, 88, 52, (byte)var10, -1};
            var13.write(var22, 0, var22.length);
            int var18;
            var13.write(var9, (var18 = var10 * 255) * var3, var18);
            var21 = cmd_ESCFF;
            var13.write(var21, 0, var21.length);
            var13.write(24);
        }

        if ((var16 %= 255) != 0) {
            byte[] var19;
            var10001 = var19 = new byte[10];
            var19[0] = 27;
            var19[1] = 87;
            var19[2] = var14;
            var19[3] = var15;
            var19[4] = 0;
            var19[5] = 0;
            var19[6] = var7;
            var19[7] = var8;
            var14 = (byte)var16;
            var10001[8] = var14;
            var10001[9] = 0;
            if (var17) {
                var19[4] = var5;
                var19[5] = var6;
            }

            var13.write(var19, 0, var19.length);
            var22 = new byte[]{27, 88, 52, (byte)var10, var14};
            var13.write(var22, 0, var22.length);
            var13.write(var9, var10 * 255 * var3, var10 * var16);
            var21 = cmd_ESCFF;
            var13.write(var21, 0, var21.length);
            var13.write(24);
        }

        return var13.toByteArray();
    }

    public static byte[] drawBitmap(int var0, int var1, Bitmap var2) {
        return drawImage(var0, var1, var2, 0);
    }

    public static byte[] drawColorBitmap(int var0, int var1, Bitmap var2) {
        return drawImage(var0, var1, var2, 2);
    }

    private static byte[] drawImage(int var0, int var1, Bitmap var2, int var3) {
        int var4;
        int var10000 = var4 = var2.getWidth();
        int var5 = var2.getHeight();
        int var6 = var10000 / 8;
        byte var7;
        if (var10000 % 8 == 0) {
            var7 = 0;
        } else {
            var7 = 1;
        }

        Bitmap var20 = var2;
        int var10001 = var3;
        int var10004 = var4;
        int var10005 = var4;
        int var10006 = var1;
        int var10007 = var1;
        int var10008 = var0;
        int var10009 = var0;
        var0 = var6 + var7;
        byte var12 = (byte)(var10009 & 255);
        byte var14 = (byte)(var10008 >> 8 & 255);
        byte var16 = (byte)(var10007 & 255);
        byte var17 = (byte)(var10006 >> 8 & 255);
        byte var18 = (byte)(var10005 & 255);
        byte var19 = (byte)(var10004 >> 8 & 255);
        byte var8 = (byte)(var5 & 255);
        byte var9 = (byte)(var5 >> 8 & 255);
        byte[] var10;
        byte[] var10002 = var10 = new byte[10];
        var10002[0] = 27;
        var10002[1] = 87;
        var10002[2] = var12;
        var10002[3] = var14;
        var10002[4] = var16;
        var10002[5] = var17;
        var10002[6] = var18;
        var10002[7] = var19;
        var10002[8] = var8;
        var10002[9] = var9;
        byte[] var13;
        var10002 = var13 = new byte[5];
        var13[0] = 27;
        var13[1] = 88;
        var13[2] = 52;
        var13[3] = (byte)var0;
        var10002[4] = (byte)var5;
        byte[] var11 = convertBMPtoX4image(var20, var10001);
        ByteBuffer var15;
        ByteBuffer var21 = var15 = ByteBuffer.allocate(var10.length + var13.length + var11.length);
        var15.put(var10);
        var15.put(var13);
        var21.put(var11);
        return var21.array();
    }

    public static byte[] drawBox(int var0, int var1, int var2, int var3, int var4) {
        if (var2 <= 0 && var3 <= 0) {
            Log.e("WoosimCustomImage", "Invalid parameters on width and/or height.");
            return null;
        } else {
            int var10000 = var3;
            int var10001 = var3;
            int var10002 = var2;
            int var10003 = var2;
            int var10004 = var1;
            int var10005 = var1;
            int var10006 = var0;
            byte var9 = (byte)(var0 & 255);
            byte var10 = (byte)(var10006 >> 8 & 255);
            byte var11 = (byte)(var10005 & 255);
            byte var12 = (byte)(var10004 >> 8 & 255);
            byte var5 = (byte)(var10003 & 255);
            byte var6 = (byte)(var10002 >> 8 & 255);
            byte var7 = (byte)(var10001 & 255);
            byte var8 = (byte)(var10000 >> 8 & 255);
            return new byte[]{27, 79, var9, var10, var11, var12, 29, 105, var5, var6, var7, var8, (byte)var4};
        }
    }

    public static byte[] drawLine(int var0, int var1, int var2, int var3, int var4) {
        if (var0 >= 0 && var1 >= 0 && var2 >= 0 && var3 >= 0 && var4 > 0) {
            if (var4 > 255) {
                var4 = 255;
            }

            int var10000 = var4;
            int var10001 = var3;
            int var10002 = var3;
            int var10003 = var2;
            int var10004 = var2;
            int var10005 = var1;
            int var10006 = var1;
            int var10007 = var0;
            byte var9 = (byte)(var0 & 255);
            byte var10 = (byte)(var10007 >> 8 & 255);
            byte var11 = (byte)(var10006 & 255);
            byte var12 = (byte)(var10005 >> 8 & 255);
            byte var13 = (byte)(var10004 & 255);
            byte var5 = (byte)(var10003 >> 8 & 255);
            byte var6 = (byte)(var10002 & 255);
            byte var7 = (byte)(var10001 >> 8 & 255);
            byte var8 = (byte)(var10000 & 255);
            return new byte[]{27, 103, 49, var9, var10, var11, var12, var13, var5, var6, var7, var8};
        } else {
            Log.e("WoosimCustomImage", "Invalid parameter.");
            return null;
        }
    }

    public static byte[] drawEllipse(int var0, int var1, int var2, int var3, int var4) {
        if (var0 >= 0 && var1 >= 0 && var2 > 0 && var3 > 0 && var4 > 0) {
            if (var4 > 255) {
                var4 = 255;
            }

            int var10000 = var4;
            int var10001 = var3;
            int var10002 = var3;
            int var10003 = var2;
            int var10004 = var2;
            int var10005 = var1;
            int var10006 = var1;
            int var10007 = var0;
            byte var9 = (byte)(var0 & 255);
            byte var10 = (byte)(var10007 >> 8 & 255);
            byte var11 = (byte)(var10006 & 255);
            byte var12 = (byte)(var10005 >> 8 & 255);
            byte var13 = (byte)(var10004 & 255);
            byte var5 = (byte)(var10003 >> 8 & 255);
            byte var6 = (byte)(var10002 & 255);
            byte var7 = (byte)(var10001 >> 8 & 255);
            byte var8 = (byte)(var10000 & 255);
            return new byte[]{27, 103, 50, var9, var10, var11, var12, var13, var5, var6, var7, var8};
        } else {
            Log.e("WoosimCustomImage", "Invalid parameter.");
            return null;
        }
    }

    public static byte[] printStdModeBitmap(Bitmap var0) {
        return printStdModeImage(var0, 0);
    }

    public static byte[] printStdModeColorBitmap(Bitmap var0) {
        return printStdModeImage(var0, 2);
    }

    private static byte[] printStdModeImage(Bitmap var0, int var1) {
        int var10000 = var0.getWidth();
        int var2 = var0.getHeight();
        int var3 = var10000 / 8;
        int var4;
        if (var10000 % 8 == 0) {
            var4 = 0;
        } else {
            var4 = 1;
        }

        int var6 = var3 + var4;
        byte[] var7 = convertBMPtoX4image(var0, var1);
        ByteArrayOutputStream var8 = new ByteArrayOutputStream(1024);

        byte[] var9;
        if (var2 > 255) {
            var9 = new byte[]{27, 51, 0};
            var8.write(var9, 0, var9.length);
        }

        byte[] var10005;
        for(var4 = 0; var4 < var2 / 255; ++var4) {
            var10005 = new byte[]{27, 88, 52, (byte)var6, -1};
            var8.write(var10005, 0, var10005.length);
            int var5;
            var8.write(var7, (var5 = var6 * 255) * var4, var5);
            var8.write(10);
        }

        if (var2 > 255) {
            var9 = new byte[]{27, 50};
            var8.write(var9, 0, var9.length);
        }

        if ((var2 %= 255) != 0) {
            byte[] var10002 = var7;
            var10005 = var7 = new byte[5];
            var7[0] = 27;
            var7[1] = 88;
            var7[2] = 52;
            var7[3] = (byte)var6;
            var10005[4] = (byte)var2;
            var8.write(var10005, 0, var10005.length);
            var8.write(var10002, var6 * 255 * var4, var6 * var2);
            var8.write(10);
        }

        return var8.toByteArray();
    }

    public static Bitmap adjustBitmap(Bitmap var0, float var1, float var2) {
        ColorMatrix var3 = new ColorMatrix(new float[]{var2, 0.0F, 0.0F, 0.0F, var1, 0.0F, var2, 0.0F, 0.0F, var1, 0.0F, 0.0F, var2, 0.0F, var1, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F});
        Bitmap var5 = Bitmap.createBitmap(var0.getWidth(), var0.getHeight(), var0.getConfig());
        Canvas var10001 = new Canvas(var5);
        Paint var6 = new Paint();
        var6.setColorFilter(new ColorMatrixColorFilter(var3));
        Rect var7 = new Rect(0, 0, var0.getWidth(), var0.getHeight());
        var10001.drawBitmap(var0, null, var7, var6);
        return var5;
    }


    public static byte[] printARGBbitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        Bitmap var5;
        byte[] var10000 = printRGBbitmap(var0, var1, var2, var3, var5 = removeAlphaValue(var4));
        var5.recycle();
        return var10000;
    }

    public static byte[] printRGBbitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printBitmap(var0, var1, var2, var3, var4);
    }

    public static byte[] bmp2PrintableImage(int var0, int var1, int var2, int var3, Bitmap var4) {
        return printRGBbitmap(var0, var1, var2, var3, var4);
    }

    private static Bitmap removeAlphaValue(Bitmap var0) {
        Bitmap var10000 = var0 = var0.copy(var0.getConfig(), true);
        int var1 = var10000.getWidth();
        int var2 = var10000.getHeight();

        for(int var3 = 0; var3 < var1; ++var3) {
            for(int var4 = 0; var4 < var2; ++var4) {
                if (var0.getPixel(var3, var4) == 0) {
                    var0.setPixel(var3, var4, -1);
                }
            }
        }

        return var0;
    }

    public static byte[] putARGBbitmap(int var0, int var1, Bitmap var2) {
        Bitmap var3;
        byte[] var10000 = putRGBbitmap(var0, var1, var3 = removeAlphaValue(var2));
        var3.recycle();
        return var10000;
    }

    public static byte[] putRGBbitmap(int var0, int var1, Bitmap var2) {
        return drawBitmap(var0, var1, var2);
    }

    public static byte[] fastPrintARGBbitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        Bitmap var5;
        byte[] var10000 = fastPrintRGBbitmap(var0, var1, var2, var3, var5 = removeAlphaValue(var4));
        var5.recycle();
        return var10000;
    }

    public static byte[] fastPrintRGBbitmap(int var0, int var1, int var2, int var3, Bitmap var4) {
        return fastPrintBitmap(var0, var1, var2, var3, var4);
    }

    static class C3 {
        int r;
        int g;
        int b;

        public C3(int var1) {
            this.r = Color.red(var1);
            this.g = Color.green(var1);
            this.b = Color.blue(var1);
        }

        public C3(int var1, int var2, int var3) {
            this.r = var1;
            this.g = var2;
            this.b = var3;
        }

        public C3 add(C3 var1) {
            return new C3(this.r + var1.r, this.g + var1.g, this.b + var1.b);
        }

        public C3 sub(C3 var1) {
            return new C3(this.r - var1.r, this.g - var1.g, this.b - var1.b);
        }

        public C3 mul(double var1) {
            return new C3((int)(var1 * (double)this.r), (int)(var1 * (double)this.g), (int)(var1 * (double)this.b));
        }

        public int diff(C3 var1) {
            return Math.abs(this.r - var1.r) + Math.abs(this.g - var1.g) + Math.abs(this.b - var1.b);
        }

        public int toRGB() {
            C3 var10000 = this;
            C3 var10001 = this;
            C3 var10002 = this;
            C3 var10003 = this;
            int var3 = this.clamp(this.r);
            int var1 = var10002.clamp(var10003.g);
            int var2 = var10000.clamp(var10001.b);
            return Color.argb(255, var3, var1, var2);
        }

        public int clamp(int var1) {
            return Math.max(0, Math.min(255, var1));
        }
    }
}


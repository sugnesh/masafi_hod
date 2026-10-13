package com.engear.vansales.printer;

import java.lang.reflect.Array;

public class Arabic864 {
    static int f4a;
    static int f5b;
    static char[][] f6c;

    static {
        f4a = 43;
        f5b = 5;
        f6c = (char[][]) Array.newInstance(Character.TYPE, new int[]{f4a, f5b});
    }

    private byte[] m8a(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[(bArr.length - 1) - i];
        }
        return bArr2;
    }

    private byte[] m9b(byte[] bArr, boolean z) {
        int i;
        Object a = m8a(bArr);
        byte[] bArr2 = (byte[]) ((byte[])a).clone();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (i = 0; i < ((byte[])a).length; i++) {
            if (m13b(((byte[])a)[i])) {
                i2++;
                if (i4 == 0) {
                    i3 = i;
                    i4 = 1;
                }
            } else {
                if (i - i3 > 1 && i4 != 0) {
                    i4 = i - 1;
                    int i5 = 0;
                    for (i4 = i - 1; i4 >= i3; i4--) {
                        bArr2[i4] = ((byte[])a)[i3 + i5];
                        i5++;
                    }
                }
                i4 = 0;
            }
        }
        if (i2 == ((byte[])a).length) {
            i2 = 0;
            for (i = ((byte[])a).length - 1; i >= 0; i--) {
                bArr2[i] = ((byte[])a)[0 + i2];
                i2++;
            }
        }
        return m12a(bArr2, z);
    }

    public byte[] Convert(String str, boolean z) {
        f6c[37][0] = '\u063b';
        f6c[37][1] = '\u00e0';
        f6c[37][2] = '\u00e0';
        f6c[37][3] = '\u00e0';
        f6c[37][4] = '\u00e0';
        f6c[38][0] = '\u0099';
        f6c[38][1] = '\u0099';
        f6c[38][2] = '\u009a';
        f6c[38][3] = '\u0099';
        f6c[38][4] = '\u0099';
        f6c[39][0] = '\u009d';
        f6c[39][1] = '\u009e';
        f6c[39][2] = '\u009d';
        f6c[39][3] = '\u009e';
        f6c[39][4] = '\u009d';
        f6c[40][0] = '\u0651';
        f6c[40][1] = '\u00f1';
        f6c[40][2] = '\u00f1';
        f6c[40][3] = '\u00f1';
        f6c[40][4] = '\u00f1';
        f6c[0][0] = '\u0630';
        f6c[0][1] = '\u00d0';
        f6c[0][2] = '\u00d0';
        f6c[0][3] = '\u00d0';
        f6c[0][4] = '\u00d0';
        f6c[1][0] = '\u062f';
        f6c[1][1] = '\u00cf';
        f6c[1][2] = '\u00cf';
        f6c[1][3] = '\u00cf';
        f6c[1][4] = '\u00cf';
        f6c[2][0] = '\u062c';
        f6c[2][1] = '\u00ad';
        f6c[2][2] = '\u00cc';
        f6c[2][3] = '\u00cc';
        f6c[2][4] = '\u00ad';
        f6c[3][0] = '\u062d';
        f6c[3][1] = '\u00ae';
        f6c[3][2] = '\u00cd';
        f6c[3][3] = '\u00cd';
        f6c[3][4] = '\u00ae';
        f6c[4][0] = '\u062e';
        f6c[4][1] = '\u00af';
        f6c[4][2] = '\u00ce';
        f6c[4][3] = '\u00ce';
        f6c[4][4] = '\u00af';
        f6c[5][0] = '\u0647';
        f6c[5][1] = '\u00f3';
        f6c[5][2] = '\u00e7';
        f6c[5][3] = '\u00f4';
        f6c[5][4] = '\u00f3';
        f6c[6][0] = '\u0639';
        f6c[6][1] = '\u00c5';
        f6c[6][2] = '\u00d9';
        f6c[6][3] = '\u00ec';
        f6c[6][4] = '\u00df';
        f6c[7][0] = '\u063a';
        f6c[7][1] = '\u00ed';
        f6c[7][2] = '\u00da';
        f6c[7][3] = '\u00f7';
        f6c[7][4] = '\u00ee';
        f6c[8][0] = '\u0641';
        f6c[8][1] = '\u00ba';
        f6c[8][2] = '\u00e1';
        f6c[8][3] = '\u00e1';
        f6c[8][4] = '\u00ba';
        f6c[9][0] = '\u0642';
        f6c[9][1] = '\u00f8';
        f6c[9][2] = '\u00e2';
        f6c[9][3] = '\u00e2';
        f6c[9][4] = '\u00f8';
        f6c[10][0] = '\u062b';
        f6c[10][1] = '\u00ab';
        f6c[10][2] = '\u00cb';
        f6c[10][3] = '\u00cb';
        f6c[10][4] = '\u00ab';
        f6c[11][0] = '\u0635';
        f6c[11][1] = '\u00be';
        f6c[11][2] = '\u00d5';
        f6c[11][3] = '\u00d5';
        f6c[11][4] = '\u00be';
        f6c[12][0] = '\u0636';
        f6c[12][1] = '\u00eb';
        f6c[12][2] = '\u00d6';
        f6c[12][3] = '\u00d6';
        f6c[12][4] = '\u00eb';
        f6c[13][0] = '\u0637';
        f6c[13][1] = '\u00d7';
        f6c[13][2] = '\u00d7';
        f6c[13][3] = '\u00d7';
        f6c[13][4] = '\u00d7';
        f6c[14][0] = '\u0643';
        f6c[14][1] = '\u00fc';
        f6c[14][2] = '\u00e3';
        f6c[14][3] = '\u00e3';
        f6c[14][4] = '\u00fc';
        f6c[15][0] = '\u0645';
        f6c[15][1] = '\u00ef';
        f6c[15][2] = '\u00e5';
        f6c[15][3] = '\u00e5';
        f6c[15][4] = '\u00ef';
        f6c[16][0] = '\u0646';
        f6c[16][1] = '\u00f2';
        f6c[16][2] = '\u00e6';
        f6c[16][3] = '\u00e6';
        f6c[16][4] = '\u00f2';
        f6c[17][0] = '\u062a';
        f6c[17][1] = '\u00aa';
        f6c[17][2] = '\u00ca';
        f6c[17][3] = '\u00aa';
        f6c[17][4] = '\u00aa';
        f6c[18][0] = '\u0627';
        f6c[18][1] = '\u00a8';
        f6c[18][2] = '\u00c7';
        f6c[18][3] = '\u00a8';
        f6c[18][4] = '\u00c7';
        f6c[19][0] = '\u0644';
        f6c[19][1] = '\u00fb';
        f6c[19][2] = '\u00e4';
        f6c[19][3] = '\u00e4';
        f6c[19][4] = '\u00fb';
        f6c[20][0] = '\u0628';
        f6c[20][1] = '\u00a9';
        f6c[20][2] = '\u00c8';
        f6c[20][3] = '\u00c8';
        f6c[20][4] = '\u00a9';
        f6c[21][0] = '\u064a';
        f6c[21][1] = '\u00f6';
        f6c[21][2] = '\u00ea';
        f6c[21][3] = '\u00ea';
        f6c[21][4] = '\u00fd';
        f6c[22][0] = '\u0633';
        f6c[22][1] = '\u00bc';
        f6c[22][2] = '\u00d3';
        f6c[22][3] = '\u00d3';
        f6c[22][4] = '\u00bc';
        f6c[23][0] = '\u0634';
        f6c[23][1] = '\u00bd';
        f6c[23][2] = '\u00d4';
        f6c[23][3] = '\u00d4';
        f6c[23][4] = '\u00bd';
        f6c[24][0] = '\u0638';
        f6c[24][1] = '\u00d8';
        f6c[24][2] = '\u00d8';
        f6c[24][3] = '\u00d8';
        f6c[24][4] = '\u00d8';
        f6c[25][0] = '\u0632';
        f6c[25][1] = '\u00d2';
        f6c[25][2] = '\u00d2';
        f6c[25][3] = '\u00d2';
        f6c[25][4] = '\u00d2';
        f6c[26][0] = '\u0648';
        f6c[26][1] = '\u00e8';
        f6c[26][2] = '\u00e8';
        f6c[26][3] = '\u00e8';
        f6c[26][4] = '\u00e8';
        f6c[27][0] = '\u0629';
        f6c[27][1] = '\u00c9';
        f6c[27][2] = '\u00c9';
        f6c[27][3] = '\u00c9';
        f6c[27][4] = '\u00c9';
        f6c[28][0] = '\u0649';
        f6c[28][1] = '\u00f5';
        f6c[28][2] = '\u00e9';
        f6c[28][3] = '\u00f5';
        f6c[28][4] = '\u00e9';
        f6c[29][0] = '\u0631';
        f6c[29][1] = '\u00d1';
        f6c[29][2] = '\u00d1';
        f6c[29][3] = '\u00d1';
        f6c[29][4] = '\u00d1';
        f6c[30][0] = '\u0624';
        f6c[30][1] = '\u00c4';
        f6c[30][2] = '\u00c4';
        f6c[30][3] = '\u00c4';
        f6c[30][4] = '\u00c4';
        f6c[31][0] = '\u0621';
        f6c[31][1] = '\u00c1';
        f6c[31][2] = '\u00c3';
        f6c[31][3] = '\u00a5';
        f6c[31][4] = '\u00c1';
        f6c[32][0] = '\u0626';
        f6c[32][1] = '\u00c6';
        f6c[32][2] = '\u00c6';
        f6c[32][3] = '\u00c6';
        f6c[32][4] = '\u00c6';
        f6c[33][0] = '\u0623';
        f6c[33][1] = '\u00a5';
        f6c[33][2] = '\u00c3';
        f6c[33][3] = '\u00a5';
        f6c[33][4] = '\u00c3';
        f6c[34][0] = '\u0622';
        f6c[34][1] = '\u00a2';
        f6c[34][2] = '\u00c2';
        f6c[34][3] = '\u00a2';
        f6c[34][4] = '\u00c2';
        f6c[35][0] = '\u0625';
        f6c[35][1] = '\ufe88';
        f6c[35][2] = '\ufe87';
        f6c[35][3] = '\ufe88';
        f6c[35][4] = '\ufe87';
        f6c[36][0] = '\ufefb';
        f6c[36][1] = '\u009d';
        f6c[36][2] = '\u009e';
        f6c[36][3] = '\u009e';
        f6c[36][4] = '\u009d';
        f6c[41][0] = '\u00f9';
        f6c[41][1] = '\u00f9';
        f6c[41][2] = '\u00fa';
        f6c[41][3] = '\u00f9';
        f6c[41][4] = '\u00f9';
        f6c[42][0] = '\u064b';
        f6c[42][1] = '\u00f1';
        f6c[42][2] = '\u00f1';
        f6c[42][3] = '\u00f1';
        f6c[42][4] = '\u00f1';
        str.trim();
        String str2 = "";
        int i = 0;
        while (i < str.trim().length()) {
            if (str.trim().charAt(i) != '\u0644') {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.trim().charAt(i)).toString();
            } else if (i == str.trim().length() - 1) {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.trim().charAt(i)).toString();
            } else if (str.trim().charAt(i + 1) == '\u0627') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\u009d').toString();
                i++;
            } else if (str.trim().charAt(i + 1) == '\u0625') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\u0099').toString();
                i++;
            } else if (str.trim().charAt(i + 1) == '\u0622') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\u00f9').toString();
                i++;
            } else if (str.trim().charAt(i + 1) == '\u0623') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\u0099').toString();
                i++;
            } else {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.trim().charAt(i)).toString();
            }
            i++;
        }
        String trim = str2.trim();
        byte[] bArr = new byte[trim.length()];
        i = 0;
        while (i < str2.trim().length()) {
            Object obj;
            int i2;
            Object obj2 = null;
            int i3 = -1;
            for (int i4 = 0; i4 < f4a; i4++) {
                for (int i5 = 0; i5 < f5b; i5++) {
                    if (f6c[i4][i5] == str2.charAt(i)) {
                        obj2 = 1;
                        i3 = i4;
                        break;
                    }
                }
                if (obj2 != null) {
                    obj = obj2;
                    i2 = i3;
                    break;
                }
            }
            obj = obj2;
            i2 = i3;
            if (obj != null) {
                Object obj3;
                if (i == str2.trim().length() - 1) {
                    obj3 = null;
                } else {
                    obj = (m11a(str2.trim().charAt(i + 1)) || m14b(str2.trim().charAt(i + 1))) ? 1 : null;
                    obj3 = obj;
                }
                boolean a = i == 0 ? false : m11a(str2.trim().charAt(i - 1));
                if (a /*&& r3 != null*/) {
                    trim.replace(trim.charAt(i), f6c[i2][3]);
                    bArr[i] = (byte) f6c[i2][3];
                }
                if (a /*&& r3 == null*/) {
                    trim.replace(trim.charAt(i), f6c[i2][1]);
                    bArr[i] = (byte) f6c[i2][1];
                }
                if (!(a /*|| r3 == null*/)) {
                    trim.replace(trim.charAt(i), f6c[i2][2]);
                    bArr[i] = (byte) f6c[i2][2];
                }
                if (!a /*&& r3 == null*/) {
                    trim.replace(trim.charAt(i), f6c[i2][4]);
                    bArr[i] = (byte) f6c[i2][4];
                }
            } else {
                bArr[i] = (byte) trim.charAt(i);
            }
            i++;
        }
        return m9b(bArr, z);
    }

    public byte[] GetBytes(String str) {
        char[] toCharArray = str.toCharArray();
        byte[] bArr = new byte[toCharArray.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) toCharArray[i];
        }
        return bArr;
    }

    boolean m10a(byte b) {
        int i = 0;
        byte[] bArr = new byte[]{(byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 48, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 96};
        boolean z = false;
        while (i < bArr.length) {
            if (b == bArr[i]) {
                z = true;
                i = bArr.length;
            }
            i++;
        }
        return z;
    }

    boolean m11a(char c) {
        char[] cArr = new char[]{'\u062c', '\u062d', '\u062e', '\u0647', '\u0639', '\u063a', '\u0641', '\u0642', '\u062b', '\u0635', '\u0636', '\u0637', '\u0643', '\u0645', '\u0646', '\u062a', '\u0644', '\u0628', '\u064a', '\u0633', '\u0634', '\u0638', '\u0626', '\u009d'};
        for (int i = 0; i < 24; i++) {
            if (c == cArr[i]) {
                return true;
            }
        }
        return false;
    }

    byte[] m12a(byte[] bArr, boolean z) {
        int i = 0;
        while (i < bArr.length) {
            if (m10a(bArr[i])) {
                if (bArr[i] == (byte) 48 || bArr[i] == 96) {
                    if (z) {
                        bArr[i] = (byte) -80;
                    } else {
                        bArr[i] = (byte) 48;
                    }
                } else if (bArr[i] == (byte) 49 || bArr[i] == 97) {
                    if (z) {
                        bArr[i] = (byte) -79;
                    } else {
                        bArr[i] = (byte) 49;
                    }
                } else if (bArr[i] == (byte) 50 || bArr[i] == 98) {
                    if (z) {
                        bArr[i] = (byte) -78;
                    } else {
                        bArr[i] = (byte) 50;
                    }
                } else if (bArr[i] == (byte) 51 || bArr[i] == 99) {
                    if (z) {
                        bArr[i] = (byte) -77;
                    } else {
                        bArr[i] = (byte) 51;
                    }
                } else if (bArr[i] == (byte) 52 || bArr[i] == 100) {
                    if (z) {
                        bArr[i] = (byte) -76;
                    } else {
                        bArr[i] = (byte) 52;
                    }
                } else if (bArr[i] == 53 || bArr[i] == 101) {
                    if (z) {
                        bArr[i] = (byte) -75;
                    } else {
                        bArr[i] = (byte) 53;
                    }
                } else if (bArr[i] == 54 || bArr[i] == 102) {
                    if (z) {
                        bArr[i] = (byte) -74;
                    } else {
                        bArr[i] = (byte) 54;
                    }
                } else if (bArr[i] == 55 || bArr[i] == 103) {
                    if (z) {
                        bArr[i] = (byte) -73;
                    } else {
                        bArr[i] = (byte) 55;
                    }
                } else if (bArr[i] == 56 || bArr[i] == 104) {
                    if (z) {
                        bArr[i] = (byte) -72;
                    } else {
                        bArr[i] = (byte) 56;
                    }
                } else if (bArr[i] == 57 || bArr[i] == 105) {
                    if (z) {
                        bArr[i] = (byte) -71;
                    } else {
                        bArr[i] = (byte) 57;
                    }
                }
            }
            i++;
        }
        return bArr;
    }

    boolean m13b(byte b) {
        int i = 0;
        char[] cArr = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '=', '{', '}', '[', ']', '\\', '|', ':', ';', '<', '>', ',', '.', '/', '?', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669', '\u0660'};
        int i2 = 0;
        boolean z = false;
        while (i2 < cArr.length) {
            if (((char) b) == cArr[i2]) {
                i2 = cArr.length;
                z = true;
            }
            i2++;
        }
        if (!z) {
            byte[] bArr = new byte[]{(byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 96};
            while (i < bArr.length) {
                if (b == bArr[i]) {
                    i = bArr.length;
                    z = true;
                }
                i++;
            }
        }
        return z;
    }

    boolean m14b(char c) {
        char[] cArr = new char[]{'\u0627', '\u0623', '\u0625', '\u0622', '\u062f', '\u0630', '\u0631', '\u0632', '\u0648', '\u0624', '\u0629', '\u0649', '\u0626', '\ufefb', '\ufef9', '\ufef7', '\ufef5'};
        for (int i = 0; i < 17; i++) {
            if (c == cArr[i]) {
                return true;
            }
        }
        return false;
    }

    boolean m15c(char c) {
        int i = 0;
        char[] cArr = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '\u0660', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669', '.'};
        boolean z = false;
        while (i < 21) {
            if (c == cArr[i]) {
                z = true;
                i = 22;
            }
            i++;
        }
        return z;
    }
}

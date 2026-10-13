package com.engear.vansales.printer;

import java.lang.reflect.Array;

public class UnicodeArabic {
    static int f13a;
    static int f14b;
    char[][] f15c;

    public enum IntermecPrinterType {
        FP,
        IPL
    }

    static {
        f13a = 40;
        f14b = 5;
    }

    public UnicodeArabic() {
        this.f15c = (char[][]) Array.newInstance(Character.TYPE, new int[]{f13a, f14b});
    }

    static String m24a(char[] cArr) {
        if (cArr.length <= 0) {
            return "";
        }
        String str = "";
        for (char append : cArr) {
            str = new StringBuilder(String.valueOf(str)).append("CHR$(").append(append).append(")+").toString();
        }
        return str.substring(0, str.length() - 1);
    }

    static char m25b(char c) {
        char[] cArr = new char[]{'\u0660', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669'};
        char[] cArr2 = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        for (int i = 0; i < 10; i++) {
            if (c == cArr2[i]) {
                return cArr[i];
            }
        }
        return c;
    }

    private char[] m26b(char[] cArr) {
        char[] cArr2 = new char[cArr.length];
        for (int i = 0; i < cArr.length; i++) {
            cArr2[i] = cArr[(cArr.length - 1) - i];
        }
        return cArr2;
    }

    private char[] m27b(char[] cArr, boolean z) {
        int i;
        Object b = m26b(cArr);
        char[] cArr2 = (char[])((char[])b).clone();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (i = 0; i < ((char[])b).length; i++) {
            if (m30a(((char[])b)[i])) {
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
                        cArr2[i4] = ((char[])b)[i3 + i5];
                        i5++;
                    }
                }
                i4 = 0;
            }
        }
        if (i2 == ((char[])b).length) {
            i2 = 0;
            for (i = ((char[])b).length - 1; i >= 0; i--) {
                cArr2[i] = ((char[])b)[0 + i2];
                i2++;
            }
        }
        return m31a(cArr2, z);
    }

    private static boolean m28d(char c) {
        char[] cArr = new char[]{'\u062c', '\u062d', '\u062e', '\u0647', '\u0639', '\u063a', '\u0641', '\u0642', '\u062b', '\u0635', '\u0636', '\u0637', '\u0643', '\u0645', '\u0646', '\u062a', '\u0644', '\u0628', '\u064a', '\u0633', '\u0634', '\u0638', '\u0626'};
        for (int i = 0; i < 23; i++) {
            if (c == cArr[i]) {
                return true;
            }
        }
        return false;
    }

    private static boolean m29e(char c) {
        char[] cArr = new char[]{'\u0627', '\u0623', '\u0625', '\u0622', '\u062f', '\u0630', '\u0631', '\u0632', '\u0648', '\u0624', '\u0629', '\u0649', '\ufefb', '\ufef9', '\ufef7', '\ufef5', '\u0626'};
        for (int i = 0; i < 17; i++) {
            if (c == cArr[i]) {
                return true;
            }
        }
        return false;
    }

    public String Convert(String str, boolean z) {
        this.f15c[0][0] = '\u0630';
        this.f15c[0][1] = '\ufeac';
        this.f15c[0][2] = '\ufeab';
        this.f15c[0][3] = '\ufeac';
        this.f15c[0][4] = '\ufeab';
        this.f15c[1][0] = '\u062f';
        this.f15c[1][1] = '\ufeaa';
        this.f15c[1][2] = '\ufea9';
        this.f15c[1][3] = '\ufeaa';
        this.f15c[1][4] = '\ufea9';
        this.f15c[2][0] = '\u062c';
        this.f15c[2][1] = '\ufe9e';
        this.f15c[2][2] = '\ufe9f';
        this.f15c[2][3] = '\ufea0';
        this.f15c[2][4] = '\ufe9d';
        this.f15c[3][0] = '\u062d';
        this.f15c[3][1] = '\ufea2';
        this.f15c[3][2] = '\ufea3';
        this.f15c[3][3] = '\ufea4';
        this.f15c[3][4] = '\ufea1';
        this.f15c[4][0] = '\u062e';
        this.f15c[4][1] = '\ufea6';
        this.f15c[4][2] = '\ufea7';
        this.f15c[4][3] = '\ufea8';
        this.f15c[4][4] = '\ufea5';
        this.f15c[5][0] = '\u0647';
        this.f15c[5][1] = '\ufeea';
        this.f15c[5][2] = '\ufeeb';
        this.f15c[5][3] = '\ufeec';
        this.f15c[5][4] = '\ufee9';
        this.f15c[6][0] = '\u0639';
        this.f15c[6][1] = '\ufeca';
        this.f15c[6][2] = '\ufecb';
        this.f15c[6][3] = '\ufecc';
        this.f15c[6][4] = '\ufec9';
        this.f15c[7][0] = '\u063a';
        this.f15c[7][1] = '\ufece';
        this.f15c[7][2] = '\ufecf';
        this.f15c[7][3] = '\ufed0';
        this.f15c[7][4] = '\ufecd';
        this.f15c[8][0] = '\u0641';
        this.f15c[8][1] = '\ufed2';
        this.f15c[8][2] = '\ufed3';
        this.f15c[8][3] = '\ufed4';
        this.f15c[8][4] = '\ufed1';
        this.f15c[9][0] = '\u0642';
        this.f15c[9][1] = '\ufed6';
        this.f15c[9][2] = '\ufed7';
        this.f15c[9][3] = '\ufed8';
        this.f15c[9][4] = '\ufed5';
        this.f15c[10][0] = '\u062b';
        this.f15c[10][1] = '\ufe9a';
        this.f15c[10][2] = '\ufe9b';
        this.f15c[10][3] = '\ufe9c';
        this.f15c[10][4] = '\ufe99';
        this.f15c[11][0] = '\u0635';
        this.f15c[11][1] = '\ufeba';
        this.f15c[11][2] = '\ufebb';
        this.f15c[11][3] = '\ufebc';
        this.f15c[11][4] = '\ufeb9';
        this.f15c[12][0] = '\u0636';
        this.f15c[12][1] = '\ufebe';
        this.f15c[12][2] = '\ufebf';
        this.f15c[12][3] = '\ufec0';
        this.f15c[12][4] = '\ufebd';
        this.f15c[13][0] = '\u0637';
        this.f15c[13][1] = '\ufec2';
        this.f15c[13][2] = '\ufec3';
        this.f15c[13][3] = '\ufec4';
        this.f15c[13][4] = '\ufec1';
        this.f15c[14][0] = '\u0643';
        this.f15c[14][1] = '\ufeda';
        this.f15c[14][2] = '\ufedb';
        this.f15c[14][3] = '\ufedc';
        this.f15c[14][4] = '\ufed9';
        this.f15c[15][0] = '\u0645';
        this.f15c[15][1] = '\ufee2';
        this.f15c[15][2] = '\ufee3';
        this.f15c[15][3] = '\ufee4';
        this.f15c[15][4] = '\ufee1';
        this.f15c[16][0] = '\u0646';
        this.f15c[16][1] = '\ufee6';
        this.f15c[16][2] = '\ufee7';
        this.f15c[16][3] = '\ufee8';
        this.f15c[16][4] = '\ufee5';
        this.f15c[17][0] = '\u062a';
        this.f15c[17][1] = '\ufe96';
        this.f15c[17][2] = '\ufe97';
        this.f15c[17][3] = '\ufe98';
        this.f15c[17][4] = '\ufe95';
        this.f15c[18][0] = '\u0627';
        this.f15c[18][1] = '\ufe8e';
        this.f15c[18][2] = '\ufe8d';
        this.f15c[18][3] = '\ufe8e';
        this.f15c[18][4] = '\ufe8d';
        this.f15c[19][0] = '\u0644';
        this.f15c[19][1] = '\ufede';
        this.f15c[19][2] = '\ufedf';
        this.f15c[19][3] = '\ufee0';
        this.f15c[19][4] = '\ufedd';
        this.f15c[20][0] = '\u0628';
        this.f15c[20][1] = '\ufe90';
        this.f15c[20][2] = '\ufe91';
        this.f15c[20][3] = '\ufe92';
        this.f15c[20][4] = '\ufe8f';
        this.f15c[21][0] = '\u064a';
        this.f15c[21][1] = '\ufef2';
        this.f15c[21][2] = '\ufef3';
        this.f15c[21][3] = '\ufef4';
        this.f15c[21][4] = '\ufef1';
        this.f15c[22][0] = '\u0633';
        this.f15c[22][1] = '\ufeb2';
        this.f15c[22][2] = '\ufeb3';
        this.f15c[22][3] = '\ufeb4';
        this.f15c[22][4] = '\ufeb1';
        this.f15c[23][0] = '\u0634';
        this.f15c[23][1] = '\ufeb6';
        this.f15c[23][2] = '\ufeb7';
        this.f15c[23][3] = '\ufeb8';
        this.f15c[23][4] = '\ufeb5';
        this.f15c[24][0] = '\u0638';
        this.f15c[24][1] = '\ufec6';
        this.f15c[24][2] = '\ufec7';
        this.f15c[24][3] = '\ufec8';
        this.f15c[24][4] = '\ufec5';
        this.f15c[25][0] = '\u0632';
        this.f15c[25][1] = '\ufeb0';
        this.f15c[25][2] = '\ufeaf';
        this.f15c[25][3] = '\ufeb0';
        this.f15c[25][4] = '\ufeaf';
        this.f15c[26][0] = '\u0648';
        this.f15c[26][1] = '\ufeee';
        this.f15c[26][2] = '\ufeed';
        this.f15c[26][3] = '\ufeee';
        this.f15c[26][4] = '\ufeed';
        this.f15c[27][0] = '\u0629';
        this.f15c[27][1] = '\ufe94';
        this.f15c[27][2] = '\ufe93';
        this.f15c[27][3] = '\ufe93';
        this.f15c[27][4] = '\ufe93';
        this.f15c[28][0] = '\u0649';
        this.f15c[28][1] = '\ufef0';
        this.f15c[28][2] = '\ufeef';
        this.f15c[28][3] = '\ufef0';
        this.f15c[28][4] = '\ufeef';
        this.f15c[29][0] = '\u0631';
        this.f15c[29][1] = '\ufeae';
        this.f15c[29][2] = '\ufead';
        this.f15c[29][3] = '\ufeae';
        this.f15c[29][4] = '\ufead';
        this.f15c[30][0] = '\u0624';
        this.f15c[30][1] = '\ufe86';
        this.f15c[30][2] = '\ufe85';
        this.f15c[30][3] = '\ufe86';
        this.f15c[30][4] = '\ufe85';
        this.f15c[31][0] = '\u0621';
        this.f15c[31][1] = '\ufe80';
        this.f15c[31][2] = '\ufe80';
        this.f15c[31][3] = '\ufe80';
        this.f15c[31][4] = '\ufe80';
        this.f15c[32][0] = '\u0626';
        this.f15c[32][1] = '\ufe8a';
        this.f15c[32][2] = '\ufe8b';
        this.f15c[32][3] = '\ufe8c';
        this.f15c[32][4] = '\ufe89';
        this.f15c[33][0] = '\u0623';
        this.f15c[33][1] = '\ufe84';
        this.f15c[33][2] = '\ufe83';
        this.f15c[33][3] = '\ufe84';
        this.f15c[33][4] = '\ufe83';
        this.f15c[34][0] = '\u0622';
        this.f15c[34][1] = '\ufe82';
        this.f15c[34][2] = '\ufe81';
        this.f15c[34][3] = '\ufe82';
        this.f15c[34][4] = '\ufe81';
        this.f15c[35][0] = '\u0625';
        this.f15c[35][1] = '\ufe88';
        this.f15c[35][2] = '\ufe87';
        this.f15c[35][3] = '\ufe88';
        this.f15c[35][4] = '\ufe87';
        this.f15c[36][0] = '\ufefb';
        this.f15c[36][1] = '\ufefc';
        this.f15c[36][2] = '\ufefb';
        this.f15c[36][3] = '\ufefc';
        this.f15c[36][4] = '\ufefb';
        this.f15c[37][0] = '\ufef9';
        this.f15c[37][1] = '\ufefa';
        this.f15c[37][2] = '\ufef9';
        this.f15c[37][3] = '\ufefa';
        this.f15c[37][4] = '\ufef9';
        this.f15c[38][0] = '\ufef7';
        this.f15c[38][1] = '\ufef8';
        this.f15c[38][2] = '\ufef7';
        this.f15c[38][3] = '\ufef8';
        this.f15c[38][4] = '\ufef7';
        this.f15c[39][0] = '\ufef5';
        this.f15c[39][1] = '\ufef6';
        this.f15c[39][2] = '\ufef5';
        this.f15c[39][3] = '\ufef6';
        this.f15c[39][4] = '\ufef5';
        str.trim();
        String trim = str.trim();
        String str2 = "";
        int i = 0;
        while (i < trim.length()) {
            if (trim.charAt(i) != '\u0644') {
                str2 = new StringBuilder(String.valueOf(str2)).append(trim.charAt(i)).toString();
            } else if (i == trim.length() - 1) {
                str2 = new StringBuilder(String.valueOf(str2)).append(trim.charAt(i)).toString();
            } else if (trim.charAt(i + 1) == '\u0627') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufefb').toString();
                i++;
            } else if (trim.charAt(i + 1) == '\u0625') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufef9').toString();
                i++;
            } else if (trim.charAt(i + 1) == '\u0623') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufef7').toString();
                i++;
            } else if (trim.charAt(i + 1) == '\u0622') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufef5').toString();
                i++;
            } else {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.charAt(i)).toString();
            }
            i++;
        }
        byte[] bArr = new byte[str2.length()];
        char[] cArr = new char[str2.length()];
        i = 0;
        while (i < str2.length()) {
            Object obj;
            int i2;
            Object obj2 = null;
            int i3 = -1;
            for (int i4 = 0; i4 < f13a; i4++) {
                for (int i5 = 0; i5 < f14b; i5++) {
                    if (this.f15c[i4][i5] == str2.charAt(i)) {
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
                if (i == str2.length() - 1) {
                    obj3 = null;
                } else {
                    obj = (m28d(str2.charAt(i + 1)) || m29e(str2.charAt(i + 1))) ? 1 : null;
                    obj3 = obj;
                }
                boolean d = i == 0 ? false : m28d(str2.charAt(i - 1));
                if (d /*&& r3 != null*/) {
                    str2.replace(str2.charAt(i), this.f15c[i2][3]);
                    cArr[i] = this.f15c[i2][3];
                }
                if (d /*&& r3 == null*/) {
                    str2.replace(str2.charAt(i), this.f15c[i2][1]);
                    cArr[i] = this.f15c[i2][1];
                }
                if (!(d /*|| r3 == null*/)) {
                    str2.replace(str2.charAt(i), this.f15c[i2][2]);
                    cArr[i] = this.f15c[i2][2];
                }
                if (!d /*&& r3 == null*/) {
                    str2.replace(str2.charAt(i), this.f15c[i2][4]);
                    cArr[i] = this.f15c[i2][4];
                }
            } else {
                cArr[i] = str2.charAt(i);
            }
            i++;
        }
        return new String(m27b(cArr, z));
    }

    boolean m30a(char c) {
        int i = 0;
        char[] cArr = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '-', '_', '+', '=', '{', '}', '[', ']', '\\', '|', ':', ';', '<', '>', ',', '.', '/', '?', '\u0661', '\u0662', '\u0663', '\u0664', '\u0665', '\u0666', '\u0667', '\u0668', '\u0669', '\u0660'};
        int i2 = 0;
        boolean z = false;
        while (i2 < cArr.length) {
            if (c == cArr[i2]) {
                i2 = cArr.length;
                z = true;
            }
            i2++;
        }
        if (!z) {
            byte[] bArr = new byte[]{(byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 96};
            while (i < bArr.length) {
                if (((byte) c) == bArr[i]) {
                    i = bArr.length;
                    z = true;
                }
                i++;
            }
        }
        return z;
    }

    char[] m31a(char[] cArr, boolean z) {
        int i = 0;
        while (i < cArr.length) {
            if (m32c(cArr[i])) {
                if (cArr[i] == '0' || cArr[i] == '`') {
                    if (z) {
                        cArr[i] = '`';
                    } else {
                        cArr[i] = '0';
                    }
                } else if (cArr[i] == '1' || cArr[i] == 'a') {
                    if (z) {
                        cArr[i] = 'a';
                    } else {
                        cArr[i] = '1';
                    }
                } else if (cArr[i] == '2' || cArr[i] == 'b') {
                    if (z) {
                        cArr[i] = 'b';
                    } else {
                        cArr[i] = '2';
                    }
                } else if (cArr[i] == '3' || cArr[i] == 'c') {
                    if (z) {
                        cArr[i] = 'c';
                    } else {
                        cArr[i] = '3';
                    }
                } else if (cArr[i] == '4' || cArr[i] == 'd') {
                    if (z) {
                        cArr[i] = 'd';
                    } else {
                        cArr[i] = '4';
                    }
                } else if (cArr[i] == '5' || cArr[i] == 'e') {
                    if (z) {
                        cArr[i] = 'e';
                    } else {
                        cArr[i] = '5';
                    }
                } else if (cArr[i] == '6' || cArr[i] == 'f') {
                    if (z) {
                        cArr[i] = 'f';
                    } else {
                        cArr[i] = '6';
                    }
                } else if (cArr[i] == '7' || cArr[i] == 'g') {
                    if (z) {
                        cArr[i] = 'g';
                    } else {
                        cArr[i] = '7';
                    }
                } else if (cArr[i] == '8' || cArr[i] == 'h') {
                    if (z) {
                        cArr[i] = 'h';
                    } else {
                        cArr[i] = '8';
                    }
                } else if (cArr[i] == '9' || cArr[i] == 'i') {
                    if (z) {
                        cArr[i] = 'i';
                    } else {
                        cArr[i] = '9';
                    }
                }
            }
            i++;
        }
        return cArr;
    }

    boolean m32c(char c) {
        int i = 0;
        byte[] bArr = new byte[]{(byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 48, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 96};
        boolean z = false;
        while (i < bArr.length) {
            if (((byte) c) == bArr[i]) {
                z = true;
                i = bArr.length;
            }
            i++;
        }
        return z;
    }
}

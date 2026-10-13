package com.engear.vansales.printer;

import java.lang.reflect.Array;

public class PBXArabic {
    static int f7a;
    static int f8b;
    char[][] f9c;

    static {
        f7a = 41;
        f8b = 5;
    }

    public PBXArabic() {
        this.f9c = (char[][]) Array.newInstance(Character.TYPE, new int[]{f7a, f8b});
    }

    private byte[] m16a(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[(bArr.length - 1) - i];
        }
        return bArr2;
    }

    private byte[] m17b(byte[] bArr, boolean z) {
        int i;
        Object a = m16a(bArr);
        byte[] bArr2 = (byte[]) ((byte[])a).clone();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        for (i = 0; i < ((byte[])a).length; i++) {
            if (m21b(((byte[])a)[i])) {
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
        return m20a(bArr2, z);
    }

    public byte[] Convert(String str, boolean z) {
        this.f9c[0][0] = '\u0630';
        this.f9c[0][1] = 'g';
        this.f9c[0][2] = 'g';
        this.f9c[0][3] = 'g';
        this.f9c[0][4] = 'g';
        this.f9c[1][0] = '\u062f';
        this.f9c[1][1] = 'e';
        this.f9c[1][2] = 'e';
        this.f9c[1][3] = 'e';
        this.f9c[1][4] = 'e';
        this.f9c[2][0] = '\u062c';
        this.f9c[2][1] = 'W';
        this.f9c[2][2] = 'U';
        this.f9c[2][3] = 'U';
        this.f9c[2][4] = 'X';
        this.f9c[3][0] = '\u062d';
        this.f9c[3][1] = '\\';
        this.f9c[3][2] = 'Y';
        this.f9c[3][3] = 'Y';
        this.f9c[3][4] = '`';
        this.f9c[4][0] = '\u062e';
        this.f9c[4][1] = 'a';
        this.f9c[4][2] = 'a';
        this.f9c[4][3] = 'a';
        this.f9c[4][4] = 'd';
        this.f9c[5][0] = '\u0647';
        this.f9c[5][1] = '\u00aa';
        this.f9c[5][2] = '\u00a8';
        this.f9c[5][3] = '\u00a9';
        this.f9c[5][4] = '\u00ab';
        this.f9c[6][0] = '\u0639';
        this.f9c[6][1] = '\u008a';
        this.f9c[6][2] = '\u0088';
        this.f9c[6][3] = '\u0089';
        this.f9c[6][4] = '\u008b';
        this.f9c[7][0] = '\u063a';
        this.f9c[7][1] = '\u008e';
        this.f9c[7][2] = '\u008c';
        this.f9c[7][3] = '\u008d';
        this.f9c[7][4] = '\u008f';
        this.f9c[8][0] = '\u0641';
        this.f9c[8][1] = '\u0092';
        this.f9c[8][2] = '\u0090';
        this.f9c[8][3] = '\u0091';
        this.f9c[8][4] = '\u0093';
        this.f9c[9][0] = '\u0642';
        this.f9c[9][1] = '\u0096';
        this.f9c[9][2] = '\u0094';
        this.f9c[9][3] = '\u0095';
        this.f9c[9][4] = '\u0097';
        this.f9c[10][0] = '\u062b';
        this.f9c[10][1] = 'S';
        this.f9c[10][2] = 'Q';
        this.f9c[10][3] = 'R';
        this.f9c[10][4] = 'T';
        this.f9c[11][0] = '\u0635';
        this.f9c[11][1] = 'w';
        this.f9c[11][2] = 'u';
        this.f9c[11][3] = 'v';
        this.f9c[11][4] = 'x';
        this.f9c[12][0] = '\u0636';
        this.f9c[12][1] = '|';
        this.f9c[12][2] = 'y';
        this.f9c[12][3] = 'z';
        this.f9c[12][4] = '~';
        this.f9c[13][0] = '\u0637';
        this.f9c[13][1] = '\u0083';
        this.f9c[13][2] = '\u0081';
        this.f9c[13][3] = '\u0082';
        this.f9c[13][4] = '\u0080';
        this.f9c[14][0] = '\u0643';
        this.f9c[14][1] = '\u009a';
        this.f9c[14][2] = '\u0098';
        this.f9c[14][3] = '\u0099';
        this.f9c[14][4] = '\u009b';
        this.f9c[15][0] = '\u0645';
        this.f9c[15][1] = '\u00a2';
        this.f9c[15][2] = '\u00a1';
        this.f9c[15][3] = '\u00a1';
        this.f9c[15][4] = '\u00a3';
        this.f9c[16][0] = '\u0646';
        this.f9c[16][1] = '\u00a6';
        this.f9c[16][2] = '\u00a4';
        this.f9c[16][3] = '\u00a5';
        this.f9c[16][4] = '\u00a7';
        this.f9c[17][0] = '\u062a';
        this.f9c[17][1] = 'O';
        this.f9c[17][2] = 'M';
        this.f9c[17][3] = 'N';
        this.f9c[17][4] = 'P';
        this.f9c[18][0] = '\u0627';
        this.f9c[18][1] = 'B';
        this.f9c[18][2] = 'A';
        this.f9c[18][3] = 'B';
        this.f9c[18][4] = 'A';
        this.f9c[19][0] = '\u0644';
        this.f9c[19][1] = '\u009e';
        this.f9c[19][2] = '\u009c';
        this.f9c[19][3] = '\u009d';
        this.f9c[19][4] = '\u009f';
        this.f9c[20][0] = '\u0628';
        this.f9c[20][1] = 'K';
        this.f9c[20][2] = 'I';
        this.f9c[20][3] = 'J';
        this.f9c[20][4] = 'L';
        this.f9c[21][0] = '\u064a';
        this.f9c[21][1] = '\u00b0';
        this.f9c[21][2] = '\u00ae';
        this.f9c[21][3] = '\u00af';
        this.f9c[21][4] = '\u00b1';
        this.f9c[22][0] = '\u0633';
        this.f9c[22][1] = 'o';
        this.f9c[22][2] = 'm';
        this.f9c[22][3] = 'n';
        this.f9c[22][4] = 'p';
        this.f9c[23][0] = '\u0634';
        this.f9c[23][1] = 's';
        this.f9c[23][2] = 'q';
        this.f9c[23][3] = 'r';
        this.f9c[23][4] = 't';
        this.f9c[24][0] = '\u0638';
        this.f9c[24][1] = '\u0085';
        this.f9c[24][2] = '\u0087';
        this.f9c[24][3] = '\u0086';
        this.f9c[24][4] = '\u0084';
        this.f9c[25][0] = '\u0632';
        this.f9c[25][1] = 'l';
        this.f9c[25][2] = 'k';
        this.f9c[25][3] = 'l';
        this.f9c[25][4] = 'k';
        this.f9c[26][0] = '\u0648';
        this.f9c[26][1] = '\u00ad';
        this.f9c[26][2] = '\u00ac';
        this.f9c[26][3] = '\u00ad';
        this.f9c[26][4] = '\u00ac';
        this.f9c[27][0] = '\u0629';
        this.f9c[27][1] = '\u00b3';
        this.f9c[27][2] = '\u00b2';
        this.f9c[27][3] = '\u00b2';
        this.f9c[27][4] = '\u00b2';
        this.f9c[28][0] = '\u0649';
        this.f9c[28][1] = '\u00b4';
        this.f9c[28][2] = '\u00b5';
        this.f9c[28][3] = '\u00b4';
        this.f9c[28][4] = '\u00b5';
        this.f9c[29][0] = '\u0631';
        this.f9c[29][1] = 'j';
        this.f9c[29][2] = 'i';
        this.f9c[29][3] = 'j';
        this.f9c[29][4] = 'i';
        this.f9c[30][0] = '\u0624';
        this.f9c[30][1] = '\u00bc';
        this.f9c[30][2] = '\u00bb';
        this.f9c[30][3] = '\u00bc';
        this.f9c[30][4] = '\u00bb';
        this.f9c[31][0] = '\u0621';
        this.f9c[31][1] = '\u0089';
        this.f9c[31][2] = '\u0088';
        this.f9c[31][3] = '\u0089';
        this.f9c[31][4] = '\u0088';
        this.f9c[32][0] = '\u0626';
        this.f9c[32][1] = '\u00b9';
        this.f9c[32][2] = '\u00b7';
        this.f9c[32][3] = '\u00b8';
        this.f9c[32][4] = '\u00ba';
        this.f9c[33][0] = '\u0623';
        this.f9c[33][1] = 'D';
        this.f9c[33][2] = 'C';
        this.f9c[33][3] = 'D';
        this.f9c[33][4] = 'C';
        this.f9c[34][0] = '\u0622';
        this.f9c[34][1] = 'F';
        this.f9c[34][2] = 'E';
        this.f9c[34][3] = 'F';
        this.f9c[34][4] = 'E';
        this.f9c[35][0] = '\u0625';
        this.f9c[35][1] = 'H';
        this.f9c[35][2] = 'G';
        this.f9c[35][3] = 'H';
        this.f9c[35][4] = 'G';
        this.f9c[36][0] = '\ufefb';
        this.f9c[36][1] = '\u00be';
        this.f9c[36][2] = '\u00bd';
        this.f9c[36][3] = '\u00be';
        this.f9c[36][4] = '\u00bd';
        this.f9c[37][0] = '\ufef9';
        this.f9c[37][1] = '\u00c4';
        this.f9c[37][2] = '\u00c3';
        this.f9c[37][3] = '\u00c4';
        this.f9c[37][4] = '\u00c3';
        this.f9c[38][0] = '\ufef7';
        this.f9c[38][1] = '\u00c1';
        this.f9c[38][2] = '\u00c2';
        this.f9c[38][3] = '\u00c1';
        this.f9c[38][4] = '\u00c2';
        this.f9c[39][0] = '\ufef5';
        this.f9c[39][1] = '\u00c0';
        this.f9c[39][2] = '\u00bf';
        this.f9c[39][3] = '\u00c0';
        this.f9c[39][4] = '\u00bf';
        this.f9c[40][0] = '\u064b';
        this.f9c[40][1] = '=';
        this.f9c[40][2] = '=';
        this.f9c[40][3] = '=';
        this.f9c[40][4] = '=';
        String str2 = "";
        int i = 0;
        while (i < str.length()) {
            if (str.charAt(i) != '\u0644') {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.charAt(i)).toString();
            } else if (i == str.length() - 1) {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.charAt(i)).toString();
            } else if (str.charAt(i + 1) == '\u0627') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufefb').toString();
                i++;
            } else if (str.charAt(i + 1) == '\u0625') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufef9').toString();
                i++;
            } else if (str.charAt(i + 1) == '\u0622') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufef5').toString();
                i++;
            } else if (str.charAt(i + 1) == '\u0623') {
                str2 = new StringBuilder(String.valueOf(str2)).append('\ufef7').toString();
                i++;
            } else {
                str2 = new StringBuilder(String.valueOf(str2)).append(str.charAt(i)).toString();
            }
            i++;
        }
        byte[] bArr = new byte[str2.length()];
        i = 0;
        while (i < str2.length()) {
            Object obj;
            int i2;
            Object obj2 = null;
            int i3 = -1;
            for (int i4 = 0; i4 < f7a; i4++) {
                for (int i5 = 0; i5 < f8b; i5++) {
                    if (this.f9c[i4][i5] == str2.charAt(i)) {
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
                    obj = (m19a(str2.charAt(i + 1)) || m22b(str2.charAt(i + 1))) ? 1 : null;
                    obj3 = obj;
                }
                boolean a = i == 0 ? false : m19a(str2.charAt(i - 1));
                if (a /*&& r3 != null*/) {
                    str2.replace(str2.charAt(i), this.f9c[i2][3]);
                    bArr[i] = (byte) this.f9c[i2][3];
                }
                if (a /*&& r3 == null*/) {
                    str2.replace(str2.charAt(i), this.f9c[i2][1]);
                    bArr[i] = (byte) this.f9c[i2][1];
                }
                if (!(a /*|| r3 == null*/)) {
                    str2.replace(str2.charAt(i), this.f9c[i2][2]);
                    bArr[i] = (byte) this.f9c[i2][2];
                }
                if (!a /*&& r3 == null*/) {
                    str2.replace(str2.charAt(i), this.f9c[i2][4]);
                    bArr[i] = (byte) this.f9c[i2][4];
                }
            } else {
                bArr[i] = (byte) str2.charAt(i);
            }
            i++;
        }
        return m17b(bArr, z);
    }

    public byte[] GetBytes(String str) {
        char[] toCharArray = str.toCharArray();
        byte[] bArr = new byte[toCharArray.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) toCharArray[i];
        }
        return bArr;
    }

    boolean m18a(byte b) {
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

    boolean m19a(char c) {
        char[] cArr = new char[]{'\u062c', '\u062d', '\u062e', '\u0647', '\u0639', '\u063a', '\u0641', '\u0642', '\u062b', '\u0635', '\u0636', '\u0637', '\u0643', '\u0645', '\u0646', '\u062a', '\u0644', '\u0628', '\u064a', '\u0633', '\u0634', '\u0638', '\u0626'};
        for (int i = 0; i < 23; i++) {
            if (c == cArr[i]) {
                return true;
            }
        }
        return false;
    }

    byte[] m20a(byte[] bArr, boolean z) {
        int i = 0;
        while (i < bArr.length) {
            if (m18a(bArr[i])) {
                if (bArr[i] == (byte) 48 || bArr[i] == 96) {
                    if (z) {
                        bArr[i] = (byte) 48;
                    } else {
                        bArr[i] = (byte) -48;
                    }
                } else if (bArr[i] == (byte) 49 || bArr[i] == 97) {
                    if (z) {
                        bArr[i] = (byte) 49;
                    } else {
                        bArr[i] = (byte) -47;
                    }
                } else if (bArr[i] == (byte) 50 || bArr[i] == 98) {
                    if (z) {
                        bArr[i] = (byte) 50;
                    } else {
                        bArr[i] = (byte) -46;
                    }
                } else if (bArr[i] == (byte) 51 || bArr[i] == 99) {
                    if (z) {
                        bArr[i] = (byte) 51;
                    } else {
                        bArr[i] = (byte) -45;
                    }
                } else if (bArr[i] == (byte) 52 || bArr[i] == 100) {
                    if (z) {
                        bArr[i] = (byte) 52;
                    } else {
                        bArr[i] = (byte) -44;
                    }
                } else if (bArr[i] == 53 || bArr[i] == 101) {
                    if (z) {
                        bArr[i] = (byte) 53;
                    } else {
                        bArr[i] = (byte) -43;
                    }
                } else if (bArr[i] == 54 || bArr[i] == 102) {
                    if (z) {
                        bArr[i] = (byte) 54;
                    } else {
                        bArr[i] = (byte) -42;
                    }
                } else if (bArr[i] == 55 || bArr[i] == 103) {
                    if (z) {
                        bArr[i] = (byte) -73;
                    } else {
                        bArr[i] = (byte) 55;
                    }
                } else if (bArr[i] == 56 || bArr[i] == 104) {
                    if (z) {
                        bArr[i] = (byte) 56;
                    } else {
                        bArr[i] = (byte) -40;
                    }
                } else if (bArr[i] == 57 || bArr[i] == 105) {
                    if (z) {
                        bArr[i] = (byte) 57;
                    } else {
                        bArr[i] = (byte) -39;
                    }
                }
            }
            i++;
        }
        return bArr;
    }

    boolean m21b(byte b) {
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

    boolean m22b(char c) {
        char[] cArr = new char[]{'\u0627', '\u0623', '\u0625', '\u0622', '\u062f', '\u0630', '\u0631', '\u0632', '\u0648', '\u0624', '\u0629', '\u0649', '\u0626', '\ufefb', '\ufef9', '\ufef7', '\ufef5'};
        for (int i = 0; i < 17; i++) {
            if (c == cArr[i]) {
                return true;
            }
        }
        return false;
    }

    boolean m23c(char c) {
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

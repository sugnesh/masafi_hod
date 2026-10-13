package com.engear.vansales.printer;
/**
 * Created by Rakshit on 06-Feb-17.
 */
public class LinePrinter {
    private static final int JAVA_PLATFORM_ANDROID = 1;
    private static final int JAVA_PLATFORM_ME = 2;
    private static final int JAVA_PLATFORM_NOT_SET = 0;
    private ILinePrinterProxy m_LPProxy;
    private int m_iJavaPlatformType;

    public static class ExtraSettings {
        private Object m_Context;

        public ExtraSettings() {
            this.m_Context = null;
        }

        public Object getContext() {
            return this.m_Context;
        }

        public void setContext(Object obj) {
            this.m_Context = obj;
        }
    }

    public class GraphicRotationDegrees {
        public static final int DEGREE_0 = 0;
        public static final int DEGREE_180 = 180;
        public static final int DEGREE_270 = 270;
        public static final int DEGREE_90 = 90;
        private final LinePrinter this$0;

        private GraphicRotationDegrees(LinePrinter linePrinter) {
            this.this$0 = linePrinter;
        }
    }

    public LinePrinter(String str, String str2, String str3, ExtraSettings extraSettings) throws LinePrinterException {
        this.m_LPProxy = null;
        this.m_iJavaPlatformType = 0;
        getJavaPlatformType();
        if (this.m_iJavaPlatformType == JAVA_PLATFORM_ANDROID) {
            if (extraSettings == null) {
               // throw new LinePrinterException("extraSettings parameter is required on Android", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
            } else if (str == null) {
                //throw new LinePrinterException("null commands and attributes file path", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
            } else {
                this.m_LPProxy = getLinePrinterProxy(this.m_iJavaPlatformType);
            }
        }
        if (this.m_LPProxy != null) {
           // this.m_LPProxy.init(this, str, str2, str3, extraSettings);
        }
    }

    private int getJavaPlatformType() {
        if (this.m_iJavaPlatformType == 0) {
            Class cls = null;
            try {
                cls = Class.forName("android.app.Application");
            } catch (ClassNotFoundException e) {
            }
            if (cls != null) {
                this.m_iJavaPlatformType = JAVA_PLATFORM_ANDROID;
            } else {
                this.m_iJavaPlatformType = JAVA_PLATFORM_ME;
            }
        }
        return this.m_iJavaPlatformType;
    }

    private ILinePrinterProxy getLinePrinterProxy(int i) throws LinePrinterException {
        if (i != JAVA_PLATFORM_ANDROID) {
            return null;
        }
        try {
            Object newInstance = Class.forName("com.intermec.print.lp.android.AndroidLinePrinter").getConstructor(new Class[0]).newInstance(new Class[0]);
            if (newInstance instanceof ILinePrinterProxy) {
                return (ILinePrinterProxy) newInstance;
            }
            //throw new LinePrinterException("Not an instance of ILinePrinterProxy", ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        } catch (Exception e) {
            //throw new LinePrinterException(e.getMessage(), ILinePrinterProxy.ERROR_UNEXPECTED_EXCEPTION);
        }
        return null;
    }

    public void addPrintProgressListener(PrintProgressListener printProgressListener) {
        this.m_LPProxy.addPrintProgressListener(printProgressListener);
    }

    public void close() throws LinePrinterException {
        this.m_LPProxy.close();
    }

    public void connect() throws LinePrinterException {
        this.m_LPProxy.connect();
    }

    public boolean disconnect() throws LinePrinterException {
        return this.m_LPProxy.disconnect();
    }

    public void flush() throws LinePrinterException {
        this.m_LPProxy.flush();
    }

    public int getBytesWritten() throws LinePrinterException {
        return this.m_LPProxy.getBytesWritten();
    }

    public void newLine(int i) throws LinePrinterException {
        this.m_LPProxy.newLine(i);
    }

    public void removePrintProgressListener(PrintProgressListener printProgressListener) {
        this.m_LPProxy.removePrintProgressListener(printProgressListener);
    }

    public void sendCustomCommand(String str) throws LinePrinterException {
        if (str == null) {
            //throw new LinePrinterException("null command identifier", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        }
        this.m_LPProxy.sendCustomCommand(str);
    }

    public void setBold(boolean z) throws LinePrinterException {
        this.m_LPProxy.setBold(z);
    }

    public void setCompress(boolean z) throws LinePrinterException {
        this.m_LPProxy.setCompress(z);
    }

    public void setDoubleHigh(boolean z) throws LinePrinterException {
        this.m_LPProxy.setDoubleHigh(z);
    }

    public void setDoubleWide(boolean z) throws LinePrinterException {
        this.m_LPProxy.setDoubleWide(z);
    }

    public void setItalic(boolean z) throws LinePrinterException {
        this.m_LPProxy.setItalic(z);
    }

    public void setSettingBool(String str, boolean z) throws LinePrinterException {
        if (str == null || str.length() == 0) {
           // throw new LinePrinterException("Setting name cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        }
        this.m_LPProxy.setSettingBool(str, z);
    }

    public void setSettingBytes(String str, byte[] bArr) throws LinePrinterException {
        if (str == null || str.length() == 0) {
           // throw new LinePrinterException("Setting name cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        } else if (bArr == null || bArr.length == 0) {
           // throw new LinePrinterException("Byte array setting value cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        } else {
            this.m_LPProxy.setSettingBytes(str, bArr);
        }
    }

    public void setSettingNum(String str, int i) throws LinePrinterException {
        if (str == null || str.length() == 0) {
          //  throw new LinePrinterException("Setting name cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        }
        this.m_LPProxy.setSettingNum(str, i);
    }

    public void setSettingString(String str, String str2) throws LinePrinterException {
        if (str == null || str.length() == 0) {
           // throw new LinePrinterException("Setting name cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        } else if (str2 == null || str2.length() == 0) {
           // throw new LinePrinterException("String setting value cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        } else {
            this.m_LPProxy.setSettingString(str, str2);
        }
    }

    public void setStrikeout(boolean z) throws LinePrinterException {
        this.m_LPProxy.setStrikeout(z);
    }

    public void setUnderline(boolean z) throws LinePrinterException {
        this.m_LPProxy.setUnderline(z);
    }

    public void write(String str) throws LinePrinterException {
        if (str == null || str.length() == 0) {
           // throw new LinePrinterException("String to write cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        }
        this.m_LPProxy.write(str);
    }

    public void write(byte[] bArr) throws LinePrinterException {
        if (bArr == null || bArr.length == 0) {
           // throw new LinePrinterException("Byte buffer to write cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        }
        this.m_LPProxy.write(bArr);
    }

    public void writeGraphic(String str, int i, int i2, int i3, int i4) throws LinePrinterException {
        if (str == null || str.length() == 0) {
           // throw new LinePrinterException("Graphic file path cannot be null or empty.", ILinePrinterProxy.ERROR_INVALID_PARAMETER);
        }
        this.m_LPProxy.writeGraphic(str, i, i2, i3, i4);
    }

    public void writeLine(String str) throws LinePrinterException {
        if (str != null) {
            this.m_LPProxy.writeLine(str);
        } else {
            this.m_LPProxy.newLine(JAVA_PLATFORM_ANDROID);
        }
    }

}

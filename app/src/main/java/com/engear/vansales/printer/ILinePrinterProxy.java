package com.engear.vansales.printer;
/**
 * Created by Rakshit on 06-Feb-17.
 */



public interface ILinePrinterProxy {

    public static final int ERROR_ALREADY_CLOSED  = LinePrinterException.formatError(1312, 2309);
    public static final int ERROR_ALREADY_CONNECTED = LinePrinterException.formatError(2402, 2309);;
    public static final int ERROR_INVALID_CONTEXT  = LinePrinterException.formatError(14080, 2309);
    public static final int ERROR_INVALID_PARAMETER = LinePrinterException.formatError(87, 2309);
    public static final int ERROR_INVALID_THREAD = LinePrinterException.formatError(14081, 2309);
    public static final int ERROR_NO_CONNECTION = LinePrinterException.formatError(1229, 2309);
    public static final int ERROR_SERVICE_NOT_AVAILABLE  = LinePrinterException.formatError(1060, 2309);
    public static final int ERROR_UNEXPECTED_EXCEPTION = LinePrinterException.formatError(GameRequest.TYPE_ALL, 2309);

    public static class FacilityCodes {
        static final int ANDROID_C_LIB = 2306;
        static final int CROSS_PLATFORM_C_LIB = 2305;
        static final int LINEPRINT_SERVICE_JAVA = 2307;
        static final int LINEPRINT_SERVICE_JNI = 2308;
        static final int LINE_PRINTER_JAVA = 2309;
    }

    boolean abort();

    void addFatalErrorListener(FatalErrorListener fatalErrorListener);

    void addFooterListener(FooterListener footerListener);

    void addHeaderListener(HeaderListener headerListener);

    void addPrintProgressListener(PrintProgressListener printProgressListener);

    void addRecoverableErrorListener(RecoverableErrorListener recoverableErrorListener);

    void addWarningListener(WarningListener warningListener);

    void close() throws LinePrinterException;

    void connect() throws LinePrinterException;

    void connect(String str);

    boolean disconnect() throws LinePrinterException;

    void ensureLinesFitBottom(int i);

    void ensureNoOrphanLines(int i);

    void flush() throws LinePrinterException;

    boolean formFeed();

    int getBytesWritten() throws LinePrinterException;

    String getDebugEchoFilePath();

    int getNumLinesFooter();

    int getNumLinesHeader();

    int getPageLen();

    int getPageNumber();

    void init(LinePrinter linePrinter, String str, String str2, String str3, LinePrinter.ExtraSettings extraSettings) throws LinePrinterException;

    boolean isBold();

    boolean isBoldDefined();

    boolean isCompress();

    boolean isCompressDefined();

    boolean isConnected();

    boolean isDoubleHigh();

    boolean isDoubleHighDefined();

    boolean isDoubleWide();

    boolean isDoubleWideDefined();

    boolean isItalic();

    boolean isItalicDefined();

    boolean isStrikeout();

    boolean isStrikeoutDefined();

    boolean isUnderline();

    boolean isUnderlineDefined();

    void newLine(int i) throws LinePrinterException;

    void pageBreak();

    void removeFatalErrorListener(FatalErrorListener fatalErrorListener);

    void removeFooterListener(FooterListener footerListener);

    void removeHeaderListener(HeaderListener headerListener);

    void removePrintProgressListener(PrintProgressListener printProgressListener);

    void removeRecoverableErrorListener(RecoverableErrorListener recoverableErrorListener);

    void removeWarningListener(WarningListener warningListener);

    void sendCustomCommand(String str) throws LinePrinterException;

    void setBold(boolean z) throws LinePrinterException;

    void setCompress(boolean z) throws LinePrinterException;

    void setDoubleHigh(boolean z) throws LinePrinterException;

    void setDoubleWide(boolean z) throws LinePrinterException;

    void setItalic(boolean z) throws LinePrinterException;

    void setSettingBool(String str, boolean z) throws LinePrinterException;

    void setSettingBytes(String str, byte[] bArr) throws LinePrinterException;

    void setSettingNum(String str, int i) throws LinePrinterException;

    void setSettingString(String str, String str2) throws LinePrinterException;

    void setStrikeout(boolean z) throws LinePrinterException;

    void setUnderline(boolean z) throws LinePrinterException;

    void write(String str) throws LinePrinterException;

    void write(byte[] bArr) throws LinePrinterException;

    void writeFile(String str);

    void writeGraphic(String str, int i, int i2, int i3, int i4) throws LinePrinterException;

    void writeLine(String str) throws LinePrinterException;
}

package com.engear.vansales.printer;

import androidx.core.internal.view.SupportMenu;
import androidx.customview.widget.ExploreByTouchHelper;

/**
 * Created by Rakshit on 06-Feb-17.
 */
public class LinePrinterException extends Exception {
    public static final int ERROR_ALREADY_CONNECTED;
    public static final int ERROR_FILE_INVALID = -1996422162;
    public static final int ERROR_FILE_NOT_FOUND = -1996423166;
    public static final int ERROR_INVALID_NET_ADDRESS = -1996421954;
    public static final int ERROR_INVALID_PRINTER_ID = -1996421367;
    public static final int ERROR_NO_CONNECTION;
    static final long serialVersionUID = 1;
    private int m_iErrorCode;

    static {
        ERROR_NO_CONNECTION = ILinePrinterProxy.ERROR_NO_CONNECTION;
        ERROR_ALREADY_CONNECTED = ILinePrinterProxy.ERROR_ALREADY_CONNECTED;
    }

    public LinePrinterException(String str, int i) {
        super(str);
        this.m_iErrorCode = ERROR_NO_CONNECTION;
        this.m_iErrorCode = i;
    }

    static int formatError(int i, int i2) {
        return (i == 0 || (SupportMenu.CATEGORY_MASK & i) != 0) ? ERROR_NO_CONNECTION : (ExploreByTouchHelper.INVALID_ID | (i2 << 16)) | i;
    }

    public int getErrorCode() {
        return this.m_iErrorCode;
    }
}


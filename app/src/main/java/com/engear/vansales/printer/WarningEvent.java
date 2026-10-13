package com.engear.vansales.printer;
import java.util.EventObject;

/**
 * Created by Rakshit on 06-Feb-17.
 */
public class WarningEvent extends EventObject {
    static final long serialVersionUID = 1;
    private int m_iErrorCode;

    public WarningEvent(LinePrinter linePrinter, int i) {
        super(linePrinter);
        this.m_iErrorCode = 0;
    }

    public int getErrorCode() {
        return this.m_iErrorCode;
    }
}

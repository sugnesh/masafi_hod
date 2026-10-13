package com.engear.vansales.printer;
import java.util.EventObject;

/**
 * Created by Rakshit on 06-Feb-17.
 */
public class PrintProgressEvent extends EventObject {
    static final long serialVersionUID = 1;
    private int m_iMsgType;

    public class MessageTypes {
        public static final int CANCEL = 6;
        public static final int COMPLETE = 8;
        public static final int ENDDOC = 4;
        public static final int FINISHED = 7;
        public static final int NONE = 0;
        public static final int STARTDOC = 1;
        private final PrintProgressEvent this$0;

        private MessageTypes(PrintProgressEvent printProgressEvent) {
            this.this$0 = printProgressEvent;
        }
    }

    public PrintProgressEvent(LinePrinter linePrinter, int i) {
        super(linePrinter);
        this.m_iMsgType = 0;
        this.m_iMsgType = i;
    }

    public int getMessageType() {
        return this.m_iMsgType;
    }
}


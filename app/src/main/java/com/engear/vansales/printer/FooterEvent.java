package com.engear.vansales.printer;
import java.util.EventObject;

/**
 * Created by Rakshit on 06-Feb-17.
 */
public class FooterEvent extends EventObject {
    static final long serialVersionUID = 1;

    public FooterEvent(LinePrinter linePrinter) {
        super(linePrinter);
    }
}

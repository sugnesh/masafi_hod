package com.engear.vansales.printer;
import java.util.EventListener;

/**
 * Created by Rakshit on 06-Feb-17.
 */
public interface PrintProgressListener extends EventListener {
    void receivedStatus(PrintProgressEvent printProgressEvent);
}


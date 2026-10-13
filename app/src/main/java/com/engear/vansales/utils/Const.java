package com.engear.vansales.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.HashMap;

/**
 * Created by eheuristic on 12/2/2016.
 */

public class Const {

    public static int loadNumber=1;
    public static String creditLimitDays="0";

    public static HashMap<String, String> AllLoad = new HashMap<>();

    public static String availableLimit = "";
    public static String NumbertoWord = "";

    public static int TotalAPICOUNT = 12;
    public static String APICOUNT = TotalAPICOUNT+"/"+TotalAPICOUNT;


    public static int customerPosition;
    public static String paySource;
    public static String payReference;
    public static boolean isPostAudit = false;
    public static boolean isDisplayVatTax = true;


    public static int id=-1;

    public static Bundle grBundle = null;
    public static Bundle brBundle = null;
    public static Bundle siBundle = null;
    public static Bundle focBundle = null;

    public static boolean isGPSEnable(Context context) {
        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

 }

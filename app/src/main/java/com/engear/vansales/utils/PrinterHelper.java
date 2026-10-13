package com.engear.vansales.utils;
/**
 * Created by Rakshit on 03-Feb-17.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.customview.widget.ExploreByTouchHelper;

import com.engear.vansales.activities.CollectionActivity;
import com.engear.vansales.activities.DataPostingActivity;
import com.engear.vansales.activities.FreshUnloadActivity;
import com.engear.vansales.activities.LoadConfirmActivity;
import com.engear.vansales.activities.OrderConfirmActivity;
import com.engear.vansales.activities.PaymentDetailActivity;
import com.engear.vansales.common.AppConstants;
import com.engear.vansales.common.MyApplication;
import com.engear.vansales.model.DevicesData;
import com.engear.vansales.printer.Arabic6822;
import com.engear.vansales.printer.JsonRpcUtil;
import com.engear.vansales.printer.LinePrinterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class PrinterHelper {
    private static final UUID MY_UUID;
    byte[] BoldOff;
    byte[] BoldOn;
    byte[] CarriageReturn;
    byte[] CompressOff;
    byte[] CompressOn;
    byte[] DoubleHighOff;
    byte[] DoubleHighOn;
    byte[] DoubleWideOff;
    byte[] DoubleWideOn;
    byte[] NewLine;
    byte[] wakeUp;
    private android.app.ProgressDialog ProgressDialog;
    byte[] UnderlineOff;
    byte[] UnderlineOn;
    ArrayAdapter<String> adapter;
    private ArrayList<DevicesData> arrData = new ArrayList<>();
    ArrayList<BluetoothDevice> arrayListBluetoothDevices;
    BluetoothSocket btSocket;
    private String callbackId;
    int cnln;
    private int count;
    ArrayAdapter<String> detectedAdapter;
    String devicename;
    int endln;
    private HashMap<String, String> hashArabVales;
    private HashMap<String, Integer> hashArbPositions;
    private HashMap<String, Integer> hashPositions;
    private HashMap<String, Integer> hashValues;
    private boolean isEnglish;
    private boolean isExceptionThrown;
    boolean isTwice;
    private JSONArray jArr;
    private BluetoothAdapter mBtAdapter;
    private final BroadcastReceiver mPairReceiver;
    private final BroadcastReceiver mReceiverRequiresPin;
    private BroadcastReceiver myReceiver;
    private NotificationManager notificationManager;
    OutputStream outStream;
    private android.app.ProgressDialog progressDialog;
    byte[] resetprinter;
    String resolution;
    private int retryCount;
    private String sMacAddr;
    int startln;
    private JSONObject status;
    String strFormat;
    String strFormatBold;
    String strFormatHeader;
    String strFormatTitle;
    String strPrintLeftBold;
    String strUnderLine;
    Context context;
    private Activity activity;

    boolean isFirstInvoice = true;

    public PrinterHelper(Context context, Activity activity) {
        this.isTwice = false;
        this.resolution = "";
        this.callbackId = "";
        this.arrayListBluetoothDevices = null;
        this.isExceptionThrown = false;
        this.btSocket = null;
        this.outStream = null;
        this.devicename = "";
        this.startln = 4;
        this.endln = 12;
        this.cnln = 16;
        this.isEnglish = true;
        this.sMacAddr = "";
        this.BoldOn = new byte[]{(byte) 27, (byte) 71};
        this.BoldOff = new byte[]{(byte) 27, (byte) 72};
        this.UnderlineOn = new byte[]{(byte) 27, (byte) 45, (byte) 1};
        byte[] bArr = new byte[3];
        bArr[0] = (byte) 27;
        bArr[1] = (byte) 45;
        this.UnderlineOff = bArr;
        this.CompressOn = new byte[]{(byte) 27, (byte) 33, (byte) 4};
        bArr = new byte[3];
        bArr[0] = (byte) 27;
        bArr[1] = (byte) 33;
        this.CompressOff = bArr;
        this.NewLine = new byte[]{(byte) 13, (byte) 10};
        this.DoubleHighOn = new byte[]{(byte) 27, (byte) 33, (byte) 16};
        this.DoubleHighOff = new byte[]{(byte) 27, (byte) 33, (byte) 16};
        this.DoubleWideOn = new byte[]{(byte) 27, (byte) 33, (byte) 32};
        bArr = new byte[3];
        bArr[0] = (byte) 27;
        bArr[1] = (byte) 33;
        this.DoubleWideOff = bArr;
        this.resetprinter = new byte[]{(byte) 27, (byte) 64};
        this.CarriageReturn = new byte[]{(byte) 13, (byte) 10};
        this.wakeUp = new byte[]{(byte) (Integer.parseInt("1B", 16) & 0xff), (byte) (Integer.parseInt("40", 16) & 0xff)};
        this.retryCount = 0;
        this.count = 0;
        this.myReceiver = new C04641();
        this.mReceiverRequiresPin = new C04652();
        this.notificationManager = null;
        this.mPairReceiver = new C04663();
        this.context = context;
        this.activity = activity;
    }

    public void execute(String request, JSONArray jsonArray) {
        try {
            this.jArr = jsonArray;
            this.arrData = new ArrayList();
            print();

//            AsyncTask.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        try {
//                            Long tsLong = System.currentTimeMillis() / 1000;
//                            String ts = tsLong.toString();
//                            String fileName = "test_" + ts + ".txt";
//                            File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
//                            outStream = new FileOutputStream(file);
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        }
//                        PrinterHelper.this.printReports(PrinterHelper.this.sMacAddr);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class C04641 extends BroadcastReceiver {
        C04641() {
        }

        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            try {
                if ("android.bluetooth.device.action.FOUND".equals(intent.getAction())) {
                    Toast.makeText(context, "ACTION_FOUND", 0).show();
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                    if (PrinterHelper.this.arrayListBluetoothDevices.size() < 1) {
                        PrinterHelper.this.detectedAdapter.add(device.getName() + StringUtilities.LF + device.getAddress());
                        PrinterHelper.this.arrayListBluetoothDevices.add(device);
                        PrinterHelper.this.detectedAdapter.notifyDataSetChanged();
                        return;
                    }
                    boolean flag = true;
                    for (int i = 0; i < PrinterHelper.this.arrayListBluetoothDevices.size(); i++) {
                        if (device.getAddress().equals(((BluetoothDevice) PrinterHelper.this.arrayListBluetoothDevices.get(i)).getAddress())) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        PrinterHelper.this.detectedAdapter.add(device.getName() + StringUtilities.LF + device.getAddress());
                        PrinterHelper.this.arrayListBluetoothDevices.add(device);
                        PrinterHelper.this.detectedAdapter.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class C04652 extends BroadcastReceiver {
        C04652() {
        }

        public void onReceive(Context context, Intent intent) {
            try {
                BluetoothDevice newDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                Class<?> btDeviceInstance = Class.forName(BluetoothDevice.class.getCanonicalName());
                byte[] pin = (byte[]) btDeviceInstance.getMethod("convertPinToBytes", new Class[]{String.class}).invoke(newDevice, new Object[]{"1234"});
                Log.e("Success", "success" + ((Boolean) btDeviceInstance.getMethod("setPin", new Class[]{byte[].class}).invoke(newDevice, new Object[]{pin})).booleanValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class C04663 extends BroadcastReceiver {
        C04663() {
        }

        public void onReceive1(Context context, Intent intent) {
            try {
                if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(intent.getAction())) {
                    int state = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", ExploreByTouchHelper.INVALID_ID);
                    int prevState = intent.getIntExtra("android.bluetooth.device.extra.PREVIOUS_BOND_STATE", ExploreByTouchHelper.INVALID_ID);
                    if (state == 12 && prevState == 11) {
                        Toast.makeText(context, "Paired", Toast.LENGTH_SHORT).show();
                    } else if (state == 10 && prevState == 12) {
                        Toast.makeText(context, "Unpaired", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onReceive(Context arg0, Intent arg1) {
        }
    }

    class C04694 implements Runnable {
        private final /* synthetic */ ArrayList val$arrData;
        private final /* synthetic */ String[] val$arrDevices;

        /* renamed from: com.phonegap.sfa.DotmatHelper.4.1 */
        class C04671 implements OnClickListener {
            private final /* synthetic */ ArrayList val$arrData;

            C04671(ArrayList arrayList) {
                this.val$arrData = arrayList;
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    PrinterHelper.this.retryCount = 0;
                    PrinterHelper.this.sMacAddr = ((DevicesData) this.val$arrData.get(which)).getAddress();
                    if (!PrinterHelper.this.sMacAddr.contains(":") && PrinterHelper.this.sMacAddr.length() == 12) {
                        char[] cAddr = new char[17];
                        int j = 0;
                        for (int i = 0; i < 12; i += 2) {
                            PrinterHelper.this.sMacAddr.getChars(i, i + 2, cAddr, j);
                            j += 2;
                            if (j < 17) {
                                int j2 = j + 1;
                                cAddr[j] = ':';
                                j = j2;
                            }
                        }
                        PrinterHelper.this.sMacAddr = new String(cAddr);
                    }
                    PrinterHelper.this.showProgressDialog();
                    PrinterHelper.this.doConnectionTest(PrinterHelper.this.sMacAddr);
                } catch (Exception e) {
                   /* if (context instanceof PrintDocumentActivity) {
                        ((PrintDocumentActivity) context).callback("");
                    }
                    if (context instanceof PrintCustomerActivity) {
                        ((PrintCustomerActivity) context).callback("");
                    }
                    if (context instanceof PromotioninfoActivity) {
                        ((PromotioninfoActivity) context).callback(App.SALES_INVOICE);
                    }
                    if (context instanceof LoadRequestActivity) {
                        ((LoadRequestActivity) context).callback();
                    }
                    if (context instanceof PaymentDetails) {
                        ((PaymentDetails) context).callback();
                    }
                    if (context instanceof PrintCustomerActivity) {
                        ((PrintCustomerActivity) context).callback("");
                    }
                    if (context instanceof PreSaleOrderProceedActivity) {
                        ((PreSaleOrderProceedActivity) context).callback();
                    }*/
                    e.printStackTrace();
                }
                Toast.makeText(context, " you have selected " + ((DevicesData) this.val$arrData.get(which)).getName(), 1).show();
            }
        }

        /* renamed from: com.phonegap.sfa.DotmatHelper.4.2 */
        class C04682 implements OnClickListener {
            C04682() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    //   PrinterHelper.this.status.put("status", false);
                    //   PrinterHelper.this.status.put("isconnected", -1);

//                    if (context instanceof OrderConfirmActivity) {
//                        OrderConfirmActivity.Companion.callbackFunction("");
//                    }
//
//                    if (context instanceof PaymentDetailActivity) {
//                        PaymentDetailActivity.Companion.callbackFunction();
//                    }
//
//                    if (context instanceof CollectionActivity) {
//                        CollectionActivity.Companion.callbackFunction();
//                    }
//
//                    if (context instanceof LoadConfirmActivity) {
//                        LoadConfirmActivity.Companion.callbackFunction();
//                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
                PrinterHelper.this.sendUpdate(PrinterHelper.this.status, true);
            }
        }

        C04694(String[] strArr, ArrayList arrayList) {
            this.val$arrDevices = strArr;
            this.val$arrData = arrayList;
        }

        public void run() {
            Builder dialog = new Builder(context);
            dialog.setTitle("Choose Device To Pair");
            dialog.setItems(this.val$arrDevices, new C04671(this.val$arrData));
            // dialog.setPositiveButton("Cancel", new C04682());
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    class C04705 implements Runnable {
        private final /* synthetic */ String val$address;

        C04705(String str) {
            this.val$address = str;
        }

        public void run() {
            new ConnectTo().execute(new String[]{this.val$address});
        }
    }

    public class ConnectTo extends AsyncTask<String, Void, Boolean> {
        @SuppressLint({"NewApi"})
        protected Boolean doInBackground(String... address) {
            try {
                BluetoothDevice device = PrinterHelper.this.mBtAdapter.getRemoteDevice(address[0]);
                if (PrinterHelper.this.btSocket != null && PrinterHelper.this.btSocket.isConnected()) {
                    PrinterHelper.this.btSocket.close();
                    PrinterHelper.this.btSocket = null;
                }
                if (VERSION.SDK_INT < 19) {
                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{Integer.TYPE});
                    PrinterHelper.this.btSocket = (BluetoothSocket) m.invoke(device, new Object[]{Integer.valueOf(1)});
                } else {
                    PrinterHelper.this.btSocket = device.createInsecureRfcommSocketToServiceRecord(device.getUuids()[0].getUuid());
                }
                PrinterHelper.this.devicename = device.getName();
                try {
                    PrinterHelper.this.btSocket.connect();
                    PrinterHelper.this.outStream = PrinterHelper.this.btSocket.getOutputStream();
                    return Boolean.valueOf(true);
                } catch (IOException e) {

                    if (context instanceof OrderConfirmActivity) {
                        OrderConfirmActivity.Companion.callbackFunction("","");
                    }

                    if (context instanceof PaymentDetailActivity) {
                        PaymentDetailActivity.Companion.callbackFunction("");
                    }

                    if (context instanceof CollectionActivity) {
                        CollectionActivity.Companion.callbackFunction("");
                    }

                    if (context instanceof LoadConfirmActivity) {
                        LoadConfirmActivity.Companion.callbackFunction();
                    }

                    if (context instanceof FreshUnloadActivity) {
                        FreshUnloadActivity.Companion.callbackFunction();
                    }

                    e.printStackTrace();
                    return Boolean.valueOf(false);
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                return Boolean.valueOf(false);
            }
        }

        protected void onPostExecute(Boolean result) {
            if (result.booleanValue()) {
                PrinterHelper.this.dismissProgress();
                Log.e("Connected", "true");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PrinterHelper.this.printReports(PrinterHelper.this.sMacAddr);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return;
            }/*else{
                PrinterHelper.this.dismissProgress();
                try {
                    PrinterHelper.this.outStream = new ByteArrayOutputStream();
                    PrinterHelper.this.printReports("000");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }*/
            try {
                PrinterHelper.this.doConnectionTest(PrinterHelper.this.sMacAddr);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        protected void onPreExecute() {
        }
    }

    void callActivity() {
        if (context instanceof OrderConfirmActivity) {
            OrderConfirmActivity.Companion.callbackFunction("","");
        }

        if (context instanceof PaymentDetailActivity) {
            PaymentDetailActivity.Companion.callbackFunction("");
        }

        if (context instanceof CollectionActivity) {
            CollectionActivity.Companion.callbackFunction("");
        }

        if (context instanceof LoadConfirmActivity) {
            LoadConfirmActivity.Companion.callbackFunction();
        }

        if (context instanceof FreshUnloadActivity) {
            FreshUnloadActivity.Companion.callbackFunction();
        }
    }

    @SuppressLint({"NewApi"})
    void printReports(String address) throws JSONException {
        Log.e("Print Report", "" + this.jArr.toString());
        Log.e("Array Length", "" + this.jArr.length());

        callActivity();

        try {
            for (int j = 0; j < (this.jArr.length() > 1 ? 1 : this.jArr.length()); j++) {
                JSONArray jInner = this.jArr.getJSONArray(j);
                for (int i = 0; i < jInner.length(); i++) {
                    JSONObject jDict = jInner.getJSONObject(i);
                    String request = jDict.getString(MyApplication.REQUEST);
                    JSONObject jsnData = jDict.getJSONObject("mainArr");
                    this.count = 0;

                    if (request.equals(MyApplication.LOAD_SUMMARY_REQUEST)) {
                        parseLoadSummaryResponse(jsnData, address);
                    }
                    if (request.equals(AppConstants.SALES_INVOICE)) {
                        printSalesInvoice(jsnData, address);
                    }

                    if (request.equals(AppConstants.RETURN_REQUEST)) {
                        printCreditNot(jsnData, address);
                    }

                    if (request.equals(AppConstants.ORDER)) {
                        printOrderRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.LOAD_REQUEST)) {
                        printLoadRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.COLLECTION)) {
                        String type = jsnData.getString("Type");
                        if (type.equalsIgnoreCase("Single")) {
                            printSinglePaymentRequest(jsnData, address);
                        } else {
                            printMultiPaymentRequest(jsnData, address);
                        }
                    }

                    if (request.equals(AppConstants.SALES_SUMMARY)) {
                        printSalesSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DEPOSITE_SUMMARY)) {
                        printDepositesSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DAILY_OTHER_SUMMARY)) {
                        printDailyOtherSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DAILY_RTC_SUMMARY)) {
                        printDailyRTCSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.UNLOAD_DAMAGED_REQUEST)) {
                        printUnloadDamagedRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.UNLOAD_EXPIRY_REQUEST)) {
                        printUnloadExpiryRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.END_INVENTORY)) {
                        printUnloadEndInventryRequest(jsnData, address);
                    }
                    if (request.equals(AppConstants.VAN_STOCK)) {
                        parseVanSummaryResponse(jsnData, address);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            for (int j = 0; j < this.jArr.length(); j++) {
                JSONArray jInner = this.jArr.getJSONArray(j);
                JSONArray jInnerSub = jInner.getJSONArray(0);

                for (int i = 0; i < jInnerSub.length(); i++) {
                    JSONObject jDict = jInnerSub.getJSONObject(i);
                    String request = jDict.getString(MyApplication.REQUEST);
                    JSONObject jsnData = jDict.getJSONObject("mainArr");
                    this.count = 0;

                    if (request.equals(MyApplication.LOAD_SUMMARY_REQUEST)) {
                        parseLoadSummaryResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.SALES_INVOICE)) {
                        printSalesInvoice(jsnData, address);
                    }

                    if (request.equals(AppConstants.RETURN_REQUEST)) {
                        printCreditNot(jsnData, address);
                    }

                    if (request.equals(AppConstants.ORDER)) {
                        printOrderRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.LOAD_REQUEST)) {
                        printLoadRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.COLLECTION)) {
                        String type = jsnData.getString("Type");
                        if (type.equalsIgnoreCase("Single")) {
                            printSinglePaymentRequest(jsnData, address);
                        } else {
                            printMultiPaymentRequest(jsnData, address);
                        }
                    }

                    if (request.equals(AppConstants.SALES_SUMMARY)) {
                        printSalesSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DEPOSITE_SUMMARY)) {
                        printDepositesSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DAILY_OTHER_SUMMARY)) {
                        printDailyOtherSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DAILY_RTC_SUMMARY)) {
                        printDailyRTCSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.UNLOAD_DAMAGED_REQUEST)) {
                        printUnloadDamagedRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.UNLOAD_EXPIRY_REQUEST)) {
                        printUnloadExpiryRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.END_INVENTORY)) {
                        printUnloadEndInventryRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.VAN_STOCK)) {
                        parseVanSummaryResponse(jsnData, address);
                    }

                }
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.outStream != null) {
            try {
                Log.e("FullPrint", "" + this.outStream);
                this.outStream.flush();
                this.outStream.close();
            } catch (IOException e2222) {
                e2222.printStackTrace();
            }
        }
        if (this.btSocket != null && this.btSocket.isConnected()) {
            try {
                this.btSocket.close();
            } catch (IOException e22222) {
                e22222.printStackTrace();
            }
        }



       /* if (context instanceof PrintDocumentActivity) {
            ((PrintDocumentActivity) context).callback("");
        }
        if (context instanceof PrintCustomerActivity) {
            ((PrintCustomerActivity) context).callback("");
        }*/
    }

    void printVanStockReport() {
        try {
            int i;
            StringBuilder stringBuilder;
            this.hashValues = new HashMap();
            this.hashValues.put("Item#", Integer.valueOf(7));
            this.hashValues.put("Description", Integer.valueOf(37));
            this.hashValues.put("Loaded Qty", Integer.valueOf(8));
            this.hashValues.put("Transfer Qty", Integer.valueOf(0));
            this.hashValues.put("Sale Qty", Integer.valueOf(6));
            this.hashValues.put("Return Qty", Integer.valueOf(6));
            this.hashValues.put("Truck Stock", Integer.valueOf(7));
            this.hashValues.put("Total", Integer.valueOf(9));
            this.hashValues.put("Description", Integer.valueOf(37));
            this.hashPositions = new HashMap();
            this.hashPositions.put("Item#", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Loaded Qty", Integer.valueOf(2));
            this.hashPositions.put("Transfer Qty", Integer.valueOf(2));
            this.hashPositions.put("Sale Qty", Integer.valueOf(2));
            this.hashPositions.put("Return Qty", Integer.valueOf(2));
            this.hashPositions.put("Truck Stock", Integer.valueOf(2));
            this.hashPositions.put("Total", Integer.valueOf(1));
            this.hashPositions.put("Description", Integer.valueOf(0));
            line(this.startln);
            JSONObject object = new JSONObject();
            object.put("ROUTE", "1234");
            object.put("DOC DATE", "06-02-2017");
            object.put("TIME", "12:12:12");
            object.put("SALESMAN", "هذا هو الاختبار العربية");
            object.put("CONTACTNO", "01234567");
            object.put("supervisorname", "No Supervisor");
            object.put("supervisorno", "12389930");
            object.put("TourID", "1235");
            headervanstockprint(object, 104);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class asyncgetDevices extends AsyncTask<Void, Set<BluetoothDevice>, Set<BluetoothDevice>> {
        private asyncgetDevices() {
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected Set<BluetoothDevice> doInBackground(Void... params) {
            PrinterHelper.this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            PrinterHelper.this.mBtAdapter.enable();
            if (PrinterHelper.this.mBtAdapter.isEnabled()) {
                return PrinterHelper.this.mBtAdapter.getBondedDevices();
            }
            cancel(true);

            new asyncgetDevices().execute(new Void[0]);
            return null;
        }

        protected void onPostExecute(Set<BluetoothDevice> pairedDevices) {
            super.onPostExecute(pairedDevices);
            try {
                if (pairedDevices == null) {
                    return;
                }
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        Log.e("devices", device.getName() + StringUtilities.LF + device.getAddress());
                        System.out.println("devices" + device.getName() + StringUtilities.LF + device.getAddress());
                        DevicesData d1 = new DevicesData();
                        d1.setAddress(device.getAddress());
                        d1.setName(device.getName());
                        PrinterHelper.this.arrData.add(d1);
                    }
                    PrinterHelper.this.showDialog(PrinterHelper.this.arrData);
                    return;
                }
                Toast.makeText(context, "No Devices Found!", 0).show();
                System.out.println("No devices");
                Log.e("devices", "No devices");

                /*if (context instanceof PromotioninfoActivity) {
                    ((PromotioninfoActivity) context).callback(App.SALES_INVOICE);
                }*/

                try {
                    PrinterHelper.this.status.put("status", false);
                    PrinterHelper.this.status.put("isconnected", -7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PrinterHelper.this.sendUpdate(PrinterHelper.this.status, true);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static {
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    }

    public void print() {

        /*AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage("Print Option");
        dialog.setPositiveButton("Arebic", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {*/
        new asyncgetDevices().execute(new Void[0]);
        /*    }
        });
        dialog.setNegativeButton("English", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new asyncgetDevices().execute(new Void[0]);
            }
        });
        dialog.show();*/

    }

    public void showDialog(ArrayList<DevicesData> arrData) {
        String[] arrDevices = new String[arrData.size()];
        for (int i = 0; i < arrData.size(); i++) {
            arrDevices[i] = new StringBuilder(String.valueOf(((DevicesData) arrData.get(i)).getName())).append(StringUtilities.LF).append(((DevicesData) arrData.get(i)).getAddress()).toString();
        }
        activity.runOnUiThread(new C04694(arrDevices, arrData));
    }

    private void sendUpdate(JSONObject obj, boolean keepCallback) {
        if (this.callbackId != null) {
            Log.e("End of plugin", "true");
            // success(new PluginResult(Status.OK, obj), this.callbackId);
        }
    }

    public void onDestroy() {
        onDestroy();
        try {
            if (this.btSocket != null) {
                this.btSocket.close();
            }
            if (this.outStream != null) {
                this.outStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doConnectionTest(String address) throws JSONException {
        try {
            this.retryCount++;
            if (this.retryCount < 3) {
                Thread.sleep(200);
                activity.runOnUiThread(new C04705(address));
                return;
            }
            dismissProgress();
            try {
                this.status.put("status", false);
                sendUpdate(this.status, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
           /* if (context instanceof PrintDocumentActivity) {
                ((PrintDocumentActivity) context).callback("");
            }
            if (context instanceof PrintCustomerActivity) {
                ((PrintCustomerActivity) context).callback("");
            }
            if (context instanceof PromotioninfoActivity) {
                ((PromotioninfoActivity) context).callback(App.SALES_INVOICE);
            }
            if (context instanceof LoadRequestActivity) {
                ((LoadRequestActivity) context).callback();
            }
            if (context instanceof PaymentDetails) {
                ((PaymentDetails) context).callback();
            }
            if (context instanceof PrintCustomerActivity) {
                ((PrintCustomerActivity) context).callback("");
            }
            if (context instanceof PreSaleOrderProceedActivity) {
                ((PreSaleOrderProceedActivity) context).callback();
            }*/
            e2.printStackTrace();
        }
    }

    private void showProgressDialog() {
        try {
            this.progressDialog = ProgressDialog.show(context, "Please Wait", "Connecting to printer..", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissProgress() {
        try {
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String printSepratorcomp() {
        String seprator = "";
        for (int i = 0; i < 137; i++) {
            seprator = new StringBuilder(String.valueOf(seprator)).append("-").toString();
        }
        return seprator;
    }

    private String printSeprator() {
        String seprator = "";
        for (int i = 0; i < 80; i++) {
            seprator = new StringBuilder(String.valueOf(seprator)).append("-").toString();
        }
        return seprator;
    }

    private String printSepratorCompress() {
        String seprator = "";
        for (int i = 0; i < 137; i++) {
            seprator = new StringBuilder(String.valueOf(seprator)).append("-").toString();
        }
        return seprator;
    }

    private void printArabic(String data) {
        try {
            if (data.indexOf("@") == -1 || data.indexOf("!") == -1) {
                this.outStream.write(data.getBytes());
                return;
            }
            String start = data.substring(0, data.indexOf("@")).replaceAll("VATATVAT", "@");
            String middle = data.substring(data.indexOf("@") + 1, data.indexOf("!")).replaceAll("VATATVAT", "@");
            String end = data.substring(data.indexOf("!") + 1, data.length());
//            Log.e("start", start);
//            Log.e("middle", middle);
//            Log.e("end", end);
            Arabic6822 Arabic = new Arabic6822();
            byte[] printbyte = Arabic.Convert(middle, true);
            this.outStream.write(start.getBytes());
            this.outStream.write(printbyte);
            this.outStream.write("  ".getBytes());
            if (end.indexOf("@") == -1 || end.indexOf("!") == -1) {
                this.outStream.write(end.getBytes());
                return;
            }
            String startbet = end.substring(0, end.indexOf("@"));
            String middlebet = end.substring(end.indexOf("@") + 1, end.indexOf("!"));
            String endbet = end.substring(end.indexOf("!") + 1, end.length());
            byte[] printmidbyte = Arabic.Convert(middlebet, true);
            this.outStream.write(startbet.getBytes());
            this.outStream.write(printmidbyte);
            this.outStream.write("  ".getBytes());
            this.outStream.write(endbet.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printheaders(String line, boolean isArabic, int pluscount) {
        Log.e("PrintLine", "" + line);
        if (isArabic) {
            printArabic(line);
        } else {
            try {
                this.outStream.write(line.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.count += pluscount;
    }

    private void line(int ln) {
        for (int i = 0; i < ln; i++) {
            try {
                this.outStream.write(this.NewLine);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getAccurateText(String text, int width, int position) {
        String finalText = "";
        if (text.length() == width) {
//            Log.d("Matched String", text);
            return text;
        } else if (text.length() > width) {
            return text.substring(0, width);
        } else {
            finalText = text;
            int i;
            switch (position) {
                case 0:
                    for (i = 0; i < width - text.length(); i++) {
                        finalText = finalText.concat(" ");
                    }
                    return finalText;
                case 1:
                    for (i = 0; i < width - text.length(); i++) {
                        if (i < (width - text.length()) / 2) {
                            finalText = " " + finalText;
                        } else {
                            finalText = new StringBuilder(String.valueOf(finalText)).append(" ").toString();
                        }
                    }
                    return finalText;
                case 2:
                    for (i = 0; i < width - text.length(); i++) {
                        finalText = " " + finalText;
                    }
                    return finalText;
                default:
                    return finalText;
            }
        }
    }

    private void headervanstockprint(JSONObject object, int type) throws JSONException {
        try {
            this.outStream.write(this.BoldOn);
            printheaders(getAccurateText("ROUTE: " + object.getString("ROUTE"), 40, 0) + getAccurateText("DATE:" + object.getString("DOC DATE") + " " + object.getString("TIME") + " ", 40, 2), false, 1);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            printheaders(getAccurateText("SALESMAN: " + object.getString("SALESMAN") + "", 40, 0) + getAccurateText("SALESMAN NO: " + object.getString("SALESMANNO"), 40, 2), true, 1);
            this.outStream.write(this.NewLine);
            printheaders(getAccurateText("TRIP ID:" + object.getString("TourID"), 80, 0), false, 2);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.count++;
            if (type == 104) {
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("VAN STOCK SUMMARY ", 40, 1), false, 1);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
            } else if (type == 10) {
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("ITEM SALES SUMMARY ", 40, 1), false, 1);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
            } else if (type == 6) {
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("COMPANY CREDIT SUMMARY", 40, 1), false, 1);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
            } else if (type == 25) {
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("TEMPORARY CREDIT SUMMARY", 40, 1), false, 1);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
            }
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Parsing Data
    //Load Summary
    void parseLoadSummaryResponse(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;
            String string;
            int j;
            this.hashValues = new HashMap();
            this.hashValues.put("S.NO", Integer.valueOf(8));
            this.hashValues.put("ITEM#", Integer.valueOf(9));
            this.hashValues.put("DESCRIPTION", Integer.valueOf(48));
            this.hashValues.put("REQUESTED UOM", Integer.valueOf(16));
            this.hashValues.put("LOAD_REQUESTED QTY", Integer.valueOf(20));
            this.hashValues.put("RECEIVED UOM", Integer.valueOf(16));
            this.hashValues.put("LOAD_RECEIVED QTY", Integer.valueOf(20));

            this.hashPositions = new HashMap();
            this.hashPositions.put("S.NO", Integer.valueOf(0));
            this.hashPositions.put("ITEM#", Integer.valueOf(0));
            this.hashPositions.put("DESCRIPTION", Integer.valueOf(0));
            this.hashPositions.put("REQUESTED UOM", Integer.valueOf(1));
            this.hashPositions.put("LOAD_REQUESTED QTY", Integer.valueOf(1));
            this.hashPositions.put("RECEIVED UOM", Integer.valueOf(1));
            this.hashPositions.put("LOAD_RECEIVED QTY", Integer.valueOf(1));

            line(this.startln);

            Const.loadNumber = 1;
            headerprint(object, 101);  //Uncomment to print header

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            //int MAXLEngth = 137;
            //String strTotal = "";
            //JSONArray jTotal = object.getJSONArray("TOTAL");

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }
            //JSONObject jTOBject = jTotal.getJSONObject(0);
            for (i = 0; i < headers.length(); i++) {
                stringBuilder = new StringBuilder(String.valueOf(strheader));
                if (headers.getString(i).indexOf(" ") == -1) {
                    string = headers.getString(i);
                } else {
                    string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                }
                string = string.replaceAll("_", " ");
                strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();
                stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                if (headers.getString(i).indexOf(" ") == -1) {
                    string = "";
                } else {
                    string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length()).trim();
                }
                strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();
            }
            this.outStream.write(this.CompressOn);
            printlines1(strheader, 1, object, 1, args, 101);
            printlines1(strHeaderBottom, 1, object, 1, args, 101);
            printlines1(printSepratorcomp(), 1, object, 1, args, 101);
            this.outStream.write(this.CompressOff);
            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;
                    HashMap hashMap = this.hashPositions;
                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion, i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                //printlines1(strData, 1, object, 1, args, 1);
                printlines1(strData, 1, object, 1, args, 101);
                this.outStream.write(this.CompressOff);
            }
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 101);

            //Logic for Total

         /*   JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);
            String strTotalNoVat = "";

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("AED - " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 101);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("AED - " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 101);
                }
            }

            if (jTOBject.has("Total Amount(AED)")) {
                strTotalNoVat = getAccurateText("Total Amount(AED) : " + jTOBject.getString("Total Amount(AED)"), 137 + MAXLEngth, 2);
                printlines1(strTotalNoVat, 1, object, 1, args, 101);
            }*/

            /*JSONArray jTotal = object.getJSONArray("TOTAL");
            for (i = 0; i < jTotal.length(); i++) {
                JSONObject jTOBject = jTotal.getJSONObject(0);
                String strTotal = "";
                for (j = 0; j < headers.length(); j++) {
                    if (jTOBject.has(headers.getString(j))) {
                        strTotal = new StringBuilder(String.valueOf(strTotal)).append(getAccurateText(jTOBject.getString(headers.getString(j).toString()), ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(j).toString())).intValue())).toString();
                    } else {
                        stringBuilder = new StringBuilder(String.valueOf(strTotal));
                        if (headers.getString(j).equals("ARABIC DESCRIPTION")) {
                            string = "TOTAL";
                        } else {
                            string = "";
                        }
                        strTotal = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(j))).intValue() + MAXLEngth, 1)).toString();
                    }
                }
                printlines1(strTotal, 1, object, 1, args, 1);
            }*/

            //rintlines1(strTotal, 1, object, 1, args, 1);
            //Logic Ends Here
            this.outStream.write(this.CompressOff);
            printlines1(" ", 1, object, 1, args, 101);
            this.outStream.write(this.BoldOn);

            JSONObject jSONObject = object;
            //printlines1(getAccurateText("Load Value : ", 50, 2) + getAccurateText(object.getString("LoadValue"), 12, 2), 1, jSONObject, 1, args, 2);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.BoldOff);
            printlines1(getAccurateText("_____________", 26, 1) + getAccurateText("____________", 27, 1) + getAccurateText("____________", 27, 1), 2, object, 1, args, 101);
            this.outStream.write(this.NewLine);
            printlines1(getAccurateText("STORE KEEPER", 26, 1) + getAccurateText("DUTY SUPERVISOR", 26, 1) + getAccurateText("SALESMAN", 26, 1), 2, object, 1, args, 101);
            jSONObject = object;
            // printlines1(getAccurateText(object.getString("printstatus"), 80, 1), 2, jSONObject, 2, args, 1);
            //closeConnection();
            if (context instanceof LoadConfirmActivity) {
                LoadConfirmActivity.Companion.callbackFunction();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Parsing Data
    //Load Summary
    void parseVanSummaryResponse(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;
            String string;
            int j;
            this.hashValues = new HashMap();
            this.hashValues.put("S.NO", Integer.valueOf(8));
            this.hashValues.put("ITEM#", Integer.valueOf(9));
            this.hashValues.put("DESCRIPTION", Integer.valueOf(48));
            this.hashValues.put("UOM", Integer.valueOf(16));
            this.hashValues.put("QTY", Integer.valueOf(20));

            this.hashPositions = new HashMap();
            this.hashPositions.put("S.NO", Integer.valueOf(0));
            this.hashPositions.put("ITEM#", Integer.valueOf(0));
            this.hashPositions.put("DESCRIPTION", Integer.valueOf(0));
            this.hashPositions.put("UOM", Integer.valueOf(1));
            this.hashPositions.put("QTY", Integer.valueOf(1));

            line(this.startln);

            Const.loadNumber = 1;
            headerprint(object, 21);  //Uncomment to print header

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            //int MAXLEngth = 137;
            //String strTotal = "";
            //JSONArray jTotal = object.getJSONArray("TOTAL");

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }
            //JSONObject jTOBject = jTotal.getJSONObject(0);
            for (i = 0; i < headers.length(); i++) {
                stringBuilder = new StringBuilder(String.valueOf(strheader));
                if (headers.getString(i).indexOf(" ") == -1) {
                    string = headers.getString(i);
                } else {
                    string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                }
                string = string.replaceAll("_", " ");
                strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();
                stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                if (headers.getString(i).indexOf(" ") == -1) {
                    string = "";
                } else {
                    string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length()).trim();
                }
                strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();
            }
            this.outStream.write(this.CompressOn);
            printlines1(strheader, 1, object, 1, args, 101);
            printlines1(strHeaderBottom, 1, object, 1, args, 101);
            printlines1(printSepratorcomp(), 1, object, 1, args, 101);
            this.outStream.write(this.CompressOff);
            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;
                    HashMap hashMap = this.hashPositions;
                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion, i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                //printlines1(strData, 1, object, 1, args, 1);
                printlines1(strData, 1, object, 1, args, 101);
                this.outStream.write(this.CompressOff);
            }
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 101);

            //Logic for Total
            this.outStream.write(this.CompressOff);
            printlines1(" ", 1, object, 1, args, 101);
            this.outStream.write(this.BoldOn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Sales Invoice
    void printSalesInvoice(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int printoultlet;
            JSONArray jCheques;
            JSONObject jCash;
            JSONObject jChequeDetails;
            String copyStatus;
            JSONObject jSONObject;
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(7));
            this.hashValues.put("Description", Integer.valueOf(32));
            this.hashValues.put("Unit", Integer.valueOf(8));
            this.hashValues.put("Qty", Integer.valueOf(8));
            this.hashValues.put("Unit Price", Integer.valueOf(13));
            this.hashValues.put("Disc%", Integer.valueOf(13));
            this.hashValues.put("Vat", Integer.valueOf(8));
            this.hashValues.put("Disc Amount", Integer.valueOf(13));
            this.hashValues.put("Value", Integer.valueOf(13));
            this.hashValues.put("Vat Amt", Integer.valueOf(8));
            this.hashValues.put("Price (AED)", Integer.valueOf(13));


            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Qty", Integer.valueOf(1));
            this.hashPositions.put("Unit Price", Integer.valueOf(1));
            this.hashPositions.put("Disc%", Integer.valueOf(1));
            this.hashPositions.put("Vat", Integer.valueOf(1));
            this.hashPositions.put("Disc Amount", Integer.valueOf(1));
            this.hashPositions.put("Value", Integer.valueOf(1));
            this.hashPositions.put("Vat Amt", Integer.valueOf(1));
            this.hashPositions.put("Price (AED)", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 9);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 9);
            printlines1(strHeaderBottom, 1, object, 1, args, 9);
            printlines1(printSepratorcomp(), 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {

                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;
                    HashMap hashMap = this.hashPositions;
                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion, i2, ((Integer) hashMap.get(obj)).intValue())).toString();

//                    String string;
//                    stringBuilder = new StringBuilder(String.valueOf(strDataHeader));
//                    if (itemDescrion.indexOf(" ") == -1) {
//                        string = itemDescrion;
//                    } else {
//                        string = itemDescrion.substring(0, itemDescrion.indexOf(" "));
//                    }
//                    strDataHeader = stringBuilder.append(getAccurateText(string, i2, ((Integer)hashMap.get(obj)).intValue())).toString();
//
//                    stringBuilder = new StringBuilder(String.valueOf(strDataBottom));
//                    if (itemDescrion.indexOf(" ") == -1) {
//                        string = "";
//                    } else {
//                        string = itemDescrion.substring(itemDescrion.indexOf(" "), itemDescrion.length());
//                    }
//                    strDataBottom = stringBuilder.append(getAccurateText(string, i2, ((Integer) hashMap.get(obj)).intValue())).toString();

                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 9);
                // printlines1(strDataBottom, 1, object, 1, args, 9);
                this.outStream.write(this.CompressOff);
            }
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);

            JSONArray jGrosArr = object.getJSONArray("grossTotal");
            String strData = "";
            for (int j = 0; j < jGrosArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jGrosArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);
            //this.outStream.write(this.NewLine);
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);

            JSONArray jDisArr = object.getJSONArray("disTotal");
            strData = "";
            for (int j = 0; j < jDisArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jDisArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);
            //this.outStream.write(this.NewLine);
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);

            JSONArray jNetArr = object.getJSONArray("netTotal");
            strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");


                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 9);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 9);
                }
            }
            this.outStream.write(this.CompressOn);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Sales");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Request
    void printOrderRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int printoultlet;
            JSONArray jCheques;
            JSONObject jCash;
            JSONObject jChequeDetails;
            String copyStatus;
            JSONObject jSONObject;
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(7));
            this.hashValues.put("Description", Integer.valueOf(32));
            this.hashValues.put("Unit", Integer.valueOf(8));
            this.hashValues.put("Qty", Integer.valueOf(8));
            this.hashValues.put("Unit Price", Integer.valueOf(13));
            this.hashValues.put("Disc", Integer.valueOf(13));
            this.hashValues.put("Vat", Integer.valueOf(8));
            this.hashValues.put("Disc Amount", Integer.valueOf(13));
            this.hashValues.put("Value", Integer.valueOf(13));
            this.hashValues.put("Vat Amt", Integer.valueOf(8));
            this.hashValues.put("Price (AED)", Integer.valueOf(13));


            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Qty", Integer.valueOf(1));
            this.hashPositions.put("Unit Price", Integer.valueOf(1));
            this.hashPositions.put("Disc", Integer.valueOf(1));
            this.hashPositions.put("Vat", Integer.valueOf(1));
            this.hashPositions.put("Disc Amount", Integer.valueOf(1));
            this.hashPositions.put("Value", Integer.valueOf(1));
            this.hashPositions.put("Vat Amt", Integer.valueOf(1));
            this.hashPositions.put("Price (AED)", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 11);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 11);
            printlines1(strHeaderBottom, 1, object, 1, args, 11);
            printlines1(printSepratorcomp(), 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 11);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);

            JSONArray jGrosArr = object.getJSONArray("grossTotal");
            String strData = "";
            for (int j = 0; j < jGrosArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jGrosArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);
            //this.outStream.write(this.NewLine);
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);

            JSONArray jDisArr = object.getJSONArray("disTotal");
            strData = "";
            for (int j = 0; j < jDisArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jDisArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);

            JSONArray jNetArr = object.getJSONArray("netTotal");
            strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 11);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");


                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 9);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 9);
                }
            }
            this.outStream.write(this.CompressOn);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Order");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Load Request
    void printLoadRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(10));
            this.hashValues.put("Description", Integer.valueOf(50));
            this.hashValues.put("Unit", Integer.valueOf(8));
            this.hashValues.put("Qty", Integer.valueOf(8));
            this.hashValues.put("Unit Price", Integer.valueOf(13));
            this.hashValues.put("Vat", Integer.valueOf(8));
            this.hashValues.put("Value", Integer.valueOf(16));
            this.hashValues.put("Vat Amt", Integer.valueOf(8));
            this.hashValues.put("Price (AED)", Integer.valueOf(16));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Qty", Integer.valueOf(1));
            this.hashPositions.put("Unit Price", Integer.valueOf(1));
            this.hashPositions.put("Vat", Integer.valueOf(1));
            this.hashPositions.put("Value", Integer.valueOf(1));
            this.hashPositions.put("Vat Amt", Integer.valueOf(1));
            this.hashPositions.put("Price (AED)", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 12);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 12);
            printlines1(strHeaderBottom, 1, object, 1, args, 12);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 12);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);

            JSONArray jNetArr = object.getJSONArray("netTotal");
            String strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");


                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 12);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 12);
                }
            }
            this.outStream.write(this.CompressOn);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Order");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Sales Summary
    void printSalesSummary(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(15));
            this.hashValues.put("Transaction No.", Integer.valueOf(30));
            this.hashValues.put("Cust. No.", Integer.valueOf(18));
            this.hashValues.put("Customer", Integer.valueOf(45));
            this.hashValues.put("Sales", Integer.valueOf(13));
            this.hashValues.put("Amount Paid", Integer.valueOf(15));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Transaction No.", Integer.valueOf(0));
            this.hashPositions.put("Cust. No.", Integer.valueOf(1));
            this.hashPositions.put("Customer", Integer.valueOf(1));
            this.hashPositions.put("Sales", Integer.valueOf(1));
            this.hashPositions.put("Amount Paid", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 15);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
//            this.outStream.write(this.CompressOn);
//            printlines1(strheader, 1, object, 1, args, 9);
//            printlines1(strHeaderBottom, 1, object, 1, args, 9);
//            printlines1(printSepratorcomp(), 1, object, 1, args, 9);
//            this.outStream.write(this.CompressOff);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            if (jData.length() > 0) {
                this.outStream.write(this.BoldOn);
                this.outStream.write("       ".getBytes());
                this.outStream.write(this.UnderlineOn);
                printlines2("CASH INVOICE", 1, object, 1, args, 4, 15);
                this.outStream.write(this.UnderlineOff);
                this.outStream.write(this.BoldOff);

                this.outStream.write(this.NewLine);
                this.outStream.write(this.CompressOn);
                printlines1(strheader, 1, object, 1, args, 15);
                printlines1(strHeaderBottom, 1, object, 1, args, 15);
                printlines1(printSepratorcomp(), 1, object, 1, args, 15);
                this.outStream.write(this.CompressOff);

                for (i = 0; i < jData.length(); i++) {
                    JSONArray jArr = jData.getJSONArray(i);
                    String strData = "";
                    for (int j = 0; j < jArr.length(); j++) {
                        int i2;
                        Object obj;
                        String itemDescrion = jArr.getString(j);

                        stringBuilder = new StringBuilder(String.valueOf(strData));

                        i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                        HashMap hashMap = this.hashPositions;

                        obj = headers.getString(j).toString();

                        strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                    }
                    this.outStream.write(this.CompressOn);
                    this.count++;
                    printlines1(strData, 1, object, 1, args, 15);
                    this.outStream.write(this.CompressOff);
                }

                //Gagdadfdsafsd
                this.outStream.write(this.CompressOn);
                printlines1(printSepratorcomp(), 1, object, 1, args, 15);
                this.outStream.write(this.CompressOff);
                this.outStream.write(this.NewLine);

                //this.outStream.write(this.NewLine);
                if (jTOBject.has("Total_Case")) {
                    this.outStream.write(this.CompressOn);
                    String strTotalVat = getAccurateText("Total Cash Sales : " + jTOBject.getString("Total_Case"), 68, 2);
                    String strTotalVatPaid = getAccurateText(" Total Cash Collection : " + jTOBject.getString("Total_CasePaid"), 68, 2);
                    printlines1(strTotalVat + strTotalVatPaid, 1, object, 1, args, 15);
                    this.outStream.write(this.CompressOff);
                    this.outStream.write(this.NewLine);
                }
            }

            JSONArray grData = object.getJSONArray("creditdata");
            if (grData.length() > 0) {
                // this.count += 5;
                this.outStream.write(this.BoldOn);
                this.outStream.write("       ".getBytes());
                this.outStream.write(this.UnderlineOn);
                printlines2("CREDIT INVOICE", 1, object, 1, args, 4, 15);
                this.outStream.write(this.UnderlineOff);
                this.outStream.write(this.BoldOff);

                this.outStream.write(this.NewLine);

                this.outStream.write(this.CompressOn);
                printlines1(strheader, 1, object, 1, args, 9);
                printlines1(strHeaderBottom, 1, object, 1, args, 9);
                printlines1(printSepratorcomp(), 1, object, 1, args, 9);
                this.outStream.write(this.CompressOff);

                for (i = 0; i < grData.length(); i++) {
                    JSONArray jArr = grData.getJSONArray(i);
                    String strData = "";
                    for (int j = 0; j < jArr.length(); j++) {
                        int i2;
                        Object obj;
                        String itemDescrion = jArr.getString(j);

                        stringBuilder = new StringBuilder(String.valueOf(strData));

                        i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                        HashMap hashMap = this.hashPositions;

                        obj = headers.getString(j).toString();

                        strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                    }
                    this.outStream.write(this.CompressOn);
                    this.count++;
                    printlines1(strData, 1, object, 1, args, 15);
                    this.outStream.write(this.CompressOff);
                }
                this.outStream.write(this.CompressOn);
                printlines1(printSepratorcomp(), 1, object, 1, args, 15);
                this.outStream.write(this.CompressOff);

                if (jTOBject.has("Total_Credit")) {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.CompressOn);
                    String strTotalVat = getAccurateText("Total Credit Sales : " + jTOBject.getString("Total_Credit"), 68, 2);
                    String strTotalVatPaid = getAccurateText("Total Credit Collection : " + jTOBject.getString("Total_CreditPaid"), 68, 2);
                    printlines1(strTotalVat + strTotalVatPaid, 1, object, 1, args, 15);
                    this.outStream.write(this.CompressOff);
                }
            }

            if (jTOBject.has("Total_Sales")) {
                this.outStream.write(this.NewLine);
                this.outStream.write(this.CompressOn);
                String strTotalVat = getAccurateText("Total Sales : " + jTOBject.getString("Total_Sales"), 68, 2);
                String strTotalVatPaid = getAccurateText("Total Amount Collected : " + jTOBject.getString("Total_SalesPaid"), 68, 2);
                printlines1(strTotalVat + strTotalVatPaid, 1, object, 1, args, 15);
                this.outStream.write(this.CompressOff);
            }
            //printlines2(getAccurateText("_______________ ", 80, 1), 2, object, 2, args, 4, 4);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            // printlines2(getAccurateText("SALESMAN ", 80, 1), 2, object, 2, args, 4, 4);
            printlines1(getAccurateText("_____________", 40, 1) + getAccurateText("____________", 40, 1), 2, object, 1, args, 4);
            printlines1(getAccurateText("SUPERVISOR", 40, 1) + getAccurateText("SALESMAN", 40, 1), 2, object, 1, args, 4);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Deposite Summary
    void printDepositesSummary(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(12));
            this.hashValues.put("Receipt No", Integer.valueOf(15));
            this.hashValues.put("Customer Code", Integer.valueOf(15));
            this.hashValues.put("Customer Name", Integer.valueOf(40));
            this.hashValues.put("Cheque No", Integer.valueOf(15));
            this.hashValues.put("Cheque Amount", Integer.valueOf(20));
            this.hashValues.put("Cash Amount", Integer.valueOf(20));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Receipt No", Integer.valueOf(0));
            this.hashPositions.put("Customer Code", Integer.valueOf(0));
            this.hashPositions.put("Customer Name", Integer.valueOf(0));
            this.hashPositions.put("Cheque No", Integer.valueOf(1));
            this.hashPositions.put("Cheque Amount", Integer.valueOf(1));
            this.hashPositions.put("Cash Amount", Integer.valueOf(1));


            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 16);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 16);
            printlines1(strHeaderBottom, 1, object, 1, args, 16);
            printlines1(printSepratorcomp(), 1, object, 1, args, 16);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 16);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 16);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            //this.outStream.write(this.NewLine);
            if (jTOBject.has("Total_Case")) {
                this.outStream.write(this.CompressOn);
                String strTotalVat = getAccurateText("Total Cash Amount : " + jTOBject.getString("Total_Case"), 68, 2);
                String strTotalVatPaid = getAccurateText(" Total Cheque Amount : " + jTOBject.getString("Total_Credit"), 68, 2);
                printlines1(strTotalVat + strTotalVatPaid, 1, object, 1, args, 16);
                this.outStream.write(this.CompressOff);
                this.outStream.write(this.NewLine);
            }
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            // printlines2(getAccurateText("SALESMAN ", 80, 1), 2, object, 2, args, 4, 4);
            printlines1(getAccurateText("_____________", 40, 1) + getAccurateText("____________", 40, 1), 2, object, 1, args, 16);
            printlines1(getAccurateText("ACCOUNTS", 40, 1) + getAccurateText("SALESMAN", 40, 1), 2, object, 1, args, 16);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Deposite Summary
    void printDailyOtherSummary(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(12));
            this.hashValues.put("DR No", Integer.valueOf(15));
            this.hashValues.put("Customer Name", Integer.valueOf(42));
            this.hashValues.put("P.O.B", Integer.valueOf(12));
            this.hashValues.put("Delivered Amount", Integer.valueOf(19));
            this.hashValues.put("Cash", Integer.valueOf(12));
            this.hashValues.put("Cheque", Integer.valueOf(12));
            this.hashValues.put("GRV", Integer.valueOf(12));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("DR No", Integer.valueOf(0));
            this.hashPositions.put("Customer Name", Integer.valueOf(0));
            this.hashPositions.put("P.O.B", Integer.valueOf(1));
            this.hashPositions.put("Delivered Amount", Integer.valueOf(1));
            this.hashPositions.put("Cash", Integer.valueOf(1));
            this.hashPositions.put("Cheque", Integer.valueOf(1));
            this.hashPositions.put("GRV", Integer.valueOf(1));


            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 17);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 17);
            printlines1(strHeaderBottom, 1, object, 1, args, 17);
            printlines1(printSepratorcomp(), 1, object, 1, args, 17);
            this.outStream.write(this.CompressOff);
            String strData = "";
            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 17);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 17);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);

            JSONArray jNetArr = object.getJSONArray("netTotal");
            strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            this.outStream.write(this.CompressOn);
            printheaders(getAccurateText("No of Invoices: " + jTOBject.getString("No of Invoice"), 40, 0) + getAccurateText("Total Sales: " + jTOBject.getString("Total_Sales"), 26, 2), false, 1);
            this.outStream.write(this.NewLine);
            printheaders(getAccurateText("No of Visit: " + jTOBject.getString("No of Visit"), 40, 0) + getAccurateText("MTD Sales: " + jTOBject.getString("MTD_SALES"), 26, 2), false, 1);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            // printlines2(getAccurateText("SALESMAN ", 80, 1), 2, object, 2, args, 4, 4);
            printlines1(getAccurateText("_____________", 40, 1) + getAccurateText("____________", 40, 1), 2, object, 1, args, 17);
            printlines1(getAccurateText("SUPERVISOR", 40, 1) + getAccurateText("SALESMAN", 40, 1), 2, object, 1, args, 17);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //RTC Summary
    void printDailyRTCSummary(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(9));
            this.hashValues.put("DR No", Integer.valueOf(9));
            this.hashValues.put("Customer Name", Integer.valueOf(15));
            this.hashValues.put("D/I", Integer.valueOf(5));
            this.hashValues.put("D/I (B)", Integer.valueOf(5));
            this.hashValues.put("APP", Integer.valueOf(5));
            this.hashValues.put("APP (B)", Integer.valueOf(5));
            this.hashValues.put("WOD", Integer.valueOf(5));
            this.hashValues.put("M/P", Integer.valueOf(5));
            this.hashValues.put("M/P/C", Integer.valueOf(5));
            this.hashValues.put("W/P", Integer.valueOf(5));
            this.hashValues.put("W/P/C", Integer.valueOf(6));
            this.hashValues.put("CHAP", Integer.valueOf(5));
            this.hashValues.put("CHAP (C)", Integer.valueOf(5));
            this.hashValues.put("ID/AP", Integer.valueOf(5));
            this.hashValues.put("PRI", Integer.valueOf(5));
            this.hashValues.put("PATH", Integer.valueOf(5));
            this.hashValues.put("VADA", Integer.valueOf(5));
            this.hashValues.put("GRV", Integer.valueOf(9));
            this.hashValues.put("Total Sales", Integer.valueOf(9));
            this.hashValues.put("Cash/Cheque", Integer.valueOf(9));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("DR No", Integer.valueOf(0));
            this.hashPositions.put("Customer Name", Integer.valueOf(0));
            this.hashPositions.put("D/I", Integer.valueOf(1));
            this.hashPositions.put("D/I (B)", Integer.valueOf(1));
            this.hashPositions.put("APP", Integer.valueOf(1));
            this.hashPositions.put("APP (B)", Integer.valueOf(1));
            this.hashPositions.put("WOD", Integer.valueOf(1));
            this.hashPositions.put("M/P", Integer.valueOf(1));
            this.hashPositions.put("M/P/C", Integer.valueOf(1));
            this.hashPositions.put("W/P", Integer.valueOf(1));
            this.hashPositions.put("W/P/C", Integer.valueOf(1));
            this.hashPositions.put("CHAP", Integer.valueOf(1));
            this.hashPositions.put("CHAP (C)", Integer.valueOf(1));
            this.hashPositions.put("ID/AP", Integer.valueOf(1));
            this.hashPositions.put("PRI", Integer.valueOf(1));
            this.hashPositions.put("PATH", Integer.valueOf(1));
            this.hashPositions.put("VADA", Integer.valueOf(1));
            this.hashPositions.put("GRV", Integer.valueOf(1));
            this.hashPositions.put("Total Sales", Integer.valueOf(1));
            this.hashPositions.put("Cash/Cheque", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 17);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 17);
            printlines1(strHeaderBottom, 1, object, 1, args, 17);
            printlines1(printSepratorcomp(), 1, object, 1, args, 17);
            this.outStream.write(this.CompressOff);
            String strData = "";
            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 17);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 17);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);

            JSONArray jNetArr = object.getJSONArray("netTotal");
            strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 9);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            this.outStream.write(this.CompressOn);
            printheaders(getAccurateText("No of Invoices: " + jTOBject.getString("No of Invoice"), 40, 0) + getAccurateText("Total Sales: " + jTOBject.getString("Total_Sales"), 26, 2), false, 1);
            this.outStream.write(this.NewLine);
            printheaders(getAccurateText("No of Visit: " + jTOBject.getString("No of Visit"), 40, 0) + getAccurateText("MTD Sales: " + jTOBject.getString("MTD_SALES"), 26, 2), false, 1);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            // printlines2(getAccurateText("SALESMAN ", 80, 1), 2, object, 2, args, 4, 4);
            printlines1(getAccurateText("_____________", 40, 1) + getAccurateText("____________", 40, 1), 2, object, 1, args, 17);
            printlines1(getAccurateText("SUPERVISOR", 40, 1) + getAccurateText("SALESMAN", 40, 1), 2, object, 1, args, 17);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Cahs Payment Request
    void printSinglePaymentRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Date", Integer.valueOf(28));
            this.hashValues.put("Invoice No", Integer.valueOf(28));
            this.hashValues.put("Amount", Integer.valueOf(26));
            this.hashValues.put("O/s Amount", Integer.valueOf(26));
            this.hashValues.put("Recvd Amount", Integer.valueOf(28));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Date", Integer.valueOf(0));
            this.hashPositions.put("Invoice No", Integer.valueOf(0));
            this.hashPositions.put("Amount", Integer.valueOf(1));
            this.hashPositions.put("O/s Amount", Integer.valueOf(1));
            this.hashPositions.put("Recvd Amount", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 13);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 13);
            printlines1(strHeaderBottom, 1, object, 1, args, 13);
            printlines1(printSepratorcomp(), 1, object, 1, args, 13);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 13);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 13);
            this.outStream.write(this.CompressOff);

            JSONArray jNetArr = object.getJSONArray("netTotal");
            String strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 13);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);

            if (object.getString("Comment") != "") {
                JSONArray jNetDArr = object.getJSONArray("shelfTotal");
                String strSData = "";
                for (int j = 0; j < jNetDArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jNetDArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strSData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strSData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strSData, 1, object, 1, args, 13);
                this.outStream.write(this.CompressOff);
                this.outStream.write(this.BoldOff);

                JSONArray jNetDTArr = object.getJSONArray("MDTotal");
                String strSTData = "";
                for (int j = 0; j < jNetDTArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jNetDTArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strSTData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strSTData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strSTData, 1, object, 1, args, 13);
                this.outStream.write(this.CompressOff);
                this.outStream.write(this.BoldOff);
            }

            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");


                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 13);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 13);
                }
            }
            this.outStream.write(this.CompressOn);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            if (context instanceof CollectionActivity) {
                CollectionActivity.Companion.callbackFunction("");
            }

            if (context instanceof PaymentDetailActivity) {
                PaymentDetailActivity.Companion.callbackFunction("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Cahs Payment Request
    void printMultiPaymentRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Date", Integer.valueOf(28));
            this.hashValues.put("Invoice No", Integer.valueOf(28));
            this.hashValues.put("Amount", Integer.valueOf(26));
            this.hashValues.put("O/s Amount", Integer.valueOf(26));
            this.hashValues.put("Recvd Amount", Integer.valueOf(28));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Date", Integer.valueOf(0));
            this.hashPositions.put("Invoice No", Integer.valueOf(0));
            this.hashPositions.put("Amount", Integer.valueOf(1));
            this.hashPositions.put("O/s Amount", Integer.valueOf(1));
            this.hashPositions.put("Recvd Amount", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 13);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 13);
            printlines1(strHeaderBottom, 1, object, 1, args, 13);
            printlines1(printSepratorcomp(), 1, object, 1, args, 13);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {

                JSONObject jCust = jData.getJSONObject(i);
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.CompressOn);
                printlines1(jCust.getString("Customer"), 1, object, 1, args, 13);
                this.outStream.write(this.CompressOff);
                this.outStream.write(this.BoldOff);

                JSONArray jCDArr = jCust.getJSONArray("data");
                int index = 0;
                for (int k = 0; k < jCDArr.length(); k++) {
                    JSONArray jArr = jCDArr.getJSONArray(k);
                    String strData = "";
                    for (int j = 0; j < jArr.length(); j++) {
                        int i2;
                        Object obj;
                        String itemDescrion = jArr.getString(j);

                        stringBuilder = new StringBuilder(String.valueOf(strData));

                        i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                        HashMap hashMap = this.hashPositions;

                        obj = headers.getString(j).toString();

                        strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                    }
                    if (index == (jCDArr.length() - 1)) {
                        this.outStream.write(this.CompressOn);
                        printlines1(printSepratorcomp(), 1, object, 1, args, 13);
                        this.outStream.write(this.CompressOff);
                        this.outStream.write(this.NewLine);
                        this.outStream.write(this.CompressOn);
                        this.count++;
                        printlines1(strData, 1, object, 1, args, 13);
                        this.outStream.write(this.CompressOff);
                        this.outStream.write(this.CompressOn);
                        printlines1(printSepratorcomp(), 1, object, 1, args, 13);
                        this.outStream.write(this.CompressOff);
                    } else {
                        this.outStream.write(this.CompressOn);
                        this.count++;
                        printlines1(strData, 1, object, 1, args, 13);
                        this.outStream.write(this.CompressOff);
                    }
                    index++;
                }
            }

            this.outStream.write(this.NewLine);
            JSONArray jNetArr = object.getJSONArray("netTotal");
            String strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 13);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            if (object.getString("Comment") != "") {
                printlines1(getAccurateText("Note: " + object.getString("Comment"), 130, 0), 1, object, 1, args, 13);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
            }

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 9);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 9);
                }
            }
            this.outStream.write(this.CompressOn);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            if (context instanceof CollectionActivity) {
                CollectionActivity.Companion.callbackFunction("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Credit Note
    void printCreditNot(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int printoultlet;
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(10));
            this.hashValues.put("Description", Integer.valueOf(28));
            this.hashValues.put("Tax Inv.No", Integer.valueOf(16));
            this.hashValues.put("Inv.Amount", Integer.valueOf(13));
            this.hashValues.put("Unit", Integer.valueOf(7));
            this.hashValues.put("Qty", Integer.valueOf(7));
            this.hashValues.put("Unit Price", Integer.valueOf(13));
            this.hashValues.put("Vat %", Integer.valueOf(8));
            this.hashValues.put("Value", Integer.valueOf(13));
            this.hashValues.put("Vat Amt", Integer.valueOf(8));
            this.hashValues.put("Price (AED)", Integer.valueOf(13));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Tax Inv.No", Integer.valueOf(0));
            this.hashPositions.put("Inv.Amount", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Qty", Integer.valueOf(1));
            this.hashPositions.put("Unit Price", Integer.valueOf(1));
            this.hashPositions.put("Vat %", Integer.valueOf(1));
            this.hashPositions.put("Value", Integer.valueOf(1));
            this.hashPositions.put("Vat Amt", Integer.valueOf(1));
            this.hashPositions.put("Price (AED)", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 10);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);
            printlines1(strheader, 1, object, 1, args, 10);
            printlines1(strHeaderBottom, 1, object, 1, args, 10);
            printlines1(printSepratorcomp(), 1, object, 1, args, 10);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 10);
                this.outStream.write(this.CompressOff);
            }
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 10);
            this.outStream.write(this.CompressOff);


            JSONArray jNetArr = object.getJSONArray("netTotal");
            String strData = "";
            for (int j = 0; j < jNetArr.length(); j++) {
                int i2;
                Object obj;
                String itemDescrion = jNetArr.getString(j);

                stringBuilder = new StringBuilder(String.valueOf(strData));

                i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                HashMap hashMap = this.hashPositions;

                obj = headers.getString(j).toString();

                strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
            }
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.CompressOn);
            this.count++;
            printlines1(strData, 1, object, 1, args, 10);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (jTOBject.has("Total Amount(AED)")) {
                String Amount = jTOBject.getString("Total Amount(AED)");
                int decimals = Amount.indexOf(".");
                String decimalValue = null;
                if (0 <= decimals) {
                    decimalValue = Amount.substring(decimals + 1);
                    Amount = Amount.substring(0, decimals).replaceAll("-", "");


                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        Long dataDecimal = Long.parseLong(decimalValue);
                        strWord = NumberToWord.convert(data) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 10);
                } else {

                    String strWord = "";
                    try {
                        Long data = Long.parseLong(Amount);
                        strWord = NumberToWord.convert(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    printlines1(getAccurateText("Total Amounts in Words: AED " + strWord.toUpperCase(), 130, 0), 1, object, 1, args, 10);
                }
            }
            this.outStream.write(this.CompressOn);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Return");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Load Request
    void printUnloadDamagedRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(15));
            this.hashValues.put("Description", Integer.valueOf(82));
            this.hashValues.put("Unit", Integer.valueOf(20));
            this.hashValues.put("Qty", Integer.valueOf(20));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Qty", Integer.valueOf(1));


            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 18);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 12);
            printlines1(strHeaderBottom, 1, object, 1, args, 12);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 12);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Order");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Load Request
    void printUnloadExpiryRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(15));
            this.hashValues.put("Description", Integer.valueOf(82));
            this.hashValues.put("Unit", Integer.valueOf(20));
            this.hashValues.put("Qty", Integer.valueOf(20));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Qty", Integer.valueOf(1));


            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 19);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 12);
            printlines1(strHeaderBottom, 1, object, 1, args, 12);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 12);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Order");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Load Request
    void printUnloadEndInventryRequest(JSONObject object, String args) {
        StringBuffer s1 = new StringBuffer();
        try {
            int i;
            StringBuilder stringBuilder;

            this.hashValues = new HashMap();
            this.hashValues.put("Sr No", Integer.valueOf(12));
            this.hashValues.put("Description", Integer.valueOf(41));
            this.hashValues.put("Unit", Integer.valueOf(12));
            this.hashValues.put("Load Qty", Integer.valueOf(12));
            this.hashValues.put("Sale Qty", Integer.valueOf(12));
            this.hashValues.put("Return Qty", Integer.valueOf(12));
            this.hashValues.put("Variance Qty", Integer.valueOf(12));
            this.hashValues.put("Fresh Qty", Integer.valueOf(12));
            this.hashValues.put("Unload Qty", Integer.valueOf(12));

            this.hashPositions = new HashMap();
            this.hashPositions.put("Sr No", Integer.valueOf(0));
            this.hashPositions.put("Description", Integer.valueOf(0));
            this.hashPositions.put("Unit", Integer.valueOf(1));
            this.hashPositions.put("Load Qty", Integer.valueOf(1));
            this.hashPositions.put("Sale Qty", Integer.valueOf(1));
            this.hashPositions.put("Return Qty", Integer.valueOf(1));
            this.hashPositions.put("Variance Qty", Integer.valueOf(1));
            this.hashPositions.put("Fresh Qty", Integer.valueOf(1));
            this.hashPositions.put("Unload Qty", Integer.valueOf(1));

            this.outStream.write(this.wakeUp);
            line(this.startln);
            headerprint(object, 20);
            this.outStream.write(this.NewLine);

            JSONArray headers = object.getJSONArray("HEADERS");
            String strheader = "";
            String strHeaderBottom = "";
            String strTotal = "";

            int MAXLEngth = 137;
            for (i = 0; i < headers.length(); i++) {
                MAXLEngth -= ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue();
            }
            if (MAXLEngth > 0) {
                MAXLEngth /= headers.length();
            }

            for (i = 0; i < headers.length(); i++) {
                try {
                    String string;
                    stringBuilder = new StringBuilder(String.valueOf(strheader));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = headers.getString(i);
                    } else {
                        string = headers.getString(i).substring(0, headers.getString(i).indexOf(" "));
                    }
                    strheader = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                    stringBuilder = new StringBuilder(String.valueOf(strHeaderBottom));
                    if (headers.getString(i).indexOf(" ") == -1) {
                        string = "";
                    } else {
                        string = headers.getString(i).substring(headers.getString(i).indexOf(" "), headers.getString(i).length());
                    }
                    strHeaderBottom = stringBuilder.append(getAccurateText(string, ((Integer) this.hashValues.get(headers.getString(i).toString())).intValue() + MAXLEngth, ((Integer) this.hashPositions.get(headers.getString(i).toString())).intValue())).toString();

                } catch (Exception e) {
                }
            }
            this.outStream.write(this.CompressOn);

            printlines1(strheader, 1, object, 1, args, 12);
            printlines1(strHeaderBottom, 1, object, 1, args, 12);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String strData = "";
                for (int j = 0; j < jArr.length(); j++) {
                    int i2;
                    Object obj;
                    String itemDescrion = jArr.getString(j);

                    stringBuilder = new StringBuilder(String.valueOf(strData));

                    i2 = ((Integer) this.hashValues.get(headers.getString(j).toString())).intValue() + MAXLEngth;

                    HashMap hashMap = this.hashPositions;

                    obj = headers.getString(j).toString();

                    strData = stringBuilder.append(getAccurateText(itemDescrion.replaceAll("%20", " ").replaceAll("%26", "&").replaceAll("%2C", ",").replaceAll("%28", "(").replaceAll("%29", ")").replaceAll("%2F", "/").replaceAll("%5C", "\\").replaceAll("%5C", "\\"), i2, ((Integer) hashMap.get(obj)).intValue())).toString();
                }
                this.outStream.write(this.CompressOn);
                this.count++;
                printlines1(strData, 1, object, 1, args, 12);
                this.outStream.write(this.CompressOff);
            }

            this.outStream.write(this.NewLine);
            //Gagdadfdsafsd
            this.outStream.write(this.CompressOn);
            printlines1(printSepratorcomp(), 1, object, 1, args, 12);
            this.outStream.write(this.CompressOff);
            this.outStream.write(this.NewLine);

//            if (context instanceof OrderConfirmActivity) {
//                OrderConfirmActivity.Companion.callbackFunction("Order");
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printlines1(String data, int ln, JSONObject object, int sts, String adr, int tp) throws JSONException, IOException, LinePrinterException {
        int i;
        this.count += ln;
        boolean isEnd = false;
        if (sts == 2 && this.count != 0) {
            Log.e("String", "" + data);
            printArabic(data);
            isEnd = true;
            int lnno = (55 - this.count) + this.endln;
            for (i = 0; i < lnno; i++) {
                try {
                    if (i % 10 == 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.outStream.write(this.NewLine);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            this.outStream.write(this.CarriageReturn);
            this.count = 0;
            try {
                Thread.sleep(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
            this.status.put("status", true);
            this.status.put("isconnected", 0);
            sendUpdate(this.status, true);
        }
        if (!isEnd) {
            printArabic(data);
            Log.e("String", "" + data);
            for (i = 0; i < ln; i++) {
                try {
                    this.outStream.write(this.NewLine);
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
            if (this.count % 10 == 0) {
                try {
                    Thread.sleep(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
                } catch (InterruptedException e32) {
                    e32.printStackTrace();
                }
            }
            if (this.count > 70) {
                Log.e("Count 1 time", "Count " + this.count);
                this.outStream.write(this.CompressOff);
                this.count = 0;
                try {
                    line(this.cnln);

                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
                    if (tp == 9 || tp == 10 || tp == 11 || tp == 12 || tp == 13 || tp == 15 || tp == 16 || tp == 17
                            || tp == 101 || tp == 20 || tp == 21 || tp == 18 || tp == 19) {
                        headerprint(object, tp);
                    } else {
                        headerinvprint(object, tp);
                    }
                    this.outStream.write(printSeprator().getBytes());
                    this.count++;
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    private void printlines2(String data, int ln, JSONObject object, int sts, String adr, int tran, int tp) throws JSONException, IOException, LinePrinterException {
        int i;
        this.count += ln;
        boolean isEnd = false;
        if (sts == 2 && this.count != 0) {
            printArabic(data);
            //Log.e("salessummary count", this.count);
            //Log.e("salessummary pln", 55);
            isEnd = true;
            int lnno1 = (55 - this.count) + this.endln;
            for (i = 0; i < lnno1; i++) {
                try {
                    if (i % 10 == 0) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    this.outStream.write(this.NewLine);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            this.outStream.write(this.CarriageReturn);
            this.count = 0;
            try {
                Thread.sleep(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
            } catch (InterruptedException e3) {
                e3.printStackTrace();
            }
//            this.status.put("status", true);
//            this.status.put("isconnected", 0);
            //           sendUpdate(this.status, true);
        }
        if (!isEnd) {
            printArabic(data);
            for (i = 0; i < ln; i++) {
                try {
                    this.outStream.write(this.NewLine);
                } catch (IOException e22) {
                    e22.printStackTrace();
                }
            }
            if (this.count % 10 == 0) {
                try {
                    Thread.sleep(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
                } catch (InterruptedException e32) {
                    e32.printStackTrace();
                }
            }
            Log.d("Count", "Count1 " + this.count);
            if (this.count > 70) {
                this.count = 0;
                Log.d("Count", "Count1 " + this.count);
                Log.d("Pln", "PLn" + 55);
                try {
                    line(this.cnln);
                    this.outStream.write(this.CompressOff);

                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);
//                    this.outStream.write(this.NewLine);

                    if (tp == 9 || tp == 10 || tp == 11 || tp == 12 || tp == 13 || tp == 15 || tp == 16 || tp == 17
                            || tp == 101 || tp == 20 || tp == 21 || tp == 18 || tp == 19) {
                        headerprint(object, tp);
                    } else {
                        headerinvprint(object, tp);
                    }

                    if (tran == 2 || tran == 3) {
                        headerprint(object, tp);
                    } else if (tran == 104) {
                        headervanstockprint(object, tp);
                    } else {
                        headerprint(object, tp);
                    }
                    this.outStream.write(printSeprator().getBytes());
                    this.count++;
                    if (tran == 5 || tran == 4) {
                        this.outStream.write(this.CompressOn);
                    }
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
        }
    }

    private void headerinvprint(JSONObject object, int invtype) throws JSONException {
        try {

            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);

            this.outStream.write(this.BoldOn);
            printheaders(getAccurateText("ROUTE#: " + object.getString("ROUTE"), 26, 0) + getAccurateText("SMAN#: " + object.getString("SALESMAN"), 26, 0) + getAccurateText("DATE:" + object.getString("DOC DATE") + " " + object.getString("TIME"), 26, 2), false, 1);
            this.outStream.write(this.NewLine);
            // printheaders(getAccurateText("SALESMAN: " + object.getString("SALESMAN"), 40, 0) + getAccurateText("SALESMAN NO: " + object.getString("CONTACTNO"), 40, 2), false, 1);
            // this.outStream.write(this.NewLine);
            try {
                if (!(invtype == 3)) {
                    printheaders(getAccurateText("DOCUMENT NO: " + object.getString("DOCUMENT NO"), 40, 0) + getAccurateText("TRIP START DATE:" + object.getString("TRIP START DATE"), 40, 2), false, 1);
                    this.outStream.write(this.NewLine);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //printheaders(getAccurateText("SUPERVISOR NAME:" + object.getString("supervisorname"), 40, 0) + getAccurateText("SUPERVISOR PHONE: " + object.getString("supervisorno"), 40, 2), true, 1);
            /*if(invtype != 3) {
                this.outStream.write(this.NewLine);
                printheaders(getAccurateText("TRIP ID:" + object.getString("TripID"), 80, 0), false, 2);
            }*/
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.BoldOn);
            this.outStream.write(this.DoubleWideOn);
            if (invtype == 101) {
                printheaders(getAccurateText("LOAD SUMMARY ", 40, 1), false, 1);
                printheaders(getAccurateText("LOAD NUMBER: " + object.getString("DOCUMENT NO"), 40, 1), false, 1);
            } else if (invtype == 2) {
                printheaders(getAccurateText("LOAD TRANSFER SUMMARY", 40, 1), false, 1);
            } else if (invtype == 103) {
                printheaders(getAccurateText("ENDING INVENTORY SUMMARY", 40, 1), false, 2);
            } else if (invtype == 102) {
                printheaders(getAccurateText("LOAD REQUEST - " + object.getString("LOAD DATE"), 40, 1), false, 1);
            } else if (invtype == 6) {
                printheaders(getAccurateText("COMPANY CREDIT SUMMARY", 40, 1), false, 1);
            }
            this.outStream.write(this.DoubleWideOff);
            this.outStream.write(this.BoldOff);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            /*if (invtype == 2) {
                JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
                if (jData.getJSONObject(0).getJSONArray("DATA").length() > 0 && (jData.getJSONObject(1).getJSONArray("DATA").length() > 0 || jData.getJSONObject(2).getJSONArray("DATA").length() > 0)) {
                    printheaders(getAccurateText("FROM & TO ROUTE: " + object.getString("TO ROUTE"), 80, 0), false, 1);
                    this.outStream.write(this.NewLine);
                } else if (jData.getJSONObject(0).getJSONArray("DATA").length() > 0) {
                    printheaders(getAccurateText("FROM ROUTE: " + object.getString("TO ROUTE"), 80, 0), false, 1);
                    this.outStream.write(this.NewLine);
                } else if (jData.getJSONObject(1).getJSONArray("DATA").length() > 0) {
                    printheaders(getAccurateText("TO ROUTE: " + object.getString("TO ROUTE"), 80, 0), false, 1);
                    this.outStream.write(this.NewLine);
                } else if (jData.getJSONObject(2).getJSONArray("DATA").length() > 0) {
                    printheaders(getAccurateText("TO ROUTE: " + object.getString("TO ROUTE"), 80, 0), false, 1);
                    this.outStream.write(this.NewLine);
                }
            }
            if (invtype == 5) {
                printheaders(getAccurateText("Requested Delivery Date : " + object.getString("Requestdate"), 80, 0), false, 1);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
            }*/
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void headerprint(JSONObject object, int type) throws JSONException {
        try {
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            this.outStream.write(this.NewLine);
            if (type == 9) {   //Type for Sales Invoice
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("TAX INVOICE", 40, 1), false, 2);
                this.outStream.write(this.NewLine);
                printheaders(getAccurateText("INVOICE NO : " + object.getString("DOCUMENT NO"), 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    String customer = "[" + object.getString("CUSTOMERID") + "] " + object.getString("CUSTOMER");
                    printheaders(getAccurateText(customer, 50, 0), false, 1);
                    this.outStream.write(this.NewLine);

                    if (object.getString("ADDRESS").equals("")) {
                        printheaders(getAccurateText(object.getString("CITY"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Country"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Mobile"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        String no = object.getString("TRN").equals("null") ? "" : object.getString("TRN");
                        printheaders("CUSTOMER TRN: " + no, false, 1);
                    } else {
                        printheaders(getAccurateText(object.getString("ADDRESS"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        if (!object.getString("ADDRESS2").equals("")) {
                            printheaders(getAccurateText(object.getString("ADDRESS2"), 50, 0), false, 1);
                            this.outStream.write(this.NewLine);
                        }
                        printheaders(getAccurateText(object.getString("CITY"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Country"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Mobile"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        String no = object.getString("TRN").equals("null") ? "" : object.getString("TRN");
                        printheaders("CUSTOMER TRN: " + no, false, 1);
                    }
                    this.outStream.write(this.NewLine);//object.getString("LPO")
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Date    : " + object.getString("DOC DATE"), 40, 0) + getAccurateText("Customer LPO#: " + object.getString("CustomerLPO"), 26, 2), false, 1);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Location: " + object.getString("ROUTE"), 40, 0) + getAccurateText("Mode: " + object.getString("invoiceType"), 26, 2), false, 1);
                    this.outStream.write(this.NewLine);
                    if (object.getString("exchangeNo").equals("")) {
                        printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    } else {
                        printheaders(getAccurateText("Salesman: " + object.getString("SALESMAN"), 40, 0) + getAccurateText("Exchange No: " + object.getString("exchangeNo"), 26, 2), false, 1);
                    }
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 10) {   //Type for Sales Invoice
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("TAX CREDIT NOTE", 40, 1), false, 2);
                this.outStream.write(this.NewLine);
                printheaders(getAccurateText("CREDIT INVOICE NO : " + object.getString("DOCUMENT NO"), 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    String customer = "[" + object.getString("CUSTOMERID") + "] " + object.getString("CUSTOMER");
                    printheaders(getAccurateText(customer, 50, 0), false, 1);
                    this.outStream.write(this.NewLine);

                    if (object.getString("ADDRESS").equals("")) {
                        printheaders(getAccurateText(object.getString("CITY"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Country"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Mobile"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        String no = object.getString("TRN").equals("null") ? "" : object.getString("TRN");
                        printheaders("CUSTOMER TRN: " + no, false, 1);
                    } else {
                        printheaders(getAccurateText(object.getString("ADDRESS"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        if (!object.getString("ADDRESS2").equals("")) {
                            printheaders(getAccurateText(object.getString("ADDRESS2"), 50, 0), false, 1);
                            this.outStream.write(this.NewLine);
                        }
                        printheaders(getAccurateText(object.getString("CITY"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Country"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Mobile"), 50, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        String no = object.getString("TRN").equals("null") ? "" : object.getString("TRN");
                        printheaders("CUSTOMER TRN: " + no, false, 1);
                    }
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Date    : " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    if (object.getString("exchangeNo").equals("")) {
                        printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    } else {
                        printheaders(getAccurateText("Salesman: " + object.getString("SALESMAN"), 40, 0) + getAccurateText("Exchange No: " + object.getString("exchangeNo"), 26, 2), false, 1);
                    }
                    //printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 11) {   //Type for Sales Invoice
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("ORDER REQUEST", 40, 1), false, 2);
                this.outStream.write(this.NewLine);
                printheaders(getAccurateText("ORDER NO : " + object.getString("DOCUMENT NO"), 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    String customer = "[" + object.getString("CUSTOMERID") + "] " + object.getString("CUSTOMER");
                    printheaders(getAccurateText(customer, 50, 0), false, 1);
                    this.outStream.write(this.NewLine);

                    if (object.getString("ADDRESS").equals("")) {
                        printheaders(getAccurateText(object.getString("CITY"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Country"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Mobile"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        String no = object.getString("TRN").equals("null") ? "" : object.getString("TRN");
                        printheaders("CUSTOMER TRN: " + no, false, 1);
                    } else {
                        printheaders(getAccurateText(object.getString("ADDRESS"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        if (!object.getString("ADDRESS2").equals("")) {
                            printheaders(getAccurateText(object.getString("ADDRESS2"), 50, 0), false, 1);
                            this.outStream.write(this.NewLine);
                        }
                        printheaders(getAccurateText(object.getString("CITY"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Country"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText(object.getString("Mobile"), 40, 0), false, 1);
                        this.outStream.write(this.NewLine);
                        String no = object.getString("TRN").equals("null") ? "" : object.getString("TRN");
                        printheaders("CUSTOMER TRN: " + no, false, 1);
                    }
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Date    : " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 12) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("LOAD REQUEST", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Load Request No: " + object.getString("DOCUMENT NO"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Load Date: " + object.getString("LoadDate"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 13) {   //Type for Payment
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("RECEIPT", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Receipt No   : " + object.getString("DOCUMENT NO"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Date         : " + object.getString("DOC DATE"), 50, 0) + getAccurateText("Phone: " + object.getString("Mobile"), 26, 2), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman     : " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Recvd From   : " + object.getString("CUSTOMER") + "/" + object.getString("CUSTOMERID"), false, 2);
                    this.outStream.write(this.NewLine);
                    if (object.getString("collectionType").equalsIgnoreCase("Cash")) {
                        printheaders("Paid Through : " + object.getString("collectionType"), false, 2);
                        this.outStream.write(this.NewLine);
                    } else {
                        printheaders(getAccurateText("Paid Through : " + object.getString("collectionType"), 50, 0) + getAccurateText("Cheque No :  " + object.getString("chequeNo"), 26, 2), false, 2);
                        this.outStream.write(this.NewLine);
                        printheaders(getAccurateText("Bank Name    : " + object.getString("Bank"), 50, 0) + getAccurateText("Cheque Date : " + object.getString("chequeDate"), 26, 2), false, 2);
                        this.outStream.write(this.NewLine);
                    }
                    this.outStream.write(this.BoldOff);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Payment against the following invoices....", false, 2);
                    this.outStream.write(this.NewLine);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 15) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("SALES SUMMARY", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Summary Date : " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman     : " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 16) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("DEPOSIT SUMMARY", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Location : " + object.getString("ROUTE"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman : " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Date     : " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 17) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("DAILY FIELD ACTIVITY REPORT", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Location : " + object.getString("ROUTE"), 40, 0) + getAccurateText("Date: " + object.getString("DOC DATE"), 26, 2), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Salesman : " + object.getString("SALESMAN"), 40, 0) + getAccurateText("Start Time: " + object.getString("startTime"), 26, 2), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Helper   : " + object.getString("helperName"), 40, 0), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 101) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("LOAD SUMMARY", 40, 1), false, 2);
                this.outStream.write(this.NewLine);
                printheaders(getAccurateText("LOAD NO : " + object.getString("DOCUMENT NO"), 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Location: " + object.getString("ROUTE"), 50, 0) + getAccurateText("Date: " + object.getString("DOC DATE"), 26, 2), false, 1);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Salesman: " + object.getString("SALESMAN"), 50, 0) + getAccurateText("Trip Date: " + object.getString("TRIP START DATE"), 26, 2), false, 1);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 18) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("UNLOAD BAD DAMAGE", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Unload No: " + object.getString("DOCUMENT NO"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Unload Date: " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Location: " + object.getString("ROUTE"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 19) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("UNLOAD BAD EXPIRY", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Unload No: " + object.getString("DOCUMENT NO"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Unload Date: " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Location: " + object.getString("ROUTE"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 20) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("END INVENTORY", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders("Unload No: " + object.getString("DOCUMENT NO"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Unload Date: " + object.getString("DOC DATE"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Salesman: " + object.getString("SALESMAN"), false, 2);
                    this.outStream.write(this.NewLine);
                    printheaders("Location: " + object.getString("ROUTE"), false, 2);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            } else if (type == 21) {   //Type for Load Request
                this.outStream.write(this.BoldOn);
                this.outStream.write(this.DoubleWideOn);
                printheaders(getAccurateText("VAN STOCK SUMMARY", 40, 1), false, 2);
                this.outStream.write(this.DoubleWideOff);
                this.outStream.write(this.BoldOff);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.NewLine);
                this.outStream.write(this.BoldOn);
                try {
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Location: " + object.getString("ROUTE"), 50, 0) + getAccurateText("Date: " + object.getString("DOC DATE"), 26, 2), false, 1);
                    this.outStream.write(this.NewLine);
                    printheaders(getAccurateText("Salesman: " + object.getString("SALESMAN"), 50, 0) + getAccurateText("Trip Date: " + object.getString("TRIP START DATE"), 26, 2), false, 1);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.NewLine);
                    this.outStream.write(this.BoldOff);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.count++;
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.outStream != null) {
            try {
                this.outStream.flush();
                this.outStream.close();
                this.outStream = null;
            } catch (IOException e2222) {
                e2222.printStackTrace();
            }
        }
        if (this.btSocket != null && this.btSocket.isConnected()) {
            try {
                this.btSocket.close();
            } catch (IOException e22222) {
                e22222.printStackTrace();
            }
        }
    }
}

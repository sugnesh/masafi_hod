package com.engear.vansales.printer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.engear.vansales.R;
import com.engear.vansales.activities.CollectionActivity;
import com.engear.vansales.activities.FreshUnloadActivity;
import com.engear.vansales.activities.LoadConfirmActivity;
import com.engear.vansales.activities.OrderConfirmActivity;
import com.engear.vansales.activities.PalletReturnActivity;
import com.engear.vansales.activities.PaymentDetailActivity;
import com.engear.vansales.common.AppConstants;
import com.engear.vansales.common.MyApplication;
import com.engear.vansales.localDB.DatabaseHandler;
import com.engear.vansales.model.DevicesData;
import com.engear.vansales.utils.NumberToWord;
import com.engear.vansales.utils.StringUtilities;
import com.engear.vansales.utils.Utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.printer.sdk.PrinterConstants;
import com.printer.sdk.PrinterInstance;
import com.printer.sdk.Table;
import com.woosim.printer.WoosimBarcode;
import com.woosim.printer.WoosimCmd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class PrintLog {

    private BluetoothAdapter mBtAdapter;
    private ArrayList<DevicesData> arrData = new ArrayList<>();
    Context context;
    private Activity activity;
    private JSONObject status;
    private String callbackId;
    private ProgressDialog progressDialog;
    private int retryCount;
    private String sMacAddr;
    String devicename;
    public static PrinterInstance myPrinter;
    private JSONArray jArr;
    private static BluetoothDevice mDevice;
    public static boolean isConnected = false;
    private int count;
    public static WoosimCustomService mWoosim = null;

    public static BluetoothPrintService mPrintService = null;

    public static final int MESSAGE_DEVICE_NAME = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_READ = 3;

    // Key names received from the BluetoothPrintService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private boolean mEmphasis = false;
    private boolean mUnderline = false;
    private int mCharsize = 0;


    DatabaseHandler db;

    public PrintLog(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void execute(String request, JSONArray jsonArray) {
        this.jArr = jsonArray;
        this.arrData = new ArrayList();

        db = new DatabaseHandler(context);
        //setupPrint();
        print();

    }

    public void print() {
        try {
            PrintLog.this.printReports(PrintLog.this.sMacAddr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //new asyncgetDevices().execute(new Void[0]);
    }

    private class asyncgetDevices extends AsyncTask<Void, Set<BluetoothDevice>, Set<BluetoothDevice>> {
        private asyncgetDevices() {
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        protected Set<BluetoothDevice> doInBackground(Void... params) {
            PrintLog.this.mBtAdapter = BluetoothAdapter.getDefaultAdapter();
            PrintLog.this.mBtAdapter.enable();
            if (PrintLog.this.mBtAdapter.isEnabled()) {
                return PrintLog.this.mBtAdapter.getBondedDevices();
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
                        PrintLog.this.arrData.add(d1);
                    }
                    PrintLog.this.showDialog(PrintLog.this.arrData);
                    return;
                }
                Toast.makeText(context, "No Devices Found!", Toast.LENGTH_SHORT).show();
                System.out.println("No devices");
                Log.e("devices", "No devices");

                try {
                    PrintLog.this.status.put("status", false);
                    PrintLog.this.status.put("isconnected", -7);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PrintLog.this.sendUpdate(PrintLog.this.status, true);

                callActivity();

            } catch (Exception e) {
                e.printStackTrace();

                // Crashlytics.logException(e);
            }
        }
    }

    private void sendUpdate(JSONObject obj, boolean keepCallback) {
        if (this.callbackId != null) {
            Log.e("End of plugin", "true");
            // success(new PluginResult(Status.OK, obj), this.callbackId);
        }
    }

    public void showDialog(ArrayList<DevicesData> arrData) {
        String[] arrDevices = new String[arrData.size()];
        for (int i = 0; i < arrData.size(); i++) {
            arrDevices[i] = new StringBuilder(String.valueOf(((DevicesData) arrData.get(i)).getName())).append(StringUtilities.LF).append(((DevicesData) arrData.get(i)).getAddress()).toString();
        }
        activity.runOnUiThread(new C04694(arrDevices, arrData));
    }

    class C04694 implements Runnable {
        private final /* synthetic */ ArrayList val$arrData;
        private final /* synthetic */ String[] val$arrDevices;

        /* renamed from: com.phonegap.sfa.DotmatHelper.4.1 */
        class C04671 implements DialogInterface.OnClickListener {
            private final /* synthetic */ ArrayList val$arrData;

            C04671(ArrayList arrayList) {
                this.val$arrData = arrayList;
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    PrintLog.this.retryCount = 0;
                    PrintLog.this.sMacAddr = ((DevicesData) this.val$arrData.get(which)).getAddress();
                    if (!PrintLog.this.sMacAddr.contains(":") && PrintLog.this.sMacAddr.length() == 12) {
                        char[] cAddr = new char[17];
                        int j = 0;
                        for (int i = 0; i < 12; i += 2) {
                            PrintLog.this.sMacAddr.getChars(i, i + 2, cAddr, j);
                            j += 2;
                            if (j < 17) {
                                int j2 = j + 1;
                                cAddr[j] = ':';
                                j = j2;
                            }

                        }
                        PrintLog.this.sMacAddr = new String(cAddr);
                    }
                    connect2BlueToothdevice(PrintLog.this.sMacAddr);
//                    PrintLog.this.showProgressDialog();
//                    PrintLog.this.doConnectionTest(PrintLog.this.sMacAddr);
                } catch (Exception e) {

                    e.printStackTrace();
                    // Crashlytics.logException(e);
                }
                Toast.makeText(context, " you have selected " + ((DevicesData) this.val$arrData.get(which)).getName(), Toast.LENGTH_SHORT).show();
            }
        }

        /* renamed from: com.phonegap.sfa.DotmatHelper.4.2 */
        class C04682 implements DialogInterface.OnClickListener {
            C04682() {
            }

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                try {
                    //   PrinterHelper.this.status.put("status", false);
                    //   PrinterHelper.this.status.put("isconnected", -1);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                PrintLog.this.sendUpdate(PrintLog.this.status, true);
            }
        }

        C04694(String[] strArr, ArrayList arrayList) {
            this.val$arrDevices = strArr;
            this.val$arrData = arrayList;
        }

        public void run() {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Choose Device To Pair");
            dialog.setItems(this.val$arrDevices, new C04671(this.val$arrData));
            dialog.setPositiveButton("Cancel", new C04682());
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    private void connect2BlueToothdevice(String devicesAddress) {
        showProgressDialog();
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(devicesAddress);
        devicename = mDevice.getName();


        mPrintService.connect(mDevice, true);

        //myPrinter = PrinterInstance.getPrinterInstance(mDevice, mHandler);
//        if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {// 未绑定
//            // IntentFilter boundFilter = new IntentFilter();
//            // boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//            // mContext.registerReceiver(boundDeviceReceiver, boundFilter);
//            PairOrConnect(true);
//        } else {
//            PairOrConnect(false);
//        }
    }

    private void sendData(byte[] data) {
        // Check that we're actually connected before trying printing
        if (mPrintService.getState() != BluetoothPrintService.STATE_CONNECTED) {
            Toast.makeText(context, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (data.length > 0) mPrintService.write(data);
    }

    private void PairOrConnect(boolean pair) {
        if (pair) {
            IntentFilter boundFilter = new IntentFilter();
            boundFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            context.registerReceiver(boundDeviceReceiver, boundFilter);
            boolean success = false;
            try {
                // // 自动设置pin值
                // Method autoBondMethod =
                // BluetoothDevice.class.getMethod("setPin", new Class[] {
                // byte[].class });
                // boolean result = (Boolean) autoBondMethod.invoke(mDevice, new
                // Object[] { "1234".getBytes() });
                // Log.i(TAG, "setPin is success? : " + result);

                // 开始配对 这段代码打开输入配对密码的对话框
                Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
                success = (Boolean) createBondMethod.invoke(mDevice);
                // // 取消用户输入
                // Method cancelInputMethod =
                // BluetoothDevice.class.getMethod("cancelPairingUserInput");
                // boolean cancleResult = (Boolean)
                // cancelInputMethod.invoke(mDevice);
                // Log.i(TAG, "cancle is success? : " + cancleResult);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            Log.i("sdfds", "createBond is success? : " + success);
        } else {
            new connectThread().start();
        }
    }

    void callActivity() {
        if (context instanceof OrderConfirmActivity) {
            OrderConfirmActivity.Companion.callbackFunction("", "");
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

    private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mDevice.equals(device)) {
                    return;
                }
                switch (device.getBondState()) {
                    case BluetoothDevice.BOND_BONDING:
                        Log.i("", "bounding......");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.i("", "bound success");
                        // if bound success, auto init BluetoothPrinter. open
                        // connect.
                        context.unregisterReceiver(boundDeviceReceiver);
                        PrintLog.this.showProgressDialog();
                        // 配对完成开始连接
                        if (myPrinter != null) {
                            new connectThread().start();
                        }
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.i("", "执行顺序----4");
                        context.unregisterReceiver(boundDeviceReceiver);
                        Log.i("", "bound cancel");
                        break;
                    default:
                        break;
                }

            }
        }
    };

    private class connectThread extends Thread {
        @Override
        public void run() {

            PrintLog.this.dismissProgress();

            try {
                PrintLog.this.printReports(PrintLog.this.sMacAddr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

//            if (myPrinter != null) {
//                isConnected = myPrinter.openConnection();
//                PrinterConstants.paperWidth = 724;
//                myPrinter.setPaperWidth(724);
//
//                PrintLog.this.dismissProgress();
//
//                try {
//                    PrintLog.this.printReports(PrintLog.this.sMacAddr);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            } else {
//                PrintLog.this.dismissProgress();
//            }
        }
    }

    @SuppressLint({"NewApi"})
    void printReports(String address) throws JSONException {
        Log.e("Print Report", "" + this.jArr.toString());
        Log.e("Array Length", "" + this.jArr.length());

        try {
            for (int j = 0; j < (this.jArr.length() > 1 ? 1 : this.jArr.length()); j++) {
                JSONArray jInner = this.jArr.getJSONArray(j);
                for (int i = 0; i < jInner.length(); i++) {
                    JSONObject jDict = jInner.getJSONObject(i);
                    String request = jDict.getString(AppConstants.REQUEST);
                    JSONObject jsnData = jDict.getJSONObject("mainArr");
                    this.count = 0;

                    if (request.equals(MyApplication.LOAD_SUMMARY_REQUEST)) {
                        parseLoadSummaryResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.SALES_INVOICE)) {
                        parseSalesInvoiceResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.RETURN_REQUEST)) {
                        parseReturnResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.PALLET_RETURN_REQUEST)) {
                        parsePalletReturnResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.ORDER)) {
                        parseOrderResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.LOAD_REQUEST)) {
                        parseLoadRequestResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.COLLECTION)) {
                        String type = jsnData.getString("Type");
                        if (type.equalsIgnoreCase("Single")) {
                            printSinglePaymentRequest(jsnData, address);
                        }
                    }

                    if (request.equals(AppConstants.VAN_STOCK)) {
                        parseVanSummaryResponse(jsnData, address);
                    }

                    if (request.equals(AppConstants.END_INVENTORY)) {
                        printUnloadEndInventryRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.UNLOAD_EXPIRY_REQUEST)) {
                        printUnloadExpiryRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.UNLOAD_DAMAGED_REQUEST)) {
                        printUnloadDamageRequest(jsnData, address);
                    }

                    if (request.equals(AppConstants.SALES_SUMMARY)) {
                        printSalesSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.SOA_COLLECTION)) {
                        printSOASummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DEPOSITE_SUMMARY)) {
                        printDepositesSummary(jsnData, address);
                    }

                    if (request.equals(AppConstants.DAILY_OTHER_SUMMARY)) {
                        printDailyOtherSummary(jsnData, address);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
//            for (int j = 0; j < this.jArr.length(); j++) {
//                JSONArray jInner = this.jArr.getJSONArray(j);
//                JSONArray jInnerSub = jInner.getJSONArray(0);
//                for (int i = 0; i < jInnerSub.length(); i++) {
//                    JSONObject jDict = jInnerSub.getJSONObject(i);
//                    String request = jDict.getString(AppConstants.REQUEST);
//                    JSONObject jsnData = jDict.getJSONObject("mainArr");
//                    this.count = 0;
//
//                    if (request.equals(MyApplication.LOAD_SUMMARY_REQUEST)) {
//                        parseLoadSummaryResponse(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.SALES_INVOICE)) {
//                        parseSalesInvoiceResponse(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.RETURN_REQUEST)) {
//                        parseReturnResponse(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.ORDER)) {
//                        parseOrderResponse(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.LOAD_REQUEST)) {
//                        parseLoadRequestResponse(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.COLLECTION)) {
//                        String type = jsnData.getString("Type");
//                        if (type.equalsIgnoreCase("Single")) {
//                            printSinglePaymentRequest(jsnData, address);
//                        }
//                    }
//
//                    if (request.equals(AppConstants.VAN_STOCK)) {
//                        parseVanSummaryResponse(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.END_INVENTORY)) {
//                        printUnloadEndInventryRequest(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.UNLOAD_EXPIRY_REQUEST)) {
//                        printUnloadExpiryRequest(jsnData, address);
//                    }
//                    if (request.equals(AppConstants.UNLOAD_DAMAGED_REQUEST)) {
//                        printUnloadDamageRequest(jsnData, address);
//                    }
//
//
//                    if (request.equals(AppConstants.SALES_SUMMARY)) {
//                        printSalesSummary(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.DEPOSITE_SUMMARY)) {
//                        printDepositesSummary(jsnData, address);
//                    }
//
//                    if (request.equals(AppConstants.DAILY_OTHER_SUMMARY)) {
//                        printDailyOtherSummary(jsnData, address);
//                    }
//
//                }
//            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // myPrinter.closeConnection();
        isConnected = false;


    }

    private Handler mHandler = new Handler() {
        @SuppressLint("ShowToast")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(context, "Connected to " + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context, msg.getData().getInt(TOAST), Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    mWoosim.processRcvData((byte[]) msg.obj, msg.arg1);
                    break;
                case WoosimCustomService.MESSAGE_PRINTER:
                    if (msg.arg1 == WoosimCustomService.MSR) {
                        if (msg.arg2 == 0) {
                            Toast.makeText(context, "MSR reading failure", Toast.LENGTH_SHORT).show();
                        } else {
                            byte[][] track = (byte[][]) msg.obj;
                            if (track[0] != null) {
                                String str = new String(track[0]);
                                //      mTrack1View.setText(str);
                            }
                            if (track[1] != null) {
                                String str = new String(track[1]);
                                //     mTrack2View.setText(str);
                            }
                            if (track[2] != null) {
                                String str = new String(track[2]);
                                //     mTrack3View.setText(str);
                            }
                        }
                    }
                    break;
            }

        }

    };

    private void showProgressDialog() {
        try {
            this.progressDialog = ProgressDialog.show(context, "Please Wait", "Connecting to printer..", false);
        } catch (Exception e) {
            e.printStackTrace();
            // Crashlytics.logException(e);
        }
    }

    private void dismissProgress() {
        try {
            if (this.progressDialog != null && this.progressDialog.isShowing()) {
                this.progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Crashlytics.logException(e);
        }

    }


    //Invoice Summary Print
    void parseSalesInvoiceResponse(JSONObject object, String args) {

        String mTypeS = "";
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());


        try {
            writeText("TAX INVOICE", mEmphasis, false, 1, 1);

            if (object.getString("invoiceType").equals("CASH")) {
                writeText("CASH INVOICE", mEmphasis, false, 1, 1);
            } else {
                writeText("CREDIT INVOICE", mEmphasis, false, 1, 1);
            }


            HashMap<String, String> mParent = db.getCustomerParentCodeDetail(object.getString("CUSTOMERID"));

//            writeText("Bill To : " + mParent.get("Name"), mEmphasis, false, mCharsize, 0);
//            writeText("Ship To: " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);

            writeText("Bill To : " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);
            writeText("Ship To: " + object.getString("ADDRESS"), mEmphasis, false, mCharsize, 0);

            String isHOD = "0";
            if (object.has("isHOD")) {
                isHOD = object.getString("isHOD");
            }

            int columnWidth = 34; // Adjust this value based on your layout

            if (object.getString("isSales").equals("1")) {
                mTypeS = "sales";
                // Customer Code and Site No
                String customerSiteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Customer Code: " + mParent.get("ParentCode"), "Site No: " + object.getString("CUSTOMERID"));
                writeText(customerSiteLine, mEmphasis, false, mCharsize, 0);


                String trnRouteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "TRN: " + object.getString("TRN"), "Route Code: " + object.getString("ROUTE"));
                writeText(trnRouteLine, mEmphasis, false, mCharsize, 0);


                // Delivered By and Invoice Date
                String deliveredInvoiceLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Invoice ID: " + object.getString("DOCUMENT NO"), "Invoice Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"));
                writeText(deliveredInvoiceLine, mEmphasis, false, mCharsize, 0);

                String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Delivered by: " + object.getString("SALESMAN"), "Salesman: " + object.getString("SALESMAN"));
                writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);

            } else {
                mTypeS = "delivery";
                // Customer Code and Site No
                String customerSiteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Customer Code: " + object.getString("ParentCode"), "Site No: " + object.getString("CUSTOMERID"));
                writeText(customerSiteLine, mEmphasis, false, mCharsize, 0);

                String trnRouteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "EMP Code: " + object.getString("SALESMAN_CODE"), "TRN: " + object.getString("TRN"));
                writeText(trnRouteLine, mEmphasis, false, mCharsize, 0);


                String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Delivered by: " + object.getString("SALESMAN"), "LPO: " + object.getString("LPO"));
                writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);

                // Delivered By and Invoice Date
                String deliveredInvoiceLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Invoice ID: " + object.getString("DOCUMENT NO"), "Invoice Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"));
                writeText(deliveredInvoiceLine, mEmphasis, false, mCharsize, 0);

                // TRN and Route Code
                String orderId = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Order Id: " + object.getString("OrderNo"), "Delivery Date: " + object.getString("Delivery Date"));
                writeText(orderId, mEmphasis, false, mCharsize, 0);

                // Delivered By and Invoice Date
                String presaler = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Route Code: " + object.getString("PresellerId"), "Salesman Name: " + object.getString("PresellerName"));
                writeText(presaler, mEmphasis, false, mCharsize, 0);
            }


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item Code   RATE   G.AMT  DISC  DISC   Excise  Taxable  VAT   AMOUNT", mEmphasis, false, mCharsize, 0);
            writeText("UOM/Description            %                    Amt     @5%         ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            double totalCase = 0.0;
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);

                //cf
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(3)));
                String mUom = jArr.getString(2);
                String price = String.format("%.2f", Double.parseDouble(jArr.getString(4)));
                String excs = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String discPer = String.format("%.2f", Double.parseDouble(jArr.getString(7)));
                String disc = String.format("%.2f", Double.parseDouble(jArr.getString(9)));
                String vat = String.format("%.2f", Double.parseDouble(jArr.getString(11)));
                String net = String.format("%.2f", Double.parseDouble(jArr.getString(10)));
                String Total = String.format("%.2f", Double.parseDouble(jArr.getString(12)));
                String grossPrice = String.format("%.2f", Double.parseDouble(jArr.getString(5)));
                String excisePrice = String.format("%.2f", Double.parseDouble(jArr.getString(6)));


                String barcode = jArr.getString(13);
                String plu = jArr.getString(14);
                String mBarPLu = barcode + "     " + plu;
                String batchNum = "";
                String expiry = "";
                if (jArr.getString(15) != null) {
                    batchNum = jArr.getString(15);
                }

                if (jArr.getString(16) != null) {
                    expiry = jArr.getString(16);
                }

                String totalPrice = String.valueOf((Float.parseFloat(price) + Float.parseFloat(excisePrice)));

                if (Double.parseDouble(disc) > 0) {
                    discPer = "" + ((Double.parseDouble(disc) / Double.parseDouble(grossPrice)) * 100);
                }
                totalCase = totalCase + Double.parseDouble(qty);

                String mCode = db.getItemCodeFromName(name);

                String ItemCode = mCode;
                String UOM = mUom;
                String Description = name;
                String getQty = qty;
                String getRate = price;
                String getGrossAmt = grossPrice;
                String getDiscPer = discPer;
                String getDisc = disc;
                String getExcise = excisePrice;
                String getVatFivePer = vat;
                String getTaxableAmount = net;
                String getAmount = Total;


                // totalCase = totalCase + Double.parseDouble(qty);


                String Rate;
                String GrossAmt;
                String DiscPer;
                String Disc;
                String Excise;
                String VatFivePer;
                String TaxableAmount;
                String Amount;


                String Rate2 = "";
                String GrossAmt2 = "";
                String DiscPer2 = "";
                String Disc2 = "";
                String Excise2 = "";
                String VatFivePer2 = "";
                String TaxableAmount2 = "";
                String Amount2 = "";

                String Rate3 = "";
                String GrossAmt3 = "";
                String DiscPer3 = "";
                String Disc3 = "";
                String Excise3 = "";
                String VatFivePer3 = "";
                String TaxableAmount3 = "";
                String Amount3 = "";


                if (getRate.length() <= 5) {
                    Rate = getRate;
                } else if (getRate.length() > 5 && getRate.length() <= 10) {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5);
                } else {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5, 10);
                    Rate3 = getRate.substring(10);
                }


                if (getGrossAmt.length() <= 5) {
                    GrossAmt = getGrossAmt;
                } else if (getGrossAmt.length() > 5 && getGrossAmt.length() <= 10) {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5);
                } else {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5, 10);
                    GrossAmt3 = getGrossAmt.substring(10);
                }

                if (getDiscPer.length() <= 4) {
                    DiscPer = getDiscPer;
                } else if (getDiscPer.length() > 4 && getDiscPer.length() <= 8) {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4);
                } else {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4, 8);
                    DiscPer3 = getDiscPer.substring(8);
                }


                if (getDisc.length() <= 4) {
                    Disc = getDisc;
                } else if (getDisc.length() > 4 && getDisc.length() <= 8) {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4);
                } else {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4, 8);
                    Disc3 = getDisc.substring(8);
                }


                if (getExcise.length() <= 6) {
                    Excise = getExcise;
                } else if (getExcise.length() > 6 && getExcise.length() <= 12) {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6);
                } else {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6, 12);
                    Excise3 = getExcise.substring(12);
                }


                if (getVatFivePer.length() <= 4) {
                    VatFivePer = getVatFivePer;
                } else if (getVatFivePer.length() > 4 && getVatFivePer.length() <= 8) {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4);
                } else {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4, 8);
                    VatFivePer3 = getVatFivePer.substring(8);
                }

                if (getTaxableAmount.length() <= 7) {
                    TaxableAmount = getTaxableAmount;
                } else if (getTaxableAmount.length() > 7 && getTaxableAmount.length() <= 14) {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7);
                } else {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7, 14);
                    TaxableAmount3 = getTaxableAmount.substring(14);
                }


                if (getAmount.length() <= 6) {
                    Amount = getAmount;
                } else if (getAmount.length() > 6 && getAmount.length() <= 12) {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6);
                } else {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6, 12);
                    Amount3 = getAmount.substring(12);
                }


                String formattedLine1 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", ItemCode, "", Rate, GrossAmt, DiscPer, Disc, Excise, TaxableAmount, VatFivePer, Amount);
                String formattedLine2 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", "", "", Rate2, GrossAmt2, DiscPer2, Disc2, Excise2, TaxableAmount2, VatFivePer2, Amount2);
                String formattedLine3 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", "", "", Rate3, GrossAmt3, DiscPer3, Disc3, Excise3, TaxableAmount3, VatFivePer3, Amount3);

                writeText(formattedLine1, mEmphasis, false, mCharsize, 0);

                if (!Rate2.isEmpty() || !DiscPer2.isEmpty() || !Disc2.isEmpty() ||
                        !Excise2.isEmpty() || !TaxableAmount2.isEmpty() || !VatFivePer2.isEmpty() || !Amount2.isEmpty()) {


                    writeText(formattedLine2, mEmphasis, false, mCharsize, 0);
                }


                if (!Rate3.isEmpty() || !DiscPer3.isEmpty() || !Disc3.isEmpty() ||
                        !Excise3.isEmpty() || !TaxableAmount3.isEmpty() || !VatFivePer3.isEmpty() || !Amount3.isEmpty()) {


                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                }


                writeText("(UOM:" + getQty + " " + UOM + ")" + " / " + Description, mEmphasis, false, mCharsize, 0);
                writeText("Batch No: " + batchNum + " " + "Expiry: " + expiry, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            int labelWidth = 15; // Adjust this based on your desired width
            int numSpaces = labelWidth - "Total Cases:".length();

            // Create the spacing string
            String spacing = new String(new char[numSpaces]).replace('\0', ' ');

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            String grossTotal = "", vatAmt = "", netAmt = "", excise = "", discount = "", total = "", mFinalGross = "", deliveryCharge = "", mRewards = "";
            if (jTOBject.has("Gross Total")) {
                grossTotal = jTOBject.getString("Gross Total");
            }

            if (jTOBject.has("VAT")) {
                vatAmt = String.format("%.2f", Double.parseDouble(jTOBject.getString("VAT")));
            }

            if (jTOBject.has("NET")) {
                netAmt = String.format("%.2f", Double.parseDouble(jTOBject.getString("NET")));
            }

            if (jTOBject.has("EXCISE")) {
                excise = String.format("%.2f", Double.parseDouble(jTOBject.getString("EXCISE")));
            }

            if (jTOBject.has("Discount")) {
                discount = String.format("%.2f", Double.parseDouble(jTOBject.getString("Discount")));
            }

            if (jTOBject.has("Rewards")) {
                mRewards = String.format("%.2f", Double.parseDouble(jTOBject.getString("Rewards")));
            }

            if (jTOBject.has("Delivery Charge")) {
                deliveryCharge = String.format("%.2f", Double.parseDouble(jTOBject.getString("Delivery Charge")));
            }

            if (jTOBject.has("Total Amount")) {
                total = String.format("%.2f", Double.parseDouble(jTOBject.getString("Total Amount")));
            }

            mFinalGross = String.format("%.2f", (Double.parseDouble(netAmt) + Double.parseDouble(discount)));

            String mDType = "";
            if (object.has("dType")) {
                mDType = object.getString("dType");
            }

            String TotalDeCharge = "";
            if (mDType.equals("Express")) {
                TotalDeCharge = String.format("%s%-50s%s", spacing, "Express Delivery Charge:", "AED" + alignRight(deliveryCharge, 10));
            } else if (mDType.equals("Instashop")) {
                TotalDeCharge = String.format("%s%-50s%s", spacing, "Instashop Service Charge:", "AED" + alignRight(deliveryCharge, 10));
            }

            String formattedTotalCase = String.format("%.2f", totalCase);
            String TotalCases = String.format("%s%-53s%s", spacing, "Total Cases:", alignRight(formattedTotalCase, 10));
            String NetAmount = String.format("%s%-50s%s", spacing, "Net Amount:", "AED" + alignRight(netAmt, 10));
            String TotalExcise = String.format("%s%-50s%s", spacing, "Total Excise:", "AED" + alignRight(excise, 10));
            String TotalRewards = String.format("%s%-50s%s", spacing, "Loyalty Points:", "AED" + alignRight(mRewards, 10));
            String NetTaxableAmount = String.format("%s%-50s%s", spacing, "Net Taxable Amount:", "AED" + alignRight(netAmt, 10));
            String TotalVat = String.format("%s%-50s%s", spacing, "TotalVat:", "AED" + alignRight(vatAmt, 10));

            String TotalGross = String.format("%s%-50s%s", spacing, "Total Gross:", "AED" + alignRight(total, 10));

            String totalGrossWords = "";
            String Amount = total;
            int decimals = Amount.indexOf(".");
            String decimalValue = null;
            if (0 <= decimals) {
                decimalValue = Amount.substring(decimals + 1);
                Amount = Amount.substring(0, decimals).replaceAll("-", "");


                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    Long dataDecimal = Long.parseLong(decimalValue);
                    strWord = NumberToWord.convert(data1) + " and " + NumberToWord.convert(dataDecimal) + " fils";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            } else {

                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    strWord = NumberToWord.convert(data1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            }

// Convert the total gross to words
            //String totalGrossWords = converter.convertToWords(Double.parseDouble(total));

            if (mDType.equals("Express")) {
                writeText(TotalDeCharge, mEmphasis, false, mCharsize, 0);
            } else if (mDType.equals("Instashop")) {
                writeText(TotalDeCharge, mEmphasis, false, mCharsize, 0);
            }
            writeText(TotalCases, mEmphasis, false, mCharsize, 0);
            writeText(NetAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalExcise, mEmphasis, false, mCharsize, 0);
            writeText(TotalRewards, mEmphasis, false, mCharsize, 0);
            writeText(NetTaxableAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalVat, mEmphasis, false, mCharsize, 0);
            writeText(TotalGross, mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Amount In Words: " + totalGrossWords + "", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("DISCLAIMER: The customer acknowledges and agrees that the purchased", mEmphasis, false, mCharsize, 0);
            writeText("         goods are intended for resale exclusively to end consumers.", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("REMARKS:", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            writeText("1. Received complete invoice quantity in good condition.", mEmphasis, false, mCharsize, 0);
            writeText("2. Official Receipt is mandatory for payments.", mEmphasis, false, mCharsize, 0);
            writeText("3. Please issue cheque on behalf of Masafi Co. LLC.", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Bank Name : ADCB", mEmphasis, false, mCharsize, 0);

            int columnWidth1 = 5;

            int columnWidth2 = 12;

            int columnWidth3 = 20;

            int columnWidth4 = 6;

            String bankDetailsHeader = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "Reg", "Acc No", "  IBAN No", "        Bank");

            String bankDetailsRow1 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "DXB", "345948020002", "  AE580030000345948020002", "  MASAFI CO.DUBAI");
            String bankDetailsRow1p2 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "", "", "", "        COLLECTION A/C");

            String bankDetailsRow2 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "AUH", "345948020003", "  AE310030000345948020003", "  MASAFI CO.ABU DHABI");
            String bankDetailsRow2p2 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "", "", "", "        COLLECTION A/C");

            String bankDetailsRow3 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "ALN", "345948020004", "  AE040030000345948020004", "  MASAFI CO.ALAIN");
            String bankDetailsRow3p2 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "", " ", "", "        COLLECTION A/C");

            String bankDetailsRow4 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "FJR", "345948020005", "  AE740030000345948020005", "  MASAFI CO.FUJAIRAH");
            String bankDetailsRow4p2 = String.format("%-" + columnWidth1 + "s %-" + columnWidth2 + "s %-" + columnWidth3 + "s %-" + columnWidth4 + "s",
                    "", "  ", "  ", "        COLLECTION A/C");
//
            writeText(bankDetailsHeader, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow1, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow1p2, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow2, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow2p2, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow3, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow3p2, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow4, mEmphasis, false, mCharsize, 0);
            writeText(bankDetailsRow4p2, mEmphasis, false, mCharsize, 0);


            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("4. Wooden Pallets / Chep Pallets / Gallon Racks / Base Pallets mentioned in the invoice above belongs to Masafi.", mEmphasis, false, mCharsize, 0);
            writeText("5. Wooden Pallets / Chep Pallets / Gallon Racks that were not returned within 30 days will be charged to its full amount.", mEmphasis, false, mCharsize, 0);
            writeText("6. Rebate/statement discount/adnoc rebate should be deducted on net sales excluding excise and Vat ", mEmphasis, false, mCharsize, 0);

//            BitmapFactory.Options optionsbank = new BitmapFactory.Options();
//            optionsbank.inScaled = false;
//            Bitmap bmpbank = BitmapFactory.decodeResource(context.getResources(), R.drawable.remarksmasafilogoff, optionsbank);
//            if (bmpbank == null) {
//                Log.e("TAG", "resource decoding is failed");
//                return;
//            }
//
//            byte[] databank = WoosimCustomImage.printBitmap(0, 0, 1300, 400, bmpbank);
//            bmpbank.recycle();  // Recycle the bitmap after use
//
//            sendData(WoosimCmd.setPageMode());
//            sendData(databank);
//
//            sendData(WoosimCmd.PM_setStdMode());


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("YOU HAVE PRINTED THIS INVOICE AT THE FOLLOWING GEO CODES :-", mEmphasis, false, mCharsize, 0);
            writeText("Latitude: " + AppConstants.Latitude, mEmphasis, false, mCharsize, 0);
            writeText("Longitude: " + AppConstants.Longitude, mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            writeText("                           Thank You", mEmphasis, false, mCharsize, 0);
            writeText("You can submit your concern anonymously to concern@masafi.com", mEmphasis, false, mCharsize, 0);
            BitmapFactory.Options optionsSocial = new BitmapFactory.Options();
            optionsSocial.inScaled = false;
            Bitmap bmpSocial = BitmapFactory.decodeResource(context.getResources(), R.drawable.social1, optionsSocial);
            if (bmpSocial == null) {
                Log.e("", "resource decoding is failed");
                return;
            }


            byte[] dataSocial = WoosimCustomImage.printColorBitmap(0, 0, 850, 150, bmpSocial);
            bmpSocial.recycle();

            sendData(WoosimCmd.setPageMode());
            sendData(dataSocial);
            sendData(WoosimCmd.PM_setStdMode());
//        writeText("                     - www.facebook.com/Masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.instagram.com/masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.linkedin.com/company/masafi/\n\n", mEmphasis, false, mCharsize, 0);

            String signImage = "";
            if (object.has("mSign")) {
                signImage = object.getString("mSign");
            }


            if (signImage.isEmpty()) {
                writeText("      Customer Signature & Stamp        For MASAFI CO. (L.L.C.)        ", mEmphasis, false, mCharsize, 0);
                writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
                writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
                writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
                writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
                writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            } else {
                writeText("      Customer Signature & Stamp        For MASAFI CO. (L.L.C.)        ",
                        mEmphasis, false, mCharsize, 0);
                writeText("     ----------------------------- -----------------------------    ",
                        mEmphasis, false, mCharsize, 0);
                writeText("     |                           | |                           |    ",
                        mEmphasis, false, mCharsize, 0);


                Bitmap sign = resizeForMasafiSignature(getBitmapFromFile(signImage));

                byte[] signData = WoosimCustomImage.printColorBitmap(
                        80,      // X position (left)
                        0,      // Y position
                        sign.getWidth(),
                        sign.getHeight(),
                        sign
                );

                sendData(WoosimCmd.setPageMode());
                sendData(signData);
                sendData(WoosimCmd.PM_setStdMode());

                sign.recycle();

                writeText("     |                           | |                           |    ",
                        mEmphasis, false, mCharsize, 0);

                writeText("     |                           | |                           |    ",
                        mEmphasis, false, mCharsize, 0);

                writeText("     ----------------------------- -----------------------------    ",
                        mEmphasis, false, mCharsize, 0);

            }

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);


            BarcodeFormat format = BarcodeFormat.CODE_128;
            int width = 400;
            int height = 200;

            // Generate barcode bitmap
            Bitmap barcodeBitmap = generateBarcode(object.getString("DOCUMENT NO"), format, width, height);

            // Print the barcode using Woosim printer
            if (barcodeBitmap != null) {
                int pageWidth = 780; // Set your page width here
                int barcodeWidth = barcodeBitmap.getWidth();
                int startX = (pageWidth - barcodeWidth) / 2;

                // Print the centered barcode
                byte[] dataBarcode = WoosimCustomImage.printColorBitmap(startX, 0, barcodeWidth, 150, barcodeBitmap);
                barcodeBitmap.recycle();

                sendData(WoosimCmd.setPageMode());
                sendData(dataBarcode);
                sendData(WoosimCmd.PM_setStdMode());
            }

//            final byte[] data1 = {0x30, 0x30, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30};
//            final byte[] cmd_print = WoosimCustomCmd.printData();
//
//            byte[] gs4 = WoosimBarcode.createGS1Databar(4, 2, data1);
//
//            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
//
//            // Calculate the amount of padding needed to center the barcode
//            int totalWidth = 70; // Total width of the printed area (adjust as needed)
//            int barcodeWidth = gs4.length; // Width of the barcode
//            int padding = (totalWidth - barcodeWidth) / 2;
//
//            // Write padding before the barcode data
//            for (int i = 0; i < padding; i++) {
//                byteStream.write(0x20); // Assuming 0x20 is the space character
//            }
//
//            // Write the barcode data
//            byteStream.write(gs4);
//            byteStream.write(cmd_print);
//
//            sendData(WoosimCmd.initPrinter());
//            sendData(byteStream.toByteArray());
//            sendData(WoosimCmd.PM_setStdMode());

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            if (context instanceof OrderConfirmActivity) {
                OrderConfirmActivity.Companion.callbackFunction(mTypeS, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Bitmap getBitmapFromFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1; // increase if image is large

        return BitmapFactory.decodeFile(filePath, options);
    }

    private Bitmap resizeForMasafiSignature(Bitmap bitmap) {
        int targetWidth = 100; // left box width

        float ratio = (float) targetWidth / bitmap.getWidth();
        int height = (int) (bitmap.getHeight() * ratio);

        return Bitmap.createScaledBitmap(bitmap, targetWidth, height, true);
    }

    private static String alignRight(String s, int width) {
        return String.format("%" + width + "s", s);
    }

    //Receipt Summary Print
    void printSinglePaymentRequest(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            String downPayment = "No";
            if (object.has("OnAccount")) {
                downPayment = object.getString("OnAccount");
            }

            if (object.getString("collectionType").equals("Card")) {
                if (downPayment.equalsIgnoreCase("No")) {
                    writeText("PAYMENT RECEIPT / TAX INVOICE", mEmphasis, false, 1, 1);
                } else {
                    writeText("DOWN PAYMENT RECEIPT", mEmphasis, false, 1, 1);
                }
            } else {
                if (downPayment.equalsIgnoreCase("No")) {
                    writeText("PAYMENT RECEIPT", mEmphasis, false, 1, 1);
                } else {
                    writeText("DOWN PAYMENT RECEIPT", mEmphasis, false, 1, 1);
                }
            }


            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Customer Name : " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);

            String parentCode = "";
            if (object.has("ParentCode")) {
                parentCode = object.getString("ParentCode");
            }

            int columnWidth = 34; // Adjust this value based on your layout

            String customerSiteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Customer Code: " + parentCode, "Site No: " + object.getString("CUSTOMERID"));
            writeText(customerSiteLine, mEmphasis, false, mCharsize, 0);


            String trnRouteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Payment Type: " + object.getString("collectionType"), "Route Code: " + object.getString("ROUTE"));
            writeText(trnRouteLine, mEmphasis, false, mCharsize, 0);


            // Delivered By and Invoice Date
            String deliveredInvoiceLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Receipt No: " + object.getString("DOCUMENT NO"), "Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"));
            writeText(deliveredInvoiceLine, mEmphasis, false, mCharsize, 0);

            String chequeNo = "";
            if (object.has("chequeNo")) {
                chequeNo = object.getString("chequeNo");
            } else {
                chequeNo = "";
            }
            String chequeDate = "";
            if (object.has("chequeDate")) {
                chequeDate = object.getString("chequeDate");
            } else {
                chequeDate = "";
            }

            String bank = "";
            if (object.has("Bank")) {
                bank = object.getString("Bank");
            } else {
                bank = "";
            }

            if (downPayment.equalsIgnoreCase("No")) {

                if (object.getString("collectionType").equals("Card")) {

                    String authCode = object.getString("cardRefrenceId");
//                    String lastFourId = object.getString("lastFour");
//                    String cTypelId = object.getString("CTYPE");
//                    String terminalId = object.getString("terminalId");
                    String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Auth Code: " + authCode, " ");
                    writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);
//                    String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Auth Code: " + authCode, "Card No: " + lastFourId);
//                    writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);
//                    String merchantId = object.getString("merchantId");
//                    String merchantbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Merchant Id: " + merchantId, "Card Type: " + cTypelId);
//                    writeText(merchantbyLine, mEmphasis, false, mCharsize, 0);
//                    String trancstionId = object.getString("transactionId");
//                    String tranbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Bank Order Id: " + trancstionId, "Terminal Id: " + terminalId);
//                    writeText(tranbyLine, mEmphasis, false, mCharsize, 0);

                } else {

                    String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Cheque No: " + chequeNo, "Bank Name: " + bank);
                    writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);

                    String dedateLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Cheque Date: " + chequeDate, "          ");
                    writeText(dedateLine, mEmphasis, false, mCharsize, 0);
                }


                if (object.getString("mPtype").equals("topup")) {
                    writeText(": ICASH REFILL PAYMENT", mEmphasis, false, 1, 1);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Account No.               RCHARG AMT         BONUS         TOTAL AMT", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    String iCashAccount = object.getString("iCashAccount");
                    String totalBalance = object.getString("totalBalance");
                    String bonus = object.getString("bonus");
                    String paidAmt = object.getString("Topupvalue");
                    double totalAmt = (Double.parseDouble(paidAmt) + Double.parseDouble(bonus));

                    String formattedLine3 = String.format("%-27s %-10s %-15s %-10s", iCashAccount, paidAmt, bonus, totalAmt);
                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                    if (object.getString("collectionType").equals("Card")) {
                        double mTotal = Double.parseDouble(paidAmt);
                        String mTotalStr = String.format("%.2f", mTotal);

                        String formattedLine5 = String.format("%-11s %-24s %-12s %-10s %-10s", "", "Total:", "", mTotalStr, "");
                        writeText(formattedLine5, mEmphasis, false, mCharsize, 0);
                    }

                    writeText("Icash Balance available(AED) : " + totalBalance, mEmphasis, false, mCharsize, 0);

                } else {
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Site Code   Invoice No       Inv Amt   O/S Amt   Paid Amt    Bal Due", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);

                    double mTotalInv = 0.0, mTotalPen = 0.0, mAppliyAmt = 0.0, mbalcneAmr = 0.0;
                    for (int i = 0; i < jData.length(); i++) {

                        JSONArray jArr = jData.getJSONArray(i);
                        String siteCode = object.getString("CUSTOMERID");
                        String invNo = jArr.getString(1);
                        String amount = jArr.getString(2);
                        String pending = String.format("%.2f", Double.parseDouble(jArr.getString(3)));
                        String revAmt = String.format("%.2f", Double.parseDouble(jArr.getString(4)));

                        mTotalPen += Double.parseDouble(pending);
                        mTotalInv += Double.parseDouble(amount);
                        mAppliyAmt += Double.parseDouble(revAmt);
                        double balce = Double.parseDouble(new DecimalFormat("##.##").format(Double.parseDouble(pending) - Double.parseDouble(revAmt)));
                        String mfinalbal = String.format("%.2f", balce);
                        mbalcneAmr += balce;

                        String formattedLine3 = String.format("%-11s %-16s %-10s %-10s %-10s %-10s", siteCode, invNo, amount, pending, revAmt, mfinalbal);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    String formattedLine3 = String.format("%-11s %-16s %-10s %-10s %-10s %-10s", "", "", mTotalInv, mTotalPen, mAppliyAmt, mbalcneAmr);
                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                    if (object.getString("collectionType").equals("Card")) {
                        double mTotal = mAppliyAmt;
                        String mTotalStr = String.format("%.2f", mTotal);

                        String formattedLine5 = String.format("%-11s %-24s %-12s %-10s %-10s", "", "Total:", "", mTotalStr, "");
                        writeText(formattedLine5, mEmphasis, false, mCharsize, 0);
                    }
                }

            } else {

                if (object.getString("collectionType").equals("Card")) {

                    String authCode = object.getString("cardRefrenceId");
//                    String lastFourId = object.getString("lastFour");
//                    String cTypelId = object.getString("CTYPE");
//                    String terminalId = object.getString("terminalId");
                    String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Auth Code: " + authCode, " ");
                    writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);
//                    String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Auth Code: " + authCode, "Card No: " + lastFourId);
//                    writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);
//                    String merchantId = object.getString("merchantId");
//                    String merchantbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Merchant Id: " + merchantId, "Card Type: " + cTypelId);
//                    writeText(merchantbyLine, mEmphasis, false, mCharsize, 0);
//                    String trancstionId = object.getString("transactionId");
//                    String tranbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Bank Order Id: " + trancstionId, "Terminal Id: " + terminalId);
//                    writeText(tranbyLine, mEmphasis, false, mCharsize, 0);

                }

                JSONArray jTotal = object.getJSONArray("TOTAL");
                JSONObject jTOBject = jTotal.getJSONObject(0);

                String grossTotal = "0.0";
                if (jTOBject.has("Total Amount")) {
                    grossTotal = jTOBject.getString("Total Amount");
                }

                writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                writeText("Cheque No          Cheque Date          Bank            Amount      ", mEmphasis, false, mCharsize, 0);
                writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                String formattedLine3 = String.format("%-17s %-17s %-17s %-17s", chequeNo, chequeDate, bank, grossTotal);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);

                if (object.getString("collectionType").equals("Card")) {

                    double mTotal = Double.parseDouble(grossTotal);

                    String mTotalStr = String.format("%.2f", mTotal);

                    String formattedLine5 = String.format("%-17s %-17s %-17s %-17s", "Total:", "", "", mTotalStr);
                    writeText(formattedLine5, mEmphasis, false, mCharsize, 0);
                }
            }

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("YOU HAVE PRINTED THIS RECEIPT AT THE FOLLOWING GEO CODES :-", mEmphasis, false, mCharsize, 0);
            writeText("Latitude: " + AppConstants.Latitude, mEmphasis, false, mCharsize, 0);
            writeText("Longitude: " + AppConstants.Longitude, mEmphasis, false, mCharsize, 0);
            writeText("                           ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            writeText("                           Thank You", mEmphasis, false, mCharsize, 0);
            writeText("You can submit your concern anonymously to concern@masafi.com", mEmphasis, false, mCharsize, 0);
            BitmapFactory.Options optionsSocial = new BitmapFactory.Options();
            optionsSocial.inScaled = false;
            Bitmap bmpSocial = BitmapFactory.decodeResource(context.getResources(), R.drawable.social1, optionsSocial);
            if (bmpSocial == null) {
                Log.e("", "resource decoding is failed");
                return;
            }


            byte[] dataSocial = WoosimCustomImage.printColorBitmap(0, 0, 850, 150, bmpSocial);
            bmpSocial.recycle();

            sendData(WoosimCmd.setPageMode());
            sendData(dataSocial);
            sendData(WoosimCmd.PM_setStdMode());

            writeText("      Customer Signature & Stamp        For MASAFI CO. (L.L.C.)        ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            if (context instanceof PaymentDetailActivity) {
                PaymentDetailActivity.Companion.callbackFunction("");
            }

            if (context instanceof CollectionActivity) {
                CollectionActivity.Companion.callbackFunction("");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Order Summary Print
    void parseOrderResponse(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {


            writeText("ORDER REQUEST", mEmphasis, false, 1, 1);


            HashMap<String, String> mParent = db.getCustomerParentCodeDetail(object.getString("CUSTOMERID"));

            writeText("Bill To : " + mParent.get("Name"), mEmphasis, false, mCharsize, 0);
            writeText("Ship To: " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);

            int columnWidth = 34; // Adjust this value based on your layout

            String customerSiteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Customer Code: " + mParent.get("ParentCode"), "Site No: " + object.getString("CUSTOMERID"));
            writeText(customerSiteLine, mEmphasis, false, mCharsize, 0);


            String trnRouteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "TRN: " + object.getString("TRN"), "Route Code: " + object.getString("ROUTE"));
            writeText(trnRouteLine, mEmphasis, false, mCharsize, 0);


            // Delivered By and Invoice Date
            String deliveredInvoiceLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Order ID: " + object.getString("DOCUMENT NO"), "Order Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"));
            writeText(deliveredInvoiceLine, mEmphasis, false, mCharsize, 0);

            String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Created by: " + object.getString("SALESMAN"), "Salesman: " + object.getString("SALESMAN"));
            writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item Code   RATE   G.AMT  DISC  DISC   Excise  Taxable  VAT   AMOUNT", mEmphasis, false, mCharsize, 0);
            writeText("UOM/Description            %                    Amt     @5%         ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            double totalCase = 0.0;
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);


                //cf
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(3)));
                String mUom = jArr.getString(2);
                String price = String.format("%.2f", Double.parseDouble(jArr.getString(4)));
                String excs = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String discPer = String.format("%.2f", Double.parseDouble(jArr.getString(7)));
                String disc = String.format("%.2f", Double.parseDouble(jArr.getString(9)));
                String vat = String.format("%.2f", Double.parseDouble(jArr.getString(11)));
                String net = String.format("%.2f", Double.parseDouble(jArr.getString(10)));
                String Total = String.format("%.2f", Double.parseDouble(jArr.getString(12)));
                String grossPrice = String.format("%.2f", Double.parseDouble(jArr.getString(5)));
                String excisePrice = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String barcode = jArr.getString(13);
                String plu = jArr.getString(14);
                String mBarPLu = barcode + "     " + plu;
                String totalPrice = String.valueOf((Float.parseFloat(price) + Float.parseFloat(excisePrice)));

                String mCode = db.getItemCodeFromName(name);

                String ItemCode = mCode;
                String UOM = mUom;
                String Description = name;
                String getQty = qty;
                String getRate = price;
                String getGrossAmt = grossPrice;
                String getDiscPer = discPer;
                String getDisc = disc;
                String getExcise = excisePrice;
                String getVatFivePer = vat;
                String getTaxableAmount = net;
                String getAmount = Total;


                totalCase = totalCase + Double.parseDouble(qty);


                String Rate;
                String GrossAmt;
                String DiscPer;
                String Disc;
                String Excise;
                String VatFivePer;
                String TaxableAmount;
                String Amount;


                String Rate2 = "";
                String GrossAmt2 = "";
                String DiscPer2 = "";
                String Disc2 = "";
                String Excise2 = "";
                String VatFivePer2 = "";
                String TaxableAmount2 = "";
                String Amount2 = "";

                String Rate3 = "";
                String GrossAmt3 = "";
                String DiscPer3 = "";
                String Disc3 = "";
                String Excise3 = "";
                String VatFivePer3 = "";
                String TaxableAmount3 = "";
                String Amount3 = "";


                if (getRate.length() <= 5) {
                    Rate = getRate;
                } else if (getRate.length() > 5 && getRate.length() <= 10) {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5);
                } else {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5, 10);
                    Rate3 = getRate.substring(10);
                }


                if (getGrossAmt.length() <= 5) {
                    GrossAmt = getGrossAmt;
                } else if (getGrossAmt.length() > 5 && getGrossAmt.length() <= 10) {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5);
                } else {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5, 10);
                    GrossAmt3 = getGrossAmt.substring(10);
                }

                if (getDiscPer.length() <= 4) {
                    DiscPer = getDiscPer;
                } else if (getDiscPer.length() > 4 && getDiscPer.length() <= 8) {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4);
                } else {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4, 8);
                    DiscPer3 = getDiscPer.substring(8);
                }


                if (getDisc.length() <= 4) {
                    Disc = getDisc;
                } else if (getDisc.length() > 4 && getDisc.length() <= 8) {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4);
                } else {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4, 8);
                    Disc3 = getDisc.substring(8);
                }


                if (getExcise.length() <= 6) {
                    Excise = getExcise;
                } else if (getExcise.length() > 6 && getExcise.length() <= 12) {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6);
                } else {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6, 12);
                    Excise3 = getExcise.substring(12);
                }


                if (getVatFivePer.length() <= 4) {
                    VatFivePer = getVatFivePer;
                } else if (getVatFivePer.length() > 4 && getVatFivePer.length() <= 8) {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4);
                } else {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4, 8);
                    VatFivePer3 = getVatFivePer.substring(8);
                }

                if (getTaxableAmount.length() <= 7) {
                    TaxableAmount = getTaxableAmount;
                } else if (getTaxableAmount.length() > 7 && getTaxableAmount.length() <= 14) {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7);
                } else {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7, 14);
                    TaxableAmount3 = getTaxableAmount.substring(14);
                }


                if (getAmount.length() <= 6) {
                    Amount = getAmount;
                } else if (getAmount.length() > 6 && getAmount.length() <= 12) {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6);
                } else {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6, 12);
                    Amount3 = getAmount.substring(12);
                }


                String formattedLine1 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", ItemCode, "", Rate, GrossAmt, DiscPer, Disc, Excise, TaxableAmount, VatFivePer, Amount);
                String formattedLine2 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9%-6s%-4s", "", "", Rate2, GrossAmt2, DiscPer2, Disc2, Excise2, TaxableAmount2, VatFivePer2, Amount2);
                String formattedLine3 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", "", "", Rate3, GrossAmt3, DiscPer3, Disc3, Excise3, TaxableAmount3, VatFivePer3, Amount3);

                writeText(formattedLine1, mEmphasis, false, mCharsize, 0);

                if (!Rate2.isEmpty() || !DiscPer2.isEmpty() || !Disc2.isEmpty() ||
                        !Excise2.isEmpty() || !TaxableAmount2.isEmpty() || !VatFivePer2.isEmpty() || !Amount2.isEmpty()) {


                    writeText(formattedLine2, mEmphasis, false, mCharsize, 0);
                }


                if (!Rate3.isEmpty() || !DiscPer3.isEmpty() || !Disc3.isEmpty() ||
                        !Excise3.isEmpty() || !TaxableAmount3.isEmpty() || !VatFivePer3.isEmpty() || !Amount3.isEmpty()) {


                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                }


                writeText("(UOM:" + getQty + " " + UOM + ")" + " / " + Description, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);
            }

            int labelWidth = 15; // Adjust this based on your desired width
            int numSpaces = labelWidth - "Total Cases:".length();

            // Create the spacing string
            String spacing = new String(new char[numSpaces]).replace('\0', ' ');

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            String grossTotal = "", vatAmt = "", netAmt = "", excise = "", discount = "", total = "", mFinalGross = "";
            if (jTOBject.has("Gross Total")) {
                grossTotal = jTOBject.getString("Gross Total");
            }

            if (jTOBject.has("VAT")) {
                vatAmt = jTOBject.getString("VAT");
            }

            if (jTOBject.has("NET")) {
                netAmt = jTOBject.getString("NET");
            }

            if (jTOBject.has("EXCISE")) {
                excise = jTOBject.getString("EXCISE");
            }

            if (jTOBject.has("Discount")) {
                discount = jTOBject.getString("Discount");
            }

            if (jTOBject.has("Total Amount")) {
                total = jTOBject.getString("Total Amount");
            }

            mFinalGross = String.valueOf(Double.parseDouble(netAmt) - Double.parseDouble(discount));


            String formattedTotalCase = String.format("%.2f", totalCase);
            String TotalCases = String.format("%s%-53s%s", spacing, "Total Cases:", alignRight(formattedTotalCase, 10));
            String NetAmount = String.format("%s%-50s%s", spacing, "Net Amount:", "AED" + alignRight(mFinalGross, 10));
            String TotalExcise = String.format("%s%-50s%s", spacing, "Total Excise:", "AED" + alignRight(excise, 10));
            String NetTaxableAmount = String.format("%s%-50s%s", spacing, "Net Taxable Amount:", "AED" + alignRight(netAmt, 10));
            String TotalVat = String.format("%s%-50s%s", spacing, "TotalVat:", "AED" + alignRight(vatAmt, 10));
            String TotalGross = String.format("%s%-50s%s", spacing, "Total Gross:", "AED" + alignRight(total, 10));

            String totalGrossWords = "";
            String Amount = total;
            int decimals = Amount.indexOf(".");
            String decimalValue = null;
            if (0 <= decimals) {
                decimalValue = Amount.substring(decimals + 1);
                Amount = Amount.substring(0, decimals).replaceAll("-", "");


                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    Long dataDecimal = Long.parseLong(decimalValue);
                    strWord = NumberToWord.convert(data1) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            } else {

                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    strWord = NumberToWord.convert(data1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            }

// Convert the total gross to words
            //String totalGrossWords = converter.convertToWords(Double.parseDouble(total));

            writeText(TotalCases, mEmphasis, false, mCharsize, 0);
            writeText(NetAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalExcise, mEmphasis, false, mCharsize, 0);
            writeText(NetTaxableAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalVat, mEmphasis, false, mCharsize, 0);
            writeText(TotalGross, mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Amount In Words: " + totalGrossWords + "", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            writeText("                           Thank You", mEmphasis, false, mCharsize, 0);
            writeText("You can submit your concern anonymously to concern@masafi.com", mEmphasis, false, mCharsize, 0);
            BitmapFactory.Options optionsSocial = new BitmapFactory.Options();
            optionsSocial.inScaled = false;
            Bitmap bmpSocial = BitmapFactory.decodeResource(context.getResources(), R.drawable.social1, optionsSocial);
            if (bmpSocial == null) {
                Log.e("", "resource decoding is failed");
                return;
            }


            byte[] dataSocial = WoosimCustomImage.printColorBitmap(0, 0, 850, 150, bmpSocial);
            bmpSocial.recycle();

            sendData(WoosimCmd.setPageMode());
            sendData(dataSocial);
            sendData(WoosimCmd.PM_setStdMode());
//        writeText("                     - www.facebook.com/Masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.instagram.com/masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.linkedin.com/company/masafi/\n\n", mEmphasis, false, mCharsize, 0);
            writeText("      Customer Signature & Stamp        For MASAFI CO. (L.L.C.)        ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            final byte[] data1 = {0x30, 0x30, 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x30};
            final byte[] cmd_print = WoosimCustomCmd.printData();

            byte[] gs4 = WoosimBarcode.createGS1Databar(4, 2, data1);

            ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);

            // Calculate the amount of padding needed to center the barcode
            int totalWidth = 70; // Total width of the printed area (adjust as needed)
            int barcodeWidth = gs4.length; // Width of the barcode
            int padding = (totalWidth - barcodeWidth) / 2;

            // Write padding before the barcode data
            for (int i = 0; i < padding; i++) {
                byteStream.write(0x20); // Assuming 0x20 is the space character
            }

            // Write the barcode data
            byteStream.write(gs4);
            byteStream.write(cmd_print);

            sendData(WoosimCmd.initPrinter());
            sendData(byteStream.toByteArray());
            sendData(WoosimCmd.PM_setStdMode());

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            if (context instanceof OrderConfirmActivity) {
                OrderConfirmActivity.Companion.callbackFunction("Order", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void parseLoadRequestResponse(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }

        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {


            writeText("LOAD REQUEST", mEmphasis, false, 1, 1);

            writeText("Load Request No: " + object.getString("DOCUMENT NO"), mEmphasis, false, mCharsize, 0);
            writeText("Load Date: " + object.getString("LoadDate"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);
            writeText("Route: " + object.getString("ROUTE"), mEmphasis, false, mCharsize, 0);

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item  Description                      UOM            Qty           ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            double totalCase = 0.0;
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(3)));
                String uom = jArr.getString(2);
                String Total = jArr.getString(12);

                String ItemCode = db.getItemCodeFromName(name);
                String formattedLine3 = String.format("%-39s %-13s %-13s", ItemCode, uom, qty);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText(name, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            writeText("           Warehouse Signature           Salesman Signature         ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);


            if (context instanceof OrderConfirmActivity) {
                OrderConfirmActivity.Companion.callbackFunction("load", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void parseLoadSummaryResponse(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {


            writeText("LOAD SUMMARY", mEmphasis, false, 1, 1);

            writeText("LOAD NO: " + object.getString("DOCUMENT NO"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);
            writeText("Route: " + object.getString("ROUTE"), mEmphasis, false, mCharsize, 0);
            writeText("Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"), mEmphasis, false, mCharsize, 0);

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item  Description                       UOM            Qty          ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            double totalCase = 0.0;
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(2);
                String uom = jArr.getString(3);
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(5)));

                String ItemCode = db.getItemCodeFromName(name);
                String formattedLine3 = String.format("%-39s %-13s %-13s", ItemCode, uom, qty);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText(name, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            writeText("           Warehouse Signature           Salesman Signature         ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);


            if (context instanceof LoadConfirmActivity) {
                LoadConfirmActivity.Companion.callbackFunction();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void parseVanSummaryResponse(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            writeText("VAN STOCK SUMMARY", mEmphasis, false, 1, 1);

            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);
            writeText("Route: " + object.getString("ROUTE"), mEmphasis, false, mCharsize, 0);
            writeText("Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"), mEmphasis, false, mCharsize, 0);


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item  Description                       UOM            Qty          ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(2);
                String uom = jArr.getString(3);
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(4)));

                String ItemCode = db.getItemCodeFromName(name);
                String formattedLine3 = String.format("%-39s %-14s %-12s", ItemCode, uom, qty);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText(name, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            writeText("           Warehouse Signature           Salesman Signature         ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void printUnloadEndInventryRequest(JSONObject object, String args) {


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            writeText("END INVENTORY", mEmphasis, false, 1, 1);


            writeText("Unload No: " + object.getString("DOCUMENT NO"), mEmphasis, false, mCharsize, 0);
            writeText("Unload Date: " + object.getString("DOC DATE"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item Description                 UOM    LOAD     UNLOAD       GRV   ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String uom = jArr.getString(2);
                String load = String.format("%.2f", Double.parseDouble(jArr.getString(3)));
                String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));
                String returnQty = String.format("%.2f", Double.parseDouble(jArr.getString(5)));
                String vari = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String fresh = String.format("%.2f", Double.parseDouble(jArr.getString(7)));
                String unl = String.format("%.2f", Double.parseDouble(jArr.getString(8)));


                String ItemCode = db.getItemCodeFromName(name);
                String formattedLine3 = String.format("%-32s %-6s %-8s %-10s %-12s", ItemCode, uom, load, fresh, returnQty);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText(name, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            writeText("           Warehouse Signature           Salesman Signature         ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void printUnloadExpiryRequest(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            writeText("UNLOAD BAD EXPIRY", mEmphasis, false, 1, 1);


            writeText("Unload No: " + object.getString("DOCUMENT NO"), mEmphasis, false, mCharsize, 0);
            writeText("Unload Date: " + object.getString("DOC DATE"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item  Description                       UOM            Qty          ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String load = jArr.getString(2);
                String sale = jArr.getString(3);

                String ItemCode = db.getItemCodeFromName(name);
                String formattedLine3 = String.format("%-39s %-14s %-12s", ItemCode, load, sale);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText(name, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            writeText("           Warehouse Signature           Salesman Signature         ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void printUnloadDamageRequest(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            writeText("UNLOAD BAD EXPIRY", mEmphasis, false, 1, 1);


            writeText("Unload No: " + object.getString("DOCUMENT NO"), mEmphasis, false, mCharsize, 0);
            writeText("Unload Date: " + object.getString("DOC DATE"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item  Description                       UOM            Qty          ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String load = jArr.getString(2);
                String sale = jArr.getString(3);

                String ItemCode = db.getItemCodeFromName(name);
                String formattedLine3 = String.format("%-39s %-14s %-12s", ItemCode, load, sale);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText(name, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            writeText("           Warehouse Signature           Salesman Signature         ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void printSalesSummary(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }

        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            String mType = object.getString("mType");

            if (mType.equals("Order")) {
                writeText("ORDER SUMMARY", mEmphasis, false, 1, 1);
            } else {
                writeText("SALES SUMMARY", mEmphasis, false, 1, 1);
            }


            writeText("Summary Date: " + object.getString("DOC DATE"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);
            writeText("Route: " + object.getString("ROUTE"), mEmphasis, false, mCharsize, 0);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            if (mType.equals("Order")) {
                JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
                if (jData.length() > 0) {
                    writeText("", mEmphasis, false, mCharsize, 0);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Transaction No    Site Code    Site Name                  Amount    ", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    for (int i = 0; i < jData.length(); i++) {

                        JSONArray jArr = jData.getJSONArray(i);

                        String inv = jArr.getString(1);
                        String cust = jArr.getString(2);
                        String name = jArr.getString(3);
                        String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));


                        String formattedLine3 = String.format("%-18s %-10s %-27s %-10s", inv, cust, name, sale);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                        writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    if (jTOBject.has("Total_Case")) {
                        writeText("Total Sales : " + jTOBject.getString("Total_Case") + "   ", mEmphasis, false, mCharsize, 2);
                    }

                }

                JSONArray grRData = object.getJSONArray("cashRdata");
                if (grRData.length() > 0) {
                    writeText("", mEmphasis, false, mCharsize, 0);
                    writeText("RETURN", mEmphasis, false, 1, 1);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Transaction No    Site Code    Site Name                  Amount    ", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    for (int i = 0; i < grRData.length(); i++) {

                        JSONArray jArr = grRData.getJSONArray(i);

                        String inv = jArr.getString(1);
                        String cust = jArr.getString(2);
                        String name = jArr.getString(3);
                        String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));

                        String formattedLine3 = String.format("%-18s %-10s %-27s %-10s", inv, cust, name, sale);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                        writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    if (jTOBject.has("Total_RCase")) {
                        writeText("Total Return : " + jTOBject.getString("Total_RCase") + "   ", mEmphasis, false, mCharsize, 2);
                    }

                }


            } else {
                JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
                if (jData.length() > 0) {
                    writeText("", mEmphasis, false, mCharsize, 0);
                    writeText("CASH INVOICE", mEmphasis, false, 1, 1);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Transaction No    Site Code    Site Name                  Amount    ", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    for (int i = 0; i < jData.length(); i++) {

                        JSONArray jArr = jData.getJSONArray(i);

                        String inv = jArr.getString(1);
                        String cust = jArr.getString(2);
                        String name = jArr.getString(3);
                        String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));


                        String formattedLine3 = String.format("%-18s %-10s %-27s %-10s", inv, cust, name, sale);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                        writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    if (jTOBject.has("Total_Case")) {
                        writeText("Total Cash Sales : " + jTOBject.getString("Total_Case") + "   ", mEmphasis, false, mCharsize, 2);
                    }

                }

                JSONArray grData = object.getJSONArray("creditdata");
                if (grData.length() > 0) {
                    writeText("", mEmphasis, false, mCharsize, 0);
                    writeText("CREDIT INVOICE", mEmphasis, false, 1, 1);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Transaction No    Site Code    Site Name                  Amount    ", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    for (int i = 0; i < grData.length(); i++) {

                        JSONArray jArr = grData.getJSONArray(i);

                        String inv = jArr.getString(1);
                        String cust = jArr.getString(2);
                        String name = jArr.getString(3);
                        String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));

                        String formattedLine3 = String.format("%-18s %-10s %-27s %-10s", inv, cust, name, sale);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                        writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    if (jTOBject.has("Total_Credit")) {
                        writeText("Total Credit Sales : " + jTOBject.getString("Total_Credit") + "   ", mEmphasis, false, mCharsize, 2);
                    }
                }

                JSONArray grRData = object.getJSONArray("cashRdata");
                if (grRData.length() > 0) {
                    writeText("", mEmphasis, false, mCharsize, 0);
                    writeText("CASH RETURN", mEmphasis, false, 1, 1);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Transaction No    Site Code    Site Name                  Amount    ", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    for (int i = 0; i < grRData.length(); i++) {

                        JSONArray jArr = grRData.getJSONArray(i);

                        String inv = jArr.getString(1);
                        String cust = jArr.getString(2);
                        String name = jArr.getString(3);
                        String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));

                        String formattedLine3 = String.format("%-18s %-10s %-27s %-10s", inv, cust, name, sale);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                        writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    if (jTOBject.has("Total_RCase")) {
                        writeText("Total Cash Return : " + jTOBject.getString("Total_RCase") + "   ", mEmphasis, false, mCharsize, 2);
                    }

                }

                JSONArray grRRData = object.getJSONArray("creditRdata");
                if (grRRData.length() > 0) {

                    writeText("", mEmphasis, false, mCharsize, 0);
                    writeText("CREDIT RETURN", mEmphasis, false, 1, 1);

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                    writeText("Transaction No    Site Code    Site Name                  Amount    ", mEmphasis, false, mCharsize, 0);
                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                    for (int i = 0; i < grRRData.length(); i++) {

                        JSONArray jArr = grRRData.getJSONArray(i);

                        String inv = jArr.getString(1);
                        String cust = jArr.getString(2);
                        String name = jArr.getString(3);
                        String sale = String.format("%.2f", Double.parseDouble(jArr.getString(4)));

                        String formattedLine3 = String.format("%-18s %-10s %-27s %-10s", inv, cust, name, sale);
                        writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                        writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                    }

                    writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                    if (jTOBject.has("Total_RCredit")) {
                        writeText("Total Credit Return : " + jTOBject.getString("Total_RCredit") + "   ", mEmphasis, false, mCharsize, 2);
                    }

                }
            }


            if (jTOBject.has("Total_Sales")) {

                writeText("", mEmphasis, false, mCharsize, 0);
                writeText("Total Sales : " + jTOBject.getString("Total_Sales") + "   ", mEmphasis, false, mCharsize, 2);

            }

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void printSOASummary(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }

        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {

            writeText("CUSTOMER STATEMENT", mEmphasis, false, 1, 1);

            writeText("Site Code: " + object.getString("CUSTOMERID"), mEmphasis, false, mCharsize, 0);
            writeText("Site Name: " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);
            writeText("Date: " + object.getString("DOC DATE"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);
            writeText("Route: " + object.getString("ROUTE"), mEmphasis, false, mCharsize, 0);

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            if (jData.length() > 0) {
                writeText("", mEmphasis, false, mCharsize, 0);

                writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
                writeText("Transaction No        Invoice Date      Invoice Amt      Due Amount  ", mEmphasis, false, mCharsize, 0);
                writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


                for (int i = 0; i < jData.length(); i++) {

                    JSONArray jArr = jData.getJSONArray(i);

                    String date = jArr.getString(0);
                    String inv = jArr.getString(1);
                    String totalamt = jArr.getString(2);
                    String due = jArr.getString(3);


                    String formattedLine3 = String.format("%-21s %-18s %-15s %-10s", inv, date, totalamt, due);
                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                    writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

                }

                writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

                if (jTOBject.has("Total Amount")) {
                    writeText("Total Due Amount : " + jTOBject.getString("Total Amount") + "   ", mEmphasis, false, mCharsize, 2);
                }

            }

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void printDepositesSummary(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }

        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {


            writeText("DEPOSIT SUMMARY", mEmphasis, false, 1, 1);


            writeText("Summary Date: " + object.getString("DOC DATE"), mEmphasis, false, mCharsize, 0);
            writeText("Salesman: " + object.getString("SALESMAN"), mEmphasis, false, mCharsize, 0);
            writeText("Route: " + object.getString("ROUTE"), mEmphasis, false, mCharsize, 0);

            writeText("------------------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Receipt No      Site Code   Cheque No   BalanceAmt   Cash      Card     iCash ", mEmphasis, false, mCharsize, 0);
            writeText("------------------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);

            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String inv = jArr.getString(1);
                String cust = jArr.getString(2);
                String name = jArr.getString(3);
                String sale = jArr.getString(4);
                String paid = String.format("%.2f", Double.parseDouble(jArr.getString(5)));
                String cash = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String card = String.format("%.2f", Double.parseDouble(jArr.getString(7)));
                String iCash = String.format("%.2f", Double.parseDouble(jArr.getString(8)));


                String formattedLine3 = String.format("%-14s %-14s %-10s %-10s %-10s %-10s %-10s", inv, cust, sale, paid, cash, card, iCash);
                writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }


            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);


            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Total Cash Collection: " + jTOBject.getString("Total_Case") + "   ", mEmphasis, false, mCharsize, 2);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Total Cheque Collection: " + jTOBject.getString("Total_Credit") + "   ", mEmphasis, false, mCharsize, 2);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Total Card Collection: " + jTOBject.getString("Total_Card") + "   ", mEmphasis, false, mCharsize, 2);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Total iCash Collection: " + jTOBject.getString("Total_iCash") + "   ", mEmphasis, false, mCharsize, 2);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Total AC Balance Collection: " + jTOBject.getString("Total_balanceCash") + "   ", mEmphasis, false, mCharsize, 2);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("       Account Signature & Stamp        Salesman Signature          ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Order Summary Print
    void printDailyOtherSummary(JSONObject object, String args) {

        try {

            int i = 1;
            myPrinter.initPrinter();
            myPrinter.setLeftMargin(4);
            myPrinter.setFont(0, 0, 0, 0, 0);

            printInvoiceHeader(myPrinter);

            StringBuffer sb;
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);
            myPrinter.setFont(0, 1, 0, 0, 0);
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_CENTER);
            sb = new StringBuffer();
            sb.append("DAILY FIELD ACTIVITY REPORT");
            myPrinter.printText(sb.toString());
            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);
            myPrinter.setFont(0, 0, 0, 0, 0);
            myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
            sb = new StringBuffer();
            sb.append("Route: " + object.getString("ROUTE") + "\n");
            sb.append("Salesman: " + object.getString("SALESMAN") + "\n");
            sb.append("Date: " + object.getString("DOC DATE") + "\n\n");
            myPrinter.printText(sb.toString());
            sb = new StringBuffer();
            sb.append("================================================================\n");
            myPrinter.printText(sb.toString());

            JSONArray jData = object.getJSONArray("jpData");
            printTableDailyField(myPrinter, jData); // Print Data in Table Body

            sb = new StringBuffer();
            sb.append("----------------------------------------------------------------\n");
            myPrinter.printText(sb.toString());

            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);

            sb = new StringBuffer();
            sb.append("================================================================\n");
            myPrinter.printText(sb.toString());

            JSONArray jPData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            printTableDailyFieldTotal(myPrinter, jPData); // Print Data in Table Body

            sb = new StringBuffer();
            sb.append("----------------------------------------------------------------\n");
            myPrinter.printText(sb.toString());

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);
            printTableTotalDaily(myPrinter, jTOBject);

            myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTableTotalDeposit(PrinterInstance mPrinter, JSONObject jTOBject) {

        String noOfInv = "", totalSale = "";

        String column;
        Table table = null;
        try {

            if (jTOBject.has("Total_Case")) {
                noOfInv = jTOBject.getString("Total_Case");
            }
            if (jTOBject.has("Total_Credit")) {
                totalSale = jTOBject.getString("Total_Credit");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String tot = "           ", titleTot = "Total Case Collection: " + noOfInv;
        column = tot + ";" + ";" + titleTot;
        table = new Table(column, ";", new int[]{18, 15, 34});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        String tot1 = "           ", titleTot1 = "Total Cheque Collection: " + totalSale;
        column = tot1 + ";" + ";" + titleTot1;
        table = new Table(column, ";", new int[]{18, 15, 34});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);


    }

    public static void printTableTotalDaily(PrinterInstance mPrinter, JSONObject jTOBject) {

        String noOfInv = "", totalSale = "", noOfVisit = "", mtdSales = "", netSale = "",
                retrurn = "", totalCash = "", totalCheque = "";

        String column;
        Table table = null;
        try {

            if (jTOBject.has("No of Invoice")) {
                noOfInv = jTOBject.getString("No of Invoice");
            }
            if (jTOBject.has("Total_Sales")) {
                totalSale = jTOBject.getString("Total_Sales");
            }
            if (jTOBject.has("No of Visit")) {
                noOfVisit = jTOBject.getString("No of Visit");
            }


            if (jTOBject.has("Total_Return")) {
                retrurn = jTOBject.getString("Total_Return");
            }
            if (jTOBject.has("Total_Cash")) {
                totalCash = jTOBject.getString("Total_Cash");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String tot = "No of Invoices: " + noOfInv, titleTot = "Total Sales: " + totalSale;
        column = tot + ";" + ";" + titleTot;
        table = new Table(column, ";", new int[]{18, 25, 24});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        String visit = "No of Visit: " + noOfVisit, titleMtdSale = "Total Return: " + retrurn;
        column = visit + ";" + ";" + titleMtdSale;
        table = new Table(column, ";", new int[]{18, 25, 24});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        String mBlank1 = "        ", titleCash = "Total Collection: " + totalCash;
        column = mBlank1 + ";" + ";" + titleCash;
        table = new Table(column, ";", new int[]{18, 25, 24});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);


    }

    //Return Summary Print
    void parseReturnResponse(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {


            writeText("Goods Received Voucher", mEmphasis, false, 1, 1);


            writeText("Bill To : " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);
            writeText("Ship To: " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);

            int columnWidth = 34; // Adjust this value based on your layout

            // Customer Code and Site No
            String customerSiteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Customer Code: " + object.getString("CUSTOMERID"), "Site No: " + object.getString("CUSTOMERID"));
            writeText(customerSiteLine, mEmphasis, false, mCharsize, 0);


            String trnRouteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "TRN: " + object.getString("TRN"), "Route Code: " + object.getString("ROUTE"));
            writeText(trnRouteLine, mEmphasis, false, mCharsize, 0);


            // Delivered By and Invoice Date
            String deliveredInvoiceLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "CreditNote ID: " + object.getString("DOCUMENT NO"), "Credit Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"));
            writeText(deliveredInvoiceLine, mEmphasis, false, mCharsize, 0);

            String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Created by: " + object.getString("SALESMAN"), "Salesman: " + object.getString("SALESMAN"));
            writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item Code   RATE   G.AMT  DISC  DISC   Excise  Taxable  VAT   AMOUNT", mEmphasis, false, mCharsize, 0);
            writeText("UOM/Description            %                    Amt     @5%         ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            double totalCase = 0.0;
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String invoiceNumber = jArr.getString(2);
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(5)));
                String mUom = jArr.getString(4);
                //cf

                String price = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String excs = String.format("%.2f", Double.parseDouble(jArr.getString(8)));
                String discPer = "0.00";
                String disc = "0.00";
                String vat = String.format("%.2f", Double.parseDouble(jArr.getString(11)));
                String net = String.format("%.2f", Double.parseDouble(jArr.getString(10)));
                String Total = String.format("%.2f", Double.parseDouble(jArr.getString(12)));
                String grossPrice = String.format("%.2f", Double.parseDouble(jArr.getString(7)));
                String excisePrice = String.format("%.2f", Double.parseDouble(jArr.getString(6)));


                String barcode = jArr.getString(13);
                String plu = jArr.getString(14);
                String mBarPLu = barcode + "     " + plu;
                String totalPrice = String.valueOf((Float.parseFloat(price) + Float.parseFloat(excisePrice)));

                totalCase = totalCase + Double.parseDouble(qty);

                String mCode = db.getItemCodeFromName(name);

                String ItemCode = mCode;
                String UOM = mUom;
                String Description = name;
                String getQty = qty;
                String getRate = price;
                String getGrossAmt = grossPrice;
                String getDiscPer = discPer;
                String getDisc = disc;
                String getExcise = excisePrice;
                String getVatFivePer = vat;
                String getTaxableAmount = net;
                String getAmount = Total;

                String Rate;
                String GrossAmt;
                String DiscPer;
                String Disc;
                String Excise;
                String VatFivePer;
                String TaxableAmount;
                String Amount;


                String Rate2 = "";
                String GrossAmt2 = "";
                String DiscPer2 = "";
                String Disc2 = "";
                String Excise2 = "";
                String VatFivePer2 = "";
                String TaxableAmount2 = "";
                String Amount2 = "";

                String Rate3 = "";
                String GrossAmt3 = "";
                String DiscPer3 = "";
                String Disc3 = "";
                String Excise3 = "";
                String VatFivePer3 = "";
                String TaxableAmount3 = "";
                String Amount3 = "";


                if (getRate.length() <= 5) {
                    Rate = getRate;
                } else if (getRate.length() > 5 && getRate.length() <= 10) {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5);
                } else {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5, 10);
                    Rate3 = getRate.substring(10);
                }


                if (getGrossAmt.length() <= 5) {
                    GrossAmt = getGrossAmt;
                } else if (getGrossAmt.length() > 5 && getGrossAmt.length() <= 10) {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5);
                } else {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5, 10);
                    GrossAmt3 = getGrossAmt.substring(10);
                }

                if (getDiscPer.length() <= 4) {
                    DiscPer = getDiscPer;
                } else if (getDiscPer.length() > 4 && getDiscPer.length() <= 8) {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4);
                } else {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4, 8);
                    DiscPer3 = getDiscPer.substring(8);
                }


                if (getDisc.length() <= 4) {
                    Disc = getDisc;
                } else if (getDisc.length() > 4 && getDisc.length() <= 8) {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4);
                } else {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4, 8);
                    Disc3 = getDisc.substring(8);
                }


                if (getExcise.length() <= 6) {
                    Excise = getExcise;
                } else if (getExcise.length() > 6 && getExcise.length() <= 12) {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6);
                } else {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6, 12);
                    Excise3 = getExcise.substring(12);
                }


                if (getVatFivePer.length() <= 4) {
                    VatFivePer = getVatFivePer;
                } else if (getVatFivePer.length() > 4 && getVatFivePer.length() <= 8) {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4);
                } else {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4, 8);
                    VatFivePer3 = getVatFivePer.substring(8);
                }

                if (getTaxableAmount.length() <= 7) {
                    TaxableAmount = getTaxableAmount;
                } else if (getTaxableAmount.length() > 7 && getTaxableAmount.length() <= 14) {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7);
                } else {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7, 14);
                    TaxableAmount3 = getTaxableAmount.substring(14);
                }


                if (getAmount.length() <= 6) {
                    Amount = getAmount;
                } else if (getAmount.length() > 6 && getAmount.length() <= 12) {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6);
                } else {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6, 12);
                    Amount3 = getAmount.substring(12);
                }


                String formattedLine1 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", ItemCode, "", Rate, GrossAmt, DiscPer, Disc, Excise, TaxableAmount, VatFivePer, Amount);
                String formattedLine2 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9%-6s%-4s", "", "", Rate2, GrossAmt2, DiscPer2, Disc2, Excise2, TaxableAmount2, VatFivePer2, Amount2);
                String formattedLine3 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", "", "", Rate3, GrossAmt3, DiscPer3, Disc3, Excise3, TaxableAmount3, VatFivePer3, Amount3);

                writeText(formattedLine1, mEmphasis, false, mCharsize, 0);

                if (!Rate2.isEmpty() || !DiscPer2.isEmpty() || !Disc2.isEmpty() ||
                        !Excise2.isEmpty() || !TaxableAmount2.isEmpty() || !VatFivePer2.isEmpty() || !Amount2.isEmpty()) {


                    writeText(formattedLine2, mEmphasis, false, mCharsize, 0);
                }


                if (!Rate3.isEmpty() || !DiscPer3.isEmpty() || !Disc3.isEmpty() ||
                        !Excise3.isEmpty() || !TaxableAmount3.isEmpty() || !VatFivePer3.isEmpty() || !Amount3.isEmpty()) {


                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                }


                writeText("(UOM:" + getQty + " " + UOM + ")" + " / " + Description, mEmphasis, false, mCharsize, 0);
                writeText("Invoice No: " + invoiceNumber, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            int labelWidth = 15; // Adjust this based on your desired width
            int numSpaces = labelWidth - "Total Cases:".length();

            // Create the spacing string
            String spacing = new String(new char[numSpaces]).replace('\0', ' ');

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            String grossTotal = "", vatAmt = "", netAmt = "", excise = "", discount = "", total = "", mFinalGross = "";
            if (jTOBject.has("Gross Total")) {
                grossTotal = jTOBject.getString("Gross Total");
            }

            if (jTOBject.has("VAT")) {
                vatAmt = String.format("%.2f", Double.parseDouble(jTOBject.getString("VAT")));
            }

            if (jTOBject.has("NET")) {
                netAmt = String.format("%.2f", Double.parseDouble(jTOBject.getString("NET")));
            }

            if (jTOBject.has("EXCISE")) {
                excise = String.format("%.2f", Double.parseDouble(jTOBject.getString("EXCISE")));
            }

            if (jTOBject.has("Discount")) {
                discount = String.format("%.2f", Double.parseDouble(jTOBject.getString("Discount")));
            }

            if (jTOBject.has("Total Amount")) {
                total = String.format("%.2f", Double.parseDouble(jTOBject.getString("Total Amount")));
            }

            mFinalGross = String.valueOf(Double.parseDouble(netAmt) - Double.parseDouble(discount));


            String formattedTotalCase = String.format("%.2f", totalCase);
            String TotalCases = String.format("%s%-53s%s", spacing, "Total Cases:", alignRight(formattedTotalCase, 10));
            String NetAmount = String.format("%s%-50s%s", spacing, "Net Amount:", "AED" + alignRight(mFinalGross, 10));
            String TotalExcise = String.format("%s%-50s%s", spacing, "Total Excise:", "AED" + alignRight(excise, 10));
            String NetTaxableAmount = String.format("%s%-50s%s", spacing, "Net Taxable Amount:", "AED" + alignRight(netAmt, 10));
            String TotalVat = String.format("%s%-50s%s", spacing, "TotalVat:", "AED" + alignRight(vatAmt, 10));
            String TotalGross = String.format("%s%-50s%s", spacing, "Total Gross:", "AED" + alignRight(total, 10));

            String totalGrossWords = "";
            String Amount = total;
            int decimals = Amount.indexOf(".");
            String decimalValue = null;
            if (0 <= decimals) {
                decimalValue = Amount.substring(decimals + 1);
                Amount = Amount.substring(0, decimals).replaceAll("-", "");


                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    Long dataDecimal = Long.parseLong(decimalValue);
                    strWord = NumberToWord.convert(data1) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            } else {

                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    strWord = NumberToWord.convert(data1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            }

// Convert the total gross to words
            //String totalGrossWords = converter.convertToWords(Double.parseDouble(total));

            writeText(TotalCases, mEmphasis, false, mCharsize, 0);
            writeText(NetAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalExcise, mEmphasis, false, mCharsize, 0);
            writeText(NetTaxableAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalVat, mEmphasis, false, mCharsize, 0);
            writeText(TotalGross, mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Amount In Words: " + totalGrossWords + "", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            writeText("                           Thank You", mEmphasis, false, mCharsize, 0);
            writeText("You can submit your concern anonymously to concern@masafi.com", mEmphasis, false, mCharsize, 0);
            BitmapFactory.Options optionsSocial = new BitmapFactory.Options();
            optionsSocial.inScaled = false;
            Bitmap bmpSocial = BitmapFactory.decodeResource(context.getResources(), R.drawable.social1, optionsSocial);
            if (bmpSocial == null) {
                Log.e("", "resource decoding is failed");
                return;
            }


            byte[] dataSocial = WoosimCustomImage.printColorBitmap(0, 0, 850, 150, bmpSocial);
            bmpSocial.recycle();

            sendData(WoosimCmd.setPageMode());
            sendData(dataSocial);
            sendData(WoosimCmd.PM_setStdMode());
//        writeText("                     - www.facebook.com/Masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.instagram.com/masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.linkedin.com/company/masafi/\n\n", mEmphasis, false, mCharsize, 0);
            writeText("      Customer Signature & Stamp        For MASAFI CO. (L.L.C.)        ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            BarcodeFormat format = BarcodeFormat.CODE_128;
            int width = 400;
            int height = 200;

            // Generate barcode bitmap
            Bitmap barcodeBitmap = generateBarcode(object.getString("DOCUMENT NO"), format, width, height);

            // Print the barcode using Woosim printer
            if (barcodeBitmap != null) {
                int pageWidth = 780; // Set your page width here
                int barcodeWidth = barcodeBitmap.getWidth();
                int startX = (pageWidth - barcodeWidth) / 2;

                // Print the centered barcode
                byte[] dataBarcode = WoosimCustomImage.printColorBitmap(startX, 0, barcodeWidth, 150, barcodeBitmap);
                barcodeBitmap.recycle();

                sendData(WoosimCmd.setPageMode());
                sendData(dataBarcode);
                sendData(WoosimCmd.PM_setStdMode());
            }

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            if (context instanceof OrderConfirmActivity) {
                OrderConfirmActivity.Companion.callbackFunction("Return", "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void parsePalletReturnResponse(JSONObject object, String args) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.masafi_new_logo_convert, options);
        if (bmp == null) {
            Log.e("ss", "resource decoding is failed");
            return;
        }


        byte[] data = WoosimCustomImage.printColorBitmap(0, 0, 850, 300, bmp);
        bmp.recycle();

        sendData(WoosimCmd.setPageMode());
        sendData(data);
        sendData(WoosimCmd.PM_setStdMode());

        try {


            writeText("Pallet Received Voucher", mEmphasis, false, 1, 1);


            writeText("Bill To : " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);
            writeText("Ship To: " + object.getString("CUSTOMER"), mEmphasis, false, mCharsize, 0);

            int columnWidth = 34; // Adjust this value based on your layout

            // Customer Code and Site No
            String customerSiteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Customer Code: " + object.getString("CUSTOMERID"), "Site No: " + object.getString("CUSTOMERID"));
            writeText(customerSiteLine, mEmphasis, false, mCharsize, 0);


            String trnRouteLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "TRN: " + object.getString("TRN"), "Route Code: " + object.getString("ROUTE"));
            writeText(trnRouteLine, mEmphasis, false, mCharsize, 0);


            // Delivered By and Invoice Date
            String deliveredInvoiceLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "CreditNote ID: " + object.getString("DOCUMENT NO"), "Credit Date: " + object.getString("DOC DATE") + " " + object.getString("TIME"));
            writeText(deliveredInvoiceLine, mEmphasis, false, mCharsize, 0);

            String deliveredbyLine = String.format("%-" + columnWidth + "s %-" + columnWidth + "s", "Created by: " + object.getString("SALESMAN"), "Salesman: " + object.getString("SALESMAN"));
            writeText(deliveredbyLine, mEmphasis, false, mCharsize, 0);


            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("Item Code   RATE   G.AMT  DISC  DISC   Excise  Taxable  VAT   AMOUNT", mEmphasis, false, mCharsize, 0);
            writeText("UOM/Description            %                    Amt     @5%         ", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);


            JSONArray jData = object.getJSONArray(JsonRpcUtil.PARAM_DATA);
            double totalCase = 0.0;
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String invoiceNumber = jArr.getString(2);
                String qty = String.format("%.2f", Double.parseDouble(jArr.getString(5)));
                String mUom = jArr.getString(4);
                //cf

                String price = String.format("%.2f", Double.parseDouble(jArr.getString(6)));
                String excs = String.format("%.2f", Double.parseDouble(jArr.getString(8)));
                String discPer = "0.00";
                String disc = "0.00";
                String vat = String.format("%.2f", Double.parseDouble(jArr.getString(11)));
                String net = String.format("%.2f", Double.parseDouble(jArr.getString(10)));
                String Total = String.format("%.2f", Double.parseDouble(jArr.getString(12)));
                String grossPrice = String.format("%.2f", Double.parseDouble(jArr.getString(7)));
                String excisePrice = String.format("%.2f", Double.parseDouble(jArr.getString(6)));


                String barcode = jArr.getString(13);
                String plu = jArr.getString(14);
                String mBarPLu = barcode + "     " + plu;
                String totalPrice = String.valueOf((Float.parseFloat(price) + Float.parseFloat(excisePrice)));

                totalCase = totalCase + Double.parseDouble(qty);

                String mCode = db.getItemCodeFromName(name);

                String ItemCode = mCode;
                String UOM = mUom;
                String Description = name;
                String getQty = qty;
                String getRate = price;
                String getGrossAmt = grossPrice;
                String getDiscPer = discPer;
                String getDisc = disc;
                String getExcise = excisePrice;
                String getVatFivePer = vat;
                String getTaxableAmount = net;
                String getAmount = Total;

                String Rate;
                String GrossAmt;
                String DiscPer;
                String Disc;
                String Excise;
                String VatFivePer;
                String TaxableAmount;
                String Amount;


                String Rate2 = "";
                String GrossAmt2 = "";
                String DiscPer2 = "";
                String Disc2 = "";
                String Excise2 = "";
                String VatFivePer2 = "";
                String TaxableAmount2 = "";
                String Amount2 = "";

                String Rate3 = "";
                String GrossAmt3 = "";
                String DiscPer3 = "";
                String Disc3 = "";
                String Excise3 = "";
                String VatFivePer3 = "";
                String TaxableAmount3 = "";
                String Amount3 = "";


                if (getRate.length() <= 5) {
                    Rate = getRate;
                } else if (getRate.length() > 5 && getRate.length() <= 10) {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5);
                } else {
                    Rate = getRate.substring(0, 5);
                    Rate2 = getRate.substring(5, 10);
                    Rate3 = getRate.substring(10);
                }


                if (getGrossAmt.length() <= 5) {
                    GrossAmt = getGrossAmt;
                } else if (getGrossAmt.length() > 5 && getGrossAmt.length() <= 10) {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5);
                } else {
                    GrossAmt = getGrossAmt.substring(0, 5);
                    GrossAmt2 = getGrossAmt.substring(5, 10);
                    GrossAmt3 = getGrossAmt.substring(10);
                }

                if (getDiscPer.length() <= 4) {
                    DiscPer = getDiscPer;
                } else if (getDiscPer.length() > 4 && getDiscPer.length() <= 8) {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4);
                } else {
                    DiscPer = getDiscPer.substring(0, 4);
                    DiscPer2 = getDiscPer.substring(4, 8);
                    DiscPer3 = getDiscPer.substring(8);
                }


                if (getDisc.length() <= 4) {
                    Disc = getDisc;
                } else if (getDisc.length() > 4 && getDisc.length() <= 8) {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4);
                } else {
                    Disc = getDisc.substring(0, 4);
                    Disc2 = getDisc.substring(4, 8);
                    Disc3 = getDisc.substring(8);
                }


                if (getExcise.length() <= 6) {
                    Excise = getExcise;
                } else if (getExcise.length() > 6 && getExcise.length() <= 12) {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6);
                } else {
                    Excise = getExcise.substring(0, 6);
                    Excise2 = getExcise.substring(6, 12);
                    Excise3 = getExcise.substring(12);
                }


                if (getVatFivePer.length() <= 4) {
                    VatFivePer = getVatFivePer;
                } else if (getVatFivePer.length() > 4 && getVatFivePer.length() <= 8) {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4);
                } else {
                    VatFivePer = getVatFivePer.substring(0, 4);
                    VatFivePer2 = getVatFivePer.substring(4, 8);
                    VatFivePer3 = getVatFivePer.substring(8);
                }

                if (getTaxableAmount.length() <= 7) {
                    TaxableAmount = getTaxableAmount;
                } else if (getTaxableAmount.length() > 7 && getTaxableAmount.length() <= 14) {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7);
                } else {
                    TaxableAmount = getTaxableAmount.substring(0, 7);
                    TaxableAmount2 = getTaxableAmount.substring(7, 14);
                    TaxableAmount3 = getTaxableAmount.substring(14);
                }


                if (getAmount.length() <= 6) {
                    Amount = getAmount;
                } else if (getAmount.length() > 6 && getAmount.length() <= 12) {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6);
                } else {
                    Amount = getAmount.substring(0, 6);
                    Amount2 = getAmount.substring(6, 12);
                    Amount3 = getAmount.substring(12);
                }


                String formattedLine1 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", ItemCode, "", Rate, GrossAmt, DiscPer, Disc, Excise, TaxableAmount, VatFivePer, Amount);
                String formattedLine2 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9%-6s%-4s", "", "", Rate2, GrossAmt2, DiscPer2, Disc2, Excise2, TaxableAmount2, VatFivePer2, Amount2);
                String formattedLine3 = String.format("%-10s%-2s%-7s%-7s%-6s%-7s%-8s%-9s%-6s%-4s", "", "", Rate3, GrossAmt3, DiscPer3, Disc3, Excise3, TaxableAmount3, VatFivePer3, Amount3);

                writeText(formattedLine1, mEmphasis, false, mCharsize, 0);

                if (!Rate2.isEmpty() || !DiscPer2.isEmpty() || !Disc2.isEmpty() ||
                        !Excise2.isEmpty() || !TaxableAmount2.isEmpty() || !VatFivePer2.isEmpty() || !Amount2.isEmpty()) {


                    writeText(formattedLine2, mEmphasis, false, mCharsize, 0);
                }


                if (!Rate3.isEmpty() || !DiscPer3.isEmpty() || !Disc3.isEmpty() ||
                        !Excise3.isEmpty() || !TaxableAmount3.isEmpty() || !VatFivePer3.isEmpty() || !Amount3.isEmpty()) {


                    writeText(formattedLine3, mEmphasis, false, mCharsize, 0);
                }


                writeText("(UOM:" + getQty + " " + UOM + ")" + " / " + Description, mEmphasis, false, mCharsize, 0);
                writeText("Invoice No: " + invoiceNumber, mEmphasis, false, mCharsize, 0);
                writeText("                                                                    ", mEmphasis, false, mCharsize, 0);

            }

            int labelWidth = 15; // Adjust this based on your desired width
            int numSpaces = labelWidth - "Total Cases:".length();

            // Create the spacing string
            String spacing = new String(new char[numSpaces]).replace('\0', ' ');

            JSONArray jTotal = object.getJSONArray("TOTAL");
            JSONObject jTOBject = jTotal.getJSONObject(0);

            String grossTotal = "", vatAmt = "", netAmt = "", excise = "", discount = "", total = "", mFinalGross = "";
            if (jTOBject.has("Gross Total")) {
                grossTotal = jTOBject.getString("Gross Total");
            }

            if (jTOBject.has("VAT")) {
                vatAmt = String.format("%.2f", Double.parseDouble(jTOBject.getString("VAT")));
            }

            if (jTOBject.has("NET")) {
                netAmt = String.format("%.2f", Double.parseDouble(jTOBject.getString("NET")));
            }

            if (jTOBject.has("EXCISE")) {
                excise = String.format("%.2f", Double.parseDouble(jTOBject.getString("EXCISE")));
            }

            if (jTOBject.has("Discount")) {
                discount = String.format("%.2f", Double.parseDouble(jTOBject.getString("Discount")));
            }

            if (jTOBject.has("Total Amount")) {
                total = String.format("%.2f", Double.parseDouble(jTOBject.getString("Total Amount")));
            }

            mFinalGross = String.valueOf(Double.parseDouble(netAmt) - Double.parseDouble(discount));


            String formattedTotalCase = String.format("%.2f", totalCase);
            String TotalCases = String.format("%s%-53s%s", spacing, "Total Cases:", alignRight(formattedTotalCase, 10));
            String NetAmount = String.format("%s%-50s%s", spacing, "Net Amount:", "AED" + alignRight(mFinalGross, 10));
            String TotalExcise = String.format("%s%-50s%s", spacing, "Total Excise:", "AED" + alignRight(excise, 10));
            String NetTaxableAmount = String.format("%s%-50s%s", spacing, "Net Taxable Amount:", "AED" + alignRight(netAmt, 10));
            String TotalVat = String.format("%s%-50s%s", spacing, "TotalVat:", "AED" + alignRight(vatAmt, 10));
            String TotalGross = String.format("%s%-50s%s", spacing, "Total Gross:", "AED" + alignRight(total, 10));

            String totalGrossWords = "";
            String Amount = total;
            int decimals = Amount.indexOf(".");
            String decimalValue = null;
            if (0 <= decimals) {
                decimalValue = Amount.substring(decimals + 1);
                Amount = Amount.substring(0, decimals).replaceAll("-", "");


                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    Long dataDecimal = Long.parseLong(decimalValue);
                    strWord = NumberToWord.convert(data1) + " and " + NumberToWord.convert(dataDecimal) + " fills";
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            } else {

                String strWord = "";
                try {
                    Long data1 = Long.parseLong(Amount);
                    strWord = NumberToWord.convert(data1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                totalGrossWords = strWord;
            }

// Convert the total gross to words
            //String totalGrossWords = converter.convertToWords(Double.parseDouble(total));

            writeText(TotalCases, mEmphasis, false, mCharsize, 0);
            writeText(NetAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalExcise, mEmphasis, false, mCharsize, 0);
            writeText(NetTaxableAmount, mEmphasis, false, mCharsize, 0);
            writeText(TotalVat, mEmphasis, false, mCharsize, 0);
            writeText(TotalGross, mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("Amount In Words: " + totalGrossWords + "", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);
            writeText("--------------------------------------------------------------------", mEmphasis, false, mCharsize, 0);

            writeText("                           Thank You", mEmphasis, false, mCharsize, 0);
            writeText("You can submit your concern anonymously to concern@masafi.com", mEmphasis, false, mCharsize, 0);
            BitmapFactory.Options optionsSocial = new BitmapFactory.Options();
            optionsSocial.inScaled = false;
            Bitmap bmpSocial = BitmapFactory.decodeResource(context.getResources(), R.drawable.social1, optionsSocial);
            if (bmpSocial == null) {
                Log.e("", "resource decoding is failed");
                return;
            }


            byte[] dataSocial = WoosimCustomImage.printColorBitmap(0, 0, 850, 150, bmpSocial);
            bmpSocial.recycle();

            sendData(WoosimCmd.setPageMode());
            sendData(dataSocial);
            sendData(WoosimCmd.PM_setStdMode());
//        writeText("                     - www.facebook.com/Masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.instagram.com/masafi/", mEmphasis, false, mCharsize, 0);
//        writeText("                     - www.linkedin.com/company/masafi/\n\n", mEmphasis, false, mCharsize, 0);
            writeText("      Customer Signature & Stamp        For MASAFI CO. (L.L.C.)        ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     |                           | |                           |    ", mEmphasis, false, mCharsize, 0);
            writeText("     ----------------------------- -----------------------------    ", mEmphasis, false, mCharsize, 0);

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            BarcodeFormat format = BarcodeFormat.CODE_128;
            int width = 400;
            int height = 200;

            // Generate barcode bitmap
            Bitmap barcodeBitmap = generateBarcode(object.getString("DOCUMENT NO"), format, width, height);

            // Print the barcode using Woosim printer
            if (barcodeBitmap != null) {
                int pageWidth = 780; // Set your page width here
                int barcodeWidth = barcodeBitmap.getWidth();
                int startX = (pageWidth - barcodeWidth) / 2;

                // Print the centered barcode
                byte[] dataBarcode = WoosimCustomImage.printColorBitmap(startX, 0, barcodeWidth, 150, barcodeBitmap);
                barcodeBitmap.recycle();

                sendData(WoosimCmd.setPageMode());
                sendData(dataBarcode);
                sendData(WoosimCmd.PM_setStdMode());
            }

            writeText("", mEmphasis, false, mCharsize, 0);
            writeText("", mEmphasis, false, mCharsize, 0);

            if (context instanceof PalletReturnActivity) {
                PalletReturnActivity.Companion.callbackFunction();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printTableInvoice(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Item Name;Qty;Price;Excise;Vat;Total";
        //String column = "Item Name;Qty;Price;Gross;Excise;Vat%;Vat;Total";
        String column2 = "Barcode;;;;;";
        Table table = null;
        Table table1 = null;
        table = new Table(column, ";", new int[]{33, 5, 7, 7, 6, 8});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);
        table1 = new Table(column2, ";", new int[]{33, 5, 7, 7, 6, 8});
        table1.setColumnAlignRight(false);
        mPrinter.printTable(table1);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String qty = jArr.getString(3);
                String UOM = jArr.getString(2);
                String price = jArr.getString(4);
                String disc = jArr.getString(9);
                String vat = jArr.getString(11);
                String net = jArr.getString(10);
                String Total = jArr.getString(12);
                String grossPrice = jArr.getString(5);
                String excisePrice = jArr.getString(6);
                String barcode = jArr.getString(13);
                String plu = jArr.getString(14);
                String mBarPLu = barcode + "     " + plu;
                String totalPrice = String.valueOf((Float.parseFloat(price) + Float.parseFloat(excisePrice)));

                //String columnRow = "" + name + ";" + qty + ";" + price + ";" + grossPrice + ";" + excisePrice + ";" + "5" + ";" + vat + ";" + Total;

                String columnRow = "" + name + ";" + qty + ";" + price + ";" + excisePrice + ";" + vat + ";" + Total;
                String columnRow1 = "" + mBarPLu + ";" + "" + ";" + "" + ";" + "" + ";" + "" + ";" + "";
                //String columnRow1 = "" + barcode + ";" + plu + ";" + ";" + ";" + ";" + ";" + ";";
                Table tableRow = null;
                Table tableRow1 = null;
                tableRow = new Table(columnRow, ";", new int[]{33, 5, 7, 7, 6, 8});
                tableRow.setColumnAlignRight(false);
                tableRow1 = new Table(columnRow1, ";", new int[]{33, 5, 7, 7, 6, 8});
                tableRow1.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);
                mPrinter.printTable(tableRow1);

                //myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableLoadRequest(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Item;UOM;Qty";
        Table table = null;
        table = new Table(column, ";", new int[]{42, 12, 12});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String name = jArr.getString(1);
                String qty = jArr.getString(3);
                String uom = jArr.getString(2);
                String Total = jArr.getString(12);

                String columnRow = name + ";" + uom + ";" + qty;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{42, 12, 12});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);
                //mPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableLoadSummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Item;Received UOM;Received Qty;Confirmed Qty;Variance Qty";
        Table table = null;
        table = new Table(column, ";", new int[]{26, 10, 10, 10, 10});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String date = jArr.getString(2);
                String invNo = jArr.getString(3);
                String amount = jArr.getString(4);
                String osAMt = jArr.getString(5);
                String revAmt = jArr.getString(6);

                String columnRow = date + ";" + invNo + ";" + amount + ";" + osAMt + ";" + revAmt;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{26, 10, 10, 10, 10});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableVanSummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "ITEM;UOM;QTY";
        Table table = null;
        table = new Table(column, ";", new int[]{42, 12, 12});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String date = jArr.getString(2);
                String invNo = jArr.getString(3);
                String amount = jArr.getString(4);

                String columnRow = date + ";" + invNo + ";" + amount;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{42, 12, 12});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableEndInventorySummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Item;UOM;Load;Fresh UL;Expiry;Damage";
        String column2 = ";Expiry;Varia.;Fresh;Unload";
        Table table = null;
        Table table1 = null;
        table = new Table(column, ";", new int[]{21, 9, 9, 9, 9, 9});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);
//        table1 = new Table(column2, ";", new int[]{30, 9, 9, 9, 9});
//        table1.setColumnAlignRight(true);
//        mPrinter.printTable(table1);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String uom = jArr.getString(2);
                String load = jArr.getString(3);
                String sale = jArr.getString(4);
                String returnQty = jArr.getString(5);
                String vari = jArr.getString(6);
                String fresh = jArr.getString(7);
                String unl = jArr.getString(8);
                String expQty = jArr.getString(9);

                Double unloadQTy = Double.parseDouble(vari) + Double.parseDouble(returnQty)
                        + Double.parseDouble(expQty) + Double.parseDouble(fresh);

                String columnRow = name + ";" + uom + ";" + load + ";" + fresh + ";" + expQty + ";" + returnQty;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{21, 9, 9, 9, 9, 9});
                tableRow.setColumnAlignRight(true);
                mPrinter.printTable(tableRow);

//                String columnRow1 = " " + ";" + expQty + ";" + vari + ";" + fresh + ";" + unl;
//                Table tableRow1 = null;
//                tableRow1 = new Table(columnRow1, ";", new int[]{30, 9, 9, 9, 9});
//                tableRow1.setColumnAlignRight(true);

                //    mPrinter.printTable(tableRow1);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableDERSummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Item;Unit;Qty";
        Table table = null;
        table = new Table(column, ";", new int[]{40, 13, 13});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String load = jArr.getString(2);
                String sale = jArr.getString(3);

                String columnRow = "" + name + ";" + load + ";" + sale;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{40, 13, 13});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableSalesSummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Tran. No.;Cus.No;Name;Sale";
        Table table = null;
        table = new Table(column, ";", new int[]{12, 12, 31, 12});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String inv = jArr.getString(1);
                String cust = jArr.getString(2);
                String name = jArr.getString(3);
                String sale = jArr.getString(4);
                String paid = jArr.getString(5);

                String columnRow = inv + ";" + cust + ";" + name + ";" + sale;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{12, 12, 31, 12});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableReturnSalesSummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Tran. No.;Cus.No;Name;Return";
        Table table = null;
        table = new Table(column, ";", new int[]{12, 12, 31, 12});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String inv = jArr.getString(1);
                String cust = jArr.getString(2);
                String name = jArr.getString(3);
                String sale = jArr.getString(4);
                String paid = jArr.getString(5);

                String columnRow = inv + ";" + cust + ";" + name + ";" + sale;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{12, 12, 31, 12});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableDepositeSummary(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Recep. No.;Cus.No;Name;Cheq No;Cheq Amt;Cash";
        Table table = null;
        table = new Table(column, ";", new int[]{12, 10, 18, 9, 9, 8});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String inv = jArr.getString(1);
                String cust = jArr.getString(2);
                String name = jArr.getString(3);
                String sale = jArr.getString(4);
                String paid = jArr.getString(5);
                String cash = jArr.getString(6);

                String columnRow = inv + ";" + cust + ";" + name + ";" + sale + ";" + paid + ";" + cash;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{12, 10, 18, 9, 9, 8});
                tableRow.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableDailyField(PrinterInstance mPrinter, JSONArray jData) {
        String column = "JP;Visited;JP%;Strike;Strike%;UP;UP%;Total";
        Table table = null;
        Table table1 = null;
        table = new Table(column, ";", new int[]{8, 9, 8, 9, 9, 8, 8, 8});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String JP = jArr.getString(0);
                String PLam = jArr.getString(1);
                String planP = jArr.getString(2);
                String strike = jArr.getString(3);
                String strikePer = jArr.getString(4);
                String UP = jArr.getString(5);
                String upPe = jArr.getString(6);
                String total = jArr.getString(7);

                String columnRow = JP + ";" + PLam + ";" + planP + ";" + strike + ";" + strikePer + ";" + UP + ";" + upPe + ";" + total;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{8, 9, 8, 9, 9, 8, 8, 8});
                tableRow.setColumnAlignRight(false);
                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableDailyFieldTotal(PrinterInstance mPrinter, JSONArray jData) {
        String column = "P/UP;Cus.No;Name;No.Inv;Sales;No.RV;RV;No.RP;RP Amount";
        Table table = null;
        Table table1 = null;
        table = new Table(column, ";", new int[]{7, 7, 11, 7, 7, 7, 7, 7, 7});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {

                JSONArray jArr = jData.getJSONArray(i);

                String JP = jArr.getString(0);
                String PLam = jArr.getString(1);
                String planP = jArr.getString(2);
                String strike = jArr.getString(3);
                String strikePer = jArr.getString(4);
                String UP = jArr.getString(5);
                String upPe = jArr.getString(6);
                String total = jArr.getString(7);
                String totalP = jArr.getString(8);

                String columnRow = JP + ";" + PLam + ";" + planP + ";" + strike + ";" + strikePer + ";" + UP + ";" + upPe + ";" + total + ";" + totalP;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{7, 7, 11, 7, 7, 7, 7, 7, 7});
                tableRow.setColumnAlignRight(false);
                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }


    public static void printTableReceipt(PrinterInstance mPrinter, JSONArray jData) {
        String column = "Date;Invoice No;Amount;O/s Amount;Recvd Amount";
        Table table = null;
        table = new Table(column, ";", new int[]{11, 11, 11, 11, 11, 11});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String date = jArr.getString(0);
                String invNo = jArr.getString(1);
                String amount = jArr.getString(2);
                String osAMt = jArr.getString(3);
                String revAmt = jArr.getString(4);

                String columnRow = date + ";" + invNo + ";" + amount + ";" + osAMt + ";" + revAmt;
                Table tableRow = null;
                tableRow = new Table(columnRow, ";", new int[]{11, 11, 11, 11, 11, 11});
                tableRow.setColumnAlignRight(true);

                mPrinter.printTable(tableRow);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public static void printTableReturn(PrinterInstance mPrinter, JSONArray jData) {

        String column = "Item Name;Qty;Price;Excise;Vat;Total";
        //String column = "Item Name;Qty;Price;Gross;Excise;Vat%;Vat;Total";
        String column2 = "Barcode;;;;;";

//        String column = "Item Name;Qty;Price;Gross;Disc;Net";
//        //String column = "Item Name;Qty;Price;Gross;Excise;Vat%;Vat;Total";
//        String column2 = "Barcode;UOM;Excise;Vat%;Vat;Total";
        Table table = null;
        Table table1 = null;
        table = new Table(column, ";", new int[]{33, 5, 7, 7, 6, 8});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);
        table1 = new Table(column2, ";", new int[]{33, 5, 7, 7, 6, 8});
        table1.setColumnAlignRight(false);
        mPrinter.printTable(table1);

        StringBuffer sb = new StringBuffer();
        sb.append("================================================================\n");
        mPrinter.printText(sb.toString());

        try {
            for (int i = 0; i < jData.length(); i++) {
                JSONArray jArr = jData.getJSONArray(i);
                String name = jArr.getString(1);
                String qty = jArr.getString(5);
                String UOM = jArr.getString(4);
                String price = jArr.getString(6);
                String disc = "0.0";
                String vat = jArr.getString(11);
                String net = jArr.getString(10);
                String Total = jArr.getString(12);
                String grossPrice = jArr.getString(7);
                String excisePrice = jArr.getString(8);
                String totalPrice = String.valueOf((Float.parseFloat(price) + Float.parseFloat(excisePrice)));
                String barcode = jArr.getString(13);
                String plu = jArr.getString(14);
                String mBarPLu = barcode + "       " + plu;

                //String columnRow = "" + name + ";" + qty + ";" + price + ";" + grossPrice + ";" + excisePrice + ";" + "5" + ";" + vat + ";" + Total;

                String columnRow = "" + name + ";" + qty + ";" + price + ";" + excisePrice + ";" + vat + ";" + Total;
                String columnRow1 = "" + mBarPLu + ";" + "" + ";" + "" + ";" + "" + ";" + "" + ";" + "";
                //String columnRow1 = "" + barcode + ";" + plu + ";" + ";" + ";" + ";" + ";" + ";";
                Table tableRow = null;
                Table tableRow1 = null;
                tableRow = new Table(columnRow, ";", new int[]{33, 5, 7, 7, 6, 8});
                tableRow.setColumnAlignRight(false);
                tableRow1 = new Table(columnRow1, ";", new int[]{33, 5, 7, 7, 6, 8});
                tableRow1.setColumnAlignRight(false);

                mPrinter.printTable(tableRow);
                mPrinter.printTable(tableRow1);

                //myPrinter.setPrinter(PrinterConstants.Command.PRINT_AND_WAKE_PAPER_BY_LINE, i++);

            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public void printTableTotalInvoice(PrinterInstance mPrinter, JSONObject jTOBject) {

        String grossTotal = "", netAmt = "0", vatAmt = "0", excise = "", discount = "", total = "", qty = "";

        String column;
        Table table = null;
        try {
            if (jTOBject.has("Gross Total")) {
                grossTotal = jTOBject.getString("Gross Total");
            }

            if (jTOBject.has("VAT")) {
                vatAmt = jTOBject.getString("VAT");
            }

            if (jTOBject.has("NET")) {
                netAmt = jTOBject.getString("NET");
            }

            if (jTOBject.has("EXCISE")) {
                excise = jTOBject.getString("EXCISE");
            }

            if (jTOBject.has("Discount")) {
                discount = jTOBject.getString("Discount");
            }

            if (jTOBject.has("Total Amount")) {
                total = jTOBject.getString("Total Amount");
            }

            if (jTOBject.has("Total Qty")) {
                qty = jTOBject.getString("Total Qty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (qty != "") {
            String mQty = "Total Qty", titleSub = " ";
            column = mQty + ";" + titleSub + ";" + qty;
            table = new Table(column, ";", new int[]{34, 20, 12});
            table.setColumnAlignRight(true);
            mPrinter.printTable(table);
        }

//        String name1 = "Total Gross Amount", titleSub = " ";
//        column = name1 + ";" + titleSub + ";" + grossTotal;
//        table = new Table(column, ";", new int[]{34, 20, 12});
//        table.setColumnAlignRight(true);
//        mPrinter.printTable(table);

//        String disc = "Discount Amount", titleDis = " ";
//        column = disc + ";" + titleDis + ";" + discount;
//        table = new Table(column, ";", new int[]{34, 20, 12});
//        table.setColumnAlignRight(true);
//        mPrinter.printTable(table);

        String net = "Net Amount", titleNet = " ";
        column = net + ";" + titleNet + ";" + netAmt;
        table = new Table(column, ";", new int[]{34, 20, 12});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        String exci = "Excise Amount", titleExc = " ";
        column = exci + ";" + titleExc + ";" + excise;
        table = new Table(column, ";", new int[]{34, 20, 12});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        String vat = "Vat Amount", titleVat = " ";
        column = vat + ";" + titleVat + ";" + vatAmt;
        table = new Table(column, ";", new int[]{34, 20, 12});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

        String tot = "Total Amount", titleTot = " ";
        column = tot + ";" + titleTot + ";" + total;
        table = new Table(column, ";", new int[]{34, 20, 12});
        table.setColumnAlignRight(true);
        mPrinter.printTable(table);

    }

    public static void printTableTotalLoadRequ(PrinterInstance mPrinter, JSONObject jTOBject) {

        String total = "", totalQty = "";

        String column;
        Table table = null;
        try {

            if (jTOBject.has("Total Amount")) {
                total = jTOBject.getString("Total Amount");
            }

            if (jTOBject.has("Total Qty")) {
                totalQty = jTOBject.getString("Total Qty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tot = "Total ", titleTot = " ";
        column = tot + ";" + titleTot + ";" + totalQty;
        table = new Table(column, ";", new int[]{42, 12, 12});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

    }

    public static void printTableTotalLoadSummary(PrinterInstance mPrinter, JSONObject jTOBject) {

        String total = "", totalQty = "", totalVQty = "";

        String column;
        Table table = null;
        try {

            if (jTOBject.has("Total RecvQty")) {
                total = jTOBject.getString("Total RecvQty");
            }

            if (jTOBject.has("Total ConQty")) {
                totalQty = jTOBject.getString("Total ConQty");
            }

            if (jTOBject.has("Total VariQty")) {
                totalVQty = jTOBject.getString("Total VariQty");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String tot = "Total ", titleTot = " ";
        column = tot + ";" + titleTot + ";" + total + ";" + totalQty + ";" + totalVQty;
        table = new Table(column, ";", new int[]{26, 10, 10, 10, 10});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

    }

    public static void printTableTotalVanSummary(PrinterInstance mPrinter, JSONObject jTOBject) {

        String total = "";

        String column;
        Table table = null;
        try {

            if (jTOBject.has("Total Qty")) {
                total = jTOBject.getString("Total Qty");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String tot = "Total ", titleTot = " ";
        column = tot + ";" + titleTot + ";" + total;
        table = new Table(column, ";", new int[]{42, 12, 12});
        table.setColumnAlignRight(false);
        mPrinter.printTable(table);

    }

    public void printInvoiceHeader(PrinterInstance myPrinter) {

//        Bitmap bitmap1 = BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.ic_barakat);
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
//                bitmap1, 200, 120, false);
//        myPrinter.printImage(resizedBitmap, PrinterConstants.PAlign.START, 0, 128);

        myPrinter.setFont(0, 0, 0, 1, 0);
        myPrinter.setPrinter(PrinterConstants.Command.ALIGN, PrinterConstants.Command.ALIGN_LEFT);
        StringBuffer sb = new StringBuffer();
        sb.append("Barakat Quality Plus\n");
        myPrinter.printText(sb.toString());
        myPrinter.setFont(0, 0, 0, 0, 0);

        String name1 = "P.O. Box 48989 Dubai", titleSub = "Tel: +971 4 8802121";
        String column = name1 + ";" + titleSub;
        Table table = new Table(column, ";", new int[]{33, 33});
        table.setColumnAlignRight(true);
        myPrinter.printTable(table);
//
        String fax = "Fax: +971 4 8802122", email = "Email: Retail.o@barakatgroup.ae";
        column = fax + ";" + email;
        table = new Table(column, ";", new int[]{33, 33});
        table.setColumnAlignRight(true);
        myPrinter.printTable(table);
//
        String trn = "TRN No: 10000927400003", web = "                    ";
        column = trn + ";" + web;
        table = new Table(column, ";", new int[]{33, 33});
        table.setColumnAlignRight(true);
        myPrinter.printTable(table);

    }

    public Bitmap getBitmap(String path) {
        try {
            Bitmap bitmap = null;
            File f = new File(path);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bmOptions);
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 100, false);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void setupPrint() {
        mPrintService = new BluetoothPrintService(mHandler);
        mWoosim = new WoosimCustomService(mHandler);
    }

    public void writeText(String text, boolean mEmphasis, boolean mUnderline, int mCharsize,
                          int mJustification) {
        try {
            byte text_array[] = text.getBytes("US-ASCII");
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            byteStream.write(WoosimCustomCmd.setTextStyle(mEmphasis, mUnderline, false, mCharsize, mCharsize));
            byteStream.write(WoosimCustomCmd.setTextAlign(mJustification));


            if (text_array != null) byteStream.write(text_array);
            byteStream.write(WoosimCmd.printData());
            sendData(WoosimCmd.initPrinter());
            sendData(byteStream.toByteArray());
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public class NumberToWordsConverter {
        private final String[] units = {
                "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
        };

        private final String[] tens = {
                "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
        };

        private final String[] teens = {
                "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        };

        private final String[] currencyNames = {
                "", "AED"
        };

        private final String[] fractionalNames = {
                "Fils"
        };

        public String convertToWords(double number) {
            if (number == 0) {
                return "Zero " + currencyNames[1];
            }

            long integerPart = (long) number;
            int fractionalPart = (int) Math.round((number - integerPart) * 100);

            String words = convertToWords((int) integerPart) + " " + currencyNames[1];

            if (fractionalPart > 0) {
                words += " and " + convertToWords(fractionalPart) + " " + fractionalNames[0];
            }

            return words;
        }

        private String convertToWords(int number) {
            if (number == 0) {
                return "";
            }

            if (number < 20) {
                return units[number];
            } else if (number < 100) {
                return tens[number / 10] + " " + units[number % 10];
            } else {
                return units[number / 100] + " Hundred " + convertToWords(number % 100);
            }
        }
    }

    private Bitmap generateBarcode(String data, BarcodeFormat format, int width, int height) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(data, format, width, height);
            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();
            int[] pixels = new int[matrixWidth * matrixHeight];
            for (int y = 0; y < matrixHeight; y++) {
                for (int x = 0; x < matrixWidth; x++) {
                    pixels[y * matrixWidth + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap barcodeBitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888);
            barcodeBitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight);
            return barcodeBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.engear.vansales.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Logger {
    private boolean isAppenlog;
    private Context context;
    private Activity activity;
    private String text;

    public Logger() {
        this.isAppenlog = true;
    }

    public void appendLog(Activity activity, String text) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.text = text;

        if (this.isAppenlog) {
//            File documentsFolder = new File(
//                    activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "SFA"
//            );

            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File documentsFolder = null;
            documentsFolder = new File(downloadDir,
                    "SFA");

            if (!documentsFolder.exists()) {
                documentsFolder.mkdirs();
            }

            deletePreviousLogFiles(context);  // Assuming this still works with new path

            File logFile = new File(documentsFolder,
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + ".txt"
            );

            if (!logFile.exists()) {
                try {
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(new Date().toString()).append(StringUtilities.LF);
                buf.append(text);
                buf.newLine();
                buf.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void appendLog(Context context, String text) {
        this.context = context;
        this.text = text;
        if (this.isAppenlog) {
            if (checkPermission()) {
                try {
//                    File documentsFolder = new File(
//                            context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
//                            "SFA"
//                    );

                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    File documentsFolder = null;
                    documentsFolder = new File(downloadDir,
                            "SFA");

                    if (!documentsFolder.exists()) {
                        boolean created = documentsFolder.mkdirs();
                        Log.d("appendLog", "Directory created: " + created);
                    }

                    File logFile = new File(documentsFolder,
                            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + ".txt"
                    );

                    Log.d("appendLog", "Writing to file: " + logFile.getAbsolutePath());

                    if (!logFile.exists()) {
                        boolean fileCreated = logFile.createNewFile();
                        Log.d("appendLog", "File created: " + fileCreated);
                    }

                    BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
                    buf.append(new Date().toString()).append("\n");
                    buf.append(text);
                    buf.newLine();
                    buf.close();

                } catch (IOException e) {
                    Log.e("LogFileError", "IOException writing log file", e);
                } catch (Exception e) {
                    Log.e("LogFileError", "General error writing log file", e);
                }
            } else {
                requestPermission();
            }
        }
    }

    public void appendLogContext(Context context, String text) {
        if (!isAppenlog) return;

        try {
//            File documentsFolder = new File(
//                    context.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
//                    "SFA"
//            );

            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File documentsFolder = null;
            documentsFolder = new File(downloadDir,
                    "SFA");
            if (!documentsFolder.exists()) {
                boolean created = documentsFolder.mkdirs();
                Log.d("appendLog", "Directory created: " + created);
            }

            File logFile = new File(documentsFolder,
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()) + ".txt"
            );

            Log.d("appendLog", "Writing to file: " + logFile.getAbsolutePath());

            if (!logFile.exists()) {
                boolean fileCreated = logFile.createNewFile();
                Log.d("appendLog", "File created: " + fileCreated);
            }

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(new Date().toString()).append("\n");
            buf.append(text);
            buf.newLine();
            buf.close();

        } catch (IOException e) {
            Log.e("LogFileError", "IOException writing log file", e);
        } catch (Exception e) {
            Log.e("LogFileError", "General error writing log file", e);
        }
    }

    private boolean checkPermission() {

        int storageresult = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraresult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (storageresult == PackageManager.PERMISSION_GRANTED && cameraresult == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(context, "Write External Storage permission allows us to do write files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
//        } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 100);
        }
//        }
    }

    protected void deletePreviousLogFiles(Context context) {
        String storagestate = Environment.getExternalStorageState();
        File documentsFolder = null;
        if (storagestate.equals(Environment.MEDIA_MOUNTED)) {
            documentsFolder = new File(Environment.getExternalStorageDirectory(),
                    "Download" + File.separator + "SFA");
            if (documentsFolder.exists()) {
                File logFile = new File(documentsFolder.getPath() + File.separator + new SimpleDateFormat("yyyy-MM-dd").format(giveLowerDate(new Date())) + ".txt");
                if (logFile.exists()) {
                    try {
                        if (logFile.delete()) {
                            logFile.getCanonicalFile().delete();
                            if (logFile.exists()) {
                                context.deleteFile(logFile.getName());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //File logFile = new File(documentsFolder + "/sfa/sfa_log_+" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt");

        }
    }

    public Date giveLowerDate(Date date) {
        Date date1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date); // convert your date to Calendar object
        int daysToDecrement = -5;
        cal.add(Calendar.DATE, daysToDecrement);
        date1 = cal.getTime(); // again get back your date object
        return date1;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appendLog(activity, text);
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}

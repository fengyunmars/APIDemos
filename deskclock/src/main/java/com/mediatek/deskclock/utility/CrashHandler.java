package com.mediatek.deskclock.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler{
	public static final String TAG = "CrashHandler";  
    
	// The default UncaughtException processing class
    private Thread.UncaughtExceptionHandler mDefaultHandler;  
	// CrashHandler examples
    private static CrashHandler INSTANCE = new CrashHandler();  
	// Program Context Object
    private Context mContext;  
	// Used to store device information and abnormal information
    private Map<String, String> infos = new HashMap<String, String>();  
  
	// Part for formatting dates as the log file name
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");  
  
	/** Ensure that only one instance CrashHandler */
    private CrashHandler() {  
    }  
  
 	/** Get CrashHandler instance, the Singleton pattern */
    public static CrashHandler getInstance() {  
        return INSTANCE;  
    }  
  
    /** 
     * Initialization
     *  
     * @param context 
     */  
    public void init(Context context) {  
        mContext = context;  
        // Get the system default processor UncaughtException
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();  
        // Set the default program for the processor that CrashHandler
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }  
  
    /** 
     * When UncaughtException occurs will turn this function to handle
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {  
        if (!handleException(ex) && mDefaultHandler != null) {  
        	// If the user does not allow the system to handle the default exception handler to handle
            mDefaultHandler.uncaughtException(thread, ex);  
        } else {  
            try {  
                Thread.sleep(3000);  
            } catch (InterruptedException e) {  
                Log.e(TAG, "error : ", e);  
            }  
            //exit the program 
            android.os.Process.killProcess(android.os.Process.myPid());  
            System.exit(1);  
        }  
    }  
  
    /** 
     * Custom error handling, error information is sent to collect error reports and other operations are completed here.
     *  
     * @param ex 
     * @return true:If the exception processing information; otherwise it returns false.
     */  
    private boolean handleException(Throwable ex) {  
        if (ex == null) {  
            return false;  
        }  
        // Use Toast to display exception information
        new Thread() {  
            @Override  
            public void run() {  
                Looper.prepare();  
                Toast.makeText(mContext, "Sorry, abnormal procedures,app will exit.", Toast.LENGTH_LONG).show();  
                Looper.loop();  
            }  
        }.start();  
        // Parameter information collection device
        collectDeviceInfo(mContext);  
        // Save the log file
        saveCrashInfo2File(ex);  
        return true;  
    }  
      
    /** 
     * Collecting device parameter information
     * @param ctx 
     */  
    public void collectDeviceInfo(Context ctx) {  
        try {  
            PackageManager pm = ctx.getPackageManager();  
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);  
            if (pi != null) {  
                String versionName = pi.versionName == null ? "null" : pi.versionName;  
                String versionCode = pi.versionCode + "";  
                infos.put("versionName", versionName);  
                infos.put("versionCode", versionCode);  
            }  
        } catch (NameNotFoundException e) {  
            Log.e(TAG, "an error occured when collect package info", e);  
        }  
        Field[] fields = Build.class.getDeclaredFields();  
        for (Field field : fields) {  
            try {  
                field.setAccessible(true);  
                infos.put(field.getName(), field.get(null).toString());  
                Log.d(TAG, field.getName() + " : " + field.get(null));  
            } catch (Exception e) {  
                Log.e(TAG, "an error occured when collect crash info", e);  
            }  
        }  
    }  
  
    /** 
     * Save the error messages to a file
     *  
     * @param ex 
     * @return  the file name, easy to transfer files to the server
     */  
    private String saveCrashInfo2File(Throwable ex) {  
          
        StringBuffer sb = new StringBuffer();  
        for (Map.Entry<String, String> entry : infos.entrySet()) {  
            String key = entry.getKey();  
            String value = entry.getValue();  
            sb.append(key + "=" + value + "\n");  
        }  
          
        Writer writer = new StringWriter();  
        PrintWriter printWriter = new PrintWriter(writer);  
        ex.printStackTrace(printWriter);  
        Throwable cause = ex.getCause();  
        while (cause != null) {  
            cause.printStackTrace(printWriter);  
            cause = cause.getCause();  
        }  
        printWriter.close();  
        String result = writer.toString();  
        sb.append(result);  
        try {  
            long timestamp = System.currentTimeMillis();  
            String time = formatter.format(new Date());  
            String fileName = "crash-" + time + "-" + timestamp + ".log";  
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                String path = "/sdcard/crash/";  
                File dir = new File(path);  
                if (!dir.exists()) {  
                    dir.mkdirs();  
                }  
                FileOutputStream fos = new FileOutputStream(path + fileName);  
                fos.write(sb.toString().getBytes());
                fos.close();  
            }
            return fileName;  
        } catch (Exception e) {  
            Log.e(TAG, "an error occured while writing file...", e);  
        }
        return null;  
    }  
}

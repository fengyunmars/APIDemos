/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.content;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.
import com.example.android.apis.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
* Demonstration of styled text resources.
*/
public class ExternalStorage extends Activity {
    ViewGroup mLayout;

    static class Item {
        View mRoot;
        Button mCreate;
        Button mDelete;
    }

    Item mExternalStoragePublicFile;
    Item mExternalStoragePublicPicture;
    Item mExternalStorageFile;
    Item mExternalFilesDirPicture;
    Item mExternalFilesDirFile;
    Item mInternalGetFilesDir;
//    Item mInternalStoragePrivateFile;

    // BEGIN_INCLUDE(monitor_storage)
    BroadcastReceiver mExternalStorageReceiver;
    boolean mExternalStorageAvailable = false;
    boolean mExternalStorageWriteable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.external_storage);
        mLayout = (ViewGroup)findViewById(R.id.layout);
//        mExternalStoragePublicFile = createStorageControls(
//                "getExternalStoragePublicDirectory(null)",
//                Environment.getExternalStoragePublicDirectory(null),
//                new View.OnClickListener() {
//                    public void onClick(View v) {
//                        createExternalStoragePublicFile();
//                        updateExternalStorageState();
//                    }
//                },
//                new View.OnClickListener() {
//                    public void onClick(View v) {
//                        deleteExternalStoragePublicFile();
//                        updateExternalStorageState();
//                    }
//                });
//        mLayout.addView(mExternalStoragePublicFile.mRoot);
        mExternalStoragePublicPicture = createStorageControls(
                "getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)",
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        createExternalStoragePublicPicture();
                        updateExternalStorageState();
                    }
                },
                new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteExternalStoragePublicPicture();
                        updateExternalStorageState();
                    }
                });
        mLayout.addView(mExternalStoragePublicPicture.mRoot);
        mExternalStorageFile = createStorageControls(
                "getExternalStorageDirectory()",
                Environment.getExternalStorageDirectory(),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        createExternalStorageFile();
                        updateExternalStorageState();
                    }
                },
                new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteExternalStorageFile();
                        updateExternalStorageState();
                    }
                });
        mLayout.addView(mExternalStorageFile.mRoot);

        mExternalFilesDirFile = createStorageControls(
                "File getExternalFilesDir(null)",
                getExternalFilesDir(null),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        createExternalFilesDirFile();
                        updateExternalStorageState();
                    }
                },
                new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteExternalFilesDirFile();
                        updateExternalStorageState();
                    }
                });
        mLayout.addView(mExternalFilesDirFile.mRoot);

        mExternalFilesDirPicture = createStorageControls(
                "Picture getExternalFilesDir",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        createExternalFilesDirPicture();
                        updateExternalStorageState();
                    }
                },
                new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteExternalFilesDirPicture();
                        updateExternalStorageState();
                    }
                });
        mLayout.addView(mExternalFilesDirPicture.mRoot);

        mInternalGetFilesDir = createStorageControls(
                "File getFilesDir()",
                getFilesDir(),
                new View.OnClickListener() {
                    public void onClick(View v) {
                        createInternalFilesDirFile();
                        updateExternalStorageState();
                    }
                },
                new View.OnClickListener() {
                    public void onClick(View v) {
                        deleteInternalFilesDirFile();
                        updateExternalStorageState();
                    }
                });
        mLayout.addView(mInternalGetFilesDir.mRoot);

        startWatchingExternalStorage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopWatchingExternalStorage();
    }

    void handleExternalStorageState(boolean available, boolean writeable) {
//        boolean has = hasExternalStoragePublicFile();
//        mExternalStoragePublicFile.mCreate.setEnabled(writeable && !has);
//        mExternalStoragePublicFile.mDelete.setEnabled(writeable && has);
        boolean has = hasExternalStoragePublicPicture();
        mExternalStoragePublicPicture.mCreate.setEnabled(writeable && !has);
        mExternalStoragePublicPicture.mDelete.setEnabled(writeable && has);
        has = hasExternalStorageFile();
        mExternalStorageFile.mCreate.setEnabled(writeable && !has);
        mExternalStorageFile.mDelete.setEnabled(writeable && has);
        has = hasExternalFilesDirFile();
        mExternalFilesDirFile.mCreate.setEnabled(writeable && !has);
        mExternalFilesDirFile.mDelete.setEnabled(writeable && has);
        has = hasExternalFilesDirPicture();
        mExternalFilesDirPicture.mCreate.setEnabled(writeable && !has);
        mExternalFilesDirPicture.mDelete.setEnabled(writeable && has);
        has = hasInternalFilesDirFile();
        mInternalGetFilesDir.mCreate.setEnabled(writeable && !has);
        mInternalGetFilesDir.mDelete.setEnabled(writeable && has);
    }

    void updateExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        handleExternalStorageState(mExternalStorageAvailable,
                mExternalStorageWriteable);
    }

    void startWatchingExternalStorage() {
        mExternalStorageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("test", "Storage: " + intent.getData());
                updateExternalStorageState();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        registerReceiver(mExternalStorageReceiver, filter);
        updateExternalStorageState();
    }

    void stopWatchingExternalStorage() {
        unregisterReceiver(mExternalStorageReceiver);
    }
 // END_INCLUDE(monitor_storage)

 // BEGIN_INCLUDE(public_picture)
    void createExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(R.drawable.balloons);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    void deleteExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and delete the file.  If external
        // storage is not currently mounted this will fail.
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");
        file.delete();
    }

    boolean hasExternalStoragePublicPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");
        return file.exists();
    }
// END_INCLUDE(public_picture)


//  BEGIN_INCLUDE(public_file)
    void createExternalStoragePublicFile() {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStoragePublicDirectory(null);
        File file = new File(path, "DemoPicture.jpg");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(R.drawable.balloons);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    void deleteExternalStoragePublicFile() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and delete the file.  If external
        // storage is not currently mounted this will fail.
        File path = Environment.getExternalStoragePublicDirectory(null);
        File file = new File(path, "DemoPicture.jpg");
        file.delete();
    }

    boolean hasExternalStoragePublicFile() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = Environment.getExternalStoragePublicDirectory(null);
        File file = new File(path, "DemoPicture.jpg");
        return file.exists();
    }
// END_INCLUDE(public_file)

//  BEGIN_INCLUDE(public_file)
    void createExternalStorageFile() {
        // Create a path where we will place our picture in the user's
        // public pictures directory.  Note that you should be careful about
        // what you place here, since the user often manages these files.  For
        // pictures and other media owned by the application, consider
        // Context.getExternalMediaDir().
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "DemoPicture.jpg");

        try {
            // Make sure the Pictures directory exists.
            path.mkdirs();

            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(R.drawable.balloons);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    void deleteExternalStorageFile() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and delete the file.  If external
        // storage is not currently mounted this will fail.
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "DemoPicture.jpg");
        file.delete();
    }

    boolean hasExternalStorageFile() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = Environment.getExternalStorageDirectory();
        File file = new File(path, "DemoPicture.jpg");
        return file.exists();
    }
//     END_INCLUDE(public_file)

// BEGIN_INCLUDE(private_picture)
    void createExternalFilesDirPicture() {
        // Create a path where we will place our picture in our own private
        // pictures directory.  Note that we don't really need to place a
        // picture in DIRECTORY_PICTURES, since the media scanner will see
        // all media in these directories; this may be useful with other
        // media types such as DIRECTORY_MUSIC however to help it classify
        // your media for display to the user.
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(path, "DemoPicture.jpg");

        try {
            // Very simple code to copy a picture from the application's
            // resource into the external file.  Note that this code does
            // no error checking, and assumes the picture is small (does not
            // try to copy it in chunks).  Note that if external storage is
            // not currently mounted this will silently fail.
            InputStream is = getResources().openRawResource(R.drawable.balloons);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data);
            os.write(data);
            is.close();
            os.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[] { file.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (IOException e) {
            // Unable to create file, likely because external storage is
            // not currently mounted.
            Log.w("ExternalStorage", "Error writing " + file, e);
        }
    }

    void deleteExternalFilesDirPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and delete the file.  If external
        // storage is not currently mounted this will fail.
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (path != null) {
            File file = new File(path, "DemoPicture.jpg");
            file.delete();
        }
    }

    boolean hasExternalFilesDirPicture() {
        // Create a path where we will place our picture in the user's
        // public pictures directory and check if the file exists.  If
        // external storage is not currently mounted this will think the
        // picture doesn't exist.
        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (path != null) {
            File file = new File(path, "DemoPicture.jpg");
            return file.exists();
        }
        return false;
    }
// END_INCLUDE(private_picture)

// BEGIN_INCLUDE(private_file)
     void createExternalFilesDirFile() {
         // Create a path where we will place our private file on external
         // storage.
         File file = new File(getExternalFilesDir(null), "DemoFile.jpg");

         try {
             // Very simple code to copy a picture from the application's
             // resource into the external file.  Note that this code does
             // no error checking, and assumes the picture is small (does not
             // try to copy it in chunks).  Note that if external storage is
             // not currently mounted this will silently fail.
             InputStream is = getResources().openRawResource(R.drawable.balloons);
             OutputStream os = new FileOutputStream(file);
             byte[] data = new byte[is.available()];
             is.read(data);
             os.write(data);
             is.close();
             os.close();
         } catch (IOException e) {
             // Unable to create file, likely because external storage is
             // not currently mounted.
             Log.w("ExternalStorage", "Error writing " + file, e);
         }
     }

     void deleteExternalFilesDirFile() {
         // Get path for the file on external storage.  If external
         // storage is not currently mounted this will fail.
         File file = new File(getExternalFilesDir(null), "DemoFile.jpg");
         if (file != null) {
             file.delete();
         }
     }

     boolean hasExternalFilesDirFile() {
         // Get path for the file on external storage.  If external
         // storage is not currently mounted this will fail.
         File file = new File(getExternalFilesDir(null), "DemoFile.jpg");
         if (file != null) {
             return file.exists();
         }
         return false;
     }
 // END_INCLUDE(private_file)

//  BEGIN_INCLUDE(private_file)
    void createInternalFilesDirFile() {
         // Create a path where we will place our private file on external
         // storage.
         File file = new File(getFilesDir(), "DemoFile.jpg");

         try {
             // Very simple code to copy a picture from the application's
             // resource into the external file.  Note that this code does
             // no error checking, and assumes the picture is small (does not
             // try to copy it in chunks).  Note that if external storage is
             // not currently mounted this will silently fail.
             InputStream is = getResources().openRawResource(R.drawable.balloons);
             OutputStream os = new FileOutputStream(file);
             byte[] data = new byte[is.available()];
             is.read(data);
             os.write(data);
             is.close();
             os.close();
         } catch (IOException e) {
             // Unable to create file, likely because external storage is
             // not currently mounted.
             Log.w("InternalStorage", "Error writing " + file, e);
         }
     }

     void deleteInternalFilesDirFile() {
         // Get path for the file on external storage.  If external
         // storage is not currently mounted this will fail.
         File file = new File(getFilesDir(), "DemoFile.jpg");
         if (file != null) {
             file.delete();
         }
     }

     boolean hasInternalFilesDirFile() {
         // Get path for the file on external storage.  If external
         // storage is not currently mounted this will fail.
         File file = new File(getFilesDir(), "DemoFile.jpg");
         if (file != null) {
             return file.exists();
         }
         return false;
     }
 // END_INCLUDE(private_file)

    Item createStorageControls(CharSequence label, File path,
            View.OnClickListener createClick,
            View.OnClickListener deleteClick) {
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        Item item = new Item();
        item.mRoot = inflater.inflate(R.layout.external_storage_item, null);
        TextView tv = (TextView)item.mRoot.findViewById(R.id.label);
        tv.setText(label);
        if (path != null) {
            tv = (TextView)item.mRoot.findViewById(R.id.path);
            tv.setText(path.toString());
        }
        item.mCreate = (Button)item.mRoot.findViewById(R.id.create);
        item.mCreate.setOnClickListener(createClick);
        item.mDelete = (Button)item.mRoot.findViewById(R.id.delete);
        item.mDelete.setOnClickListener(deleteClick);
        return item;
    }
}

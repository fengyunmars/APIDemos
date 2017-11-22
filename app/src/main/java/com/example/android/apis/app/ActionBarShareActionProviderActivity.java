/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.example.android.apis.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;

import com.example.android.apis.R;
import com.fengyun.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * This activity demonstrates 显示 how to use an {@link android.view.ActionProvider}
 * for adding functionality to the Action Bar. In particular this demo is adding
 * a menu item with ShareActionProvider as its action provider. The
 * ShareActionProvider is responsible for managing the UI for sharing actions.
 */
public class ActionBarShareActionProviderActivity extends Activity {

    private static final String SHARED_FILE_NAME = "shared.png";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        copyPrivateRawResuorceToPubliclyAccessibleFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your menu.
        getMenuInflater().inflate(R.menu.action_bar_share_action_provider, menu);

        // Set file with share history to the provider and set the share intent.
        MenuItem actionItem = menu.findItem(R.id.menu_item_share_action_provider_action_bar);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // Note that you can set/change the intent any time,
        // say when the user has selected an image.
        actionProvider.setShareIntent(createShareIntent());

        // Set file with share history to the provider and set the share intent.
        MenuItem overflowItem = menu.findItem(R.id.menu_item_share_action_provider_overflow);
        ShareActionProvider overflowProvider =
            (ShareActionProvider) overflowItem.getActionProvider();
        overflowProvider.setShareHistoryFileName(
            ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        // Note that you can set/change the intent any time,
        // say when the user has selected an image.
        overflowProvider.setShareIntent(createShareIntent());

        return true;
    }

    /**
     * Creates a sharing {@link Intent}.
     *
     * @return The sharing intent.
     */
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
//        File file = getFileStreamPath("shared.png");
//        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File file = new File(dir, SHARED_FILE_NAME);
        File file = new File(this.getExternalFilesDir(""),SHARED_FILE_NAME);
        Uri uri = FileUtils.getUriForFile(this,file);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        shareIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        return shareIntent;
    }

    /**
     * Copies a private raw resource content to a publicly readable
     * file such that the latter can be shared with other applications.
     */
    private void copyPrivateRawResuorceToPubliclyAccessibleFile() {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        inputStream = getResources().openRawResource(R.raw.robot);
        try {
            //        outputStream = openFileOutput(SHARED_FILE_NAME,
            //        Context.MODE_WORLD_READABLE | Context.MODE_APPEND);
//            outputStream = openFileOutput(SHARED_FILE_NAME, Context.MODE_APPEND);
//            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            File file = new File(dir, SHARED_FILE_NAME);
            File file = new File(this.getExternalFilesDir(""), SHARED_FILE_NAME);
            outputStream = new FileOutputStream(file);
            //dir.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileUtils.inputStreamToOutputStream(inputStream,outputStream);
    }
}

package com.simplewidget;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint;
import com.facebook.react.defaults.DefaultReactActivityDelegate;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import androidx.work.ExistingPeriodicWorkPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MainActivity extends ReactActivity {

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "SimpleWidget";
  }

  private PeriodicWorkRequest workRequest;
  public static ArrayList<String> allNotes = new ArrayList<String>();
  public static String fileError;
  public static int timesPressed = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

      Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      intent.setType("application/json");

      // Optionally, specify a URI for the file that should appear in the
      // system file picker when it loads.
      Uri pickerInitialUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/simplenote.json");
      intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

      startActivityForResult(intent, 999);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode,
                               Intent resultData) {
    super.onActivityResult(requestCode, resultCode, resultData);
    if (requestCode == 999 && resultCode == Activity.RESULT_OK) {
      // The result data contains a URI for the document or directory that
      // the user selected.
      if (resultData != null) {
        Uri uri = resultData.getData();
        String mimeType = this.getContentResolver().getType(uri);

        if (mimeType != null && mimeType.equals("application/json")) {
            try {
              // read the json file into a json object
              StringBuilder jsonContent = new StringBuilder();
              InputStream inputStream = this.getContentResolver().openInputStream(uri);
              byte[] buffer = new byte[1024];
              int read;
              while ((read = inputStream.read(buffer)) != -1) {
                jsonContent.append(new String(buffer, 0, read));
              }
              inputStream.close();
              // extract all notes as an array of json objects
              JSONArray allNotesJSON = new JSONObject(jsonContent.toString()).getJSONArray("activeNotes");
              JSONObject[] allNotesRandomized = new JSONObject[allNotesJSON.length()];
              for (int i = 0; i < allNotesJSON.length(); i++) {
                allNotesRandomized[i] = allNotesJSON.getJSONObject(i);
              }
              // randomize the order of all notes
              Collections.shuffle(Arrays.asList(allNotesRandomized));
              // go through each note and
              for (int i = 0; i < allNotesRandomized.length; i++) {
                JSONObject selectedNote = allNotesRandomized[i];
                // if this note is marked private, skip it
                JSONArray tags = selectedNote.getJSONArray("tags");
                boolean cont = false;
                for (int ii = 0; ii < tags.length(); ii++) {
                  if ("private".equals(tags.getString(ii))) {
                    cont = true;
                  }
                }
                if (cont) {
                  continue;
                }
                // otherwise, break up its content by lines
                String content = selectedNote.getString("content");
                String[] contentSplit = content.split("\n");
                for (int j = 0; j < contentSplit.length; j++) {
                  if (contentSplit[j].length() > 0) {
                    // add each line (ignoring blank lines) to our total notes array
                    // (here the meaning of "note" changes from a text file to a single line of the text file)
                    if (contentSplit[j] != "") {
                      this.allNotes.add(contentSplit[j]);
                    }
                  }
                }
              }
            }
            catch (JSONException e) {
              this.fileError = "ERR:" + e.toString();
            }
            catch (IOException e) {
              this.fileError = "ERR:" + e.toString();
            }
        }
      }
    }
    //create the work request
    workRequest = new PeriodicWorkRequest.Builder(BackgroundWorker.class, 15, TimeUnit.MINUTES).build();
    //enqueue the work request
    WorkManager.getInstance(this).enqueueUniquePeriodicWork("simplewidget", ExistingPeriodicWorkPolicy.REPLACE, workRequest);
  }


  /**
   * Returns the instance of the {@link ReactActivityDelegate}. Here we use a util class {@link
   * DefaultReactActivityDelegate} which allows you to easily enable Fabric and Concurrent React
   * (aka React 18) with two boolean flags.
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new DefaultReactActivityDelegate(
        this,
        getMainComponentName(),
        // If you opted-in for the New Architecture, we enable the Fabric Renderer.
        DefaultNewArchitectureEntryPoint.getFabricEnabled(), // fabricEnabled
        // If you opted-in for the New Architecture, we enable Concurrent React (i.e. React 18).
        DefaultNewArchitectureEntryPoint.getConcurrentReactEnabled() // concurrentRootEnabled
        );
  }
}

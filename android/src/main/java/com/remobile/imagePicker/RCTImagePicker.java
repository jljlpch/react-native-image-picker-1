/**
 * An Image Picker Plugin for React-Native.
 */
package com.remobile.imagePicker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;

import com.facebook.common.logging.FLog;
import com.remobile.cordova.*;
import com.facebook.react.bridge.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class RCTImagePicker extends ReactContextBaseJavaModule {
    public static String LOG_TAG = "ImagePicker";

    private CallbackContext callbackContext;
    private JSONObject params;

    private Callback callback;
    private Activity activity;

    public RCTImagePicker(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        this.activity = activity;
    }

    @Override
    public String getName() { return "RCTImagePicker"; }
    protected Activity getActivity() { return activity; }

    @ReactMethod
    public void getPictures(ReadableArray args, Callback success, Callback error) throws Exception {
        String action = "getPictures";
        try {
            this.execute(action, JsonConvert.reactToJSON(args), new CallbackContext(success, error));
        } catch (Exception ex) {
            FLog.e(LOG_TAG, "Unexpected error:" + ex.getMessage());
        }
    }

    public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.params = args.getJSONObject(0);
        if (action.equals("getPictures")) {
            Intent intent = new Intent(getActivity(), MultiImageChooserActivity.class);
            int max = 20;
            int desiredWidth = 0;
            int desiredHeight = 0;
            int quality = 100;
            if (this.params.has("maximumImagesCount")) {
                max = this.params.getInt("maximumImagesCount");
            }
            if (this.params.has("width")) {
                desiredWidth = this.params.getInt("width");
            }
            if (this.params.has("height")) {
                desiredWidth = this.params.getInt("height");
            }
            if (this.params.has("quality")) {
                quality = this.params.getInt("quality");
            }
            intent.putExtra("MAX_IMAGES", max);
            intent.putExtra("WIDTH", desiredWidth);
            intent.putExtra("HEIGHT", desiredHeight);
            intent.putExtra("QUALITY", quality);
            getActivity().startActivityForResult(intent, 0);
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> fileNames = data.getStringArrayListExtra("MULTIPLEFILENAMES");
            JSONArray res = new JSONArray(fileNames);
            this.callbackContext.success(res);
        } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
            String error = data.getStringExtra("ERRORMESSAGE");
            this.callbackContext.error(error);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            this.callbackContext.error("cancel");
        } else {
            this.callbackContext.error("No images selected");
        }
    }
}
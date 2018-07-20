package com.autochip.myvehicle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

public interface OnImageUtilsListener {
    void onBitmapCompressed(String sMessage, int nCase, Bitmap bitmap, Intent intent, Uri outputFileUri);
}

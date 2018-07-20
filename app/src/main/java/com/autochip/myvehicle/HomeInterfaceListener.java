package com.autochip.myvehicle;

import android.net.Uri;

public interface HomeInterfaceListener {
    void onHomeCalled(String sMessage, int nCase, String sActivityName, Uri outputFileUri);
}

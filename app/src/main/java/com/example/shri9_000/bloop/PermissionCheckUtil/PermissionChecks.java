package com.example.shri9_000.bloop.PermissionCheckUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;


public class PermissionChecks {

    @SuppressLint("NewApi")
    public static boolean hasPermission(Context context, String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }
}

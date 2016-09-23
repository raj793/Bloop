package com.example.shri9_000.bloop.PermissionCheckUtil;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;


public class BaseActivity  extends AppCompatActivity {
    private static final int REQUEST_ALL_MISSING_PERMISSIONS=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!hasAllRequiredPermission()){
            requestAllRequiredPermission();

        }

    }

    @Override
    protected void onResume() {
                super.onResume();
        if(!hasAllRequiredPermission()){
            requestAllRequiredPermission();

        }

    }

    /**
     *@return All permission which this activity needs
     */
    protected String[] getRequiredPermision(){
        return new String[0];
    }
    /**
    *Called when all need permissions granted
     */
    protected void onAllRequiredPermissionGranted(){
        //Do nothing here
    }
    private boolean hasAllRequiredPermission(){
        for( String permission : getRequiredPermision()){
            if(!PermissionChecks.hasPermission(getApplicationContext(),permission)){
                return false;
            }
        }
        return true;
    }
@SuppressLint("NewApi")
private void requestAllRequiredPermission(){
    ArrayList<String> notGrantedPermission = new ArrayList<>();

    for(String permission : getRequiredPermision()){
        if(!PermissionChecks.hasPermission(getApplicationContext(),permission)){
            notGrantedPermission.add(permission);
        }
    }
    if(notGrantedPermission.size() > 0){
        requestPermissions(notGrantedPermission.toArray(new String[notGrantedPermission.size()]), REQUEST_ALL_MISSING_PERMISSIONS);
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ALL_MISSING_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
onAllRequiredPermissionGranted();
                } else {
                    Toast.makeText(getApplicationContext(),"Please allow all Permission",Toast.LENGTH_SHORT).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}

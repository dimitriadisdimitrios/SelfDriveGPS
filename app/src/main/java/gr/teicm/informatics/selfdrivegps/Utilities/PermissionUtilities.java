package gr.teicm.informatics.selfdrivegps.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashMap;
import java.util.Map;

import gr.teicm.informatics.selfdrivegps.Activities.MainActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PermissionUtilities extends Activity {
    private static final int PERMISSION_ALL = 0;
    private static final int REQUEST_LOCATION = 199;
    private Handler h;
    private Runnable r;
    private String[] PERMISSIONS = {ACCESS_FINE_LOCATION};
//    SharedPreferences mPrefs;
//    final String settingScreenShownPref = "settingScreenShown";
//    final String versionCheckedPref = "versionChecked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        h = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
  /*          // (OPTIONAL) these lines to check if the `First run` ativity is required
                int versionCode = BuildConfig.VERSION_CODE;
                String versionName = BuildConfig.VERSION_NAME;

                mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = mPrefs.edit();

                Boolean settingScreenShown = mPrefs.getBoolean(settingScreenShownPref, false);
                int savedVersionCode = mPrefs.getInt(versionCheckedPref, 1);

                if (!settingScreenShown || savedVersionCode != versionCode) {
                    startActivity(new Intent(Splash.this, FirstRun.class));
                    editor.putBoolean(settingScreenShownPref, true);
                    editor.putInt(versionCheckedPref, versionCode);
                    editor.commit();
                }
                else
  */
                startActivity(new Intent(PermissionUtilities.this, MainActivity.class));
                finish();
            }
        };

        if(!MapsUtilities.hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        else
            h.postDelayed(r, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int index = 0;
        Map<String, Integer> PermissionsMap = new HashMap<>();
        for (String permission : permissions){
            PermissionsMap.put(permission, grantResults[index]);
            index++;
        }

        if((PermissionsMap.get(ACCESS_FINE_LOCATION) != 0)){
            Toast.makeText(this, "Location and SMS permissions are a must", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            h.postDelayed(r, 1500);
        }
    }

    public static void enableLoc(GoogleApiClient mGoogleApiClient, final Activity activity) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                if (status.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(activity, REQUEST_LOCATION);

//                            finish();
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                }
            }
        });
    }
}
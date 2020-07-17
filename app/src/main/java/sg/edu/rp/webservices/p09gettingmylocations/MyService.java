package sg.edu.rp.webservices.p09gettingmylocations;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyService extends Service {

    boolean started;
    private FusedLocationProviderClient client;
    private LocationCallback mLocationCallback;
    String folderLocation;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        Log.d("Service","Service Created");
        super.onCreate();
        client = LocationServices.getFusedLocationProviderClient(this);
        LocationCallback();
        folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
        File folder = new File(folderLocation);
        if(folder.exists() == false){
            boolean result = folder.mkdir();
            if(result == false){
                Toast.makeText(MyService.this,"Error, Service exiting", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started == false) {
            started = true;
            if (checkPermission() == true) {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequest.setInterval(10000);
                mLocationRequest.setFastestInterval(5000);
                mLocationRequest.setSmallestDisplacement(100);

                Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();

                client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
            } else {
                stopSelf();
            }
        } else {
            Log.d("Service","Service is still running");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "Service is stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void LocationCallback(){
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if(locationResult != null){
                    Location locData = locationResult.getLastLocation();
                    String data = locData.getLatitude() + "," + locData.getLongitude();
                    Log.d("Service - Loc Changed", data);
                    String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
                    File targetFile = new File(folderLocation, "newdata.txt");
                    try{
                        FileWriter writer = new FileWriter(targetFile,true);
                        writer.write(data+"\n");
                        writer.flush();
                        writer.close();
                    } catch (IOException e) {
                        Toast.makeText(MyService.this,"Failed to write!",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

}


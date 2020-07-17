package sg.edu.rp.webservices.p09gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {

    Button btnStartDetector,btnStopDetector,btnCheck;
    TextView tvLng, tvLat;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;
    FusedLocationProviderClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartDetector = findViewById(R.id.btnStartDetector);
        btnStopDetector = findViewById(R.id.btnStopDetector);
        btnCheck = findViewById(R.id.btnCheck);
        tvLat = findViewById(R.id.tvLat);
        tvLng = findViewById(R.id.tvLng);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setSmallestDisplacement(100);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult){
                if (locationResult != null){
                    Location data = locationResult.getLastLocation();
                    double lat = data.getLatitude();
                    double lng = data.getLongitude();
                    String msg = "Lat: " + lat + " Lng: " + lng;
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        };
        client = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermission() == true) {
            Task<Location> location = client.getLastLocation();
            location.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        tvLat.setText("Latitude: " + location.getLatitude());
                        tvLng.setText("Longitude: " + location.getLongitude());
                    }
                }
            });
        }

        final String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";

        File folder = new File(folderLocation);
        if (folder.exists() == false){
            boolean result = folder.mkdir();
            if (result == true){
                Log.d("File Read/Write", "Folder created");
            }
        }

        btnStartDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);
            }
        });

        btnStopDetector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File targetFile = new File(folderLocation, "newdata.txt");
                if (targetFile.exists() == true) {
                    String data = "";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null) {
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to read", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean checkPermission(){
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}
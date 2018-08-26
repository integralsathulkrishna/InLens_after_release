package integrals.inlens.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import id.zelory.compressor.Compressor;
import integrals.inlens.Helper.CurrentDatabase;
import integrals.inlens.Helper.LocationHelper;
import integrals.inlens.Helper.UploadDatabaseHelper;
import integrals.inlens.R;
import integrals.inlens.Weather.Common.Common;
import integrals.inlens.Weather.Helper.Helper;
import integrals.inlens.Weather.Model.OpenWeatherMap;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Athul Krishna on 23/12/2017.
 */

public  class AttachSituation
        extends
        AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        View.OnClickListener {
    private String ImageUri,string;
    private Bitmap bitmap;
    private LocationHelper locationHelper;
    private boolean LocationIndex=false;
    private PhotoViewAttacher photoViewAttacher;
    private TextView UploadButton;
    private EditText CaptionEditText;
    private String  DatabaseImageUri;
    private String  DatabaseWeatherDetails=null;
    private String  DatabaseLocationDetails=null;
    private String  DatabaseTimeTaken=null;
    private String  DatabaseUploadStatus=null;
    private String  DatabaseTextCaption=null;
    private String  DatabaseUploaderID=null;
    private String  DatabaseUploaderName=null;
    private String  DatabaseUploaderProfilePic=null;
    private String  DatabaseCurrentTimeMilliSecond=null;
    private LocationManager locationMangaer = null;
    private LocationListener locationListener = null;
    public  Button WeatherUploadButton=null;
    private Button btnGetLocation = null;
    private ProgressBar pb = null;
    private UploadDatabaseHelper uploadDatabaseHelper;
    private static final String TAG = "Debug";
    private Boolean flag = false;
    private Boolean Locationflag=false;
    private ImageView RecentImageView;
    private Location mLastLocation;
    private LocationManager locationManager;
    private Intent mIntent;
    public  AttachSituation()
    {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        //For Full screen purpose
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        //
        setContentView(R.layout.activity_attach_situation);
        getSupportActionBar().hide();
        CreateSharedPreferences();

        // Location
        locationMangaer = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        locationHelper=new LocationHelper(this);
        locationHelper.checkpermission();

        // Database
        uploadDatabaseHelper= new UploadDatabaseHelper(getApplicationContext(),"",null,1);

        //Referencing TextView Button and EditText
        string="KKKK";
        UploadButton=(TextView)findViewById(R.id.UploadButton);
        WeatherUploadButton=(Button)findViewById(R.id.AddWeather);
        pb = (ProgressBar) findViewById(R.id.LocationProgress);
        pb.setVisibility(View.INVISIBLE);
        btnGetLocation = (Button) findViewById(R.id.AddLocationButton);
        RecentImageView=(ImageView)findViewById(R.id.PhotoViewSituation);
        CaptionEditText=(EditText)findViewById(R.id.CaptionEditText);

        // OnClick
        WeatherUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AttachSituation.this,WeatherLayout.class));
            }
        });
        btnGetLocation.setOnClickListener(this);
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                AddImageForUpload();
            }
        });

        // Image
        final SharedPreferences sharedPreferencesX = getSharedPreferences("PhotoUpdate.pref", Context.MODE_PRIVATE);
        ImageUri=sharedPreferencesX.getString("CurrentImage::",string);
        final File file= new File(ImageUri);
        try {
            bitmap= new Compressor(getApplicationContext())
                    .setMaxHeight(500)
                    .setMaxWidth(500)
                    .setQuality(100)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .compressToBitmap(file);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Compression failed...",Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"No recent image found",Toast.LENGTH_LONG).show();
        }
        RecentImageView.setImageBitmap(bitmap);
        photoViewAttacher= new PhotoViewAttacher(RecentImageView);

        // Location
        if (locationHelper.CheckPlayServices()) {
            locationHelper.buildGoogleApiClient();
        }
    }


    public final void AddImageForUpload() {
        CreateDatabaseValues();
        uploadDatabaseHelper.InsertUploadValues(
                DatabaseImageUri ,
                DatabaseWeatherDetails,
                DatabaseLocationDetails,
                "NULLX",
                DatabaseTimeTaken,
                DatabaseUploadStatus,
                DatabaseTextCaption,
                DatabaseUploaderID,
                DatabaseUploaderName,
                DatabaseUploaderProfilePic,
                DatabaseCurrentTimeMilliSecond

        );
        CurrentDatabase currentDatabase= new CurrentDatabase(getApplicationContext(),"",null,1);
        int Value=currentDatabase.GetUploadingTotal();
        currentDatabase.ResetUploadTotal((Value+1));
        finish();
    }

    //Weather
    private void CreateSharedPreferences() {
        SharedPreferences sharedPreferences1 = getSharedPreferences("txtDescription.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor1=sharedPreferences1.edit();
        editor1.putString("txtDescription:","NULLX");
        editor1.commit();
        SharedPreferences sharedPreferences2 = getSharedPreferences("txtHumidity.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2=sharedPreferences2.edit();
        editor2.putString("txtHumidity:","NULLX");
        editor2.commit();
        SharedPreferences sharedPreferences3 = getSharedPreferences("txtTime.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor3=sharedPreferences3.edit();
        editor3.putString("txtTime:","NULLX");
        editor3.commit();
        SharedPreferences sharedPreferences4 = getSharedPreferences("txtCelsius.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor4=sharedPreferences4.edit();
        editor4.putString("txtCelsius:","NULLX");
        editor4.commit();
        SharedPreferences sharedPreferences5 = getSharedPreferences("txtPressure.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor5=sharedPreferences5.edit();
        editor5.putString("txtPressure:","NULLX");
        editor5.commit();
        SharedPreferences sharedPreferences6 = getSharedPreferences("txtCloud.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor6=sharedPreferences6.edit();
        editor6.putString("txtCloud:","NULLX");
        editor6.commit();
        SharedPreferences sharedPreferences7 = getSharedPreferences("txtCloud.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor7=sharedPreferences7.edit();
        editor7.putString("txtMaxTemperature:","NULLX");
        editor7.commit();
        SharedPreferences sharedPreferences8 = getSharedPreferences("txtMinTemperature.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor8=sharedPreferences8.edit();
        editor8.putString("txtMinTemperature:","NULLX");
        editor8.commit();
        SharedPreferences sharedPreferences9 = getSharedPreferences("txtWindSpeed.pref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor9=sharedPreferences9.edit();
        editor9.putString("txtWindSpeed:","NULLX");
        editor9.commit();

    }

//Location
    @Override
    public void onClick(View v) {

        btnGetLocation.setVisibility(View.INVISIBLE);
        flag = displayGpsStatus();
        if (flag) {

            Log.v(TAG, "onClick");
            pb.setVisibility(View.VISIBLE);
            locationListener = new MyLocationListener();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationMangaer.requestLocationUpdates(LocationManager
                    .GPS_PROVIDER, 1000, 1, locationListener);

        } else {
            alertbox("Gps Status!!", "Your GPS is OFF..");
            btnGetLocation.setVisibility(View.VISIBLE);
        }

    }

//Location
    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your Device's GPS is Disable..Enable your device location and click the button again...")
                .setCancelable(false)
                .setTitle(" GPS status ")
                .setPositiveButton("GPS ON",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent myIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(myIntent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("CANCEL",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class MyLocationListener implements LocationListener {
        String currentLocation;


        @Override
        public void onLocationChanged(Location loc) {
            Double longitude =  Double.valueOf(loc.getLongitude());
            Double latitude  =  Double.valueOf(loc.getLatitude());
            String cityName = null;




            Address locationAddress;
            locationAddress=locationHelper.getAddress(latitude,longitude);
            if(locationAddress!=null)
            {

                String address = locationAddress.getAddressLine(0);
                String address1 = locationAddress.getAddressLine(1);
                String city = locationAddress.getLocality();
                String state = locationAddress.getAdminArea();
                String country = locationAddress.getCountryName();
                String postalCode = locationAddress.getPostalCode();

                if(!TextUtils.isEmpty(address)) {
                    currentLocation = address;
                    if (!TextUtils.isEmpty(address1))
                        if (!TextUtils.isEmpty(city)) {
                            if (!TextUtils.isEmpty(postalCode)) ;
                        } else {
                            if (!TextUtils.isEmpty(postalCode)) ;
                        }

                    if (!TextUtils.isEmpty(state))
                        if (!TextUtils.isEmpty(country))
                            DatabaseLocationDetails = currentLocation;
                    }
                    }
            else {


            }






            pb.setVisibility(View.INVISIBLE);
            btnGetLocation.setVisibility(View.VISIBLE);
            btnGetLocation.setBackgroundResource(R.drawable.ic_tick);

            if(LocationIndex==false) {

                if(currentLocation!=null){
                   Toast toast= Toast.makeText(getApplicationContext(), " Your location updated as..." + currentLocation, Toast.LENGTH_SHORT);
                   toast.setGravity(Gravity.CENTER,0,0);
                   toast.show();
                   LocationIndex=true;

                }
                else {
                    Toast t=Toast.makeText(getApplicationContext(),"Location not available please check your Internet and GPS connectivity",Toast.LENGTH_LONG);
                            t.setGravity(Gravity.CENTER,0,0);
                            t.show();
                            LocationIndex=true;
                      }
                    }
                }
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onStatusChanged(String provider,
                                    int status, Bundle extras) {
            // TODO Auto-generated method stub
        }
    }
//Weather
    public static class WeatherLayout extends AppCompatActivity implements LocationListener {
        CheckedTextView txtDescription, txtHumidity, txtTime, txtCelsius,txtPressure;
        CheckedTextView txtCloud, txtMaxTemperature, txtMinTemperature, txtWindSpeed;
        ImageView imageView;
        private String WeatherDescription,WeatherTime,WeatherHumidity,WeatherCelcius,WeatherPressure;
        private String WeatherCloud,WeatherMaximumTemperature,WeatherMinimumTemperature,WeatherWind;
        private Button AttachWeatherButton;
        LocationManager locationManager;
        String provider;
        static double lst,lng;
        OpenWeatherMap openWeatherMap = new OpenWeatherMap();
        int MY_PERMISSION = 0;
        private ProgressDialog progressDialog;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_weather_layout);
            getSupportActionBar().setTitle("Check details and attach");
            getSupportActionBar().show();
            progressDialog=new ProgressDialog(WeatherLayout.this);
            txtDescription = ( CheckedTextView) findViewById(R.id.txtDescription);
            txtHumidity = ( CheckedTextView) findViewById(R.id.txtHumidity);
            txtTime = ( CheckedTextView) findViewById(R.id.txtTime);
            txtCelsius = ( CheckedTextView) findViewById(R.id.txtCelcius);
            txtPressure = ( CheckedTextView) findViewById(R.id.txtPressure);
            AttachWeatherButton=(Button)findViewById(R.id.WeatherCheckedButton);
            AttachWeatherButton.setEnabled(false);
            txtCloud = ( CheckedTextView) findViewById(R.id.txtCloud);
            txtMaxTemperature = ( CheckedTextView) findViewById(R.id.txtMaxTemperature);
            txtMinTemperature = ( CheckedTextView) findViewById(R.id.txtMinimumTemperature);
            txtWindSpeed = ( CheckedTextView) findViewById(R.id.txtWind);
            SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor= sharedPreferences.edit();
            editor.putBoolean("Task_Done",true);
            editor.commit();

            imageView = (ImageView) findViewById(R.id.weathericon);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            provider = locationManager.getBestProvider(new Criteria(), false);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WeatherLayout.this, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, MY_PERMISSION);
            }
            Location location = locationManager.getLastKnownLocation(provider);
            progressDialog.setTitle("Please wait ...");
            progressDialog.show();



        }

        @Override
        protected void onPause() {
            super.onPause();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WeatherLayout.this, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, MY_PERMISSION);
            }
            locationManager.removeUpdates(this);

        }

        @Override
        protected void onResume() {
            super.onResume();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(WeatherLayout.this, new String[]{
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                }, MY_PERMISSION);
            }
            locationManager.requestLocationUpdates(provider,5000 , 1, this);

        }

        @Override
        public void onLocationChanged(Location location) {
            lst=location.getLatitude();
            lng=location.getLongitude();
            new WeatherLayout.GetWeather().execute(Common.apiRequest(String.valueOf(lst),String.valueOf(lng)));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }



        private class GetWeather extends AsyncTask<String,Void,String> {

            @Override
            protected void onPreExecute() { super.onPreExecute(); }

            @Override
            protected String doInBackground(String... params) {
                String stream=null;
                String urlString=params[0];
                Helper http=new Helper();
                stream =http.getHTTPData(urlString);
                return stream;

            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    if (s.contains("Error: Not found city")) {
                        return;
                    }
                    else {

                        Gson gson = new Gson();
                        Type type = new TypeToken<OpenWeatherMap>() {
                        }.getType();
                        openWeatherMap = gson.fromJson(s, type);
                        txtDescription.setText(String.format("Weather Description  : %s", openWeatherMap.getWeatherList().get(0).getDescription()));
                        WeatherDescription=openWeatherMap.getWeatherList().get(0).getDescription();
                        txtHumidity.setText(String.format("Humidity : %d%%", openWeatherMap.getMain().getHumidity()));
                        WeatherHumidity= String.valueOf(openWeatherMap.getMain().getHumidity());
                        txtTime.setText(String.format("Sunrise/Sunset : %s/%s ", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()), Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
                        WeatherTime=String.format("Sunrise/Sunset : %s/%s ", Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()), Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset()));
                        txtCelsius.setText(String.format("Temperature : %.2f C", openWeatherMap.getMain().getTemp()));
                        WeatherCelcius=String.format("Temperature : %.2f C", openWeatherMap.getMain().getTemp());
                        txtPressure.setText(String.format("Pressure : %.2f mmHg", openWeatherMap.getMain().getPressure()));
                        WeatherPressure=String.format("Pressure : %.2f mmHg", openWeatherMap.getMain().getPressure());
                        txtCloud.setText(String.format("Cloud : %d%% ", openWeatherMap.getClouds().getAll()));
                        WeatherCloud=String.format("Cloud : %d%% ", openWeatherMap.getClouds().getAll());
                        txtMaxTemperature.setText(String.format("Maximum temperature : %.2f C", openWeatherMap.getMain().getTemp_max()));
                        WeatherMaximumTemperature=String.format("Maximum temperature : %.2f C", openWeatherMap.getMain().getTemp_max());
                        txtMinTemperature.setText(String.format("Minimum temperature : %.2f C ", openWeatherMap.getMain().getTemp_min()));
                        WeatherMinimumTemperature=String.format("Minimum temperature : %.2f C ", openWeatherMap.getMain().getTemp_min());
                        txtWindSpeed.setText(String.format("Wind speed : %.2f Km/H", openWeatherMap.getWind().getSpeed()));
                        WeatherWind=String.format("Wind speed : %.2f Km/H", openWeatherMap.getWind().getSpeed());
                    }
                }catch (WindowManager.BadTokenException E ){
                    Toast.makeText(getApplicationContext(),"Sorry some network error.Please try again.",Toast.LENGTH_LONG).show();
                    E.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Unable to retrieve data. Please check your Internet and GPS.",Toast.LENGTH_LONG).show();
                }
                txtDescription.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtDescription.isChecked()==false) {
                            txtDescription.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtDescription.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtDescription:",WeatherDescription+"  ");
                            editor.commit();


                        }
                        else {

                            txtDescription.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }

                            SharedPreferences sharedPreferences = getSharedPreferences("txtDescription.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtDescription:","NULL");
                            editor.commit();


                        }
                    }
                });
                txtHumidity.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtHumidity.isChecked()==false) {
                            txtHumidity.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtHumidity.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtHumidity:",WeatherHumidity+"  ");
                            editor.commit();
                        }
                        else {

                            txtHumidity.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("txtHumidity.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtHumidity:","NULL");
                            editor.commit();
                        }
                    }
                });

                txtTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtTime.isChecked()==false) {
                            txtTime.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtTime.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtTime:",WeatherTime+"  ");
                            editor.commit();
                        }
                        else {

                            txtTime.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("txtTime.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtTime:","");
                            editor.commit();
                        }
                    }
                });

                txtCelsius.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtCelsius.isChecked()==false) {
                            txtCelsius.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtCelsius.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtCelsius:",WeatherCelcius+"  ");
                            editor.commit();
                        }
                        else {

                            txtCelsius.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("txtCelsius.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtCelsius:","");
                            editor.commit();
                        }
                    }
                });

                txtPressure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtPressure.isChecked()==false) {
                            txtPressure.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtPressure.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtPressure:",WeatherPressure+"  ");
                            editor.commit();
                        }
                        else {
                            txtPressure.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }

                            SharedPreferences sharedPreferences = getSharedPreferences("txtPressure.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtPressure:","");
                            editor.commit();
                        }
                    }
                });

                txtCloud.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtCloud.isChecked()==false) {
                            txtCloud.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtCloud.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtCloud:",WeatherCloud+"  ");
                            editor.commit();
                        }
                        else {
                            txtCloud.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }

                            SharedPreferences sharedPreferences = getSharedPreferences("txtCloud.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtCloud:","");
                            editor.commit();
                        }
                    }
                });

                txtMaxTemperature.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtMaxTemperature.isChecked()==false) {
                            txtMaxTemperature.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtMaxTemperature.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtMaxTemperature:",WeatherMaximumTemperature+"  ");
                            editor.commit();
                        }
                        else {

                            txtMaxTemperature.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("txtMaxTemperature.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtMaxTemperature:","");
                            editor.commit();
                        }
                    }
                });

                txtMinTemperature.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtMinTemperature.isChecked()==false) {
                            txtMinTemperature.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtMinTemperature.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtMinTemperature:",WeatherMinimumTemperature+"  ");
                            editor.commit();
                        }
                        else {

                            txtMinTemperature.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("txtMinTemperature.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtMinTemperature:","");
                            editor.commit();
                        }
                    }
                });
                txtWindSpeed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if( txtWindSpeed.isChecked()==false) {
                            txtWindSpeed.setChecked(true);
                            AttachWeatherButton.setEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("txtWindSpeed.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtWindSpeed:",WeatherWind+"  ");
                            editor.commit();
                        }
                        else {
                            txtWindSpeed.setChecked(false);
                            if(     txtWindSpeed.isChecked()==false&&
                                    txtMaxTemperature.isChecked()==false&&
                                    txtMinTemperature.isChecked()==false&&
                                    txtCloud.isChecked()==false&&
                                    txtHumidity.isChecked()==false&&
                                    txtDescription.isChecked()==false&&
                                    txtCelsius.isChecked()==false&&
                                    txtTime.isChecked()==false){
                                AttachWeatherButton.setEnabled(false);
                                SharedPreferences sharedPreferences = getSharedPreferences("WeatherLayoutTaskIndex.pref",Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor= sharedPreferences.edit();
                                editor.putBoolean("Task_Done",false);
                                editor.commit();
                            }
                            SharedPreferences sharedPreferences = getSharedPreferences("txtWindSpeed.pref",Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putString("txtWindSpeed:","");
                            editor.commit();
                        }
                    }
                });



                try {

                    RequestOptions requestOptions=new RequestOptions()
                            .centerCrop()
                            .override(176,176);
                    ;
                    Glide.with(getApplicationContext())
                            .load(Common.getImage(openWeatherMap.getWeatherList().get(0).getIcon()))
                            .thumbnail(0.1f)
                            .apply(requestOptions)
                            .into(imageView);


                   }catch (NullPointerException e){
                    Toast.makeText(getApplicationContext(),"Unable to retrieve data. Please try again.",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                AttachWeatherButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AttachWeatherButton.setTextColor(Color.RED);
                        AttachWeatherButton.setText(" Attached Successfully..");
                        finish();
                    }
                });

            }


        }


    }

//DatabaseToUpload
    public void CreateDatabaseValues()
    {
        String Default="SS";
        DatabaseImageUri=ImageUri;
        DatabaseCurrentTimeMilliSecond= String.valueOf(System.currentTimeMillis());
        GetWeatherDetails();
        if(DatabaseLocationDetails==null){
            DatabaseLocationDetails="NULLX";
        }
        if(DatabaseWeatherDetails==null){
            DatabaseWeatherDetails="NULLX";
        }
        SharedPreferences sharedPreferencesUser=getSharedPreferences("Current_User.pref",Context.MODE_PRIVATE);
        DatabaseUploaderID=sharedPreferencesUser.getString("UID",Default);
        DatabaseUploaderName=sharedPreferencesUser.getString("NAME",Default);
        DatabaseUploaderProfilePic=sharedPreferencesUser.getString("PROFILEPIC",Default);
        DatabaseUploadStatus="NOT_UPLOADED";
        Calendar calender=Calendar.getInstance();
        DatabaseTimeTaken=""+calender.get(Calendar.HOUR_OF_DAY)+ ":"
                +calender.get(Calendar.MINUTE)+""
                +"        "+calender.get(Calendar.DAY_OF_MONTH)+"/"
                +calender.get(Calendar.MONTH)+1+"/"
                +calender.get(Calendar.YEAR)+"";
        String TextCaption=null;
        TextCaption=CaptionEditText.getText().toString().trim();
        if(TextCaption==null){
            TextCaption="NULL";
        }else{
            DatabaseTextCaption=TextCaption;
        }
      }

    //Location
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        locationHelper.onActivityResult(requestCode,resultCode,data);
    }
    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.CheckPlayServices();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("Connection failed:", " ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        mLastLocation=locationHelper.getLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        locationHelper.connectApiClient();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }
    //Weather
    private void GetWeatherDetails()
    {
        String Text;
        String DefaultText="NULLX";
        DatabaseWeatherDetails=" ";
        SharedPreferences sharedPreferences1 = getSharedPreferences("txtDescription.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences1.getString("txtDescription:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";

        }
        SharedPreferences sharedPreferences2 = getSharedPreferences("txtHumidity.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences2.getString("txtHumidity:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
            }

        SharedPreferences sharedPreferences3 = getSharedPreferences("txtTime.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences3.getString("txtTime:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
            }


        SharedPreferences sharedPreferences4 = getSharedPreferences("txtCelsius.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences4.getString("txtCelsius:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
            }

        SharedPreferences sharedPreferences5 = getSharedPreferences("txtPressure.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences5.getString("txtPressure:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
            }


        SharedPreferences sharedPreferences6 = getSharedPreferences("txtCloud.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences6.getString("txtCloud:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
            }




        SharedPreferences sharedPreferences7 = getSharedPreferences("txtMinTemperature.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences7.getString("txtMinTemperature:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
        }


        SharedPreferences sharedPreferences8 = getSharedPreferences("txtWindSpeed.pref",Context.MODE_PRIVATE);
        Text=sharedPreferences8.getString("txtWindSpeed:",DefaultText);
        if(Text.contentEquals("NULLX")){}
        else {
            DatabaseWeatherDetails += Text;
            DatabaseLocationDetails+="   ";
            }

    }

   }





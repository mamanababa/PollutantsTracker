package ie.gmit.computing.pollutantstracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Date;

/**
 * main activity of this application
 * 3 buttons on the bottom, to add and delete the tree , and change generic information
 * 1 camera button to take a photo then pass photo data and information to next activity
 * 1 SEARCH button to view and search the tree without taking a photo or
 * to identify the tree for saving photo and information
 *
 * @author Zeyuan Li
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageButton cameraa;
    private ImageView photo;
    private Button search;
    private ImageButton add;
    private ImageButton delete;
    private ImageButton settings;
    private TreeModel tree = new TreeModel();
    private final int IMG_REQUEST_CODE = 1;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraa = (ImageButton) this.findViewById(R.id.cameraa);
        photo = (ImageView) this.findViewById(R.id.photo);
        search = (Button) this.findViewById(R.id.search);
        add = (ImageButton) this.findViewById(R.id.add);
        delete = (ImageButton) this.findViewById(R.id.delete);
        settings = (ImageButton) this.findViewById(R.id.settings);

        cameraa.setOnClickListener(this);
        search.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        settings.setOnClickListener(this);

//        Log.d("GPS:", getGPS());
//        Toast.makeText(getApplicationContext(), "GPS:" + getGPS(), Toast.LENGTH_SHORT).show();
        LocationManager alm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            //Log.d("GPS:", "GPS ready");
            Toast.makeText(getApplicationContext(), "GPS ready", Toast.LENGTH_SHORT).show();
        } else {
            //Log.d("GPS:", "cannot use GPS, please open it");
            Toast.makeText(getApplicationContext(), "cannot use GPS", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param bitmap set bitmap of photo for pass to next activity
     */
    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * @return return bitmap of photo for pass to next activity
     */
    private Bitmap getBitmap() {
        return this.bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //click the camera button
            case R.id.cameraa:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, IMG_REQUEST_CODE);
                break;

            //click the search button
            case R.id.search:
                if (photo.getHeight() == 0) {
                    Toast.makeText(getApplicationContext(), "Viewing Mode", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, Activity_start.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "took a photo, Saving Mode", Toast.LENGTH_SHORT).show();
                    // pass DATE and GPS of photo to next activity
                    Date d = new Date(System.currentTimeMillis());
                    String date = String.valueOf(d);
                    String gps = getGPS();
                    Intent intent = new Intent(MainActivity.this, Activity_start.class);
                    intent.putExtra("date", date);
                    intent.putExtra("gps", gps);
                    intent.putExtra("img", getBitmap());
                    startActivity(intent);
                }
                break;

            //click the add button
            case R.id.add:
                // Log.d("load tree in add page, ", "load tree, and the number：" + String.valueOf(tree.loadTree().size()));
                if (tree.loadTree() == null) {
                    Node rootNode = new CompositeNode("start", null);
                    tree.saveTree(rootNode, true);
                    //Log.d("first time run app, you clicked add button", "create root node：" + String.valueOf(tree.loadTree().size()));
                }
                Intent addIntent = new Intent(MainActivity.this, add.class);
                startActivity(addIntent);
                break;

            //click the delete button
            case R.id.delete:
                if (tree.loadTree() == null) {
                    Node rootNode = new CompositeNode("start", null);
                    tree.saveTree(rootNode, true);
                    //Log.d("first time run app, you clicked delete button", "create root node：" + String.valueOf(tree.loadTree().size()));
                }
                Intent deleteIntent = new Intent(MainActivity.this, delete.class);
                startActivity(deleteIntent);
                break;

            //click the settings button to change generic information(ship name, scientist, email)
            case R.id.settings:
                Intent settingIn = new Intent(MainActivity.this, Settings.class);
                startActivity(settingIn);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQUEST_CODE && resultCode == RESULT_OK) {
            //get the data of photo
            Bundle bundle = data.getExtras();
            //convert photo data to bitmap
            Bitmap b = (Bitmap) bundle.get("data");
            //show photo
            Matrix matrix = new Matrix();
            matrix.postScale(2.5f, 2.5f);
            photo.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true));

            setBitmap(b);
        }
    }

    /**
     * @return return gps information
     */
    private String getGPS() {
        LocationManager locationManager;
        String serviceName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(serviceName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(provider);
        String s = updateWithNewLocation(location);
        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
        return s;
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * @param location
     * @return return Latitude and  Longitude information as a string
     */
    private String updateWithNewLocation(Location location) {
        if (location != null) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            return "Latitude:" + lat + " Longitude:" + lng;
        } else
            return "cannot get GPS";
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
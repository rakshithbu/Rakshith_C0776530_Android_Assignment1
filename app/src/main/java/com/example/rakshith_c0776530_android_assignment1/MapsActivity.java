package com.example.rakshith_c0776530_android_assignment1;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int count =0;
    List<Marker> distanceMarkers = new ArrayList<>();
    List<LatLng> placeMarkers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
        mMap = googleMap;
        final  PolygonOptions polygonOptions = new PolygonOptions();
        final PolylineOptions polylineOptions = new PolylineOptions();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresses = null;
                System.out.println("inside map click");
                count = count+1;
               if(count <=4){
                   System.out.println("inside map click");
                   MarkerOptions markerOptions = new MarkerOptions();
                   markerOptions.position(latLng);
                   placeMarkers.add(latLng);
                   try {
                       addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                   } catch (IOException e) {
                     e.printStackTrace();
                       e.printStackTrace();
                   }


                   markerOptions.title
                           ( addresses.get(0).getThoroughfare()+" "+ addresses.get(0).getSubThoroughfare()+" "+
                                   addresses.get(0).getPostalCode()).
                           snippet(addresses.get(0).getCountryName() +"  "+addresses.get(0).getLocality()).
                           icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                   mMap.addMarker(markerOptions);
                   polylineOptions.add(new LatLng(latLng.latitude , latLng.longitude)).color(Color.RED);
                   mMap.addPolyline(polylineOptions).setClickable(true);

                   mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                   polygonOptions.add(new LatLng(latLng.latitude , latLng.longitude));

               }

                if(count ==4){
                    polygonOptions.fillColor(Color.BLUE);
                   mMap.addPolygon(polygonOptions.strokeColor(Color.RED)).setClickable(true);
                }
            }
        });


        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener(){


            @Override
            public void onPolygonClick(Polygon polygon) {
                float[] results0 = new float[1];
                float[] results1 = new float[1];
                float[] results2 = new float[1];
                float[] results3 = new float[1];

                double totalDistance = 0.0;
                Location.distanceBetween(polygon.getPoints().get(0).latitude, polygon.getPoints().get(0).longitude,
                        polygon.getPoints().get(1).latitude, polygon.getPoints().get(1).latitude,
                        results0);
                Location.distanceBetween(polygon.getPoints().get(1).latitude, polygon.getPoints().get(1).longitude,
                        polygon.getPoints().get(2).latitude, polygon.getPoints().get(2).latitude,
                        results1);
                Location.distanceBetween(polygon.getPoints().get(2).latitude, polygon.getPoints().get(2).longitude,
                        polygon.getPoints().get(3).latitude, polygon.getPoints().get(3).latitude,
                        results2);
                Location.distanceBetween(polygon.getPoints().get(3).latitude, polygon.getPoints().get(3).longitude,
                        polygon.getPoints().get(0).latitude, polygon.getPoints().get(0).latitude,
                        results3);

                System.out.println("results0"+results0[0]);
                System.out.println("results1"+results1[0]);
                System.out.println("results2"+results2[0]);
                System.out.println("results3"+results3[0]);

               totalDistance = (results0[0]+results1[0]+results2[0]+results3[0])/1000;

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("A-B-C-D")
                        .setMessage(String.valueOf(totalDistance)+"KMS")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                System.out.println("inside polyline click");

              LatLng midpoint =  findMidPoint(polyline.getPoints().get(0).latitude,polyline.getPoints().get(0).longitude,polyline.getPoints().get(1).latitude,polyline.getPoints().get(1).longitude);

                float[] results = new float[1];
                Location.distanceBetween(polyline.getPoints().get(0).latitude, polyline.getPoints().get(0).longitude,
                        polyline.getPoints().get(1).latitude, polyline.getPoints().get(1).latitude,
                        results);

                BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.mipmap.transparent);
                MarkerOptions options = new MarkerOptions()
                        .position(midpoint)
                        .title(String.format(Locale.CANADA,"%.2f Km", results[0]/1000))

                        .icon(transparent)
                        .anchor((float) 0.5, (float) 0.5); //puts the info window on the polyline

                Marker m = mMap.addMarker(options);


                for (Marker mLocationMarker: distanceMarkers) {
                    mLocationMarker.remove();
                }

                m.showInfoWindow();
                distanceMarkers.add(m);

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Distance between two polylines")
                        .setMessage(String.valueOf(results[0]/1000)+"KMS")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(final Marker arg0) {

                Iterator<LatLng> iter = placeMarkers.iterator();
                while (iter.hasNext()) {
                    LatLng p = iter.next();
                    if(p.longitude== arg0.getPosition().longitude && p.latitude== arg0.getPosition().latitude){
                        iter.remove();
                    }
                }



            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                mMap.clear();
                // TODO Auto-generated method stub

                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
                placeMarkers.add(arg0.getPosition());
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
               // Log.i("System out", "onMarkerDrag...");
            }
        });

    }

    public void placeMarksDrawPolyline(){

    }

    public LatLng findMidPoint(double lat1,double lon1,double lat2,double lon2){
        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);
        return new LatLng(lat3,lon3);
    }
}
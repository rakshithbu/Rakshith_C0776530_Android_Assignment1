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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int count =0;

     ArrayList<Marker> distanceMarkers = new ArrayList<>();
     ArrayList<Marker> dragMarker = new ArrayList<>();
     ArrayList<Polyline> polylines = new ArrayList<>();
     ArrayList<Polygon> polygons = new ArrayList<>();

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

       final PolylineOptions polylineOptions = new PolylineOptions();
       final PolygonOptions polygonOptions = new PolygonOptions();


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                List<Address> addresses = null;
                System.out.println("inside map click");
                count = count+1;
               if(count <=4){

                   MarkerOptions markerOptions = new MarkerOptions();
                   markerOptions.position(latLng);
                   try {
                       addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                       if(!addresses.isEmpty())
                       markerOptions.title
                               ( addresses.get(0).getThoroughfare()+" "+ addresses.get(0).getSubThoroughfare()+" "+
                                       addresses.get(0).getPostalCode()).
                               snippet(addresses.get(0).getCountryName() +"  "+addresses.get(0).getLocality()).
                               icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                   } catch (IOException e) {
                     e.printStackTrace();
                       e.printStackTrace();
                   }

                  Marker ms =  mMap.addMarker(markerOptions);
                  ms.setDraggable(true);
                  dragMarker.add(ms);
                   polylineOptions.add(new LatLng(latLng.latitude , latLng.longitude)).color(Color.RED);
                   Polyline py = mMap.addPolyline(polylineOptions);
                   py.setClickable(true);
                   polylines.add(mMap.addPolyline(polylineOptions));

                   mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                   polygonOptions.add(new LatLng(latLng.latitude , latLng.longitude));

               }

                if(count ==4){
                    polygonOptions.fillColor(Color.BLUE);
                    polygonOptions.strokeColor(Color.RED);

                    Polygon po =  mMap.addPolygon(polygonOptions);
                    po.setClickable(true);
                    polygons.add(po);

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

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                System.out.println("inside polyline click");

              LatLng midpoint =  findMidPoint(polyline.getPoints().get(0).latitude,
                      polyline.getPoints().get(0).longitude,polyline.getPoints().get(1).latitude,
                      polyline.getPoints().get(1).longitude);

                float[] results = new float[1];
                Location.distanceBetween(polyline.getPoints().get(0).latitude, polyline.getPoints().get(0).longitude,
                        polyline.getPoints().get(1).latitude, polyline.getPoints().get(1).latitude,
                        results);

                BitmapDescriptor transparent = BitmapDescriptorFactory.fromResource(R.mipmap.transparent);
                MarkerOptions options = new MarkerOptions()
                        .position(midpoint)
                        .title(String.format(Locale.CANADA,"%.2f Km", results[0]/1000))

                        .icon(transparent)
                        .anchor((float) 0.5, (float) 0.5);

                Marker m = mMap.addMarker(options);


                for (Marker mLocationMarker: distanceMarkers) {
                    mLocationMarker.remove();
                }

                m.showInfoWindow();
                distanceMarkers.add(m);

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Distance between two polylines")
                        .setMessage(String.valueOf(results[0]/1000)+"KMS")

                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });


        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(final Marker arg0) {

            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {

                Iterator<Polyline> iter = polylines.iterator();
                while (iter.hasNext()) {
                    Polyline p = iter.next();
                    p.remove();
                    iter.remove();
                }

                Iterator<Polygon> iter1 = polygons.iterator();
                while (iter1.hasNext()) {
                    Polygon p = iter1.next();
                    p.remove();
                    iter1.remove();
                }



                 List<Address> addresses = null;
                 PolylineOptions polylineOptions1 = new PolylineOptions();
                 PolygonOptions polygonOptions1 = new PolygonOptions();

                for (int i = 0; i < dragMarker.size(); i++){
                    try {
                        addresses = geocoder.getFromLocation(dragMarker.get(i).getPosition().latitude,
                                dragMarker.get(i).getPosition().longitude, 1);

                        if(!addresses.isEmpty())
                        arg0.setTitle
                                ( addresses.get(0).getThoroughfare()+" "+ addresses.get(0).getSubThoroughfare()+" "+
                                        addresses.get(0).getPostalCode());

                        arg0.setSnippet(addresses.get(0).getCountryName() +"  "+addresses.get(0).getLocality());
                    } catch (IOException e) {
                        e.printStackTrace();
                        e.printStackTrace();
                    }

                    polylineOptions1.add(new LatLng(dragMarker.get(i).getPosition().latitude ,
                            dragMarker.get(i).getPosition().longitude)).color(Color.RED);
                    polygonOptions1.add(dragMarker.get(i).getPosition());

                }

                Polyline p =  mMap.addPolyline(polylineOptions1);
                p.setClickable(true);
                polylines.add(p);

                polygonOptions1.fillColor(Color.BLUE);
                polygonOptions1.strokeColor(Color.RED);
                Polygon po = mMap.addPolygon(polygonOptions1);
                po.setClickable(true);
                polygons.add(po);

            }

            @Override
            public void onMarkerDrag(Marker arg0) {

            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(final LatLng latLng) {
                System.out.println("inside map long press");

                /*Collections.sort(polylines, new Comparator<Polyline>() {

                    @Override
                    public int compare(Polyline polyline, Polyline t1) {
                        Location currentLocation = new Location("currentLocation");
                        currentLocation.setLatitude(latLng.latitude);
                        currentLocation.setLongitude(latLng.longitude);

                        ArrayList<Float> pointsA = new ArrayList<>();

                        Location polylineA1 = new Location("point A");
                        polylineA1.setLatitude(polyline.getPoints().get(0).latitude);
                        polylineA1.setLongitude(polyline.getPoints().get(0).longitude);

                        pointsA.add(currentLocation.distanceTo(polylineA1));

                        ArrayList<Float> pointsA2 = new ArrayList<>();

                        Location polylineA2 = new Location("point A2");
                        polylineA2.setLatitude(polyline.getPoints().get(1).latitude);
                        polylineA2.setLongitude(polyline.getPoints().get(1).longitude);

                        pointsA2.add(currentLocation.distanceTo(polylineA2));


                        return Float.compare(distanceOne, distanceTwo);

                    }


                    public int compare(Marker marker, Marker t1) {
                        Location currentLocation = new Location("currentLocation");
                        currentLocation.setLatitude(latLng.latitude);
                        currentLocation.setLongitude(latLng.longitude);
                        Location locationA = new Location("point A");
                        locationA.setLatitude(marker.getPosition().latitude);
                        locationA.setLongitude(marker.getPosition().longitude);
                        Location locationB = new Location("point B");
                        locationB.setLatitude(t1.getPosition().latitude);
                        locationB.setLongitude(t1.getPosition().longitude);
                        float distanceOne = currentLocation.distanceTo(locationA);
                        float distanceTwo = currentLocation.distanceTo(locationB);
                        return Float.compare(distanceOne, distanceTwo);
                    }
                });*/

if(dragMarker.size()>0){
    Collections.sort(dragMarker, new Comparator<Marker>() {

        @Override
        public int compare(Marker marker, Marker t1) {
            Location currentLocation = new Location("currentLocation");
            currentLocation.setLatitude(latLng.latitude);
            currentLocation.setLongitude(latLng.longitude);
            Location locationA = new Location("point A");
            locationA.setLatitude(marker.getPosition().latitude);
            locationA.setLongitude(marker.getPosition().longitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(t1.getPosition().latitude);
            locationB.setLongitude(t1.getPosition().longitude);
            float distanceOne = currentLocation.distanceTo(locationA);
            float distanceTwo = currentLocation.distanceTo(locationB);
            return Float.compare(distanceOne, distanceTwo);
        }
    });
    dragMarker.get(0).remove();
    count = count-1;

    Iterator<Polyline> iter = polylines.iterator();
    while (iter.hasNext()) {
        Polyline p = iter.next();
        p.remove();
        iter.remove();
    }

    Iterator<Polygon> iter1 = polygons.iterator();
    while (iter1.hasNext()) {
        Polygon p = iter1.next();
        p.remove();
        iter1.remove();



        List<Address> addresses = null;
        PolylineOptions polylineOptions1 = new PolylineOptions();
        PolygonOptions polygonOptions1 = new PolygonOptions();

        for (int i = 0; i < dragMarker.size(); i++){

            polylineOptions1.add(new LatLng(dragMarker.get(i).getPosition().latitude ,
                    dragMarker.get(i).getPosition().longitude)).color(Color.RED);
            polygonOptions1.add(dragMarker.get(i).getPosition());

        }

        Polyline pp =  mMap.addPolyline(polylineOptions1);
        p.setClickable(true);
        polylines.add(pp);

        if(count==4){
            polygonOptions1.fillColor(Color.BLUE);
            polygonOptions1.strokeColor(Color.RED);
            Polygon pos = mMap.addPolygon(polygonOptions1);
            pos.setClickable(true);
            polygons.add(pos);
        }

    }

}



            }
        });
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
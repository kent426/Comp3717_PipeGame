package pipegame.comp3717.bcit.ca.pipegame;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlGeometry;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    private KmlLayer kmlIntersectionsLayer;
    private KmlLayer kmlNeighbourhood_areas;
   static private List<LatLng> points;
    static  private List<LatLngBounds> areas;
    //points in different level
    static private List<ArrayList<LatLng>> pointSets;
    //the levels:from 0 to 14, used for filtering the points
    private int levelarea;
    private LatLngBounds newW = new LatLngBounds(
            new LatLng(49.209763, -122.938656), new LatLng(49.220423, -122.911588));
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        levelarea = this.getIntent().getIntExtra("level", 0) - 1;


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        retrieveFileFromResource();




        /*getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        levelarea = this.getIntent().getIntExtra("level", -1) - 1;
        displayPointsAndArea(levelarea, areas, pointSets);


        map.addMarker(new MarkerOptions().position(new LatLng(49.215826, -122.929966)).title("Marker"));
        map.addMarker(new MarkerOptions().position(new LatLng(49.215826, -122.929966)).title("Marker"));


    }

    private void retrieveFileFromResource() {
        try {
            kmlIntersectionsLayer = new KmlLayer(map, R.raw.intersections, getApplicationContext());
            kmlNeighbourhood_areas = new KmlLayer(map, R.raw.areas, getApplicationContext());
            points = new ArrayList<LatLng>();
            areas = new ArrayList<LatLngBounds>();


            points = getPoints(kmlIntersectionsLayer.getContainers());
            areas = getPolygons(kmlNeighbourhood_areas.getContainers());




            pointSets = sortPointsToAreas(points, areas);

            Log.d("gamelevl", "now" + levelarea);
            Log.d("pointsse", pointSets.toString());










            /*kmlIntersectionsLayer.addLayerToMap();*/

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> getPoints(final Iterable<KmlContainer> containers) {
        List<LatLng> ps = new ArrayList<LatLng>();


        for (KmlContainer container : containers) {
            // Do something to container
            for (KmlPlacemark placemark : container.getPlacemarks()) {
                // Do something to Placemark
                KmlGeometry g = placemark.getGeometry();
                if (g.getGeometryType().equals("Point")) {
                    LatLng point = (LatLng) g.getGeometryObject();

                    ps.add(point);
                }

            }
            if (container.hasContainers()) {
                getPoints(container.getContainers());
            }
        }

        return ps;
    }

    private List<LatLngBounds> getPolygons(final Iterable<KmlContainer> containers) {
        List<LatLngBounds> polgs = new ArrayList<>();

        for (KmlContainer container : containers) {
            // Do something to container
            for (KmlPlacemark placemark : container.getPlacemarks()) {
                // Do something to Placemark
                KmlGeometry g = placemark.getGeometry();
                if (g.getGeometryType().equals("Polygon")) {
                    KmlPolygon polygon = (KmlPolygon) g;
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng latLng : polygon.getOuterBoundaryCoordinates()) {
                        builder.include(latLng);
                    }

                    polgs.add(builder.build());
                }

            }
            if (container.hasContainers()) {
                getPolygons(container.getContainers());
            }
        }

        return polgs;
    }

    private ArrayList<ArrayList<LatLng>> sortPointsToAreas(List<LatLng> points, List<LatLngBounds> areas) {
        ArrayList<ArrayList<LatLng>> pointSets = new ArrayList<ArrayList<LatLng>>();


        for (LatLngBounds a : areas) {
            ArrayList<LatLng> pointSet1 = new ArrayList<>();
            for (LatLng p : points) {
                if (a.contains(p)) {
                    pointSet1.add(p);
                }
            }
            pointSets.add(areas.indexOf(a), pointSet1);
        }
        Log.d("size", "the size is " + pointSets.size());

        return pointSets;
    }

    private void displayPointsAndArea(int level, List<LatLngBounds> areas, List<ArrayList<LatLng>> pointSets) {
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(areas.get(level), width, height, 1));



        for (LatLng p : pointSets.get(level)) {
            map.addMarker(new MarkerOptions()
                    .position(p)
                    .icon(BitmapDescriptorFactory.fromResource(R.raw.inters)));

        }


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Game Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }


    @Override
    public void onStart()  {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());


    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

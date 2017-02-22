package pipegame.comp3717.bcit.ca.pipegame;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlGeometry;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;
import com.google.maps.android.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kent on 2017-02-21.
 */

public class Map extends AsyncTask<GoogleMap, Integer, GoogleMap>   {

    private GameActivity activity;

    private GoogleMap map;
    Intersections intersections;
    ArrayList<PolylineOptions> allConnectedPoints;

    private KmlLayer kmlIntersectionsLayer;
    private KmlLayer kmlNeighbourhood_areas;
    private ArrayList<LatLng> points;
    static  private List<LatLngBounds> areas;
    //points in different level
    static private List<ArrayList<LatLng>> pointSets;
    //the levels:from 0 to 14, used for filtering the points
    private int levelarea;

    public Map(GameActivity a) {
        activity = a;
        intersections = new Intersections(a);


    }

    protected void onPreExecute() {
        super.onPreExecute();
        retrieveFileFromResource();
        map = activity.getmap();
        displayPointsAndArea(activity.getLevelarea(),areas,pointSets);


    }

    @Override
    protected GoogleMap doInBackground(GoogleMap... g) {
        intersections.readPoints();
        allConnectedPoints = intersections.getPolylineOptions();
        points = intersections.getPoints();



        return g[0];

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(GoogleMap g) {

        super.onPostExecute(g);
        for(PolylineOptions p: allConnectedPoints) {
            g.addPolyline(p);
        }

        for (LatLng p : points) {
            map.addMarker(new MarkerOptions()
                    .position(p)
                    .anchor(0.5f,0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.raw.inters)));
            Log.d("show", "onPostExecute: ");
        }


    }

    private void retrieveFileFromResource() {
        try {
            /*kmlIntersectionsLayer = new KmlLayer(map, R.raw.intersections,this.activity.getApplicationContext());*/
            kmlNeighbourhood_areas = new KmlLayer(map, R.raw.areas, this.activity.getApplicationContext());
            /*points = new ArrayList<LatLng>();*/
            areas = new ArrayList<LatLngBounds>();


            /*points = getPoints(kmlIntersectionsLayer.getContainers());*/
            areas = getPolygons(kmlNeighbourhood_areas.getContainers());




            /*pointSets = sortPointsToAreas(points, areas);*/

            Log.d("gamelevl", "now" + levelarea);
            /*Log.d("pointsse", pointSets.toString());*/
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
        int width = this.activity.getResources().getDisplayMetrics().widthPixels;
        int height = this.activity.getResources().getDisplayMetrics().heightPixels;
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(areas.get(level), width, height, 1));



/*        for (LatLng p : pointSets.get(level)) {
            map.addMarker(new MarkerOptions()
                    .position(p)
                    .icon(BitmapDescriptorFactory.fromResource(R.raw.inters)));

        }*/


    }
}

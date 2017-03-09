package pipegame.comp3717.bcit.ca.pipegame;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlGeometry;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Kent on 2017-02-21.
 */

public class Map extends AsyncTask<GoogleMap, Integer, GoogleMap> implements Serializable {

    private GameActivity activity;

    private GoogleMap map;
    Intersections intersections;
    Bounds bounds;
    ArrayList<PolylineOptions> allConnectedPoints;
    private HashMap<LatLng,LatLng[]> startNEnd;

    private KmlLayer kmlIntersectionsLayer;
    private KmlLayer kmlNeighbourhood_areas;
    private ArrayList<LatLng> points;
    private List<LatLngBounds> areas;
    private Marker moveOj;
    //points in different level
    static private List<ArrayList<LatLng>> pointSets;
    //the levels:from 0 to 14, used for filtering the points
    private int levelarea;

    public Map(GameActivity a) {
        activity = a;
        intersections = new Intersections(a);
        bounds = new Bounds(a);
        startNEnd = intersections.getFindStartAndEnd();
        /*retrieveFileFromResource();*/



    }

    protected void onPreExecute() {
        super.onPreExecute();
        map = activity.getmap();






    }

    @Override
    protected GoogleMap doInBackground(GoogleMap... g) {
        bounds.readpointsForBound();
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
        LatLngBounds bn = bounds.getBound();
        displayPointsAndArea(bn);




        super.onPostExecute(g);
        for(PolylineOptions p: allConnectedPoints) {
            g.addPolyline(p);
        }

        ArrayList<LatLng> curLevelPoint = new ArrayList<>();
        for (LatLng p : points) {
            if(bn.contains(p)) {
                curLevelPoint.add(p);
                map.addMarker(new MarkerOptions()
                        .position(p)
                        .anchor(0.5f,0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.inters)));

            }

        }

        LatLng randomLaLg = curLevelPoint.get((new Random().nextInt(curLevelPoint.size())));

        LatLng moveOjlalg = startNEnd.get(randomLaLg)[0];
        moveOj = map.addMarker(new MarkerOptions().position(moveOjlalg)
                .title("hello"));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                try {
                    if(startNEnd.containsKey(marker.getPosition())) {
                        LatLng[] se =  startNEnd.get(marker.getPosition());
                        if(se[0].equals(moveOj.getPosition())) {
                            MarkerAnimation.animateMarkerToHC(moveOj,se[1],new LatLngInterpolator.Linear());
                        }
                        if(se[1].equals(moveOj.getPosition())) {
                            MarkerAnimation.animateMarkerToHC(moveOj,se[0],new LatLngInterpolator.Linear());
                        }
                    }


                } catch (NullPointerException e) {

                }



                return false;
            }
        });




    }

    private void displayPointsAndArea( LatLngBounds areas) {
        int width = this.activity.getResources().getDisplayMetrics().widthPixels;
        int height = this.activity.getResources().getDisplayMetrics().heightPixels;
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(areas, width, height, 1));
        /*map.setLatLngBoundsForCameraTarget(areas);*/
    }



/*        for (LatLng p : pointSets.get(level)) {
            map.addMarker(new MarkerOptions()
                    .position(p)
                    .icon(BitmapDescriptorFactory.fromResource(R.raw.inters)));

        }*/




/*    private void retrieveFileFromResource() {
        try {
            *//*kmlIntersectionsLayer = new KmlLayer(map, R.raw.intersections,this.activity.getApplicationContext());*//*
            kmlNeighbourhood_areas = new KmlLayer(map, R.raw.areas, this.activity.getApplicationContext());
            *//*points = new ArrayList<LatLng>();*//*
            areas = new ArrayList<LatLngBounds>();


            *//*points = getPoints(kmlIntersectionsLayer.getContainers());*//*
            areas = getPolygons(kmlNeighbourhood_areas.getContainers());




            *//*pointSets = sortPointsToAreas(points, areas);*//*

            Log.d("gamelevl", "now" + this.activity.getLevelarea());
            *//*Log.d("pointsse", pointSets.toString());*//*
            *//*kmlIntersectionsLayer.addLayerToMap();*//*

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }*/

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

/*    private List<LatLngBounds> getPolygons(final Iterable<KmlContainer> containers) {
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
    }*/

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


}

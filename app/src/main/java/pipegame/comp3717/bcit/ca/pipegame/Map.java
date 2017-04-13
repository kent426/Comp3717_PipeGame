package pipegame.comp3717.bcit.ca.pipegame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import pipegame.comp3717.bcit.ca.pipegame.BFS.Edge;
import pipegame.comp3717.bcit.ca.pipegame.BFS.IntersectionMap;
import pipegame.comp3717.bcit.ca.pipegame.BFS.IntersectionNode;

import static java.lang.String.valueOf;

/**
 * Created by Kent on 2017-02-21.
 */

public class Map extends AsyncTask<GoogleMap, Integer, GoogleMap> implements Serializable {

    private GameActivity activity;

    private GoogleMap map;
    Bounds bounds;

    /*version 3*/
    IntersectionMap interMap;

    /*version 3*/

    /*version 2*/
/*    Intersections intersections;
    ArrayList<PolylineOptions> allConnectedPoints;
    private HashMap<LatLng,LatLng[]> startNEnd;*/
    /*version 2*/

    private KmlLayer kmlIntersectionsLayer;
    private KmlLayer kmlNeighbourhood_areas;
    private ArrayList<LatLng> points;
    private List<LatLngBounds> areas;
    private Marker moveOj;
    private Marker destin;
    private HashMap<Marker, Edge> markerToEdgeMap;
    private HashMap<Edge, Marker> EdgeTomakerMap;
    //points in different level
    static private List<ArrayList<LatLng>> pointSets;
    //the levels:from 0 to 14, used for filtering the points
    private int levelarea;

    public Map(GameActivity a) {
        activity = a;
        bounds = new Bounds(a);
        interMap = Singleton.getInstance(a);
        levelarea = a.getLevelarea();
/*        intersections = new Intersections(a);
        startNEnd = intersections.getFindStartAndEnd();*/
        /*retrieveFileFromResource();*/


    }

    protected void onPreExecute() {
        super.onPreExecute();
        map = activity.getmap();


    }

    private ArrayList<Marker> highlighted = new ArrayList<>();

    public void changeMarkercolorFromRedToGreen(Marker m) {
        if (highlighted.contains(m)) {
            m.setIcon(BitmapDescriptorFactory.fromResource(R.raw.highligtedinters));
        }
    }

    public void IntersectionToFindMidPointMarker(IntersectionNode inNode, IntersectionNode FromNode) {
        for (Marker m : highlighted) {
            m.setIcon(BitmapDescriptorFactory.fromResource(R.raw.inters));
        }
        highlighted.clear();
        LinkedList<Edge> adEdges = inNode.getadEdges();
        for (Edge eg : adEdges) {
            if (!eg.contains(FromNode)) {
                Marker mk = EdgeTomakerMap.get(eg);
                mk.setIcon(BitmapDescriptorFactory.fromResource(R.raw.gg));
                highlighted.add(mk);
            }
        }
    }

    @Override
    protected GoogleMap doInBackground(GoogleMap... g) {

            bounds.readpointsForBound();




        /*version3*/
        /*interMap.readAndConstruct();*/



        /*version3*/

        /*Bfs*/
        //interMap.BFS();

        /*version 2*/
/*        intersections.readPoints();
        allConnectedPoints = intersections.getPolylineOptions();
        points = intersections.getPoints();*/
        /*version2*/


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


        /*version3*/

        for (PolylineOptions p : interMap.getPolygonOptions()) {
            g.addPolyline(p);
        }

        markerToEdgeMap = new HashMap<>();
        EdgeTomakerMap = new HashMap<>();
        HashMap<MarkerOptions, Edge> pairs = interMap.getMidPointsInBound(bn);
        for (MarkerOptions mr : pairs.keySet()) {
            Marker marker = g.addMarker(mr);

            //markers as keys and edges as values
            markerToEdgeMap.put(marker, pairs.get(mr));

            EdgeTomakerMap.put(pairs.get(mr), marker);

        }

        //selecting starting and ending markers
        final LinkedList<IntersectionNode> DestNodesinBound = interMap.getRandomPoints(bn);

        IntersectionNode dest = DestNodesinBound.get((new Random().nextInt(DestNodesinBound.size())));

        IntersectionNode[] startDesttmp = new IntersectionNode[3];
        Log.d("", "rrr: " + bounds.getlevel());

        while (true) {
            IntersectionNode start = dest;
            IntersectionNode startToward = dest;
            LinkedList<IntersectionNode> NoInpath = new LinkedList<>();
            startToward = dest.getRandomadNode();
            for (int i = 0; i < (bounds.getlevel() + 1) * 2; i++) {
                if (!start.equals(dest)&&start.getAdjacentNodes().size()!=1)
                    startToward = start.getRandomadNode();
                start = startToward.getRandomadNode();
                NoInpath.add(start);
            }

            if (!start.equals(dest) && !startToward.equals(dest)) {
                startDesttmp[0] = start;
                startDesttmp[1] = dest;
                startDesttmp[2] = startToward;
                Log.d("cool", "eee: " + startDesttmp.toString());
                break;
            }
        }
        final IntersectionNode[] startDest = startDesttmp;


        moveOj = map.addMarker(new MarkerOptions().position(startDest[0].getLocation())
                .title("hello"));
        moveOj.setIcon(BitmapDescriptorFactory.fromResource(R.raw.obj));
        moveOj.setAnchor(0.5f,0.5f);
        moveOj.setTitle("Click to start");
        moveOj.showInfoWindow();
        Log.d("old ", valueOf(startDest[0].getLocation()));
        startDest[0].setSelected(true);
        destin = map.addMarker(new MarkerOptions().position(startDest[1].getLocation())
                .title("Destination"));
        destin.setIcon(BitmapDescriptorFactory.fromResource(R.raw.eeend));
        destin.setAnchor(0.5f,0.5f);
        destin.setTitle("Destination");

        double cLat = (destin.getPosition().latitude + moveOj.getPosition().latitude) / 2.0;
        double cLong = (destin.getPosition().longitude + moveOj.getPosition().longitude) / 2.0;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cLat, cLong), 15));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            IntersectionNode i = startDest[0];
            IntersectionNode prevNode = i;

            LatLng cur = moveOj.getPosition();
            LatLng previousLoc = cur;
            LatLng destinlocation = destin.getPosition();




            int count = 1;
            int countTheFirst = 1;


            @Override
            public boolean onMarkerClick(Marker marker) {

                changeMarkercolorFromRedToGreen(marker);

                //tap the mid point on the edge, set two end points's selected attribute to true
                if (markerToEdgeMap.containsKey(marker)) {
                    Edge ed = markerToEdgeMap.get(marker);
                    ed.getFirst().setSelected(true);
                    ed.getSecond().setSelected(true);

                    Log.d("----------->>>>>>", ed.getFirst().getLocation().longitude + "," + ed.getFirst().getLocation().latitude
                            + "," + ed.getSecond().getLocation().longitude + "," + ed.getSecond().getLocation().latitude);

                    Log.d("----------->>>>>>", ed.getSecond().getLocation().longitude + "," + ed.getSecond().getLocation().latitude
                            + "," + ed.getFirst().getLocation().longitude + "," + ed.getFirst().getLocation().latitude);
                }


                if (marker.equals(moveOj) && count++ == 1) {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {

                                while (true) {

                                    //if reach destin, stop moving
                                    if (cur.equals(destinlocation)) {
                                        Log.d("game status", "win");


                                        if(!Singleton.isCancelAsyn()){
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                                    builder.setMessage("You Win");
                                                    builder.setTitle("Hello");
                                                    builder.setPositiveButton("Level Select", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // User clicked OK button
                                                            Intent next = new Intent(activity,LevelSelect.class);

                                                            Log.d("checking level from", "onClick: " + levelarea);
                                                            next.putExtra("level", levelarea);

                                                            activity.finish();

                                                            activity.startActivity(next);

                                                            dialog.dismiss();
                                                            //
                                                        }




                                                    });
                                                    builder.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // User clicked OK button
                                                            Intent next = new Intent(activity,MainActivity.class);

                                                            Log.d("checking level from", "onClick: " + levelarea);
                                                            next.putExtra("level", levelarea);

                                                            activity.finish();

                                                            activity.startActivity(next);

                                                            dialog.dismiss();
                                                            //
                                                        }




                                                    });

                                                    if(!Singleton.isCancelAsyn()) {
                                                        AlertDialog dialog = builder.create();
                                                        dialog.show();
                                                    }


                                                }

                                            });

                                            break;
                                        }
                                        }



                                    int totalIntersections = i.getAdjacentNodes().size();
                                    int tappedNumber = 0;
                                    IntersectionNode nonTappedNode = i.getAdjacentNodes().get(0);

                                    //find how many nodes have been tapped for next intersection
                                    for (IntersectionNode nextNode : i.getAdjacentNodes()) {
                                        if (nextNode.isSelected()) {
                                            tappedNumber++;
                                        } else {
                                            nonTappedNode = nextNode;
                                        }
                                    }

                                    if (countTheFirst == 1) {
                                        nonTappedNode = startDest[2];
                                    }

                                    if (totalIntersections - 1 == tappedNumber || countTheFirst++ == 1) {
                                        prevNode = i;
                                        i = nonTappedNode;
                                        previousLoc = cur;
                                        cur = i.getLocation();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    MarkerAnimation.animateMarkerToHCAndDraw(get(), moveOj,
                                                            i.getLocation(), new LatLngInterpolator.Linear());
                                                    //change to the red images for the selectable
                                                    IntersectionToFindMidPointMarker(i, prevNode);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } catch (ExecutionException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        });

                                        //set back to false to be ready for the next round
                                        for (IntersectionNode one : i.getAdjacentNodes()
                                                ) {
                                            one.setSelected(false);
                                            if (one.getLocation().equals(previousLoc)) {
                                                one.setSelected(true);
                                                Log.d("set pre", "yes");
                                            }

                                        }


                                        //no need to consider the node that is the starting point of the previous round

                                        /*IntersectionNode previousStartPoint = interMap.getNodeByLatLng(previousLoc);

                                        if(previousStartPoint!=null){
                                            previousStartPoint.setSelected(true);
                                            Log.d("set pre", "yes");
                                        }*/

                                    } else {
                                        //TODO pops up failure dialog
                                        if(!Singleton.isCancelAsyn()){
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                                                    builder.setMessage("You Lose");
                                                    builder.setTitle("Oops");
                                                    builder.setPositiveButton("Level Select", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // User clicked OK button
                                                            Intent next = new Intent(activity,LevelSelect.class);

                                                            Log.d("checking level from", "onClick: " + levelarea);
                                                            next.putExtra("level", levelarea);

                                                            activity.finish();

                                                            activity.startActivity(next);

                                                            dialog.dismiss();
                                                            //
                                                        }
                                                    });
                                                    builder.setNegativeButton("Menu", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            // User clicked OK button
                                                            Intent next = new Intent(activity,MainActivity.class);

                                                            Log.d("checking level from", "onClick: " + levelarea);
                                                            next.putExtra("level", levelarea);

                                                            activity.finish();

                                                            activity.startActivity(next);

                                                            dialog.dismiss();
                                                            //
                                                        }
                                                    });
                                                    if(!Singleton.isCancelAsyn()) {
                                                        AlertDialog dialog = builder.create();
                                                        dialog.show();
                                                    }


                                                }

                                            });
                                        }

                                        Log.d("tappedNum", valueOf(tappedNumber));
                                        Log.d("totalNum", valueOf(totalIntersections));
                                        Log.d("game status", "end");
                                        break;

                                    }

                                    //i = i.getAdjacentNodes().get(new Random().nextInt(i.getAdjacentNodes().size()));


                                    sleep(3000);


                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };

                    thread.start();


                }
                return true;
            }
        });













        /*version3*/






/*        for(PolylineOptions p: allConnectedPoints) {
            g.addPolyline(p);
        }

        ArrayList<LatLng> curLevelPoint = new ArrayList<>();
        for (LatLng p : points) {
            *//*if(bn.contains(p)) {*//*
                curLevelPoint.add(p);
                map.addMarker(new MarkerOptions()
                        .position(p)
                        .anchor(0.5f,0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.inters)));

            *//*}*//*

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
        });*/


    }

    private void displayPointsAndArea(LatLngBounds areas) {
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

    @Override
    protected void onCancelled(GoogleMap googleMap) {
        super.onCancelled(googleMap);

    }
}

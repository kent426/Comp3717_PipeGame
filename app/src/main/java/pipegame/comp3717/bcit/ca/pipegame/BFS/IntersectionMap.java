package pipegame.comp3717.bcit.ca.pipegame.BFS;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import pipegame.comp3717.bcit.ca.pipegame.R;

/**
 * Created by Kent on 2017-03-13.
 */

public class IntersectionMap {
    //game activity
    Activity act;
    private Set<IntersectionNode> allNodes;
    private Set<Edge> allEdges;

    public IntersectionMap(Activity actv) {
        allNodes = new HashSet<IntersectionNode>();
        allEdges = new HashSet<Edge>();
        this.act = actv;
    }

    public void readAndConstruct() {
        Scanner s = new Scanner(act.getResources().openRawResource(R.raw.pathpoints));


        while (s.hasNext()) {
            String line = s.next();
            String[] pieces = line.split(",");
            LatLng  first = new LatLng(Double.parseDouble(pieces[1]), Double.parseDouble(pieces[0]));
            LatLng second = new LatLng(Double.parseDouble(pieces[3]), Double.parseDouble(pieces[2]));
            //two nodes
            IntersectionNode a = new IntersectionNode(first);
            IntersectionNode b = new IntersectionNode(second);
            Edge edge = new Edge(a,b);
            allNodes.add(a);
            allNodes.add(b);
            allEdges.add(edge);
        }


        s.close();

        for(IntersectionNode node: allNodes) {
            for(Edge e: allEdges) {
                node.addAdjList(e);
                node.addAdjEdges(e);
            }
        }
    }

    public ArrayList<PolylineOptions> getPolygonOptions() {
        ArrayList<PolylineOptions> allPolylines = new ArrayList<PolylineOptions>();
        int count = 1;
        int col = Color.BLACK;
        for(Edge edge: allEdges) {
            switch((count++)%5) {
                case 0: col = Color.BLACK; break;
                case 1: col = Color.RED;break;
                case 2: col = Color.BLUE;break;
                case 3: col =Color.GREEN;break;
                case 4: col = Color.MAGENTA;break;



            }
            allPolylines.add(new PolylineOptions()
                    .add(edge.getFirst().getLocation(),edge.getSecond().getLocation())
                    .width(5)
                    .color(col));
            /*Log.d("points",pieces[0]+pieces[1]+pieces[2]+pieces[3]);*/
        }
        return allPolylines;

    }

    public HashMap<MarkerOptions,Edge> getMidPointsInBound(LatLngBounds bn) {
        HashMap<MarkerOptions,Edge> midpoints = new HashMap<>();
        for(Edge edge: allEdges) {
            if(bn.contains(edge.getFirst().getLocation())
                    &&bn.contains(edge.getSecond().getLocation())) {
                LatLng lalg = edge.getMidLocation();
                MarkerOptions markerOption = new MarkerOptions()
                        .position(lalg)
                        .anchor(0.5f,0.5f)
                        .icon(BitmapDescriptorFactory.fromResource(R.raw.inters));
                midpoints.put(markerOption,edge);

            }
        }

        return midpoints;

    }

    public IntersectionNode[] getRandomPoints(LatLngBounds bn) {
        LinkedList<IntersectionNode> locations = new LinkedList<>();

        for(IntersectionNode one: allNodes) {
            LatLng oneLocation = one.getLocation();
            if(bn.contains(oneLocation)) {
                locations.add(one);
            }
        }
        IntersectionNode[] startAndDestination = new IntersectionNode[2];
        startAndDestination[0] = locations.remove((new Random().nextInt(locations.size())));
        startAndDestination[1] = locations.remove((new Random().nextInt(locations.size())));

        return startAndDestination;
    }


    public void addNodes(LatLng oneNode) {

    }
}

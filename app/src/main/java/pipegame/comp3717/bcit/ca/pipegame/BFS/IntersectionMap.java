package pipegame.comp3717.bcit.ca.pipegame.BFS;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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


    HashMap<IntersectionNode,Boolean> nodetoBool;

    public void BFS() {
        int numNode = allNodes.size();
        nodetoBool = new HashMap<>();
        for(IntersectionNode one: allNodes) {
            nodetoBool.put(one,false);
        }
        Iterator<IntersectionNode> iter = allNodes.iterator();
        System.out.println("------------BFS-------------");
        int num = 0;
        while(iter.hasNext()) {
            num++;
            IntersectionNode next = iter.next();
            if(!nodetoBool.get(next))
                bfs(next);
        }
        System.out.println("------------nnnn-------------");
    }



    private void bfs(IntersectionNode v) {
        HashSet<Edge> setNodes = new HashSet<>();
        nodetoBool.put(v,true);
        /*System.out.println("visiting vertex " + v.getLocation());*/
        Deque<IntersectionNode> q = new LinkedList<IntersectionNode>();
        q.add(v);
        int num = 0;
        while(!q.isEmpty()) {
            LinkedList<IntersectionNode> adjs = q.peekFirst().getAdjacentNodes();
            for(IntersectionNode ad: adjs) {
                if(!nodetoBool.get(ad)) {
                    nodetoBool.put(ad,true);
                    Edge newEd = new Edge(q.peekFirst(),ad);
                    /*if(!setNodes.contains(newEd)) {
                        setNodes.add(newEd);
                        *//*Log.d("BFS",q.peekFirst().getLocation().longitude + "," +q.peekFirst().getLocation().latitude
                                + "," +  ad.getLocation().longitude +","+ad.getLocation().latitude);*//*
                    }*/
                    boolean ne = true;
                    for(Edge x: setNodes) {
                        if(newEd.getFirst().getLocation().equals(((Edge) x).getFirst().getLocation())&&newEd.getSecond().getLocation().equals(((Edge) x).getSecond().getLocation())) {
                            ne = false;
                        }
                        if(newEd.getFirst().getLocation().equals(((Edge) x).getSecond().getLocation())&&newEd.getSecond().getLocation().equals(((Edge) x).getFirst().getLocation())) {
                            ne = false;
                        }
                    }
                    if(ne) {
                        setNodes.add(newEd);
                    }


                    q.add(ad);
                }
            }
            q.pollFirst();
        }

        if(q.isEmpty()) {

            for(Edge one: setNodes) {
                String writeoneSet="";
                writeoneSet += String.valueOf(one.getFirst().getLocation().longitude) + "," +String.valueOf(one.getFirst().getLocation().latitude)
                            + "," +  String.valueOf(one.getSecond().getLocation().longitude) +","+String.valueOf(one.getSecond().getLocation().latitude) + "\n";
                Log.d("BFS", writeoneSet);
            }

            System.out.println("------------one set-------------" +allNodes.size());

        }
    }

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
            if(!allEdges.contains(edge))
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

    public IntersectionNode getNodeByLatLng (LatLng lalg) {
        IntersectionNode itemToFind = new IntersectionNode(lalg);
           for(IntersectionNode one: allNodes) {
               if(one.getLocation().equals(lalg)) {
                   one.setSelected(true);
                   return one;
               }
           }

        return null;
    }
}

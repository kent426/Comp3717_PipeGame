package pipegame.comp3717.bcit.ca.pipegame;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

/**
 * Created by Kent on 2017-02-20.
 */

public class Intersections {
    private ArrayList<String[]> allconnectedPs;
    private ArrayList<LatLng> points;
    Activity act;



    private java.util.HashMap<LatLng,LatLng[]> findStartAndEnd;



    public Intersections(Activity a) {
        allconnectedPs = new ArrayList<String[]>();
        points = new ArrayList<LatLng>();
        act = a;
        findStartAndEnd = new HashMap<>();
    }

    public HashMap<LatLng, LatLng[]> getFindStartAndEnd() {
        return findStartAndEnd;
    }





    public void readPoints() {
        Scanner s = new Scanner(act.getResources().openRawResource(R.raw.pathpoints));


        while (s.hasNext()) {
            String line = s.next();
            String[] pieces = line.split(",");
            LatLng  p = new LatLng((Double.parseDouble(pieces[1]) + Double.parseDouble(pieces[3]))/2,(Double.parseDouble(pieces[0]) + Double.parseDouble(pieces[2]))/2);



            LatLng[] strend = {new LatLng(Double.parseDouble(pieces[1]), Double.parseDouble(pieces[0])), new LatLng(Double.parseDouble(pieces[3]), Double.parseDouble(pieces[2]))};
            findStartAndEnd.put(p,strend);
            points.add(p);
            allconnectedPs.add(pieces);




        }
        s.close();
    }



    public ArrayList<PolylineOptions> getPolylineOptions() {
        ArrayList<PolylineOptions> allPolylines = new ArrayList<PolylineOptions>();
        int count = 1;
        int col = Color.BLACK;

        for(String[] pieces: allconnectedPs) {
            switch((count++)%5) {
                case 0: col = Color.BLACK; break;
                case 1: col = Color.RED;break;
                case 2: col = Color.BLUE;break;
                case 3: col =Color.GREEN;break;
                case 4: col = Color.MAGENTA;break;



            }
            allPolylines.add(new PolylineOptions()
                    .add(new LatLng(Double.parseDouble(pieces[1]), Double.parseDouble(pieces[0])), new LatLng(Double.parseDouble(pieces[3]), Double.parseDouble(pieces[2])))
                    .width(5)
                    .color(col));
            /*Log.d("points",pieces[0]+pieces[1]+pieces[2]+pieces[3]);*/
        }
        return allPolylines;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }







}

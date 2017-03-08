package pipegame.comp3717.bcit.ca.pipegame;

import android.app.Activity;
import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by Kent on 2017-02-21.
 */

public class Bounds {
    private ArrayList<LatLng> points;
    private LatLngBounds bound;
    GameActivity act;

    public Bounds(GameActivity a) {
        points = new ArrayList<LatLng>();
        act = a;
        readpointsForBound();
    }





    public void readpointsForBound() {
        String name;
        Scanner s = new Scanner(act.getResources().openRawResource(act.getResources().getIdentifier("bound" + act.getLevelarea(),"raw", act.getPackageName())));

        ArrayList<String> all = new ArrayList<>();
        String allSt = "";
        LatLngBounds.Builder ber =  new LatLngBounds.Builder();

        while (s.hasNext()) {
            allSt += s.next();
        }
            String[] pieces = allSt.split(",0");
            for(String p: pieces) {
                String[] xy = p.split(",");
                /*points.add(new LatLng(Double.parseDouble(xy[1]),Double.parseDouble(xy[0])));*/
                ber.include(new LatLng(Double.parseDouble(xy[1]),Double.parseDouble(xy[0])));
            }
        System.out.println(Arrays.toString(pieces));
        bound = ber.build();


        s.close();
    }

    public LatLngBounds getBound() {


        return bound;
    }

}

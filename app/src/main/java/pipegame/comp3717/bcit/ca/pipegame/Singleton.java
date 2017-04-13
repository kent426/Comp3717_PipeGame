package pipegame.comp3717.bcit.ca.pipegame;

import android.app.Activity;

import pipegame.comp3717.bcit.ca.pipegame.BFS.IntersectionMap;

/**
 * Created by Kent on 2017-03-26.
 */

public class Singleton {

        private static IntersectionMap intermap;


    public static boolean isCancelAsyn() {
        return cancelAsyn;
    }

    public static void setCancelAsyn(boolean cancelAs) {
        cancelAsyn = cancelAs;
    }

    private static boolean cancelAsyn = false;


        private Singleton() {
        }

        public static boolean isCreated() {
            return intermap==null?false:true;
        }

        public static IntersectionMap getInstance(Activity a) {
            if (intermap == null) {


                intermap = new IntersectionMap(a);

            }
            return intermap;
        }

        // other useful methods here
    }


package pipegame.comp3717.bcit.ca.pipegame.BFS;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Kent on 2017-03-13.
 */

public class Edge {
    private IntersectionNode one;
    private IntersectionNode two;

    public Edge(IntersectionNode fst, IntersectionNode sed) {
        one = fst;
        two = sed;
    }


    public boolean contains(IntersectionNode node) {
        return node.equals(one)|| node.equals(two);
    }


    public IntersectionNode returnTheOtherNode(IntersectionNode o) {
        if(o.equals(one))
            return two;
        if(o.equals(two))
            return one;
        return null;
    }

    public LatLng getMidLocation () {

        double la = (this.getFirst().getLocation().latitude+this.getSecond().getLocation().latitude)/2;
        double lg = (this.getFirst().getLocation().longitude+this.getSecond().getLocation().longitude)/2;
        return new LatLng(la,lg);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Edge) {
            if(this.getFirst().getLocation().equals(((Edge) obj).getFirst().getLocation())&&this.getSecond().getLocation().equals(((Edge) obj).getSecond().getLocation())) {
                return true;
            }
            if(this.getFirst().getLocation().equals(((Edge) obj).getSecond().getLocation())&&this.getSecond().getLocation().equals(((Edge) obj).getFirst().getLocation())) {
                return true;
            }
        }

        return false;
    }

    public String getEdge() {
        return one.getLocation().toString()+"&&&" +two.getLocation().toString();
    }
    public IntersectionNode getFirst() {
        return one;
    }

    public IntersectionNode getSecond() {
        return two;
    }

}

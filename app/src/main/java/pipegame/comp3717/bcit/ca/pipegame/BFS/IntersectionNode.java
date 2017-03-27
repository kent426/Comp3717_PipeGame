package pipegame.comp3717.bcit.ca.pipegame.BFS;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

/**
 * Created by Kent on 2017-03-13.
 */

public class IntersectionNode {
    private LatLng location;
    private LinkedList<IntersectionNode> adjacencyNodes;
    private LinkedList<Edge> adjacencyEdges;
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public IntersectionNode(LatLng one) {
        location = one;
        selected = false;
        adjacencyNodes = new LinkedList<IntersectionNode>();
        adjacencyEdges = new LinkedList<Edge>();
    }

    public void addAdjEdges(Edge edge) {
        if(edge.contains(this)) {
            if(!adjacencyEdges.contains(edge)){
                adjacencyEdges.add(edge);
            }
        }
    }

    public void addAdjList(Edge edge) {
        //if the edge has the current node
        if(edge.contains(this)) {
            //return the other node in the edge
            IntersectionNode other = edge.returnTheOtherNode(this);
            //the other node is not in the adjacencyList
            if(!adjacencyNodes.contains(other)&&other!=null) {
                adjacencyNodes.add(other);
            }
        }
    }

    public LatLng getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object var1) {
        if(this == var1) {
            return true;
        } else if(!(var1 instanceof IntersectionNode)) {
            return false;
        } else {
            IntersectionNode var2 = (IntersectionNode)var1;
            return this.location.equals(var2.getLocation());
        }
    }

    public LinkedList<IntersectionNode> getAdjacentNodes() {
        return adjacencyNodes;
    }



}

/*
 * Copyright (c) 2005, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 *
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 *
 * Created on Jul 9, 2005
 */

package ltsa.jung;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.PolarPoint;
import edu.uci.ics.jung.graph.Graph;

/**
 * A radial layout for Tree or Forest graphs.
 * 
 * @author Tom Nelson 
 * @author Cédric Delforge - graph handling
 * @author Charles Pecheur - fixes, support resizing
 *  
 */
public class RadialGraphLayout<V,E> extends TreeLikeGraphLayout<V,E> {

    protected Map<V,PolarPoint> polarLocations;

    /**
     * Creates an instance for the specified graph with default X and Y distances.
     */
    public RadialGraphLayout(Graph<V,E> g) {
    	super(g);
    }

	@Override
    protected void buildTree() {
	    super.buildTree();
	    this.polarLocations = new HashMap<V, PolarPoint>();
        setPolarLocations();
    }

    @Override
    public void setSize(Dimension size) {
    	this.size = size;
        buildTree();
    }

	@Override
    public void setLocation(V v, Point2D location)
    {
        Point2D c = getCenter();
        Point2D pv = new Point2D.Double(location.getX() - c.getX(), 
                location.getY() - c.getY());
        PolarPoint newLocation = PolarPoint.cartesianToPolar(pv);
        PolarPoint currentLocation = polarLocations.get(v);
        if (currentLocation == null)
        	polarLocations.put(v, newLocation);
        else
        	currentLocation.setLocation(newLocation);
     }
	
	/**
	 * Returns the map from vertices to their locations in polar coordinates.
	 */
	public Map<V,PolarPoint> getPolarLocations() {
		return polarLocations;
	}

	@Override
    public Point2D transform(V v) {
		PolarPoint pp = polarLocations.get(v);
		double centerX = size.width / 2;
		double centerY = size.height / 2;
		Point2D cartesian = PolarPoint.polarToCartesian(pp);
		cartesian.setLocation(cartesian.getX() + centerX, cartesian.getY() + centerY);
		return cartesian;
	}
		
	private void setPolarLocations() {
		double radius = Math.min(size.width, size.height) / 2 - MARGIN;
		double theta = 2*Math.PI / (size.width - 2 * MARGIN + distX);
		double deltaRadius = radius / (size.height - 3 * MARGIN);
		
		for(Map.Entry<V, Point2D> entry : locations.entrySet()) {
			V v = entry.getKey();
			Point2D p = entry.getValue();
			PolarPoint polarPoint = new PolarPoint(
					(p.getX() - MARGIN) * theta + Math.PI / 2, 
					(p.getY() - 2 * MARGIN) * deltaRadius);
			polarLocations.put(v, polarPoint);
		}
	}
}

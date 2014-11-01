package com.company;

import spark.data.*;

import java.awt.geom.*;
import java.util.*;

public interface Selectable {


	/**
	 * takes a point and if the object or its contents are selected then it returns a path to the selected object, not in the transformed coordinates
	 * @param x in the coordinates of your panel
	 * @param y in the coordinates of your panel
	 * @param myIndex
	 * @param transform - the full transform from current contents coordinates to the coordinates of your panel
	 * @return - If the object or its contents are not selected, then NULL is returned
	 */
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform);


	/**
	 * Returns an array of control points that can be used to modify the geometry of the object
	 * @return Point2D[]
	 */
	public Point2D[] controls();

	public String getClassStatus();

	public void setBackgroundColor(SO newColor);
}

package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.SO;
import spark.data.SOReflect;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Ellipse extends SOReflect implements Drawable, Selectable, Interactable {

	public double left;
	public double top;
	public double width;
	public double height;
	public double thickness;
	public SO border;
	public SO fill;

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		if (thickness > 0) {
			Stroke stroke = new BasicStroke((int) thickness);
			g2.setStroke(stroke);
		} else {
			Stroke stroke = new BasicStroke(1);
			g2.setStroke(stroke);
		}

		if (fill != null) {
			Double red = fill.get("r").getDouble();
			Double green = fill.get("g").getDouble();
			Double blue = fill.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
		}
//		Draw fill
		g2.drawOval((int) left, (int) top, (int) width, (int) height);

		if (border != null) {
			Double red = border.get("r").getDouble();
			Double green = border.get("g").getDouble();
			Double blue = border.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
		}
//		Draw border
		g2.fillOval((int) left, (int) top, (int) width, (int) height);
	}

	/**
	 * takes a point and if the object or its contents are selected then it returns a path to the selected object, not in the transformed coordinates
	 * @param x in the coordinates of your panel
	 * @param y in the coordinates of your panel
	 * @param myIndex
	 * @param transform - the full transform from current contents coordinates to the coordinates of your panel
	 * @return - If the object or its contents are not selected, then NULL is returned
	 */
	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);
		System.out.println("(" + ptDst.getX() + ", " + ptDst.getY() + ")");

		boolean isInEllipse = SelectUtils.checkIfInOvalShape(this, ptDst.getX(), ptDst.getY());

		if (isInEllipse) {
			ArrayList<Integer> arrayList = new ArrayList<Integer>();
			arrayList.add(myIndex);
			return arrayList;
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[4];
		retArray[0] = new Point(this.left, this.top);
		retArray[1] = new Point(this.left + this.width, this.top);
		retArray[2] = new Point(this.left, this.top + this.height);
		retArray[3] = new Point(this.left + this.width, this.top + this.height);
		return retArray;
	}

	@Override
	public void setBackgroundColor(SO newColor) {
		throw new NotImplementedException();
	}

	@Override
	public Root getPanel() {
		throw new NotImplementedException();
	}

	@Override
	public boolean key(char key) {
		return false;
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return false;
	}
}
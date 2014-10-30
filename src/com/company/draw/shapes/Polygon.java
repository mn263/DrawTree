package com.company.draw.shapes;

import com.company.*;
import spark.data.SA;
import spark.data.SO;
import spark.data.SOReflect;
import spark.data.SV;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Polygon extends SOReflect implements Drawable, Selectable, Interactable {

	public SA points;
	public double thickness;
	public SO border;
	public SO fill;


	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
//		Make point arrays
		int[] xArray = new int[points.size()];
		int[] yArray = new int[points.size()];
		for (int i = 0; i < points.size(); i++) {
			SV xPoint = points.get(i).get("x");
			SV yPoint = points.get(i).get("y");
			Long x = xPoint.getLong();
			Long y = yPoint.getLong();
			xArray[i] = x.intValue();
			yArray[i] = y.intValue();
		}

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
		g2.drawPolygon(xArray, yArray, points.size());

		if (border != null) {
			Double red = border.get("r").getDouble();
			Double green = border.get("g").getDouble();
			Double blue = border.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
		}
//		Draw border
		g2.fillPolygon(xArray, yArray, points.size());
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
		// TODO: implement when we are asked to
		return null;
	}

	@Override
	public Point2D[] controls() {
		throw new UnsupportedOperationException("This method is not implemented");
//		return new Point2D[0];
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

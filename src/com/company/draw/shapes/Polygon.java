package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
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

	public Polygon() {}

	public Polygon(SA points, double thickness, SO fill) {
		this.points = points;
		this.thickness = thickness;
		this.fill = fill;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
//		Make point arrays
		int[] xArray = getPoints("X");
		int[] yArray = getPoints("Y");

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
	//		Draw fill
			g2.fillPolygon(xArray, yArray, points.size());
		}

		if (border != null) {
			Double red = border.get("r").getDouble();
			Double green = border.get("g").getDouble();
			Double blue = border.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
	//		Draw border
			g2.drawPolygon(xArray, yArray, points.size());
		}
	}

	private ArrayList<Point> getPoints() {
		int[] xArray = getPoints("X");
		int[] yArray = getPoints("Y");

		ArrayList<Point> points = new ArrayList<>();
		for (int i = 0; i < xArray.length; i++) {
			Point point = new Point(xArray[i], yArray[i]);
			points.add(point);
		}
		return points;
	}

	private int[] getPoints(String coord) {
		int[] xArray = new int[points.size()];
		int[] yArray = new int[points.size()];
		for (int i = 0; i < points.size(); i++) {
			SV xPoint = points.get(i).get("x");
			SV yPoint = points.get(i).get("y");
			try {
				Long x = xPoint.getLong();
				Long y = yPoint.getLong();
				xArray[i] = x.intValue();
				yArray[i] = y.intValue();
			} catch (Exception e) {
				double x = xPoint.getDouble();
				double y = yPoint.getDouble();
				xArray[i] = (int) x;
				yArray[i] = (int) y;
			}
		}
		if (coord.equals("X")) {
			return xArray;
		} else {
			return yArray;
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();

		int[] xArray = getPoints("X");
		int[] yArray = getPoints("Y");
		if (PolyContains.contains(x, y, xArray, yArray)) {
			return new ArrayList<>();
		} else {
			return null;
		}
	}

	@Override
	public Point2D[] controls() {
		ArrayList<Point> polygonPoints = getPoints();
		Point2D[] controlPoints = new Point2D[polygonPoints.size()];
		for (int i = 0; i < polygonPoints.size(); i++) {
			controlPoints[i] = polygonPoints.get(i);
		}
		return controlPoints;	}

	@Override
	public void setBackgroundColor(SO newColor) {
		this.fill = newColor;
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
	public void makeIdle() {}

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

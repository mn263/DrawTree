package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static java.lang.Math.pow;

public class Curve extends SOReflect implements Drawable, Selectable, Interactable {

	//	Curve{ just like Polygon } This has points just like Polygon.
	// The difference is that these points define a Catmull-Rom curve rather than a polygon.
	// If the first point and the last point are the same, then the curve is closed and can have a fill: color.
	// This should implement Selectable.
	// If the curve is not filled, then it is selected if the point is within 3 pixels of the curve.
	// If the curve is filled then it is selected if the point is inside the curve.
	public SA points;
	public double thickness;
	public SO border;
	public SO fill;


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
		}

//		CHECK IF CLOSED
		if (isClosed(xArray, yArray)) {
			// Draw fill
			g2.drawPolygon(xArray, yArray, points.size());

			if (border != null) {
				Double red = border.get("r").getDouble();
				Double green = border.get("g").getDouble();
				Double blue = border.get("b").getDouble();
				Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
				g2.setColor(lineColor);
			}
			// Draw border
			g2.fillPolygon(xArray, yArray, points.size());
		} else {
			for (int index = 0; index < xArray.length; index++) {
				g2.drawRect(xArray[index] - 2, yArray[index] - 2, 4, 4);
			}
		}
	}

	private boolean isClosed(int[] xArray, int[] yArray) {
		return xArray[xArray.length - 1] == xArray[0] && yArray[yArray.length - 1] == yArray[0];
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
		int[] xArray = getPoints("X");
		int[] yArray = getPoints("Y");
		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);
		x = ptDst.getX();
		y = ptDst.getY();

		if (isClosed(xArray, yArray)) {
//			TODO: test this
			if (PolyContains.contains(x, y, xArray, yArray)) return new ArrayList<>();
		} else {
			for (int i = 0; i < xArray.length; i++) {
//				TODO: test this
				Point currPoint = new Point(xArray[i], yArray[i]);
				double xSqr = pow(currPoint.getX() - ptDst.getX(), 2);
				double ySqr = pow(currPoint.getY() - ptDst.getY(), 2);
				double distance = pow(xSqr + ySqr, 0.5);
				if (distance <= 3) {
					System.out.println("Selected");
					return new ArrayList<>();
				}
			}
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		throw new UnsupportedOperationException("This method is not implemented");
	}

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

package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Polyline extends SOReflect implements Drawable, Selectable, Interactable {

	public SA points;
	public double thickness;
	public SO color;


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

		if (color != null) {
			Double red = color.get("r").getDouble();
			Double green = color.get("g").getDouble();
			Double blue = color.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
		}
//		Draw fill
		g2.drawPolyline(xArray, yArray, points.size());
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		Point2D click = transform.transform(new Point(x, y), null);
		double allowance = (thickness * transform.getScaleX()) / 4 + (thickness * transform.getScaleY()) / 4 + 3;
		ArrayList<Point> pointsList = getLinesInPolyline();

		for (int i = 0; i < pointsList.size(); i++) {
			Point from = pointsList.get(i);
			Point to = (i + 1 == pointsList.size()) ? pointsList.get(0) : pointsList.get(i + 1);
			if (SelectUtils.checkIfInLine(from.getX(), to.getX(), from.getY(), to.getY(), click.getX(), click.getY(), allowance)) {
				ArrayList<Integer> retVal = new ArrayList<>();
				retVal.add(myIndex);
				return retVal;
			}
		}
		return null;
	}

	private ArrayList<Point> getLinesInPolyline() {
		ArrayList<Point> pointsList = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			SV xPoint = points.get(i).get("x");
			SV yPoint = points.get(i).get("y");
			pointsList.add(new Point(xPoint.getLong(), yPoint.getLong()));
		}
		return pointsList;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[points.size()];
		for (int i = 0; i < points.size(); i++) {
			retArray[i] = new Point(points.get(i).get("x").getDouble() - 1,
					points.get(i).get("y").getDouble() - 1);
		}

		return retArray;
	}

	@Override
	public void setBackgroundColor(SO newColor) {
		this.color = newColor;
	}

	@Override
	public Root getPanel() {
		SParented parent = myParent();
		while(!(parent instanceof Interactable)){
			parent = parent.myParent();
		}
		Interactable InteractableParent = (Interactable)parent;
		return InteractableParent.getPanel();
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
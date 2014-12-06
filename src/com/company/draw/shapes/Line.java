package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Line extends SOReflect implements Drawable, Selectable, Interactable {

	public double x1;
	public double y1;
	public double x2;
	public double y2;
	public double thickness;
	public SO color;


	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (color != null) {
			Double red = color.get("r").getDouble();
			Double green = color.get("g").getDouble();
			Double blue = color.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
		}
		if (thickness > 0) {
			Stroke stroke = new BasicStroke((int) thickness);
			g2.setStroke(stroke);
		} else {
			Stroke stroke = new BasicStroke(1);
			g2.setStroke(stroke);
		}
		g2.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);
		double allowance = (thickness*transform.getScaleX())/4 + (thickness*transform.getScaleY())/4 + 3;

		if (SelectUtils.checkIfInLine(x1, x2, y1, y2, ptDst.getX(), ptDst.getY(), allowance)) {
			ArrayList<Integer> retVal = new ArrayList<>();
			retVal.add(myIndex);
			return retVal;
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[2];
		retArray[0] = new Point(this.x1 - 2, this.y1 - 2);
		retArray[1] = new Point(this.x2 - 2, this.y2 - 2);
		return retArray;
	}

	@Override
	public void setBackgroundColor(SO newColor) {
		this.color = newColor;
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
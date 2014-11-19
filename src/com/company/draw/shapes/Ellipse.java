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

	public Ellipse(){}

	public Ellipse(double left, double top, double width, double height) {
		this.thickness = 0;
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
	}


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
	//		Draw fill
			g2.fillOval((int) left, (int) top, (int) width, (int) height);
		}
//		Draw fill
		g2.fillOval((int) left, (int) top, (int) width, (int) height);

		if (border != null) {
			Double red = border.get("r").getDouble();
			Double green = border.get("g").getDouble();
			Double blue = border.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
	//		Draw border
			g2.drawOval((int) left, (int) top, (int) width, (int) height);
		}
//		Draw border
		g2.drawOval((int) left, (int) top, (int) width, (int) height);
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);

		boolean isInEllipse = SelectUtils.checkIfInOvalShape(this, ptDst.getX(), ptDst.getY());

		if (isInEllipse) {
			ArrayList<Integer> arrayList = new ArrayList<>();
			arrayList.add(myIndex);
			return arrayList;
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[4];
		retArray[0] = new Point(this.left - 3, this.top - 2);
		retArray[1] = new Point(this.left + this.width, this.top - 2);
		retArray[2] = new Point(this.left - 3, this.top + this.height);
		retArray[3] = new Point(this.left + this.width, this.top + this.height);
		return retArray;
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
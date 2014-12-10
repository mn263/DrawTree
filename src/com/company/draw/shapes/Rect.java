package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Rect extends SOReflect implements Drawable, Selectable, Interactable {

	public double left;
	public double top;
	public double width;
	public double height;
	public double thickness;
	public SO border;
	public SO fill;

	public Rect(){}
	public Rect(double left, double top, double width, double height, double thickness, SO fill) {
		this.left = left;
		this.top = top;
		this.width = width;
		this.height = height;
		this.thickness = thickness;
		this.fill = fill;
	}

	public void setTop(double val) {
		this.top = val;
	}
	public void setLeft(double val) {
		this.left = val;
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
			g2.fillRect((int) left, (int) top, (int) width, (int) height);
		}
		if (border != null) {
			Double red = border.get("r").getDouble();
			Double green = border.get("g").getDouble();
			Double blue = border.get("b").getDouble();
			Color lineColor = new Color(red.intValue(), green.intValue(), blue.intValue());
			g2.setColor(lineColor);
			//		Draw border
			g2.drawRect((int) left, (int) top, (int) width, (int) height);
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);

		ArrayList<Integer> arrayList = SelectUtils.checkIfInBoxShape(this, ptDst.getX(), ptDst.getY());
		if (arrayList != null) {
			arrayList.add(myIndex);
		}
		return arrayList;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[4];
		retArray[0] = new Point(this.left - 3, this.top - 2);
		retArray[1] = new Point(this.left + this.width + 1, this.top - 2);
		retArray[2] = new Point(this.left - 3, this.top + this.height);
		retArray[3] = new Point(this.left + this.width + 1, this.top + this.height);
		return retArray;
	}

	@Override
	public void setBackgroundColor(SO newColor) {
		this.fill = newColor;
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
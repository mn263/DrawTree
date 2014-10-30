package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Text extends SOReflect implements Drawable, Selectable, Interactable {

	public String text;
	public double x;
	public double y;
	public String font;
	public double size;
	public boolean edit;
	public double cursor;

	@Override
	public void paint(Graphics g) {
		Font gFont = new Font(font, Font.PLAIN, (int) size);
		g.setFont(gFont);
		if (text != null) {
			g.drawString(text, (int) x, (int) y);
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {

		Point2D ptSrc = new Point(x, y);
		Point2D ptDst = transform.transform(ptSrc, null);

		ArrayList<Integer> arrayList = SelectUtils.checkIfInText(this, ptDst.getX(), ptDst.getY());
		if (arrayList != null) {
			arrayList.add(myIndex);
		}
		return arrayList;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[4];
		retArray[0] = new Point(x, y - size);
		retArray[1] = new Point(x + (size * text.length()), y - size);
		retArray[2] = new Point(x, y);
		retArray[3] = new Point(x + (size * text.length()), y);
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
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		throw new NotImplementedException();
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		throw new NotImplementedException();
	}
}
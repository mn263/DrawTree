package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;

public class Group extends SOReflect implements Drawable, Selectable, Interactable, Layout {
	public SA contents;
	public double sx;
	public double sy;
	public double rotate;
	public double tx;
	public double ty;
	public double width;
	public double height;
	public double columnSpan;
	public SV model;

	public Group(){}
	public Group(SA contents, double sx, double sy, double tx, double ty, double rotate, double width, double height) {
		this.contents = contents;
		this.sx	= sx;
		this.sy = sy;
		this.tx = tx;
		this.ty = ty;
		this.rotate = rotate;
		this.width = width;
		this.height = height;
	}

	@Override
	public void paint(Graphics g) {
		int cSize = contents.size();
//		The original and next we transform and repaint
		Graphics2D g2 = (Graphics2D) g;
//		Perform Transformations
		AffineTransform transform = g2.getTransform();
		WidgetUtils.transformGraphics(g2, tx, ty, sx, sy, rotate);
//		Call Draw on all contained objects
		for (int i = 0; i < cSize; i++) {
			callPaintOnContents(contents.get(i), g2);
		}
		g2.setTransform(transform);
	}

	public void callPaintOnContents(SV sv, Graphics g) {
		SO so = sv.getSO();
		Drawable drawable = (Drawable) so;
		drawable.paint(g);
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform oldTrans) {
		AffineTransform transform = WidgetUtils.getTransform(tx, ty, sx, sy, rotate);
		// Add on old transform
		transform.concatenate(oldTrans);

		for (int i = contents.size() - 1; i >= 0; i--) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (!(so instanceof Selectable)) continue;
			Selectable selectable = (Selectable) so;
			ArrayList<Integer> path = selectable.select(x, y, i, transform);
			if (path != null) {
				path.add(myIndex);
				return path;
			}
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		return new Point2D[0];
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
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.MOVE, x, y, myTransform);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.UP, x, y, myTransform);
	}

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform oldTrans) {
		AffineTransform newTransform = getTransform(tx, ty, sx, sy, rotate);
		// Add on old transform
		newTransform.concatenate(oldTrans);
		return handleMouse(contents, x, y, newTransform, mouseType);
	}


//	LAYOUT

	@Override
	public double getColSpan() {
		return columnSpan;
	}

	@Override
	public double getMinWidth() {
		return this.width;
	}

	@Override
	public double getDesiredWidth() {
		return this.width;
	}

	@Override
	public double getMaxWidth() {
		return 10000000;
	}

	@Override
	public double getMinHeight() {
		return this.height;
	}

	@Override
	public double getDesiredHeight() {
		return this.height;
	}

	@Override
	public double getMaxHeight() {
		return 10000000;
	}

	@Override
	public void setHBounds(double left, double right) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (so instanceof Layout) {
				Layout layout = (Layout) so;
				layout.setHBounds(left + tx, right);
			}
		}
		this.width = right - left;
	}

	@Override
	public void setVBounds(double top, double bottom) {
		this.height = top - bottom;
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (so instanceof Layout) {
				Layout layout = (Layout) so;
				layout.setVBounds(top + ty, bottom - ty);
			}
		}
	}
}
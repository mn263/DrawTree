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

public class Group extends SOReflect implements Drawable, Selectable, Interactable {

	public SA contents;
	public double sx;
	public double sy;
	public double rotate;
	public double tx;
	public double ty;

	public Group(){}

	public Group(SA contents, double sx, double sy, double tx, double ty, double rotate) {
		this.contents = contents;
		this.sx	= sx;
		this.sy = sy;
		this.tx = tx;
		this.ty = ty;
		this.rotate = rotate;
	}

	@Override
	public void paint(Graphics g) {
		int cSize = contents.size();
//		The original and next we transform and repaint
		Graphics2D g2 = (Graphics2D) g;
//		Perform Transformations
		if (sx != 0) g2.scale(sx, sy);
		g2.rotate(-Math.toRadians(rotate));
		g2.translate((int) tx, (int) ty);

//		Call Draw on all contained objects
		for (int i = 0; i < cSize; i++) {
			callPaintOnContents(contents.get(i), g2);
		}

//		Revert Transformations
		g2.translate((int) -tx, (int) -ty);
		g2.rotate(Math.toRadians(rotate));
		if (sx != 0) g2.scale(1 / sx, 1 / sy);
	}

	public void callPaintOnContents(SV sv, Graphics g) {
		SO so = sv.getSO();
		Drawable drawable = (Drawable) so;
		drawable.paint(g);
	}


	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform oldTrans) {
		AffineTransform transform = new AffineTransform();
		transform.translate((int) -tx, (int) -ty);
		transform.rotate(-Math.toRadians(rotate));
		transform.scale(1 / sx, 1 / sy);
		// Add on old transform
		transform.concatenate(oldTrans);

		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
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
		throw new UnsupportedOperationException("This method is not implemented");
//		return new Point2D[0];
	}

	@Override
	public String getClassStatus() {
		throw new NotImplementedException();
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
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Interactable interactable = (Interactable) so;
			if (interactable.mouseDown(x, y, myTransform)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Interactable interactable = (Interactable) so;
			if(interactable.mouseMove(x, y, myTransform)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Interactable interactable = (Interactable) so;
			if(interactable.mouseUp(x, y, myTransform)) {
				return true;
			}
		}
		return false;
	}
}
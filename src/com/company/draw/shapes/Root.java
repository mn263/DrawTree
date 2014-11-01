package com.company.draw.shapes;

import com.company.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;

public class Root extends SOReflect implements Interactable, Drawable {

	public SV model;

	public SA contents;
	public double sx;
	public double sy;
	public double rotate;
	public double tx;
	public double ty;


	public Root() {}
	public Root(SV model) {
		this.model = model;
	}

	public Interactable focus = null;

	// When this method is called on a Root object it saves the pointer to the focus object,
	// but not as a SPARK attribute.
	// Whenever the Root receives a key() event it will call key() on the focus object if there is one.
	public void setKeyFocus(Interactable focus) {
		this.focus = focus;
//		this.focus.key();
	}

	public void releaseKeyFocus() { // Sets the key focus to null.
		this.focus = null;
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
		System.out.println(so.getClass());
		if (!so.getClass().toString().equals("class com.company.draw.shapes.Button")) {
			Drawable drawable = (Drawable) so;
			drawable.paint(g);
		}
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
		System.out.println(this.model);
		throw new NotImplementedException();
	}

	@Override
	public boolean key(char key) {
		throw new NotImplementedException();
	}

	@Override
	public Root getPanel() {
		return this;
	}
}

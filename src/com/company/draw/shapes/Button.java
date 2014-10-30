package com.company.draw.shapes;

import com.company.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.StaticUtils.*;
import static com.company.draw.shapes.StaticUtils.mouseType.UP;

public class Button extends SOReflect implements Drawable, Interactable {

	public String label;
	public SA contents;
	public String state;
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;
	public double value;

	public boolean mouseIsDown = false;


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
		mouseIsDown = true;
		boolean wasHandled = handleMouseEvent(x, y, myTransform);
		return wasHandled || callHandleMouse(mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		boolean wasHandled = handleMouseEvent(x, y, myTransform);
		return wasHandled || callHandleMouse(mouseType.MOVE, x, y, myTransform);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
//		TODO:
		// If a mouseUp() occurs while the button is in the
		// active state then the model attribute should be used
		// as a path from the Root's model to identify an
		// attribute to be changed. The model attribute
		// should be changed to the value of the Button's
		// value attribute.

		if (this.state.equals("active")) {
			StaticUtils.buttonPressed(this.model);
		}

		mouseIsDown = false;
		boolean wasHandled = handleMouseEvent(x, y, myTransform);
		return wasHandled || callHandleMouse(UP, x, y, myTransform);
	}

	private boolean handleMouseEvent(double x, double y, AffineTransform myTransform) {
		if (mouseIsOver(x, y, myTransform)) {
			if (this.mouseIsDown) {
				changeState(this.active);
			} else {
				changeState(this.hover);
			}
			return true;
		} else {
			changeState(this.idle);
			return false;
		}
	}

	private boolean mouseIsOver(double x, double y, AffineTransform myTransform) {
		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			Selectable selectable = (Selectable) so;
			ArrayList<Integer> path = selectable.select(x, y, i, myTransform);
			if (path != null) {
				return true;
			}
		}
		return false;
	}

	private boolean callHandleMouse(StaticUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
		} else {
			if (mouseIsDown) {
				this.state = "active";
			} else {
				this.state = "hover";
			}
		}
		return isHandled;
	}

	public void changeState(SO newState) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Selectable selectable = (Selectable) so;
			if (so.get("class") != null && "active".equals(so.get("class").toString())) {
				selectable.setBackgroundColor(newState);
			}
			if (so.get("class") != null && "label".equals(so.get("class").toString())) {
				if (selectable.getClass().toString().equals("class com.company.draw.shapes.Text")) {
					Text text = (Text) selectable;
					text.text = this.label;
				}
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		int cSize = contents.size();
		Graphics2D g2 = (Graphics2D) g;
		for (int i = 0; i < cSize; i++) {
			callPaintOnContents(contents.get(i), g2);
		}
	}

	public void callPaintOnContents(SV sv, Graphics g) {
		SO so = sv.getSO();
		Drawable drawable = (Drawable) so;
		drawable.paint(g);
	}
}

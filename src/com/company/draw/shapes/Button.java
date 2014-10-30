package com.company.draw.shapes;

import com.company.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;

import static com.company.draw.shapes.WidgetUtils.*;
import static com.company.draw.shapes.WidgetUtils.mouseType.*;

public class Button extends SOReflect implements Drawable, Interactable {

	public String label;
	public SA contents;
	public SA model;
	public String state;
	public SO idle;
	public SO hover;
	public SO active;
	public String value;


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
		return callHandleMouse(mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(mouseType.MOVE, x, y, myTransform);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		if (this.state.equals("active")) {
			WidgetUtils.activeBtnSelected(this.model, this.value);
		}

		return callHandleMouse(UP, x, y, myTransform);
	}


	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
			changeState(this.idle, mouseType);
		} else {
			if (WidgetUtils.mouseStatus == MouseStatus.PRESSED) {
				this.state = "active";
				changeState(this.active, mouseType);
			} else {
				this.state = "hover";
				changeState(this.hover, mouseType);
			}
		}
		return isHandled;
	}

	public void changeState(SO newState, WidgetUtils.mouseType mouseType) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Selectable selectable = (Selectable) so;
			if (so.get("class") != null && "\"active\"".equals(so.get("class").toString())) {
				selectable.setBackgroundColor(newState);
			}
			if (so.get("class") != null && "\"label\"".equals(so.get("class").toString())
					&& mouseType == UP && !this.state.equals("idle")) {
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

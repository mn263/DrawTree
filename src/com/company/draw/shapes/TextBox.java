package com.company.draw.shapes;

import com.company.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.handleMouse;
import static com.company.draw.shapes.WidgetUtils.mouseType.UP;

public class TextBox extends SOReflect implements ModelListener, Drawable, Interactable {

	public String state;
	public SA contents;
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;

	private Text content = null;

	public TextBox() {
		WidgetUtils.addListener(this);
	}

	//	INTERACTABLE
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
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.MOVE, x, y, myTransform);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(UP, x, y, myTransform);
	}

	@Override
	public void makeIdle() {
		changeState(this.idle);
	}

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		if (content == null) loadContentText();
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
			changeState(this.idle);
		} else {
			if (WidgetUtils.getMouseStatus() == WidgetUtils.MouseStatus.PRESSED) {
				this.state = "active";
				changeState(this.active);
				if (content.select(x, y, 0, myTransform) != null) {
					content.mouseDown(x, y, myTransform);
				} else {
					content.setCursor(x, y, myTransform);
				}
			} else {
				this.state = "hover";
				changeState(this.hover);
			}
		}
		return isHandled;
	}

	private void loadContentText() {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Selectable selectable = (Selectable) so;
			if (so.get("class") != null && "\"content\"".equals(so.get("class").toString())) {
				if (selectable instanceof Text) {
					this.content = (Text) selectable;
				}
			}
		}
	}

	public void changeState(SO newState) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Selectable selectable = (Selectable) so;
			if (so.get("class") != null && "\"active\"".equals(so.get("class").toString())) {
				selectable.setBackgroundColor(newState);
			}
		}
	}

	//	DRAWABLE
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


	// MODEL LISTENER
	@Override
	public void modelUpdated(ArrayList<String> modelPath, String newValue) {
		if(content == null) loadContentText();

		if (modelPath.size() == model.size()) {
			for (int i = 0; i < model.size(); i++) {
				if (!modelPath.get(i).equals(model.getString(i))) {
					return; //IT WASN'T A MATCH
				}
			}
			content.text = newValue;
		}
	}
}
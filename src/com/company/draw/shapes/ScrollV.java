package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;
import static com.company.draw.shapes.WidgetUtils.mouseType.*;

public class ScrollV extends SOReflect implements ModelListener, Drawable, Interactable {

	public String state;
	public SA contents;
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;
	public double max;
	public double min;
	public double step;
	private Point sliderLast;

	public ScrollV() {
		WidgetUtils.addListener(this);
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
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		if (WidgetUtils.sliderBeingUsed) {
			moveSlider(y);
			return true;
		} else {
			sliderLast = null;
			return callHandleMouse(WidgetUtils.mouseType.MOVE, x, y, myTransform);
		}
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
//		if (this.state.equals("active")) {
//			StaticUtils.updateModel(this.model, this.value);
//		}
		return callHandleMouse(UP, x, y, myTransform);
	}


	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
			changeState(this.idle, x, y, myTransform, mouseType);
		} else {
			if (WidgetUtils.getMouseStatus() == WidgetUtils.MouseStatus.PRESSED) {
				this.state = "active";
				changeState(this.active, x, y, myTransform, mouseType);
			} else {
				this.state = "hover";
				changeState(this.hover, x, y, myTransform, mouseType);
			}
		}
		return isHandled;
	}

	public void changeState(SO newState, double x, double y, AffineTransform myTransform, WidgetUtils.mouseType mouseType) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			Selectable selectable = (Selectable) so;
			//UPDATE THE COLOR
			if (so.get("class") != null && "\"active\"".equals(so.get("class").toString())) {
				selectable.setBackgroundColor(newState);
			}
			if (mouseType == UP) { //MOVE SCROLL BAR IF "RELEASED" IS PRESSED
				if (so.get("class") != null && "\"up\"".equals(so.get("class").toString())) {
					if (selectable.select(x, y, 0, myTransform) != null) {
						moveBar(-step);
					}
				} //MOVE SCROLL BAR IF "DOWN" IS PRESSED
				if (so.get("class") != null && "\"down\"".equals(so.get("class").toString())) {
					if (selectable.select(x, y, 0, myTransform) != null) {
						moveBar(step);
					}
				}
			}
			if (mouseType == DOWN) {
			//MOVE SCROLL BAR IF "SLIDER" IS PRESSED
				if (so.get("class") != null && "\"slide\"".equals(so.get("class").toString())) {
					if (selectable.select(x, y, 0, myTransform) != null) {
						this.sliderLast = new Point(x, y);
						WidgetUtils.sliderBeingUsed = true;
					}
				}
			}
		}
	}

	private void moveBar(double step) {
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			if (so.get("class") != null && "\"slide\"".equals(so.get("class").toString())) {
				Rect slide = (Rect) so;
				double newValue = slide.top + step;
				if (newValue < min) {
					slide.setTop(min);
				} else if (newValue > max) {
					slide.setTop(max);
				} else {
					slide.setTop(newValue);
				}
			}
		}
	}

	private void moveSlider(double y) {
		if (y == sliderLast.getY()) {
			return; //NO NEED TO UPDATE IF IT IS THE SAME
		}
		for (int i = 0; i < contents.size(); i++) {
			SO so = contents.get(i).getSO();
			if (so.get("class") != null && "\"slide\"".equals(so.get("class").toString())) {
				Rect slide = (Rect) so;
				double diff = y - sliderLast.getY();
				double newValue = slide.top + diff;
				if (newValue < min) {
					slide.setTop(min);
					sliderLast = new Point(0, min);
					WidgetUtils.updateModel(model, String.valueOf(min));
				} else if (newValue > max) {
					sliderLast = new Point(0, max);
					slide.setTop(max);
					WidgetUtils.updateModel(model, String.valueOf(max));
				} else {
					slide.setTop(newValue);
					sliderLast = new Point(0, newValue);
					WidgetUtils.updateModel(model, String.valueOf(newValue));
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

	@Override
	public void modelUpdated(ArrayList<String> modelPath, String newValue) {
		if (modelPath.size() == model.size()) {
			for (int i = 0; i < model.size(); i++) {
				if(!modelPath.get(i).equals(model.getString(i))) {
					return; //IT WASN'T A MATCH
				}
			}
			moveSlider(Double.valueOf(newValue)); //IT WAS A MATCH SO UPDATE THE LABEL
		}
	}
}

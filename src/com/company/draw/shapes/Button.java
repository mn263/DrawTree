package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import com.company.draw.*;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;
import static com.company.draw.shapes.WidgetUtils.mouseType.*;

public class Button extends SOReflect implements Layout, Drawable, Interactable {

//	The content list is generated programatically based on the dimensions specified in setHBounds() and setVBounds()
//	public SA contents;
	public ArrayList<Drawable> contents = new ArrayList<>();
	public String label;
	public SA model;
	public String state;
	public SO idle;
	public SO hover;
	public SO active;
	public String value;



	private Ellipse ellipse = null;
	private Text text = null;
	private final double BEVEL = 3;
	private final Point MARGIN = new Point(3, 3);

	public Button() { }
	public Button(String label, SA model, String state, SO idle, SO hover, SO active, String value) {
		this.label = label;
		this.model = model;
		this.state = state;
		this.idle = idle;
		this.hover = hover;
		this.active = active;
		this.value = value;
//		this.contents.a
		// TODO: figure out how to add Ellipse and Text to the contents
	}

	// INTERACTABLE
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
			WidgetUtils.updateModel(this.model, this.value);
		}

		return callHandleMouse(UP, x, y, myTransform);
	}


	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
			changeState(this.idle);
		} else {
			if (WidgetUtils.getMouseStatus() == MouseStatus.PRESSED) {
				this.state = "active";
				changeState(this.active);
			} else {
				this.state = "hover";
				changeState(this.hover);
			}
		}
		return isHandled;
	}

	public void changeState(SO newState) {
		this.ellipse.setBackgroundColor(newState);
		this.text.text = this.label;
	}

	// DRAWABLE
	@Override
	public void paint(Graphics g) {
		if(this.text == null) initializeContents(g);
		for (Drawable drawable : contents) {
			drawable.paint(g);
		}
	}

	//	LAYOUT
//	TODO: read pg. 114 and redo these methods accordingly
	private void initializeContents(Graphics g) {
		int ellipseWidth = 40;
		int ellipseHeight = 40;
		this.text = new Text(this.label, 0, ellipseHeight/2, "sans-serif", 24, false, -1);
		this.text.setFontMetrics(g);
		this.text.adjustFontSize(this.label, 0, ellipseWidth, ellipseHeight);
		this.ellipse = new Ellipse(0, 0, ellipseWidth, ellipseHeight);
		this.ellipse.setBackgroundColor(this.idle);
		this.contents.add(ellipse);
		this.contents.add(text);
	}

//	This should pick a default font size and then report

	// min size that will create a button of that size based on the contents of the label attribute
	@Override
	public double getMinWidth() {
//		return this.text.getTextWidth() * 1.3;
		return (BEVEL*2) + (text.getTextWidth());
	}

	@Override
	public double getMinHeight() {
//		return this.text.getFontMetrics().getHeight() * 1.3;
		return (BEVEL*2) + (text.getFontMetrics().getHeight());
	}

	// desired size that will create a button of that size based on the contents of the label attribute
	@Override
	public double getDesiredWidth() {
//		return this.text.getTextWidth() * 1.6;
		return (BEVEL*2) + (MARGIN.getWidth()*2) + (text.getTextWidth());
	}

	@Override
	public double getDesiredHeight() {
//		return this.text.getFontMetrics().getHeight() * 1.6;
		return (BEVEL*2) + (MARGIN.getHeight()*2) + (text.getFontMetrics().getHeight());
	}

	// max size that will create a button of that size based on the contents of the label attribute
	@Override
	public double getMaxWidth() {
//		return this.text.getTextWidth() * 2.0;
		return getDesiredWidth();
	}

	@Override
	public double getMaxHeight() {
//		return this.text.getFontMetrics().getHeight() * 2.0;
		return getDesiredHeight();
	}


	//	When the sizes are actually received via setHBounds() and setVBounds() you should adapt the font size so that the label, border and other visuals will fit in the bounds.
	@Override
	public void setHBounds(double left, double right) {
		ellipse.left = left;
		ellipse.width = right - left;
		ellipse.setBackgroundColor(this.idle);
		text.adjustFontSize(this.label, left, (left - right), -1);
	}


	@Override
	public void setVBounds(double top, double bottom) {
		ellipse.top = top;
		ellipse.height = bottom - top;
		ellipse.setBackgroundColor(this.idle);
		text.adjustFontSize(this.label, ellipse.left, -1, bottom - top);
	}
}

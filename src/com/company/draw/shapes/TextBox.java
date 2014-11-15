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

public class TextBox extends SOReflect implements Layout, ModelListener, Drawable, Interactable {

	public String state;
	public ArrayList<Drawable> contents = new ArrayList<>();
//	public SA contents;
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;
	public double columnSpan;


	private Rect rect = null;
	private Text text = null;
	private final double BEVEL = 3;
	private final Point MARGIN = new Point(3, 3);

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

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		if (content == null) loadContentText();
		boolean isHandled = handleMouse(contents, x, y, myTransform, mouseType);
		if (!isHandled) {
			this.state = "idle";
			changeState(this.idle, mouseType);
		} else {
			if (WidgetUtils.getMouseStatus() == WidgetUtils.MouseStatus.PRESSED) {
				this.state = "active";
				changeState(this.active, mouseType);
				if (content.select(x, y, 0, myTransform) != null) {
					content.mouseDown(x, y, myTransform);
				}
			} else {
				this.state = "hover";
				changeState(this.hover, mouseType);
			}
		}
		return isHandled;
	}

	private void loadContentText() {
		this.content = this.text;
	}

	public void changeState(SO newState, WidgetUtils.mouseType mouseType) {
		if (this.rect == null) return;
		this.rect.setBackgroundColor(newState);
	}

//	DRAWABLE
	@Override
	public void paint(Graphics g) {
		if (rect == null || text == null) return;
		rect.paint(g);
		text.paint(g);
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


	//	LAYOUT
//	This implements Layout and operates as before. Its contents are generated programmatically based on the
// bounds settings. The width values should depend upon a string of "W" characters that as the length of desiredChars.
// To this you add any of the other text box borders etc. The height values should be based on standard font ranges.
// Once the bounds are set, then adjust everything to fit as you did with the Button.

	private void initializeContents() {
		this.text = new Text("", 0, 20, "sans-serif", 36, true, -1);
		Graphics g = WidgetUtils.graphics;
		this.text.setFontMetrics(g);
		this.text.adjustFontWidth("", 0, 20);
		this.text.adjustFontHeight(0, 20);
		this.rect = new Rect(0, 0, 20, 20, 4, getFill(100, 100, 0));
		this.rect.setBackgroundColor(this.idle);
		this.contents.add(rect);
		this.contents.add(text);
	}

	private SO getFill(double red, double green, double blue) {
		SO fill = new SObj();
		fill.set("r", new SV(red));
		fill.set("g", new SV(green));
		fill.set("b", new SV(blue));
		return fill;
	}


	@Override
	public double getColSpan() {
		return columnSpan;
	}

	@Override
	public double getMinWidth() {
		return (BEVEL*2) + (getText().getTextWidth());
	}

	@Override
	public double getMinHeight() {
		return (BEVEL*2) + (getText().getFontMetrics().getHeight());
	}

	@Override
	public double getDesiredWidth() {
		return (BEVEL*2) + (MARGIN.getWidth()*2) + (getText().getTextWidth());
	}

	@Override
	public double getDesiredHeight() {
		return (BEVEL*2) + (MARGIN.getHeight()*2) + (getText().getFontMetrics().getHeight());
	}

	@Override
	public double getMaxWidth() {
		return getDesiredWidth();
	}

	@Override
	public double getMaxHeight() {
		return getDesiredHeight();
	}

	@Override
	public void setHBounds(double left, double right) {
		if(this.text == null) initializeContents();
		rect.left = left;
		rect.width = right - left;
		rect.setBackgroundColor(this.idle);
		text.adjustFontWidth("", left, rect.width);
	}

	@Override
	public void setVBounds(double top, double bottom) {
		if(this.text == null) initializeContents();
		rect.top = top;
		rect.height = bottom - top;
		rect.setBackgroundColor(this.idle);
		text.adjustFontHeight(top, rect.height);
	}

	private Text getText() {
		if(this.text == null) initializeContents();
		return text;
	}
}

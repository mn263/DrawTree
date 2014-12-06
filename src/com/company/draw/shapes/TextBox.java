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
	public SO idle;
	public SO hover;
	public SO active;
	public SA model;
	public double desiredChars;

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
	public void makeIdle() { }


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
		this.content = getText();
	}

	public void changeState(SO newState) {
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
		if (content == null) loadContentText();

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
		this.text = new Text("", 0, 20, "sans-serif", true, -1);
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
		String wString = "";
		for (int i = 0; i < desiredChars; i++) {
			wString += "W";
		}
		return getText().getMinWidth(wString);
	}

	@Override
	public double getDesiredWidth() {
		String wString = "";
		for (int i = 0; i < desiredChars; i++) {
			wString += "W";
		}
		int stringWidth = text.getFontMetrics().stringWidth(wString);
		return stringWidth + 10 * 2;
	}

	@Override
	public double getMaxWidth() {
		return getDesiredWidth();
	}

	@Override
	public double getMinHeight() {
		return (BEVEL * 2) + (getText().getMinHeight());
	}

	@Override
	public double getDesiredHeight() {
		return (BEVEL * 2) + (MARGIN.getHeight() * 2) + (getText().getFontMetrics().getHeight());
	}

	@Override
	public double getMaxHeight() {
		return getDesiredHeight();
	}


	@Override
	public void setHBounds(double left, double right) {
		rect.left = left;
		rect.width = right - left;
		rect.setBackgroundColor(this.idle);
		text.x = left;
		text.adjustFontWidth("", rect.width);
	}

	@Override
	public void setVBounds(double top, double bottom) {
		rect.top = top;
		rect.height = bottom - top;
		rect.setBackgroundColor(this.idle);
		text.adjustFontHeight(top, rect.height);
	}

	private Text getText() {
		if (this.text == null) initializeContents();
		return text;
	}
}

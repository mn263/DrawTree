package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;

public class Root extends Group {

	public Interactable focus = null;

	private int width = -1;

	public void setKeyFocus(Interactable focus) {
		this.focus = focus;
	}

	public void releaseKeyFocus() { // Sets the key focus to null.
		if (this.focus != null) {
			Text text = (Text) focus;
			text.releaseFocus();
		}
		this.focus = null;
	}

	public void updateRoot(SO modelObjects, ArrayList<String> path, String value) {
		if (path.size() == 1) {
			try {
				modelObjects.get(path.get(0)).getDouble();
				modelObjects.set(path.get(0), Double.valueOf(value));
			} catch (Exception e) {
				modelObjects.set(path.get(0), value);
			}
			return;
		}
		modelObjects = modelObjects.get(path.get(0)).getSO();
		path.remove(0);
		updateRoot(modelObjects, path, value);
	}

	public void updateModel(ArrayList<String> path, String value) {
		ArrayList<String> copyPath = new ArrayList<>(path);
		updateRoot(model.getSO(), copyPath, value);
		WidgetUtils.updateModListeners(path, value);
	}

	@Override
	public void paint(Graphics g) {
		if (width == -1) { //because contents aren't initialized yet
			WidgetUtils.graphics = g;
			setHBounds(tx, 600 - tx);
			setVBounds(ty, 600 - ty);
			width = (int) (600 - tx);
		}

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

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		AffineTransform newTransform = getTransform(tx, ty, sx, sy, rotate);
		newTransform.concatenate(myTransform);
		return handleMouse(contents, x, y, newTransform, mouseType);
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		boolean handeled = callHandleMouse(mouseType.UP, x, y, myTransform);

		SO modelObjects = this.model.getSO();
		String[] modelAttrs = modelObjects.attributes();
		for (String attr : modelAttrs) {
			System.out.println(attr + " -> " + modelObjects.get(attr));
		}

		return handeled;
	}

	@Override
	public boolean key(char key) {
		return this.focus != null && this.focus.key(key);
	}

	@Override
	public Root getPanel() {
		return this;
	}


	Layout onlyChild = null;

	//	LAYOUT
	private Layout getOnlyChild() {
		if (onlyChild == null) {
			SV sv = contents.get(0);
			SO so = sv.getSO();
			onlyChild = (Layout) so;
		}
		return onlyChild;
	}

	@Override
	public double getMinWidth() {
		Layout child = getOnlyChild();
		return child.getMinWidth();
	}

	@Override
	public double getDesiredWidth() {
		return getOnlyChild().getDesiredWidth();
	}

	@Override
	public double getMaxWidth() {
		return getOnlyChild().getMaxWidth();
	}

	@Override
	public void setHBounds(double left, double right) {
		getOnlyChild().setHBounds(left, right);
	}

	@Override
	public double getMinHeight() {
		return getOnlyChild().getMinHeight();
	}

	@Override
	public double getDesiredHeight() {
		return getOnlyChild().getDesiredHeight();
	}

	@Override
	public double getMaxHeight() {
		return getOnlyChild().getMaxHeight();
	}

	@Override
	public void setVBounds(double top, double bottom) {
		getOnlyChild().setVBounds(top, bottom);
	}

	public void handleComponentResize(ComponentEvent e) {
		width = e.getComponent().getWidth();
		int height = e.getComponent().getHeight();
		setHBounds(tx, width - tx);
		setVBounds(ty, height - ty);
	}
}

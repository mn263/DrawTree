package com.company.draw.shapes;

import com.company.*;
import com.company.draw.*;
import spark.data.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;

public class Root extends SOReflect implements Layout, Interactable, Drawable {

	public SV model;
	public SA contents;
	public double sx;
	public double sy;
	public double rotate;
	public double tx;
	public double ty;
	public double columnSpan;

	public Interactable focus = null;

	private boolean initUpdateComplete = false;


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

	private void doInitialModelUpdate(ArrayList<String> path, SV currModel) {
		this.initUpdateComplete = true;
		SO modelObjects = currModel.getSO();
		String[] modelAttrs = modelObjects.attributes();
		for (String attr : modelAttrs) {
			path.add(attr);
			if (!modelObjects.get(attr).typeName().equals("OBJECT")) {
				String objValue = modelObjects.get(attr).toString();
				objValue = objValue.replaceAll("\"", "");
				updateModel(path, objValue);
			} else {
				doInitialModelUpdate(path, modelObjects.get(attr));
			}
			path.remove(path.size() -1);
		}
	}

	@Override
	public void paint(Graphics g) {
		if (!initUpdateComplete) { //because contents aren't initialized yet
			doInitialModelUpdate(new ArrayList<String>(), this.model);
			WidgetUtils.graphics = g;
			setHBounds(tx, TreePanel.width - tx);
			setVBounds(ty, TreePanel.height - ty);
			setHBounds(tx, TreePanel.width - tx);
			setVBounds(ty, TreePanel.height - ty);
		}


//		The original and next we transform and repaint
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform transform = g2.getTransform();
		WidgetUtils.transformGraphics(g2, tx, ty, sx, sy, rotate);
		Drawable child = (Drawable) getOnlyChild();
		child.paint(g2);
		g2.setTransform(transform);
	}

	private boolean callHandleMouse(WidgetUtils.mouseType mouseType, double x, double y, AffineTransform myTransform) {
		AffineTransform newTransform = getTransform(tx, ty, sx, sy, rotate);
		newTransform.concatenate(myTransform);
		return handleMouse(contents, x, y, newTransform, mouseType);
	}

	@Override
	public boolean mouseDown(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(WidgetUtils.mouseType.DOWN, x, y, myTransform);
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return callHandleMouse(mouseType.MOVE, x, y, myTransform);
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
	public double getColSpan() {
		return columnSpan;
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
		TreePanel.width = e.getComponent().getWidth();
		TreePanel.height = e.getComponent().getHeight();
		setHBounds(tx, TreePanel.width - tx);
		setVBounds(ty, TreePanel.height - ty);
	}
}
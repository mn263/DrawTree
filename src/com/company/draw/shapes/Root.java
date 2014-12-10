package com.company.draw.shapes;

import com.company.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;

public class Root extends SOReflect implements Interactable, Drawable {

	public SV model;
	public SA contents;
	public double sx;
	public double sy;
	public double rotate;
	public double tx;
	public double ty;

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
		try {
			modelObjects = modelObjects.get(path.get(0)).getSO();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		mouseMove(0, 0, new AffineTransform());
	}

	@Override
	public void paint(Graphics g) {
		if (!initUpdateComplete) doInitialModelUpdate(new ArrayList<String>(), this.model);
		Graphics2D g2 = (Graphics2D) g;
		AffineTransform transform = g2.getTransform();
		WidgetUtils.transformGraphics(g2, tx, ty, sx, sy, rotate);
		for (int i = 0; i < contents.size(); i++) { // Call Draw on all contained objects
			callPaintOnContents(contents.get(i), g2);
		}
		g2.setTransform(transform);
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
		if (this.model != null) {
			printModel(this.model);
		}
		return handeled;
	}

	private void printModel(SV modelObject) {
		SO modelObjects = modelObject.getSO();
		String[] modelAttrs = modelObjects.attributes();
		for (String attr : modelAttrs) {
			System.out.println(attr + " -> " + modelObjects.get(attr));
			if(modelObjects.get(attr).toString().equals("{SO }")) {
				printModel(modelObjects.get(attr));
			}
		}
	}

	@Override
	public void makeIdle() { }

	@Override
	public boolean key(char key) {
		return this.focus != null && this.focus.key(key);
	}

	@Override
	public Root getPanel() {
		return this;
	}
}

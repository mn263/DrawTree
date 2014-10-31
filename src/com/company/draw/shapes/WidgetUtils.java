package com.company.draw.shapes;

import com.company.*;
import spark.data.*;

import java.awt.geom.*;
import java.util.*;

public class WidgetUtils {

	private static SwingTree swingTree;
	private static ModelListener sliderBeingUsed;
	private static ArrayList<ModelListener> modelListeners = new ArrayList<ModelListener>();
	public enum MouseStatus {RELEASED, PRESSED}
	public static enum mouseType { UP, DOWN, MOVE }
	private static MouseStatus mouseStatus = MouseStatus.RELEASED;

// MOUSE
	public static MouseStatus getMouseStatus() {
		return mouseStatus;
	}

	public static void setMouseStatus(MouseStatus mouseStatus) {
		WidgetUtils.mouseStatus = mouseStatus;
		if (mouseStatus == MouseStatus.RELEASED) {
			sliderBeingUsed = null;
		}
	}

	//	WIDGET
	public static boolean sliderBeingUsed(ModelListener scroll) {
		return sliderBeingUsed == scroll;
	}

	public static void setSliderBeingUsed(ModelListener scroll) {
		sliderBeingUsed = scroll;
	}

// LISTENER
	public static void addListener(ModelListener listener) {
		modelListeners.add(listener);
	}

	public static void updateModListeners(ArrayList<String> modelPath, String newValue) {
		for (ModelListener listener : modelListeners) {
			listener.modelUpdated(modelPath, newValue);
		}
	}

	public static void updateModel(SA model, String value) {
		ArrayList<String> path = new ArrayList<String>();
		for (int i = 0; i < model.size(); i++) {
			path.add(model.getString(i));
		}
		SwingTree.root.updateModel(null, path, value);
	}

// SWING TREE
	public static void setSwingTree(SwingTree swingTree) {
		WidgetUtils.swingTree = swingTree;
	}

	public static void repaintAll() {
		swingTree.repaint();
		swingTree.getContentPane().repaint();
	}

// WIDGET BUTTON UTILS
	public static AffineTransform getTransform(double tx, double ty, double sx, double sy, double rotate) {
		AffineTransform transform = new AffineTransform();
		transform.translate((int) -tx, (int) -ty);
		transform.rotate(-Math.toRadians(rotate));
		if (sx != 0 && sy != 0) {
			transform.scale(1 / sx, 1 / sy);
		}
		return transform;
	}

	public static boolean handleMouse(SA contents, double x, double y, AffineTransform myTransform, mouseType mouseType) {

		for (int i = 0; i < contents.size(); i++) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (so instanceof Selectable) {
				Selectable selectable = (Selectable) so;
				if (selectable.select(x, y, 0, myTransform) != null) {
					return true;
				}
			} else if (so instanceof Interactable) {
				Interactable interactable = (Interactable) so;
				boolean wasHandled = false;
				if (mouseType == WidgetUtils.mouseType.UP) {
					wasHandled = interactable.mouseUp(x, y, myTransform);
				} else if (mouseType == WidgetUtils.mouseType.DOWN) {
					wasHandled = interactable.mouseDown(x, y, myTransform);
				} else if (mouseType == WidgetUtils.mouseType.MOVE) {
					wasHandled = interactable.mouseMove(x, y, myTransform);
				} else {
					try {
						throw new Exception("mouse type must have been invalid");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (wasHandled) {
					return true;
				}
			}
		}
		return false;
	}
}

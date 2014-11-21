package com.company.draw.shapes;

import com.company.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class WidgetUtils {

	private static SwingTree swingTree;
	private static ModelListener sliderBeingUsed;
	private static ArrayList<ModelListener> modelListeners = new ArrayList<>();
	public static Graphics graphics;

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
		} else if (mouseStatus == MouseStatus.PRESSED) {
			SwingTree.getRoot().releaseKeyFocus();
		}
	}

	//	WIDGET
	public static boolean sliderBeingUsed(ModelListener scroll) {
		return sliderBeingUsed == scroll;
	}

	public static void setSliderBeingUsed(ModelListener scroll) {
		sliderBeingUsed = scroll;
	}

	public static void setRootFocus(Interactable focus) {
		if (focus == null) {
			SwingTree.getRoot().releaseKeyFocus();
		} else {
			SwingTree.getRoot().setKeyFocus(focus);
		}
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
		if (model == null) return;
		ArrayList<String> path = new ArrayList<>();
		for (int i = 0; i < model.size(); i++) {
			path.add(model.getString(i));
		}
		SwingTree.getRoot().updateModel(path, value);
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
		if (sx != 0 && sy != 0) transform.scale(1 / sx, 1 / sy);
		transform.rotate(Math.toRadians(rotate));
		transform.translate((int) -tx, (int) -ty);
		return transform;
	}

	public static AffineTransform getBackwardsTransform(double tx, double ty, double sx, double sy, double rotate) {
		AffineTransform transform = new AffineTransform();
		transform.translate((int) tx, (int) ty);
		transform.rotate(-Math.toRadians(rotate));
		if (sx != 0 && sy != 0) transform.scale(sx, sy);
		return transform;
	}

	public static void transformGraphics(Graphics2D g2, double tx, double ty, double sx, double sy, double rotate) {
		g2.translate((int) tx, (int) ty);
		g2.rotate(-Math.toRadians(rotate));
		if (sx != 0 && sy != 0) g2.scale(sx, sy);
	}

	public static boolean handleMouse(SA contents, double x, double y, AffineTransform myTransform, mouseType mouseType) {

//		for (int i = 0; i < contents.size(); i++) {
		for (int i = contents.size() - 1; i >= 0; i--) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (so instanceof Selectable && !(so instanceof Text) && !(so instanceof Group)) {
				if(isSelectable(so, x, y, myTransform)) return true;
			} else if (so instanceof Interactable) {
				if (so instanceof Text && !isSelectable(so, x, y, myTransform)) continue;

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

	public static boolean handleMouse(ArrayList<Drawable> contents, double x, double y, AffineTransform myTransform, mouseType mouseType) {
		for (Drawable drawable : contents) {
			if (drawable instanceof Selectable && !(drawable instanceof Text) && !(drawable instanceof Group)) {
				if(isSelectable(drawable, x, y, myTransform)) return true;
			} else if (drawable instanceof Interactable) {
				if (drawable instanceof Text && !isSelectable(drawable, x, y, myTransform)) return false;

				Interactable interactable = (Interactable) drawable;
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

	private static boolean isSelectable(SO so, double x, double y, AffineTransform myTransform) {
		Selectable selectable = (Selectable) so;
		return selectable.select(x, y, 0, myTransform) != null;
	}

	private static boolean isSelectable(Drawable drawable, double x, double y, AffineTransform myTransform) {
		Selectable selectable = (Selectable) drawable;
		return selectable.select(x, y, 0, myTransform) != null;
	}
}
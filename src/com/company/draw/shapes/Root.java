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

	private ArrayList<Command> undo = new ArrayList<>();
	private ArrayList<Command> redo = new ArrayList<>();

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
		addToUndo();
		setModel(path, value);
	}

	private void setModel(ArrayList<String> path, String value) {
		ArrayList<String> copyPath = new ArrayList<>(path);
		updateRoot(model.getSO(), copyPath, value);
		WidgetUtils.updateModListeners(path, value);
	}

	private void addToUndo() {
		if (undo.isEmpty()) {
			undo.add(new Command(this.model));
			return;
		}
		Command lastUndo = undo.get(undo.size() - 1);
		Command newUndo = new Command(this.model);
		if (!lastUndo.equals(newUndo)) {
			undo.add(newUndo);
		}
		redo.clear();
	}


	public void undo() {
		if(undo.isEmpty()) return;
		Command redoCommand = new Command(model);
		redo.add(redoCommand);

		Command undoneCommand = undo.get(undo.size() - 1);
		undo.remove(undo.size() - 1);

		this.model = undoneCommand.model;
		doInitialModelUpdate(new ArrayList<String>(), this.model);
	}

	public void redo() {
		if (redo.isEmpty()) return;
		undo.add(new Command(this.model));

		Command redoCommand = redo.get(redo.size() - 1);
		this.model = redoCommand.model;
		doInitialModelUpdate(new ArrayList<String>(), this.model);
		redo.remove(redo.size() - 1);
	}

	private void doInitialModelUpdate(ArrayList<String> path, SV currModel) {
		this.initUpdateComplete = true;
		if (model == null) return;
		SO modelObjects = currModel.getSO();
		String[] modelAttrs = modelObjects.attributes();
		for (String attr : modelAttrs) {
			path.add(attr);
			if (!modelObjects.get(attr).typeName().equals("OBJECT")) {
				String objValue = modelObjects.get(attr).toString();
				objValue = objValue.replaceAll("\"", "");
				setModel(path, objValue);
			} else {
				doInitialModelUpdate(path, modelObjects.get(attr));
			}
			path.remove(path.size() - 1);
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
		if (this.model != null) {
			printModel(this.model);
//			addToUndo();
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
	public boolean key(char key) {
		if(key == 26) {
			undo();
			return false;
		} else if(key == 25) {
			redo();
			return false;
		}
		return this.focus != null && this.focus.key(key);
	}


	@Override
	public void makeIdle() {}

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



	private class Command {
		public SV model;

		public Command(SV model) {
			this.model = createNewSV(model, new SV());
		}

		private SV createNewSV(SV model, SV newSV) {
			SO modelObjects = model.getSO();
			String[] modelAttrs = modelObjects.attributes();
			SO newSO = new SObj();
			for (String attr : modelAttrs) {
				if (modelObjects.get(attr).toString().equals("{SO }")) {
					SV subSV = createNewSV(modelObjects.get(attr), new SV());
					newSO.set(attr, subSV);
				} else {
					newSO.set(attr, modelObjects.get(attr));
				}
			}
			newSV.set(newSO);
			return newSV;
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof  Command)) return false;
			Command last = (Command) o;
			SV lastUndo = last.model;
			return isNotDiff(lastUndo);
		}

		private boolean isNotDiff(SV lastUndo) {
			SO lastUndoObjects = lastUndo.getSO();
			String[] lastUndoAttrs = lastUndoObjects.attributes();
			SO modelObjects = this.model.getSO();
			String[] modelAttrs = modelObjects.attributes();
			for (int i = 0; i < modelAttrs.length; i++) {
				String undoAttr = lastUndoAttrs[i];
				String attr = modelAttrs[i];
				if (modelObjects.get(attr).toString().equals("{SO }")) {
					boolean subpartChanged = isNotDiff(modelObjects.get(attr));
					if (subpartChanged) {
						return false;
					}
				} else {
					String oldValue;
					String currValue;
					try {
						double oldValueD = lastUndoObjects.get(attr).getDouble();
						double currValueD = modelObjects.get(attr).getDouble();
						oldValue = String.valueOf(oldValueD);
						currValue = String.valueOf(currValueD);
					} catch (Exception e) {
						oldValue = lastUndoObjects.get(attr).toString();
						currValue = modelObjects.get(attr).toString();
					}
					System.out.println(undoAttr + " - " + attr);
					System.out.println(oldValue + " - " + currValue);
					if (!undoAttr.equals(attr) || !(oldValue.equals(currValue))) {
						return false;
					}
				}
			}
			return true;
		}
	}
}
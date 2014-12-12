package com.company.draw.shapes;

import com.company.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Select extends Group implements Selectable {

	//When selected changes we should call repaint
	private ArrayList<Integer> selected = new ArrayList<>();
	private boolean checkedSuper = false;

	public Select() {
		super();
		System.out.print("");
	}
	public Select(Group group) {
		super(group.contents, group.sx, group.sy, group.tx, group.ty, group.rotate);
	}

	@Override
	public void paint(Graphics g) {
		if(!checkedSuper) checkedSuper();
		int cSize = contents.size();
//		The original and next we transform and repaint
		Graphics2D g2 = (Graphics2D) g;
//		Perform Transformations
		AffineTransform transform = g2.getTransform();
		WidgetUtils.transformGraphics(g2, tx, ty, sx, sy, rotate);
//		Call Draw on all contained objects
		for (int i = 0; i < cSize; i++) {
			Drawable drawable = (Drawable) contents.get(i).getSO();
			drawable.paint(g);
		}
		g2.setTransform(transform);
	}

	private void checkedSuper() {
		checkedSuper = true;
		SA currSelection = super.selected;
		if (currSelection != null && currSelection.size() > 0) {
			TreePanel.selected = this;
			for (int i = 0; i < currSelection.size(); i++) {
				String strNum = currSelection.get(i).toString();
				selected.add(Integer.valueOf(strNum));
			}
			selected.add(0); // for this select
		}
	}


	public void paintSelected(Graphics g) {
		if (selected.size() <= 0) return;
		SV sv = null;
		AffineTransform forwardTransform = WidgetUtils.getBackwardsTransform(tx, ty, sx, sy, rotate);
		for (int i = selected.size() - 2; i >= 0; i--) {
			int index = selected.get(i);
			if (sv == null) {
				sv = contents.get(index);
			} else {
				SO so = sv.getSO();
				Selectable selectable = (Selectable) so;
				if (selectable instanceof Group) {
					Group group = (Group) selectable;
					sv = group.contents.get(index);
				}
			}
			SO so = sv.getSO();
			Selectable selectable = (Selectable) so;
			if (selectable instanceof Group) {
				Group group = (Group) selectable;
				AffineTransform temp = WidgetUtils.getBackwardsTransform(group.tx, group.ty, group.sx, group.sy, group.rotate);
				forwardTransform.concatenate(temp);
			}
		}
		if (sv == null) return;
		SO so = sv.getSO();
		Selectable selectable = (Selectable) so;
		Point2D[] controlPoints = selectable.controls();

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.darkGray);
		AffineTransform gTrans = g2.getTransform();
		gTrans.concatenate(forwardTransform);

		double offY = g2.getTransform().getTranslateY();
		for (Point2D point : controlPoints) {
			Point2D ptDst = gTrans.transform(point, null);
			g2.fillRect((int) ptDst.getX(), (int) (ptDst.getY() - offY), 4, 4);
		}
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		ArrayList<Integer> path = super.select(x, y, myIndex, transform);
		if (path != null) {
			this.selected = path;
			SwingTree.treePanel.repaint();
		}
		return path;
	}

	@Override
	public Point2D[] controls() {
		return new Point2D[0];
	}

}

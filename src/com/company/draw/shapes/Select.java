package com.company.draw.shapes;

import com.company.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Select extends Group implements Selectable {

	//When selected changes we should call repaint
	private ArrayList<Integer> selected = new ArrayList<>();

	public Select(){super();}
	public Select(Group group) {
		super(group.contents, group.sx, group.sy, group.tx, group.ty, group.rotate, group.width, group.height);
	}


//	@Override
	public void paintSelected(Graphics g) {
		if (selected.size() <= 0) return;
		SV sv = null;
		AffineTransform forwardTransform = new AffineTransform();
		if (sx != 0 && sy != 0) forwardTransform.scale(sx, sy);
		forwardTransform.rotate(-Math.toRadians(rotate));
		forwardTransform.translate(tx, ty);
		AffineTransform revertTransform	= new AffineTransform();
		revertTransform.translate(-tx, -ty);
		revertTransform.rotate(Math.toRadians(rotate));
		if (sx != 0 && sy != 0) revertTransform.scale(1/sx, 1/sy);
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
				} else if (selectable instanceof Path) {
					Path path = (Path) selectable;
					sv = path.contents.get(index);
				}
			}
			SO so = sv.getSO();
			Selectable selectable = (Selectable) so;
			if (selectable instanceof Group) {
				Group group = (Group) selectable;
				AffineTransform temp = new AffineTransform();
				if (sx != 0 && sy != 0) temp.scale(group.sx, group.sy);
				temp.rotate(-Math.toRadians(group.rotate));
				temp.translate(group.tx, group.ty);
				forwardTransform.concatenate(temp);

				temp = new AffineTransform();
				temp.translate(-group.tx, -group.ty);
				temp.rotate(Math.toRadians(group.rotate));
				if (sx != 0 && sy != 0) temp.scale(1 / group.sx, 1 / group.sy);
				revertTransform.preConcatenate(temp);
			}
		}
		assert sv != null;
		SO so = sv.getSO();
		Selectable selectable = (Selectable) so;
		Point2D[] controlPoints = selectable.controls();

		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.darkGray);
		g2.transform(forwardTransform);
		for (Point2D point : controlPoints) {
			g2.drawRect((int) point.getX(), (int) point.getY(), 2, 2);
		}
		g2.transform(revertTransform);
	}

	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform transform) {
		ArrayList<Integer> path = super.select(x, y, myIndex, transform);
		if (path != null) {
			updateSelected(path);
		}
		return path;
	}

	private void updateSelected(ArrayList<Integer> path) {
		this.selected = path;
		SwingTree.treePanel.repaint();
	}

	@Override
	public Point2D[] controls() {
		return new Point2D[0];
	}

}

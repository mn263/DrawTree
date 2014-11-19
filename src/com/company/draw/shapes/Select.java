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
		super(group.contents, group.sx, group.sy, group.tx, group.ty, group.rotate);
	}


	public void paintSelected(Graphics g) {
		if (selected.size() <= 0) return;
		SV sv = null;
		AffineTransform forwardTransform = WidgetUtils.getBackwardsTransform(tx, ty, sx, sy, rotate);
		AffineTransform revertTransform	= WidgetUtils.getTransform(tx, ty, sx, sy, rotate);

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
				AffineTransform temp = WidgetUtils.getTransform(group.tx, group.ty, group.sx, group.sy, group.rotate);
				forwardTransform.concatenate(temp);

				temp = WidgetUtils.getBackwardsTransform(group.tx, group.ty, group.sx, group.sy, group.rotate);
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

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

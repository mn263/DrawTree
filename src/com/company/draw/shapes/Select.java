package com.company.draw.shapes;

import com.company.Point;
import com.company.*;
import spark.data.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class Select extends Group implements Selectable {

	//When selected changes we should call repaint
//	private ArrayList<Integer> selected = new ArrayList<Integer>();
	private Selectable selected = null;
	private AffineTransform totalTransform = null;
	public double columnSpan;

	public Select() {
		super();
	}

	public Select(Group group) {
		super(group.contents, group.sx, group.sy, group.tx, group.ty, group.rotate, group.width, group.height);
	}


	public void paintSelected(Graphics g) {
		if (selected == null || totalTransform == null) return;
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.darkGray);

		AffineTransform gTrans = g2.getTransform();
		try {
			gTrans.preConcatenate(totalTransform.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}

		Point2D[] controlPoints = selected.controls();
		double offY = g2.getTransform().getTranslateY();
		for (Point2D point : controlPoints) {
			point = new Point(point.getX(), point.getY() - offY);
			Point2D ptDst = gTrans.transform(point, null);
//			g2.fillRect((int) ptDst.getX(), (int) (ptDst.getY() - offY), 4, 4);
			g2.fillRect((int) ptDst.getX(), (int) (ptDst.getY()), 4, 4);
		}
	}


	@Override
	public ArrayList<Integer> select(double x, double y, int myIndex, AffineTransform oldTrans) {

		AffineTransform transform = WidgetUtils.getTransform(tx, ty, sx, sy, rotate);
		// Add on old transform
		transform.concatenate(oldTrans);

		for (int i = contents.size() - 1; i >= 0; i--) {
			SV sv = contents.get(i);
			SO so = sv.getSO();
			if (!(so instanceof Selectable)) continue;
			Selectable selectable = (Selectable) so;
			ArrayList<Integer> path = selectable.select(x, y, i, transform);
			if (path != null) {
				this.selected = selectable;
				this.totalTransform = transform;
				TreePanel.selected = this;
				return null;
			}
		}
		return null;
	}

	@Override
	public Point2D[] controls() {
		return new Point2D[0];
	}


	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		boolean handled = super.mouseUp(x, y, myTransform);
		if (handled) {
			this.select(x, y, 0, myTransform);
		}
		return handled;
	}

}
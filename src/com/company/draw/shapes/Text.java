package com.company.draw.shapes;

import com.company.*;
import com.company.Point;
import spark.data.*;
import sun.reflect.generics.reflectiveObjects.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;

import static com.company.draw.shapes.WidgetUtils.*;
import static java.lang.Math.max;

public class Text extends SOReflect implements Drawable, Selectable, Interactable {

	public String text;
	public double x;
	public double y;
	public String font;
	public double size;
	public boolean edit;
	public double cursor;

	private FontMetrics metrics = null;

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		Font gFont = new Font(font, Font.PLAIN, (int) size);
		g.setFont(gFont);
		if (text != null) {
			g.drawString(text, (int) x, (int) y);
		}
		this.metrics = g.getFontMetrics(gFont);
	}

	@Override
	public ArrayList<Integer> select(double mX, double mY, int myIndex, AffineTransform transform) {
		if (metrics == null) return null;

		int height = metrics.getHeight();
		int width = metrics.stringWidth(text);

		Point2D ptSrc = new Point(mX, mY);
		Point2D ptDst = transform.transform(ptSrc, null);

		boolean isSelected = (ptDst.getX() < x + width + 5) && (ptDst.getX() > x - 3) && (ptDst.getY() < y + 3) && (ptDst.getY() > y - height);
		if (isSelected) {
			ArrayList<Integer> arrayList = new ArrayList<Integer>();
			arrayList.add(myIndex);
			return arrayList;
		} else return null;
	}

	private void setCursor(double mX) {
		if(edit && cursor >= 0) {
			int width = (int) (mX - x);
			StringBuilder newText = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				char letter = text.charAt(i);
				int charWidth = metrics.charWidth(letter);
				width -= charWidth;

				if (letter != '|') newText.append(letter);

				if (width <= charWidth / 2) {
					if (width > -charWidth / 4) cursor = max(0, i + 1);
					else cursor = i;

					newText.insert((int) cursor, '|');
					setRootFocus(this);
					width = (int) Double.POSITIVE_INFINITY;
				}
			}
			text = newText.toString();
		}
	}

	private void removeCursor() {
		StringBuilder newText = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			char letter = text.charAt(i);
			if (letter != '|') {
				newText.append(letter);
			}
		}
		text = newText.toString();
	}

	public void releaseFocus() {
		this.removeCursor();
	}

	private int getCursorIndex() {
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '|') return i;
		}
		return -1;
	}

	@Override
	public Point2D[] controls() {
		Point2D[] retArray = new Point2D[4];
		retArray[0] = new Point(x, y - size);
		retArray[1] = new Point(x + (size * text.length()), y - size);
		retArray[2] = new Point(x, y);
		retArray[3] = new Point(x + (size * text.length()), y);
		return retArray;
	}

	@Override
	public void setBackgroundColor(SO newColor) {
		throw new NotImplementedException();
	}

	@Override
	public Root getPanel() {
		throw new NotImplementedException();
	}

	@Override
	public boolean key(char key) {
		int cursorIndex = getCursorIndex();
		if (edit && cursor >= 0 && cursorIndex != -1) {
			StringBuilder stringBuilder = new StringBuilder(text);
			if (KeyEvent.getExtendedKeyCodeForChar(key) == 8) {
				if (cursorIndex != 0) {
					stringBuilder.deleteCharAt(cursorIndex - 1);
				}
			} else {
				stringBuilder.insert(cursorIndex, key);
			}
			text = stringBuilder.toString();
		}
		return false;
	}

	@Override
	public boolean mouseDown(double mx, double my, AffineTransform myTransform) {
		if(!edit) return false;
		setCursor(mx);
		return true;
	}

	@Override
	public boolean mouseMove(double x, double y, AffineTransform myTransform) {
		return false;
	}

	@Override
	public boolean mouseUp(double x, double y, AffineTransform myTransform) {
		return false;
	}
}
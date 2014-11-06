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
import static java.lang.Math.*;

public class Text extends SOReflect implements Drawable, Selectable, Interactable {

	public String text;
	public double x;
	public double y;
	public String font;
	public double size;
	public boolean edit;
	public double cursor;

	private FontMetrics metrics = null;
	private Font gFont = null;

	public Text(){}

	public Text(String text, double x, double y, String font, double size, boolean edit, double cursor) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.font = font;
		this.size = size;
		this.edit = edit;
		this.cursor = cursor;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.black);
		setFontMetrics(g);
		if (text != null) {
			g.setFont(gFont);
			g.drawString(text, (int) x, (int) y + (metrics.getHeight() / 2));
		}
	}

	@Override
	public ArrayList<Integer> select(double mX, double mY, int myIndex, AffineTransform transform) {
		if (metrics == null) return null;
		if (text == null) text = "";

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

	private void setCursor(double mX, double mY, AffineTransform transform) {
		if(edit) {
			Point2D ptSrc = new Point(mX, mY);
			Point2D ptDst = transform.transform(ptSrc, null);
			mX = ptDst.getX();

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
		cursor = -1;
		text = newText.toString();
	}

	public void releaseFocus() {
		this.removeCursor();
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
		if (edit && cursor >= 0) {
			StringBuilder stringBuilder = new StringBuilder(text);
			if (KeyEvent.getExtendedKeyCodeForChar(key) == 8) {
				if (cursor != 0) {
					stringBuilder.deleteCharAt((int) cursor - 1);
					cursor--;
				}
			} else {
				stringBuilder.insert((int) cursor, key);
				cursor++;
			}
			text = stringBuilder.toString();
		}
		return false;
	}

	@Override
	public boolean mouseDown(double mx, double my, AffineTransform myTransform) {
		if(!edit) return false;
		setCursor(mx, my, myTransform);
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

	//	LAYOUT
	public void adjustFontSize(String label, double left, double width, double height) {
		this.text = label;
		if(width > 0) {
			int textWidth = getTextWidth();
			if (textWidth * 1.3 > width) {
				double oldSize = this.size;
				this.size = oldSize / (textWidth / (width / 1.6));
				centerWidth(left, width, textWidth * (this.size / oldSize));
			} else if (textWidth * 3.0 < width) {
				this.size = this.size / (textWidth / (width / 1.6));
				centerWidth(left, width, textWidth * (width / 1.6));
			}
		}
		if (height > 0) {
			int textHeight = this.metrics.getHeight();
			if (textHeight * 1.3 > height) {
				this.size = this.size / (textHeight / (width / 1.6));
			} else if (textHeight * 3.0 < height) {
				this.size = this.size / (textHeight / (width / 1.6));
			}
		}
	}

	private void centerWidth(double btnLeft, double btnWidth, double textWidth) {
		double diff = btnWidth - textWidth;
		if (diff < 0) try {
			throw new Exception("Invalid width");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.x = btnLeft + diff/2;
	}

	public void setFontMetrics(Graphics g) {
		gFont = new Font(this.font, Font.PLAIN, (int) this.size);
		this.metrics = g.getFontMetrics(gFont);
	}

	public FontMetrics getFontMetrics() {
		return this.metrics;
	}

	public int getTextWidth() {
		if(this.metrics == null) return 0;

		int width = 0;
		for (int i = 0; i < this.text.length(); i++) {
			width += metrics.charWidth(this.text.charAt(i));
		}
		return width;
	}
}
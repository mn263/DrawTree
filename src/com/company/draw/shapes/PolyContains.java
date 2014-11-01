package com.company.draw.shapes;

public class PolyContains {
// DISCLAIMER:
//	THIS IS 3RD PARTY CODE, FROM THE JAVA.AWT.POLYGON CLASS.
//	NOT WRITTEN BY MY, ALTHOUGH I DID MAKE VERY MINOR CHANGES.
	public static boolean contains(double x, double y, int[] xpoints, int[] ypoints) {
		int npoints = xpoints.length;
		if (npoints <= 2) {
			return false;
		}
		int hits = 0;

		int lastx = xpoints[npoints - 1];
		int lasty = ypoints[npoints - 1];
		int curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
			curx = xpoints[i];
			cury = ypoints[i];

			if (cury == lasty) {
				continue;
			}

			int leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) continue;
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			} else {
				if (y < lasty || y >= cury) continue;
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) hits++;
		}
		return ((hits & 1) != 0);
	}
}
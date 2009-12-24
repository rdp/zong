package com.xenoage.util.math;

import static com.xenoage.util.math.Point2f.p;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import com.xenoage.util.enums.VSide;


/**
 * Test cases for the {@link ConvexHull} class.
 * 
 * @author Andreas Wenger
 */
public class ConvexHullTest
{
	
	@Test public void computeHullTest()
	{
		//top hull
		ArrayList<Point2f> points = new ArrayList<Point2f>();
		points.add(p(1, 1));
		points.add(p(2, 2));
		points.add(p(3, 4));
		points.add(p(4, 2));
		points.add(p(5, 1));
		ArrayList<Point2f> res = ConvexHull.create(points, VSide.Top).getPoints();
		assertEquals(3, res.size());
		assertEquals(p(1, 1), res.get(0));
		assertEquals(p(3, 4), res.get(1));
		assertEquals(p(5, 1), res.get(2));
		//bottom hull
		points = new ArrayList<Point2f>();
		points.add(p(1, 4));
		points.add(p(2, 3));
		points.add(p(3, 1));
		points.add(p(4, 2));
		points.add(p(5, 4));
		res = ConvexHull.create(points, VSide.Bottom).getPoints();
		assertEquals(4, res.size());
		assertEquals(p(1, 4), res.get(0));
		assertEquals(p(3, 1), res.get(1));
		assertEquals(p(4, 2), res.get(2));
		assertEquals(p(5, 4), res.get(3));
	}


}

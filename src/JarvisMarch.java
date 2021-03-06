/* JarvisMarch.java
 * 
 * Copyright (c) 2013 David Haegele
 *
 * (MIT License)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
import static java.lang.Math.sqrt;

/**
 * This class provides a method to get the convex hull from a set of
 * points. The method implements the Jarvis March (Giftwrapping in 2D)
 * algorithm.
 * <br>
 * (You can find the algorithm in pseudocode at 
 * http://en.wikipedia.org/wiki/Gift_wrapping_algorithm)
 * @author David Hägele
 * @version 1.0 - 26.11.13
 *
 */
public class JarvisMarch {

	/**
	 * calculates the convex hull of the specified array of points.
	 * <br>
	 * the array of points has to be of dimensions [n][2], <br>
	 * which means that a point can be obtained like this: <br>
	 * <code> double[] point = array[i]; </code><br>
	 * and coordinates like this: <br>
	 * <code> x= array[i][0] and y= array[i][1] </code>
	 * @param points in double[][]
	 * @return double[][] with points of the convex hull
	 */
	public static double[][] getConvexHull(double[][] points){
		if(points.length < 4){
			return points.clone();
		}
		
		double[] pointOnHull = points[getIndexOfLeftMostPoint(points)]; // leftmost point in shape
		double[][] hull = new double[points.length][2];
		
		int i = 0;
		double[] endpoint = points[0]; // initial endpoint for a candidate edge on the hull
		do {
			hull[i] = pointOnHull;
			endpoint = points[0];
			for(int j = 1; j < points.length; j++){
				if(endpoint == pointOnHull || isLeftOfLine(points[j], hull[i], endpoint)){
					endpoint = points[j]; // found greater left turn, update endpoint
				}
			}
			i++;
			pointOnHull = endpoint;
		} while(endpoint != hull[0]);
		
		/* i is now equal to the number of points of the hull.
		 * need to make correctly sized hull array now.
		 */
		double[][] hullToReturn = new double[i][2];
		for(i = 0; i < hullToReturn.length; i++){
			hullToReturn[i] = hull[i];
		}
		
		return hullToReturn;
	}
	
	// http://pastebin.com/6eBn8p7c
	private static int getIndexOfLeftMostPoint(double[][] points){
		int index = 0;
		double x = points[0][0];
		for(int i = 1; i < points.length; i++){
			if(points[i][0] < x){
				x = points[i][0];
				index = i;
			}
		}
		return index;
	}
	
	
	private static boolean isLeftOfLine(double[] point, double[] linePoint1, double[] linePoint2){
		// vec1 = vector from linePoint1 to linePoint2
		double[] vec1 = new double[]{ 	linePoint2[0] - linePoint1[0],
										linePoint2[1] - linePoint1[1]	};
		// vec2 = vector from linePoint1 to point
		double[] vec2 = new double[]{	point[0] - linePoint1[0],
										point[1] - linePoint1[1]	};
		
		// making vec1 a unit vector
		double var = sqrt(vec1[0]*vec1[0] + vec1[1]*vec1[1]);
		vec1[0] = vec1[0] / var;
		vec1[1] = vec1[1] / var;
		
		/* cause vec1 is now a unit vector (length = 1 = hypotenuse), sine 
		 * and cosine of vec1's angle can be obtained as follows: 
		 */
		double cos = vec1[0];
		double sin = vec1[1];
		
		/* making rotation matrix to turn coordinate system.
		 * (turn vectors clockwise)
		 * 
		 * clockwise rotation matrix  is  inverted counterclckws rot. matrix
		 *    counterclockwise rot. matrix:
		 *           cos(a)  -sin(a)
		 *           sin(a)   cos(a)
		 * 
		 *    clockwise rot. matrix:
		 *       cos(a)/det  sin(a)/det
		 *      -sin(a)/det  cos(a)/det
		 * 
		 * (det = determinant of counterclockwise rot. matrix)
		 */ 
		double det = (cos * cos) + (sin * sin); // or cos*cos - sin*-sin
		
		// rotating vec2 -> matrix * vec2
		var = vec2[0];
		vec2[0] =  cos/det * var	+ sin/det * vec2[1];
		vec2[1] = -sin/det * var 	+ cos/det * vec2[1];
		
		/* now the line between linePoint1 and 2 is x-Axis.
		 * if vec2 (vector from origin to point) points upwards,
		 * then point is left of line */
		return vec2[1] > 0.0;
	}

}

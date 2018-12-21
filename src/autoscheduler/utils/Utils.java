package autoscheduler.utils;

import java.util.Iterator;
import java.util.Vector;

import autoscheduler.types.Task;

public class Utils {

	public static double max(Vector<Double> vec) throws Exception {
		Iterator<Double> vecIt = vec.iterator();
		if(vecIt.hasNext()) {
			double maxVal = vecIt.next();
			while(vecIt.hasNext()) {
				maxVal = Math.max(maxVal,vecIt.next());
			}
			return maxVal;
		}
		else {
			throw new Exception("Empty vector");
		}
	}

}

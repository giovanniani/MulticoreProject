/**
 * BoundaryFinder class to be used for finding the minimum/maximum boundaries of
 * the data.
 */
package multicoreproject.version2;
import multicoreproject.*;

import java.util.concurrent.RecursiveTask;
import java.util.concurrent.ForkJoinPool;

public class BoundaryFinder extends RecursiveTask<Boundary> {
	static final int THRESHOLD = 50000;

	private CensusData _data;
	private int _low;
	private int _high;

	public BoundaryFinder(CensusData data, int lo, int hi) {
		_data = data;
		_low = lo;
		_high = hi;
	}

	private float min(float a, float b) {
		return a < b ? a : b;
	}

	private float max(float a, float b) {
		return a > b ? a : b;
	}

	protected Boundary compute() {
		if ((_high - _low) <= THRESHOLD) {
			float minLat = (float)1.0e10;
			float maxLat = (float)-1.0e10;
			float minLong = (float)1.0e10;
			float maxLong = (float)-1.0e10;
			for (int i = _low; i < _high; ++i) {
				minLat = min(minLat, _data.data[i].latitude);
				maxLat = max(maxLat, _data.data[i].latitude);
				minLong = min(minLong, _data.data[i].longitude);
				maxLong = max(maxLong, _data.data[i].longitude);
			}
			Boundary bounds = new Boundary(minLat, maxLat, minLong, maxLong);
			return bounds;
		} else {
			int mid = _low + (_high - _low) / 2;
			BoundaryFinder left = new BoundaryFinder(_data, _low, mid);
			BoundaryFinder right = new BoundaryFinder(_data, mid, _high);
			left.fork();
			Boundary rres = right.compute();
			Boundary lres = left.join();
			// Combine the results
			return new Boundary(min(lres.minLatitude, rres.minLatitude),
				max(lres.maxLatitude, rres.maxLatitude),
				min(lres.minLongitude, rres.minLongitude),
				max(lres.maxLongitude, rres.maxLongitude));
		}
	}

	public static Boundary findBoundaries(CensusData data) {
		return ForkJoinPool.commonPool().invoke(new BoundaryFinder(data, 0, data.data_size));
	}
}

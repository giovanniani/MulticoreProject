/**
 * Class for calculating the result.
 */
package multicoreproject.version2;
import multicoreproject.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class Query extends RecursiveTask<IntermediateResult> {
	static final int THRESHOLD = 5000;
	private CensusData _data;
	private float _min_lat;
	private float _max_lat;
	private float _min_long;
	private float _max_long;
	private int _low;
	private int _high;

	Query(CensusData data, float min_lat, float max_lat, float min_long, float max_long, int lo, int hi) {
		_data = data;
		_min_lat = min_lat;
		_max_lat = max_lat;
		_min_long = min_long;
		_max_long = max_long;
		_low = lo;
		_high = hi;
	}

	protected IntermediateResult compute() {
		int count = 0;
		int total = 0;
		if ((_high - _low) <= THRESHOLD) {
			for (int i = _low; i < _high; ++i) {
				// Add up the count within the triangle
				if (_data.data[i].latitude >= _min_lat
				    && _data.data[i].latitude < _max_lat
				    && _data.data[i].longitude >= _min_long
				    && _data.data[i].longitude < _max_long) {
					count += _data.data[i].population;
				}
				// Also add up the total population
				total += _data.data[i].population;
			}
		} else {
			int mid = _low + (_high - _low) / 2;
			Query left = new Query(_data, _min_lat, _max_lat, _min_long, _max_long, _low, mid);
			Query right = new Query(_data, _min_lat, _max_lat, _min_long, _max_long, mid, _high);
			left.fork();
			IntermediateResult rightRes = right.compute();
			IntermediateResult leftRes = left.join();
			// Combine the totals produced from each child
			count += rightRes.currentCount;
			total += rightRes.totalPopulation;
			count += leftRes.currentCount;
			total += leftRes.totalPopulation;
		}
		return new IntermediateResult(count, total);
	}

	// TODO: Fix parameter order
	public static QueryResult query(CensusData data, Boundary bounds, int grid_x, int grid_y, int min_x, int max_x, int min_y, int max_y) {
		float cell_width = (bounds.maxLatitude - bounds.minLatitude) / ((float)grid_x);
		float cell_height = (bounds.maxLongitude - bounds.minLongitude) / ((float)grid_y);
		// Calculate the query boundaries
		float min_lat = bounds.minLatitude + (min_x - 1) * cell_width;
		float max_lat = bounds.minLatitude + max_x * cell_width;
		float min_long = bounds.minLongitude + (min_y - 1) * cell_height;
		float max_long = bounds.minLongitude + max_y * cell_height;
		IntermediateResult res = ForkJoinPool.commonPool()
		    .invoke(new Query(data, min_lat, max_lat, min_long,
			max_long, 0, data.data_size));
		return new QueryResult(res.currentCount,
		    ((float)100.0) * res.currentCount / ((float)res.totalPopulation));
	}
}

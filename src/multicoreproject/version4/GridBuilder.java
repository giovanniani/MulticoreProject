package multicoreproject.version4;

import multicoreproject.version2.Boundary;
import multicoreproject.CensusData;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class GridBuilder extends RecursiveTask<Grid> {
	static final int THRESHOLD = 2000;
	private Grid _grid;
	private CensusData _data;
	private int _high;
	private int _low;

	GridBuilder(CensusData data, int lo, int hi, Grid grid) {
		_data = data;
		_high = hi;
		_low = lo;
		_grid = grid;
	}

	protected Grid compute() {
		if ((_high - _low) <= THRESHOLD) {
			for (int i = _low; i < _high; ++i) {
				_grid.update(_data.data[i]);
			}
		} else {
			int mid = _low + (_high - _low) / 2;
			GridBuilder left = new GridBuilder(_data, _low, mid, _grid.emptyCopy());
			GridBuilder right = new GridBuilder(_data, mid, _high, _grid.emptyCopy());
			left.fork();
			Grid rightg = right.compute();
			Grid leftg = left.join();
			_grid.combine(leftg);
			_grid.combine(rightg);
		}
		return _grid;
	}

	public static int[][] buildGrid(CensusData data, int grid_x, int grid_y, Boundary boundary) {
		Grid grid = new Grid(boundary, grid_x, grid_y);
		return ForkJoinPool.commonPool().invoke(new GridBuilder(data, 0, data.data_size, grid)).data;
	}
}

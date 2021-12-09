/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multicoreproject.version5;


import multicoreproject.version2.Boundary;
import multicoreproject.CensusData;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.lang.Thread;
import multicoreproject.version5.Grid;

public class GridBuilder extends Thread {
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

	public void run() {
		if ((_high - _low) <= THRESHOLD) {
			for (int i = _low; i < _high; ++i) {
				_grid.update(_data.data[i]);
			}
		} else {
			int mid = _low + (_high - _low) / 2;
			GridBuilder left = new GridBuilder(_data, _low, mid, _grid);
			GridBuilder right = new GridBuilder(_data, mid, _high, _grid);
			left.start();
			right.run();
			try {
				left.join();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static GridItem[][] buildGrid(CensusData data, int grid_x, int grid_y, Boundary boundary) {
		Grid grid = new Grid(boundary, grid_x, grid_y);
		GridBuilder builder = new GridBuilder(data, 0, data.data_size, grid);
		builder.run();
		try {
			builder.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return grid.data;
	}
}

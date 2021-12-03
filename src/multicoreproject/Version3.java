package multicoreproject;
import multicoreproject.version2.*;

public class Version3 extends VersionObject {
	private CensusData _data;
	private int _grid_x;
	private int _grid_y;

	public Version3(CensusData data, int grid_x, int grid_y) {
		_data = data;
		_grid_x = grid_x;
		_grid_y = grid_y;
	}

	public QueryResult query(int min_x, int min_y, int max_x, int max_y) {
		return null;
	}
}

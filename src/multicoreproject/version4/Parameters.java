package multicoreproject.version4;

import multicoreproject.version2.*;

public class Parameters {
	public int grid_x;
	public int grid_y;
	public Boundary bounds;

	public Parameters(int gx, int gy, Boundary b) {
		grid_x = gx;
		grid_y = gy;
		bounds = b;
	}
}

package multicoreproject;

abstract class VersionObject {
	// Do an actual query (to be called after a user inputs four coordinates
	// on the command line)
	abstract QueryResult query(int min_x, int max_x, int min_y, int max_y);
}

package multicoreproject;

/**
 *
 * @author Giova
 */
public class Version1 extends VersionObject{
    private CensusData _data;
    
    public Version1(CensusData data) {
		_data = data;
	}

	public QueryResult query(int min_x, int max_x, int min_y, int max_y) {
		// TODO
		return new QueryResult(100, (float)3.8);
	}
    
}

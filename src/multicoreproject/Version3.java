package multicoreproject;
import multicoreproject.version2.*;

public class Version3 extends VersionObject {
	private CensusData _data;
	private int _grid_x;
	private int _grid_y;
        private int populationGrid[][];

	public Version3(CensusData data, int grid_x, int grid_y) {
		_data = data;
		_grid_x = grid_x;
		_grid_y = grid_y;
                populationGrid = new int[grid_x][grid_y];
	}

	public QueryResult query(int min_x, int min_y, int max_x, int max_y) {
            
            QueryResult tempQuery = new QueryResult(0, 0);
            
            populationGrid[0][100] = 89;
            for(int i = 0; i < _grid_x; i ++)
            {
                for(int j = 0; j < _grid_y; j ++)
                {
                    if(populationGrid[i][j] == 89)
                    {
                        System.out.println(populationGrid[i][j]);
                    }
                    
                }                
            }
            return tempQuery;
	}
}

package multicoreproject;
import multicoreproject.version2.*;

public class Version3 extends VersionObject {
	private CensusData _data;
	private int _grid_x;
	private int _grid_y;
        private int _populationGrid[][];
        private Boundary _globalBoundaries;

	public Version3(CensusData data, int grid_x, int grid_y) {
		_data = data;
		_grid_x = grid_x;
		_grid_y = grid_y;
                _populationGrid = new int[grid_x][grid_y];
	}

	public QueryResult query(int min_long, int min_lat, int max_long, int max_lat) {
            
            QueryResult tempQuery = new QueryResult(0, 0);
            _globalBoundaries = new Boundary(0, 0, 0, 0);
            float newMaxLat, newMinLat, newMinLong, newMaxLong;
            CensusGroup tempGroup;           
            findBoundaries();
            tempGroup = this._data.data[0];
            
            int blockX, blockY;
            blockX = convertRangeInt(0, _grid_x, _globalBoundaries.minLongitude, _globalBoundaries.maxLongitude, tempGroup.longitude); // converting the latitud into Y block
            blockY = convertRangeInt(0, _grid_y, _globalBoundaries.minLatitude, _globalBoundaries.maxLatitude, tempGroup.latitude); // converting the longitued into X block
            //once converted we can put them in the corresponding block in the grid
            
            
            
            for(int block = 0; block < _data.data_size; block++)
            {
                tempGroup = this._data.data[block];
                blockX = convertRangeInt(0, _grid_x, _globalBoundaries.minLongitude, _globalBoundaries.maxLongitude, tempGroup.longitude); // converting the latitud into Y block
                blockY = convertRangeInt(0, _grid_y, _globalBoundaries.minLatitude, _globalBoundaries.maxLatitude, tempGroup.latitude); // converting the longitued into X block               
                if(_populationGrid[blockX][blockY] == 0)
                {
                    _populationGrid[blockX][blockY] = tempGroup.population;
                }
                else
                {
                    _populationGrid[blockX][blockY] += tempGroup.population;
                }
            }
            
            
            for(int i = 0; i < _grid_x; i ++)
            {
                for(int j = 0; j < _grid_y; j ++)
                {                    
                    System.out.println(_populationGrid[i][j]);                    
                }                
            }
            return tempQuery;
	}
        
        private void findBoundaries()
        {
            _globalBoundaries.maxLatitude = _globalBoundaries.minLatitude = _data.data[0].latitude;
            _globalBoundaries.maxLongitude = _globalBoundaries.minLongitude = _data.data[0].longitude;
            for(int block = 0; block < _data.data_size; block++)
            {
                _globalBoundaries.population += _data.data[block].population;
                if(_data.data[block].latitude < _globalBoundaries.minLatitude)
                        _globalBoundaries.minLatitude = _data.data[block].latitude;
                if(_data.data[block].latitude > _globalBoundaries.maxLatitude)
                    _globalBoundaries.maxLatitude = _data.data[block].latitude;

                if(_data.data[block].longitude < _globalBoundaries.minLongitude)
                    _globalBoundaries.minLongitude = _data.data[block].longitude;
                if(_data.data[block].longitude > _globalBoundaries.minLongitude)
                    _globalBoundaries.maxLongitude = _data.data[block].longitude;            
            }
        }            
        
        private float convertRange(float minRealRange, float maxRealRange, int newRange, int actualValue)
        {
            float newValue = (((maxRealRange - minRealRange) * actualValue) / newRange) + minRealRange;
            return newValue;
        }
        
        private int convertRangeInt(float minRealRange, float maxRealRange, float newMinRange, float newMaxRange, float actualValue)
        {
            float newValue = (((maxRealRange - minRealRange) * actualValue) / (newMaxRange + newMinRange)) + minRealRange;
            return (int) newValue;
        }
}

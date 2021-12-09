package multicoreproject;
import multicoreproject.version2.*;

public class Version3 extends VersionObject {
	private CensusData _data;
	private int _grid_x;
	private int _grid_y;
        private int _populationGrid[][], _updatedPopulationGrid[][];
        private Boundary _globalBoundaries;

	public Version3(CensusData data, int grid_x, int grid_y) {
		_data = data;
		_grid_x = grid_x;
		_grid_y = grid_y;
                _populationGrid = new int[grid_y][grid_x];
                _updatedPopulationGrid = new int[grid_y][grid_x];                           
            
            _globalBoundaries = new Boundary(0, 0, 0, 0);
            float newMaxLat, newMinLat, newMinLong, newMaxLong;
            CensusGroup tempGroup;           
            findBoundaries();
            tempGroup = this._data.data[0];
            
            int blockX, blockY;
            blockX = convertRangeInt(0, _grid_x, _globalBoundaries.minLongitude, _globalBoundaries.maxLongitude, tempGroup.longitude); // converting the latitud into Y block
            blockY = convertRangeInt(0, _grid_y, _globalBoundaries.minLatitude, _globalBoundaries.maxLatitude, tempGroup.latitude); // converting the longitued into X block
            //once converted we can put them in the corresponding block in the grid
             
            
            //creating now the population grid
            for(int block = 0; block < _data.data_size; block++)
            {
                tempGroup = this._data.data[block];
                blockX = convertRangeInt(0, _grid_x, _globalBoundaries.minLongitude, _globalBoundaries.maxLongitude, tempGroup.longitude); // converting the latitud into Y block
                blockY = convertRangeInt(0, _grid_y, _globalBoundaries.minLatitude, _globalBoundaries.maxLatitude, tempGroup.latitude); // converting the longitued into X block               
                if(_populationGrid[blockY][blockX] == 0)
                {
                    _populationGrid[blockY][blockX] = tempGroup.population;
                }
                else
                {
                    _populationGrid[blockY][blockX] += tempGroup.population;
                }
            }
            
           //now with the population grid we update it with the new formula of combining previous fields
           updateGrid();
	}

	public QueryResult query(int min_long, int min_lat, int max_long, int max_lat) {
            
            min_long -= 1;
            max_lat -= 1;
            max_long -= 1;
            min_lat -= 1;
            QueryResult tempQuery = new QueryResult(0, 0);
            //now with the gripd updated, we run the query
            int topLeft = 0, topRight = 0, bottomLeft = 0, bottomRight = 0;
            if(max_lat > 1 && max_long > 1)
                bottomRight = _updatedPopulationGrid[max_lat - 1][max_long - 1];
            
            if(min_long > 0 && min_lat > 0)
            {
                bottomLeft = _updatedPopulationGrid[max_lat][min_long - 1];
                topLeft = _updatedPopulationGrid[min_lat - 1][min_long - 1];               
                topRight = _updatedPopulationGrid[min_lat - 1][max_long];               
            }
            else
            {
                if(min_lat == 0 && min_long > 0)
                {
                    topLeft = topRight = 0;
                    bottomLeft = _updatedPopulationGrid[max_lat][min_long - 1];
                }
                else                
                {   
                    if(min_lat > 1 && min_long == 0)
                    {
                        topRight = _updatedPopulationGrid[min_lat - 1][max_long];
                        topLeft = bottomLeft = 0;
                    }     
                    else
                    {
                        if(min_lat == 0 && min_long == 0)
                        {
                            topLeft = topRight = bottomLeft = 0;
                        }
                    }

                }
            }
           
            tempQuery.population = bottomRight - bottomLeft - topRight + topLeft;
            tempQuery.percentage = (tempQuery.population / (float)_globalBoundaries.population) * 100;
            
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
        
        private int convertRangeInt(float minRealRange, float maxRealRange, float newMinRange, float newMaxRange, float actualValue)
        {
            float newValue = (((maxRealRange - minRealRange) * actualValue) / (newMaxRange + newMinRange)) + minRealRange;
            return (int) newValue;
        }
        
        private void updateGrid()
        {
            int populationGrid[][] = _populationGrid;
            //int populationGrid[][] = {{0,11,1,9},{1,7,4,3},{2,2,0,0},{9,1,1,1}};
            int updatedGrid[][] = new int[populationGrid.length][populationGrid[0].length];

            int tempPopulationSize;             

            for(int i = 0; i < populationGrid.length; i++)
            {
                for(int j = 0; j < populationGrid[i].length; j++)
                {
                    tempPopulationSize = populationGrid[i][j];
                    if(i > 0 && j > 0)
                        tempPopulationSize += updatedGrid[i - 1][j] + updatedGrid[i][j - 1] - updatedGrid[i - 1][j - 1];
                    else{
                        if(i != 0)
                            tempPopulationSize += updatedGrid[i - 1][j];
                        if(j != 0)
                            tempPopulationSize += updatedGrid[i][j - 1];
                    }                    
                    updatedGrid[i][j] = tempPopulationSize;
                }
            }
            this._updatedPopulationGrid = updatedGrid;
            System.out.println(updatedGrid[updatedGrid.length - 1][updatedGrid[0].length - 1]);            
        }
        
        public String getVersion()
        {
            return "Version3";
        }
}

package multicoreproject;
import multicoreproject.version2.*;
/**
 *
 * @author Giova
 */
public class Version1 extends VersionObject{
    private CensusData _data;    
    private Boundary _globalBoundaries;
    private int _grid_x;
    private int _grid_y;
    
    public Version1(CensusData data, int grid_x, int grid_y) {
        _data = data;
        _grid_x = grid_x;
        _grid_y = grid_y;   
        _globalBoundaries = new Boundary(0, 0, 0, 0);
                
        findBoundaries();      
        //transforming ranges for search
        
        //-----------
    }

    public QueryResult query(int min_long, int min_lat, int max_long, int max_lat) 
    {
        min_long -= 1;        
        min_lat -= 1;
        float newMaxLat, newMinLat, newMinLong, newMaxLong;
        newMinLat = convertRange(_globalBoundaries.minLatitude, _globalBoundaries.maxLatitude, _grid_y, min_lat);
        newMaxLat = convertRange(_globalBoundaries.minLatitude, _globalBoundaries.maxLatitude, _grid_y, max_lat);
        newMinLong = convertRange(_globalBoundaries.minLongitude, _globalBoundaries.maxLongitude, _grid_x, min_long);
        newMaxLong = convertRange(_globalBoundaries.minLongitude, _globalBoundaries.maxLongitude, _grid_x, max_long);
        // TODO
        QueryResult tempQuery = new QueryResult(0, 0);
        CensusGroup tempGroup;           
        for(int block = 0; block < _data.data_size; block++)
        {
            tempGroup = this._data.data[block];
            if(tempGroup.latitude >= newMinLat && tempGroup.latitude <= newMaxLat)
            {
                if(tempGroup.longitude >= newMinLong && tempGroup.longitude <= newMaxLong)
                    tempQuery.population += tempGroup.population;                    
            }
        }
        
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
    
    private float convertRange(float minRealRange, float maxRealRange, int newRange, int actualValue)
    {
        float newValue = (((maxRealRange - minRealRange) * actualValue) / newRange) + minRealRange;
        return newValue;
    }
}

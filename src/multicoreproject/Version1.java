package multicoreproject;
import multicoreproject.version2.*;
/**
 *
 * @author Giova
 */
public class Version1 extends VersionObject{
    private CensusData _data;
    private Boundary _boundaries;
    private Boundary _globalBoundaries;
    
    public Version1(CensusData data) {
		_data = data;
                
	}

    public QueryResult query(int min_long, int max_lat, int max_long, int min_lat) {
            // TODO
            _globalBoundaries = new Boundary(0, 0, 0, 0);
            QueryResult tempQuery = new QueryResult(0, 0);
            findBoundaries();      
            CensusGroup tempGroup;
            /*if(_globalBoundaries.checkBoudnaries(min_lat, max_lat, min_long, max_long))
            {*/
                for(int block = 0; block < _data.data_size; block++)
                {
                    tempGroup = this._data.data[block];
                    if(tempGroup.realLatitude >= min_lat && tempGroup.realLatitude <= max_lat)
                    {
                        if(tempGroup.longitude >= min_long && tempGroup.longitude <= max_long)
                            tempQuery.population += tempGroup.population;                    
                    }
                }
            //}
            tempQuery.percentage = (tempQuery.population / (float)_globalBoundaries.population) * 100;
            return tempQuery;

    }
        
    private Boundary findBoundaries()
    {
        _globalBoundaries.maxLatitude = _globalBoundaries.minLatitude = _data.data[0].realLatitude;
        _globalBoundaries.maxLongitude = _globalBoundaries.minLongitude = _data.data[0].longitude;
        for(int block = 0; block < _data.data_size; block++)
        {
            if(_data.data[block].realLatitude < _globalBoundaries.minLatitude)
                _globalBoundaries.minLatitude = _data.data[block].realLatitude;
            if(_data.data[block].realLatitude > _globalBoundaries.maxLatitude)
                _globalBoundaries.maxLatitude = _data.data[block].realLatitude;
            
            if(_data.data[block].longitude < _globalBoundaries.minLongitude)
                _globalBoundaries.minLatitude = _data.data[block].longitude;
            if(_data.data[block].longitude > _globalBoundaries.minLongitude)
                _globalBoundaries.minLongitude = _data.data[block].longitude;
            _globalBoundaries.population += _data.data[0].population;
        }
        return null;
    }
        
    
    //private get
        
    
}

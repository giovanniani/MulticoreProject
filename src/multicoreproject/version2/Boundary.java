package multicoreproject.version2;

public class Boundary {
	public float minLatitude;
	public float maxLatitude;
	public float minLongitude;
	public float maxLongitude;
        public int population;

	public Boundary(float minLat, float maxLat, float minLong, float maxLong) {
		minLatitude = minLat;
		maxLatitude = maxLat;
		minLongitude = minLong;
		maxLongitude = maxLong;
                population = 0;
	}
        
        public boolean checkBoudnaries(float minLat, float maxLat, float minLong, float maxLong)
        {
            if(minLat < this.minLatitude || maxLat > this.maxLatitude || minLong < this.minLongitude || maxLong > this.maxLongitude)
                return false;
            if(minLat > maxLat || minLong > maxLong)
                return false;
            return true;
        }
        
        
}

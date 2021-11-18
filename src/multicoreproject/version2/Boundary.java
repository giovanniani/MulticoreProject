package multicoreproject.version2;

public class Boundary {
	public float minLatitude;
	public float maxLatitude;
	public float minLongitude;
	public float maxLongitude;

	Boundary(float minLat, float maxLat, float minLong, float maxLong) {
		minLatitude = minLat;
		maxLatitude = maxLat;
		minLongitude = minLong;
		maxLongitude = maxLong;
	}
}

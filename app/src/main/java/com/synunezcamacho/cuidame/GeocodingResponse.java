package com.synunezcamacho.cuidame;

import java.util.List;

public class GeocodingResponse {
    public List<Result> results;
    public String status;

    public static class Result {
        public Geometry geometry;
    }

    public static class Geometry {
        public Location location;
    }

    public static class Location {
        public double lat;
        public double lng;
    }
}

package ch.epfl.javelo.routing;


import ch.epfl.javelo.projection.PointCh;

import java.util.List;

public class TestForRoute implements Route{

    @Override
    public int indexOfSegmentAt(double position) {
        return 0;
    }

    @Override
    public double length() {
        return 0;
    }

    @Override
    public List<Edge> edges() {
        return null;
    }

    @Override
    public List<PointCh> points() {
        return null;
    }

    @Override
    public PointCh pointAt(double position) {
        return null;
    }

    @Override
    public double elevationAt(double position) {
        return 0;
    }

    @Override
    public int nodeClosestTo(double position) {
        return 0;
    }

    @Override
    public RoutePoint pointClosestTo(PointCh point) {
        return null;
    }
/*
    public static void main(String[] args) {
        int[] tab = {3, 2, 12, 53, 23};
        for (int i = 0; i < 5; ++i) {
            String position = (i > 0 && i < tab.length - 1) ? "middle" : ((i == 0) ? "first" : "last");
            System.out.println(position);
        }
    }*/
}

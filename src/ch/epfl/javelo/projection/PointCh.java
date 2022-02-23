package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

public record PointCh(double e, double n) {
    public PointCh {
        Preconditions.checkArgument(SwissBounds.containsEN(e, n));
    }

    public double squaredDistanceTo(PointCh that){
        double coordX = this.e() - that.e();
        double coordY = this.n() - that.n();
        return Math2.squaredNorm(coordX, coordY);
    }

    public double distanceTo(PointCh that){
        return Math.sqrt(this.squaredDistanceTo(that));
    }

    public double lon(){
        return Ch1903.lon(this.e(), this.n());
    }

    public double lat(){
        return Ch1903.lat(this.e(), this.n());
    }
}

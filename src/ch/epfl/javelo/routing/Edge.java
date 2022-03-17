package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;

import java.util.function.DoubleUnaryOperator;

public record Edge(int fromNodeId, int toNodeId, PointCh fromPoint,
                   PointCh toPoint, double length, DoubleUnaryOperator profile) {

    public static Edge of(Graph graph, int edgeId, int fromNodeId, int toNodeId) {
        PointCh fromPoint = graph.nodePoint(fromNodeId);
        PointCh toPoint = graph.nodePoint(toNodeId);
        double length = graph.edgeLength(edgeId);
        DoubleUnaryOperator profil = graph.edgeProfile(edgeId);

        return new Edge(fromNodeId, toNodeId, fromPoint, toPoint, length, profil);
    }

    public double positionClosestTo(PointCh point) {

        double aX = fromPoint.e();
        double aY = fromPoint.n();
        double bX = toPoint.e();
        double bY = toPoint.n();
        double pX = point.e();

        double pY = point.n();
        return Math2.projectionLength(aX, aY, bX, bY, pX, pY);
    }

    public PointCh pointAt(double position) {

        double positionE = fromPoint.e() + (position / this.length) * (toPoint.e() - fromPoint.e());
        double positionN = fromPoint.n() + (position / this.length) * (toPoint.n() - fromPoint.n());

        return new PointCh(positionE, positionN);
    }
    // À faire.
    public double elevationAt(double position) {
        return 0.0;
    }
}

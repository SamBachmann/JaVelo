package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;
public record MapViewParameters(int zoom, double xHautGauche, double yHautGauche) {

    public Point2D topLeft() {
        return new Point2D(this.xHautGauche, this.yHautGauche);
    }

    public MapViewParameters withMinXY(double xHautGauche, double yHautGauche) {
        return new MapViewParameters(this.zoom, xHautGauche, yHautGauche);
    }

    public PointWebMercator pointAt2(double x, double y) {
        double coordonneesX = x + this.xHautGauche;
        double coordonneesY = y + this.yHautGauche;
        return PointWebMercator.of(zoom, coordonneesX, coordonneesY);
    }

    public double viewX(PointWebMercator pointWebMercator) {
        return pointWebMercator.xAtZoomLevel(zoom) - this.xHautGauche;
    }

    public double viewY(PointWebMercator pointWebMercator) {
        return pointWebMercator.yAtZoomLevel(zoom) - this.yHautGauche;
    }
}

package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.geometry.Point2D;

/**
 * Enregistrement représentant les paramètres de la carte : le niveau de zoom ainsi
 * que les coordonnées en x et en y du coin en haut à gauche de la portion de carte
 * affichée sur l'interface graphique, exprimées dans le système Web Mercator.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 05/05/2022
 */
public record MapViewParameters(int zoom, double xHautGauche, double yHautGauche) {

    /**
     * Méthode nous donnant les coordonnées du coin haut gauche sous la forme d'un objet
     * de type Point2D.
     *
     * @return Les coordonnées du coin haut gauche sous la forme d'un objet de type Point2D.
     */
    public Point2D topLeft() {
        return new Point2D(this.xHautGauche, this.yHautGauche);
    }

    /**
     * Méthode nous donnant une instance de MapViewParameters identique au récepteur,
     * mis à part les coordonnées du coin haut gauche, qui sont celles passées en arguments.
     *
     * @param xHautGauche La coordonnée en x du coin en haut à gauche.
     * @param yHautGauche La coordonnée en y du coin en haut à gauche.
     * @return Une instance de MapViewParameters identique au récepteur à l'exception
     * des coordonnées du coin en haut à gauche de l'interface graphique.
     */
    public MapViewParameters withMinXY(double xHautGauche, double yHautGauche) {
        return new MapViewParameters(this.zoom, xHautGauche, yHautGauche);
    }

    /**
     * Méthode nous donnant un point Web Mercator à partir de coordonnées exprimées
     * par rapport au coin haut gauche de la portion de carte affichée à l'écran.
     *
     * @param x La coordonnée en x par rapport au coin haut gauche.
     * @param y La coordonnée en y par rapport au coin haut gauche.
     * @return Un point Web Mercator à partir de coordonnées exprimées par rapport
     * au coin haut gauche de l'écran.
     */
    public PointWebMercator pointAt2(double x, double y) {
        double coordonneesX = x + this.xHautGauche;
        double coordonneesY = y + this.yHautGauche;
        return PointWebMercator.of(zoom, coordonneesX, coordonneesY);
    }

    /**
     * Méthode nous donnant la coordonnée en x, exprimée par rapport au coin haut gauche
     * de la portion de carte affichée à l'écran, à partir d'un point Web Mercator.
     *
     * @param pointWebMercator Un point Web Mercator.
     * @return La coordonnée en x correspondante, exprimée par rapport au coin haut gauche.
     */
    public double viewX(PointWebMercator pointWebMercator) {
        return pointWebMercator.xAtZoomLevel(zoom) - this.xHautGauche;
    }

    /**
     * Méthode nous donnant la coordonnée en y, exprimée par rapport au coin haut gauche
     * de la portion de carte affichée à l'écran, à partir d'un point Web Mercator.
     *
     * @param pointWebMercator Un point Web Mercator.
     * @return La coordonnée en y correspondante, exprimée par rapport au coin haut gauche.
     */
    public double viewY(PointWebMercator pointWebMercator) {
        return pointWebMercator.yAtZoomLevel(zoom) - this.yHautGauche;
    }
}

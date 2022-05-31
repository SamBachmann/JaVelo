package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe gérant l'affichage de l'itinéraire et certaines des interactions avec celui-ci.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 11/05/2022
 */
public final class RouteManager {

    public static final int RAYON_CERCLE = 5;

    private final RouteBean routeBean;
    private final ObjectProperty<MapViewParameters> parametresCarte;
    private final Pane pane;
    private final Polyline dessinItineraire;
    private final Circle highlightPosition;

    public RouteManager (RouteBean routeBean,
                         ObjectProperty<MapViewParameters> parametresCarte){

        this.routeBean = routeBean;
        this.parametresCarte = parametresCarte;
        this.pane = new Pane();
        this.dessinItineraire = new Polyline();
        this.highlightPosition = new Circle();
        highlightPosition.setVisible(false);

        pane.setPickOnBounds(false);
        dessinItineraire.setId("route");
        highlightPosition.setRadius(RAYON_CERCLE);
        highlightPosition.setId("highlight");
        pane.getChildren().add(dessinItineraire);
        pane.getChildren().add(highlightPosition);


        routeBean.routeProperty().addListener(observable -> {
            dessinItineraire();
            dessinCercle();
        });

        parametresCarte.addListener((observable, oldValue, newValue) -> {
            //changement niveau zoom
            if (oldValue.zoom() != newValue.zoom()){
                dessinItineraire();
                dessinCercle();
            }
            //déplacement carte
            if (oldValue.yHautGauche() != newValue.yHautGauche()
                    || oldValue.xHautGauche() != newValue.xHautGauche()){
                dessinItineraire.setLayoutX( - newValue.xHautGauche());
                dessinItineraire.setLayoutY( - newValue.yHautGauche());
                dessinCercle();
            }
        });

        routeBean.highlightedPositionProperty().addListener(observable -> dessinCercle());

        pane.setOnMouseClicked(event -> {

            Point2D point2D = pane.localToParent(event.getX(), event.getY());
            double xSouris = point2D.getX();
            double ySouris = point2D.getY();

            PointWebMercator pointWebMercatorCurseur = PointWebMercator.ofPointCh(
                    routeBean.route().pointAt(routeBean.highlightedPosition())
            );

            double xPostionHighlighted = this.parametresCarte.get().viewX(pointWebMercatorCurseur);
            double yPostionHighlighted = this.parametresCarte.get().viewY(pointWebMercatorCurseur);

            if (xPostionHighlighted - RAYON_CERCLE <= xSouris && xSouris <= xPostionHighlighted + RAYON_CERCLE
                    && yPostionHighlighted - RAYON_CERCLE <= ySouris && ySouris <= yPostionHighlighted + RAYON_CERCLE){
                Waypoint newWaypoint = new Waypoint(pointWebMercatorCurseur.toPointCh(),
                        routeBean.route().nodeClosestTo(routeBean.highlightedPosition()));

                routeBean.WaypointsListProperty().add(
                        routeBean.indexOfNonEmptySegmentAt(routeBean.highlightedPosition())+ 1, newWaypoint
                );

            }

        });
    }

    /**
     * Accesseur du panneau contenant l'itinéraire et le disque.
     *
     * @return Le Pane contenant l'itinéraire.
     */
    public Pane pane() {
        return pane;
    }

    /**
     * Construction et positionnement de la polyline représentant l'itinéraire.
     */
    private void dessinItineraire(){
        Route itineraire = routeBean.route();

        int zoom = parametresCarte.get().zoom();
        List<Double> listPoints = new ArrayList<>();

        if (itineraire != null) {
            dessinItineraire.setVisible(true);

            for (PointCh pointExtremite : itineraire.points()) {
                PointWebMercator pointEnWebMercator = PointWebMercator.ofPointCh(pointExtremite);
                listPoints.add(pointEnWebMercator.xAtZoomLevel(zoom));
                listPoints.add(pointEnWebMercator.yAtZoomLevel(zoom));
            }
        }else{
            dessinItineraire.setVisible(false);
            highlightPosition.setVisible(false);
        }

        dessinItineraire.getPoints().setAll(listPoints);
        dessinItineraire.setLayoutX( - parametresCarte.get().xHautGauche());
        dessinItineraire.setLayoutY( - parametresCarte.get().yHautGauche());


    }

    private void dessinCercle(){

        Route itineraire = routeBean.route();

        if (itineraire != null) {

            if (! Double.isNaN(routeBean.highlightedPositionProperty().get())) {
                highlightPosition.setVisible(true);

                PointWebMercator pointWebMercator = PointWebMercator.ofPointCh(routeBean.route().pointAt(routeBean.highlightedPosition()));
                highlightPosition.setCenterX(this.parametresCarte.get().viewX(pointWebMercator));
                highlightPosition.setCenterY(this.parametresCarte.get().viewY(pointWebMercator));

            } else {
                highlightPosition.setVisible(false);
            }
        }
    }
}

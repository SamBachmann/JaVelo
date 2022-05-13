package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.function.Consumer;

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
    private final Consumer<String> errorHandler;
    private final Pane pane;
    private Polyline dessin;
    private Circle highlightPosition;

    public RouteManager (RouteBean routeBean,
                         ObjectProperty<MapViewParameters> parametresCarte,
                         Consumer<String> errorHandler ){
        this.routeBean = routeBean;
        this.parametresCarte = parametresCarte;
        this.errorHandler = errorHandler;
        this.pane = new Pane();
        this.dessin = new Polyline();
        this.highlightPosition = new Circle();

        pane.setPickOnBounds(false);
        dessin.setId("route");
        highlightPosition.setRadius(RAYON_CERCLE);
        highlightPosition.setId("highlight");
        pane.getChildren().add(dessin);

        routeBean.routeProperty().addListener(observable ->{
            dessinItineraire();
            highlightPosition.setId("highlight");
            pane.getChildren().add(dessin);
            highlightPosition.setRadius(RAYON_CERCLE);
        });

        pane.getChildren().add(dessin);
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

        for (PointCh pointExtremite: itineraire.points()) {
            PointWebMercator pointEnWebMercator = PointWebMercator.ofPointCh(pointExtremite);
                        highlightPosition.setRadius(RAYON_CERCLE);            dessin.getPoints().add(pointEnWebMercator.xAtZoomLevel(zoom));
            dessin.getPoints().add(pointEnWebMercator.yAtZoomLevel(zoom));
        }listPoints.add(pointEnWebMercator.xAtZoomLevel(zoom));
            listPoints.add(pointEnWebMercator.yAtZoomLevel(zoom));
        double xEcran = dessin.getLayoutX() - parametresCarte.get().xHautGauche();
        double yEcran = dessin.getLayoutY() - parametresCarte.get().yHautGauche();

        dessin.setLayoutX(xEcran);
        dessin.setLayoutY(yEcran);
    }

    private void dessinCercle(){

    }


}

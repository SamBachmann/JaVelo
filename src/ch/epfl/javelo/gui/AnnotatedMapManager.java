package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

/**
 * Classe gérant l'affichage de la carte annotée.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 25/05/2022
 */
public final class AnnotatedMapManager {

    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final StackPane stackPane;
    private final DoubleProperty position;
    private final ObjectProperty<Point2D> positionActuelleSouris;
    private final ObjectProperty<MapViewParameters> mapViewParameters = new SimpleObjectProperty<>();
    private final RouteBean routeBean;

    /**
     * Constructeur de AnnotatedMapManager
     *
     * @param graph         Le graph de JaVelo.
     * @param tileManager   Le gestionnaire de tuiles.
     * @param routeBean     Le bean qui contient les propriétés relatives à un itinéraire.
     * @param errorConsumer Interface fonctionnelle qui un consommateur d'erreur.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager,
                               RouteBean routeBean, Consumer<String> errorConsumer) {

        this.mapViewParameters.set(new MapViewParameters(12, 543200, 370650));
        this.waypointsManager = new WaypointsManager(graph, mapViewParameters, routeBean.WaypointsListProperty(), errorConsumer);
        this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParameters);
        this.routeManager = new RouteManager(routeBean, mapViewParameters);

        Pane premierPane = this.baseMapManager.pane();
        Pane deuxiemePane = this.routeManager.pane();
        Pane troisiemePane = this.waypointsManager.pane();

        this.stackPane = new StackPane(premierPane, deuxiemePane, troisiemePane);
        this.stackPane.getStylesheets().add("map.css");

        this.routeBean = routeBean;
        this.position = new SimpleDoubleProperty();
        this.positionActuelleSouris = new SimpleObjectProperty<>();
    }

    /**
     * Méthode retournant le panneau contenant la carte annotée.
     *
     * @return Le pane du panneau contenant la carte associée.
     */
    public Pane pane() {
        return this.stackPane;
    }

    /**
     * Méthode retournant la propriété contenant la position du pointeur de la souris
     * le long de l'itinéraire.
     *
     * @return La propriété contenant la position du pointeur de la souris le long de l'itinéraire.
     */
    public DoubleProperty mousePositionOnRouteProperty() {

        stackPane.setOnMouseMoved(event -> {

            Point2D positionActuelleDeLaSouris = new Point2D(event.getX(), event.getY());
            this.positionActuelleSouris.set(positionActuelleDeLaSouris);

            double xEnWebMercator = positionActuelleDeLaSouris.getX();
            double yEnWebMercator = positionActuelleDeLaSouris.getY();

            PointCh pointCh = this.mapViewParameters.get().pointAt2(xEnWebMercator, yEnWebMercator).toPointCh();
            RoutePoint sourisItineraire = this.routeBean.route().pointClosestTo(pointCh);
            double positionItineraire = sourisItineraire.position();
            double distanceToReference = sourisItineraire.distanceToReference();
            PointCh routePointCh = sourisItineraire.point();
            PointWebMercator ptWebMercator = PointWebMercator.ofPointCh(routePointCh);
            double routePointChXEcran = this.mapViewParameters.get().viewX(ptWebMercator);
            double routePointChYEcran = this.mapViewParameters.get().viewY(ptWebMercator);
            Point2D point2DSurEcran = new Point2D(routePointChXEcran, routePointChYEcran);
            if (positionActuelleDeLaSouris.distance(point2DSurEcran) <= 15) {
                this.position.set(positionItineraire);
            } else {
                this.position.set(Double.NaN);
            }

        });

        stackPane.setOnMouseExited(observable -> this.position.set(Double.NaN));

        return this.position;

    }
}

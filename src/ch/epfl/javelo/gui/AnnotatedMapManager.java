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
    private static final MapViewParameters MAP_BASE_LAUSANNE = new MapViewParameters
            (12, 543200, 370650);
    private static final int DISTANCE_PROXIMITE_SOURIS = 15;
    private final StackPane stackPane;
    private final DoubleProperty position;
    private final ObjectProperty<Point2D> positionSouris;
    private final ObjectProperty<MapViewParameters> mapViewParameters = new SimpleObjectProperty<>();

    /**
     * Constructeur de AnnotatedMapManager
     *
     * @param graph         Le graph de JaVelo.
     * @param tileManager   Le gestionnaire de tuiles.
     * @param routeBean     Le bean qui contient les propriétés relatives à un itinéraire.
     * @param errorConsumer Interface fonctionnelle qui est un consommateur d'erreur.
     */
    public AnnotatedMapManager(Graph graph, TileManager tileManager,
                               RouteBean routeBean, Consumer<String> errorConsumer) {

        this.mapViewParameters.set(MAP_BASE_LAUSANNE);
        WaypointsManager waypointsManager = new WaypointsManager(
                graph,
                mapViewParameters,
                routeBean.WaypointsListProperty(),
                errorConsumer);
        BaseMapManager baseMapManager = new BaseMapManager(
                tileManager,
                waypointsManager,
                mapViewParameters);
        RouteManager routeManager = new RouteManager(routeBean, mapViewParameters);

        Pane mapPane = baseMapManager.pane();
        Pane routePane = routeManager.pane();
        Pane waypointsPane = waypointsManager.pane();

        this.stackPane = new StackPane(mapPane, routePane, waypointsPane);
        this.stackPane.getStylesheets().add("map.css");
        this.position = new SimpleDoubleProperty(Double.NaN);
        this.positionSouris = new SimpleObjectProperty<>();


        stackPane.setOnMouseMoved(event -> {

            Point2D positionSouris = new Point2D(event.getX(), event.getY());
            this.positionSouris.set(positionSouris);

            if (routeBean.route() != null) {
                double xEnWebMercator = positionSouris.getX();
                double yEnWebMercator = positionSouris.getY();


                PointCh pointChSouris = this.mapViewParameters.get()
                            .pointAt2(xEnWebMercator, yEnWebMercator)
                            .toPointCh();

                if (pointChSouris != null) {
                    //Problème ici, lance parfois des exceptions pour une cause incompréensible...
                    try {
                        RoutePoint sourisItineraire = routeBean.route().pointClosestTo(pointChSouris);
                        double positionItineraire = sourisItineraire.position();
                        PointCh routePointCh = sourisItineraire.point();
                        PointWebMercator ptWebMercator = PointWebMercator.ofPointCh(routePointCh);
                        double routePointChXEcran = this.mapViewParameters.get().viewX(ptWebMercator);
                        double routePointChYEcran = this.mapViewParameters.get().viewY(ptWebMercator);
                        Point2D point2DSurEcran = new Point2D(routePointChXEcran, routePointChYEcran);

                        if (positionSouris.distance(point2DSurEcran) <= DISTANCE_PROXIMITE_SOURIS) {
                            this.position.set(positionItineraire);
                        } else {
                            this.position.set(Double.NaN);
                        }
                    } catch (IllegalArgumentException ignored){
                    }

                } else {
                    this.position.set(Double.NaN);
                }
            }
        });

            stackPane.setOnMouseExited(observable -> this.position.set(Double.NaN));
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
     * le long de l'itinéraire, distance donnée en mètres.
     *
     * @return La propriété contenant la position du pointeur de la souris le long de l'itinéraire.
     */
    public DoubleProperty mousePositionOnRouteProperty() {
        return this.position;
    }
}
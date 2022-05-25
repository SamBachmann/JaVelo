package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.RoutePoint;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private BaseMapManager baseMapManager;
    private WaypointsManager waypointsManager;
    private RouteManager routeManager;
    private StackPane stackPane;
    private DoubleProperty position;
    private ObjectProperty<Point2D> positionActuelleSouris;
    MapViewParameters mapViewParameters = new MapViewParameters(12, 543200, 370650);

    private RouteBean routeBean;

    public AnnotatedMapManager(Graph graph, TileManager tileManager,
                               RouteBean routeBean, Consumer<String> errorConsumer) {


        ObjectProperty<MapViewParameters> newMapViewParameters = new SimpleObjectProperty<>(this.mapViewParameters);

        this.baseMapManager = new BaseMapManager(tileManager, waypointsManager, newMapViewParameters);
        this.waypointsManager = new WaypointsManager(graph, newMapViewParameters, routeBean.WaypointsListProperty(), errorConsumer);
        this.routeManager = new RouteManager(routeBean, newMapViewParameters, errorConsumer);

        Pane premierPane = this.baseMapManager.pane();
        Pane deuxiemePane = this.routeManager.pane();
        Pane troisiemePane = this.waypointsManager.pane();

        this.stackPane = new StackPane(premierPane, deuxiemePane, troisiemePane);
        this.stackPane.getStylesheets().add("map.css");

        this.routeBean = routeBean;
    }

    public Pane pane() {
        return this.stackPane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {

        stackPane.setOnMouseMoved(event -> {

            Point2D positionActuelleDeLaSouris = new Point2D(event.getX(), event.getY());
            this.positionActuelleSouris.set(positionActuelleDeLaSouris);

            double xEnWebMercator = positionActuelleDeLaSouris.getX();
            double yEnWebMercator = positionActuelleDeLaSouris.getY();

            PointCh pointCh = this.mapViewParameters.pointAt2(xEnWebMercator, yEnWebMercator).toPointCh();
            double positionItineraire = this.routeBean.route().pointClosestTo(pointCh).position();
            this.position = new SimpleDoubleProperty(positionItineraire);

        });

        stackPane.setOnMouseExited(observable -> this.position.set(Double.NaN));

        return this.position;

    }
}

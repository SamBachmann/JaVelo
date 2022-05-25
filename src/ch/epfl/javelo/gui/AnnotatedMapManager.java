package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.Consumer;

public final class AnnotatedMapManager {

    private final BaseMapManager baseMapManager;
    private final WaypointsManager waypointsManager;
    private final RouteManager routeManager;
    private final StackPane stackPane;
    private final DoubleProperty position;
    private final ObjectProperty<Point2D> positionActuelleSouris;
    private final ObjectProperty<MapViewParameters> mapViewParameters = new SimpleObjectProperty<>();
    private final RouteBean routeBean;

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

    public Pane pane() {
        return this.stackPane;
    }

    public DoubleProperty mousePositionOnRouteProperty() {

        stackPane.setOnMouseMoved(event -> {

            Point2D positionActuelleDeLaSouris = new Point2D(event.getX(), event.getY());
            this.positionActuelleSouris.set(positionActuelleDeLaSouris);

            double xEnWebMercator = positionActuelleDeLaSouris.getX();
            double yEnWebMercator = positionActuelleDeLaSouris.getY();

            PointCh pointCh = this.mapViewParameters.get().pointAt2(xEnWebMercator, yEnWebMercator).toPointCh();
            double positionItineraire = this.routeBean.route().pointClosestTo(pointCh).position();
            this.position.set(positionItineraire);

        });

        stackPane.setOnMouseExited(observable -> this.position.set(Double.NaN));

        return this.position;

    }
}

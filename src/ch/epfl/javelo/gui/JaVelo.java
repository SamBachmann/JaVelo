package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.*;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    public static void main(String[] args) { launch(args); }

    //private SplitPane splitPane = new SplitPane();

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        CostFunction costFunction = new CityBikeCF(graph);

        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        ErrorManager errorManager = new ErrorManager();
        Consumer<String> errorConsumer = new ErrorConsumer();

        RouteComputer routeComputer = new RouteComputer(graph,costFunction);
        RouteBean routeBean = new RouteBean(routeComputer);

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,
                tileManager,
                routeBean,
                errorConsumer);

        SplitPane splitPane;

        if (annotatedMapManager.mousePositionOnRouteProperty().get() != Double.NaN) {
            routeBean.highlightedPositionProperty().bind(annotatedMapManager.mousePositionOnRouteProperty());
            System.out.println("PAS NAN");
        }

        if (routeBean.WaypointsListProperty().size() >= 2) {
            System.out.println("liste de waypoint superieure a 2");

            int startNode = routeBean.WaypointsListProperty().get(0).nodeId();
            int endNode = routeBean.WaypointsListProperty().get(routeBean.WaypointsListProperty().size() - 1).nodeId();

        Route route = routeComputer
                .bestRouteBetween(startNode, endNode);
        ElevationProfile profile = ElevationProfileComputer
                .elevationProfile(route, 5);

        ObjectProperty<ElevationProfile> profileProperty =
                new SimpleObjectProperty<>(profile);
        DoubleProperty highlightProperty =
                new SimpleDoubleProperty(1500);

        ElevationProfileManager profileManager =
                new ElevationProfileManager(profileProperty,
                        highlightProperty);

        highlightProperty.bind(
                profileManager.mousePositionOnProfileProperty());


        splitPane = new SplitPane(annotatedMapManager.pane(), profileManager.pane());
        splitPane.setOrientation(Orientation.VERTICAL);
        } else {
            splitPane = new SplitPane(annotatedMapManager.pane());
        }

        //BorderPane contenant le splitPane au centre et la barre de menu en haut.
        //BorderPane fenetreJaVelo = new BorderPane();

        //MenuBar

        Scene scene = new Scene(splitPane);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JaVelo");
        primaryStage.show();

    }

    private static final class ErrorConsumer
            implements Consumer<String> {
        @Override
        public void accept(String s) {
            ErrorManager errorManager = new ErrorManager();
            errorManager.displayError(s);
        }
    }
}

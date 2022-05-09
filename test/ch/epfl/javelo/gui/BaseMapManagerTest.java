
package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class BaseMapManagerTest extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";


        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        Graph graph = Graph.loadFrom(Path.of("lausanne"));
        MapViewParameters mapViewParameters = new MapViewParameters(10, 135735, 92327);
        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);
        ObservableList<Waypoint> waypoints =
                FXCollections.observableArrayList(
                        new Waypoint(new PointCh(2532697, 1152350), 159049),
                        new Waypoint(new PointCh(2538659, 1154350), 117669));
        Consumer<String> errorConsumer = new ErrorConsumer();

        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParametersP, waypoints, errorConsumer);

        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        StackPane mainPane = new StackPane(baseMapManager.pane());

        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();

    }
    private static final class ErrorConsumer implements Consumer<String> {
        @Override
        public void accept(String s) { System.out.println(s); }
    }
}


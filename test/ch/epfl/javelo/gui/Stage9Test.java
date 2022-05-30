package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
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

public final class Stage9Test extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("ch_west"));
        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        CostFunction costFunction = new CityBikeCF(graph);

        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters = new MapViewParameters(12, 543200, 370650);

        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);

        ObservableList<Waypoint> waypoints =
                FXCollections.observableArrayList();


        Consumer<String> errorConsumer = new ErrorConsumer();
        ErrorManager errorManager = new ErrorManager();
        RouteComputer routeComputer = new RouteComputer(graph,costFunction);

        RouteBean routeBean = new RouteBean(routeComputer);

        WaypointsManager waypointsManager = new WaypointsManager(graph, mapViewParametersP, routeBean.WaypointsListProperty(), errorConsumer);

        BaseMapManager baseMapManager = new BaseMapManager(tileManager, waypointsManager, mapViewParametersP);

        RouteManager routeManager = new RouteManager(routeBean,mapViewParametersP);

        StackPane mainPane = new StackPane(baseMapManager.pane(), waypointsManager.pane(), routeManager.pane());



        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
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



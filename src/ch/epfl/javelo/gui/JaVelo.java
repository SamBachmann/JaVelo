package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
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

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        //primaryStage.setScene(new Scene());
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

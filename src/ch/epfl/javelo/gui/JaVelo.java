package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.routing.CityBikeCF;
import ch.epfl.javelo.routing.CostFunction;
import ch.epfl.javelo.routing.GpxGenerator;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    private static final int PREF_WIDTH = 800;
    private static final int PREF_HEIGHT = 600;

    public static void main(String[] args) { launch(args); }

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
        DoubleProperty highlightProperty = new SimpleDoubleProperty(1500);

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,
                tileManager,
                routeBean,
                errorConsumer);

        ElevationProfileManager profileManager = new ElevationProfileManager(
                routeBean.elevationProfilProperty(),
                highlightProperty);

        highlightProperty.bind(annotatedMapManager.mousePositionOnRouteProperty().get() > 0 ?
                annotatedMapManager.mousePositionOnRouteProperty() :
                profileManager.mousePositionOnProfileProperty()
        );

        SplitPane carteEtProfil = new SplitPane(annotatedMapManager.pane(), profileManager.pane());
        carteEtProfil.setOrientation(Orientation.VERTICAL);


        //BorderPane contenant le splitPane au centre et la barre de menu en haut.
        BorderPane fenetreJaVelo = new BorderPane();

        //MenuBar
        MenuBar menuBar = new MenuBar();
        Menu menuFichiers = new Menu();
        MenuItem optionExporterGPX = new MenuItem();
        //optionExporterGPX.disableProperty().bind();

        optionExporterGPX.setOnAction(e -> {
            try {
                GpxGenerator.writeGpx("javelo.gpx", routeBean.route(), routeBean.elevationProfile());
            }catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        });

        menuFichiers.setText("Fichier");
        optionExporterGPX.setText("Exporter GPX");

        menuBar.getMenus().add(menuFichiers);
        menuFichiers.getItems().add(optionExporterGPX);

        fenetreJaVelo.setTop(menuBar);
        fenetreJaVelo.setCenter(carteEtProfil);
        primaryStage.setMinWidth(PREF_WIDTH);
        primaryStage.setMinHeight(PREF_HEIGHT);
        primaryStage.setScene(new Scene(fenetreJaVelo));
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

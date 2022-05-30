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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.function.Consumer;

public final class JaVelo extends Application {
    private static final int PREF_WIDTH = 800;
    private static final int PREF_HEIGHT = 600;
    private static final ErrorManager errorManager = new ErrorManager();

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = Graph.loadFrom(Path.of("javelo-data"));
        Path cacheBasePath = Path.of("osm-cache");
        String tileServerHost = "tile.openstreetmap.org";
        CostFunction costFunction = new CityBikeCF(graph);

        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);
        Consumer<String> errorConsumer = new ErrorConsumer();

        RouteComputer routeComputer = new RouteComputer(graph,costFunction);
        RouteBean routeBean = new RouteBean(routeComputer);
        DoubleProperty ligneSurProfil = new SimpleDoubleProperty();

        AnnotatedMapManager annotatedMapManager = new AnnotatedMapManager(graph,
                tileManager,
                routeBean,
                errorConsumer);


        ElevationProfileManager profileManager = new ElevationProfileManager(
                routeBean.elevationProfilProperty(),
                ligneSurProfil);

        routeBean.highlightedPositionProperty().addListener((observable, oldValue, newValue) -> {
            ligneSurProfil.bind(routeBean.highlightedPosition() > 0 ?
                    routeBean.highlightedPositionProperty() :
                    profileManager.mousePositionOnProfileProperty());
        });
        ligneSurProfil.bind(routeBean.highlightedPositionProperty());


        routeBean.highlightedPositionProperty().bind(annotatedMapManager.mousePositionOnRouteProperty());


        SplitPane carteEtProfil = new SplitPane(annotatedMapManager.pane());
        SplitPane.setResizableWithParent(profileManager.pane(), false);
        carteEtProfil.setOrientation(Orientation.VERTICAL);

        routeBean.elevationProfilProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null)
                carteEtProfil.getItems().add(profileManager.pane());
            else if (newValue != null)
                carteEtProfil.getItems().set(1, profileManager.pane());
            else
                carteEtProfil.getItems().remove(profileManager.pane());

        });

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

        Pane errorPane = errorManager.pane();
        StackPane conteneur = new StackPane(carteEtProfil, errorPane);

        //BorderPane contenant le splitPane au centre et la barre de menu en haut.
        BorderPane fenetreJaVelo = new BorderPane();
        fenetreJaVelo.setTop(menuBar);
        fenetreJaVelo.setCenter(conteneur);
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
            JaVelo.errorManager.displayError(s);
        }
    }
}

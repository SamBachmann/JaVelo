/*
package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.nio.file.Path;

public final class BaseMapManagerTest extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {

        Path cacheBasePath = Path.of(".");
        String tileServerHost = "tile.openstreetmap.org";
        TileManager tileManager = new TileManager(cacheBasePath, tileServerHost);

        MapViewParameters mapViewParameters = new MapViewParameters(10, 135735, 92327);
        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);

        BaseMapManager baseMapManager = new BaseMapManager(tileManager, mapViewParametersP);

        StackPane mainPane = new StackPane(baseMapManager.pane());

        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}

*/
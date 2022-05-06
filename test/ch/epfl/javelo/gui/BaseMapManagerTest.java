
package ch.epfl.javelo.gui;

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

        MapViewParameters mapViewParameters = new MapViewParameters(19, 271709, 185429);
        ObjectProperty<MapViewParameters> mapViewParametersP = new SimpleObjectProperty<>(mapViewParameters);

        BaseMapManager baseMapManager = new BaseMapManager(tileManager, mapViewParametersP);

        StackPane mainPane = new StackPane(baseMapManager.pane());

        mainPane.getStylesheets().add("map.css");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }
}

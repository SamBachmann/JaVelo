package ch.epfl.javelo.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;


public final class TileManagerTest extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {

            TileManager tm = new TileManager(
                    Path.of("."), "tile.openstreetmap.org");
            Image tileImage = tm.imageForTileAt(
                    new TileManager.TileId(12, 271726, 185423));
            Platform.exit();
    }
}


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
            for (int i = 0; i < 105; ++i) {
            Image tileImage = tm.imageForTileAt(
                    new TileManager.TileId(19, 271725 + i, 185422));
            }
            Platform.exit();
    }
}


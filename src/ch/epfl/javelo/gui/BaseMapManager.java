package ch.epfl.javelo.gui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

    TileManager tileManager;
    ObjectProperty<MapViewParameters> property;

    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.property = property;

    }

        public Pane pane() {

            Canvas canvas = new Canvas();
            Pane pane = new Pane();

            pane.getChildren().add(canvas);
            canvas.widthProperty().bind(pane.widthProperty());
            canvas.heightProperty().bind(pane.heightProperty());

            int zoom = this.property.get().zoom();
            int indexXhautGauche = (int) this.property.get().xHautGauche();
            int indexYhautGauche = (int) this.property.get().yHautGauche();

            for (int i = 0; i < canvas.heightProperty().intValue(); i = i + 256) {
                for (int j = 0; j < canvas.widthProperty().intValue(); j = j + 256) {
                    TileManager.TileId tileId = new TileManager.TileId(zoom, indexXhautGauche + j, indexYhautGauche + i);
                    try {
                        Image image = this.tileManager.imageForTileAt(tileId);
                        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
                        graphicsContext.drawImage(image, (double) j, (double) i);
                    } catch (IOException e) {
                        System.out.println("IOException");
                    }
                }
            }
            return pane;
        }
}



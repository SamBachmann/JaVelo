package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

    TileManager tileManager;
    ObjectProperty<MapViewParameters> property;
    Pane pane;
    Canvas canvas;
    Image image;

    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.property = property;

        canvas = new Canvas(2560, 1600);
        pane = new Pane();

        pane.getChildren().add(canvas);

        int zoom = this.property.get().zoom();
        int indexXHautGauche = (int) this.property.get().xHautGauche();
        int indexYHautGauche = (int) this.property.get().yHautGauche();

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        for (int i = 0; i < Math2.ceilDiv(canvas.widthProperty().intValue(), 256); ++i) {
            for (int j = 0; j < Math2.ceilDiv(canvas.heightProperty().intValue(), 256); ++j) {
                TileManager.TileId tileId = new TileManager.TileId(zoom, indexXHautGauche + i, indexYHautGauche + j);
                try {
                    if (TileManager.TileId.isValid(tileId.zoom(), tileId.indexX(), tileId.indexY())) {
                        image = this.tileManager.imageForTileAt(tileId);
                        graphicsContext.drawImage(image, i * 256, j * 256);
                    }
                } catch (IOException ignored) {
                }
            }
        }

    }
        public Pane pane() {

            //this.canvas.widthProperty().bind(pane.widthProperty());
            //this.canvas.heightProperty().bind(pane.heightProperty());

            //System.out.println(canvas.heightProperty());
            //System.out.println(canvas.widthProperty());

            return this.pane;
        }
}



package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

   private TileManager tileManager;
   private ObjectProperty<MapViewParameters> property;
   private Pane pane;
   private Canvas canvas;
   private Image image;
   private boolean redrawNeeded;

    public BaseMapManager(TileManager tileManager, ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.property = property;

        // Redimensionnement automatique à écrire.
        //canvas = new Canvas(2560, 1600);
        canvas = new Canvas();
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

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        //Rajouter la condition sur les dimensions du canvas.
        if (this.property.get().zoom() != property.get().zoom() || this.property.get().xHautGauche() != property.get().xHautGauche()
            || this.property.get().yHautGauche() != property.get().yHautGauche()) {
            redrawOnNextPulse();
        }

    }
        public Pane pane() {

            //this.canvas.widthProperty().bind(pane.widthProperty());
            //this.canvas.heightProperty().bind(pane.heightProperty());

            //System.out.println(canvas.heightProperty());
            //System.out.println(canvas.widthProperty());

            return this.pane;
        }

        private void redrawIfNeeded() {
            if (!redrawNeeded) return;
            redrawNeeded = false;
        }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}



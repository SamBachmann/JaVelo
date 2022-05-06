package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

   private final TileManager tileManager;
   private final ObjectProperty<MapViewParameters> property;
   private final Pane pane;
   private final Canvas canvas;
   private boolean redrawNeeded;

    public BaseMapManager(TileManager tileManager, /*WaypointsManager waypointsManager,*/ ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.property = property;

        // Redimensionnement automatique à écrire.
        //canvas = new Canvas(1200, 700);
        canvas = new Canvas();
        pane = new Pane();

        this.canvas.widthProperty().bind(pane.widthProperty());
        this.canvas.heightProperty().bind(pane.heightProperty());


        pane.heightProperty().addListener(observable -> redrawOnNextPulse());
        pane.widthProperty().addListener(observable -> redrawOnNextPulse());
        pane.getChildren().add(canvas);


        dessinCarte();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        pane.setPickOnBounds(false);

        pane.setOnScroll(event -> {

            int zoom2 = this.property.get().zoom();
            zoom2 = Math2.clamp(9, zoom2, 11);
            if (event.getDeltaY() > 0) {
                zoom2 = Math2.clamp(9, zoom2, 11);
                zoom2 = zoom2 + 1;
                zoom2 = Math2.clamp(9, zoom2, 11);
                System.out.println(zoom2);
            } else {
                if (event.getDeltaY() < 0) {
                    zoom2 = Math2.clamp(9, zoom2, 11);
                    zoom2 = zoom2 - 1;
                    zoom2 = Math2.clamp(9, zoom2, 11);
                    System.out.println(zoom2);
                }
            }
            //int zoom2 = (int) Math.round(this.property.get().zoom() + event.getDeltaY());

            MapViewParameters newMapViewParameters = new MapViewParameters(zoom2, this.property.get().xHautGauche(),
                    this.property.get().yHautGauche());
            this.property.addListener(observable -> redrawOnNextPulse());
            this.property.set(newMapViewParameters);
        } );

    }

    private void dessinCarte() {
        int zoom = this.property.get().zoom();
        int indexXHautGauche = (int) this.property.get().xHautGauche();
        int indexYHautGauche = (int) this.property.get().yHautGauche();
        int indexXTuileHautGauche = Math.floorDiv(indexXHautGauche, 256);
        int indexYTuileHautGauche = Math.floorDiv(indexYHautGauche, 256);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        int nombreDeTuilesEnX = Math2.ceilDiv((int) canvas.getWidth(), 256);
        int nombreDeTuilesEnY = Math2.ceilDiv((int) canvas.getHeight(), 256);

        if (this.property.get().xHautGauche() - indexXTuileHautGauche * 256 > 0) {
            nombreDeTuilesEnX = Math2.ceilDiv((int) canvas.getWidth(), 256) + 1;
        }

        if (this.property.get().yHautGauche() - indexYTuileHautGauche * 256 > 0) {
            nombreDeTuilesEnY = Math2.ceilDiv((int) canvas.getHeight(), 256) + 1;
        }

        for (int i = 0; i < nombreDeTuilesEnX; ++i) {
            for (int j = 0; j < nombreDeTuilesEnY; ++j) {
                TileManager.TileId tileId = new TileManager.TileId(zoom, indexXTuileHautGauche + i,
                        indexYTuileHautGauche + j);
                try {
                    if (TileManager.TileId.isValid(tileId.zoom(), tileId.indexX(), tileId.indexY())) {
                        Image image = this.tileManager.imageForTileAt(tileId);
                        double departX = this.property.get().xHautGauche() - (indexXTuileHautGauche) * 256;
                        double departY = this.property.get().yHautGauche() - (indexYTuileHautGauche) * 256;
                        graphicsContext.drawImage(image,
                                i * 256 - departX, j * 256 - departY);
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    public Pane pane() {
        return this.pane;
    }

    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        dessinCarte();
    }

    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}



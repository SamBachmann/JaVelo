package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.IOException;

public final class BaseMapManager {

   private final TileManager tileManager;
   private ObjectProperty<MapViewParameters> property;
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

        pane.setOnScroll(event -> {
            int zoom2 = (int) Math.round(this.property.get().zoom() + event.getDeltaY());
            zoom2 = Math2.clamp(8, zoom2, 19);
            MapViewParameters newMapViewParameters = new MapViewParameters(zoom2, this.property.get().xHautGauche(),
                    this.property.get().yHautGauche());
            this.property.set(newMapViewParameters);
            System.out.println(zoom2);
        } );

    }

    private void dessinCarte() {
        int zoom = this.property.get().zoom();
        int indexXHautGauche = (int) this.property.get().xHautGauche();
        int indexYHautGauche = (int) this.property.get().yHautGauche();
        PointWebMercator pointWebMercator = PointWebMercator.of(zoom, indexXHautGauche, indexYHautGauche);
        int indexXTuileHautGauche = (int) Math.floor(Math.scalb(pointWebMercator.x(), zoom));
        int indexYTuileHautGauche = (int) Math.floor(Math.scalb(pointWebMercator.y(), zoom));

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        for (int i = 0; i < Math2.ceilDiv((int) canvas.getWidth(), 256); ++i) {
            for (int j = 0; j < Math2.ceilDiv((int) canvas.getHeight(), 256); ++j) {
                TileManager.TileId tileId = new TileManager.TileId(zoom, indexXTuileHautGauche + i,
                        indexYTuileHautGauche + j);
                try {
                    if (TileManager.TileId.isValid(tileId.zoom(), tileId.indexX(), tileId.indexY())) {
                        Image image = this.tileManager.imageForTileAt(tileId);
                        graphicsContext.drawImage(image, i * 256, j * 256);
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



package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import java.io.IOException;

/**
 * Classe représentant un gestionnaire du fond de carte.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 08/05/2022
 */
public final class BaseMapManager {

   private final TileManager tileManager;
   private final static int ZOOM_MIN_VALUE = 8;
   private final ObjectProperty<MapViewParameters> property;
   private final Pane pane;
   private final Canvas canvas;
   private boolean redrawNeeded;
   private final static int ZOOM_MAX_VALUE = 19;
   private final WaypointsManager waypointsManager;


    /**
     * Constructeur du gestionnaire du fond de carte.
     *
     * @param tileManager Un gestionnaire de tuiles.
     * @param waypointsManager Un gestionnaire des points de passage.
     * @param property Des propriétés de type MapViewParameters.
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager, ObjectProperty<MapViewParameters> property) {

        this.tileManager = tileManager;
        this.waypointsManager = waypointsManager;
        this.property = property;

        // Redimensionnement automatique à écrire.
        //canvas = new Canvas(1200, 700);
        canvas = new Canvas();
        pane = new Pane();

        this.canvas.widthProperty().bind(pane.widthProperty());
        this.canvas.heightProperty().bind(pane.heightProperty());

        //Détecter les changements de taille de la fenêtre et redessiner
        pane.heightProperty().addListener(observable -> redrawOnNextPulse());
        pane.widthProperty().addListener(observable -> redrawOnNextPulse());
        pane.getChildren().add(canvas);


        dessinCarte();

        canvas.sceneProperty().addListener((p, oldS, newS) -> {
            assert oldS == null;
            newS.addPreLayoutPulseListener(this::redrawIfNeeded);
        });

        this.property.addListener(observable -> redrawOnNextPulse());
        //pane.setPickOnBounds(false);

        //interaction du zoom
        pane.setOnScroll(event -> {

            int zoom = this.property.get().zoom();
            int zoom2 = Math2.clamp(ZOOM_MIN_VALUE, zoom, ZOOM_MAX_VALUE);
            if (event.getDeltaY() > 0) {
                zoom2 = Math2.clamp(ZOOM_MIN_VALUE, zoom2, ZOOM_MAX_VALUE);
                zoom2 = zoom2 + 1;
                zoom2 = Math2.clamp(ZOOM_MIN_VALUE, zoom2, ZOOM_MAX_VALUE);
                zoom2 = Math2.clamp(9, zoom2 + 1, 11);
                double newXHautGauche = this.property.get().xHautGauche() * 2;
                double newYHautGauche = this.property.get().yHautGauche() * 2;
                MapViewParameters newOne = new MapViewParameters(zoom2, newXHautGauche, newYHautGauche);
                this.property.set(newOne);
                redrawOnNextPulse();

                System.out.println(zoom2);
            } else {
                if (event.getDeltaY() < 0) {
                    zoom2 = Math2.clamp(ZOOM_MIN_VALUE, zoom2, ZOOM_MAX_VALUE);
                    zoom2 = zoom2 - 1;
                    zoom2 = Math2.clamp(ZOOM_MIN_VALUE, zoom2, ZOOM_MAX_VALUE);
                    zoom2 = Math2.clamp(9, zoom2 - 1, 11);
                    System.out.println(zoom2);
                }
            }
            //int zoom2 = (int) Math.round(this.property.get().zoom() + event.getDeltaY());

            int deltaZoom = zoom2 - zoom;

            PointWebMercator pointclic = property.get().pointAt2(event.getSceneX(), event.getSceneY());
            double decalageX = pointclic.x() - property.get().xHautGauche();
            double decalageY = pointclic.y() - property.get().yHautGauche();

            double newXOrigine = pointclic.x()  - Math.scalb(decalageX, deltaZoom);
            double newYOrigine = pointclic.y()  - Math.scalb(decalageY, deltaZoom);

            MapViewParameters newMapViewParameters = new MapViewParameters(zoom2, newXOrigine, newYOrigine);
            this.property.set(newMapViewParameters);
        } );

        dessinCarte();

    }


    /**
     * Méthode privée qui permet de dessiner la carte.
     */
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

    /**
     * Méthode nous donnant le panneau qui va contenir le canvas et la carte.
     *
     * @return Le panneau qui va contenir la carte.
     */
    public Pane pane() {
        return this.pane;
    }

    /**
     * Méthode redessinant la carte si nécessaire.
     */
    private void redrawIfNeeded() {
        if (!redrawNeeded) return;
        redrawNeeded = false;
        dessinCarte();
    }

    /**
     * Méthode permettant de demander un redessin au prochain battement.
     */
    private void redrawOnNextPulse() {
        redrawNeeded = true;
        Platform.requestNextPulse();
    }
}



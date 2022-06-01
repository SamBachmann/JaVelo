package ch.epfl.javelo.gui;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
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

    private static final int TAILLE_TUILE = 256;
    private final static int ZOOM_MIN_VALUE = 8;
    private final static int ZOOM_MAX_VALUE = 19;
    private static final int DELTA_AJOUT_TUILE = 1;

    private final TileManager tileManager;
    private final ObjectProperty<MapViewParameters> parametresCarte;
    private final ObjectProperty<Point2D> pointBaseDrag = new SimpleObjectProperty<>();
    private final Pane pane;
    private final Canvas canvas;
    private boolean redrawNeeded;

    /**
     * Constructeur du gestionnaire du fond de carte.
     *
     * @param tileManager Un gestionnaire de tuiles.
     * @param waypointsManager Un gestionnaire des points de passage.
     * @param parametresCarte Des propriétés de type MapViewParameters.
     */
    public BaseMapManager(TileManager tileManager, WaypointsManager waypointsManager,
                          ObjectProperty<MapViewParameters> parametresCarte) {

        this.tileManager = tileManager;
        this.parametresCarte = parametresCarte;

        this.canvas = new Canvas();
        this.pane = new Pane();

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

        this.parametresCarte.addListener(observable -> redrawOnNextPulse());

        //interaction du zoom
        SimpleLongProperty minScrollTime = new SimpleLongProperty();

        pane.setOnScroll(event -> {

            int zoom = this.parametresCarte.get().zoom();

            if (event.getDeltaY() == 0d) return;
            long currentTime = System.currentTimeMillis();
            if (currentTime < minScrollTime.get()) return;
            minScrollTime.set(currentTime + 200);
            int zoomDelta = (int) Math.signum(event.getDeltaY());

            int newZoom = zoom + zoomDelta;

            if (newZoom >= ZOOM_MIN_VALUE && newZoom <= ZOOM_MAX_VALUE) {
                PointWebMercator pointClic = parametresCarte.get().pointAt2(event.getX(), event.getY());
                double decalageX = pointClic.xAtZoomLevel(zoom) - parametresCarte.get().xHautGauche();
                double decalageY = pointClic.yAtZoomLevel(zoom) - parametresCarte.get().yHautGauche();

                double newX = pointClic.xAtZoomLevel(zoom) - Math.scalb(decalageX, -zoomDelta);
                double newY = pointClic.yAtZoomLevel(zoom) - Math.scalb(decalageY, -zoomDelta);

                double newXZoom = Math.scalb(newX, zoomDelta);
                double newYZoom = Math.scalb(newY, zoomDelta);

                MapViewParameters newMapViewParameters = new MapViewParameters(newZoom, newXZoom, newYZoom);
                this.parametresCarte.set(newMapViewParameters);
            }

        } );

        dessinCarte();


        pane.setOnMousePressed(event -> {
            Point2D point2D = point2DPositionSouris(event);
            pointBaseDrag.set(point2D);
            //Point2D positionSourisAvant = new Point2D(event.getX(), event.getY());
        });

        pane.setOnMouseDragged(event -> {

            Point2D point2DSouris = point2DPositionSouris(event);
            Point2D difference =  point2DSouris.subtract(pointBaseDrag.get());
            pointBaseDrag.set(point2DSouris);
            Point2D newTopLeft = parametresCarte.get().topLeft().subtract(difference);
            MapViewParameters newParameters = this.parametresCarte.get()
                    .withMinXY(newTopLeft.getX(),newTopLeft.getY());
            this.parametresCarte.set(newParameters);
        });

        pane.setOnMouseClicked(event -> {
            if (event.isStillSincePress()){
                waypointsManager.addWaypoint(event.getX(), event.getY());
            }
        });
    }


    /**
     * Méthode privée qui permet de dessiner la carte.
     */
    private void dessinCarte() {
        int zoom = this.parametresCarte.get().zoom();
        int indexXHautGauche = (int) this.parametresCarte.get().xHautGauche();
        int indexYHautGauche = (int) this.parametresCarte.get().yHautGauche();
        int indexXTuileHautGauche = Math.floorDiv(indexXHautGauche, TAILLE_TUILE);
        int indexYTuileHautGauche = Math.floorDiv(indexYHautGauche, TAILLE_TUILE);

        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        int nombreDeTuilesEnX = Math2.ceilDiv((int) canvas.getWidth(), TAILLE_TUILE);
        int nombreDeTuilesEnY = Math2.ceilDiv((int) canvas.getHeight(), TAILLE_TUILE);

        if (this.parametresCarte.get().xHautGauche() - indexXTuileHautGauche * TAILLE_TUILE > 0) {
            nombreDeTuilesEnX = Math2.ceilDiv((int) canvas.getWidth(), TAILLE_TUILE) + DELTA_AJOUT_TUILE;
        }

        if (this.parametresCarte.get().yHautGauche() - indexYTuileHautGauche * TAILLE_TUILE > 0) {
            nombreDeTuilesEnY = Math2.ceilDiv((int) canvas.getHeight(), TAILLE_TUILE) + DELTA_AJOUT_TUILE;
        }

        for (int x = 0; x < nombreDeTuilesEnX; ++x) {
            for (int y = 0; y < nombreDeTuilesEnY; ++y) {
                TileManager.TileId tileId = new TileManager.TileId(zoom, indexXTuileHautGauche + x,
                        indexYTuileHautGauche + y);
                try {
                    if (TileManager.TileId.isValid(tileId.zoom(), tileId.indexX(), tileId.indexY())) {
                        Image image = this.tileManager.imageForTileAt(tileId);
                        double departX = this.parametresCarte.get().xHautGauche() - indexXTuileHautGauche*TAILLE_TUILE;
                        double departY = this.parametresCarte.get().yHautGauche() - indexYTuileHautGauche*TAILLE_TUILE;
                        graphicsContext.drawImage(image,
                                x * TAILLE_TUILE - departX, y * TAILLE_TUILE - departY);
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

    private Point2D point2DPositionSouris(MouseEvent event) {
        return new Point2D(event.getX(), event.getY());
    }

}




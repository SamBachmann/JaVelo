package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe gérant l'affichage et les interactions avec le profil en long d'un itinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 14/05/2022
 */
public final class ElevationProfileManager {

    private final BorderPane borderPane;
    private final Pane pane;
    private final ObjectProperty<Transform> screenToWorld = new SimpleObjectProperty<>();
    private final ObjectProperty<Transform> worldToScreen = new SimpleObjectProperty<>();
    private final ObjectProperty<Rectangle2D> rectangleBleu = new SimpleObjectProperty<>();
    private final Polygon dessinProfil;

    /**
     * Constructeur d'ElevationProfileManager
     *
     * @param profil         Profil de l'itinéraire.
     * @param positionProfil Position en évidence.
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profil,
                                   ReadOnlyDoubleProperty positionProfil) {
        this.pane = new Pane();
        this.borderPane = new BorderPane();

        //Définir des dimentions par défauts si elles ne sont pas définies au démarrage
        pane.setPrefWidth(600);
        pane.setPrefHeight(300);

        this.borderPane.getStylesheets().add("elevation_profile.css");
        this.borderPane.setCenter(this.pane);

        this.dessinProfil = new Polygon();
        initHierarchie();

        Insets insets = new Insets(10, 10, 20, 40);

        dessineProfil(profil, insets);

        Rectangle2D rectangle2D = new Rectangle2D(insets.getLeft(), insets.getTop(),
                Math.max(pane.getWidth() - insets.getRight() - insets.getLeft(), 0),
                Math.max(pane.getHeight() - insets.getTop() - insets.getBottom(), 0));
        this.rectangleBleu.set(rectangle2D);

       pane.widthProperty().addListener(observable -> dessineProfil(profil, insets));

        //Bindings.createObjectBinding() pour lier la ligne à la zone rectangle bleu
        //this.borderPane.layoutXProperty().bind(Bindings.createDoubleBinding());
        //this.borderPane.minp
        //this.borderPane.layoutXProperty().bind(borderPane.layoutXProperty());
    }


    /**
     * Méthode privée qui dessine le profil à partir d'un ElevationProfile et des
     * décalages du profil sur sa fenêtre.
     *
     * @param profil Le profil à dessiner.
     * @param insets Les marges latérales et verticales autour du profil.
     */
    private void dessineProfil(ReadOnlyObjectProperty<ElevationProfile> profil, Insets insets) {
        //Point bas gauche de l'affichage du profil
        Point2D p1 = new Point2D(insets.getLeft(), Math.max(pane.getHeight(), 300) - insets.getBottom());
        //Point haut droite de l'affichage du profil
        Point2D p2 =  new Point2D(Math.max(pane.getWidth(), 600) - insets.getRight(), insets.getTop());

        double deltaYworld = profil.get().maxElevation() - profil.get().minElevation();
        double deltaXworld = profil.get().length();

        double coeffX = deltaXworld / (p2.getX() - p1.getX());
        double coeffY = deltaYworld / (p2.getY() - p1.getY());

        //Fonctions de transformations écran-réalité
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation(- p1.getX(), - p2.getY());
        screenToWorld.prependScale(coeffX, coeffY);
        screenToWorld.prependTranslation(0, profil.get().maxElevation());
        this.screenToWorld.set(screenToWorld);

        try {
            Affine worldToScreen = screenToWorld.createInverse();
            this.worldToScreen.set(worldToScreen);
        } catch (NonInvertibleTransformException e) {
            throw new Error();
        }

        List<Double> listeDePoints = new ArrayList<>(List.of(p2.getX(), p1.getY(), p1.getX(), p1.getY()));

        // parcourir tous les pixels
        for (int x = (int) p1.getX(); x <= p2.getX(); ++x ){
            double xItineraire = screenToWorld.transform(x, 0).getX();
            double elevationAtx = profil.get().elevationAt(xItineraire);

            Point2D pointAffiche = worldToScreen.get().transform(xItineraire, elevationAtx);
            System.out.printf("Altitude : %f Y écran : %f  \n", elevationAtx, pointAffiche.getY());
            listeDePoints.add(pointAffiche.getX());
            listeDePoints.add(pointAffiche.getY());
        }

        dessinProfil.getPoints().setAll(listeDePoints);


        Path path = new Path();

        double deltaElevation = worldToScreen.get().deltaTransform(0, profil.get().maxElevation() - profil.get().minElevation()).getY();

        int[] POS_STEPS =
                { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

        int ecartAltitude = ELE_STEPS[9];

        for (Integer step : ELE_STEPS) {
            if ((deltaElevation / step) >= 25) {
               ecartAltitude = step;
               break;
            }
        }

        double y = profil.get().minElevation();
        while (y < profil.get().maxElevation()) {
            y = y + ecartAltitude;
            PathElement ligneextremite1 = new MoveTo(0,y);
            PathElement ligneextremite2 = new LineTo(p2.getX(), y);
        }

    }


    /**
     * Méthode privée appelée dans le constructeur qui crée la hiérarchie javaFX des éléments.
     */
    private void initHierarchie() {
        VBox vBox = new VBox();
        vBox.setId("profile_data");
        this.borderPane.setBottom(vBox);

        Text textVBox = new Text();
        vBox.getChildren().add(textVBox);

        Path grille = new Path();
        this.pane.getChildren().add(grille);
        grille.setId("grid");

        Group group = new Group();
        this.pane.getChildren().add(group);

        Text text1 = new Text();
        text1.getStyleClass().add("grid_label");
        text1.getStyleClass().add("horizontal");
        group.getChildren().add(text1);

        Text text2 = new Text();
        text2.getStyleClass().add("grid_label");
        text2.getStyleClass().add("vertical");
        group.getChildren().add(text2);

        dessinProfil.setId("profile");
        this.pane.getChildren().add(dessinProfil);

        Line line = new Line();
        this.pane.getChildren().add(line);

    }

    /**
     * Accesseur du Pane contenant le profil et les infos corespondantes.
     *
     * @return Le Pane du profil
     */
    public Pane pane(){
        return this.borderPane;
    }

    public ReadOnlyIntegerProperty mousePositionOnProfileProperty(){
        return null;
    }


}

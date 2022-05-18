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
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
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

    /**
     * Constructeur d'ElevationProfileManager
     *
     * @param profil Profil de l'itinéraire.
     * @param positionProfil Position en évidence.
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profil,
                                ReadOnlyDoubleProperty positionProfil){
        this.pane = new Pane();
        this.borderPane = new BorderPane();
        borderPane.setPrefWidth(600);
        borderPane.setPrefHeight(300);

        this.borderPane.getStylesheets().add("elevation_profile.css");
        this.borderPane.setCenter(this.pane);

        VBox vBox = new VBox();
        vBox.setId("profile_data");
        Insets insets = new Insets(10, 10, 20, 40);

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

        Polygon dessinProfil = new Polygon();
        dessinProfil.setId("profile");
        this.pane.getChildren().add(dessinProfil);

        Line line = new Line();
        this.pane.getChildren().add(line);


        //Dessiner dans le meme systeme d'axe. Identifier les pts d'extremiter dans les coordonnées
        //Fonctions affines de transformation définies.
        double deltaYworld = profil.get().maxElevation() - profil.get().minElevation();
        double deltaXworld = profil.get().length();

        //Point bas gauche de l'affichage du profil
        Point2D p1 = new Point2D(insets.getLeft(), Math.max(borderPane.getHeight(), 300) - insets.getBottom());
        //Point haut droite de l'affichage du profil
        Point2D p2 =  new Point2D(Math.max(borderPane.getWidth(), 600) - insets.getRight(), insets.getTop());
        double coeffX = deltaXworld / (p2.getX() - p1.getX());
        double coeffY = deltaYworld / (p2.getY() - p1.getY());

        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation(- p1.getX(), insets.getBottom());
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

        // À vérifier et/ou simplifier.
//         Rectangle2D rectangle2D = new Rectangle2D(40, 10, borderPane.getWidth() - insets.getRight() -
//                insets.getLeft(), borderPane.getHeight() - insets.getTop() - insets.getBottom());
//        this.rectangleBleu.set(rectangle2D);

//        Math.max(borderPane.getWidth(), 600);
//        Math.max(borderPane.getHeight(), 300);

    }

    public Pane pane(){
        return this.borderPane;
    }

    public ReadOnlyIntegerProperty mousePositionOnProfileProperty(){
        return null;
    }


}

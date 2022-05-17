package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;


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

        this.borderPane.getStylesheets().add("elevation_profile.css");
        this.borderPane.setCenter(this.pane);

        VBox vBox = new VBox();
        vBox.setId("profile_data");
        Insets insets = new Insets(10, 10, 20, 40);
        double hauteur = insets.getTop() - insets.getBottom();
        double largeur = insets.getRight();

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

        Polygon profile = new Polygon();
        profile.setId("profile");
        this.pane.getChildren().add(profile);

        Line line = new Line();
        this.pane.getChildren().add(line);


        //Dessiner dans le meme systeme d'axe. Identifier les pts d'extremiter dans les coordonnées
         double diffAltitude = profil.get().maxElevation() - profil.get().minElevation();
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation( - insets.getLeft(), - insets.getBottom());
        //screenToWorld.prependScale();
        //screenToWorld.prependTranslation();
        Point2D pt2d = new Point2D(1,2);

    }

    public Pane pane(){
        return this.borderPane;
    }

    public ReadOnlyIntegerProperty mousePositionOnProfileProperty(){
        return null;
    }


}

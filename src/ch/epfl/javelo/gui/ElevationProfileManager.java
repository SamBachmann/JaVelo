package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
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
    private final DoubleProperty mousePositionOnProfileProperty = new SimpleDoubleProperty();
    private final Polygon dessinProfil;
    private final Path grille;
    private final Group textConteneur;


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
        this.borderPane.getStylesheets().add("elevation_profile.css");
        this.borderPane.setCenter(this.pane);

        this.textConteneur = new Group();
        this.pane.getChildren().add(textConteneur);

        this.dessinProfil = new Polygon();
        dessinProfil.setId("profile");
        this.pane.getChildren().add(dessinProfil);

        this.grille = new Path();
        this.pane.getChildren().add(grille);
        grille.setId("grid");

        Line line = new Line();
        this.pane.getChildren().add(line);

        //suite de l'initialisation
        VBox vBox = new VBox();
        vBox.setId("profile_data");
        this.borderPane.setBottom(vBox);

        Text textVBox = new Text();
        vBox.getChildren().add(textVBox);

        Insets insets = new Insets(10, 10, 20, 40);

        //binding
        rectangleBleu.bind(Bindings.createObjectBinding(() ->
                        new Rectangle2D(insets.getLeft(),insets.getTop(),
                                Math.max(pane.getWidth() - insets.getRight() - insets.getLeft(), 0),
                                Math.max(pane.getHeight() - insets.getTop() - insets.getBottom(), 0)),
                pane.widthProperty(),
                pane.heightProperty()));
        dessineProfil(profil,insets);

        rectangleBleu.addListener((observable, oldValue, newValue) -> dessineProfil(profil, insets));


        line.layoutXProperty().bind(Bindings.createDoubleBinding(()->
                worldToScreen.get().transform(positionProfil.get(), 0).getX(),
                positionProfil,
                worldToScreen));

        line.startYProperty().bind(Bindings.select(rectangleBleu, "minY"));
        line.endYProperty().bind(Bindings.select(rectangleBleu, "maxY"));
        line.visibleProperty().bind(positionProfil.greaterThanOrEqualTo(0));

        //affichage des stats:
        String stats = String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                profil.get().length() / 1000,
                profil.get().totalAscent(),
                profil.get().totalDescent(),
                profil.get().minElevation(),
                profil.get().maxElevation());
        textVBox.setText(stats);

        //Interractions entre la souris et le pane
        pane.setOnMouseMoved(event ->{
            double nouvellePosition = (int) screenToWorld.get().transform(event.getX(), 0).getX();
            mousePositionOnProfileProperty.set(nouvellePosition);
            //System.out.println(event.getX());
        } );

        pane.setOnMouseExited(observable -> mousePositionOnProfileProperty.set(Double.NaN));
    }


    /**
     * Méthode privée qui dessine le profil à partir d'un ElevationProfile et des
     * décalages du profil sur sa fenêtre.
     *
     * @param profil Le profil à dessiner.
     * @param insets Les marges latérales et verticales autour du profil.
     */
    private void dessineProfil(ReadOnlyObjectProperty<ElevationProfile> profil, Insets insets) {
        double deltaYworld = profil.get().minElevation() - profil.get().maxElevation();
        double deltaXworld = profil.get().length();

        double coeffX = deltaXworld / rectangleBleu.get().getWidth();
        double coeffY = deltaYworld / (rectangleBleu.get().getHeight());

        //Fonctions de transformations écran-réalité
        Affine screenToWorld = new Affine();
        screenToWorld.prependTranslation(- rectangleBleu.get().getMinX(), - rectangleBleu.get().getMinY());
        screenToWorld.prependScale(coeffX, coeffY);
        screenToWorld.prependTranslation(0, profil.get().maxElevation());
        this.screenToWorld.set(screenToWorld);

        try {
            Affine worldToScreen = screenToWorld.createInverse();
            this.worldToScreen.set(worldToScreen);
        } catch (NonInvertibleTransformException e) {
            throw new Error();
        }

        List<Double> listeDePoints = new ArrayList<>(List.of(rectangleBleu.get().getMaxX(),
                rectangleBleu.get().getMaxY(),
                rectangleBleu.get().getMinX(),
                rectangleBleu.get().getMaxY()));

        // parcourir tous les pixels
        for (int x = (int) rectangleBleu.get().getMinX(); x <= rectangleBleu.get().getMaxX(); ++x ){
            double xItineraire = screenToWorld.transform(x, 0).getX();
            double elevationAtx = profil.get().elevationAt(xItineraire);

            Point2D pointAffiche = worldToScreen.get().transform(xItineraire, elevationAtx);
            listeDePoints.add(pointAffiche.getX());
            listeDePoints.add(pointAffiche.getY());
        }

        dessinProfil.getPoints().setAll(listeDePoints);


        double deltaElevation = profil.get().maxElevation() - profil.get().minElevation();
        double deltaWidth = profil.get().length();

        int[] POS_STEPS =
                { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

        int ecartAltitude = ELE_STEPS[9];
        int ecartColonnes = POS_STEPS[6];

        double valeurminimaleElevation = 25;
        double valeurminimaleColonnes = 50;

        for (Integer step : ELE_STEPS) {

            double nombreDeLignes = Math.ceil(deltaElevation / step);

            double hauteurRectangleBleu = this.pane.getHeight() - insets.getBottom() - insets.getTop();

            double ecartentreligne = Math.ceil(hauteurRectangleBleu) / nombreDeLignes;

            if (ecartentreligne >= valeurminimaleElevation) {
                ecartAltitude = step;
                break;
            }
        }

        for (Integer step : POS_STEPS) {

            double nombreDeColonnes = Math.ceil(deltaWidth / step);

            double largeurRectangleBleu = this.pane.getWidth() - insets.getLeft() - insets.getRight();

            double ecartentreColonnes = Math.ceil(largeurRectangleBleu) / nombreDeColonnes;

            if (ecartentreColonnes >= valeurminimaleColonnes) {
                ecartColonnes = step;
                break;
            }
        }

        double y = Math.ceil(profil.get().minElevation() / ecartAltitude) * ecartAltitude;
        System.out.println(y);
        System.out.println(profil.get().minElevation());
        double x = 0.0;

        grille.getElements().clear();
        textConteneur.getChildren().clear();
        while (y <= profil.get().maxElevation()) {

            double yEnPixels = worldToScreen.get().transform(0, y).getY();

            PathElement ligneExtremite1 = new MoveTo(insets.getLeft(),yEnPixels);
            grille.getElements().add(ligneExtremite1);

            PathElement ligneExtremite2 = new LineTo(rectangleBleu.get().getMaxX(), yEnPixels);
            grille.getElements().add(ligneExtremite2);


            PathElement ligneextremite1 = new MoveTo(insets.getLeft(),yEnPixels);
            grille.getElements().add(ligneextremite1);
            Text text2 = new Text();
            text2.setFont(Font.font("Avenir", 10));
            text2.getStyleClass().add("grid_label");
            text2.getStyleClass().add("vertical");
            text2.setTextOrigin(VPos.CENTER);
            text2.setText(Integer.toString((int)y));

            text2.setLayoutY(yEnPixels);
            text2.setLayoutX(rectangleBleu.get().getMinX() - text2.prefWidth(0) - 2);

            textConteneur.getChildren().add(text2);

            y = y + ecartAltitude;


        }

        while (x <= profil.get().length()) {

            double xEnPixels = worldToScreen.get().transform(x, 0).getX();

            PathElement colonneExtremite1 = new MoveTo(xEnPixels,rectangleBleu.get().getMaxY());
            grille.getElements().add(colonneExtremite1);

            PathElement colonneExtremite2 = new LineTo(xEnPixels, rectangleBleu.get().getMinY());
            grille.getElements().add(colonneExtremite2);

            Text text1 = new Text();
            text1.setFont(Font.font("Avenir", 10));
            text1.getStyleClass().add("grid_label");
            text1.getStyleClass().add("horizontal");
            text1.setTextOrigin(VPos.TOP);

            text1.setLayoutY(rectangleBleu.get().getMaxY());
            text1.setText(Integer.toString((int)x / 1000));
            text1.setLayoutX(xEnPixels - text1.prefWidth(0)/2);
            textConteneur.getChildren().add(text1);


            x = x + ecartColonnes;
        }
    }

    /**
     * Accesseur du Pane contenant le profil et les infos correspondantes.
     *
     * @return Le Pane du profil
     */
    public Pane pane(){
        return this.borderPane;
    }

    /**
     * Accesseur de la position le long du profil de la souris, à l'entier le plus près.
     *
     * @return La position sur le profil.
     */
    public ReadOnlyDoubleProperty mousePositionOnProfileProperty(){
        return (ReadOnlyDoubleProperty) mousePositionOnProfileProperty;
    }


}
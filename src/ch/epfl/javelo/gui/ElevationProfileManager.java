package ch.epfl.javelo.gui;


import ch.epfl.javelo.Math2;
import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
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
    private Line line;
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
        //suite de l'initialisation
        VBox vBox = new VBox();
        vBox.setId("profile_data");
        this.borderPane.setBottom(vBox);

        Text textVBox = new Text();
        vBox.getChildren().add(textVBox);

        Insets insets = new Insets(10, 10, 20, 40);

        dessineProfil(profil, insets);


        pane.widthProperty().addListener(observable -> dessineProfil(profil, insets));

        //binding
        rectangleBleu.bind(Bindings.createObjectBinding(() -> {
            return new Rectangle2D(insets.getLeft(),insets.getTop(),
                    Math.max(pane.getWidth() - insets.getRight() - insets.getLeft(), 0),
                    Math.max(pane.getHeight() - insets.getTop() - insets.getBottom(), 0));
                }, pane.widthProperty(), pane.heightProperty()));

        line.layoutXProperty().bind(Bindings.createDoubleBinding(()-> {
            return worldToScreen.get().transform(positionProfil.get(), 0).getX();
        }, positionProfil));

        line.startYProperty().bind(Bindings.select(rectangleBleu.get(), "minY"));
        line.endYProperty().bind(Bindings.select(rectangleBleu.get(), "maxY"));
        line.visibleProperty().bind(positionProfil.greaterThanOrEqualTo(0));

        //affichage des stats:
        String stats = String.format("Longueur : %.1f km" +
                "     Montée : %.0f m" +
                "     Descente : %.0f m" +
                "     Altitude : de %.0f m à %.0f m",
                profil.get().length(),
                profil.get().totalAscent(),
                profil.get().totalDescent(),
                profil.get().minElevation(),
                profil.get().maxElevation());
        textVBox.setText(stats);
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
            listeDePoints.add(pointAffiche.getX());
            listeDePoints.add(pointAffiche.getY());
        }

        dessinProfil.getPoints().setAll(listeDePoints);

        //double test = worldToScreen.get().transform(0, 287.5596923828125).getY();
        //System.out.println("valeur de 287 en javafx : " + test);

        double deltaElevation = profil.get().maxElevation() - profil.get().minElevation();
        System.out.println("Delta élevation totale : " + deltaElevation);

        int[] POS_STEPS =
                { 1000, 2000, 5000, 10_000, 25_000, 50_000, 100_000 };
        int[] ELE_STEPS =
                { 5, 10, 20, 25, 50, 100, 200, 250, 500, 1_000 };

        int ecartAltitude = ELE_STEPS[9];

        double valeurminimale = 25;
        System.out.println("Valeur minimale : " + valeurminimale);

        for (Integer step : ELE_STEPS) {

            System.out.println("step : " + step);

            double nombreDeLignes = Math2.ceilDiv((int) Math.ceil(deltaElevation), step);
            System.out.println("Nombre de lignes : " + nombreDeLignes);

            double nouvelleDeltaElevation = worldToScreen.get().transform(0, deltaElevation).getY();
            System.out.println("Nouvelle Delta Elevation : " + nouvelleDeltaElevation);

            double ecartentreligne = Math.ceil(nouvelleDeltaElevation) / nombreDeLignes;
            System.out.println("Écart entre lignes : " + ecartentreligne);

            if (ecartentreligne >= valeurminimale) {
                ecartAltitude = step;
                break;
            }
        }
        //System.out.println(ecartAltitude);
        //System.out.println("p1Y : " + p1.getY());
        //System.out.println("p2Y : " + p2.getY());

        System.out.println("y min en reel : " + profil.get().minElevation());
        double y = profil.get().minElevation();

        Path grille = new Path();
        this.pane.getChildren().add(grille);
        grille.setId("grid");

        System.out.println("while : " + worldToScreen.get().transform(0, profil.get().maxElevation()).getY());
        while (y <= profil.get().maxElevation()) {
            y = y + ecartAltitude;

            double yEnPixels = worldToScreen.get().transform(0, y).getY();

            PathElement ligneextremite1 = new MoveTo(insets.getLeft(),yEnPixels);
            System.out.println("Extremite ligne gauche : " + ligneextremite1);
            grille.getElements().add(ligneextremite1);

            PathElement ligneextremite2 = new LineTo(p2.getX(), yEnPixels);
            System.out.println("Extremite ligne droite : " + ligneextremite2);
            grille.getElements().add(ligneextremite2);
        }






        //System.out.println(path.getElements());
    }


    /**
     * Méthode privée appelée dans le constructeur qui crée la hiérarchie javaFX des éléments.
     */
    private void initHierarchie() {


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

        this.line = new Line();
        this.pane.getChildren().add(line);

    }

    /**
     * Accesseur du Pane contenant le profil et les infos correspondantes.
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

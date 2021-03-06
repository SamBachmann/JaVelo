package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.SVGPath;

import java.util.function.Consumer;

/**
 * Classe gérant l'affichage et les interactions des points de passages d'un itinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 26/04/2022
 */
public final class WaypointsManager {
    private final static int DISTANCE_RECHERCHE = 500;
    private final static String SVG_CHEMIN_EXTERIEUR = "M-8-20C-5-14-2-7 0 0 2-7 5-14 8-20 20-40-20-40-8-20";
    private final static String SVG_CHEMIN_INTERIEUR = "M0-23A1 1 0 000-29 1 1 0 000-23";
    private final static String MESSAGE_ERREUR = "Aucune route à proximité !";
    private final static int NO_NODE = -1;

    private final Graph graph;
    private final ObjectProperty<MapViewParameters> parametresCarte;
    private final ObservableList<Waypoint> waypointsList;
    private final Consumer<String> errorHandler;
    private final Pane waypointPane;

    /**
     * Constructeur de WaypointsManager.
     *
     * @param graph Le graphe du réseau JaVelo.
     * @param parametresCarte Les paramètres de la carte affichée.
     * @param waypointsList Une liste observable des Waypoints.
     * @param errorHandler Objet affichant un message en cas d'erreur.
     */
    public WaypointsManager (Graph graph, ObjectProperty<MapViewParameters> parametresCarte,
                             ObservableList<Waypoint> waypointsList, Consumer<String> errorHandler){
        this.graph = graph;
        this.parametresCarte = parametresCarte;
        this.waypointsList = waypointsList;
        this.errorHandler = errorHandler;
        this.waypointPane = new Pane();
        waypointPane.setPickOnBounds(false);

        creationMarqueurs();

        parametresCarte.addListener(observable -> {
            for (int i = 0; i < waypointPane.getChildren().size(); ++i){
                PointWebMercator positionDeplacee = PointWebMercator.ofPointCh(
                        waypointsList.get(i).PointPassage());
                Group marqueur = (Group) waypointPane.getChildren().get(i);
                setPositionMarqueur(positionDeplacee, marqueur);
            }
        });

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> creationMarqueurs());

    }

    /**
     * Méthode retournant le Pane contenant tous les points de passage.
     *
     * @return Pane contenant les points de passages.
     */
    public Pane pane() {
        return waypointPane;
    }


    /**
    * Ajoute un Waypoint à la liste waypointsList, positionné sur le noeud le plus proche du point
    * passé en paramètre.
    *
    * @param x Coordonnée x dans la fenêtre.
    * @param y Coordonnée y dans la fenêtre.
    */
    public void addWaypoint ( double x, double y){
        PointWebMercator pointDonne = parametresCarte.get().pointAt2(x, y);
        PointCh pointDonneEnCH = pointDonne.toPointCh();
        if (pointDonneEnCH != null) {
            int nodeNewWaypoint = graph.nodeClosestTo(pointDonneEnCH, DISTANCE_RECHERCHE);
            if (nodeNewWaypoint == NO_NODE) {
                errorHandler.accept(MESSAGE_ERREUR);
            } else {
                waypointsList.add(new Waypoint(pointDonneEnCH, nodeNewWaypoint));
            }
        } else {
            errorHandler.accept(MESSAGE_ERREUR);
        }
    }
    /**
     * Méthode privée qui crée (ou recrée) les marqueurs sur le Pane à partir de la liste de waypoints
     *
     */
    private void creationMarqueurs() {
        int i = 0;
        waypointPane.getChildren().clear();

        for (Waypoint waypoint : waypointsList) {
            PointWebMercator positionMarqueur = PointWebMercator.ofPointCh(waypoint.PointPassage());
            Group marqueur = createGroup(i);
            setPositionMarqueur(positionMarqueur, marqueur);

            // Déplacer le visuel, mais pas de création de nouveau waypoint avant le release
            marqueur.setOnMouseDragged(event -> {
                PointWebMercator positionActuelle = parametresCarte
                        .get()
                        .pointAt2(event.getSceneX(), event.getSceneY());
                setPositionMarqueur(positionActuelle, marqueur);
            });


            marqueur.setOnMouseReleased(e -> {
                //Cas du clic
                if (e.isStillSincePress()) {
                    waypointsList.remove(waypoint);
                } else {

                    //Cas du relachement de drag
                    PointWebMercator positionPostDrag = parametresCarte.get().pointAt2(e.getSceneX(), e.getSceneY());
                    PointCh pointDonneEnCH = positionPostDrag.toPointCh();
                    if (pointDonneEnCH != null) {
                        int nodeNewWaypoint = graph.nodeClosestTo(pointDonneEnCH, DISTANCE_RECHERCHE);
                        if (nodeNewWaypoint != NO_NODE) {
                            int index = waypointsList.indexOf(waypoint);
                            Waypoint newWaypoint = new Waypoint(pointDonneEnCH, nodeNewWaypoint);

                            waypointsList.set(index, newWaypoint);

                        } else {
                            errorHandler.accept(MESSAGE_ERREUR);
                            creationMarqueurs();
                        }
                    }else {
                        errorHandler.accept(MESSAGE_ERREUR);
                        creationMarqueurs();
                    }
                }

            });
            waypointPane.getChildren().add(marqueur);

            ++i;
        }
    }

    /**
     * Méthode privée qui crée un marqueur (un groupe) à partir de 2 chemins SVG.
     *
     * @param index l'index du marqueur en question.
     * @return Le marqueur créé.
     */
    private Group createGroup (int index){
        SVGPath bordExt = new SVGPath();
        bordExt.setContent(SVG_CHEMIN_EXTERIEUR);
        bordExt.getStyleClass().add("pin_outside");

        SVGPath bordIn = new SVGPath();
        bordIn.setContent(SVG_CHEMIN_INTERIEUR);
        bordIn.getStyleClass().add(" pin_inside");
        Group marqueur = new Group(bordExt, bordIn);

        //Teste la position du marqueur dans la liste
        String position = (index > 0 && index < waypointsList.size() - 1)
                ? "middle" : ((index == 0) ? "first" : "last");
        marqueur.getStyleClass().addAll("pin", position);

        return marqueur;
    }

    /**
     * Méthode privée qui positionne les marqueurs au bon emplacement sur le Pane.
     *
     * @param positionMarqueur La position géographique du marqueur
     * @param marqueur Le Group que l'on veut positionner.
     */
    private void setPositionMarqueur (PointWebMercator positionMarqueur, Group marqueur){
        double xEcran = parametresCarte.get().viewX(positionMarqueur);
        double yEcran = parametresCarte.get().viewY(positionMarqueur);
        marqueur.setLayoutX(xEcran);
        marqueur.setLayoutY(yEcran);
    }

}
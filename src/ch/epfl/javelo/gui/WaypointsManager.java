package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.PointWebMercator;
import javafx.beans.property.ObjectProperty;
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

    private final Graph graph;
    private ObjectProperty<MapViewParameters> parametersCarte;
    private ObservableList<Waypoint> waypointsList;
    private Consumer<String> errorHandler;

    /**
     * Constructeur de WaypointsManager.
     *
     * @param graph Le graphe du réseau JaVelo.
     * @param parametersCarte Les paramètres de la carte affichée.
     * @param waypointsList Une liste observable des Waypoints.
     * @param errorHandler Objet affichant un message en cas d'erreur.
     */
    public WaypointsManager (Graph graph, ObjectProperty<MapViewParameters> parametersCarte,
                             ObservableList<Waypoint> waypointsList, Consumer<String> errorHandler){
        this.graph = graph;
        this.parametersCarte = parametersCarte;
        this.waypointsList = waypointsList;
        this.errorHandler = errorHandler;

    }

    /**
     * Méthode préparant le Pane contenant tous les points de passage.
     *
     * @return Pane contenant les points de passages.
     */
    public Pane pane (){
        Pane waypointPane = new Pane();

        for (int i = 0; i < waypointsList.size(); ++i) {
            SVGPath bordExt = new SVGPath();
            bordExt.setContent(SVG_CHEMIN_EXTERIEUR);
            bordExt.getStyleClass().add("pin_outside");

            SVGPath bordIn = new SVGPath();
            bordIn.setContent(SVG_CHEMIN_INTERIEUR);
            bordIn.getStyleClass().add(" pin_inside");

            Group marqueur = new Group(bordExt, bordIn);

            PointWebMercator positionMarqueur = PointWebMercator.ofPointCh(waypointsList.get(i).PointPassage());

            double xEcran = parametersCarte.get().viewX(positionMarqueur);
            double yEcran = parametersCarte.get().viewY(positionMarqueur);
            marqueur.setLayoutX(xEcran);
            marqueur.setLayoutY(yEcran);

            //Teste la position du marqueur dans la liste
            String position = (i > 0 && i < waypointsList.size()-1) ? "middle" : ((i == 0) ? "first" : "last");
            marqueur.getStyleClass().addAll("pin", position);

            waypointPane.getChildren().add(marqueur);
        }

        return waypointPane;
    }

    /**
     * Ajoute un Waypoint à la liste waypointsList, positionné sur le noeud le plus proche du point
     * passé en paramètre.
     *
     * @param x Coordonnée est du point.
     * @param y Coordonnée nord du point.
     */
    public void addWaypoint(double x, double y){
        PointCh pointDonne = new PointCh(x,y);
        int nodeNewWaypoint = graph.nodeClosestTo(pointDonne, DISTANCE_RECHERCHE);
        PointCh positionNoeud = graph.nodePoint(nodeNewWaypoint);

        waypointsList.add(new Waypoint(positionNoeud,nodeNewWaypoint));

    }

}

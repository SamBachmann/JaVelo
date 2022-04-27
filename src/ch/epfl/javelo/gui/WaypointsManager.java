package ch.epfl.javelo.gui;

import ch.epfl.javelo.data.Graph;
import ch.epfl.javelo.projection.PointCh;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;

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

    public Pane pane (){
        return null;
    }

    public void addWaypoint(double x, double y){
        PointCh pointDonne = new PointCh(x,y);
        int nodeNewWaypoint = graph.nodeClosestTo(pointDonne, DISTANCE_RECHERCHE);
        PointCh positionNoeud = graph.nodePoint(nodeNewWaypoint);

        waypointsList.add(new Waypoint(positionNoeud,nodeNewWaypoint));

    }

}

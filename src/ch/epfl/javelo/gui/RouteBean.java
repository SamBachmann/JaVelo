package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

/**
 * Bean JavaFX qui contient les propriétés relatives à un itinéraire.
 * En particulier, appelle le constructeur de l'itinéraire
 *
 */
public final class RouteBean {

    private final ObservableList<Waypoint> waypointsList;

    public static final int DISTANCE_MAX_ECHANTILLONS = 5;
    private final RouteComputer routeComputer;
    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfil;
    private DoubleProperty highlightedPosition = new SimpleDoubleProperty(1000);

    /**
     * Constructeur de RouteBean
     *
     * @param routeComputer Un calculateur d'itinéraire.
     */
    public RouteBean(RouteComputer routeComputer) {

        this.routeComputer = routeComputer;
        this.route = new SimpleObjectProperty<>();
        this.elevationProfil = new SimpleObjectProperty<>();
        this.waypointsList = FXCollections.observableArrayList();

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> {
            List<Route> listeDeRoutes = new ArrayList<>();
            if (waypointsList.size() >= 2) {
                for (int i = 0; i < waypointsList.size() - 1; ++i) {
                    int startNodeIndex = waypointsList.get(i).nodeId();
                    int endNodeIndex = waypointsList.get(i + 1).nodeId();
                    RouteNodes routeNodes = new RouteNodes(startNodeIndex, endNodeIndex);

                    Route routeX;

                    if (cacheItineraires.containsKey(routeNodes)) {
                        routeX = cacheItineraires.get(routeNodes);
                        listeDeRoutes.add(routeX);
                    } else {
                        if (cacheItineraires.size() >= 100) {
                            Iterator<RouteNodes> iterator = cacheItineraires.keySet().iterator();
                            cacheItineraires.remove(iterator.next());
                        }
                        routeX = routeComputer.bestRouteBetween(startNodeIndex, endNodeIndex);
                        if (routeX != null) {
                            listeDeRoutes.add(routeX);
                            cacheItineraires.put(routeNodes, routeX);
                        }
                        else {
                            listeDeRoutes.clear();
                            this.route.set(null);
                            this.highlightedPosition = null;
                            this.elevationProfil.set(null);
                            break;
                        }
                    }
                }
                if (!listeDeRoutes.isEmpty()) {
                    Route multiRoute = new MultiRoute(listeDeRoutes);
                    this.elevationProfil.set(
                            ElevationProfileComputer.elevationProfile(multiRoute,DISTANCE_MAX_ECHANTILLONS)
                    );
                    this.route.set(multiRoute);
                    System.out.println(multiRoute);
                }

            }else {
                this.route.set(null);
            }

        });
    }
    private final Map< RouteNodes, Route> cacheItineraires =
            new LinkedHashMap<>(100, 0.75f, true);

    /**
     * Accesseur de la propriété contenant la position mise en évidence.
     *
     * @return La propriété contenant la position mise en évidence.
     */
    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    /**
     * Accesseur de la position mise en évidence, en metre sur l'itinéraire.
     *
     * @return La position le long de l'itinéraire
     */
    public double highlightedPosition(){
        return highlightedPosition.get();
    }

    /**
     * Enregistrement imbriqué représentant un itinéraire par ses noeuds de départ et d'arrivée.
     *
     * @param startNodeIndex L'index du noeud Javelo de départ.
     * @param endNodeIndex L'index du noeud Javelo de départ.
     */
    private record RouteNodes(int startNodeIndex, int endNodeIndex) {}

    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    public Route route() {
        return route.get();
    }

    public ReadOnlyObjectProperty<ElevationProfile> elevationProfilProperty(){
        return elevationProfil;
    }

    public ObservableList<Waypoint> WaypointsListProperty() {
        return waypointsList;
    }

}

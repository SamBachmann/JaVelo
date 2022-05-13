package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Bean JavaFX contenant les propriétés relatives aux points de passage et à un itinéraire correspondant.
 * En particulier appelle la construction de l'itinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 11/05/2022
 */
import java.sql.Array;
import java.util.*;

public final class RouteBean {

    public static final int DISTANCE_MAX_ECHANTILLONS = 5;
    private final RouteComputer routeComputer;
    private ObservableList<Waypoint> waypointsList;
    private ObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfil;
    private final Map< Map<Integer, Integer>, Route> cacheItineraires =
            new LinkedHashMap<>(100, 0.75f, true);

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        this.elevationProfil = new SimpleObjectProperty<>();
        this.waypointsList = FXCollections.observableArrayList(
                new Waypoint(new PointCh(2532697, 1152350), 159049));
        List<Route> listeDeRoutes = new ArrayList<>();

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> {
            if (waypointsList.size() >= 2) {
                for (int i = 0; i < waypointsList.size() - 1; ++i) {
                    int startNodeIndex = waypointsList.get(i).nodeId();
                    int endNodeIndex = waypointsList.get(i + 1).nodeId();
                    Map<Integer, Integer> nodesId = new HashMap<>();
                    nodesId.put(startNodeIndex, endNodeIndex);

                    Route routeX;

                    if (cacheItineraires.containsKey(nodesId)) {
                        routeX = cacheItineraires.get(nodesId);
                    } else {
                        if (cacheItineraires.size() >= 100) {
                            Iterator<Map<Integer, Integer>> iterator = cacheItineraires.keySet().iterator();
                            cacheItineraires.remove(iterator.next());
                        }
                        routeX = routeComputer.bestRouteBetween(startNodeIndex, endNodeIndex);
                    }
                    if (routeX != null) {
                        listeDeRoutes.add(routeX);
                        cacheItineraires.put(nodesId, routeX);
                        this.elevationProfil.set(
                                ElevationProfileComputer.elevationProfile(routeX, DISTANCE_MAX_ECHANTILLONS)
                        );
                    } else {
                        listeDeRoutes.clear();
                        this.route = null;
                        this.highlightedPosition = null;
                        this.elevationProfil.set(null);
                        break;
                    }
                }
                Route multiRoute = new MultiRoute(listeDeRoutes);
                this.route.set(multiRoute);
                }

            }
        });
    }



    public DoubleProperty highlightedPositionProperty(){
        return highlightedPosition;
    }

    public double highlightedPosition(){
        return highlightedPosition.get();
    }

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

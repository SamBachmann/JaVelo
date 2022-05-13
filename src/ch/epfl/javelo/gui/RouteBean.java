package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.MultiRoute;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.*;

public final class RouteBean {

    private record RouteNodes(int startNodeIndex, int endNodeIndex) {}

    public static final int DISTANCE_MAX_ECHANTILLONS = 5;
    private final RouteComputer routeComputer;
    private ObservableList<Waypoint> waypointsList;
    private ObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfil;
    private final Map< RouteNodes, Route> cacheItineraires =
            new LinkedHashMap<>(100, 0.75f, true);

    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;
        this.route = new SimpleObjectProperty<>();
        this.elevationProfil = new SimpleObjectProperty<>();
        this.waypointsList = FXCollections.observableArrayList();

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> {
            if (waypointsList.size() >= 2) {
                List<Route> listeDeRoutes = new ArrayList<>();
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
                            this.route = null;
                            this.highlightedPosition = null;
                            this.elevationProfil.set(null);
                            break;
                        }
                    }


                }
                if (!listeDeRoutes.isEmpty()) {
                    Route multiRoute = new MultiRoute(listeDeRoutes);
                    this.route.set(multiRoute);
                    System.out.println(multiRoute);
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

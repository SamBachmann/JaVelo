package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
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
public final class RouteBean {

    public static final int DISTANCE_MAX_ECHANTILLONS = 5;
    private final RouteComputer routeComputer;
    private ObservableList<Waypoint> waypointsList;
    private ObjectProperty<Route> route;
    private DoubleProperty highlightedPosition;
    private ObjectProperty<ElevationProfile> elevationProfil;



    public RouteBean(RouteComputer routeComputer) {
        this.routeComputer = routeComputer;

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> {
            if (waypointsList.size() >= 2) {
                for (int i = 0; i < waypointsList.size() - 1; ++i) {
                    //int startNodeIndex = waypointsList.get(i).nodeId;

                }

                this.elevationProfil.set(
                        ElevationProfileComputer.elevationProfile(route.get(), DISTANCE_MAX_ECHANTILLONS)
                );

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

    public ReadOnlyObjectProperty routeProperty(){
        return route;
    }

    public Route route() {
        return route.get();
    }

    public ReadOnlyObjectProperty elevationProfilProperty(){
        return elevationProfil;
    }
}

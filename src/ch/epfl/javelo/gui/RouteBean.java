package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public final class RouteBean {

   private final RouteComputer routeComputer;
   private ObservableList<Waypoint> waypointsList;
   private ReadOnlyObjectProperty<Route> route;
   private DoubleProperty highlightedPosition;
   private ReadOnlyObjectProperty<ElevationProfile> elevationProfil;

    public RouteBean(RouteComputer routeComputer) {

        this.routeComputer = routeComputer;

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> {
            if (waypointsList.size() >= 2){
               for (int i = 0; i < waypointsList.size() - 1; ++i){
                  int startNodeIndex = waypointsList.get(i).nodeId;

               }

            }
        });
    }

    private void calculItineraire(){
    }
}

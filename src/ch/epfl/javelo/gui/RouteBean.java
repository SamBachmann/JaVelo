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
    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfil;

    private DoubleProperty highlightedPosition = new SimpleDoubleProperty(1500);

    /**
     * Constructeur de RouteBean
     *
     * @param routeComputer Un calculateur d'itinéraire.
     */
    public RouteBean(RouteComputer routeComputer) {

        this.route = new SimpleObjectProperty<>();
        this.elevationProfil = new SimpleObjectProperty<>();
        this.waypointsList = FXCollections.observableArrayList();

        waypointsList.addListener((ListChangeListener<? super Waypoint>) observable -> {
            System.out.println("la liste de waypoints a changé");
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
                            this.highlightedPosition.set(Double.NaN);
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
                }

            }else {
                this.route.set(null);
            }

        });
    }
    private final Map< RouteNodes, Route> cacheItineraires =
            new LinkedHashMap<>(100, 0.75f, true);


    /**
     * Enregistrement imbriqué représentant un itinéraire par ses noeuds de départ et d'arrivée.
     *
     * @param startNodeIndex L'index du noeud Javelo de départ.
     * @param endNodeIndex L'index du noeud Javelo de départ.
     */
    private record RouteNodes(int startNodeIndex, int endNodeIndex) {}

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

    public void setHighlightedPosition(double highlightedPosition) {
        this.highlightedPosition.set(highlightedPosition);
    }

    /**
     * Accesseur de la propriété contenant la route.
     *
     * @return La propriété contenant la route.
     */
    public ReadOnlyObjectProperty<Route> routeProperty(){
        return route;
    }

    /**
     * Accesseur de la route calculée
     *
     * @return L'itinéraire calculé
     */
    public Route route() {
        return route.get();
    }

    /**
     * Accesseur de la propriété contenant le profil de l'itinéraire.
     *
     * @return La propriété contenant le profil de l'itinéraire
     */
    public ReadOnlyObjectProperty<ElevationProfile> elevationProfilProperty(){
        return elevationProfil;
    }

    public ElevationProfile elevationProfile(){return elevationProfil.get();}

    public ObservableList<Waypoint> WaypointsListProperty() {
        return waypointsList;
    }

    /**
     * Retourne l'index du segment d'un point donné sans tenir compte des segments vide.
     *
     * @param position Une position sur l'intinéraire
     * @return L'index du segment.
     */
    public int indexOfNonEmptySegmentAt(double position) {
        int index = route().indexOfSegmentAt(position);
        for (int i = 0; i <= index; i += 1) {
            int n1 = waypointsList.get(i).nodeId();
            int n2 = waypointsList.get(i + 1).nodeId();
            if (n1 == n2) index += 1;
        }
        return index;
    }

}

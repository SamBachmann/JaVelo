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
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 06/05/2022
 */
public final class RouteBean {

    private static final int NOMBRE_WAYPOINTS_MIN_ITINERAIRE = 2;
    private final ObservableList<Waypoint> waypointsList;

    private static final int DISTANCE_MAX_ECHANTILLONS = 5;
    private static final int TAILLE_MAX_CACHE_ITINERAIRE = 100;
    private final ObjectProperty<Route> route;
    private final ObjectProperty<ElevationProfile> elevationProfil;
    private final Map< RouteNodes, Route> cacheItineraires =
            new LinkedHashMap<>(TAILLE_MAX_CACHE_ITINERAIRE, 0.75f, true);

    private final DoubleProperty highlightedPosition = new SimpleDoubleProperty();

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

            List<Route> listeDeRoutes = new ArrayList<>();
            if (waypointsList.size() >= NOMBRE_WAYPOINTS_MIN_ITINERAIRE) {
                for (int i = 0; i < waypointsList.size() - 1; ++i) {
                    int startNodeIndex = waypointsList.get(i).nodeId();
                    int endNodeIndex = waypointsList.get(i + 1).nodeId();
                    RouteNodes routeNodes = new RouteNodes(startNodeIndex, endNodeIndex);

                    Route segment;

                    if (cacheItineraires.containsKey(routeNodes)) {
                        segment = cacheItineraires.get(routeNodes);
                        listeDeRoutes.add(segment);
                    } else {
                        if (cacheItineraires.size() >= TAILLE_MAX_CACHE_ITINERAIRE) {
                            Iterator<RouteNodes> iterator = cacheItineraires.keySet().iterator();
                            cacheItineraires.remove(iterator.next());
                        }
                        segment = routeComputer.bestRouteBetween(startNodeIndex, endNodeIndex);
                        if (segment != null) {
                            listeDeRoutes.add(segment);
                            cacheItineraires.put(routeNodes, segment);
                        }
                        else {
                            listeDeRoutes.clear();
                            this.route.set(null);
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
                this.elevationProfil.set(null);
            }

        });
    }

    /**
     * Enregistrement imbriqué représentant un itinéraire par ses nœuds de départ et d'arrivée.
     *
     * @param startNodeIndex L'index du nœud Javelo de départ.
     * @param endNodeIndex L'index du nœud Javelo de départ.
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
     * @return La position le long de l'itinéraire.
     */
    public double highlightedPosition(){
        return highlightedPosition.get();
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
     * @param position Une position sur l'itinéraire
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

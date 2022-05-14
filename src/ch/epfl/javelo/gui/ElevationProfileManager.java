package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

/**
 * Classe gérant l'affichage et les interactions avec le profil en long d'un itinéraire
 *
 */
public final class ElevationProfileManager {

    private final Pane pane;

    /**
     * Constructeur d'ElevationProfileManager
     *
     * @param profil Profil de l'itinéraire.
     * @param highlightedPosition Position en évidence.
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profil,
                                   ReadOnlyDoubleProperty highlightedPosition){
        this.pane = new Pane();
    }

    public Pane pane(){
        return this.pane;
    }
}

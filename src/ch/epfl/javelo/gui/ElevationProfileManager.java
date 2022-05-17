package ch.epfl.javelo.gui;


import ch.epfl.javelo.routing.ElevationProfile;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.layout.Pane;

/**
 * Classe gérant l'affichage et les interactions avec le profil en long d'un itinéraire.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 14/05/2022
 */
public final class ElevationProfileManager {

    private final Pane pane;

    /**
     * Constructeur d'ElevationProfileManager
     *
     * @param profil Profil de l'itinéraire.
     * @param positionProfil Position en évidence.
     */
    public ElevationProfileManager(ReadOnlyObjectProperty<ElevationProfile> profil,
                                   ReadOnlyDoubleProperty positionProfil){
        this.pane = new Pane();
        this.pane.getStylesheets().add("elevation_profile.css");
    }

    public Pane pane(){
        return this.pane;
    }

    public ReadOnlyIntegerProperty mousePositionOnProfileProperty(){
        return null;
    }
}

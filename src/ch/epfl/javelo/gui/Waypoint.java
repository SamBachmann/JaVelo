package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement représentant un point de passage sur l'itinéraire
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 22/04/2022
 *
 * @param PointPassage Position du point de passge sur l'itinéraire
 * @param nodeId Identité du noeud Javelo le plus proche de PointPassage.
 */
public record Waypoint(PointCh PointPassage, int nodeId) {

}

package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Enregistrement représentant les arêtes du graphe JaVelo
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 09/03/2022
 */

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    // Question : on peut utiliser les méthodes de Q28_4 pour représenter des 12_4 ?
    //définir les constantes.
    private static final int OFFSET_DESTINATION_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_DESTINATION_NODE + Integer.BYTES;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_OSM_ATTRIBUTES = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private static final int EDGES_INTS = OFFSET_OSM_ATTRIBUTES + Short.BYTES;

    /**
     * Retourne le dénivelé positif d'une arête.
     * On se sert de
     *
     * @param edgeId L'index de l'arête
     * @return Le dénivelé positif de l'arête d'index edgeId, en mètres.
     */
    public double elevationGain(int edgeId){
        return Q28_4.asDouble(Short.toUnsignedInt(
                edgesBuffer.getShort(
                        edgeId * EDGES_INTS + OFFSET_ELEVATION_GAIN)
                )
            );

    }

    /*public boolean hasProfile(int edgeId) {
    }*/

}

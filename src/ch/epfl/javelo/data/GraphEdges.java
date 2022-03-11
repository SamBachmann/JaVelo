package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
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
    // Définition des constantes pour l'accès aux éléments dans edgesBuffer
    private static final int OFFSET_DESTINATION_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_DESTINATION_NODE + Integer.BYTES;
    private static final int OFFSET_ELEVATION_GAIN = OFFSET_LENGTH + Short.BYTES;
    private static final int OFFSET_OSM_ATTRIBUTES = OFFSET_ELEVATION_GAIN + Short.BYTES;
    private static final int EDGES_INTS = OFFSET_OSM_ATTRIBUTES + Short.BYTES;


    /**
     * Méthode nous permettant de savoir si une arête va dans le sens de la voie OSM dont elle provient.
     *
     * @param edgeId L'index de l'arête dont on veut connaître le sens.
     * @return Un booléen indiquant le sens de l'arête.
     */
    public boolean isInverted(int edgeId) {
        int attribut1 = edgesBuffer.get(edgeId * 4);
        return attribut1 < 0;
    }

    /**
     * Méthode nous permettant de connaître l'index du nœud de destination de l'arête d'index donné.
     *
     * @param edgeId L'index de l'arête dont on veut connaître le nœud de destination.
     * @return Un entier représentant l'identité du nœud de destination.
    */

    //public int targetNodeId( int edgeId) {
        //if (this.isInverted(edgeId)) {
            //int positif = -(edgesBuffer.get(edgeId));
            //int etape2 = Bits.extractSigned(positif, 0, 32);
            // Question concernant l'inversion de chaque bit.
           // return
       // } else {
          // return edgesBuffer.get(edgeId * 4);
        //}
    //}

    /**
     * Méthode nous permettant de connaître la longueur en mètres de l'arête d'index donné.
     *
     * @param edgeId L'index de l'arête dont on veut connaître la longueur.
     * @return Un double représentant la longueur de l'arête d'index donné.
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(edgesBuffer.getShort(edgeId * 4 + 1));
    }

    /**
     * Retourne le dénivelé positif d'une arête.
     * On se sert de la méthode Q28_4.asDouble pour passer la valeur de dénivelé en UQ12.4 en double.
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

    /**
     * Méthode qui test si une arête donnée possède un profil.
     * Fait appel à la méthode privée typeProfil qui lui retourne le type de profil.
     *
     * @param edgeId L'index de l'arête
     * @return vrai si un profil est enregistré, faux sinon.
     */
    public boolean hasProfile(int edgeId) {
        if (typeProfil(edgeId) == 0){
            return false;
        }
        return true;
    }

    /**
     *
     * @param edgeId
     * @return
     */
   /* public float[] profileSamples(int edgeId){
        if ( !this.hasProfile(edgeId)){
            return new float[0];
        }
        // question pour cette partie
        int nombreEchantillons = (int) (1 + Math.ceil(this.length(edgeId) / 2));
        int idPremierEchantillon = Bits.extractUnsigned(profileIds.get(edgeId),0,30);

        // Premier échantillon: toujours le même cas.
        float [] profilSamplesTable = new float[nombreEchantillons];
        float premiereAltitude = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(idPremierEchantillon)));
        profilSamplesTable[0] = premiereAltitude;

        for (int i = 1; i < nombreEchantillons; ++i){
            // Sélectionne le type de profil (1: non compressé, 2: compressé 8-bits, 3: compressé 4-bits)
            switch (this.typeProfil(edgeId)){
                case 1 :
                    profilSamplesTable[i] = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(idPremierEchantillon + i)));
                break;
                case 2 :
                    if (((i - 1) / 2.0) == Math.floor((i + 1) / 2.0)){
                        profilSamplesTable[i] = Q28_4.asFloat(
                                Bits.extractSigned(elevations.get(idPremierEchantillon + i),4,4)
                        );
                    }
                break;
                case 3 :
            }

        }

        //inverser sens du tableau si isInverted = vrai
        if (this.isInverted(edgeId)){
            for (int i = 0; i < nombreEchantillons; ++i){

            }
        }


        return new float[12];
    }*/


    /**
     * Méthode privée retournant le type de profil de l'arête.
     * @param edgeId L'index de l'arête
     * @return
     */
    private int typeProfil(int edgeId){
        return Bits.extractUnsigned(profileIds.get(edgeId), 30,2);
    }

    /**
     * Méthode nous permettant de connaître l'identité de l'ensemble d'attributs attaché à l'arête
     * d'identité donnée.
     *
     * @param edgeId L'index de l'arête attachée à l'ensemble d'attributs.
     * @return Un entier de type short qui retourne l'identité de l'ensemble d'attributs attaché
     * à l'arête d'identité donnée.
     */
    public int attributesIndex(int edgeId) {
        return edgesBuffer.getShort(edgeId * 4 + 3);
    }

}

package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;


import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

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
    private static final int Q4_4_PER_SHORT = Short.BYTES;
    private static final int Q0_4_PER_SHORT = 2 * Q4_4_PER_SHORT;
    private static final int Q0_4_LENGTH = 4;
    private static final int Q4_4_LENGTH = 8;


    /**
     * Méthode auxiliaire nous permettant d'effectuer la concatenation des
     * 4 premiers octets du ByteBuffer edgesBuffer liés à chaque arête.
     *
     * @param edgeId L'index de l'arête dont on veut connaître les 4 premiers
     *               octets.
     * @return Un entier représentant les 4 premiers octets liés à chaque arête.
     */
    public int concatenation4PremiersOctets(int edgeId) {
        return edgesBuffer.getInt(edgeId * EDGES_INTS);
    }

    /**
     * Méthode nous permettant de savoir si une arête va dans le sens de la voie OSM dont elle provient.
     *
     * @param edgeId L'index de l'arête dont on veut connaître le sens.
     * @return Un booléen indiquant le sens de l'arête.
     */
    public boolean isInverted(int edgeId) {
        return this.concatenation4PremiersOctets(edgeId) < 0;
    }

    /**
     * Méthode nous permettant de connaître l'index du nœud de destination de l'arête d'index donné.
     *
     * @param edgeId L'index de l'arête dont on veut connaître le nœud de destination.
     * @return Un entier représentant l'identité du nœud de destination.
    */
    public int targetNodeId( int edgeId) {
        if (this.isInverted(edgeId)) {
            int entier = concatenation4PremiersOctets(edgeId);
            return ~entier;
        } else {
           return edgesBuffer.getInt(edgeId * EDGES_INTS);
        }
    }

    /**
     * Méthode nous permettant de connaître la longueur en mètres de l'arête d'index donné.
     *
     * @param edgeId L'index de l'arête dont on veut connaître la longueur.
     * @return Un double représentant la longueur de l'arête d'index donné.
     */
    public double length(int edgeId) {
        return Q28_4.asDouble(Short.toUnsignedInt(edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_LENGTH)));
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
        return (Bits.extractUnsigned(profileIds.get(edgeId), 30,2) != 0);
    }

    /**
     * Retourne le tableau des échantillons du profil de l'arête d'identité donnée,
     *
     * vide si l'arête ne possède pas de profil.
     * @param edgeId L'index de l'arête
     * @return Un tableau float[] contenant les échantillons de profil
     */
    public float[] profileSamples(int edgeId){
        if ( !this.hasProfile(edgeId)){
            return new float[0];
        }

        int nombreEchantillons =  1 + Math2.ceilDiv(edgesBuffer.getShort(
                edgeId * EDGES_INTS + OFFSET_LENGTH), Q28_4.ofInt(2)
        );
        int idPremierEchantillon = Bits.extractUnsigned(profileIds.get(edgeId),0,30);

        // Premier échantillon: toujours le même cas.
        float [] profilSamplesTable = new float[nombreEchantillons];
        float premiereAltitude = Q28_4.asFloat(Short.toUnsignedInt(elevations.get(idPremierEchantillon)));
        profilSamplesTable[0] = premiereAltitude;

        switch (typeProfil(edgeId)) {
            case NOT_COMPRESSED:
                for (int i = 1; i < nombreEchantillons; ++i) {
                    profilSamplesTable[i] = Q28_4.asFloat(
                            Short.toUnsignedInt(elevations.get(idPremierEchantillon + i))
                    );
                }
            break;
            case COMPRESSED_8BITS:
                int nombreIteration = 1;
                //Parcours les shorts
                for (int i = 1; i <= Math2.ceilDiv(nombreEchantillons, Q4_4_PER_SHORT); ++i) {
                    int k = Q4_4_PER_SHORT;
                    // séparations dans les shorts
                    while ((k > 0) && (nombreIteration < nombreEchantillons)) {
                        profilSamplesTable[nombreIteration] = profilSamplesTable[nombreIteration - 1] +
                                this.extractBitsCompressedProfil(
                                        (k - 1) * 8, Q4_4_LENGTH, idPremierEchantillon + i
                                );
                        --k;
                        ++nombreIteration;
                    }
                }
            break;
            case COMPRESSED_4BITS:
                int nbIteration = 1;

                for (int i = 1; i <= Math2.ceilDiv(nombreEchantillons, Q0_4_PER_SHORT); ++i) {
                    int k = Q0_4_PER_SHORT; // == 4
                    // séparations dans les shorts
                    while ((k > 0) && (nbIteration < nombreEchantillons)) {
                        profilSamplesTable[nbIteration] = profilSamplesTable[nbIteration - 1] +
                                this.extractBitsCompressedProfil(
                                        (k - 1) * 4,Q0_4_LENGTH, idPremierEchantillon + i
                                );
                        --k;
                        ++nbIteration;
                    }
                }
            break;
        }

        //inverser sens du tableau si isInverted = vrai
        if (this.isInverted(edgeId)){
            for (int i = 0; i < nombreEchantillons / 2; ++i){
                float inter = profilSamplesTable[i];
                profilSamplesTable[i] = profilSamplesTable[nombreEchantillons - 1 - i];
                profilSamplesTable[nombreEchantillons - 1 - i] = inter;
            }
        }

        return profilSamplesTable;
    }


    private float extractBitsCompressedProfil(int start, int length, int index){
        return Q28_4.asFloat(
                Bits.extractSigned(elevations.get(index),start,length)
        );
    }

    private float extractCompressed(){
        return 0f;
    }
    private void extractUncompressed(){}


    /**
     * Méthode privée retournant le type de profil de l'arête.
     * @param edgeId L'index de l'arête
     * @return
     */
    private CompressionType typeProfil(int edgeId){
        int profilIndex = (Bits.extractUnsigned(profileIds.get(edgeId), 30,2));
        return CompressionType.values()[profilIndex];
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
        return edgesBuffer.getShort(edgeId * EDGES_INTS + OFFSET_OSM_ATTRIBUTES);
    }

    private enum CompressionType{
        NO_PROFIL, NOT_COMPRESSED, COMPRESSED_8BITS, COMPRESSED_4BITS;
    }

}
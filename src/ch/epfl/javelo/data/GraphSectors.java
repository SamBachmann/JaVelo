package ch.epfl.javelo.data;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Enregistrement représentant le tableau des secteurs JaVelo
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 07/03/2022
 */

public record GraphSectors(ByteBuffer buffer ) {
    private static final int OFFSET_NODE = 0;
    private static final int OFFSET_LENGTH = OFFSET_NODE + Integer.BYTES;
    private static final int SECTOR_INTS = OFFSET_LENGTH + Short.BYTES;
    private static final int SECTEURS_PAR_COTE = 128;

    /**
     * Enregistrement imbriqué pour représenter un secteur à l'aide de l'index de 2 noeuds
     *
     * @param startNodeId L'index du premier noeud du secteur
     * @param endNodeId L'index du noeud situé juste après le dernier noeud du secteur.
     *     */
    public record Sector(int startNodeId, int endNodeId){

    }

    /**
     * Calcule et retourne tous les secteurs ayant une intersection avec le carré dont le centre
     * et le demi coté sont donnés en arguments.
     *
     * @param center Le centre du carré.
     * @param distance La longueur d'un demi coté de ce carré.
     * @return La liste des secteurs ayant intersection avec ce carré.
     */
   public List<Sector> sectorsInArea(PointCh center, double distance){
        // première étape : calculer les coordonnées de secteurs xmin, xmax, ymin, ymax
        int xMin = (int)Math.floor(SECTEURS_PAR_COTE * (center.e() - distance - SwissBounds.MIN_E)/ SwissBounds.WIDTH);
        int xMax = (int)Math.floor(SECTEURS_PAR_COTE * (center.e() + distance - SwissBounds.MIN_E)/ SwissBounds.WIDTH);
        int yMin = (int)Math.floor(SECTEURS_PAR_COTE * (center.n() - distance - SwissBounds.MIN_N)/ SwissBounds.HEIGHT);
        int yMax = (int)Math.floor(SECTEURS_PAR_COTE * (center.n() + distance - SwissBounds.MIN_N)/ SwissBounds.HEIGHT);

       // calculer les index des secteurs
       ArrayList<Integer> listIndexSector = new ArrayList<Integer>();
        for (int x = xMin; x <= xMax; ++x){
            for (int y = yMin; y <= yMax; ++y){
                listIndexSector.add(x + 128 * y);
            }
        }
        // construire les secteurs depuis le buffer
       ArrayList<Sector> listSector = new ArrayList<Sector>();

       for (int firstNodeIndex : listIndexSector) {
           int nodeNumberInSector = Short.toUnsignedInt(
                   buffer.getShort(
                           SECTOR_INTS * firstNodeIndex + OFFSET_LENGTH
                   )
           );

           int endNodeIndex = firstNodeIndex + nodeNumberInSector;
           listSector.add(new Sector(firstNodeIndex, endNodeIndex));
       }

        return listSector;
    }
}
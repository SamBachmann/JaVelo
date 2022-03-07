package ch.epfl.javelo.data;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.projection.SwissBounds;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Enregistremenet représentant le tableau des secteurs JAVelo
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 07/03/2022
 */

public record GraphSectors(ByteBuffer buffer ) {

    public record Sector(int startNodeId, int endNodeId){

    }

   /* public List<Sector> sectorsInArea(PointCh center, double distance){
        // première étape : calculer les coordonnées de secteurs xmin, xmax, ymin, ymax
        int xMin = (int) Math.floor(128 * (center.e() - distance) / SwissBounds.WIDTH);
        int xMax = (int) Math.floor(128 * (center.e() + distance) / SwissBounds.WIDTH);
        int yMin = (int) Math.floor(128 * (center.n() - distance) / SwissBounds.HEIGHT);
        int yMax = (int) Math.floor(128 * (center.n() + distance) / SwissBounds.HEIGHT);




    }*/
}

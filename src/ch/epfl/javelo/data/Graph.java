package ch.epfl.javelo.data;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * Classe représentant un graphe de JaVelo, qui comporte des nœuds, des arêtes
 * des secteurs et des ensembles d'attributs.
 *
 *  @author Samuel Bachmann (340373)
 *  @author Cyrus Giblain (312042)
 * <br>
 * 18/03/2022
 */
public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;
    private final static int NO_NODE = -1;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges,
                 List<AttributeSet> attributeSets) {

        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    /**
     * Méthode permettant d'obtenir le graphe JaVelo obtenu à partir de fichiers
     * dont on fournit le chemin d'accès.
     *
     * @param basePath Le chemin d'accès du fichier.
     * @return Un graphe JaVelo obtenu à partir du fichier donné.
     */
    public static Graph loadFrom(Path basePath) throws IOException {

        GraphNodes newNodes;
        GraphSectors newSectors;
        GraphEdges newEdges;
        List<AttributeSet> newAttributeSets1 = new ArrayList<>();

        Path nodesPath = basePath.resolve("nodes.bin");
        IntBuffer nodesBuffer;
        try (FileChannel channel1 = FileChannel.open(nodesPath)) {
            nodesBuffer = channel1
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel1.size())
                    .asIntBuffer();
            newNodes = new GraphNodes(nodesBuffer);
        }

        Path sectorsPath = basePath.resolve("sectors.bin");
        ByteBuffer sectorsBuffer;
        try (FileChannel channel2 = FileChannel.open(sectorsPath)) {
            sectorsBuffer = channel2
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel2.size());
            newSectors = new GraphSectors(sectorsBuffer);
        }

        Path edgesPath = basePath.resolve("edges.bin");
        ByteBuffer edgesBuffer;
        try (FileChannel channel3 = FileChannel.open(edgesPath)) {
            edgesBuffer = channel3
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel3.size());
        }

        Path profilePath = basePath.resolve("profile_ids.bin");
        IntBuffer profileIdsBuffer;
        try (FileChannel channel4 = FileChannel.open(profilePath)) {
            profileIdsBuffer = channel4
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel4.size())
                    .asIntBuffer();
        }

        Path elevationsPath = basePath.resolve("elevations.bin");
        ShortBuffer elevationsBuffer;
        try (FileChannel channel5 = FileChannel.open(elevationsPath)) {
            elevationsBuffer = channel5
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel5.size())
                    .asShortBuffer();
        }

        newEdges = new GraphEdges(edgesBuffer, profileIdsBuffer, elevationsBuffer);

        Path attributePath = basePath.resolve("attributes.bin");
        LongBuffer attributes;
        List<AttributeSet> newAttributeSets;
        try (FileChannel channel6 = FileChannel.open(attributePath)) {
            attributes = channel6
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel6.size())
                    .asLongBuffer();
            for (int i = 0; i < attributes.capacity(); ++i) {
                newAttributeSets1.add(new AttributeSet(attributes.get(i)));
            }
        }
        newAttributeSets = List.copyOf(newAttributeSets1);

        return new Graph(newNodes, newSectors, newEdges, newAttributeSets);
    }

    /**
     * Méthode qui nous donne le nombre total de nœuds dans le graphe JaVelo.
     *
     * @return Un int représentant le nombre de nœuds du graphe JaVelo.
     */
    public int nodeCount() {
        return this.nodes.count();
    }

    /**
     * Méthode qui nous donne la position du nœud d'identité donné.
     *
     * @param nodeId L'identité du nœud donnée.
     * @return Un PointCh caractérisant la position du nœud d'identité donnée.
     */
    public PointCh nodePoint(int nodeId) {
        double E = this.nodes.nodeE(nodeId);
        double N = this.nodes.nodeN(nodeId);
        return new PointCh(E, N);
    }

    /**
     * Méthode qui nous donne le nombre d'arêtes sortant du nœud d'identité donnée.
     *
     * @param nodeId L'identité du nœud donnée.
     * @return le nombre d'arêtes sortant du nœud d'identité donnée.
     */
    public int nodeOutDegree(int nodeId) {
        return this.nodes.outDegree(nodeId);
    }

    /**
     * Méthode qui nous donne l'identité de la edgeIndex-ème arête sortant
     * du nœud d'identité donnée.
     *
     * @param nodeId L'identité du nœud en question.
     * @param edgeIndex L'index de l'arête en question.
     * @return Un int représentant l'identité de la edgeIndex-ème arête sortant du noeud d'identité
     * nodeId.
     */
    public int  nodeOutEdgeId(int nodeId, int edgeIndex) {
        return this.nodes.edgeId(nodeId, edgeIndex);
    }

    /**
     * Méthode qui nous donne l'identité du nœud se trouvant le plus proche du point donné,
     * à la distance maximale donnée.
     *
     * @param point Le point donné.
     * @param searchDistance La distance maximale donnée.
     * @return Un int représentant l'identité du nœud se trouvant le plus proche du point donné,
     * à la distance maximale donnée, ou la constante NO_NODE valant -1 si aucun nœud ne correspond
     * à ces critères.
     */
    public int nodeClosestTo(PointCh point, double searchDistance) {

        int nodeId = NO_NODE;

        List<GraphSectors.Sector> listOfSectors = this.sectors.sectorsInArea(point, searchDistance);

        double autreComparaison = Double.MAX_VALUE;
        for (GraphSectors.Sector sector : listOfSectors) {
            for (int i = sector.startNodeId(); i < sector.endNodeId(); ++i) {

                PointCh pointAComparer = this.nodePoint(i);
                if (point.squaredDistanceTo(pointAComparer) <= searchDistance * searchDistance
                        && point.squaredDistanceTo(pointAComparer) < autreComparaison) {
                    nodeId = i;
                    autreComparaison = point.squaredDistanceTo(pointAComparer);
                }
            }
       }
        return nodeId;
    }

    /**
     * Méthode nous donnant l'identité du nœud de destination de l'arête d'identité
     * donnée.
     *
     * @param edgeId L'identité de l'arête.
     * @return Un int représentant l'identité du nœud destination de l'arête d'identité
     * donnée.
     */
    public int edgeTargetNodeId(int edgeId) {
        return this.edges.targetNodeId(edgeId);
    }

    /**
     * Méthode nous permettant de savoir si l'arête d'identité donnée va dans le sens
     * contraire de la voie OSM dont elle provient.
     *
     * @param edgeId L'identité de l'arête.
     * @return Un booléen nous permettant de savoir si l'arête d'identité donnée va
     * dans le sens contraire de la voie OSM dont elle provient.
     */
    public boolean edgeIsInverted(int edgeId) {
        return this.edges.isInverted(edgeId);
    }

    /**
     * Méthode nous donnant l'ensemble des attributs OSM attachés à l'arête d'identité
     * donnée.
     *
     * @param edgeId L'identité de l'arête.
     * @return L'ensemble d'attributs attachés à l'arête d'identité donnée.
     */
    public AttributeSet edgeAttributes(int edgeId) {
        return attributeSets.get(this.edges.attributesIndex(edgeId));
    }

    /**
     * Méthode nous donnant la longueur, en mètres, de l'arête d'identité donnée.
     *
     * @param edgeId L'identité de l'arête.
     * @return Un double représentant la longueur, en mètres, de l'arête d'identité donnée.
     */
    public double edgeLength(int edgeId) {
        return this.edges.length(edgeId);
    }

    /**
     * Méthode nous donnant le dénivelé positif total de l'arête d'identité donnée.
     *
     * @param edgeId L'identité de l'arête.
     * @return Un double représentant le dénivelé positif total de l'arête d'identité donnée.
     */
    public double edgeElevationGain(int edgeId) {
        return this.edges.elevationGain(edgeId);
    }

    /**
     * Retourne la fonction samples du profil en long d'une arête.
     *
     * @param edgeId L'index de l'arête dont on veut le profil.
     * @return La fonction DoubleUnaryOperator représentant le profil.
     */
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (this.edges.hasProfile(edgeId)) {
            float[] profil =  this.edges.profileSamples(edgeId);
            return Functions.sampled(profil,edges.length(edgeId));
        } else {
            return Functions.constant(Double.NaN);
        }
    }

}
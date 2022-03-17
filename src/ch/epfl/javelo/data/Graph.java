package ch.epfl.javelo.data;

import ch.epfl.javelo.projection.PointCh;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

public final class Graph {

    private final GraphNodes nodes;
    private final GraphSectors sectors;
    private final GraphEdges edges;
    private final List<AttributeSet> attributeSets;

    public Graph(GraphNodes nodes, GraphSectors sectors, GraphEdges edges,
                 List<AttributeSet> attributeSets) {

        this.nodes = nodes;
        this.sectors = sectors;
        this.edges = edges;
        this.attributeSets = List.copyOf(attributeSets);
    }

    public Graph loadFrom(Path basePath) throws IOException {

        basePath = Path.of("lausanne");

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

        Path profilePath = basePath.resolve("profile_ids");
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
        ByteBuffer attributes;
        List<AttributeSet> newAttributeSets;
        try (FileChannel channel6 = FileChannel.open(attributePath)) {
            attributes = channel6
                    .map(FileChannel.MapMode.READ_ONLY, 0, channel6.size());
            for (int i = 0; i < attributes.capacity(); ++i) {
                newAttributeSets1.add(new AttributeSet(attributes.get(i)));
            }
        }
        newAttributeSets = List.copyOf(newAttributeSets1);

        return new Graph(newNodes, newSectors, newEdges, newAttributeSets);
    }

    public int nodeCount() {
        return this.nodes.count();
    }

    public PointCh nodePoint(int nodeId) {
        double E = this.nodes.nodeE(nodeId);
        double N = this.nodes.nodeN(nodeId);
        return new PointCh(E, N);
    }

    public int nodeOutDegree(int nodeId) {
        return this.nodes.outDegree(nodeId);
    }

    public int nodeOutEdgeId(int nodeId, int edgeIndex) {
        return this.nodes.edgeId(nodeId, edgeIndex);
    }

    public int nodeClosestTo(PointCh point, double searchDistance) {

        int nodeId = -1;

        for (int i = 0; i < this.nodes.count(); ++i) {
            PointCh pointAComparer = this.nodePoint(i);
            double autreComparaison = Double.MAX_VALUE;
            if ((point.squaredDistanceTo(pointAComparer) <= searchDistance)
             && point.squaredDistanceTo(pointAComparer) < autreComparaison) {
                nodeId = i;
                autreComparaison = point.squaredDistanceTo(pointAComparer);
            }
        }
        return nodeId;
    }

    public int edgeTargetNodeId(int edgeId) {
        return this.edges.targetNodeId(edgeId);
    }

    public boolean edgeIsInverted(int edgeId) {
        return this.edges.isInverted(edgeId);
    }

    public AttributeSet edgeAttributes(int edgeId) {
        return new AttributeSet(this.edges
                .attributesIndex(edgeId));
    }

    public double edgeLength(int edgeId) {
        return this.edges.length(edgeId);
    }

    public double edgeElevationGain(int edgeId) {
        return this.edges.elevationGain(edgeId);
    }

    //Méthode à compléter. Question concernant les DoubleUnaryOperator
    public DoubleUnaryOperator edgeProfile(int edgeId) {
        if (this.edges.hasProfile(edgeId)) {
            return this.edges.profileSamples(edgeId);
        } else {
            return Double.NaN;
        }
    }
}





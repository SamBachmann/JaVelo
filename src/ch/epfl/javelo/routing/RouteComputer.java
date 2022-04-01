package ch.epfl.javelo.routing;


import ch.epfl.javelo.Preconditions;
import ch.epfl.javelo.data.Graph;

import java.util.*;

public final class RouteComputer {
    private final Graph graph;
    private final CostFunction costFunction;
    private float [] distance;
    private int[] predecesseur;

    public RouteComputer(Graph graph , CostFunction costFunction){
        this.graph = graph;
        this.costFunction = costFunction;
        int nombreNoeuds = graph.nodeCount();
        this.distance = new float[nombreNoeuds];
        this.predecesseur = new int[nombreNoeuds];
    }

    /**
     * Méthode appliquant l'algorithme de calcul d'itinéraire pour trouver le meilleur entre
     * un nœud de départ et un nœud d'arrivée donnés.
     *
     * @param startNodeId L'index du nœud de départ.
     * @param endNodeId L'index du nœud d'arrivée.
     * @return Une Route représentant le meilleur itinéraire entre ces 2 nœuds.
     */
    public Route  bestRouteBetween(int startNodeId, int endNodeId){
        Preconditions.checkArgument(startNodeId != endNodeId);

        /**
         * Enregistrement imbriqué pour représenter un noeud à une distance donnée
         */
        record WeightedNode(int nodeId, float distance)
                implements Comparable<WeightedNode> {
            @Override
            public int compareTo(WeightedNode that) {
                return Float.compare(this.distance, that.distance);
            }
        }

        PriorityQueue<WeightedNode> enExploration = new PriorityQueue<>();
        boolean itineraireExiste = false;

        //initialisation de tous les noeuds à +infini
        for (int i = 0; i < distance.length; ++i){
            distance[i] = Float.POSITIVE_INFINITY;
            predecesseur[i] = 0;
        }

        int noeudActuelId;
        distance[startNodeId] = 0;
        enExploration.add(new WeightedNode(startNodeId, distance[startNodeId]));

        while (enExploration.size() > 0){
            noeudActuelId = enExploration.remove().nodeId;

            if (noeudActuelId == endNodeId){
                itineraireExiste = true;
                break;

            }

            //if (distance[noeudActuelId] != Float.NEGATIVE_INFINITY) {
                for (int k = 0; k < graph.nodeOutDegree(noeudActuelId); ++k) {
                    int indexEdge = graph.nodeOutEdgeId(noeudActuelId, k);
                    int nouveauNoeudId = graph.edgeTargetNodeId(indexEdge);
                    float distanceNouveauNoeud = (float) (distance[noeudActuelId]
                            + costFunction.costFactor(noeudActuelId, indexEdge) * graph.edgeLength(indexEdge));

                    if (distanceNouveauNoeud < distance[nouveauNoeudId]) {
                        distance[nouveauNoeudId] = distanceNouveauNoeud;
                        predecesseur[nouveauNoeudId] = noeudActuelId;
                        enExploration.add(new WeightedNode(nouveauNoeudId, distance[nouveauNoeudId]));
                    }
                    //On ne veut plus parcourir les noeuds déjà vus.
                    
                    //distance[noeudActuelId] = Float.NEGATIVE_INFINITY;
                }
            //}
        }

        if (itineraireExiste){
            noeudActuelId = endNodeId;
            List<Edge> listEdges = new ArrayList<Edge>();

            while (noeudActuelId != startNodeId){
                int noeudPrecedentId = predecesseur[noeudActuelId];
                for (int i = 0; i < graph.nodeOutDegree(noeudPrecedentId); ++i){
                    int indexEdge = graph.nodeOutEdgeId(noeudPrecedentId, i);
                    if (graph.edgeTargetNodeId(indexEdge) == noeudActuelId){
                        listEdges.add(Edge.of(graph, indexEdge,noeudPrecedentId,noeudActuelId));
                        noeudActuelId = noeudPrecedentId;
                        break;
                    }
                }
            }
            Collections.reverse(listEdges);

            return new SingleRoute(List.copyOf(listEdges));

        }
        // Aucun itinéraire trouvé
        return null;
    }

}
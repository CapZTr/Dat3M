package com.dat3m.dartagnan.analysis.graphRefinement.graphs.eventGraph.utils;

import com.dat3m.dartagnan.analysis.graphRefinement.graphs.eventGraph.EventGraph;
import com.dat3m.dartagnan.verification.model.EventData;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/*
IMPORTANT NOTE: As opposed to all other graphs, a Subgraph is only a snapshot of an already existing graph that
gets populated on construction and won't perform any updates. In particular, it should not be used
in a GraphHierarchy as it cannot perform propagation.

Note: This seems to perform better than a virtualized Subgraph.
*/
public class MaterializedSubgraph extends MaterializedGraph {

    private final EventGraph sourceGraph;

    @Override
    public List<EventGraph> getDependencies() {
        return Collections.singletonList(sourceGraph);
    }

    public MaterializedSubgraph(EventGraph source, Collection<EventData> events) {
        sourceGraph = source;
        simpleGraph.constructFromModel(sourceGraph.getModel());

        for (EventData e : events) {
            sourceGraph.outEdgeStream(e).filter(edge -> events.contains(edge.getSecond())).forEach(simpleGraph::add);
        }

    }

    @Override
    public void backtrack() { }

}

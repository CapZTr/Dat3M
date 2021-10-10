package com.dat3m.dartagnan.analysis.saturation.graphs;

import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.Edge;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.RelationGraph;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.basic.SimpleCoherenceGraph;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.binary.CompositionGraph;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.binary.DifferenceGraph;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.binary.IntersectionGraph;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.binary.UnionGraph;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.constraints.AcyclicityConstraint;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.constraints.Constraint;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.constraints.EmptinessConstraint;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.constraints.IrreflexivityConstraint;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.stat.*;
import com.dat3m.dartagnan.analysis.saturation.graphs.relationGraphs.unary.*;
import com.dat3m.dartagnan.verification.VerificationTask;
import com.dat3m.dartagnan.verification.model.ExecutionModel;
import com.dat3m.dartagnan.wmm.axiom.Axiom;
import com.dat3m.dartagnan.wmm.relation.Relation;
import com.dat3m.dartagnan.wmm.relation.base.RelRMW;
import com.dat3m.dartagnan.wmm.relation.base.memory.RelCo;
import com.dat3m.dartagnan.wmm.relation.base.memory.RelLoc;
import com.dat3m.dartagnan.wmm.relation.base.memory.RelRf;
import com.dat3m.dartagnan.wmm.relation.base.stat.*;
import com.dat3m.dartagnan.wmm.relation.binary.RelComposition;
import com.dat3m.dartagnan.wmm.relation.binary.RelIntersection;
import com.dat3m.dartagnan.wmm.relation.binary.RelMinus;
import com.dat3m.dartagnan.wmm.relation.binary.RelUnion;
import com.dat3m.dartagnan.wmm.relation.unary.RelInverse;
import com.dat3m.dartagnan.wmm.relation.unary.RelRangeIdentity;
import com.dat3m.dartagnan.wmm.relation.unary.RelTrans;
import com.dat3m.dartagnan.wmm.relation.unary.RelTransRef;
import com.google.common.collect.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.dat3m.dartagnan.wmm.relation.RelationNameRepository.*;

public class ExecutionGraph {

    // These graphs are only relevant for data, ctrl and addr, all of which have a special event graph (see below)
    //TODO: We need a better way to handle the composed relations (in case we rename them as well?)
    private static final Set<String> EXCLUDED_RELS = ImmutableSet.of(
            IDD, IDDTRANS, CTRLDIRECT, ADDRDIRECT, String.format("(%s;%s)", IDDTRANS, ADDRDIRECT),
            String.format("(%s+(%s;%s))", ADDRDIRECT, IDDTRANS, ADDRDIRECT),
            String.format("(%s;%s)", IDDTRANS, CTRLDIRECT)
    );


    // These relations have special event graphs associated with them
    private static final Set<String> SPECIAL_RELS = ImmutableSet.of(ADDR, DATA, CTRL, CRIT);


    // ================== Fields =====================

    // All following variables can be considered "final".
    // Some are not declared final for purely technical reasons but all of them get
    // assigned during construction.

    private final VerificationTask verificationTask;
    private final BiMap<Relation, RelationGraph> relationGraphMap;
    private final BiMap<Axiom, Constraint> constraintMap;
    private GraphHierarchy graphHierarchy;

    private RelationGraph poGraph;
    private RelationGraph rfGraph;
    private RelationGraph coGraph;
    private RelationGraph locGraph;

    /*
        A non-transitive version of <coGraph> that is maintained by the Saturation procedure.
        We use it to distinguish between directly added coherences and coherences that were transitively derived.
        We maintain the invariant co = sco^+ at all times.
     */
    private RelationGraph scoGraph;

    // =================================================

    // ============= Construction & Init ===============

    public ExecutionGraph(VerificationTask verificationTask) {
        this.verificationTask = verificationTask;
        relationGraphMap = HashBiMap.create();
        constraintMap = HashBiMap.create();
        constructMappings();
    }

    public void initializeFromModel(ExecutionModel executionModel) {
        graphHierarchy.constructFromModel(executionModel);
        constraintMap.values().forEach(x -> x.initialize(executionModel));
    }

    // --------------------------------------------------

    private void constructMappings() {
        // We make sure to compute graphs along the dependency order
        // TODO: Is the order really important?
        Set<RelationGraph> graphs = new HashSet<>();
        for (Relation rel : verificationTask.getRelationDependencyGraph().getNodeContents()) {
            if (!EXCLUDED_RELS.contains(rel.getName())) {
                RelationGraph graph = getOrCreateGraphFromRelation(rel);
                graphs.add(graph);
            }
        }
        graphHierarchy = new GraphHierarchy(graphs);

        for (Axiom axiom : verificationTask.getAxioms()) {
            RelationGraph graph = getOrCreateGraphFromRelation(axiom.getRelation());
            Constraint constraint = getOrCreateConstraintFromAxiom(axiom);
            graphHierarchy.addGraphListener(graph, constraint);
        }
    }

    // =================================================

    // ================ Accessors =======================

    public VerificationTask getVerificationTask() { return verificationTask; }

    public BiMap<Relation, RelationGraph> getRelationGraphMap() {
        return Maps.unmodifiableBiMap(relationGraphMap);
    }

    public BiMap<Axiom, Constraint> getConstraintMap() {
        return Maps.unmodifiableBiMap(constraintMap);
    }

    public RelationGraph getProgramOrderGraph() { return poGraph; }
    public RelationGraph getReadFromGraph() { return rfGraph; }
    public RelationGraph getLocationGraph() { return locGraph; }
    public RelationGraph getCoherenceGraph() { return coGraph; }
    public RelationGraph getSimpleCoherenceGraph() { return scoGraph; }

    public RelationGraph getEventGraph(Relation rel) {
        return relationGraphMap.get(rel);
    }

    public Collection<RelationGraph> getEventGraphs() {
        return graphHierarchy.getGraphList();
    }

    public Constraint getConstraint(Axiom axiom) {
        return constraintMap.get(axiom);
    }

    public Collection<Constraint> getConstraints() {
        return constraintMap.values();
    }

    // ====================================================

    // ==================== Mutation ======================

    // For now we only allow refinement on co-edges.
    // We might want to add similar features for other linear orders (i.e. user defined orders)
    // We also assume, that the non-transitive write order is defined.
    public boolean addCoherenceEdges(Edge coEdge) {
        return addCoherenceEdges(ImmutableList.of(coEdge));
    }

    public boolean addCoherenceEdges(Collection<Edge> coEdges) {
        if ( scoGraph == null) {
            return false;
        }
        graphHierarchy.addEdgesAndPropagate(scoGraph, coEdges);
        return true;
    }

    public void backtrack() {
        graphHierarchy.backtrack();
    }

    // =======================================================


    //=================== Reading the WMM ====================

    private Constraint getOrCreateConstraintFromAxiom(Axiom axiom) {
        if (constraintMap.containsKey(axiom)) {
            return constraintMap.get(axiom);
        }

        Constraint constraint;
        RelationGraph innerGraph = getOrCreateGraphFromRelation(axiom.getRelation());
        if (axiom.isAcyclicity()) {
            constraint = new AcyclicityConstraint(innerGraph);
        } else if (axiom.isEmptiness()) {
            constraint = new EmptinessConstraint(innerGraph);
        } else if (axiom.isIrreflexivity()) {
            constraint = new IrreflexivityConstraint(innerGraph);
        } else {
            throw new UnsupportedOperationException("The axiom " + axiom + " is not recognized.");
        }

        constraintMap.put(axiom, constraint);
        return constraint;
    }

    private RelationGraph getOrCreateGraphFromRelation(Relation rel) {
        if (relationGraphMap.containsKey(rel)) {
            return relationGraphMap.get(rel);
        }

        RelationGraph graph;
        Class<?> relClass = rel.getClass();

        // ===== Filter special relations ======
        if (SPECIAL_RELS.contains(rel.getName())) {
            switch (rel.getName()) {
                case CTRL:
                    graph = new CtrlDepGraph();
                    break;
                case DATA:
                    graph = new DataDepGraph();
                    break;
                case ADDR:
                    graph = new AddrDepGraph();
                    break;
                case CRIT:
                    graph = new RcuGraph();
                    break;
                default:
                    throw new UnsupportedOperationException(rel.getName() + " is marked as special relation but has associated graph.");
            }
        } else if (relClass == RelRf.class) {
            graph = rfGraph = new ReadFromGraph();
        } else if (relClass == RelLoc.class) {
            graph = locGraph = new LocationGraph();
        } else if (relClass == RelPo.class) {
            graph = poGraph = new ProgramOrderGraph();
        } else if (relClass == RelCo.class) {
            graph = coGraph = new TransitiveGraph(scoGraph = new SimpleCoherenceGraph());
            scoGraph.setName("_sco");
        } else if (rel.isRecursiveRelation()) {
            RecursiveGraph recGraph = new RecursiveGraph();
            recGraph.setName(rel.getName() + "_rec");
            relationGraphMap.put(rel, recGraph);
            recGraph.setConcreteGraph(getOrCreateGraphFromRelation(rel.getInner()));
            return recGraph;
        } else if (rel.isUnaryRelation()) {
            Relation innerRelation = rel.getInner();
            RelationGraph innerGraph = getOrCreateGraphFromRelation(innerRelation);
            // A safety check because recursion might have computed this RelationGraph already
            if (relationGraphMap.containsKey(rel)) {
                return relationGraphMap.get(rel);
            }

            if (relClass == RelInverse.class) {
                graph = new InverseGraph(innerGraph);
            } else if (relClass == RelTrans.class) {
                graph = new TransitiveGraph(innerGraph);
            } else if (relClass == RelRangeIdentity.class) {
                graph = new RangeIdentityGraph(innerGraph);
            } else if (relClass == RelTransRef.class) {
                RelTrans relTrans = new RelTrans(innerRelation);
                relTrans.initialise(verificationTask, null); // A little sketchy
                RelationGraph transGraph = getOrCreateGraphFromRelation(relTrans);
                graph = new ReflexiveClosureGraph(transGraph);
            } else {
                throw new UnsupportedOperationException(relClass.toString() + " has no associated graph yet.");
            }
        } else if (rel.isBinaryRelation()) {
            RelationGraph first = getOrCreateGraphFromRelation(rel.getFirst());
            RelationGraph second = getOrCreateGraphFromRelation(rel.getSecond());

            // A safety check because recursion might have computed this RelationGraph already
            if (relationGraphMap.containsKey(rel)) {
                return relationGraphMap.get(rel);
            }

            if (relClass == RelUnion.class) {
                graph = new UnionGraph(first, second);
            } else if (relClass == RelIntersection.class) {
                graph = new IntersectionGraph(first, second);
            } else if (relClass == RelComposition.class) {
                graph = new CompositionGraph(first, second);
            } else if (relClass == RelMinus.class) {
                graph = new DifferenceGraph(first, second);
            } else {
                throw new UnsupportedOperationException(relClass.toString() + " has no associated graph yet.");
            }
        } else if (rel.isStaticRelation()) {
            if (relClass == RelCartesian.class) {
                RelCartesian cartRel = (RelCartesian)rel;
                graph = new CartesianGraph(cartRel.getFirstFilter(), cartRel.getSecondFilter());
            } else if (relClass == RelRMW.class) {
                graph = new RMWGraph();
            } else if (relClass == RelExt.class) {
                graph = new ExternalGraph();
            } else if (relClass == RelInt.class) {
                graph = new InternalGraph();
            } else if (relClass == RelFencerel.class) {
                graph = new FenceGraph(((RelFencerel) rel).getFenceName());
            } else if (relClass == RelSetIdentity.class) {
                graph = new SetIdentityGraph(((RelSetIdentity) rel).getFilter());
            } else if (relClass == RelId.class) {
                graph = new IdentityGraph();
            } else if (relClass == RelEmpty.class) {
                graph = new EmptyGraph();
            } else {
                // This is a fallback for all unimplemented static graphs
                graph = new StaticDefaultRelationGraph(rel);
            }
        } else {
            throw new UnsupportedOperationException(relClass.toString() + " has no associated graph yet.");
        }

        graph.setName(rel.getName());
        relationGraphMap.put(rel, graph);
        return graph;
    }

    // =======================================================


}
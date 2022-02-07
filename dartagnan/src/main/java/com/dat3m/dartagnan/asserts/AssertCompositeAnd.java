package com.dat3m.dartagnan.asserts;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.SolverContext;

public class AssertCompositeAnd extends AbstractAssert {

    private final AbstractAssert a1;
    private final AbstractAssert a2;

    public AssertCompositeAnd(AbstractAssert a1, AbstractAssert a2){
        this.a1 = a1;
        this.a2 = a2;
    }

    @Override
    public BooleanFormula encode(SolverContext ctx) {
        return ctx.getFormulaManager().getBooleanFormulaManager().and(a1.encode(ctx), a2.encode(ctx));
    }

    @Override
    public String toString() {
        return a1 + " && " + a2;
    }
}

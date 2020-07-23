package com.dat3m.dartagnan.expression;

import com.google.common.collect.ImmutableSet;
import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.dat3m.dartagnan.expression.op.BOpBin;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.Event;

public class BExprBin extends BExpr {

    private final ExprInterface b1;
    private final ExprInterface b2;
    private final BOpBin op;

    public BExprBin(ExprInterface b1, BOpBin op, ExprInterface b2) {
        this.b1 = b1;
        this.b2 = b2;
        this.op = op;
    }

    @Override
    public BoolExpr toZ3Bool(Event e, Context ctx, boolean bp) {
        return op.encode(b1.toZ3Bool(e, ctx, bp), b2.toZ3Bool(e, ctx, bp), ctx);
    }

    @Override
    public Expr getLastValueExpr(Context ctx, boolean bp){
        BoolExpr expr1 = bp ? 
        					ctx.mkBVSGT((BitVecExpr)b1.getLastValueExpr(ctx, bp), ctx.mkBV(1,32)) : 
        					ctx.mkGt((IntExpr)b1.getLastValueExpr(ctx, bp), ctx.mkInt(1));
        BoolExpr expr2 = bp ? 
                			ctx.mkBVSGT((BitVecExpr)b2.getLastValueExpr(ctx, bp), ctx.mkBV(1,32)) : 
                			ctx.mkGt((IntExpr)b2.getLastValueExpr(ctx, bp), ctx.mkInt(1));
        return ctx.mkITE(op.encode(expr1, expr2, ctx), ctx.mkInt(1), ctx.mkInt(0));
    }

    @Override
    public ImmutableSet<Register> getRegs() {
        return new ImmutableSet.Builder<Register>().addAll(b1.getRegs()).addAll(b2.getRegs()).build();
    }

    @Override
    public String toString() {
        return "(" + b1 + " " + op + " " + b2 + ")";
    }

    @Override
    public boolean getBoolValue(Event e, Context ctx, Model model, boolean bp){
        return op.combine(b1.getBoolValue(e, ctx, model, bp), b2.getBoolValue(e, ctx, model, bp));
    }

    @Override
	public IConst reduce() {
		int v1 = b1.reduce().getValue();
		int v2 = b2.reduce().getValue();
        switch(op) {
        case AND:
        	return new IConst(v1 == 1 ? v2 : 0);
        case OR:
        	return new IConst(v1 == 1 ? 1 : v2);
        }
        throw new UnsupportedOperationException("Reduce not supported for " + this);
	}
}

package com.dat3m.dartagnan.program.memory;

import com.google.common.collect.ImmutableSet;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.dat3m.dartagnan.expression.ExprInterface;
import com.dat3m.dartagnan.expression.IConst;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.Event;

public class Address extends IConst implements ExprInterface {

    private final int index;
    private Integer constValue;

    Address(int index){
        super(index);
        this.index = index;
    }

    @Override
    public ImmutableSet<Register> getRegs(){
        return ImmutableSet.of();
    }

    @Override
    public Expr toZ3NumExpr(Event e, Context ctx, boolean bp){
        return toZ3NumExpr(ctx, bp);
    }

    @Override
    public Expr getLastValueExpr(Context ctx, boolean bp){
        return toZ3NumExpr(ctx, bp);
    }

    public Expr getLastMemValueExpr(Context ctx, boolean bp){
        return bp ? ctx.mkBVConst("last_val_at_memory_" + index, 32) : ctx.mkIntConst("last_val_at_memory_" + index);
    }

    @Override
    public BoolExpr toZ3Bool(Event e, Context ctx, boolean bp){
        return ctx.mkTrue();
    }

    @Override
    public String toString(){
        return "&mem" + index;
    }

    @Override
    public int hashCode(){
        return index;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        return index == ((Address)obj).index;
    }

    @Override
    public Expr toZ3NumExpr(Context ctx, boolean bp){
		return bp ? ctx.mkBVConst("memory_" + index, 32) : ctx.mkIntConst("memory_" + index);
    }

    @Override
    public int getIntValue(Event e, Context ctx, Model model, boolean bp){
        return Integer.parseInt(model.getConstInterp(toZ3NumExpr(ctx, bp)).toString());
    }
    
    public boolean hasConstValue() {
    	return constValue != null;
    }
    
    public Integer getConstValue() {
    	return constValue;
    }
    
    public void setConstValue(Integer value) {
    	this.constValue = value;
    }
}

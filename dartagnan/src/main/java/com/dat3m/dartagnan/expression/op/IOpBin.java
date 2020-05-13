package com.dat3m.dartagnan.expression.op;

import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;

public enum IOpBin {
    PLUS, MINUS, MULT, DIV, MOD, AND, OR, XOR, L_SHIFT, R_SHIFT, AR_SHIFT;
	
	private boolean useBV = true;
	
	public void disableBV() {
		this.useBV = false;
	}

    @Override
    public String toString() {
        switch(this){
            case PLUS:
                return "+";
            case MINUS:
                return "-";
            case MULT:
                return "*";
            case DIV:
                return "/";
            case MOD:
                return "%";
            case AND:
                return "&";
            case OR:
                return "|";
            case XOR:
                return "^";
            case L_SHIFT:
                return "<<";
            case R_SHIFT:
                return ">>>";
            case AR_SHIFT:
                return ">>";
        }
        return super.toString();
    }

    public String toLinuxName(){
        switch(this){
            case PLUS:
                return "add";
            case MINUS:
                return "sub";
            case AND:
                return "and";
            case OR:
                return "or";
            case XOR:
                return "xor";
            default:
            	throw new UnsupportedOperationException("Linux op name is not defined for " + this);
        }
    }

    public IntExpr encode(IntExpr e1, IntExpr e2, Context ctx){
        switch(this){
            case PLUS:
                return (IntExpr)ctx.mkAdd(e1, e2);
            case MINUS:
                return (IntExpr)ctx.mkSub(e1, e2);
            case MULT:
                return (IntExpr)ctx.mkMul(e1, e2);
            case DIV:
                return (IntExpr)ctx.mkDiv(e1, e2);
            case MOD:
                return (IntExpr)ctx.mkMod(e1, e2);
            case AND:
            	if(useBV) {
                    return ctx.mkBV2Int(ctx.mkBVAND(ctx.mkInt2BV(32, e1), ctx.mkInt2BV(32, e2)), false);	
            	}
            	return (IntExpr)ctx.mkITE(ctx.mkEq(e1, ctx.mkInt(0)), ctx.mkInt(0), ctx.mkITE(ctx.mkEq(e2, ctx.mkInt(0)), ctx.mkInt(0), ctx.mkInt(1)));
            case OR:
            	if(useBV) {
                    return ctx.mkBV2Int(ctx.mkBVOR(ctx.mkInt2BV(32, e1), ctx.mkInt2BV(32, e2)), false);	
            	}
            	return (IntExpr)ctx.mkITE(ctx.mkNot(ctx.mkEq(e1, ctx.mkInt(0))), ctx.mkInt(1), ctx.mkITE(ctx.mkEq(e2, ctx.mkInt(0)), ctx.mkInt(0), ctx.mkInt(1)));
            case XOR:
            	if(useBV) {
                    return ctx.mkBV2Int(ctx.mkBVXOR(ctx.mkInt2BV(32, e1), ctx.mkInt2BV(32, e2)), false);
            	}
            	return (IntExpr)ctx.mkITE(ctx.mkEq(e1, ctx.mkInt(0)), ctx.mkITE(ctx.mkEq(e2, ctx.mkInt(0)), ctx.mkInt(0), ctx.mkInt(1)), ctx.mkITE(ctx.mkEq(e2, ctx.mkInt(0)), ctx.mkInt(1), ctx.mkInt(0)));
            case L_SHIFT:
            	return ctx.mkBV2Int(ctx.mkBVSHL(ctx.mkInt2BV(32, e1), ctx.mkInt2BV(32, e2)), false);
            case R_SHIFT:
            	return ctx.mkBV2Int(ctx.mkBVLSHR(ctx.mkInt2BV(32, e1), ctx.mkInt2BV(32, e2)), false);
            case AR_SHIFT:
            	return ctx.mkBV2Int(ctx.mkBVASHR(ctx.mkInt2BV(32, e1), ctx.mkInt2BV(32, e2)), false);
            default:
                throw new UnsupportedOperationException("Encoding of not supported for IOpBin " + this);
        }
    }

    public int combine(int a, int b){
        switch(this){
            case PLUS:
                return a + b;
            case MINUS:
                return a - b;
            case MULT:
                return a * b;
            case DIV:
                return a / b;
            case MOD:
                return a % b;
            case AND:
                return a & b;
            case OR:
                return a | b;
            case XOR:
                return a ^ b;
            case L_SHIFT:
                return a << b;
            case R_SHIFT:
                return a >>> b;
            case AR_SHIFT:
                return a >> b;
        }
        throw new UnsupportedOperationException("Illegal operator " + this + " in IOpBin");
    }
}

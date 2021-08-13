package com.dat3m.dartagnan.program.memory;

import com.dat3m.dartagnan.expression.processing.ExpressionVisitor;
import com.google.common.collect.ImmutableSet;

import java.math.BigInteger;

import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.Formula;
import org.sosy_lab.java_smt.api.IntegerFormulaManager;
import org.sosy_lab.java_smt.api.Model;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;
import org.sosy_lab.java_smt.api.SolverContext;

import com.dat3m.dartagnan.expression.ExprInterface;
import com.dat3m.dartagnan.expression.IConst;
import com.dat3m.dartagnan.expression.IExpr;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.event.Init;
import com.dat3m.dartagnan.program.event.Load;
import com.dat3m.dartagnan.program.event.MemEvent;
import com.dat3m.dartagnan.program.event.Store;

public class Location implements ExprInterface {

	public static final BigInteger DEFAULT_INIT_VALUE = BigInteger.ZERO;

	private final String name;
	private final Address address;

	public Location(String name, Address address) {
		this.name = name;
		this.address = address;
	}
	
	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode(){
		return address.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null || getClass() != obj.getClass())
			return false;

		return address.hashCode() == obj.hashCode();
	}

	@Override
	public ImmutableSet<Register> getRegs(){
		return ImmutableSet.of();
	}

	@Override
	public ImmutableSet<Location> getLocs() {
		return ImmutableSet.of(this);
	}

	@Override
	public Formula getLastValueExpr(SolverContext ctx){
		return address.getLastMemValueExpr(ctx);
	}

	@Override
	public Formula toIntFormula(Event e, SolverContext ctx){
		if(e instanceof MemEvent){
			return ((MemEvent) e).getMemValueExpr();
		}
		throw new RuntimeException("Attempt to encode memory value for illegal event");
	}

	@Override
	public BooleanFormula toBoolFormula(Event e, SolverContext ctx){
		if(e instanceof MemEvent){
			IntegerFormulaManager imgr = ctx.getFormulaManager().getIntegerFormulaManager();
			return imgr.greaterThan((IntegerFormula)((MemEvent) e).getMemValueExpr(), imgr.makeNumber(BigInteger.ZERO));
		}
		throw new RuntimeException("Attempt to encode memory value for illegal event");
	}

	@Override
	public BigInteger getIntValue(Event e, Model model, SolverContext ctx){
		if(e instanceof Store){
			return ((Store) e).getMemValue().getIntValue(e, model, ctx);
		}
		if(e instanceof Init){
			return ((Init) e).getMemValue().getIntValue(e, model, ctx);
		}
		if(e instanceof Load){
			Register reg = ((Load) e).getResultRegister();
			return new BigInteger(model.evaluate(reg.toZ3IntResult(e, ctx)).toString());

		}
		throw new RuntimeException("Attempt to encode memory value for illegal event");
	}

	@Override
	public boolean getBoolValue(Event e, Model model, SolverContext ctx){
		if(e instanceof MemEvent){
			return ((MemEvent) e).getMemValue().getBoolValue(e, model, ctx);
		}
		throw new RuntimeException("Attempt to encode memory value for illegal event");
	}

	@Override
	public <T> T visit(ExpressionVisitor<T> visitor) {
		return visitor.visit(this);
	}

	@Override
	public IConst reduce() {
		throw new UnsupportedOperationException("Reduce not supported for " + this);
	}

	@Override
	public int getPrecision() {
		return address.getPrecision();
	}

	@Override
	public IExpr getBase() {
		throw new UnsupportedOperationException("getBase not supported for " + this);
	}
}

package com.dat3m.dartagnan.program.atomic.event;

import com.dat3m.dartagnan.expression.Atom;
import com.dat3m.dartagnan.expression.ExprInterface;
import com.dat3m.dartagnan.expression.IConst;
import com.dat3m.dartagnan.expression.IExpr;
import com.dat3m.dartagnan.program.EventFactory;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.*;
import com.dat3m.dartagnan.program.event.utils.RegReaderData;
import com.dat3m.dartagnan.program.event.utils.RegWriter;
import com.dat3m.dartagnan.program.utils.EType;
import com.dat3m.dartagnan.utils.recursion.RecursiveFunction;
import com.dat3m.dartagnan.wmm.utils.Arch;

import java.util.Arrays;
import java.util.LinkedList;

import static com.dat3m.dartagnan.expression.op.COpBin.EQ;
import static com.dat3m.dartagnan.expression.op.COpBin.NEQ;
import static com.dat3m.dartagnan.program.arch.aarch64.utils.Mo.*;
import static com.dat3m.dartagnan.program.atomic.utils.Mo.*;
import static com.dat3m.dartagnan.program.utils.EType.STRONG;
import static com.dat3m.dartagnan.wmm.utils.Arch.POWER;

public class AtomicCmpXchg extends AtomicAbstract implements RegWriter, RegReaderData {

    private final Register expected;

    public AtomicCmpXchg(Register register, IExpr address, Register expected, ExprInterface value, String mo, boolean strong) {
        super(address, register, value, mo);
        this.expected = expected;
        if(strong) {
        	addFilters(STRONG);
        }
    }

    public AtomicCmpXchg(Register register, IExpr address, Register expected, ExprInterface value, String mo) {
        this(register, address, expected, value, mo, false);
    }

    private AtomicCmpXchg(AtomicCmpXchg other){
        super(other);
        this.expected = other.expected;
    }

    //TODO: Override getDataRegs???

    @Override
    public String toString() {
    	String tag = is(STRONG) ? "_strong" : "_weak";
    	tag += mo != null ? "_explicit" : "";
        return resultRegister + " = atomic_compare_exchange" + tag + "(*" + address + ", " + expected + ", " + value + (mo != null ? ", " + mo : "") + ")";
    }

    // Unrolling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public AtomicCmpXchg getCopy(){
        return new AtomicCmpXchg(this);
    }


    // Compilation
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    protected RecursiveFunction<Integer> compileRecursive(Arch target, int nextId, Event predecessor, int depth) {
    	Load load;
    	Store store;
    	LinkedList<Event> events = new LinkedList<>();
        switch(target) {
            case NONE:
            case TSO: {
                Register dummy = new Register(null, resultRegister.getThreadId(), resultRegister.getPrecision());
                load = EventFactory.newRMWLoad(dummy, address, mo);
                Local casResult = EventFactory.newLocal(resultRegister, new Atom(dummy, EQ, expected));
                Label fail = EventFactory.newLabel("CAS_fail");
                Label endCas = EventFactory.newLabel("CAS_end");
                CondJump branch = EventFactory.newJump(new Atom(resultRegister, NEQ, IConst.ONE), fail);
                store = EventFactory.newRMWStore(load, address, value, mo);
                CondJump jumpToEnd = EventFactory.newGoto(endCas);
                Local updateReg = EventFactory.newLocal(expected, dummy);
                events.addAll(Arrays.asList(load, casResult, branch, store, jumpToEnd, fail, updateReg, endCas));
                break;
            }
            case POWER:
            case ARM8: {
                String loadMo;
                String storeMo;
                switch (mo) {
                    case SC:
                    case ACQ_REL:
                        loadMo = ACQ;
                        storeMo = REL;
                        break;
                    case ACQUIRE:
                        loadMo = ACQ;
                        storeMo = RX;
                        break;
                    case RELEASE:
                        loadMo = RX;
                        storeMo = REL;
                        break;
                    case RELAXED:
                        loadMo = RX;
                        storeMo = RX;
                        break;
                    default:
                        throw new UnsupportedOperationException("Compilation to " + target + " is not supported for " + this);
                }

                Register dummy = new Register(null, resultRegister.getThreadId(), resultRegister.getPrecision());
                load = EventFactory.newRMWLoadExclusive(dummy, address, loadMo);
                Local casResult = EventFactory.newLocal(resultRegister, new Atom(dummy, EQ, expected));
                Label fail = EventFactory.newLabel("CAS_fail");
                Label endCas = EventFactory.newLabel("CAS_end");
                CondJump branch = EventFactory.newJump(new Atom(resultRegister, NEQ, IConst.ONE), fail);
                // ---- CAS success ----
                store = EventFactory.newRMWStoreExclusive(address, value, storeMo, is(STRONG));
                Register statusReg = new Register("status(" + getOId() + ")", resultRegister.getThreadId(), resultRegister.getPrecision());
                ExecutionStatus status = EventFactory.newExecutionStatus(statusReg, store);
                Event jumpStoreFail = EventFactory.newJump(new Atom(statusReg, EQ, IConst.ONE), (Label) getThread().getExit());
                jumpStoreFail.addFilters(EType.BOUND);
                CondJump jumpToEndCas = EventFactory.newGoto(endCas);
                // ---------------------
                // ---- CAS Fail ----
                Local updateReg = EventFactory.newLocal(expected, dummy);
               
                // --- Add Fence before under POWER ---
                if(target.equals(POWER)) {
                    if (mo.equals(SC)) {
                        events.addFirst(EventFactory.Power.newSyncBarrier());
                    } else if (storeMo.equals(REL)) {
                        events.addFirst(EventFactory.Power.newLwSyncBarrier());
                    }                	
                }
                // --- Add success events ---
                events.addAll(Arrays.asList(load, casResult, branch, store, status, jumpStoreFail));
                // --- Add Fence after success under POWER ---
                if (target.equals(POWER) && loadMo.equals(ACQ)) {
                    events.addLast(EventFactory.Power.newISyncBarrier());
                }
                // --- Add fail events + exit ---
                events.addAll(Arrays.asList(jumpToEndCas, fail, updateReg, endCas));
                break;
            }
            default:
                throw new UnsupportedOperationException("Compilation to " + target + " is not supported for " + this);
        }
        return compileSequenceRecursive(target, nextId, predecessor, events, depth + 1);
    }
}

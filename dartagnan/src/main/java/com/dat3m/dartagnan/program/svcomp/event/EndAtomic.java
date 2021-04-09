package com.dat3m.dartagnan.program.svcomp.event;

import static com.dat3m.dartagnan.program.utils.EType.SVCOMPATOMIC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.event.CondJump;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.utils.EType;
import com.dat3m.dartagnan.utils.equivalence.BranchEquivalence;
import com.dat3m.dartagnan.utils.recursion.RecursiveAction;
import com.dat3m.dartagnan.verification.VerificationTask;
import com.google.common.collect.ImmutableList;
import com.microsoft.z3.Context;

public class EndAtomic extends Event {

	protected BeginAtomic begin;
	protected List<Event> enclosedEvents;

	public EndAtomic(BeginAtomic begin) {
        this.begin = begin;
    	this.begin.addListener(this);
        addFilters(EType.RMW, SVCOMPATOMIC);
    }

    protected EndAtomic(EndAtomic other){
		super(other);
		this.begin = other.getBegin();
		this.begin.addListener(this);
	}

    public BeginAtomic getBegin(){
    	return begin;
    }
    
    public List<Event> getBlock(){
    	if (cId < 0) {
    		throw new IllegalStateException("The program needs to get compiled first");
		}
    	return enclosedEvents;
    }

	@Override
	public void initialise(VerificationTask task, Context ctx) {
		super.initialise(task, ctx);
		//===== Temporary fix to rematch atomic blocks correctly =====
		BranchEquivalence eq = task.getBranchEquivalence();
		List<Event> begins = this.thread.getEvents()
				.stream().filter( x -> x instanceof BeginAtomic && eq.isReachableFrom(x, this))
				.collect(Collectors.toList());
		this.begin = (BeginAtomic) begins.get(begins.size() - 1);
		// =======================================================

		findEnclosedEvents();

	}

	private void findEnclosedEvents() {
    	enclosedEvents = new ArrayList<>();
		BranchEquivalence eq = task.getBranchEquivalence();
		BranchEquivalence.Class startClass = eq.getEquivalenceClass(begin);
		BranchEquivalence.Class endClass = eq.getEquivalenceClass(this);
		if (!startClass.getReachableClasses().contains(endClass)) {
			//throw new IllegalStateException("BeginAtomic can't reach EndAtomic");
			System.out.println("==== EndAtomic " + this.getCId() + " unreachable by BeginAtomic " + begin.getCId() + " ====");
			return;
		}

		for (BranchEquivalence.Class c : startClass.getReachableClasses()) {
			for (Event e : c) {
				if (begin.getCId() <= e.getCId() && e.getCId() <= this.getCId()) {
					if (!eq.isImplied(e, begin)) {
						System.out.println(e.toString() + " is inside atomic block but can be reached from the outside");
					}
					enclosedEvents.add(e);
					e.addFilters(EType.RMW);
				}
			}
		}
		enclosedEvents.sort(Comparator.naturalOrder());
		enclosedEvents = ImmutableList.copyOf(enclosedEvents);
	}

	@Override
    public String toString() {
    	return "end_atomic()";
    }
    
    @Override
    public void notify(Event begin) {
		if (this.oId > begin.getOId()) {
			this.begin = (BeginAtomic) begin;
		}
    }

    // Unrolling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
	public EndAtomic getCopy(){
		return new EndAtomic(this);
	}
}
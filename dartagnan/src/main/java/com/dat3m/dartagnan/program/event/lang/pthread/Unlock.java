package com.dat3m.dartagnan.program.event.lang.pthread;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.expression.Atom;
import com.dat3m.dartagnan.expression.IConst;
import com.dat3m.dartagnan.expression.IExpr;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.core.Event;
import com.dat3m.dartagnan.program.event.core.Label;
import com.dat3m.dartagnan.program.event.core.MemEvent;
import com.google.common.base.Preconditions;

import java.util.List;

import static com.dat3m.dartagnan.expression.op.COpBin.NEQ;
import static com.dat3m.dartagnan.program.event.EType.LOCK;
import static com.dat3m.dartagnan.program.event.EType.RMW;
import static com.dat3m.dartagnan.program.event.EventFactory.*;
import static com.dat3m.dartagnan.program.event.lang.catomic.utils.Mo.SC;

public class Unlock extends MemEvent {
	
	private final String name;
	private final Register reg;
	private Label label;
    private Label label4Copy;

	public Unlock(String name, IExpr address, Register reg, Label label){
		super(address, SC);
		this.name = name;
        this.reg = reg;
        this.label = label;
        this.label.addListener(this);
    }

	private Unlock(Unlock other){
		super(other);
		this.name = other.name;
        this.reg = other.reg;
		this.label = other.label4Copy;
		Event notifier = label != null ? label : other.label;
		notifier.addListener(this);
    }

    @Override
    public String toString() {
        return "pthread_mutex_unlock(&" + name + ")";
    }

    @Override
    public void notify(Event label) {
    	if(this.label == null) {
        	this.label = (Label)label;
    	} else if (oId > label.getOId()) {
    		this.label4Copy = (Label)label;
    	}
    }

    public Label getLabel() {
    	return label;
    }
    
    public Register getResultRegister() {
    	return reg;
    }
    
    // Unrolling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Unlock getCopy(){
        return new Unlock(this);
    }

    // Compilation
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Event> compile(Arch target) {
    	Preconditions.checkNotNull(target, "Target cannot be null");

        List<Event> events = eventSequence(
                newLoad(reg, address, mo),
                newJump(new Atom(reg, NEQ, IConst.ONE), label),
                newStore(address, IConst.ZERO, mo)
        );
        for(Event e : events) {
            e.addFilters(LOCK, RMW);
        }
        return events;
    }
}
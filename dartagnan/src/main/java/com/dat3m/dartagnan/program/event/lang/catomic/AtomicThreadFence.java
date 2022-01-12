package com.dat3m.dartagnan.program.event.lang.catomic;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.program.event.core.Event;
import com.dat3m.dartagnan.program.event.core.Fence;

import java.util.List;

import static com.dat3m.dartagnan.program.event.EventFactory.*;
import static com.dat3m.dartagnan.program.event.lang.catomic.utils.Mo.*;

public class AtomicThreadFence extends Fence {

    private final String mo;

    public AtomicThreadFence(String mo) {
        super("atomic_thread_fence");
        this.mo = mo;
    }

    private AtomicThreadFence(AtomicThreadFence other){
        super(other);
        this.mo = other.mo;
    }

    @Override
    public String toString() {
        return name + "(" + mo + ")";
    }


    // Unrolling
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public AtomicThreadFence getCopy(){
        return new AtomicThreadFence(this);
    }


    // Compilation
    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public List<Event> compile(Arch target) {
        Fence fence = null;
        switch (target) {
            case NONE:
                break;
            case TSO:
                fence = mo.equals(SC) ? X86.newMemoryFence() : null;
                break;
            case POWER:
                fence = mo.equals(ACQUIRE) || mo.equals(RELEASE) || mo.equals(ACQUIRE_RELEASE) || mo.equals(SC) ?
                        Power.newLwSyncBarrier() : null;
                break;
            case ARM8:
                fence = mo.equals(RELEASE) || mo.equals(ACQUIRE_RELEASE) || mo.equals(SC) ? Arm8.DMB.newISHBarrier()
                        : mo.equals(ACQUIRE) ? Arm8.DSB.newISHLDBarrier() : null;
                break;
            default:
                throw new UnsupportedOperationException("Compilation to " + target + " is not supported for " + getClass().getName());
        }
        return eventSequence(
                fence
        );
    }
}
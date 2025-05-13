package com.dat3m.dartagnan.verification.model.event;

import com.dat3m.dartagnan.program.event.core.Alloc;
import com.dat3m.dartagnan.verification.model.ThreadModel;


public class AllocModel extends DefaultEventModel implements RegReaderModel, RegWriterModel {
    public AllocModel(Alloc event, ThreadModel thread, int id) {
        super(event, thread, id);
    }
}

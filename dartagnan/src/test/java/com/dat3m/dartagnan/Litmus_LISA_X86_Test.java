package com.dat3m.dartagnan;

import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.utils.rules.Provider;
import com.dat3m.dartagnan.configuration.Arch;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;

@RunWith(Parameterized.class)
public class Litmus_LISA_X86_Test extends AbstractLitmusTest {

    @Parameterized.Parameters(name = "{index}: {0}, {1}")
    public static Iterable<Object[]> data() throws IOException {
		return buildLitmusTests("litmus/LISA/X86/");
    }

    @Override
    protected Provider<Arch> getTargetProvider() {
        return () -> Arch.TSO;
    }

    public Litmus_LISA_X86_Test(String path, Result expected) {
        super(path, expected);
    }
}
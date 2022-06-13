package com.dat3m.dartagnan.compilation;

import com.dat3m.dartagnan.utils.rules.Provider;
import com.dat3m.dartagnan.configuration.Arch;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;

@RunWith(Parameterized.class)
public class LKMM2PPCTest extends AbstractCompilationTest {

    @Parameterized.Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() throws IOException {
        return buildLitmusTests("litmus/LKMM/");
    }

    public LKMM2PPCTest(String path) {
        super(path);
    }

	@Override
	protected Provider<Arch> getSourceProvider() {
		return () -> Arch.LKMM;
	}

	@Override
	protected Provider<Arch> getTargetProvider() {
		return () -> Arch.POWER;
	}
}

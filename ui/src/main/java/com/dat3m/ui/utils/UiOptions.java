package com.dat3m.ui.utils;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.configuration.Method;
import com.dat3m.dartagnan.configuration.ProgressModel;
import com.dat3m.dartagnan.configuration.Property;
import org.sosy_lab.java_smt.SolverContextFactory.Solvers;

import java.util.EnumSet;
import java.util.Map;

public record UiOptions(Arch target, Method method, int bound, Solvers solver, int timeout, boolean showWitness,
                        String cflags, Map<String, String> config, EnumSet<Property> properties, ProgressModel progress) {
}
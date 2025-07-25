package com.dat3m.dartagnan.spirv.vulkan.benchmarks;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.encoding.ProverWithTracker;
import com.dat3m.dartagnan.parsers.cat.ParserCat;
import com.dat3m.dartagnan.parsers.program.ProgramParser;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.verification.VerificationTask;
import com.dat3m.dartagnan.verification.solving.AssumeSolver;
import com.dat3m.dartagnan.wmm.Wmm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sosy_lab.common.ShutdownManager;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.BasicLogManager;
import org.sosy_lab.java_smt.SolverContextFactory;
import org.sosy_lab.java_smt.api.SolverContext;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;

import static com.dat3m.dartagnan.configuration.Property.PROGRAM_SPEC;
import static com.dat3m.dartagnan.utils.ResourceHelper.getRootPath;
import static com.dat3m.dartagnan.utils.ResourceHelper.getTestResourcePath;
import static com.dat3m.dartagnan.utils.Result.FAIL;
import static com.dat3m.dartagnan.utils.Result.PASS;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SpirvAssertionsTest {

    private final String modelPath = getRootPath("cat/vulkan.cat");
    private final String programPath;
    private final int bound;
    private final Result expected;

    public SpirvAssertionsTest(String file, int bound, Result expected) {
        this.programPath = getTestResourcePath("spirv/vulkan/benchmarks/" + file);
        this.bound = bound;
        this.expected = expected;
    }

    @Parameterized.Parameters(name = "{index}: {0}, {1}, {2}")
    public static Iterable<Object[]> data() throws IOException {
        return Arrays.asList(new Object[][]{
                {"caslock-1.1.2.spvasm", 2, PASS},
                {"caslock-2.1.1.spvasm", 2, PASS},
                {"caslock-acq2rx.spvasm", 1, FAIL},
                {"caslock-rel2rx.spvasm", 1, FAIL},
                {"caslock-dv2wg-2.1.1.spvasm", 2, PASS},
                {"caslock-dv2wg-1.1.2.spvasm", 1, FAIL},
                {"ticketlock-1.1.2.spvasm", 1, PASS},
                {"ticketlock-2.1.1.spvasm", 1, PASS},
                {"ticketlock-acq2rx.spvasm", 1, FAIL},
                {"ticketlock-rel2rx.spvasm", 1, FAIL},
                {"ticketlock-dv2wg-2.1.1.spvasm", 2, PASS},
                {"ticketlock-dv2wg-1.1.2.spvasm", 1, FAIL},
                {"ttaslock-1.1.2.spvasm", 2, PASS},
                {"ttaslock-2.1.1.spvasm", 2, PASS},
                {"ttaslock-acq2rx.spvasm", 1, FAIL},
                {"ttaslock-rel2rx.spvasm", 1, FAIL},
                {"ttaslock-dv2wg-2.1.1.spvasm", 2, PASS},
                {"ttaslock-dv2wg-1.1.2.spvasm", 1, FAIL},

                {"xf-barrier-2.1.2.spvasm", 4, PASS},
                {"xf-barrier-3.1.3.spvasm", 9, PASS},
                // TODO: IMO should pass (spinloop handling?)
                // {"xf-barrier-1.1.2.spvasm", 2, PASS},
                {"xf-barrier-2.1.1.spvasm", 2, PASS},
                {"xf-barrier-fail1.spvasm", 4, FAIL},
                {"xf-barrier-fail2.spvasm", 4, FAIL},
                {"xf-barrier-fail3.spvasm", 4, FAIL},
                {"xf-barrier-fail4.spvasm", 4, FAIL},
                {"xf-barrier-weakest.spvasm", 4, FAIL},
        });
    }

    @Test
    public void test() throws Exception {
        try (SolverContext ctx = mkCtx(); ProverWithTracker prover = mkProver(ctx)) {
            assertEquals(expected, AssumeSolver.run(ctx, prover, mkTask()).getResult());
        }
    }

    private SolverContext mkCtx() throws InvalidConfigurationException {
        Configuration cfg = Configuration.builder().build();
        return SolverContextFactory.createSolverContext(
                cfg,
                BasicLogManager.create(cfg),
                ShutdownManager.create().getNotifier(),
                SolverContextFactory.Solvers.Z3);
    }

    private ProverWithTracker mkProver(SolverContext ctx) {
        return new ProverWithTracker(ctx, "", SolverContext.ProverOptions.GENERATE_MODELS);
    }

    private VerificationTask mkTask() throws Exception {
        VerificationTask.VerificationTaskBuilder builder = VerificationTask.builder()
                .withConfig(Configuration.builder().build())
                .withBound(bound)
                .withTarget(Arch.VULKAN);
        Program program = new ProgramParser().parse(new File(programPath));
        Wmm mcm = new ParserCat().parse(new File(modelPath));
        return builder.build(program, mcm, EnumSet.of(PROGRAM_SPEC));
    }
}

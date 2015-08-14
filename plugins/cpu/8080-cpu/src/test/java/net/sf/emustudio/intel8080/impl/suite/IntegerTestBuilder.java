package net.sf.emustudio.intel8080.impl.suite;

import net.sf.emustudio.cpu.testsuite.TestBuilder;
import net.sf.emustudio.cpu.testsuite.injectors.MemoryExpand;
import net.sf.emustudio.cpu.testsuite.runners.RunnerContext;
import net.sf.emustudio.intel8080.impl.suite.injectors.RegisterPair;
import net.sf.emustudio.intel8080.impl.suite.injectors.RegisterPairPSW;
import net.sf.emustudio.intel8080.impl.suite.verifiers.PCVerifier;
import net.sf.emustudio.intel8080.impl.suite.verifiers.RegisterPair_PSW_Verifier;
import net.sf.emustudio.intel8080.impl.suite.verifiers.RegisterPair_SP_Verifier;
import net.sf.emustudio.intel8080.impl.suite.verifiers.RegisterVerifier;

import java.util.function.Function;

public class IntegerTestBuilder extends TestBuilder<Integer, IntegerTestBuilder, CpuRunnerImpl, CpuVerifierImpl>  {

    public IntegerTestBuilder(CpuRunnerImpl cpuRunner, CpuVerifierImpl cpuVerifier) {
        super(cpuRunner, cpuVerifier);
    }

    public IntegerTestBuilder firstIsPair(int registerPair) {
        runner.injectFirst(new MemoryExpand(), new RegisterPair(registerPair));
        return this;
    }

    public IntegerTestBuilder secondIsPair(int registerPair) {
        runner.injectSecond(new MemoryExpand(), new RegisterPair(registerPair));
        return this;
    }

    public IntegerTestBuilder firstIsRegisterPairPSW(int registerPairPSW) {
        runner.injectFirst(new MemoryExpand(), new RegisterPairPSW(registerPairPSW));
        return this;
    }

    public IntegerTestBuilder secondIsRegisterPairPSW(int registerPairPSW) {
        runner.injectSecond(new MemoryExpand(), new RegisterPairPSW(registerPairPSW));
        return this;
    }

    public IntegerTestBuilder setRegister(int register, int value) {
        runner.injectFirst((tmpRunner, argument) -> cpuRunner.setRegister(register, value));
        return this;
    }

    public IntegerTestBuilder setPair(int registerPair, int value) {
        runner.injectFirst((tmpRunner, argument) -> cpuRunner.setRegisterPair(registerPair, value));
        return this;
    }

    public IntegerTestBuilder verifyPairAndPSW(int registerPair, Function<RunnerContext<Integer>, Integer> operation) {
        lastOperation = operation;
        verifiers.add(new RegisterPair_PSW_Verifier(cpuVerifier, operation, registerPair));
        return this;
    }

    public IntegerTestBuilder verifyRegister(int register, Function<RunnerContext<Integer>, Integer> operator) {
        lastOperation = operator;
        return verifyRegister(register);
    }

    public IntegerTestBuilder verifyRegister(int register) {
        if (lastOperation == null) {
            throw new IllegalStateException("Last operation is not set!");
        }
        verifiers.add(new RegisterVerifier<>(cpuVerifier, lastOperation, register));
        return this;
    }

    public IntegerTestBuilder verifyPair(int registerPair, Function<RunnerContext<Integer>, Integer> operator) {
        lastOperation = operator;
        verifiers.add(new RegisterPair_SP_Verifier<>(cpuVerifier, operator, registerPair));
        return this;
    }

    public IntegerTestBuilder verifyPC(Function<RunnerContext<Integer>, Integer> operator) {
        lastOperation = operator;
        verifiers.add(new PCVerifier(cpuVerifier, operator));
        return this;
    }


}
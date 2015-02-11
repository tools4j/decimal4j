package org.decimal4j.jmh;

import java.io.IOException;

import org.openjdk.jmh.runner.RunnerException;

/**
 * Base class for micro benchmarks based on the jmh library.
 */
abstract public class AbstractBenchmark {

	public static final int OPERATIONS_PER_INVOCATION = 100;

	protected static void run(Class<? extends AbstractBenchmark> benchmarkClass) throws RunnerException, IOException, InterruptedException {
		new JmhRunner(benchmarkClass).run();
	}
}

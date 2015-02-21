/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.jmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.results.format.ResultFormat;
import org.openjdk.jmh.results.format.ResultFormatFactory;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class JmhRunner {
	
	private final Class<?> benchmarkClass;

	public JmhRunner(Class<?> benchmarkClass) {
		if (benchmarkClass == null) {
			throw new NullPointerException("benchmarkClass cannot be null");
		}
		this.benchmarkClass = benchmarkClass;
	}
	public void run() throws RunnerException, IOException, InterruptedException {
		final Process process = Runtime.getRuntime().exec("java -cp ./build/libs/javasoft-decimal-1.0-jmh.jar " + JmhRunner.class.getName() + " " + benchmarkClass.getSimpleName());
		final Thread t1 = read(process.getInputStream());
		final Thread t2 = read(process.getErrorStream());
		if (0 != process.waitFor()) {
			System.err.println("FAILED");
		}
		t1.interrupt();
		t2.interrupt();
	}
	private static Thread read(InputStream inputStream) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		final Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					String line = reader.readLine();
					while (line != null && !isInterrupted()) {
						System.out.println(line);
						line = reader.readLine();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		};
		thread.start();
		return thread;
	}
	
	public static void main(String[] args) throws RunnerException {
		final Options opt = new OptionsBuilder()//
			.include(".*" + args[0] + ".*")//
			.mode(Mode.Throughput)//
			.measurementIterations(3)//
			.measurementBatchSize(1)//
			.measurementTime(TimeValue.milliseconds(1000))//
			.forks(1)//
			.timeUnit(TimeUnit.MICROSECONDS)//
			.warmupIterations(3)//
			.warmupTime(TimeValue.milliseconds(1000))//
			.build();
		final Collection<RunResult> runResult = new Runner(opt).run();
		final PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out));
		final ResultFormat resultFormat = ResultFormatFactory.getInstance(ResultFormatType.CSV, writer);
		resultFormat.writeOut(runResult);
		writer.flush();
	}

}

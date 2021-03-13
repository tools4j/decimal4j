/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2021 decimal4j (tools4j), Marco Terzer
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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
		final File jmhJar = findJmhJar();
		final Process process = Runtime.getRuntime().exec("java -cp " + jmhJar.getAbsolutePath() + " " + JmhRunner.class.getName() + " " + benchmarkClass.getName());
		final Reader r1 = new Reader(process.getInputStream());
		final Reader r2 = new Reader(process.getErrorStream());
		r1.start();
		r2.start();
		if (0 != process.waitFor()) {
			System.err.println("FAILED");
		}
		r1.await();
		r2.await();
	}
	
	private final File findJmhJar() {
		final File libDir = new File("./build/libs");
		final File[] files = libDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("-jmh.jar");
			}
		});
		if (files != null && files.length == 1) {
			return files[0];
		}
		throw new IllegalStateException("no jmh jar file found in '" + libDir.getAbsolutePath() + "', hint: run 'gradle jmhJar' first");
	}
	private static class Reader extends Thread {
		private final AtomicBoolean isReading = new AtomicBoolean(false);
		private final BufferedReader reader;
		public Reader(final InputStream inputStream) {
			this.reader = new BufferedReader(new InputStreamReader(inputStream));
		}
		@Override
		public void run() {
			try {
				String line = reader.readLine();
				while (line != null && !isInterrupted()) {
					isReading.set(true);
					System.out.println(line);
					line = reader.readLine();
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		public void await() throws InterruptedException {
			while (isReading.get()) {
				isReading.set(false);
				Thread.sleep(1000);
			}
			interrupt();
		}
	}
	
	private static String askRunAll() throws IOException {
		System.out.print("Do you want to run all jmh benchmarks? (can take approx. 3h!)[y/n]");
		final char ch = (char)System.in.read();
		System.out.println();
		if (Character.toLowerCase(ch) == 'y') {
			return ".*";
		}
		System.out.println("aborted.");
		System.exit(1);
		return null;//should not get here
	}
	public static void main(String[] args) throws RunnerException, IOException {
		final String include;
		if (args.length == 0) {
			include = askRunAll();
		} else {
			include = args[0];
		}
		final Options opt = new OptionsBuilder()//
			.include(include)//
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
		System.out.flush();
		final ResultFormat resultFormat = ResultFormatFactory.getInstance(ResultFormatType.CSV, System.out);
		resultFormat.writeOut(runResult);
		System.out.flush();
	}

}

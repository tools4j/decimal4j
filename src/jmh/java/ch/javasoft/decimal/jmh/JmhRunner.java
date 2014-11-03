package ch.javasoft.decimal.jmh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;
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
	
	private final Class<? extends AbstractBenchmark> benchmarkClass;
	public JmhRunner(Class<? extends AbstractBenchmark> benchmarkClass) {
		this.benchmarkClass = Objects.requireNonNull(benchmarkClass, "benchmarkClass cannot be null");
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

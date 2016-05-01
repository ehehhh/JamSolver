package ee.subscribe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

/**
 * Google Code Jam boilerplate reducer.
 *
 * @author ehehhh@gmail.com
 */
public class JamSolver {

	private static final int VARYING_LINES_PER_TEST_CASE = -1;

	private final int linesPerTestCase;
	private final BufferedWriter writer;
	private final BufferedReader reader;
	private final boolean timeExecution;

	private boolean started = false;

	/**
	 * @param args
	 * 		<br />
	 * 		&nbsp;&nbsp;&nbsp;&nbsp;<code>args[0]</code> - input file path<br />
	 * 		&nbsp;&nbsp;&nbsp;&nbsp;<code>args[1]</code> - output file path
	 * @return JamSolverRequiredFields object (next step in the builder)
	 * @throws Throwable
	 */
	public static JamSolverRequiredFields withIO(String args[]) throws Throwable {
		if (args.length != 2) {
			throw new Exception("args[0] should be the input file path, args[1] should be the output file path");
		}
		return new JamSolverRequiredFields(args[0], args[1]);
	}

	/**
	 * @param inPath
	 * 		input file path
	 * @param outPath
	 * 		output file path
	 * @return JamSolverRequiredFields object (next step in the builder)
	 * @throws Throwable
	 */
	public static JamSolverRequiredFields withIO(String inPath, String outPath) throws Throwable {
		if (inPath == null || inPath.isEmpty()) {
			throw new Exception("Parameter inPath cannot be null or empty");
		}
		if (outPath == null || outPath.isEmpty()) {
			throw new Exception("Parameter outPath cannot be null or empty");
		}
		return new JamSolverRequiredFields(inPath, outPath);
	}

	private JamSolver(String inFile, String outFile, int linesPerTestCase, boolean timeExecution) throws Throwable {
		this.timeExecution = timeExecution;
		this.linesPerTestCase = linesPerTestCase;

		Path inPath = Paths.get("", inFile);
		if (Files.notExists(inPath)) {
			throw new IOException("Input file does not exist!");
		}
		if (!Files.isReadable(inPath)) {
			throw new IOException("Input file is not readable!");
		}
		reader = Files.newBufferedReader(inPath);

		Path outPath = Paths.get("", outFile);
		if (Files.exists(outPath) && !Files.deleteIfExists(outPath)) {
			throw new IOException("Output file could not be deleted!");
		}
		writer = Files.newBufferedWriter(outPath, StandardOpenOption.CREATE);
	}

	void solve(Solver solver) throws Throwable {
		if (started) {
			throw new Exception("Solver has already been started once!");
		} else {
			started = true;
		}
		long allTestCasesStartTime = System.nanoTime();
		long numOfTestCases = Long.parseLong(reader.readLine());
		long testCaseCount = 0;
		try {
			while (testCaseCount < numOfTestCases) {
				testCaseCount++;
				int linesPerCase = linesPerTestCase != VARYING_LINES_PER_TEST_CASE ? linesPerTestCase : Integer.parseInt(reader.readLine());
				String[] testCase = new String[linesPerCase];
				int count = 0;
				while (count < linesPerCase) {
					String line = reader.readLine();
					if (line != null) {
						testCase[count++] = line;
					} else {
						throw new IOException("readLine() got null. Have you specified the correct lines per test case?");
					}
				}
				long testCaseStartTime = System.nanoTime();
				solveAndWrite(solver, testCase, testCaseCount, testCaseCount == numOfTestCases);
				if (timeExecution) {
					long testCaseTime = TimeUnit.MILLISECONDS.convert(System.nanoTime() - testCaseStartTime, TimeUnit.NANOSECONDS);
					if (testCaseTime > 100) {
						System.out.println("Test case #" + testCaseCount + " solved in " + getHumanReadableTimeFromMs(testCaseTime));
					}
				}
			}
			if (timeExecution) {
				System.out.println("* All test cases executed in " + getHumanReadableTimeFromMs(TimeUnit.MILLISECONDS
						.convert(System.nanoTime() - allTestCasesStartTime, TimeUnit.NANOSECONDS)) + " *");
			}
		} catch (Exception e) {
			System.err.println("Error reading input file!");
			e.printStackTrace();
		}
		reader.close();
		writer.flush();
		writer.close();
	}

	private void solveAndWrite(Solver solver, String[] testCase, long testCaseCount, boolean isLast) throws Throwable {
		writer.write(String.format("Case #%s: %s", testCaseCount, solver.solve(testCase)));
		if (!isLast) {
			writer.newLine();
		}
	}

	private String getHumanReadableTimeFromMs(long timeInMs) {
		if (timeInMs > 10000) {
			return "~" + TimeUnit.SECONDS.convert(timeInMs, TimeUnit.MILLISECONDS) + " seconds";
		} else {
			return timeInMs + " milliseconds";
		}
	}

	public static class JamSolverRequiredFields {

		private final String inFile;
		private final String outFile;

		private JamSolverRequiredFields(String inFile, String outFile) {
			this.inFile = inFile;
			this.outFile = outFile;
		}

		/**
		 * When all of the test cases have a constant number of lines, this method should be used.
		 * <br /><br />
		 * <i>Example input file with 2 lines per test case:</i><br />
		 * <code>
		 * 3<br />
		 * testcase1_line1<br />
		 * testcase1_line2<br />
		 * testcase2_line1<br />
		 * testcase2_line2<br />
		 * testcase3_line1<br />
		 * testcase3_line2<br />
		 * </code>
		 *
		 * @param linesPerTestCase
		 * 		lines per test case has to be >= 1
		 * @return JamSolverOptionalFields builder object
		 * @throws Throwable
		 */
		public JamSolverOptionalFields withLinesPerTestCase(int linesPerTestCase) throws Throwable {
			if (linesPerTestCase < 1) {
				throw new Exception("Lines per test case must be >=1");
			}
			return new JamSolverOptionalFields(inFile, outFile, linesPerTestCase);
		}

		/**
		 * When the test cases have a varying number of lines, this method should be used.<br /><br />
		 * <i>Example input file with varying lines per test case:</i><br />
		 * <code>
		 * 3<br />
		 * 2<br />
		 * testcase1_line1<br />
		 * testcase1_line2<br />
		 * 1<br />
		 * testcase2_line1<br />
		 * 4<br />
		 * testcase3_line1<br />
		 * testcase3_line2<br />
		 * testcase3_line3<br />
		 * testcase3_line4
		 * </code>
		 *
		 * @return JamSolverOptionalFields builder object
		 */
		public JamSolverOptionalFields withVaryingLinesPerTestCase() {
			return new JamSolverOptionalFields(inFile, outFile, VARYING_LINES_PER_TEST_CASE);
		}
	}

	public static class JamSolverOptionalFields {

		private final String inFile;
		private final String outFile;
		private final int linesPerTestCase;
		private boolean timeExecution = false;

		private JamSolverOptionalFields(String inFile, String outFile, int linesPerTestCase) {
			this.inFile = inFile;
			this.outFile = outFile;
			this.linesPerTestCase = linesPerTestCase;
		}

		/**
		 * Enabling execution time logging means that:<br />
		 * &nbsp;&nbsp;&nbsp;&nbsp;a) if the execution time for a single test case exceeds 100ms,
		 * it gets logged to stdout<br />
		 * &nbsp;&nbsp;&nbsp;&nbsp;b) when all test cases have been executed,
		 * a total execution time gets logged to stdout
		 *
		 * @return JamSolverOptionalFields builder object
		 */
		public JamSolverOptionalFields enableExecutionTimeLogging() {
			timeExecution = true;
			return this;
		}

		/**
		 * Start solving the input file.
		 *
		 * @param solver
		 * 		a Solver interface with one method - <code>solve()</code>,
		 * 		which receives a String array of all the lines in a single test case<br />
		 * 		and expects a String containing the output for the test case as a return value.<br /><br />
		 * 		<b>NB!</b> Don't write "Case #x: " in the return value, that is added automatically
		 * @throws Throwable
		 */
		public void solve(Solver solver) throws Throwable {
			new JamSolver(inFile, outFile, linesPerTestCase, timeExecution)
					.solve(solver);
		}
	}

	public interface Solver {
		/**
		 * @param testCase
		 * 		all the lines in a single test case as a String array
		 * @return output for said test case
		 */
		String solve(String[] testCase) throws Throwable;
	}
}
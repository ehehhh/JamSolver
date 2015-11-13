import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Google Code Jam boilerplate reducer.
 *
 * @author ehehhh@gmail.com
 */
public class JamSolver {

	private final int linesPerTestCase;
	private final BufferedWriter writer;
	private final BufferedReader reader;

	private boolean started = false;

	/**
	 *
	 * @param args				<code>args[0]</code> should be input file path, <code>args[1]</code> should be output file path
	 * @param linesPerTestCase	number of lines in one test case
	 * @return					JamSolver object (call .solve() on it)
	 * @throws Throwable
	 */
	public static JamSolver with(String args[], int linesPerTestCase) throws Throwable {
		if (args.length != 2) {
			throw new Exception("args[0] should be the input file path, args[1] should be the output file path");
		}
		return new JamSolver(args, linesPerTestCase);
	}

	/**
	 *
	 * @param inPath			input file path
	 * @param outPath			output file path
	 * @param linesPerTestCase	number of lines in one test case
	 * @return					JamSolver object (call .solve() on it)
	 * @throws Throwable
	 */
	public static JamSolver with(String inPath, String outPath, int linesPerTestCase) throws Throwable {
		return new JamSolver(inPath, outPath, linesPerTestCase);
	}

	private JamSolver(String[] args, int linesPerTestCase) throws Throwable {
		this(args[0], args[1], linesPerTestCase);
	}

	private JamSolver(String inFile, String outFile, int linesPerTestCase) throws Throwable {
		if (linesPerTestCase < 1) {
			throw new Exception("Lines per testcase has to be > 0");
		}
		this.linesPerTestCase = linesPerTestCase;

		if (inFile == null || inFile.length() == 0) {
			throw new Exception("Input file path can't be empty!");
		}
		if (outFile == null || outFile.length() == 0) {
			throw new Exception("Output file path can't be empty!");
		}

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

	/**
	 * Start solving the input file.
	 * @param solver			a Solver interface with one method - solve(), which receives a String array of all the lines in a single test case<br />
	 *                          and expects a String containing the output for the test case as a return value.<br />
	 *                          NB! Don't write "Case #x: " in the return value, that is added automatically
	 * @throws Throwable
	 */
	public void solve(Solver solver) throws Throwable {
		if (started) {
			throw new RuntimeException("Solver has already been started once!");
		}
		started = true;

		long numOfTestCases = Long.parseLong(reader.readLine());
		int count = 0;
		long testCaseCount = 0;
		String line;
		String[] testCase = new String[linesPerTestCase];
		while ((line = reader.readLine()) != null) {
			testCase[count] = line;
			if (++count == linesPerTestCase) {
				count = 0;
				testCaseCount++;
				solveAndWrite(solver, testCase, testCaseCount, testCaseCount == numOfTestCases);
				if (testCaseCount == numOfTestCases) {
					break;
				}
			}
		}
		reader.close();
		writer.flush();
		writer.close();
		if (testCaseCount != numOfTestCases) {
			throw new Exception("Did you enter the correct value for 'lines per test case'?");
		}
	}

	private void solveAndWrite(Solver solver, String[] testCase, long testCaseCount, boolean isLast) throws Throwable {
		try {
			writer.write(String.format("Case #%s: %s", testCaseCount, solver.solve(testCase)));
			if (!isLast) {
				writer.newLine();
			}
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public interface Solver {
		/**
		 *
		 * @param testCase		all the lines in a single test case as a String array
		 * @return				output for said test case
		 */
		String solve(String[] testCase);
	}
}

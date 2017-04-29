import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Google Code Jam boilerplate reducer.
 *
 * @author Rait Maltsaar / ehehhh@gmail.com
 * @version 3
 */
public class JamSolver {

    private final boolean timeExecution;
    private final BufferedWriter writer;
    private final BufferedReader reader;
    private final TestCaseDesign design;

    private boolean started = false;

    /**
     * @param args <br />
     *             &nbsp;&nbsp;&nbsp;&nbsp;<code>args[0]</code> - input file path<br />
     *             &nbsp;&nbsp;&nbsp;&nbsp;<code>args[1]</code> - output file path
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
     * @param inPath  input file path
     * @param outPath output file path
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

    private JamSolver(String inFile,
                      String outFile,
                      TestCaseDesign design,
                      boolean timeExecution) throws Throwable {
        this.timeExecution = timeExecution;

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

        if (design == null) {
            throw new IllegalStateException("Test case design cannot be null");
        }
        this.design = design;
    }

    void solve(Solver solver) throws Throwable {
        if (started) {
            throw new Exception("Solver has already been started once!");
        } else {
            started = true;
        }
        if (solver == null) {
            throw new IllegalStateException("Solver cannot be null");
        }
        long allTestCasesStartTime = System.nanoTime();
        long numOfTestCases = Long.parseLong(reader.readLine());
        long testCaseCount = 0;
        try {
            while (testCaseCount < numOfTestCases) {
                testCaseCount++;
                List<String> testCaseList = new ArrayList<>();
                for (TestCaseElement element : design.elements) {
                    String[] testCasePart = element.getLines(reader);
                    Collections.addAll(testCaseList, testCasePart);
                }
                String[] testCase = testCaseList.toArray(new String[testCaseList.size()]);
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
         * @param linesPerTestCase lines per test case has to be >= 1
         * @return JamSolverOptionalFields builder object
         * @throws Throwable
         */
        public JamSolverOptionalFields withLinesPerTestCase(int linesPerTestCase) throws Throwable {
            if (linesPerTestCase < 1) {
                throw new Exception("Lines per test case must be >=1");
            }
            TestCaseDesign design =
                    TestCaseDesign.create()
                            .addFixedLines(linesPerTestCase)
                            .build();
            return new JamSolverOptionalFields(inFile, outFile, design);
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
        public JamSolverOptionalFields withCustomTestCaseDesign(TestCaseDesign design) {
            return new JamSolverOptionalFields(inFile, outFile, design);
        }
    }

    public static class JamSolverOptionalFields {

        private final String inFile;
        private final String outFile;
        private final TestCaseDesign design;
        private boolean timeExecution = false;

        private JamSolverOptionalFields(String inFile, String outFile, TestCaseDesign design) {
            this.inFile = inFile;
            this.outFile = outFile;
            this.design = design;
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
         * @param solver a Solver interface with one method - <code>solve()</code>,
         *               which receives a String array of all the lines in a single test case<br />
         *               and expects a String containing the output for the test case as a return value.<br /><br />
         *               <b>NB!</b> Don't write "Case #x: " in the return value, that is added automatically
         * @throws Throwable
         */
        public void solve(Solver solver) throws Throwable {
            new JamSolver(inFile, outFile, design, timeExecution)
                    .solve(solver);
        }
    }

    public interface Solver {
        /**
         * @param testCase all the lines in a single test case as a String array
         * @return output for said test case
         */
        String solve(String[] testCase) throws Throwable;
    }

    public static class TestCaseDesign {

        private final List<TestCaseElement> elements;

        private TestCaseDesign(List<TestCaseElement> elements) {
            this.elements = elements;
        }

        /**
         * Enables creating a custom test case design to help solve the problems with dynamic input.<br />
         * Ordering matters when adding lines to this test case design!
         *
         * @return the builder object
         */
        public static TestCaseBuilder create() {
            return new TestCaseBuilder();
        }
    }

    public static class TestCaseBuilder {

        private List<TestCaseElement> elements = new ArrayList<>();

        private TestCaseBuilder() {
        }

        /**
         * Enables adding a fixed number of lines to the test case.
         *
         * @param numOfLines number of lines
         * @return builder object
         */
        public TestCaseBuilder addFixedLines(int numOfLines) {
            if (numOfLines < 1) {
                throw new RuntimeException("Number of lines cannot be smaller than 1");
            }
            elements.add(new TestCaseElementFixedLines(numOfLines));
            return this;
        }

        /**
         * First line is passed to the callback, where it can be processed. <br />
         * The amount of lines returned by the callback will then be added to the test case.<br />
         * NB! The first line is also included in the test case for solving.
         *
         * @param callback callback for processing the first line
         * @return builder object
         */
        public TestCaseBuilder addVaryingLines(TestCaseVaryingLineCallback callback) {
            if (callback == null) {
                throw new RuntimeException("TestCaseVaryingLineCallback cannot be null");
            }
            elements.add(new TestCaseElementVaryingLines(callback));
            return this;
        }

        /**
         * Call this when all the necessary elements are added to the test case design (must have at least one).
         *
         * @return the test case design
         */
        public TestCaseDesign build() {
            if (elements.isEmpty()) {
                throw new RuntimeException("Add at least one element to the TestCaseDesign before calling build()");
            }
            return new TestCaseDesign(elements);
        }
    }

    private interface TestCaseElement {

        String[] getLines(BufferedReader reader) throws IOException;
    }

    private static class TestCaseElementFixedLines implements TestCaseElement {

        private final int numOfLines;

        TestCaseElementFixedLines(int numOfLines) {
            this.numOfLines = numOfLines;
        }

        @Override
        public String[] getLines(BufferedReader reader) throws IOException {
            String[] lines = new String[numOfLines];
            for (int i = 0; i < numOfLines; i++) {
                String line = reader.readLine();
                if (line != null) {
                    lines[i] = line;
                } else {
                    throw new IOException("readLine() got null. Have you specified the correct structure for the test case?");
                }
            }
            return lines;
        }
    }

    private static class TestCaseElementVaryingLines implements TestCaseElement {

        private final TestCaseVaryingLineCallback callback;

        TestCaseElementVaryingLines(TestCaseVaryingLineCallback callback) {
            this.callback = callback;
        }

        @Override
        public String[] getLines(BufferedReader reader) throws IOException {
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            if (line != null) {
                lines.add(line);
                int numOfLines = callback.getLineCount(line);
                for (int i = 0; i < numOfLines; i++) {
                    line = reader.readLine();
                    if (line != null) {
                        lines.add(line);
                    } else {
                        throw new IOException("readLine() got null. Have you specified the correct structure for the test case?");
                    }
                }
            } else {
                throw new IOException("readLine() got null. Have you specified the correct structure for the test case?");
            }
            return lines.toArray(new String[lines.size()]);
        }
    }

    public interface TestCaseVaryingLineCallback {

        int getLineCount(String line);
    }
}
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.concurrent.TimeUnit

fun main() {
    JamSolver.withIO(System.`in`, System.out)
        .withLinesPerTestCase(1)
        .solve { testCase ->
			// testCase is a String array of all the lines in a single test case
			// TODO solve the test case
            return@solve "solution_to_test_case"
        }
}

class JamSolver private constructor(
    private val reader: BufferedReader,
    private val writer: BufferedWriter,
    private val design: TestCaseDesign,
    private val timeExecution: Boolean
) {
    private var started = false

    fun solve(solver: Solver) {
        if (started) {
            throw Exception("Solver has already been started once!")
        } else {
            started = true
        }
        val allTestCasesStartTime = System.nanoTime()
        val numOfTestCases = reader.readLine().toLong()
        var testCaseCount: Long = 0
        try {
            while (testCaseCount < numOfTestCases) {
                testCaseCount++
                val testCaseList = ArrayList<String>()
                for (element in design.elements) {
                    val testCasePart = element.getLines(reader)
                    testCaseList.addAll(testCasePart)
                }
                val testCase = testCaseList.toTypedArray()
                val testCaseStartTime = System.nanoTime()
                solveAndWrite(solver, testCase, testCaseCount, testCaseCount == numOfTestCases)
                if (timeExecution) {
                    val testCaseTime = TimeUnit.MILLISECONDS.convert(
                        System.nanoTime() - testCaseStartTime,
                        TimeUnit.NANOSECONDS
                    )
                    if (testCaseTime > 100) {
                        println("Test case #$testCaseCount solved in ${getHumanReadableTimeFromMs(testCaseTime)}")
                    }
                }
            }
            if (timeExecution) {
                println(
                    "* All test cases executed in ${getHumanReadableTimeFromMs(
                        TimeUnit.MILLISECONDS.convert(
                            System.nanoTime() - allTestCasesStartTime,
                            TimeUnit.NANOSECONDS
                        )
                    )} *"
                )
            }
        } catch (e: Exception) {
            System.err.println("Error reading input file!")
            e.printStackTrace()
        }
        reader.close()
        writer.flush()
        writer.close()
    }

    private fun solveAndWrite(
        solver: Solver,
        testCase: Array<String>,
        testCaseCount: Long,
        isLast: Boolean
    ) {
        writer.write(String.format("Case #%s: %s", testCaseCount, solver(testCase)))
        if (!isLast) {
            writer.newLine()
        }
    }

    private fun getHumanReadableTimeFromMs(timeInMs: Long): String {
        return when {
            timeInMs > 10000 -> "~${TimeUnit.SECONDS.convert(timeInMs, TimeUnit.MILLISECONDS)} seconds"
            else -> "$timeInMs milliseconds"
        }
    }

    class JamSolverRequiredFields internal constructor(
        private val reader: BufferedReader,
        private val writer: BufferedWriter
    ) {

        /**
         * When all of the test cases have a constant number of lines, this method should be used.
         * <br></br><br></br>
         * *Example input file with 2 lines per test case:*<br></br>
         * `
         * 3<br></br>
         * testcase1_line1<br></br>
         * testcase1_line2<br></br>
         * testcase2_line1<br></br>
         * testcase2_line2<br></br>
         * testcase3_line1<br></br>
         * testcase3_line2<br></br>
        ` *
         *
         * @param linesPerTestCase lines per test case has to be >= 1
         * @return JamSolverOptionalFields builder object
         */
        fun withLinesPerTestCase(linesPerTestCase: Int): JamSolverOptionalFields {
            if (linesPerTestCase < 1) {
                throw Exception("Lines per test case must be >=1")
            }
            val design = TestCaseDesign.create()
                .addFixedLines(linesPerTestCase)
                .build()
            return JamSolverOptionalFields(reader, writer, design)
        }

        /**
         * When the test cases have a varying number of lines, this method should be used.<br></br><br></br>
         * *Example input file with varying lines per test case:*<br></br>
         * `
         * 3<br></br>
         * 2<br></br>
         * testcase1_line1<br></br>
         * testcase1_line2<br></br>
         * 1<br></br>
         * testcase2_line1<br></br>
         * 4<br></br>
         * testcase3_line1<br></br>
         * testcase3_line2<br></br>
         * testcase3_line3<br></br>
         * testcase3_line4
        ` *
         *
         * @return JamSolverOptionalFields builder object
         */
        fun withCustomTestCaseDesign(design: TestCaseDesign): JamSolverOptionalFields {
            return JamSolverOptionalFields(reader, writer, design)
        }
    }

    class JamSolverOptionalFields internal constructor(
        private val reader: BufferedReader,
        private val writer: BufferedWriter,
        private val design: TestCaseDesign
    ) {
        private var timeExecution = false

        /**
         * Enabling execution time logging means that:<br></br>
         * &nbsp;&nbsp;&nbsp;&nbsp;a) if the execution time for a single test case exceeds 100ms,
         * it gets logged to stdout<br></br>
         * &nbsp;&nbsp;&nbsp;&nbsp;b) when all test cases have been executed,
         * a total execution time gets logged to stdout
         *
         * @return JamSolverOptionalFields builder object
         */
        fun enableExecutionTimeLogging(): JamSolverOptionalFields {
            timeExecution = true
            return this
        }

        /**
         * Start solving the input file.
         *
         * @param solver a Solver interface with one method - `solve()`,
         * which receives a String array of all the lines in a single test case<br></br>
         * and expects a String containing the output for the test case as a return value.<br></br><br></br>
         * **NB!** Don't write "Case #x: " in the return value, it is added automatically
         */
        fun solve(solver: Solver) {
            JamSolver(reader, writer, design, timeExecution)
                .solve(solver)
        }

    }

    class TestCaseDesign private constructor(val elements: List<TestCaseElement>) {
        companion object {
            /**
             * Enables creating a custom test case design to help solve the problems with dynamic input.<br></br>
             * Ordering matters when adding lines to this test case design!
             *
             * @return the builder object
             */
            fun create(): TestCaseBuilder {
                return TestCaseBuilder()
            }
        }

        class TestCaseBuilder {
            private val elements: MutableList<TestCaseElement> = ArrayList()

            /**
             * Enables adding a fixed number of lines to the test case.
             *
             * @param numOfLines number of lines
             * @return builder object
             */
            fun addFixedLines(numOfLines: Int): TestCaseBuilder {
                if (numOfLines < 1) {
                    throw RuntimeException("Number of lines cannot be smaller than 1")
                }
                elements.add(TestCaseElementFixedLines(numOfLines))
                return this
            }

            /**
             * First line is passed to the callback, where it can be processed. <br></br>
             * The amount of lines returned by the callback will then be added to the test case.<br></br>
             * NB! The first line is also included in the test case for solving.
             *
             * @param callback callback for processing the first line
             * @return builder object
             */
            fun addVaryingLines(callback: TestCaseVaryingLineCallback): TestCaseBuilder {
                elements.add(TestCaseElementVaryingLines(callback))
                return this
            }

            /**
             * Call this when all the necessary elements are added to the test case design (must have at least one).
             *
             * @return the test case design
             */
            fun build(): TestCaseDesign {
                if (elements.isEmpty()) {
                    throw RuntimeException("Add at least one element to the TestCaseDesign before calling build()")
                }
                return TestCaseDesign(elements)
            }
        }
    }

    interface TestCaseElement {
        fun getLines(reader: BufferedReader): Array<String>
    }

    private class TestCaseElementFixedLines internal constructor(
        private val numOfLines: Int
    ) : TestCaseElement {
        override fun getLines(reader: BufferedReader): Array<String> {
            val lines = ArrayList<String>(numOfLines)
            for (i in 0 until numOfLines) {
                val line = reader.readLine()
                if (line != null) {
                    lines.add(line)
                } else {
                    throw IOException("readLine() got null. Have you specified the correct structure for the test case?")
                }
            }
            return lines.toTypedArray()
        }

    }

    private class TestCaseElementVaryingLines internal constructor(
        private val callback: TestCaseVaryingLineCallback
    ) : TestCaseElement {
        override fun getLines(reader: BufferedReader): Array<String> {
            val lines: MutableList<String> = ArrayList()
            var line = reader.readLine()
            if (line != null) {
                lines.add(line)
                val numOfLines = callback(line)
                for (i in 0 until numOfLines) {
                    line = reader.readLine()
                    if (line != null) {
                        lines.add(line)
                    } else {
                        throw IOException("readLine() got null. Have you specified the correct structure for the test case?")
                    }
                }
            } else {
                throw IOException("readLine() got null. Have you specified the correct structure for the test case?")
            }
            return lines.toTypedArray()
        }

    }

    companion object {
        /**
         * @param args <br></br>
         * &nbsp;&nbsp;&nbsp;&nbsp;`args[0]` - input file path<br></br>
         * &nbsp;&nbsp;&nbsp;&nbsp;`args[1]` - output file path
         * @return JamSolverRequiredFields object (next step in the builder)
         */
        fun withIO(args: Array<String>): JamSolverRequiredFields {
            if (args.size != 2) {
                throw Exception("args[0] should be the input file path, args[1] should be the output file path")
            }
            val reader = bufferedReaderFromFile(args[0])
            val writer = bufferedWriterFromFile(args[1])
            return withIO(reader, writer)
        }

        /**
         * @param inPath  input file path
         * @param outPath output file path
         * @return JamSolverRequiredFields object (next step in the builder)
         */
        fun withIO(inPath: String, outPath: String): JamSolverRequiredFields {
            if (inPath.isEmpty()) {
                throw Exception("Parameter inPath cannot be empty")
            }
            if (outPath.isEmpty()) {
                throw Exception("Parameter outPath cannot be empty")
            }
            val reader = bufferedReaderFromFile(inPath)
            val writer = bufferedWriterFromFile(outPath)
            return withIO(reader, writer)
        }

        /**
         * @param inputStream  input stream
         * @param outputStream output stream
         * @return JamSolverRequiredFields object (next step in the builder)
         */
        fun withIO(inputStream: InputStream, outputStream: OutputStream): JamSolverRequiredFields {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            return withIO(reader, writer)
        }

        /**
         * @param reader input reader
         * @param writer output writer
         * @return JamSolverRequiredFields object (next step in the builder)
         */
        fun withIO(reader: BufferedReader, writer: BufferedWriter): JamSolverRequiredFields =
            JamSolverRequiredFields(reader, writer)

        private fun bufferedReaderFromFile(inFile: String): BufferedReader {
            val inPath = Paths.get("", inFile)
            if (Files.notExists(inPath)) {
                throw IOException("Input file does not exist!")
            }
            if (!Files.isReadable(inPath)) {
                throw IOException("Input file is not readable!")
            }
            return Files.newBufferedReader(inPath)
        }

        private fun bufferedWriterFromFile(outFile: String): BufferedWriter {
            val outPath = Paths.get("", outFile)
            if (Files.exists(outPath) && !Files.deleteIfExists(outPath)) {
                throw IOException("Output file could not be deleted!")
            }
            return Files.newBufferedWriter(outPath, StandardOpenOption.CREATE)
        }
    }
}

/**
 * @input All the lines in a single test case are passed in as a String array
 * @return output for test case
 */
typealias Solver = (Array<String>) -> String

/**
 * First line is passed to the callback, where it can be processed.
 * The amount of lines returned by the callback will then be added to the test case.
 * NB! The first line is also included in the test case for solving.
 *
 * @input First line of test case is passed in for processing
 * @return number of lines in the test case
 */
typealias TestCaseVaryingLineCallback = (String) -> Int

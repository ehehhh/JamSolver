JamSolver
=========
A Google CodeJam boilerplate reducer, allowing you to concentrate on solving the problem, not implementing IO.

__At least Java 7 is required to use this.__

```java
public class Main {

  public static void main(String[] args) throws Throwable {
    final String inPath = "C:\\path\\to\\input.in";
    final String outPath = "C:\\path\\to\\output.out";

    /**
     * Fixed lines per test case example.
     *
     * Input file with 2 lines per test case:
     * 3
     * testcase1_line1
     * testcase1_line2
     * testcase2_line1
     * testcase2_line2
     * testcase3_line1
     * testcase3_line2
     */
    JamSolver.withIO(inPath, outPath)
        .withLinesPerTestCase(2)
        .enableExecutionTimeLogging()    // You can choose to time the execution of your solution.
        .solve(testCase -> {
          // testCase is a String array with length specified by linesPerTestCase
          // TODO solve the test case
          return "solution_to_test_case";
        });

    /**
     * Varying lines per test case example.
     *
     * Input file with varying lines per test case:
     * 3
     * 2
     * testcase1_line1
     * testcase1_line2
     * 1
     * testcase2_line1
     * 4
     * testcase3_line1
     * testcase3_line2
     * testcase3_line3
     * testcase3_line4
     */
    JamSolver.withIO(inPath, outPath)
        .withCustomTestCaseDesign(
            TestCaseDesign
                .create()
                .addVaryingLines(firstLine -> Integer.parseInt(firstLine))
                .build()
        )
        .solve(testCase -> {
          // testCase is a String array with length that varies for each test case
          // TODO solve the test case
          return "solution_to_test_case";
        });
  }
}
```

Usage
-----
Use __Solution.java__ or __Solution.kt__ as a base, which you can copy-paste into CodeJam web interface once you've implemented your solution.

JamSolver
=========
A Google CodeJam boilerplate reducer, allowing you to concentrate on the real problem, not IO.
__At least Java 7 is required to use this.__

```java
class Main {

  public static void main(String args[]) {
    final String inPath = "C:\Code\Jam\input.in";
    final String outPath = "C:\Code\Jam\output.out";
    final int linesPerTestCase = 3;

	// Java 7 example
    JamSolver.with(inPath, outPath, linesPerTestCase)
      .solve(new JamSolver.Solver() {
        @Override
        public String solve(String[] testCase) {
          // testCase is a String array with length specified by linesPerTestCase
          // TODO solve the test case
          return "solution_to_test_case";
        }
      });

    // Looks a lot better with Java8+ lambdas
    JamSolver.with(inPath, outPath, linesPerTestCase)
      .solve(testCase -> {
        // TODO solve the test case
        return "solution_to_test_case";
      });
  
    // Input and output file paths can also be passed in as parameters, where args[0] is the input and args[1] is the output
    JamSolver.with(args, linesPerTestCase)
      .solve(testCase -> {
        // TODO solve the test case
        return "solution_to_test_case";
      });
  }
}
```

Usage
-----
Just download JamSolver.java, add it to your project and you're good to go.
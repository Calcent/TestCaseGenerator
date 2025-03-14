package com.example.testcasegenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ExpressionGenerator {

    @FXML
    private TextArea errorLog;

    @FXML
    private Button exportExpressions;

    @FXML
    private Button genCorrect;

    @FXML
    private Button genIncorrect;

    @FXML
    private Button genRandom;

    @FXML
    private Spinner<Integer> testAmount;

    @FXML
    private Button testExpressions;

    // List to store each generated expression along with its expected result.
    private List<ExpressionTest> generatedTests = new ArrayList<>();

    // Helper inner class to store an expression and its expected result.
    private class ExpressionTest {
        String expression;
        String expectedResult;
    }

    public void initialize() {
        genCorrect.setOnAction(event -> handleRandomGeneration("correct"));
        genRandom.setOnAction(event -> handleRandomGeneration("random"));
        genIncorrect.setOnAction(event -> handleRandomGeneration("incorrect"));
        testExpressions.setOnAction(event -> handleRandomGeneration("Test"));

        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10);
        testAmount.setValueFactory(valueFactory);
    }

    public void handleRandomGeneration(String type) {
        // Create a ButtonController instance.
        ButtonController controller = new ButtonController();
        Random random = new Random();

        switch (type) {
            case "correct": {
                // Generate the requested number of correct expressions.
                int count = testAmount.getValue();
                List<ExpressionTest> correctTests = generateCorrectExpressions(count, controller, random);
                for (ExpressionTest test : correctTests) {
                    errorLog.appendText(test.expression + " = " + test.expectedResult + "\n");
                }
                generatedTests.addAll(correctTests);
                break;
            }
            case "incorrect": {
                // Always generate 10 incorrect expressions.
                List<ExpressionTest> incorrectTests = generateIncorrectExpressions(10, controller, random);
                for (ExpressionTest test : incorrectTests) {
                    errorLog.appendText(test.expression + " = " + test.expectedResult + "\n");
                }
                generatedTests.addAll(incorrectTests);
                break;
            }
            case "random": {
                // Generate an even number of expressions, half correct and half incorrect.
                int total = testAmount.getValue();
                int half = total / 2;  // if total is odd, we use floor(total/2)*2 expressions.
                List<ExpressionTest> randomTests = new ArrayList<>();
                randomTests.addAll(generateCorrectExpressions(half, controller, random));
                randomTests.addAll(generateIncorrectExpressions(half, controller, random));
                // Optionally, shuffle the combined list.
                Collections.shuffle(randomTests);
                for (ExpressionTest test : randomTests) {
                    errorLog.appendText(test.expression + " = " + test.expectedResult + "\n");
                }
                generatedTests.addAll(randomTests);
                break;
            }
            case "Test": {
                if (generatedTests.isEmpty()) {
                    errorLog.appendText("No expressions generated to test.\n");
                    break;
                }
                errorLog.appendText("\n=== Test Results ===\n");
                for (ExpressionTest test : generatedTests) {
                    // Re-evaluate using the original handleEquals method.
                    String currentResult = controller.handleEquals(test.expression);
                    if (test.expectedResult.equals(currentResult)) {
                        errorLog.appendText("PASS: " + test.expression + " expected: "
                                + test.expectedResult + ", got: " + currentResult + "\n");
                    } else {
                        errorLog.appendText("FAIL: " + test.expression + " expected: "
                                + test.expectedResult + ", got: " + currentResult + "\n");
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    // Helper method to generate a list of correct expressions.
    private List<ExpressionTest> generateCorrectExpressions(int count, ButtonController controller, Random random) {
        List<ExpressionTest> tests = new ArrayList<>();
        String[] functions = { "log", "ln", "sin", "cos", "tan" };
        String[] operators = { "+", "-", "*", "/" };

        for (int i = 0; i < count; i++) {
            StringBuilder expression = new StringBuilder();
            // Randomly decide on a token count between 3 and 7.
            int tokenCount = random.nextInt(5) + 3;

            // Generate the first token.
            String token = "";
            if (random.nextDouble() < 0.5) {
                String func = functions[random.nextInt(functions.length)];
                double arg = random.nextDouble() * 100 + 1;
                token = func + "(" + String.format("%.2f", arg) + ")";
            } else {
                token = random.nextBoolean()
                        ? String.valueOf(random.nextInt(100))
                        : String.format("%.2f", random.nextDouble() * 100);
            }
            expression.append(token);

            // Generate remaining tokens.
            for (int j = 1; j < tokenCount; j++) {
                String op = operators[random.nextInt(operators.length)];
                expression.append(op);
                String nextToken = "";
                if (op.equals("/")) {
                    boolean isInteger = random.nextBoolean();
                    if (isInteger) {
                        int num;
                        do {
                            num = random.nextInt(100);
                        } while (num == 0);
                        nextToken = String.valueOf(num);
                    } else {
                        double num;
                        do {
                            num = random.nextDouble() * 100;
                        } while (num == 0.0);
                        nextToken = String.format("%.2f", num);
                    }
                } else {
                    if (random.nextDouble() < 0.5) {
                        String func = functions[random.nextInt(functions.length)];
                        double arg = random.nextDouble() * 100 + 1;
                        nextToken = func + "(" + String.format("%.2f", arg) + ")";
                    } else {
                        nextToken = random.nextBoolean()
                                ? String.valueOf(random.nextInt(100))
                                : String.format("%.2f", random.nextDouble() * 100);
                    }
                }
                expression.append(nextToken);
            }

            // Optionally wrap the expression in parentheses.
            if (random.nextBoolean()) {
                expression.insert(0, "(");
                expression.append(")");
            }

            String generatedExpression = expression.toString();
            String alternativeResult = AlternativeTester.test(generatedExpression);

            ExpressionTest test = new ExpressionTest();
            test.expression = generatedExpression;
            test.expectedResult = alternativeResult;
            tests.add(test);
        }
        return tests;
    }

    // Helper method to generate a list of incorrect expressions.
    private List<ExpressionTest> generateIncorrectExpressions(int count, ButtonController controller, Random random) {
        List<ExpressionTest> tests = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String generatedExpression = "";
            int errorType = random.nextInt(5); // 5 error types.
            switch (errorType) {
                case 0: // Division by zero.
                    double numerator = random.nextDouble() * 100;
                    generatedExpression = String.format("%.2f/0", numerator);
                    break;
                case 1: // Back-to-back operators (avoid valid unary minus).
                    int num1 = random.nextInt(100);
                    int num2 = random.nextInt(100);
                    String[] ops = { "+", "-", "*", "/" };
                    String op1 = ops[random.nextInt(ops.length)];
                    String op2;
                    do {
                        op2 = ops[random.nextInt(ops.length)];
                    } while (op2.equals("-")); // Ensure we don't produce a valid sequence like "+-"
                    generatedExpression = num1 + op1 + op2 + num2;
                    break;
                case 2: // Incomplete function call.
                    String[] funcs = { "sin", "cos", "tan", "log", "ln" };
                    String func = funcs[random.nextInt(funcs.length)];
                    generatedExpression = func + "(";
                    break;
                case 3: // Multiple decimals in a number.
                    int intPart = random.nextInt(100);
                    int dec1 = random.nextInt(100);
                    int dec2 = random.nextInt(100);
                    generatedExpression = intPart + "." + dec1 + "." + dec2;
                    break;
                case 4: // Invalid operator sequence.
                    int n3 = random.nextInt(100);
                    int n4 = random.nextInt(100);
                    generatedExpression = n3 + "+*" + n4;
                    break;
            }
            String alternativeResult = AlternativeTester.test(generatedExpression);

            ExpressionTest test = new ExpressionTest();
            test.expression = generatedExpression;
            test.expectedResult = alternativeResult;
            tests.add(test);
        }
        return tests;
    }
}

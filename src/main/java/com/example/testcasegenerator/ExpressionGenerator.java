package com.example.testcasegenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

import java.util.ArrayList;
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
        // Create an instance of ButtonController.
        ButtonController controller = new ButtonController();
        Random random = new Random();

        switch (type) {
            case "correct":
                // Get the number of expressions to generate from the spinner.
                int count = testAmount.getValue();
                // Optionally, clear the errorLog if desired.
                // errorLog.clear();
                for (int i = 0; i < count; i++) {
                    StringBuilder expression = new StringBuilder();
                    // Randomly decide on a token count between 3 and 7.
                    int tokenCount = random.nextInt(5) + 3; // yields 3 to 7 tokens

                    // Arrays for functions and operators.
                    String[] functions = { "log", "ln", "sin", "cos", "tan" };
                    String[] operators = { "+", "-", "*", "/" };

                    // --- Generate the first token ---
                    String token = "";
                    if (random.nextDouble() < 0.5) {
                        // Function-wrapped token (ensuring positive argument)
                        String func = functions[random.nextInt(functions.length)];
                        double arg = random.nextDouble() * 100 + 1;
                        token = func + "(" + String.format("%.2f", arg) + ")";
                    } else {
                        // Plain number token.
                        if (random.nextBoolean()) {
                            token = String.valueOf(random.nextInt(100));
                        } else {
                            token = String.format("%.2f", random.nextDouble() * 100);
                        }
                    }
                    expression.append(token);

                    // --- Generate remaining tokens with an operator preceding each ---
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
                                if (random.nextBoolean()) {
                                    nextToken = String.valueOf(random.nextInt(100));
                                } else {
                                    nextToken = String.format("%.2f", random.nextDouble() * 100);
                                }
                            }
                        }
                        expression.append(nextToken);
                    }

                    // Optionally wrap the entire expression in parentheses (50% chance).
                    if (random.nextBoolean()) {
                        expression.insert(0, "(");
                        expression.append(")");
                    }

                    // Convert the built expression to a string.
                    String generatedExpression = expression.toString();

                    // Use AlternativeTester (which uses mXparser) to compute the expected result.
                    String alternativeResult = AlternativeTester.test(generatedExpression);

                    // Append the generated expression and its expected result to the errorLog.
                    errorLog.appendText(generatedExpression + " = " + alternativeResult + "\n");

                    // Save the generated test for later use.
                    ExpressionTest test = new ExpressionTest();
                    test.expression = generatedExpression;
                    test.expectedResult = alternativeResult;
                    generatedTests.add(test);
                }
                break;

            case "incorrect":
                // Generate 10 deliberately incorrect expressions at random.
                for (int i = 0; i < 10; i++) {
                    String generatedExpression = "";
                    int errorType = random.nextInt(5); // 5 different error types: 0,1,2,3,4.
                    switch (errorType) {
                        case 0: // Division by zero
                            double numerator = random.nextDouble() * 100;
                            generatedExpression = String.format("%.2f/0", numerator);
                            break;
                        case 1: // Back-to-back operators (avoid a valid unary minus)
                            int num1 = random.nextInt(100);
                            int num2 = random.nextInt(100);
                            String[] ops = { "+", "-", "*", "/" };
                            String op1 = ops[random.nextInt(ops.length)];
                            String op2;
                            // Re-roll until op2 is not "-" to avoid valid expressions like "+-"
                            do {
                                op2 = ops[random.nextInt(ops.length)];
                            } while (op2.equals("-"));
                            generatedExpression = num1 + op1 + op2 + num2;
                            break;
                        case 2: // Incomplete function call (empty argument)
                            String[] functions = { "sin", "cos", "tan", "log", "ln" };
                            String func = functions[random.nextInt(functions.length)];
                            generatedExpression = func + "(";
                            break;
                        case 3: // Multiple decimals in a number (e.g., 2.2.2)
                            int intPart = random.nextInt(100);
                            int dec1 = random.nextInt(100);
                            int dec2 = random.nextInt(100);
                            generatedExpression = intPart + "." + dec1 + "." + dec2;
                            break;
                        case 4: // Invalid operator sequence (e.g., 8+*3)
                            int n3 = random.nextInt(100);
                            int n4 = random.nextInt(100);
                            generatedExpression = n3 + "+*" + n4;
                            break;
                    }
                    String alternativeResult = AlternativeTester.test(generatedExpression);
                    errorLog.appendText(generatedExpression + " = " + alternativeResult + "\n");

                    // Save the generated test.
                    ExpressionTest test = new ExpressionTest();
                    test.expression = generatedExpression;
                    test.expectedResult = alternativeResult;
                    generatedTests.add(test);
                }
                break;

            case "random":
                // Code for random expressions (if any) can be added here.
                break;

            case "Test":
                if (generatedTests.isEmpty()) {
                    errorLog.appendText("No expressions generated to test.\n");
                    break;
                }
                errorLog.appendText("\n=== Test Results ===\n");
                for (ExpressionTest test : generatedTests) {
                    // Re-evaluate using the original handleEquals method.
                    String currentResult = controller.handleEquals(test.expression);
                    // Compare with the expected result from the alternative tester.
                    if (test.expectedResult.equals(currentResult)) {
                        errorLog.appendText("PASS: " + test.expression + " expected: "
                                + test.expectedResult + ", got: " + currentResult + "\n");
                    } else {
                        errorLog.appendText("FAIL: " + test.expression + " expected: "
                                + test.expectedResult + ", got: " + currentResult + "\n");
                    }
                }
                break;

            default:
                // Handle unknown types if needed.
                break;
        }
    }
}

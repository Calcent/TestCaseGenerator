package com.example.testcasegenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;

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

    public void initialize() {
        //handles the random expression generation buttons
        genCorrect.setOnAction(event -> handleRandomGeneration("correct"));
        genRandom.setOnAction(event -> handleRandomGeneration("random"));
        genIncorrect.setOnAction(event -> handleRandomGeneration("incorrect"));
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 10);
        testAmount.setValueFactory(valueFactory);
    }

    public void handleRandomGeneration(String type) {
        Random random = new Random();
        switch (type) {
            //generation code created by chatGPT
            case "correct":
                //Get the number of expressions to generate from the spinner.
                int count = testAmount.getValue();
                //Clear the errorLog so only new outputs are shown.
                errorLog.clear();
                for (int i = 0; i < count; i++) {
                    StringBuilder expression = new StringBuilder();
                    //Randomly decide on a token count between 3 and 7.
                    int tokenCount = random.nextInt(5) + 3; // yields 3 to 7 tokens

                    //Arrays for functions and operators.
                    String[] functions = { "log", "ln", "sin", "cos", "tan" };
                    String[] operators = { "+", "-", "*", "/" };

                    // --- Generate the first token normally ---
                    String token = "";
                    if (random.nextDouble() < 0.5) {
                        //Function-wrapped token (ensuring positive argument)
                        String func = functions[random.nextInt(functions.length)];
                        double arg = random.nextDouble() * 100 + 1;
                        token = func + "(" + String.format("%.2f", arg) + ")";
                    } else {
                        //Plain number token (can be 0 here because it's not following a division)
                        if (random.nextBoolean()) {
                            token = String.valueOf(random.nextInt(100));
                        } else {
                            token = String.format("%.2f", random.nextDouble() * 100);
                        }
                    }
                    expression.append(token);

                    // --- Generate remaining tokens with an operator preceding each ---
                    for (int j = 1; j < tokenCount; j++) {
                        //Choose a random operator.
                        String op = operators[random.nextInt(operators.length)];
                        expression.append(op);

                        String nextToken = "";
                        //If the operator is division, force a plain number that is not zero.
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
                            //Otherwise, generate token normally (50% chance function, 50% chance plain).
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

                    //Optionally wrap the entire expression in parentheses (50% chance).
                    if (random.nextBoolean()) {
                        expression.insert(0, "(");
                        expression.append(")");
                    }

                    //Convert the built expression to a string.
                    String generatedExpression = expression.toString();
                    //output.setText(generatedExpression);
                    //Now call handleEquals() to evaluate the expression.
                    handleEquals();
                }
                break;
            case "syntactical":
                output.setText("Generated syntactical expression");
                break;
            case "random":
                output.setText("Generated random expression");
                break;
            default:
                output.setText("Unknown generation type");
                break;
        }
    }
}

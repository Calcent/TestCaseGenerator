package com.example.testcasegenerator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import java.util.Stack;

public class ButtonController {

    @FXML
    private Button clear;

    @FXML
    private Button cosine;

    @FXML
    private Button decimal;

    @FXML
    private Button divides;

    @FXML
    private Button eight;

    @FXML
    private Button equals;

    @FXML
    private Button exponent;

    @FXML
    private Button five;

    @FXML
    private Button four;

    @FXML
    private Button leftCurrlyBracket;

    @FXML
    private Button leftParentheses;

    @FXML
    private Button ln;

    @FXML
    private Button log;

    @FXML
    private Button minus;

    @FXML
    private Button negativeToggle;

    @FXML
    private Button nine;

    @FXML
    private Button one;

    @FXML
    private TextField output;

    @FXML
    private Button plus;

    @FXML
    private Button rightCurrlyBracket;

    @FXML
    private Button rightParentheses;

    @FXML
    private Button seven;

    @FXML
    private Button sine;

    @FXML
    private Button six;

    @FXML
    private Button tangent;

    @FXML
    private Button three;

    @FXML
    private Button times;

    @FXML
    private Button two;

    @FXML
    private Button undo;

    @FXML
    private Button zero;

    //initilize all buttons
    /*public void initialize() {
        //handles the equals function
        equals.setOnAction(event -> handleEquals(output.getText()));

        //handles numbers
        zero.setOnAction(event -> handleButtonClick("0"));
        one.setOnAction(event -> handleButtonClick("1"));
        two.setOnAction(event -> handleButtonClick("2"));
        three.setOnAction(event -> handleButtonClick("3"));
        four.setOnAction(event -> handleButtonClick("4"));
        five.setOnAction(event -> handleButtonClick("5"));
        six.setOnAction(event -> handleButtonClick("6"));
        seven.setOnAction(event -> handleButtonClick("7"));
        eight.setOnAction(event -> handleButtonClick("8"));
        nine.setOnAction(event -> handleButtonClick("9"));

        //handles operators
        plus.setOnAction(event -> handleButtonClick("+"));
        minus.setOnAction(event -> handleButtonClick("-"));
        times.setOnAction(event -> handleButtonClick("*"));
        divides.setOnAction(event -> handleButtonClick("/"));
        negativeToggle.setOnAction(event -> handleNegativeToggle());

        //handles brackets
        leftParentheses.setOnAction(event -> handleButtonClick("("));
        rightParentheses.setOnAction(event -> handleButtonClick(")"));
        leftCurrlyBracket.setOnAction(event -> handleButtonClick("{"));
        rightCurrlyBracket.setOnAction(event -> handleButtonClick("}"));

        //handles decimal
        decimal.setOnAction(event -> handleDecimal());

        //handles other math stuff
        log.setOnAction(event -> handleButtonClick("log("));
        ln.setOnAction(event -> handleButtonClick("ln("));
        sine.setOnAction(event -> handleButtonClick("sin("));
        cosine.setOnAction(event -> handleButtonClick("cos("));
        tangent.setOnAction(event -> handleButtonClick("tan("));

        //handles exponent values
        exponent.setOnAction(event -> handleButtonClick("^"));

        //handles the undo and clear button
        undo.setOnAction(this::handleDelete);
        clear.setOnAction(this::handleDelete);
    }*/

    //function to calculate the result
    public String handleEquals(String currentText) {
        if (currentText.isEmpty()) return "";

        try {
            currentText = autoCloseBrackets(currentText);
            currentText = preprocessExpression(currentText);

            //Processes Functions
            currentText = evaluateFunctions(currentText);

            //Makes parentheses work
            while (currentText.contains("(")) {
                currentText = evaluateInnermostExpression(currentText, '(', ')');
            }

            //Makes currly brackets work
            while (currentText.contains("{")) {
                currentText = evaluateInnermostExpression(currentText, '{', '}');
            }

            //Sends a call to evaluate the expression using a similar algorithm to the shunting-yard algorithm
            double result = evaluateExpression(currentText);
            String resultString = String.valueOf(result);
            return resultString;
            //output.setText(String.valueOf(result));
        }
        //throws error if anything goes wrong
        catch (Exception e) {
            return "Error: Invalid Expression";
        }
        //return "No Text";
    }

    //Prevents application from crashing if you don't have a closed currly bracket and instead just inserts them
    private String autoCloseBrackets(String expression) {
        int openParentheses = 0;
        int openCurlyBrackets = 0;

        //Count how many parentheses `{}` and `()` are unclosed
        for (char c : expression.toCharArray()) {
            if (c == '(') openParentheses++;
            if (c == ')') openParentheses--;
            if (c == '{') openCurlyBrackets++;
            if (c == '}') openCurlyBrackets--;
        }

        //Auto-close unclosed `(` with `)`
        while (openParentheses > 0) {
            expression += ")";
            openParentheses--;
        }

        //Auto-close unclosed `{` with `}`
        while (openCurlyBrackets > 0) {
            expression += "}";
            openCurlyBrackets--;
        }

        return expression;
    }

    //Begins by evaluating every expression within the parentheses or brackets
    private String evaluateInnermostExpression(String expression, char open, char close) {
        //handles the lack of a closing bracket
        int openIndex = expression.lastIndexOf(open);
        if (openIndex == -1) return expression;
        int closeIndex = expression.indexOf(close, openIndex);
        if (closeIndex == -1) return expression;

        //Calculates the expression
        String innerExpression = expression.substring(openIndex + 1, closeIndex);
        double innerResult = evaluateExpression(innerExpression);

        //Allows expressions like 2(5) to evaluate properly as multiplication
        String before = openIndex > 0 ? expression.substring(0, openIndex) : "";
        String after = closeIndex + 1 < expression.length() ? expression.substring(closeIndex + 1) : "";

        if (open == '{' && !before.isEmpty() && Character.isDigit(before.charAt(before.length() - 1))) {
            before += "*";
        }
        if (open == '{' && !after.isEmpty() && (Character.isDigit(after.charAt(0)) || after.charAt(0) == '(')) {
            after = "*" + after;
        }

        return before + innerResult + after;
    }

    //Evaluates the end result expression using a form of the shunting-yard algorithm provided by chatgpt
    private double evaluateExpression(String expression) {
        expression = preprocessExpression(expression); //Ensure negatives & multiplication are corrected

        //Process functions like sin(), cos(), log() first
        expression = evaluateFunctions(expression);

        //Handle exponentiation (right-associative, highest precedence)
        if (expression.contains("^")) {
            expression = evaluateFunctions(expression);
        }

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int i = 0;

        while (i < expression.length()) {
            char c = expression.charAt(i);

            //Skip spaces
            if (c == ' ') {
                i++;
                continue;
            }

            //Handle negative numbers at the start or after an operator
            if (c == '-' && (i == 0 || isOperator(Character.toString(expression.charAt(i - 1))))) {
                StringBuilder numBuffer = new StringBuilder("-");
                i++; // Move past '-'
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numBuffer.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(numBuffer.toString()));
                continue;
            }

            //Handle numbers (including decimals)
            if (Character.isDigit(c) || c == '.') {
                StringBuilder numBuffer = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    numBuffer.append(expression.charAt(i));
                    i++;
                }
                numbers.push(Double.parseDouble(numBuffer.toString()));
                continue;
            }

            //Handle Parentheses
            if (c == '(') {
                int closeIndex = findClosingBracket(expression, i, '(', ')');
                if (closeIndex == -1) return 0; // Mismatched parentheses

                String subExpression = expression.substring(i + 1, closeIndex);
                double subResult = evaluateExpression(subExpression);

                numbers.push(subResult);
                i = closeIndex + 1;
                continue;
            }

            //Handle Operators (+, -, *, /)
            if (isOperator(Character.toString(c))) {
                while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
            }

            i++;
        }

        //Process remaining operators
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    //Evaluates the trigonometric, logorithmic, ln, and exponent functions. Provided by yours truely chatgpt with tinkering by me to fix a couple small issues
    private String evaluateFunctions(String expression) {
        String[] functions = {"sin", "cos", "tan", "log", "ln"};

        //Process functions like sin(), cos(), log(), ln()
        for (String func : functions) {
            while (expression.contains(func + "(")) {
                int startIndex = expression.lastIndexOf(func + "(");
                int endIndex = findClosingBracket(expression, startIndex + func.length(), '(', ')');

                //No matching closing parenthesis
                if (endIndex == -1) return expression;

                String inside = expression.substring(startIndex + func.length() + 1, endIndex);
                double value = evaluateExpression(inside);
                double result = 0;

                switch (func) {
                    case "sin": result = Math.sin(Math.toRadians(value)); break;
                    case "cos": result = Math.cos(Math.toRadians(value)); break;
                    case "tan": result = Math.tan(Math.toRadians(value)); break;
                    case "log": result = Math.log10(value); break;
                    case "ln": result = Math.log(value); break;
                }

                //Replace function call with computed result
                expression = expression.substring(0, startIndex) + result + expression.substring(endIndex + 1);
            }
        }

        //Process exponentiation (`^`) **right to left**
        while (expression.contains("^")) {
            int lastIndex = expression.lastIndexOf("^"); // Find last `^` (rightmost first)

            //Find base (left-side number or expression)
            int baseStart = lastIndex - 1;
            if (expression.charAt(baseStart) == ')') {
                baseStart = findOpeningBracket(expression, baseStart, '(', ')'); //Handle expressions like `(2+3)^2`
            } else {
                while (baseStart >= 0 && (Character.isDigit(expression.charAt(baseStart)) || expression.charAt(baseStart) == '.' || expression.charAt(baseStart) == '-')) {
                    baseStart--;
                }
                baseStart++;
            }

            //Find exponent (right-side number or expression)
            int expStart = lastIndex + 1;
            int expEnd = expStart;
            if (expression.charAt(expStart) == '(') {
                expEnd = findClosingBracket(expression, expStart, '(', ')') + 1; // Handle expressions like `2^(1+1)`
            } else {
                while (expEnd < expression.length() && (Character.isDigit(expression.charAt(expEnd)) || expression.charAt(expEnd) == '.')) {
                    expEnd++;
                }
            }

            //Extract base and exponent
            double base = evaluateExpression(expression.substring(baseStart, lastIndex)); // Base must be evaluated
            double exponent = evaluateExpression(expression.substring(expStart, expEnd)); // Exponent must be evaluated

            //Compute power
            double result = Math.pow(base, exponent);

            //Replace exponentiation part with computed result
            expression = expression.substring(0, baseStart) + result + expression.substring(expEnd);
        }

        return expression;
    }

    //Finds the closing bracket for a given opening bracket, and the opening bracket for a given closed bracket
    private int findClosingBracket(String expression, int openIndex, char open, char close) {
        int balance = 0;
        for (int i = openIndex; i < expression.length(); i++) {
            if (expression.charAt(i) == open) balance++;
            if (expression.charAt(i) == close) balance--;
            if (balance == 0) return i;
        }
        return -1; //No matching bracket found
    }
    private int findOpeningBracket(String expression, int closeIndex, char open, char close) {
        int balance = 0;
        for (int i = closeIndex; i >= 0; i--) {
            if (expression.charAt(i) == close) balance++;
            if (expression.charAt(i) == open) balance--;
            if (balance == 0) return i; // Found the matching open bracket
        }
        return -1; //No matching bracket found
    }

    private String preprocessExpression(String expression) {
        //Handles nested negatives
        while (expression.contains("(-(-")) {
            expression = expression.replace("(-(-", "");
            expression = expression.replace("))", "");
        }

        //Insert Explicit Multiplication for Parentheses and Curly Brackets
        expression = expression.replaceAll("(\\d)(\\()", "$1*(");  // 2(5) → 2*(5)
        expression = expression.replaceAll("(\\))(\\d)", ")*$2");  // (2)5 → (2)*5
        expression = expression.replaceAll("(\\d)(\\{)", "$1*{");  // 3{4} → 3*{4}
        expression = expression.replaceAll("(\\})(\\d)", "}*$2");  // {3}4 → {3}*4
        expression = expression.replaceAll("(\\))(\\{)", ")*{");   // (2){3} → (2)*{3}
        expression = expression.replaceAll("(\\})(\\()", "}*(");   // {3}(4) → {3}*(4)

        return expression;
    }

    //Determines operator precedence
    private int precedence(char operator) {
        switch (operator) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            case '^': return 3;
            default: return -1;
        }
    }

    //Applies the mathematical operation
    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/': return a / b;
            case '^': return Math.pow(a, b);
            default: return 0;
        }
    }

    //Allows the values of the buttons to be input into the text field
    public void handleButtonClick(String value) {
        String currentText = output.getText();

        if (isOperator(value)) {
            //Prevent multiple operators in a row
            if (currentText.isEmpty() || isOperator(Character.toString(currentText.charAt(currentText.length() - 1)))) {
                return;
            }
        }

        //Prevents an exponent from being used in the wrong spot
        if (value.equals("^")) {
            if (currentText.isEmpty()) {
                return;
            }
            char lastChar = currentText.charAt(currentText.length() - 1);
            if (!Character.isDigit(lastChar) && lastChar != ')') {
                return;
            }
        }

        output.appendText(value);
    }

    //Checks for operators
    private boolean isOperator(String value) {
        return value.equals("+") || value.equals("-") || value.equals("*") || value.equals("/");
    }

    //Allows the clearing or undoing of those two actions to function
    public void handleDelete(ActionEvent event) {
        String currentText = output.getText();

        //Handles the undo button
        if (event.getSource() == undo && !currentText.isEmpty()) {
            output.setText(currentText.substring(0, currentText.length() - 1));
        }
        //Handles the clear button
        else if (event.getSource() == clear) {
            output.clear();
        }
    }
    //Toggles the negative (utilized chatgpt for the logic) (this function was such a headache to get working)
    public void handleNegativeToggle() {
        String currentText = output.getText().trim();
        if (currentText.isEmpty()) return;

        //Case: If the last character is an open parenthesis `(` or `{`, insert `-` immediately
        if (currentText.endsWith("(") || currentText.endsWith("{")) {
            output.setText(currentText + "-");
            return;
        }

        //Check if the entire input is a single number (not part of an expression)
        try {
            double number = Double.parseDouble(currentText);
            number = -number; // Toggle sign
            output.setText(String.valueOf(number)); // Update display
            return;
        } catch (NumberFormatException e) {
            //If not a standalone number, continue checking expression
        }

        //Find the last operator position
        int lastOperatorIndex = -1;
        for (int i = currentText.length() - 1; i >= 0; i--) {
            char c = currentText.charAt(i);
            if (isOperator(Character.toString(c))) {
                lastOperatorIndex = i;
                break;
            }
        }

        //Extract last number (including cases where it's inside parentheses `{}` or `()`)
        String lastNumber = currentText.substring(lastOperatorIndex + 1).trim();

        //Case: Unclosed Parenthesis or Bracket with a Number (e.g., `(2` → `(-2` or `{2` → `{-2`)
        if ((lastNumber.startsWith("(") && !lastNumber.endsWith(")")) ||
                (lastNumber.startsWith("{") && !lastNumber.endsWith("}"))) {
            char bracketType = lastNumber.charAt(0); //Detect the bracket type `(` or `{`
            output.setText(currentText.substring(0, lastOperatorIndex + 1) + bracketType + "-" + lastNumber.substring(1));
            return;
        }

        //Case: Completed Parentheses `(2)` → `(-2)`, `{2}` → `{-2}`
        if ((lastNumber.startsWith("(") && lastNumber.endsWith(")")) ||
                (lastNumber.startsWith("{") && lastNumber.endsWith("}"))) {
            char bracketType = lastNumber.charAt(0); // Preserve original bracket type
            String innerValue = lastNumber.substring(1, lastNumber.length() - 1).trim();
            try {
                double num = Double.parseDouble(innerValue);
                num = -num; //Toggle the sign
                output.setText(currentText.substring(0, lastOperatorIndex + 1) + bracketType + num + bracketType);
                return;
            } catch (NumberFormatException ignored) {
                //Continue to other cases if parsing fails
            }
        }

        //Case: Number at start of expression (no operators before it)
        if (lastOperatorIndex == -1) {
            try {
                double num = Double.parseDouble(lastNumber);
                num = -num;
                output.setText(String.valueOf(num)); //Directly update display
            } catch (NumberFormatException ignored) {
            }
            return;
        }

        //Case: Toggle last number in expression
        try {
            double lastNum = Double.parseDouble(lastNumber);
            lastNum = -lastNum;
            output.setText(currentText.substring(0, lastOperatorIndex + 1) + lastNum);
        }
        catch (NumberFormatException ignored) {
        }
    }

    //Handles the decimal, so they can't be used more than once per number
    private void handleDecimal() {
        String currentText = output.getText();

        if (currentText.isEmpty()) {
            output.setText("0.");
            return;
        }

        //Find the last number in the expression
        int lastOperatorIndex = -1;
        for (int i = currentText.length() - 1; i >= 0; i--) {
            char c = currentText.charAt(i);
            if (c == '+' || c == '-' || c == '*' || c == '/') {
                lastOperatorIndex = i;
                break;
            }
        }

        //Extract the last number
        String lastNumber = currentText.substring(lastOperatorIndex + 1).trim();

        //Check if the last number already has a decimal
        if (!lastNumber.contains(".")) {
            output.setText(currentText + ".");
        }
    }
}

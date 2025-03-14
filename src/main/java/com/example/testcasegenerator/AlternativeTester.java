package com.example.testcasegenerator;

import org.mariuszgromada.math.mxparser.Expression;

public class AlternativeTester {
    /**
     * Uses mXparser to evaluate a mathematical expression.
     * This library supports sin, cos, tan, log, ln, exponentiation (^), etc.
     * @param expression the mathematical expression as a String.
     * @return the evaluated result as a String, or an error message if evaluation fails.
     */
    public static String test(String expression) {
        Expression exp = new Expression(expression);
        double result = exp.calculate();
        if(Double.isNaN(result)) {
            return "Error: Invalid Expression";
        }
        return String.valueOf(result);
    }
}

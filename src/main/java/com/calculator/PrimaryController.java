package com.calculator;

import java.util.Stack;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class PrimaryController {

    @FXML
    private TextField txt_result;

    private double number1 = 0;
    private String operator = "";
    private boolean isStartNumber = true;

    // Parenthesis state
    private Stack<Double>  savedNumber1   = new Stack<>();
    private Stack<String>  savedOperator  = new Stack<>();
    private boolean        insideParen    = false;

    @FXML
    void onClearClick(ActionEvent event) {
        txt_result.clear();
        number1 = 0;
        operator = "";
        isStartNumber = true;
        savedNumber1.clear();
        savedOperator.clear();
        insideParen = false;
    }

    @FXML
    void onNumberClick(ActionEvent event) {
        String number = ((Button) event.getSource()).getText();

        if (isStartNumber) {
            txt_result.setText(number.equals(".") ? "0." : number);
            isStartNumber = false;
        } else {
            if (number.equals(".") && txt_result.getText().contains(".")) {
                return;
            }
            txt_result.appendText(number);
        }
    }

    @FXML
    void onOperatorClick(ActionEvent event) {
        String currentText = txt_result.getText();
        if (currentText.isEmpty() || currentText.equals("Aldaa")) {
            return;
        }

        if (!operator.isEmpty() && !isStartNumber) {
            double number2 = Double.parseDouble(currentText);
            double result = calculate(number1, operator, number2);
            if (result == Double.MAX_VALUE) return; // divide by zero — already handled
            number1 = result;
            setDisplay(result);
        } else {
            number1 = Double.parseDouble(currentText);
        }

        operator = ((Button) event.getSource()).getText();
        isStartNumber = true;
    }

    @FXML
    void onEqualClick(ActionEvent event) {
        String currentText = txt_result.getText();
        if (operator.isEmpty() || currentText.isEmpty() || currentText.equals("Aldaa")) {
            return;
        }

        double number2 = Double.parseDouble(currentText);
        double result = calculate(number1, operator, number2);
        if (result == Double.MAX_VALUE) return;

        // If inside parenthesis, close it automatically on '='
        if (insideParen) {
            number1 = savedNumber1.pop();
            operator = savedOperator.pop();
            insideParen = false;
            double finalResult = calculate(number1, operator, result);
            if (finalResult == Double.MAX_VALUE) return;
            setDisplay(finalResult);
            number1 = finalResult;
        } else {
            setDisplay(result);
            number1 = result;
        }

        operator = "";
        isStartNumber = true;
    }

    @FXML
    void onOpenParenClick(ActionEvent event) {
        String currentText = txt_result.getText();

        // Save current state
        savedNumber1.push(currentText.isEmpty() || isStartNumber ? number1 :
                Double.parseDouble(currentText));
        savedOperator.push(operator);

        // Reset for expression inside paren
        number1 = 0;
        operator = "";
        isStartNumber = true;
        insideParen = true;

        txt_result.setText("0");
    }

    @FXML
    void onCloseParenClick(ActionEvent event) {
        if (!insideParen || savedNumber1.isEmpty()) {
            return; // no matching '(' — ignore
        }

        String currentText = txt_result.getText();
        if (currentText.isEmpty() || currentText.equals("Aldaa")) {
            return;
        }

        // Finish expression inside paren
        double innerResult;
        if (!operator.isEmpty()) {
            double number2 = Double.parseDouble(currentText);
            innerResult = calculate(number1, operator, number2);
            if (innerResult == Double.MAX_VALUE) return;
        } else {
            innerResult = Double.parseDouble(currentText);
        }

        // Restore outer state
        number1 = savedNumber1.pop();
        operator = savedOperator.pop();
        insideParen = false;

        // Inner result becomes current input for outer expression
        setDisplay(innerResult);
        if (innerResult % 1 == 0) {
            txt_result.setText(String.valueOf((long) innerResult));
        } else {
            txt_result.setText(String.valueOf(innerResult));
        }
        isStartNumber = false;
    }

    @FXML
    void onPlusMinusClick(ActionEvent event) {
        String currentText = txt_result.getText();
        if (currentText.isEmpty() || currentText.equals("0") || currentText.equals("Aldaa")) {
            return;
        }

        double value = Double.parseDouble(currentText) * -1;
        setDisplay(value);
        txt_result.setText(value % 1 == 0
                ? String.valueOf((long) value)
                : String.valueOf(value));
    }

    // --- Helpers ---

    private double calculate(double a, String op, double b) {
        switch (op) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/":
                if (b == 0) {
                    txt_result.setText("Aldaa");
                    operator = "";
                    isStartNumber = true;
                    return Double.MAX_VALUE;
                }
                return a / b;
        }
        return a;
    }

    private void setDisplay(double value) {
        if (value % 1 == 0) {
            txt_result.setText(String.valueOf((long) value));
        } else {
            txt_result.setText(String.valueOf(value));
        }
    }
}
package com.bugsense.service;

import com.bugsense.model.ErrorMessage;
import com.bugsense.service.LexerService.Token;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ParserService {

    public void parse(List<Token> tokens, List<ErrorMessage> errors) {
        checkBraces(tokens, errors);
        checkSemicolons(tokens, errors);
        checkIfWhileFor(tokens, errors);
    }

    private void checkBraces(List<Token> tokens, List<ErrorMessage> errors) {
        Deque<Token> stack = new ArrayDeque<>();
        Map<String, String> matching = Map.of(")", "(", "}", "{", "]", "[");

        for (Token t : tokens) {
            if (List.of("(", "{", "[").contains(t.value)) {
                stack.push(t);
            } else if (List.of(")", "}", "]").contains(t.value)) {
                if (stack.isEmpty() || !stack.peek().value.equals(matching.get(t.value))) {
                    errors.add(new ErrorMessage(t.line, "SYNTAX",
                        "Unmatched closing bracket: '" + t.value + "'"));
                } else {
                    stack.pop();
                }
            }
        }
        while (!stack.isEmpty()) {
            Token t = stack.pop();
            errors.add(new ErrorMessage(t.line, "SYNTAX",
                "Unmatched opening bracket: '" + t.value + "'"));
        }
    }

    private void checkSemicolons(List<Token> tokens, List<ErrorMessage> errors) {
        Set<String> statementStarters = Set.of("int", "float", "char", "double",
            "return", "printf", "scanf", "break", "continue");

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (t.type.equals("KEYWORD") && statementStarters.contains(t.value)) {
                int j = i + 1;
                int startLine = t.line;
                while (j < tokens.size()) {
                    String v = tokens.get(j).value;
                    if (v.equals(";")) break;
                    if (v.equals("{") || v.equals("}")) {
                        if (!t.value.equals("int") || !isFunction(tokens, i)) {
                            errors.add(new ErrorMessage(startLine, "SYNTAX",
                                "Missing semicolon after '" + t.value + "' statement"));
                        }
                        break;
                    }
                    j++;
                }
                if (j >= tokens.size()) {
                    errors.add(new ErrorMessage(startLine, "SYNTAX",
                        "Missing semicolon at end of '" + t.value + "' statement"));
                }
            }
        }
    }

    private boolean isFunction(List<Token> tokens, int keywordIndex) {
        for (int i = keywordIndex + 1; i < tokens.size() && i < keywordIndex + 4; i++) {
            if (tokens.get(i).value.equals("(")) return true;
            if (tokens.get(i).value.equals(";")) return false;
        }
        return false;
    }

    private void checkIfWhileFor(List<Token> tokens, List<ErrorMessage> errors) {
        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);
            if (List.of("if", "while", "for").contains(t.value)) {
                if (i + 1 >= tokens.size() || !tokens.get(i + 1).value.equals("(")) {
                    errors.add(new ErrorMessage(t.line, "SYNTAX",
                        "'" + t.value + "' must be followed by '('"));
                }
            }
        }
    }
}
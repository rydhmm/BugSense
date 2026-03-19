package com.bugsense.service;

import com.bugsense.model.ErrorMessage;
import com.bugsense.service.LexerService.Token;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SemanticService {

    private static final Set<String> DATA_TYPES = Set.of("int", "float", "char", "double", "void");

    public void analyze(List<Token> tokens, List<ErrorMessage> errors) {
        Map<String, String> symbolTable = new HashMap<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token t = tokens.get(i);

            if (t.type.equals("KEYWORD") && DATA_TYPES.contains(t.value)) {
                if (i + 1 < tokens.size() && tokens.get(i + 1).type.equals("IDENTIFIER")) {
                    String varName = tokens.get(i + 1).value;
                    String varType = t.value;
                    if (i + 2 < tokens.size() && tokens.get(i + 2).value.equals("(")) continue;
                    if (symbolTable.containsKey(varName)) {
                        errors.add(new ErrorMessage(tokens.get(i + 1).line, "SEMANTIC",
                            "Variable '" + varName + "' already declared"));
                    } else {
                        symbolTable.put(varName, varType);
                    }
                }
            }

            if (t.type.equals("IDENTIFIER")) {
                if (i > 0 && tokens.get(i - 1).type.equals("KEYWORD")
                        && DATA_TYPES.contains(tokens.get(i - 1).value)) continue;
                if (i + 1 < tokens.size() && tokens.get(i + 1).value.equals("(")) continue;
                if (!symbolTable.containsKey(t.value)) {
                    errors.add(new ErrorMessage(t.line, "SEMANTIC",
                        "Undeclared variable: '" + t.value + "'"));
                }
            }

            if (t.value.equals("=") && i > 0 && i + 1 < tokens.size()) {
                Token lhs = tokens.get(i - 1);
                Token rhs = tokens.get(i + 1);
                if (lhs.type.equals("IDENTIFIER") && symbolTable.containsKey(lhs.value)) {
                    String declaredType = symbolTable.get(lhs.value);
                    if (declaredType.equals("int") && rhs.type.equals("NUMBER")
                            && rhs.value.contains(".")) {
                        errors.add(new ErrorMessage(t.line, "SEMANTIC",
                            "Type mismatch: assigning float value to int variable '" + lhs.value + "'"));
                    }
                }
            }
        }
    }
}
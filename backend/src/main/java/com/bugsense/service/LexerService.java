package com.bugsense.service;

import com.bugsense.model.ErrorMessage;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class LexerService {

    private static final Set<String> KEYWORDS = Set.of(
        "int", "float", "char", "double", "void", "return",
        "if", "else", "while", "for", "do", "break", "continue",
        "printf", "scanf", "main"
    );

    public static class Token {
        public String type;
        public String value;
        public int line;

        public Token(String type, String value, int line) {
            this.type = type;
            this.value = value;
            this.line = line;
        }
    }

    public List<Token> tokenize(String code, List<ErrorMessage> errors) {
        List<Token> tokens = new ArrayList<>();
        String[] lines = code.split("\n");

        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = lines[lineNum];
            int i = 0;

            while (i < line.length()) {
                char ch = line.charAt(i);

                if (Character.isWhitespace(ch)) { i++; continue; }
                if (i + 1 < line.length() && ch == '/' && line.charAt(i + 1) == '/') break;

                if (Character.isDigit(ch)) {
                    StringBuilder num = new StringBuilder();
                    while (i < line.length() && (Character.isDigit(line.charAt(i)) || line.charAt(i) == '.'))
                        num.append(line.charAt(i++));
                    tokens.add(new Token("NUMBER", num.toString(), lineNum + 1));
                    continue;
                }

                if (Character.isLetter(ch) || ch == '_') {
                    StringBuilder word = new StringBuilder();
                    while (i < line.length() && (Character.isLetterOrDigit(line.charAt(i)) || line.charAt(i) == '_'))
                        word.append(line.charAt(i++));
                    String w = word.toString();
                    tokens.add(new Token(KEYWORDS.contains(w) ? "KEYWORD" : "IDENTIFIER", w, lineNum + 1));
                    continue;
                }

                if (ch == '"') {
                    StringBuilder str = new StringBuilder();
                    i++;
                    while (i < line.length() && line.charAt(i) != '"')
                        str.append(line.charAt(i++));
                    if (i >= line.length())
                        errors.add(new ErrorMessage(lineNum + 1, "LEXICAL", "Unterminated string literal"));
                    else i++;
                    tokens.add(new Token("STRING", str.toString(), lineNum + 1));
                    continue;
                }

                String twoChar = (i + 1 < line.length()) ? "" + ch + line.charAt(i + 1) : "";
                if (List.of("==", "!=", "<=", ">=", "&&", "||", "++", "--").contains(twoChar)) {
                    tokens.add(new Token("OPERATOR", twoChar, lineNum + 1));
                    i += 2;
                } else if ("+-*/=<>!%&|".indexOf(ch) >= 0) {
                    tokens.add(new Token("OPERATOR", String.valueOf(ch), lineNum + 1));
                    i++;
                } else if ("(){};,".indexOf(ch) >= 0) {
                    tokens.add(new Token("SEPARATOR", String.valueOf(ch), lineNum + 1));
                    i++;
                } else if (ch == '#') {
                    break;
                } else {
                    errors.add(new ErrorMessage(lineNum + 1, "LEXICAL",
                        "Invalid character: '" + ch + "'"));
                    i++;
                }
            }
        }
        return tokens;
    }
}
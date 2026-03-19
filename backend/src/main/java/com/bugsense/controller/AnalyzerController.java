package com.bugsense.controller;

import com.bugsense.dto.AnalysisResult;
import com.bugsense.model.ErrorMessage;
import com.bugsense.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AnalyzerController {

    @Autowired private LexerService lexerService;
    @Autowired private ParserService parserService;
    @Autowired private SemanticService semanticService;

    @PostMapping("/analyze")
    public AnalysisResult analyze(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        List<ErrorMessage> errors = new ArrayList<>();

        List<LexerService.Token> tokens = lexerService.tokenize(code, errors);
        parserService.parse(tokens, errors);
        semanticService.analyze(tokens, errors);

        errors.sort(Comparator.comparingInt(ErrorMessage::getLine));
        return new AnalysisResult(errors);
    }
}
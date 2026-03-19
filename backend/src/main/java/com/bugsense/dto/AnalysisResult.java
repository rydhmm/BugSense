package com.bugsense.dto;

import com.bugsense.model.ErrorMessage;
import java.util.List;

public class AnalysisResult {
    private List<ErrorMessage> errors;
    private boolean success;

    public AnalysisResult(List<ErrorMessage> errors) {
        this.errors = errors;
        this.success = errors.isEmpty();
    }

    public List<ErrorMessage> getErrors() { return errors; }
    public boolean isSuccess() { return success; }
}
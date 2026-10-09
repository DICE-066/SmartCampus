package com.smartcampus.service;

import org.springframework.stereotype.Component;
import java.util.List;

/** SDD: ReportGenerator — generic CSV-formatting utility shared by all report types. */
@Component
public class ReportGenerator {

    public String exportCSV(List<String> headers, List<List<String>> rows) {
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", headers)).append("\n");
        for (List<String> row : rows) {
            csv.append(String.join(",", row.stream().map(this::escape).toList())).append("\n");
        }
        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

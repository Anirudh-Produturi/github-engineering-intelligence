package dev.engineeringintelligence.api.coverage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

class CoverageReportParserTest {
    private final CoverageReportParser parser = new CoverageReportParser();

    @Test void parsesJacoco() {
        var report="<report><package name=\"src\"><sourcefile name=\"A.java\"><counter type=\"LINE\" missed=\"2\" covered=\"8\"/></sourcefile></package></report>";
        assertThat(parser.parse(CoverageReportParser.Format.JACOCO,report).getFirst().lineCoverage()).isEqualTo(.8);
    }
    @Test void parsesCobertura() {
        var report="<coverage><packages><class filename=\"a.py\" line-rate=\"0.75\"/></packages></coverage>";
        assertThat(parser.parse(CoverageReportParser.Format.COBERTURA,report).getFirst().lineCoverage()).isEqualTo(.75);
    }
    @Test void parsesLcov() {
        var report="SF:src/a.ts\nDA:1,1\nDA:2,0\nend_of_record\n";
        assertThat(parser.parse(CoverageReportParser.Format.LCOV,report).getFirst().lineCoverage()).isEqualTo(.5);
    }
    @Test void rejectsXmlDoctype() {
        assertThatThrownBy(() -> parser.parse(CoverageReportParser.Format.JACOCO,"<!DOCTYPE foo><report/>"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}


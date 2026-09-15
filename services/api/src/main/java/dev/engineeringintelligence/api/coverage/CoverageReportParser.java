package dev.engineeringintelligence.api.coverage;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

@Component
public class CoverageReportParser {
    public enum Format { JACOCO, COBERTURA, LCOV }
    public record FileCoverage(String path, double lineCoverage) {}

    public List<FileCoverage> parse(Format format, String report) {
        if (report == null || report.length() > 10_000_000) throw new IllegalArgumentException("Coverage report is empty or too large");
        return format == Format.LCOV ? parseLcov(report) : parseXml(format, report);
    }

    private List<FileCoverage> parseLcov(String report) {
        List<FileCoverage> result = new ArrayList<>();
        String path = null; int found = 0; int hit = 0;
        for (String line : report.lines().toList()) {
            if (line.startsWith("SF:")) path = line.substring(3);
            else if (line.startsWith("DA:")) { found++; if (Integer.parseInt(line.substring(line.indexOf(',')+1)) > 0) hit++; }
            else if (line.equals("end_of_record") && path != null) {
                result.add(new FileCoverage(path, found == 0 ? 0 : (double)hit/found)); path=null; found=0; hit=0;
            }
        }
        return result;
    }

    private List<FileCoverage> parseXml(Format format, String report) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            var document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(StandardCharsets.UTF_8)));
            List<FileCoverage> result = new ArrayList<>();
            if (format == Format.COBERTURA) {
                var nodes=document.getElementsByTagName("class");
                for(int i=0;i<nodes.getLength();i++){var e=(Element)nodes.item(i);result.add(new FileCoverage(e.getAttribute("filename"),Double.parseDouble(e.getAttribute("line-rate"))));}
            } else {
                var packages=document.getElementsByTagName("package");
                for(int p=0;p<packages.getLength();p++){var pack=(Element)packages.item(p);var files=pack.getElementsByTagName("sourcefile");
                    for(int i=0;i<files.getLength();i++){var file=(Element)files.item(i);var counters=file.getElementsByTagName("counter");
                        for(int c=0;c<counters.getLength();c++){var counter=(Element)counters.item(c);if("LINE".equals(counter.getAttribute("type"))){int missed=Integer.parseInt(counter.getAttribute("missed"));int covered=Integer.parseInt(counter.getAttribute("covered"));result.add(new FileCoverage(pack.getAttribute("name")+"/"+file.getAttribute("name"),covered+missed==0?0:(double)covered/(covered+missed)));break;}}}}
            }
            return result;
        } catch (Exception exception) { throw new IllegalArgumentException("Invalid "+format+" report", exception); }
    }
}


package net.sf.alchim.spoon.contrib.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;

import net.sf.alchim.spoon.contrib.maven.SpoonReportData.GroupBy;
import net.sf.alchim.spoon.contrib.maven.SpoonReportData.LineInfo;
import net.sf.alchim.spoon.contrib.maven.SpoonReportData.Severity;

class SpoonReportGenerator {
    private Sink sink_;
    private ResourceBundle bundle_;
    private String xrefLocationBase_;

    SpoonReportGenerator(Sink sink, ResourceBundle bundle, String xrefLocationBase) {
        sink_ = sink;
        bundle_ = bundle;
        xrefLocationBase_ = xrefLocationBase;
    }

    public void doGenerateReport(SpoonReportData data) throws Exception {
        beginReport();
        printSummary(data);
        printGroupBy(data.groupByProcessorClassList, "processors", "classes");
        printGroupBy(data.groupByClassProcessorList, "classes", "processors");
        endReport();
    }

    private String getString(String key) {
        return bundle_.getString("report.spoon." + key);
    }

    private void beginReport() {
        sink_.head();
        sink_.title();
        sink_.text(getString("title"));
        sink_.title_();
        StringBuilder str = new StringBuilder();
        str.append("<script type='text/javascript'>\n");
        str.append("function toggleDisplay(elementId) {\n");
        str.append("  var elm = document.getElementById(elementId);\n");
        str.append("  if (elm && typeof elm.style != 'undefined') {\n");
        str.append("    if (elm.style.display == 'none') {\n");
        str.append("      elm.style.display = '';\n");
        str.append("      document.getElementById(elementId + '_on').style.display = 'none';\n");
        str.append("      document.getElementById(elementId + '_off').style.display = 'inline';\n");
        str.append("    } else if (elm.style.display == '') {");
        str.append("      elm.style.display = 'none';\n");
        str.append("      document.getElementById(elementId + '_on').style.display = 'inline';\n");
        str.append("      document.getElementById(elementId + '_off').style.display = 'none';\n");
        str.append("    } \n");
        str.append("  } \n");
        str.append("}\n");
        str.append("</script>");
        sink_.rawText(str.toString());
        sink_.head_();

        sink_.body();

        // the title of the report
        sink_.section1();
        sink_.sectionTitle1();
        sink_.text(getString("title"));
        sink_.sectionTitle1_();

        // information about FindBugs
        sink_.paragraph();
        sink_.text(getString("linktitle") + " ");
        sink_.link(getString("link"));
        sink_.text(getString("name"));
        sink_.link_();
        sink_.paragraph_();

        // sink_.paragraph();
        // sink_.text(getString("versiontitle") + " " );
        // sink_.italic();
        // sink_.text( this.getFindBugsVersion() );
        // sink_.italic_();
        // sink_.paragraph_();

        sink_.section1_();

    }

    // TODO: add links to details
    private void printSummary(SpoonReportData data) {
        // the summary section
        sink_.sectionTitle1();
        sink_.text(getString("summary"));
        sink_.sectionTitle1_();

        sink_.table();

        // header
        sink_.tableRow();

        sink_.tableHeaderCell();
        sink_.text(getString("column.processors"));
        sink_.tableHeaderCell_();

        sink_.tableHeaderCell();
        sink_.text(getString("column.classes"));
        sink_.tableHeaderCell_();

        sink_.tableHeaderCell();
        sink_.text(getString("column.errors"));
        sink_.tableHeaderCell_();

        sink_.tableHeaderCell();
        sink_.text(getString("column.warnings"));
        sink_.tableHeaderCell_();

        sink_.tableHeaderCell();
        sink_.text(getString("column.infos"));
        sink_.tableHeaderCell_();

        sink_.tableRow_();

        // Total
        sink_.tableRow();

        sink_.tableHeaderCell();
        sink_.text("...TOTAL...");
        sink_.tableHeaderCell_();

        sink_.tableCell();
        sink_.text(Integer.toString(data.groupByClassProcessorList.size()));
        sink_.tableCell_();

        sink_.tableCell();
        sink_.text(Integer.toString(data.total.nbErrors));
        sink_.tableCell_();

        sink_.tableCell();
        sink_.text(Integer.toString(data.total.nbWarnings));
        sink_.tableCell_();

        sink_.tableCell();
        sink_.text(Integer.toString(data.total.nbInfos));
        sink_.tableCell_();

        sink_.tableRow_();

        // Summary group by Processor, order by nbErrors, nbWarnings, nbInfos,
        // ProcessorName
        for (GroupBy group : data.groupByProcessorClassList) {
            sink_.tableRow();

            sink_.tableCell();
            sink_.text(group.name);
            sink_.tableCell_();

            sink_.tableCell();
            sink_.text(Integer.toString(group.groups.size()));
            sink_.tableCell_();

            sink_.tableCell();
            sink_.text(Integer.toString(group.cnt.nbErrors));
            sink_.tableCell_();

            sink_.tableCell();
            sink_.text(Integer.toString(group.cnt.nbWarnings));
            sink_.tableCell_();

            sink_.tableCell();
            sink_.text(Integer.toString(group.cnt.nbInfos));
            sink_.tableCell_();

            sink_.tableRow_();
        }

        sink_.table_();

        sink_.paragraph_();
        sink_.section1_();
    }

    // TODO: add toggle display button
    private void printGroupBy(List<GroupBy> groups, String l2, String l1) {
        sink_.section1();

        // the summary section
        sink_.sectionTitle1();
        sink_.text(getString("column." + l2));
        sink_.sectionTitle1_();

        String htmlId = getString("column." + l2).replaceAll("/|\\| ", "_");
        sink_.rawText("<div class='detailToggle' style='display:inline'>");
        sink_.link("javascript:toggleDisplay('" + htmlId + "');");

        sink_.rawText("<span style='display:inline;' id='" + htmlId + "_on'>+</span><span id='" + htmlId + "_off' " + "style='display: none;'>-</span> ");
        sink_.text("[ Detail ]");
        sink_.link_();

        sink_.rawText("</div>");

        sink_.rawText("<div id='" + htmlId + "' style='display:none'>");
        for (GroupBy group : groups) {
            sink_.section2();
            sink_.sectionTitle2();
            sink_.anchor(group.name);
            sink_.text(group.name);
            sink_.anchor_();
            sink_.sectionTitle2_();
            sink_.table();

            // header
            sink_.tableRow();

            sink_.tableHeaderCell();
            sink_.text(getString("column.severity"));
            sink_.tableHeaderCell_();

            sink_.tableHeaderCell();
            sink_.text(getString("column." + l1));
            sink_.tableHeaderCell_();

            sink_.tableHeaderCell();
            sink_.text(getString("column.msg"));
            sink_.tableHeaderCell_();

            sink_.tableHeaderCell();
            sink_.text(getString("column.line"));
            sink_.tableHeaderCell_();

            sink_.tableRow_();

            // contents
            List<String> keys = new ArrayList<String>(group.groups.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                for (LineInfo value : group.groups.get(key)) {
                    sink_.tableRow();

                    sink_.tableCell();
                    sinkSeverityIcon(value.severity);
                    sink_.tableCell_();

                    sink_.tableCell();
                    sink_.link("#" + key);
                    sink_.text(key);
                    sink_.link_();
                    sink_.tableCell_();

                    sink_.tableCell();
                    sink_.text(value.msg);
                    sink_.tableCell_();

                    sink_.tableCell();
                    sinkSourceLink(value.className, value.line);
                    sink_.tableCell_();

                    sink_.tableRow_();
                }
            }
            sink_.table_();
            sink_.section2_();
        }
        sink_.rawText("</div>");
        sink_.section1_();
    }

    /**
     * Assembles the hyperlink to point to the source code.
     *
     * @return The hyperlink which points to the code.
     *
     */
    private void sinkSourceLink(String className, int line) {
        String hyperlink = null;
        if (xrefLocationBase_ != null) {
            final String path = xrefLocationBase_ + "/" + className.replaceAll("[.]", "/");
            if (line < 0) {
                hyperlink = "<a href=" + path + ".html>" + line + "</a>";
            } else {
                hyperlink = "<a href=" + path + ".html#" + line + ">" + line + "</a>";
            }
        }

        if (hyperlink == null) {
            sink_.rawText(hyperlink);
        } else {
            sink_.text(Integer.toString(line));
        }
    }

    private void endReport() {
        sink_.body_();
    }

    private void sinkSeverityIcon(Severity severity) {
        String image = null;
        String altText = null;

        if (severity == null) {
            image = "images/UNDEF.png";
            altText = "UNDEF";
        } else {
            image = "images/" + severity + ".png";
            altText = severity.name();
        }

        sink_.figure();
        sink_.figureGraphics(image);
        if (altText != null) {
            sink_.figureCaption();
            sink_.text(altText);
            sink_.figureCaption_();
        }
        sink_.figure_();
    }

}

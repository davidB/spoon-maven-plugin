package net.sf.alchim.spoon.contrib.maven;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class SpoonReportData {

    enum Severity {ERROR, WARNING, MESSAGE };

    private Map<String, GroupBy> groupByProcessorClassMap_;
    private Map<String, GroupBy> groupByClassProcessorMap_;

    List<GroupBy> groupByProcessorClassList;
    List<GroupBy> groupByClassProcessorList;
    Counter total;

    public void begin() {
        groupByProcessorClassMap_ = new TreeMap<String, GroupBy>();
        groupByClassProcessorMap_ = new TreeMap<String, GroupBy>();
        total = new Counter();
    }

    public void add(String processorName, String className, String lineNumber, String severity, String msg) {
        LineInfo line = new LineInfo();
        line.line = Integer.parseInt(lineNumber);
        try {
            line.severity = Severity.valueOf(severity);
        } catch (IllegalArgumentException exc) {
            line.severity = null;
        }
        line.msg = msg;
        line.className = className;

        total.add(line.severity);
        add(groupByProcessorClassMap_, processorName, className, line);
        add(groupByClassProcessorMap_, className, processorName, line);
    }

    private void add(Map<String, GroupBy> groups, String l2, String l1, LineInfo line) {
        GroupBy group = groups.get(l2);
        if (group == null) {
            group = new GroupBy(l2);
            groups.put(l2, group);
        }
        group.add(l1, line);
    }

    public void end() {
        Comparator<GroupBy> cmp = new GroupByComparatorSeverity();

        groupByProcessorClassList = new ArrayList<GroupBy>(groupByProcessorClassMap_.values());
        Collections.sort(groupByProcessorClassList, cmp);
        groupByProcessorClassMap_ = null;

        groupByClassProcessorList = new ArrayList<GroupBy>(groupByClassProcessorMap_.values());
        Collections.sort(groupByProcessorClassList, cmp);
        groupByClassProcessorMap_ = null;
    }

    protected static class Counter {
        int nbInfos;
        int nbWarnings;
        int nbErrors;
        int nbGroup;

        public void add(Severity v) {
            switch (v) {
                case MESSAGE:
                    nbInfos++;
                    break;
                case WARNING:
                    nbWarnings++;
                    break;
                case ERROR:
                    nbErrors++;
                    break;
            }
        }
    }

    protected static class GroupBy {
        String name;
        Map<String, List<LineInfo>> groups;
        Counter cnt;

        public GroupBy(String name) {
            this.name = name;
            groups = new TreeMap<String, List<LineInfo>>();
            cnt = new Counter();
        }

        public void add(String groupName, LineInfo line) {
            List<LineInfo> lines = groups.get(groupName);
            if (lines == null) {
                lines = new ArrayList<LineInfo>();
                groups.put(groupName, lines);
                cnt.nbGroup++;
            }
            lines.add(line);
            cnt.add(line.severity);
        }
    }

    protected static class LineInfo {
        int line;
        Severity severity;
        String msg;
        String className;
    }

    private static class GroupByComparatorSeverity implements Comparator<GroupBy> {
        public int compare(GroupBy o1, GroupBy o2) {
            int back = compare(o1.cnt.nbErrors, o2.cnt.nbErrors);
            if (back == 0) {
                back = compare(o1.cnt.nbWarnings, o2.cnt.nbWarnings);
            }
            if (back == 0) {
                back = compare(o1.cnt.nbInfos, o2.cnt.nbInfos);
            }
            if (back == 0) {
                back = o1.name.compareToIgnoreCase(o2.name);
            }
            return back;
        }

        public int compare(int i1, int i2) {
            if (i1 > i2) {
                return 1;
            }
            if (i1 < i2) {
                return -1;
            }
            return 0;
        }
    }

}

package net.egork.chelper.parser;

import net.egork.chelper.checkers.TokenChecker;
import net.egork.chelper.task.StreamConfiguration;
import net.egork.chelper.task.Task;
import net.egork.chelper.task.Test;
import net.egork.chelper.task.TestType;
import org.apache.commons.lang.StringEscapeUtils;

import javax.swing.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author egorku@yandex-team.ru
 */
public class AtCoderParser implements Parser {
    public Icon getIcon() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return "AtCoder";
    }

    public void getContests(DescriptionReceiver receiver) {
        throw new UnsupportedOperationException();
    }

    public void parseContest(String id, DescriptionReceiver receiver) {
        throw new UnsupportedOperationException();
    }

    public Task parseTask(Description description) {
        throw new UnsupportedOperationException();
    }

    public TestType defaultTestType() {
        return TestType.SINGLE;
    }

    public Collection<Task> parseTaskFromHTML(String html) {
        StringParser parser = new StringParser(html);
        try {
            parser.advance(true, "a class=\"contest-title\"");
            parser.advance(true, ">");
            String contestName = parser.advance(false, "</a>");
            parser.advance(true, "<span class=\"h2\">");
            String taskName = parser.advance(false, "</span>");
            parser.advance(true, "Memory Limit: ");
            String memoryLimit = parser.advance(false, "</p>");
            memoryLimit = memoryLimit.substring(0, memoryLimit.length() - 1).replace(" ", "");
            StreamConfiguration input = StreamConfiguration.STANDARD;
            StreamConfiguration output = StreamConfiguration.STANDARD;
            List<Test> tests = new ArrayList<Test>();
            while (parser.advanceIfPossible(true, "<pre id=\"pre-sample") != null) {
                parser.advance(true, ">");
                String testInput = StringEscapeUtils.unescapeHtml(parser.advance(false, "</pre>"));
                parser.advance(true, "<pre id=\"pre-sample");
                parser.advance(true, ">");
                String testOutput = StringEscapeUtils.unescapeHtml(parser.advance(false, "</pre>"));
                boolean found = false;
                for (Test test : tests) {
                    if (testInput.equals(test.input) && testOutput.equals(test.output)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    tests.add(new Test(testInput, testOutput, tests.size()));
                }
            }
            String letter = Character.toString(taskName.charAt(0));
            return Collections.singleton(new Task(taskName, defaultTestType(), input, output, tests.toArray(new Test[tests.size()]), null,
                    "-Xmx" + memoryLimit, "Main", "Task" + letter, TokenChecker.class.getCanonicalName(), "",
                    new String[0], null, contestName, true, null, null, false, false));
        } catch (ParseException e) {
            return Collections.emptyList();
        }
    }

}

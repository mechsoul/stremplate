package org.mechsoul.stremplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTemplate {
    private static final char START_CHAR = '{';
    private static final char STOP_CHAR = '}';
    private static final String ANY_EXCEPT_SLASH = "([^/]+)";

    private String template;
    private LinkedList<Part> keywords;

    private Pattern regexPattern;
    private StringBuilder regexBuilder;

    private StringTemplate(final String templatePattern) {
	this.template = templatePattern;
	this.regexBuilder = new StringBuilder();
	this.keywords = new LinkedList<Part>();

	doCompile();
    }

    public static StringTemplate compile(final String templatePattern) {
	return new StringTemplate(templatePattern);
    }

    private void checkState() {
	if (regexPattern != null) {
	    throw new IllegalStateException("StringTemplate already compiled");
	}
    }

    private void doCompile() {
	checkState();
	readTemplate();
	finalizePattern();
    }

    private void readTemplate() {
	char[] charArray = template.toCharArray();

	for (int i = 0; i < charArray.length; i++) {
	    if (charArray[i] == START_CHAR) {
		i = readKeyword(charArray, i);
	    } else if (charArray[i] == STOP_CHAR) {
		unexpected(STOP_CHAR, i);
	    } else {
		i = readSeparator(charArray, i);
	    }
	}
    }

    private int readSeparator(final char[] charArray, final int currentIndex) {
	int end = currentIndex;

	final StringBuilder separatorBuilder = new StringBuilder();

	for (int i = currentIndex; i < charArray.length; i++) {
	    char currentChar = charArray[i];

	    end = i;

	    if (currentChar == START_CHAR) {
		end--;
		break;
	    } else if (currentChar == STOP_CHAR) {
		unexpected(STOP_CHAR, i);
	    } else {
		separatorBuilder.append(currentChar);
	    }
	}

	regexBuilder.append(Pattern.quote(separatorBuilder.toString()));

	return end;
    }

    private void finalizePattern() {
	regexBuilder.insert(0, '^');
	regexBuilder.append('$');

	regexPattern = Pattern.compile(regexBuilder.toString());
    }

    private void unexpected(final char c, final int i) {
	throw new IllegalArgumentException("Unexpected char '" + c + "' at position: " + i);
    }

    private void checkNextEnd(final char[] charArray, final int currentIndex) {
	if (currentIndex + 1 > charArray.length) {
	    throw new IllegalArgumentException("Unexpected end of pattern");
	}
    }

    private int readKeyword(final char[] charArray, final int currentIndex) {
	checkNextEnd(charArray, currentIndex);

	int start = currentIndex + 1;
	int end = currentIndex;

	for (int i = start; i < charArray.length; i++) {
	    char currentChar = charArray[i];
	    end = i;

	    if (currentChar == START_CHAR) {
		unexpected(START_CHAR, i);
	    } else if (currentChar == STOP_CHAR) {
		return handleStopCharInKeywordRead(charArray, start, end);
	    }
	}
	throw new IllegalArgumentException("Bad end of template expected char '}' at the end");
    }

    private int handleStopCharInKeywordRead(final char[] charArray, final int start, final int end) {
	if (end > start) {
	    createKeyword(charArray, start, end);
	    return end;
	}

	throw new IllegalArgumentException("Empty keyword not allowed");
    }

    private void createKeyword(final char[] charArray, final int start, final int end) {
	final String keyContent = new String(Arrays.copyOfRange(charArray, start, end));
	final Part part = new Part(keyContent, start - 1, end + 1);
	if (keywords.contains(part)) {
	    throw new IllegalArgumentException("Pattern contains key '" + keyContent + "' more than once");
	}
	keywords.push(part);

	regexBuilder.append(ANY_EXCEPT_SLASH);
    }

    public StringExpression parse(final String stringToParse) {
	final Matcher matcher = regexPattern.matcher(stringToParse);

	if (!matcher.matches()) {
	    throw new NotMatchingStringException("String '" + stringToParse + "' is not matching template [" + template
		    + "]");
	}

	final StringExpression expression = new StringExpression(stringToParse, this);
	int groupCount = matcher.groupCount();

	assert groupCount == keywords.size();

	for (int i = 0; i < groupCount; i++) {
	    int matcherIdx = i + 1;
	    int keywordIdx = groupCount - i - 1;

	    int groupStart = matcher.start(matcherIdx);
	    int groupEnd = matcher.end(matcherIdx);
	    expression.add(keywords.get(keywordIdx), new Part(matcher.group(matcherIdx), groupStart, groupEnd));
	}

	return expression;
    }

    public List<String> getKeywords() {
	List<String> keys = new ArrayList<String>();

	for (Part p : keywords) {
	    keys.add(p.part);
	}

	Collections.reverse(keys);

	return keys;
    }

    @Override
    public String toString() {
	return template;
    }

    public String getTemplate() {
	return template;
    }

    public String eval(final Map<String, String> map) {
	final PartBind[] reversed = new PartBind[keywords.size()];

	int i = 0;
	for (Part keyPart : keywords) {
	    String match = map.get(keyPart.part);
	    if (match == null) {
		throw new IllegalArgumentException("Keyword '" + keyPart.part + "' is not defined in map.");
	    }
	    reversed[i++] = new PartBind(keyPart, match);
	}

	final StringBuilder result = new StringBuilder(template);

	for (i = 0; i < reversed.length; i++) {
	    PartBind pb = reversed[i];
	    result.replace(pb.keyword.start, pb.keyword.end, pb.match);
	}

	return result.toString();
    }

    static class Part {

	String part;
	int start;
	int end;

	Part(final String part, final int start, final int end) {
	    this.part = part;
	    this.start = start;
	    this.end = end;
	}

	@Override
	public boolean equals(final Object obj) {
	    return obj != null && obj.getClass() == Part.class && part != null && part.equals(((Part) obj).part);
	}
    }

    static class PartBind {
	Part keyword;
	String match;

	PartBind(final Part key, final String match) {
	    this.keyword = key;
	    this.match = match;
	}
    }
}

package org.mechsoul.stremplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class StringTemplateTest {

    @Test(expected = NullPointerException.class)
    public void testCompileNull() {
	StringTemplate.compile(null);
    }

    @Test
    public void testCompileTemplate() {
	final StringTemplate template = StringTemplate.compile("{topic}");
	assertNotNull("template should be compiled", template);
    }

    @Test
    public void testCompileEmptyKeyword() {
	try {
	    StringTemplate.compile("{}");
	} catch (Exception e) {
	    assertTrue(e.getMessage(), e.getMessage().equals("Empty keyword not allowed"));
	    return;
	}
	fail("exception should be raised if pattern has empty keyword syntax");
    }

    @Test
    public void testCompileWith2RightOpeningBrace() {
	try {
	    StringTemplate.compile("{topic{");
	} catch (Exception e) {
	    assertTrue(e.getMessage(), e.getMessage().equals("Unexpected char '{' at position: 6"));
	    return;
	}
	fail("exception should be raised if pattern has invalid syntax");
    }

    @Test
    public void testCompileWith2LeftClosingBrace() {
	try {
	    StringTemplate.compile("}topic}");
	} catch (Exception e) {
	    assertTrue(e.getMessage(), e.getMessage().equals("Unexpected char '}' at position: 0"));
	    return;
	}
	fail("exception should be raised if pattern has invalid syntax");
    }

    @Test
    public void testCompileWithOnly1RightClosingBrace() {
	try {
	    StringTemplate.compile("topic}");
	} catch (Exception e) {
	    assertTrue(e.getMessage(), e.getMessage().equals("Unexpected char '}' at position: 5"));
	    return;
	}
	fail("exception should be raised if pattern has invalid syntax");
    }

    @Test
    public void testCompiledTemplateHasKeywords() {
	final StringTemplate template = StringTemplate.compile("{topic}");
	assertNotNull("template should have some keywords", template.getKeywords());
    }

    @Test
    public void testCompiledTemplateHasExactKeyword() {
	final StringTemplate template = StringTemplate.compile("{topic}");
	assertEquals("template should have keyword 'topic'", "topic", template.getKeywords().iterator().next());
    }

    @Test
    public void testCompiledTemplateKeywordsSize() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}");
	assertEquals("template should have 2 keywords", 2, template.getKeywords().size());
    }

    @Test
    public void testCompiledTemplateKeywordsContent() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}");
	assertEquals("template should have second key 'name'", "name", template.getKeywords().get(1));
    }

    @Test
    public void testTemplateWith2SameKeywords() {
	try {
	    StringTemplate.compile("{topic}/{topic}");
	} catch (Exception e) {
	    assertTrue(e.getMessage(), e.getMessage().contains("more than once"));
	    return;
	}
	fail("exception should be raised if pattern contains same key twice");
    }

    @Test
    public void testTemplateString() {
	final String stringtemplate = "{topic}/{next}";
	final StringTemplate template = StringTemplate.compile(stringtemplate);
	assertEquals("template should return same template string", stringtemplate, template.getTemplate());

    }

    @Test
    public void testParseIsNotNull() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}");
	assertNotNull("parsed pattern should be not null", template.parse("hello-dolly"));
    }

    @Test
    public void testEval() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}");
	Map<String, String> map = new HashMap<String, String>();
	map.put("topic", "tests");
	map.put("name", "ROCKS!");

	assertEquals("template should evaluate", "tests-ROCKS!", template.eval(map));
    }

}

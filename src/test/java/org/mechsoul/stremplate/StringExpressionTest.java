package org.mechsoul.stremplate;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class StringExpressionTest {

    @Test
    public void testParseString() {
	final StringTemplate template = StringTemplate.compile("{topic}");
	final StringExpression expression = template.parse("anything");
	assertEquals("expression should call itself", "anything", expression.toString());
    }
    
    @Test(expected=NullPointerException.class)
    public void testParseNull() {
	StringTemplate.compile("{topic}").parse(null);
    }

    @Test
    public void testCheckIfParseReturnSomeExpression() {
	final StringTemplate template = StringTemplate.compile("{topic}");
	final StringExpression expression = template.parse("anything");
	assertNotNull("expression should be parsed", expression);
    }

    @Test
    public void testParseSimpleTemplate() {
	StringTemplate sTemplate = StringTemplate.compile("{topic}");
	StringExpression expression = sTemplate.parse("anything");
	assertEquals("parsed expression should return right result", "anything", expression.get("topic"));
    }

    @Test
    public void testIfKeySizeMatch() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	StringExpression expr = template.parse("hello-dolly/sheep");
	assertEquals("parsed pattern should contains 3 keys", 3, expr.keySize());
    }

    @Test
    public void testCheckThirdArgument() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	StringExpression expr = template.parse("hello-dolly/sheep");
	assertEquals("parsed pattern should contains third argument", "sheep", expr.get("header"));
    }
    
    @Test
    public void testSource() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	String test = "hello-dolly/sheep";
	StringExpression expr = template.parse(test);
	expr.set("topic", "test");
	assertEquals("parsed pattern should return source string", test, expr.getSource());
    }
    
    @Test
    public void testEvalAfterReparse() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	String test = "hello-dolly/sheep";
	StringExpression expr = template.parse(test);
	expr.set("topic", "test");
	assertTrue("eval expression should not equal to source after change", !test.equals(expr.eval()));
	expr.reparse();
	assertTrue("eval expression should be equal after reparse to source", test.equals(expr.eval()));
    }

    @Test
    public void testEval() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	String test = "hello-dolly/sheep";
	StringExpression expr = template.parse(test);
	assertEquals("parsed pattern should eval string", test, expr.eval());
    }

    @Test
    public void testSetArgument() {
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	StringExpression expr = template.parse("hello-dolly/sheep");
	expr.set("topic", "goodbye");
	assertEquals("parsed pattern should replace topic string", "goodbye-dolly/sheep", expr.eval());
    }

    @Test(expected = NotMatchingStringException.class)
    public void testSlashSeparatedPattern() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}");
	template.parse("/hello/dolly/sheep");
    }

    @Test(expected = NotMatchingStringException.class)
    public void testSlashSeparatedPatternEmptyKey() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	template.parse("/hello/dolly/");
    }

    @Test
    public void testIfMapIsReturned() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	assertNotNull("map should be returned", template.parse("/hello/dolly/sheep").getMap());
    }

    @Test
    public void testIfMapSizeMatch() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	assertEquals("map size should be 3", 3, template.parse("/hello/dolly/sheep").getMap().size());
    }

    @Test
    public void testIfMapKeysMatch() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	Map<String, String> keyMap = template.parse("/hello/dolly/sheep").getMap();
	assertTrue("map should contain 'topic'", keyMap.containsKey("topic"));
	assertTrue("map should contain 'name'", keyMap.containsKey("name"));
	assertTrue("map should contain 'any'", keyMap.containsKey("any"));
    }

    @Test
    public void testIfMapContentMatch() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	Map<String, String> keyMap = template.parse("/hello/dolly/sheep").getMap();
	assertEquals("map should contain value 'topic'", "hello", keyMap.get("topic"));
	assertEquals("map should contain value 'name'", "dolly", keyMap.get("name"));
	assertEquals("map should contain value 'any'", "sheep", keyMap.get("any"));
    }

    @Test
    public void testEvalWithArgument() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	final StringExpression expr = template.parse("/hello/dolly/sheep");
	Map<String, String> keyMap = expr.getMap();
	keyMap.put("topic", "tests");
	keyMap.put("name", "are");
	keyMap.put("any", "good");
	keyMap.put("more", "stuff");
	assertEquals("eval with map should evaluate", "/tests/are/good", expr.eval(keyMap));

    }

    @Test
    public void testEvalWithoutArgument() {
	final StringTemplate template = StringTemplate.compile("/{topic}/{name}/{any}");
	final StringExpression expr = template.parse("/hello/dolly/sheep");
	Map<String, String> keyMap = expr.getMap();
	keyMap.put("topic", "tests");
	keyMap.put("name", "are");
	keyMap.put("any", "good");
	assertEquals("eval without arg map should evaluate same as before map manipulation", "/hello/dolly/sheep",
		expr.eval());

    }
}

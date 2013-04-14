package org.mechsoul.stremplate;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringTemplateMapperTest {
    

    @Test
    public void testCreateMapper() {
	final StringTemplate source = StringTemplate.compile("{topic}");
	final StringTemplate destination = StringTemplate.compile("hello-{topic}");
	assertNotNull("mapper should be created", StringTemplateMapper.bind(source, destination));
    }
    
    @Test
    public void testSimpleMapping() {
	final StringTemplate source = StringTemplate.compile("{topic}");
	final StringTemplate destination = StringTemplate.compile("hello-{topic}");
	final StringTemplateMapper mapper = StringTemplateMapper.bind(source, destination);
	assertEquals("expression should return be remaped", "hello-dolly", mapper.process("dolly"));
    }
    
    @Test(expected=TemplateMappingException.class)
    public void testMappingDestHasMoreKeywords() {
	final StringTemplate source = StringTemplate.compile("{topic}");
	final StringTemplate destination = StringTemplate.compile("hello-{topic}-{name}");
	StringTemplateMapper.bind(source, destination);
    }
    
    @Test(expected=TemplateMappingException.class)
    public void testMappingDestKeywordsAreNotInSource() {
	final StringTemplate source = StringTemplate.compile("{topic}-{word}");
	final StringTemplate destination = StringTemplate.compile("hello-{topic}-{name}");
	StringTemplateMapper.bind(source, destination);
    }
    
    @Test
    public void testSourceHasMoreKeywords() {
	final StringTemplate source = StringTemplate.compile("{topic}-{word}-{name}");
	final StringTemplate destination = StringTemplate.compile("hello-{topic}-{name}");
	assertNotNull("mapper should be created", StringTemplateMapper.bind(source, destination));
    }
    
    @Test
    public void testUrlRemap() {
	final StringTemplate source = StringTemplate.compile("/{category}/{year}");
	final StringTemplate destination = StringTemplate.compile("/news/category/{category}/year/{year}");
	StringTemplateMapper mapper = StringTemplateMapper.bind(source, destination);
	String input = "/rugby/2013";
	assertEquals("mapper should remap url path part", "/news/category/rugby/year/2013", mapper.process(input));
    }
}

[![Build Status](https://travis-ci.org/mkrajc/stremplate.svg?branch=master)](https://travis-ci.org/mkrajc/stremplate)

stremplate
==========

String template utility that was written as helper for URL rewrites. It can parse or evaluate string template against simple model.

Template
--------
Template string is any string and pattern is enclosed in brackets `{`,`}`.
So string `anything{something}template` is template string that can match string `anythingIstemplate` with result model `{something="Is"}`.

Binding
-------
Two string templates can be bound together. One will parse string and provide model and second will consume that model and provide string. Url rewrites can be mapped in simple way.


Examples
--------
Few examples from tests.

### Url path rewrite

Goal of this utility is SEO url rewrites
```
	final StringTemplate source = StringTemplate.compile("/{category}/{year}");
	final StringTemplate destination = StringTemplate.compile("/news/category/{category}/year/{year}");
	StringTemplateMapper mapper = StringTemplateMapper.bind(source, destination);
	String input = "/rugby/2013";
	assertEquals("mapper should remap url path part", "/news/category/rugby/year/2013", mapper.process(input));
```
### Parsing and evaluating template

Simple expression matching.
```
	final StringTemplate template = StringTemplate.compile("{topic}-{name}/{header}");
	StringExpression expr = template.parse("hello-dolly/sheep");
	expr.set("topic", "goodbye");
	assertEquals("parsed pattern should replace topic string", "goodbye-dolly/sheep", expr.eval());
```
With map
```
	final StringTemplate template = StringTemplate.compile("{topic}-{name}");
	Map<String, String> map = new HashMap<String, String>();
	map.put("topic", "tests");
	map.put("name", "ROCKS!");
	assertEquals("template should evaluate", "tests-ROCKS!", template.eval(map));
```

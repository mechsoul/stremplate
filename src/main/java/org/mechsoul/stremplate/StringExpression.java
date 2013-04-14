package org.mechsoul.stremplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.mechsoul.stremplate.StringTemplate.Part;
import org.mechsoul.stremplate.StringTemplate.PartBind;

public class StringExpression {
    private StringTemplate template;
    private String source;
    private Map<String, PartBind> keyPartMap;

    StringExpression(final String string, final StringTemplate template) {
	this.source = string;
	this.template = template;
	keyPartMap = new LinkedHashMap<String, PartBind>();
    }

    public String get(final String key) {
	return keyPartMap.get(key).match;
    }

    void add(final Part key, final Part value) {
	keyPartMap.put(key.part, new PartBind(key, value.part));
    }

    @Override
    public String toString() {
	return source;
    }

    public int keySize() {
	return keyPartMap.size();
    }

    public Set<String> keys() {
	return keyPartMap.keySet();
    }

    public void set(final String key, final String value) {
	final PartBind pb = keyPartMap.get(key);
	if (pb != null) {
	    pb.match = value;
	}
    }

    public String eval() {
	return template.eval(getMap());
    }

    public String eval(final Map<String, String> keyMap) {
	return template.eval(keyMap);
    }

    public Map<String, String> getMap() {
	final Map<String, String> stringMap = new HashMap<String, String>();

	for (PartBind pb : keyPartMap.values()) {
	    stringMap.put(pb.keyword.part, pb.match);
	}
	return stringMap;
    }

    public String getSource() {
	return source;
    }

    public void reparse() {
	this.keyPartMap = template.parse(source).keyPartMap;
    }

}

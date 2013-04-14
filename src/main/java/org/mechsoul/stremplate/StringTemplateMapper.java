package org.mechsoul.stremplate;

import java.util.List;

public class StringTemplateMapper {
    private StringTemplate source, destination;

    private StringTemplateMapper(final StringTemplate source, final StringTemplate destination) {
	this.source = source;
	this.destination = destination;

	final List<String> destKeys = destination.getKeywords();
	final List<String> sourceKeys = source.getKeywords();

	if (destKeys.size() > sourceKeys.size()) {
	    throw new TemplateMappingException("Cannot be mapped. Destination template [" + destination
		    + "] contains more keywords (" + destKeys.size() + ") than source [" + source + "] ("
		    + sourceKeys.size() + ") .");
	}

	for (String key : destKeys) {
	    if (!sourceKeys.contains(key)) {
		throw new TemplateMappingException("Cannot be mapped. Destination template [" + destination
			+ "] contains keyword '" + key + "' that is NOT present in source [" + source + "].");
	    }
	}
    }

    public static StringTemplateMapper bind(final StringTemplate source, final StringTemplate destination) {
	return new StringTemplateMapper(source, destination);
    }

    public String process(String string) {
	return destination.eval(source.parse(string).getMap());
    }

}

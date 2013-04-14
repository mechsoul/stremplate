package org.mechsoul.stremplate;

class Part {

    String part;
    int start;
    int end;

    public Part(String part, int start, int end) {
	this.part = part;
	this.start = start;
	this.end = end;
    }

    @Override
    public boolean equals(Object obj) {
	return obj != null && obj.getClass() == Part.class && part != null && part.equals(((Part) obj).part);
    }
}

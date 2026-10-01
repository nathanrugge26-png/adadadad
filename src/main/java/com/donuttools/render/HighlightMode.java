package com.donuttools.render;

public enum HighlightMode {
    OUTLINE("Outline"),
    FILLED("Filled"),
    OUTLINE_FILL("Outline + Fill"),
    GLOW("Glow"),
    CORNERS("Corners");

    private final String label;
    HighlightMode(String label) { this.label = label; }
    public String label() { return label; }
    public HighlightMode next() {
        HighlightMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}

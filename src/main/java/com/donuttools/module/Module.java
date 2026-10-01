package com.donuttools.module;

public abstract class Module {
    private final String id;
    private final String name;
    private boolean enabled;

    protected Module(String id, String name, boolean enabled) {
        this.id = id;
        this.name = name;
        this.enabled = enabled;
    }

    public String id() { return id; }
    public String name() { return name; }
    public boolean enabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void toggle() { this.enabled = !this.enabled; }
}

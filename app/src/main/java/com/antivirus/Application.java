package com.antivirus;

import android.graphics.drawable.Drawable;

public class Application {
    public boolean box;
    public Drawable icon;
    public int icons;
    public String name;
    public String packet;

    public Application(String _describe, String _packet, boolean _box, Drawable icon) {
        this.name = _describe;
        this.packet = _packet;
        this.box = _box;
        this.icon = icon;
    }

    public Application(String _describe, Drawable icon) {
        this.name = _describe;
        this.icon = icon;
    }

    public Application(String names, int icon) {
        this.name = names;
        this.icons = icon;
    }

    public Application(String _describe, String packagenm, Drawable icon) {
        this.name = _describe;
        this.packet = packagenm;
        this.icon = icon;
    }
}
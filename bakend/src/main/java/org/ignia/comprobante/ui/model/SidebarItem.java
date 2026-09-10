package org.ignia.comprobante.ui.model;

public record SidebarItem(String id, String label, long count, Tone tone) {

    public enum Tone { DEFAULT, WARN }
}

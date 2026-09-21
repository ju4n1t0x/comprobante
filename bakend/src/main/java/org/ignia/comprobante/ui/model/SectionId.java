package org.ignia.comprobante.ui.model;

public enum SectionId {
    VENTAS("Ventas"),
    PRODUCTOS("Productos"),
    CATEGORIAS("Categorías"),
    CLIENTES("Clientes"),
    USUARIOS("Usuarios"),
    REPORTES("Reportes"),
    COTIZAR("Cotizar cubierta"),
    COMPUTOS("Computos");


    private final String label;

    SectionId(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}

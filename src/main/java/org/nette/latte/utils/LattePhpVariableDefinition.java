package org.nette.latte.utils;

import org.nette.latte.psi.elements.LattePhpVariableElement;
import org.jetbrains.annotations.NotNull;

public class LattePhpVariableDefinition {

    private final boolean probablyUndefined;
    private final LattePhpVariableElement element;

    public LattePhpVariableDefinition(boolean probablyUndefined, @NotNull LattePhpVariableElement element) {
        this.probablyUndefined = probablyUndefined;
        this.element = element;
    }

    public boolean isProbablyUndefined() {
        return probablyUndefined;
    }

    public LattePhpVariableElement getElement() {
        return element;
    }
}
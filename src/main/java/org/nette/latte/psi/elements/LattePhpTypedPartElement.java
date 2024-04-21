package org.nette.latte.psi.elements;

import org.nette.latte.php.LattePhpTypeDetector;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpType;
import org.nette.latte.psi.LattePhpVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface LattePhpTypedPartElement extends LattePsiElement {

    @Nullable
    LattePhpType getPhpType();

    @NotNull
    LattePhpVariable getPhpVariable();

    default @NotNull NettePhpType getReturnType() {
        return LattePhpTypeDetector.detectPhpType(this);
    }

}
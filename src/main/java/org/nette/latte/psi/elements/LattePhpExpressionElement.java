package org.nette.latte.psi.elements;

import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpStatement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LattePhpExpressionElement extends LattePsiElement {

    @NotNull
    NettePhpType getReturnType();

    @NotNull
    List<LattePhpStatement> getPhpStatementList();

    int getPhpArrayLevel();

}
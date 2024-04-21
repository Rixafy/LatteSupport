package org.nette.latte.psi.elements;

import com.intellij.psi.StubBasedPsiElement;
import org.nette.latte.indexes.stubs.LattePhpTypeStub;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpTypePart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LattePhpTypeElement extends LattePsiElement, StubBasedPsiElement<LattePhpTypeStub> {

    @NotNull
    List<LattePhpTypePart> getPhpTypePartList();

    @NotNull
    NettePhpType getReturnType();

}
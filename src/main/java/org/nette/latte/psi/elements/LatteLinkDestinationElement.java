package org.nette.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public interface LatteLinkDestinationElement extends LattePsiNamedElement {
    @Override
    default PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return this;
    }

    @NotNull
    String getLinkDestination();
}

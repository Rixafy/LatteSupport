package org.nette.latte.indexes;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiFile;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.elements.LattePsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LatteStubBasedPsiElement<T extends StubElement> extends StubBasedPsiElementBase<T>implements LattePsiElement {
    private LatteFile file = null;

    public LatteStubBasedPsiElement(@NotNull T stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LatteStubBasedPsiElement(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + getNode().getElementType() + ")('" + getNode().getText() + "')";
    }

    @Nullable public LatteFile getLatteFile() {
        if (file == null) {
            PsiFile containingFile = getContainingFile();
            file = containingFile instanceof LatteFile ? (LatteFile) containingFile : null;
        }
        return file;
    }

}
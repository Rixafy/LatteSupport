package org.nette.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.nette.latte.psi.LattePhpNamespaceReference;
import org.nette.latte.php.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.PhpNamespace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public class LatteNamespaceReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String namespaceName;
    private final Project project;

    public LatteNamespaceReference(@NotNull LattePhpNamespaceReference element, TextRange textRange) {
        super(element, textRange);
        namespaceName = element.getNamespaceName();
        project = element.getProject();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        List<ResolveResult> results = new ArrayList<>();
        for (PhpNamespace phpNamespace : LattePhpUtil.getNamespacesByName(project, namespaceName)) {
            results.add(new PsiElementResolveResult(phpNamespace));
        }

        //for (LattePhpNamespaceReference namespaceReference : LatteIndexUtil.findNamespacesByFqn(project, namespaceName)) {
        //    results.add(new PsiElementResolveResult(namespaceReference));
        //}

        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpNamespaceReference) {
            ((LattePhpNamespaceReference) getElement()).setName(newName);
        }
        return getElement();
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpNamespaceReference) {
            return namespaceName.equals(((LattePhpNamespaceReference) element).getNamespaceName());
        }

        if (!(element instanceof PhpNamespace)) {
            return false;
        }
        String namespace = ((PhpNamespace) element).getParentNamespaceName() + ((PhpNamespace) element).getName();
        return namespace.equals(namespaceName);
    }

}
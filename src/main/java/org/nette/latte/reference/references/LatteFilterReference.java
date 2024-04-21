package org.nette.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.nette.latte.psi.LatteMacroModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LatteFilterReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String modifierName;
    private final Project project;

    public LatteFilterReference(@NotNull LatteMacroModifier element, TextRange textRange) {
        super(element, textRange);
        modifierName = element.getModifierName();
        project = element.getProject();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();
        //for (XmlAttributeValue attributeValue : LatteFileConfiguration.getAllMatchedXmlAttributeValues(project, "filter", modifierName)) {
        //    results.add(new PsiElementResolveResult(attributeValue));
        //}

        //final Collection<LatteMacroModifier> modifiers = LatteIndexUtil.findFiltersByName(project, modifierName);
        //for (LatteMacroModifier modifier : modifiers) {
        //    results.add(new PsiElementResolveResult(modifier));
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
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LatteMacroModifier) {
            return ((LatteMacroModifier) element).getModifierName().equals(modifierName);
        }
        return super.isReferenceTo(element);
    }
}
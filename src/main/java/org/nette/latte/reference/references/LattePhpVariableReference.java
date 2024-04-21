package org.nette.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.nette.latte.php.LattePhpVariableUtil;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.*;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.utils.LattePhpCachedVariable;
import org.nette.latte.utils.LatteUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.*;
import org.nette.latte.psi.LattePhpVariable;

import java.util.*;

public class LattePhpVariableReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {

    private final String variableName;
    private final Project project;

    public LattePhpVariableReference(@NotNull LattePhpVariable element, TextRange textRange) {
        super(element, textRange);
        variableName = element.getVariableName();
        project = element.getProject();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (!(getElement() instanceof LattePhpVariable || getElement().getContainingFile().getVirtualFile() == null)) {
            return new ResolveResult[0];
        }
        LattePhpVariable variable = (LattePhpVariable) getElement();

        List<ResolveResult> results = new ArrayList<>();
        NettePhpType fields = LatteUtil.findFirstLatteTemplateType(getElement().getContainingFile());
        String name = ((BaseLattePhpElement) getElement()).getPhpElementName();
        if (fields != null) {
            for (PhpClass phpClass : fields.getPhpClasses(project)) {
                for (Field field : phpClass.getFields()) {
                    if (!field.isConstant() && field.getName().equals(name)) {
                        results.add(new PsiElementResolveResult(field));
                    }
                }
            }
        }

        final List<LattePhpCachedVariable> variables = variable.getVariableDefinition();
        for (LattePhpCachedVariable variableDefinition : variables) {
            results.add(new PsiElementResolveResult(variableDefinition.getElement()));
        }
        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
    }

    @Override
    public boolean isSoft() {
        return true;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return LattePhpVariableUtil.normalizePhpVariable(getElement().getText());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpVariable) {
            ((LattePhpVariable) getElement()).setName(newName);
        }
        return getElement();
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable && ((LattePhpVariable) element).isDefinition()) {
            return ((LattePhpVariable) element).getVariableName().equals(variableName);
        }
        return super.isReferenceTo(element);
    }
}
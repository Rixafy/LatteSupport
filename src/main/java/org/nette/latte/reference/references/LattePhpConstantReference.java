package org.nette.latte.reference.references;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.nette.latte.indexes.LatteIndexUtil;
import org.nette.latte.psi.LattePhpConstant;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.php.LattePhpUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LattePhpConstantReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String key;
    private final Project project;
    private final Collection<PhpClass> phpClasses;

    public LattePhpConstantReference(@NotNull LattePhpConstant element, TextRange textRange) {
        super(element, textRange);
        key = element.getConstantName();
        project = element.getProject();
        phpClasses = element.getPrevReturnType().getPhpClasses(project);
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        if (phpClasses.size() == 0) {
            return new ResolveResult[0];
        }

        final Collection<LattePhpConstant> methods = LatteIndexUtil.findConstantsByName(project, key);
        List<ResolveResult> results = new ArrayList<>();
        for (BaseLattePhpElement method : methods) {
            if (method.getPrevReturnType().hasClass(phpClasses)) {
                results.add(new PsiElementResolveResult(method));
            }
        }

        List<Field> fields = LattePhpUtil.getFieldsForPhpElement((BaseLattePhpElement) getElement(), project);
        String name = ((BaseLattePhpElement) getElement()).getPhpElementName();
        for (Field field : fields) {
            if (field.getName().equals(name)) {
                results.add(new PsiElementResolveResult(field));
            }
        }

        return results.toArray(new ResolveResult[0]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        List<Field> fields = LattePhpUtil.getFieldsForPhpElement((BaseLattePhpElement) getElement(), project);
        return fields.size() > 0 ? fields.get(0) : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element) {
        if (element instanceof LattePhpConstant) {
            Collection<PhpClass> originalClasses = ((LattePhpConstant) element).getPrevReturnType().getPhpClasses(project);
            if (originalClasses.size() > 0) {
                for (PhpClass originalClass : originalClasses) {
                    if (LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), project, ((LattePhpConstant) element).getConstantName())) {
                        return true;
                    }
                }
            }
        }

        if (!(element instanceof Field)) {
            return false;
        }
        PhpClass originalClass = ((Field) element).getContainingClass();
        if (originalClass == null) {
            return false;
        }
        return LattePhpUtil.isReferenceTo(originalClass, multiResolve(false), project, ((Field) element).getName());
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) {
        if (getElement() instanceof LattePhpConstant) {
            ((LattePhpConstant) getElement()).setName(newName);
        }
        return getElement();
    }

}
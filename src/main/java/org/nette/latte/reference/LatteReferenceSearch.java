package org.nette.latte.reference;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpClassUsage;
import org.nette.latte.psi.LattePhpStaticVariable;
import org.nette.latte.psi.LattePhpVariable;
import org.nette.latte.reference.references.LattePhpClassReference;
import org.nette.latte.reference.references.LattePhpStaticVariableReference;
import org.nette.latte.reference.references.LattePhpVariableReference;
import org.nette.latte.php.LattePhpUtil;
import org.nette.latte.utils.LatteUtil;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class LatteReferenceSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters> {

    @Override
    public void processQuery(ReferencesSearch.SearchParameters searchParameters, @NotNull Processor<? super PsiReference> processor) {
        if (searchParameters.getElementToSearch() instanceof Field) {
            processField((Field) searchParameters.getElementToSearch(), searchParameters.getScopeDeterminedByUser(), processor);

        } else if (searchParameters.getElementToSearch() instanceof PhpClass) {
            processClass((PhpClass) searchParameters.getElementToSearch(), searchParameters.getScopeDeterminedByUser(), processor);
        }
    }

    private void processClass(@NotNull PhpClass phpClass, @NotNull SearchScope searchScope, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            String fieldName = phpClass.getFQN();

            String searchString = fieldName.startsWith("\\") ? fieldName.substring(1) : fieldName;
            if (searchString.length() == 0) {
                return;
            }

            PsiSearchHelper.getInstance(phpClass.getProject())
                    .processElementsWithWord((psiElement, i) -> {
                        PsiElement currentClass = psiElement.getParent();
                        if (currentClass instanceof LattePhpClassUsage) {
                            String value = ((LattePhpClassUsage) currentClass).getClassName();
                            processor.process(new LattePhpClassReference((LattePhpClassUsage) currentClass, new TextRange(0, value.length())));
                        }
                        return true;
                    }, searchScope, searchString, UsageSearchContext.IN_CODE, true);
        });
    }

    private void processField(@NotNull Field field, @NotNull SearchScope searchScope, @NotNull Processor<? super PsiReference> processor) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (field.isConstant()) {
                return;
            }
            String fieldName = field.getName();

            PsiSearchHelper.getInstance(field.getProject())
                    .processElementsWithWord((psiElement, i) -> {
                        PsiElement currentMethod = psiElement.getParent();
                        if (currentMethod instanceof LattePhpStaticVariable) {
                            String value = ((LattePhpStaticVariable) currentMethod).getVariableName();
                            processor.process(new LattePhpStaticVariableReference((LattePhpStaticVariable) currentMethod, new TextRange(0, value.length() + 1)));

                        } else if (currentMethod instanceof LattePhpVariable && field.getContainingClass() != null) {
                            NettePhpType type = LatteUtil.findFirstLatteTemplateType(currentMethod.getContainingFile());
                            if (type == null) {
                                return true;
                            }

                            Collection<PhpClass> classes = type.getPhpClasses(psiElement.getProject());
                            for (PhpClass phpClass : classes) {
                                if (LattePhpUtil.isReferenceFor(field.getContainingClass(), phpClass)) {
                                    String value = ((LattePhpVariable) currentMethod).getVariableName();
                                    processor.process(new LattePhpVariableReference((LattePhpVariable) currentMethod, new TextRange(0, value.length() + 1)));
                                }
                            }
                        }
                        return true;
                    }, searchScope, "$" + fieldName, UsageSearchContext.IN_CODE, true);
            // ProjectScope.getProjectScope(field.getProject())
        });
    }
}
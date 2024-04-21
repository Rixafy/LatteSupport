package org.nette.latte.completion.providers;

import com.intellij.codeInsight.completion.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.nette.latte.completion.handlers.PhpClassInsertHandler;
import org.nette.latte.psi.LattePhpContent;
import org.nette.latte.php.LattePhpUtil;
import com.jetbrains.php.completion.PhpLookupElement;
import com.jetbrains.php.lang.psi.elements.PhpNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Complete class names
 */
public class LattePhpClassCompletionProvider extends BaseLatteCompletionProvider {

	public LattePhpClassCompletionProvider() {
		super();
	}

	@Override
	protected void addCompletions(@NotNull CompletionParameters params, @NotNull ProcessingContext ctx, @NotNull CompletionResultSet results) {
		PsiElement curr = params.getPosition().getOriginalElement();
		if (PsiTreeUtil.getParentOfType(curr, LattePhpContent.class) == null) {
			return;
		}

		String prefix = results.getPrefixMatcher().getPrefix();
		String namespaceName = getNamespaceName(curr);
		if (namespaceName.length() > 0) {
			namespaceName = namespaceName + "\\" + prefix;
		}

		Project project = params.getPosition().getProject();
		Collection<String> classNames = LattePhpUtil.getAllExistingClassAndInterfacesNames(project, results.getPrefixMatcher().cloneWithPrefix(namespaceName));
		Collection<PhpNamedElement> variants = LattePhpUtil.getAllClassNamesAndInterfaces(project, classNames);

		// Add variants
		for (PhpNamedElement item : variants) {
			PhpLookupElement lookupItem = getPhpLookupElement(item, null);
			lookupItem.handler = PhpClassInsertHandler.getInstance();
			results.addElement(lookupItem);
		}
	}

}
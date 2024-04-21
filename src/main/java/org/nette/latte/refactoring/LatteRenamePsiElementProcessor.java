package org.nette.latte.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import org.nette.latte.psi.*;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.Method;
import org.jetbrains.annotations.NotNull;
import org.nette.latte.psi.*;

class LatteRenamePsiElementProcessor extends RenamePsiElementProcessor {

	@Override
	public boolean canProcessElement(@NotNull PsiElement psiElement) {
		return psiElement instanceof LattePhpMethod
				|| psiElement instanceof LattePhpClassUsage
				// || psiElement instanceof LattePhpNamespaceReference todo: temporarily disabled (weird PHPStorm behavior for PhpIndex.getNamespaceByName)
				|| psiElement instanceof LattePhpConstant
				|| psiElement instanceof LattePhpStaticVariable
				|| psiElement instanceof LattePhpProperty
				|| psiElement instanceof Field
				|| psiElement instanceof Method;
	}
}

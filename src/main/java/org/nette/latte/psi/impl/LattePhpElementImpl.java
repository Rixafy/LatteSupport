package org.nette.latte.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.nette.latte.psi.elements.BaseLattePhpElement;
import org.nette.latte.psi.elements.LattePhpStatementPartElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class LattePhpElementImpl extends LatteReferencedElementImpl implements BaseLattePhpElement {

	public LattePhpElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public String getName() {
		return getPhpElementName();
	}

	@Override
	public @Nullable PsiElement getTextElement() {
		return getNameIdentifier();
	}

	public int getPhpArrayLevel() {
		return this.getPhpArrayUsageList().size();
	}

	@Override
	public @Nullable LattePhpStatementPartElement getPhpStatementPart() {
		PsiElement parent = this.getParent();
		return parent instanceof LattePhpStatementPartElement ? (LattePhpStatementPartElement) parent : null;
	}
}
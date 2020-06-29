package com.jantvrdik.intellij.latte.psi.elements;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.jantvrdik.intellij.latte.psi.LattePhpArrayUsage;
import com.jantvrdik.intellij.latte.utils.LattePhpType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface BaseLattePhpElement extends PsiNameIdentifierOwner {

	@NotNull
	List<LattePhpArrayUsage> getPhpArrayUsageList();

	public abstract LattePhpType getPhpType();

	public abstract LattePhpType getReturnType();

	public abstract String getPhpElementName();

	public abstract int getPhpArrayLevel();

	@Nullable
	public abstract PsiElement getTextElement();

	@Nullable
	public LattePhpStatementPartElement getPhpStatementPart();

}
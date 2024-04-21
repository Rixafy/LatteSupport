package org.nette.latte.psi.elements;

import com.intellij.psi.PsiElement;
import org.nette.latte.psi.LattePhpContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LatteMacroContentElement extends LattePsiElement {

	@Nullable LattePhpContent getFirstPhpContent();

	@NotNull
	List<LattePhpContent> getPhpContentList();

	@Nullable PsiElement getMacroNameElement();

}
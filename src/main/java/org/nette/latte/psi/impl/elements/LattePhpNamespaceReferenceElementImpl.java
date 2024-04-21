package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.nette.latte.indexes.stubs.LattePhpNamespaceStub;
import org.nette.latte.php.LattePhpUtil;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LattePhpNamespaceReference;
import org.nette.latte.psi.elements.LattePhpNamespaceReferenceElement;
import org.nette.latte.psi.impl.LatteStubPhpElementImpl;
import com.jetbrains.php.PhpIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LattePhpNamespaceReferenceElementImpl extends LatteStubPhpElementImpl<LattePhpNamespaceStub> implements LattePhpNamespaceReferenceElement {

	public LattePhpNamespaceReferenceElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpNamespaceReferenceElementImpl(final LattePhpNamespaceStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public String getPhpElementName()
	{
		return getNamespaceName();
	}

	@Override
	public NettePhpType getReturnType() {
		return NettePhpType.create(getNamespaceName());
	}

	@Override
	public @Nullable Icon getIcon(int flags) {
		return PhpIcons.NAMESPACE;
	}

	@Override
	public String getNamespaceName() {
		final LattePhpNamespaceStub stub = getStub();
		if (stub != null) {
			return stub.getNamespaceName();
		}

		StringBuilder out = new StringBuilder();
		for (PsiElement element : getParent().getChildren()) {
			if (element instanceof LattePhpNamespaceReference) {
				out.append("\\").append(element.getText());
			}

			if (element == this) {
				break;
			}
		}
		return LattePhpUtil.normalizeClassName(out.toString());
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		return this;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		return this;
	}

	@Override
	public String getName() {
		return getNamespaceName();
	}
}
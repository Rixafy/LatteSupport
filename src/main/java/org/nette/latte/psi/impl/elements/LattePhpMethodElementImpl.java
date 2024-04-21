package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.nette.latte.indexes.stubs.LattePhpMethodStub;
import org.nette.latte.psi.LatteElementFactory;
import org.nette.latte.psi.elements.LattePhpMethodElement;
import org.nette.latte.psi.impl.LatteStubPhpElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;

public abstract class LattePhpMethodElementImpl extends LatteStubPhpElementImpl<LattePhpMethodStub> implements LattePhpMethodElement {

	private @Nullable String name = null;
	private @Nullable String methodName = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpMethodElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpMethodElementImpl(final @NotNull LattePhpMethodStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.methodName = null;
		this.identifier = null;
	}

	@Override
	public String getPhpElementName()
	{
		return getMethodName();
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_PHP_IDENTIFIER);
		}
		return identifier;
	}

	@Override
	public String getMethodName() {
		if (methodName == null) {
			final LattePhpMethodStub stub = getStub();
			if (stub != null) {
				methodName = stub.getMethodName();
				return methodName;
			}

			PsiElement found = getTextElement();
			if (found == null) {
				found = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_PHP_NAMESPACE_REFERENCE);
			}
			methodName = found != null ? found.getText() : null;
		}
		return methodName;
	}

	@Override
	public boolean isStatic() {
		PsiElement prev = PsiTreeUtil.skipWhitespacesBackward(this);
		return prev != null && prev.getNode().getElementType() == LatteTypes.T_PHP_DOUBLE_COLON;
	}

	@Override
	public boolean isFunction() {
		PsiElement prev = PsiTreeUtil.skipWhitespacesAndCommentsBackward(this);
		return prev == null || (prev.getNode().getElementType() != LatteTypes.T_PHP_DOUBLE_COLON && prev.getNode().getElementType() != LatteTypes.T_PHP_OBJECT_OPERATOR);
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement method = LatteElementFactory.createMethod(getProject(), name);
		if (method == null) {
			return this;
		}
		return LatteElementFactory.replaceChildNode(this, method, keyNode);
	}

	@Override
	public String getName() {
		if (name == null) {
			PsiElement found = getNameIdentifier();
			name = found != null ? found.getText() : null;
		}
		return name;
	}
}
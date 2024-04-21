package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.nette.latte.indexes.stubs.LattePhpConstantStub;
import org.nette.latte.psi.LatteElementFactory;
import org.nette.latte.psi.elements.LattePhpConstantElement;
import org.nette.latte.psi.impl.LatteStubPhpElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;

public abstract class LattePhpConstantElementImpl extends LatteStubPhpElementImpl<LattePhpConstantStub> implements LattePhpConstantElement {

	private @Nullable String name = null;
	private @Nullable String constantName = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpConstantElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpConstantElementImpl(final LattePhpConstantStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.constantName = null;
		this.identifier = null;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_PHP_IDENTIFIER);
		}
		return identifier;
	}

	@Override
	public String getConstantName() {
		if (constantName == null) {
			final LattePhpConstantStub stub = getStub();
			if (stub != null) {
				constantName = stub.getConstantName();
				return constantName;
			}

			PsiElement found = getTextElement();
			constantName = found != null ? found.getText() : null;
		}
		return constantName;
	}

	@Override
	public String getPhpElementName()
	{
		return getConstantName();
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createConstant(getProject(), name);
		if (property == null) {
			return this;
		}
		return LatteElementFactory.replaceChildNode(this, property, keyNode);
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
package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import org.nette.latte.indexes.stubs.LattePhpPropertyStub;
import org.nette.latte.psi.LatteElementFactory;
import org.nette.latte.psi.elements.LattePhpPropertyElement;
import org.nette.latte.psi.impl.LatteStubPhpElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteTypes;

public abstract class LattePhpPropertyElementImpl extends LatteStubPhpElementImpl<LattePhpPropertyStub> implements LattePhpPropertyElement {

	private @Nullable String name = null;
	private @Nullable String propertyName = null;
	private @Nullable PsiElement identifier = null;

	public LattePhpPropertyElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	public LattePhpPropertyElementImpl(final LattePhpPropertyStub stub, final IStubElementType nodeType) {
		super(stub, nodeType);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		this.name = null;
		this.propertyName = null;
		this.identifier = null;
	}

	@Override
	public String getPhpElementName()
	{
		return getPropertyName();
	}

	@Override
	public String getPropertyName() {
		if (propertyName == null) {
			final LattePhpPropertyStub stub = getStub();
			if (stub != null) {
				propertyName = stub.getPropertyName();
				return propertyName;
			}

			PsiElement found = getTextElement();
			propertyName = found != null ? found.getText() : null;
		}
		return propertyName;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_PHP_IDENTIFIER);
		}
		return identifier;
	}

	@Override
	public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
		ASTNode keyNode = getFirstChild().getNode();
		PsiElement property = LatteElementFactory.createProperty(getProject(), name);
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
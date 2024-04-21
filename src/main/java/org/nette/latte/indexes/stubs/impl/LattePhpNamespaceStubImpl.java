package org.nette.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.nette.latte.indexes.stubs.LattePhpNamespaceStub;
import org.nette.latte.indexes.stubs.types.LattePhpNamespaceStubType;
import org.nette.latte.psi.LattePhpNamespaceReference;
import org.nette.latte.psi.LatteTypes;

public class LattePhpNamespaceStubImpl extends StubBase<LattePhpNamespaceReference> implements LattePhpNamespaceStub {
    private final String namespaceName;

    public LattePhpNamespaceStubImpl(final StubElement parent, final String constantName) {
        super(parent, (LattePhpNamespaceStubType) LatteTypes.PHP_NAMESPACE_REFERENCE);
        this.namespaceName = constantName;
    }

    @Override
    public String getNamespaceName() {
        return namespaceName;
    }
}

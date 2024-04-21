package org.nette.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.nette.latte.indexes.stubs.LattePhpPropertyStub;
import org.nette.latte.indexes.stubs.types.LattePhpPropertyStubType;
import org.nette.latte.psi.LattePhpProperty;
import org.nette.latte.psi.LatteTypes;

public class LattePhpPropertyStubImpl extends StubBase<LattePhpProperty> implements LattePhpPropertyStub {
    private final String propertyName;

    public LattePhpPropertyStubImpl(final StubElement parent, final String propertyName) {
        super(parent, (LattePhpPropertyStubType) LatteTypes.PHP_PROPERTY);
        this.propertyName = propertyName;
    }

    @Override
    public String getPropertyName() {
        return propertyName;
    }
}

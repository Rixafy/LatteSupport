package org.nette.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.nette.latte.indexes.stubs.LattePhpTypeStub;
import org.nette.latte.indexes.stubs.types.LattePhpTypeStubType;
import org.nette.latte.psi.LattePhpType;
import org.nette.latte.psi.LatteTypes;

public class LattePhpTypeStubImpl extends StubBase<LattePhpType> implements LattePhpTypeStub {
    private final String phpType;

    public LattePhpTypeStubImpl(final StubElement parent, final String phpType) {
        super(parent, (LattePhpTypeStubType) LatteTypes.PHP_TYPE);
        this.phpType = phpType;
    }

    @Override
    public String getPhpType() {
        return phpType;
    }
}

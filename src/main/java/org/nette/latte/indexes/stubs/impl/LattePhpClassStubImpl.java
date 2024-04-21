package org.nette.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.nette.latte.indexes.stubs.LattePhpClassStub;
import org.nette.latte.indexes.stubs.types.LattePhpClassStubType;
import org.nette.latte.psi.LattePhpClassReference;
import org.nette.latte.psi.LatteTypes;

public class LattePhpClassStubImpl extends StubBase<LattePhpClassReference> implements LattePhpClassStub {
    private final String className;

    public LattePhpClassStubImpl(final StubElement parent, final String className) {
        super(parent, (LattePhpClassStubType) LatteTypes.PHP_CLASS_REFERENCE);
        this.className = className;
    }

    @Override
    public String getClassName() {
        return className;
    }
}

package org.nette.latte.indexes.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.nette.latte.indexes.stubs.LatteFilterStub;
import org.nette.latte.indexes.stubs.types.LatteFilterStubType;
import org.nette.latte.psi.LatteMacroModifier;
import org.nette.latte.psi.LatteTypes;

public class LatteFilterStubImpl extends StubBase<LatteMacroModifier> implements LatteFilterStub {
    private final String filterName;

    public LatteFilterStubImpl(final StubElement parent, final String filterName) {
        super(parent, (LatteFilterStubType) LatteTypes.MACRO_MODIFIER);
        this.filterName = filterName;
    }

    @Override
    public String getModifierName() {
        return filterName;
    }
}

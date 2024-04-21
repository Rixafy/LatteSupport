package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LatteMacroModifier;

public interface LatteFilterStub extends StubElement<LatteMacroModifier> {
    String getModifierName();
}
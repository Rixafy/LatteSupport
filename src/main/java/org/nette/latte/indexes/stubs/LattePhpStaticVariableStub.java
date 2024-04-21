package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpStaticVariable;

public interface LattePhpStaticVariableStub extends StubElement<LattePhpStaticVariable> {
    String getVariableName();
}
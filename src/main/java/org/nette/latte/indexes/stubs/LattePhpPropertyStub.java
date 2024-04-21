package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpProperty;

public interface LattePhpPropertyStub extends StubElement<LattePhpProperty> {
    String getPropertyName();
}
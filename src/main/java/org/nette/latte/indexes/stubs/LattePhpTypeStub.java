package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpType;

public interface LattePhpTypeStub extends StubElement<LattePhpType> {
    String getPhpType();
}
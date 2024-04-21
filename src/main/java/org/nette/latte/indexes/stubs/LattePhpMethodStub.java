package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpMethod;

public interface LattePhpMethodStub extends StubElement<LattePhpMethod> {
    String getMethodName();
}
package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpConstant;

public interface LattePhpConstantStub extends StubElement<LattePhpConstant> {
    String getConstantName();
}
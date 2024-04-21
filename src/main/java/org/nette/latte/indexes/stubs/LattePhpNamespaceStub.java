package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpNamespaceReference;

public interface LattePhpNamespaceStub extends StubElement<LattePhpNamespaceReference> {
    String getNamespaceName();
}
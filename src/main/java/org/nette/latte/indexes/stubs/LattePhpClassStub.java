package org.nette.latte.indexes.stubs;

import com.intellij.psi.stubs.StubElement;
import org.nette.latte.psi.LattePhpClassReference;

public interface LattePhpClassStub extends StubElement<LattePhpClassReference> {
    String getClassName();
}
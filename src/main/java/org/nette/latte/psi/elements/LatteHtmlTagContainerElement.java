package org.nette.latte.psi.elements;

import org.nette.latte.psi.LatteHtmlOpenTag;

public interface LatteHtmlTagContainerElement extends LattePsiElement {

    LatteHtmlOpenTag getHtmlOpenTag();

}
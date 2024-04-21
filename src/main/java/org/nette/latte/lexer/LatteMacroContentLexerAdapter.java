package org.nette.latte.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;
import org.nette.latte.psi.LatteTypes;

public class LatteMacroContentLexerAdapter extends MergingLexerAdapter {

	public LatteMacroContentLexerAdapter() {
		super(
				new FlexAdapter(new LatteMacroContentLexer(null)),
				TokenSet.create(LatteTypes.T_PHP_CONTENT, LatteTypes.T_MACRO_ARGS)
		);
	}
}

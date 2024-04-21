package org.nette.latte.lexer;

import com.intellij.lexer.LayeredLexer;
import org.nette.latte.psi.LatteTypes;

/**
 * Main Latte lexer which combines "top lexer" and "macro lexer".
 *
 * LatteMacroLexerAdapter is used to further process token T_MACRO_CLASSIC.
 */
public class LatteLexer extends LayeredLexer {
	public LatteLexer() {
		super(new LatteTopLexerAdapter());
		LayeredLexer macroLexer = new LayeredLexer(new LatteMacroLexerAdapter());
		macroLexer.registerLayer(createContentLexerAdapter(), LatteTypes.T_MACRO_CONTENT);

		registerLayer(macroLexer, LatteTypes.T_MACRO_CLASSIC);
		registerLayer(createContentLexerAdapter(), LatteTypes.T_MACRO_CONTENT);
	}

	private LayeredLexer createContentLexerAdapter() {
		LayeredLexer contentMacroLexer = new LayeredLexer(new LatteMacroContentLexerAdapter());
		contentMacroLexer.registerLayer(new LattePhpLexerAdapter(), LatteTypes.T_PHP_CONTENT);
		return contentMacroLexer;
	}
}

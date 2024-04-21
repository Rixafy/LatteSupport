package org.nette.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LookAheadLexer;
import com.intellij.psi.tree.IElementType;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.utils.LatteHtmlUtil;

public class LatteHtmlHighlightingLexer extends LookAheadLexer {
	public LatteHtmlHighlightingLexer(Lexer baseLexer) {
		super(baseLexer, 1);
	}

	@Override
	protected void lookAhead(Lexer baseLexer) {
		IElementType currentToken = baseLexer.getTokenType();

		if (currentToken != LatteTypes.T_TEXT && LatteHtmlUtil.HTML_TOKENS.contains(currentToken)) {
			advanceLexer(baseLexer);
			replaceCachedType(0, LatteTypes.T_TEXT);

		} else {
			super.lookAhead(baseLexer);
		}
	}
}

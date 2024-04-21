package org.nette.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static org.nette.latte.Assert.assertTokens;
import static org.nette.latte.psi.LatteTypes.*;

public class LatteLexerTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testBasic() throws Exception {
		Lexer lexer = new LatteLexer();

		lexer.start("<e>{if XYZ}B{/if}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_HTML_OPEN_TAG_OPEN, "<"),
			Pair.create(T_TEXT, "e"),
			Pair.create(T_HTML_TAG_CLOSE, ">"),
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "if"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_IDENTIFIER, "XYZ"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
			Pair.create(T_TEXT, "B"),
			Pair.create(T_MACRO_CLOSE_TAG_OPEN, "{/"),
			Pair.create(T_MACRO_NAME, "if"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{if XYZ}B{/if}<e>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "if"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_IDENTIFIER, "XYZ"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
			Pair.create(T_TEXT, "B"),
			Pair.create(T_MACRO_CLOSE_TAG_OPEN, "{/"),
			Pair.create(T_MACRO_NAME, "if"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
			Pair.create(T_HTML_OPEN_TAG_OPEN, "<"),
			Pair.create(T_TEXT, "e"),
			Pair.create(T_HTML_TAG_CLOSE, ">"),
		});
	}
}

package org.nette.latte.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.util.Pair;
import org.junit.Test;

import static org.nette.latte.Assert.assertTokens;
import static org.nette.latte.psi.LatteTypes.*;

public class LattePhpLexerTest {
	@Test
	@SuppressWarnings("unchecked")
	public void testBasic() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{if $var}B{/if}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "if"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
			Pair.create(T_TEXT, "B"),
			Pair.create(T_MACRO_CLOSE_TAG_OPEN, "{/"),
			Pair.create(T_MACRO_NAME, "if"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testVarType() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{varType \\Foo\\Bar|null $_var1}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "varType"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_NULL, "null"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$_var1"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{varType Permissions\\Permission[]|string|null $_var1}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "varType"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Permissions"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_IDENTIFIER, "Permission"),
			Pair.create(T_PHP_LEFT_BRACKET, "["),
			Pair.create(T_PHP_RIGHT_BRACKET, "]"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_TYPE, "string"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_NULL, "null"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$_var1"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{varType Foo\\Bar\\_TestClass1|Foo\\Bar|string|int $_var1}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "varType"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_IDENTIFIER, "_TestClass1"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_IDENTIFIER, "Bar"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_TYPE, "string"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_TYPE, "int"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$_var1"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{varType Foo\\Bar\\_TestClass1|\\Foo\\Bar|string|int $_var1}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "varType"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_IDENTIFIER, "_TestClass1"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_TYPE, "string"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_PHP_TYPE, "int"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$_var1"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{varType string|$int $_var1}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "varType"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_TYPE, "string"),
			Pair.create(T_PHP_OR_INCLUSIVE, "|"),
			Pair.create(T_MACRO_ARGS_VAR, "$int"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$_var1"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSingleQuotedString() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{= 'te$var'}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "te$var"),
			Pair.create(T_PHP_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{= 't e'}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_SHORTNAME, "="),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "t e"),
			Pair.create(T_PHP_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDoubleQuotedString() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{include \"some\"}");
		assertTokens(lexer, new Pair[] {
				Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
				Pair.create(T_MACRO_NAME, "include"),
				Pair.create(T_WHITESPACE, " "),
				Pair.create(T_PHP_DOUBLE_QUOTE_LEFT, "\""),
				Pair.create(T_MACRO_ARGS_STRING, "some"),
				Pair.create(T_PHP_DOUBLE_QUOTE_RIGHT, "\""),
				Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{include \"some$var text\"}");
		assertTokens(lexer, new Pair[] {
				Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
				Pair.create(T_MACRO_NAME, "include"),
				Pair.create(T_WHITESPACE, " "),
				Pair.create(T_PHP_DOUBLE_QUOTE_LEFT, "\""),
				Pair.create(T_MACRO_ARGS_STRING, "some"),
				Pair.create(T_MACRO_ARGS_VAR, "$var"),
				Pair.create(T_MACRO_ARGS_STRING, " text"),
				Pair.create(T_PHP_DOUBLE_QUOTE_RIGHT, "\""),
				Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testVarDefinition() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{var $foo = 123}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "var"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$foo"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_DEFINITION_OPERATOR, "="),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_NUMBER, "123"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testTranslationTag() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{_|test}");
		assertTokens(lexer, new Pair[] {
				Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
				Pair.create(T_MACRO_SHORTNAME, "_"),
				Pair.create(T_PHP_MACRO_SEPARATOR, "|"),
				Pair.create(T_MACRO_FILTERS, "test"),
				Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testProperty() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{$object->foo}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_OBJECT_OPERATOR, "->"),
			Pair.create(T_PHP_IDENTIFIER, "foo"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{$object->foo->bar}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_OBJECT_OPERATOR, "->"),
			Pair.create(T_PHP_IDENTIFIER, "foo"),
			Pair.create(T_PHP_OBJECT_OPERATOR, "->"),
			Pair.create(T_PHP_IDENTIFIER, "bar"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testMethod() throws Exception {
		Lexer lexer = new LatteHighlightingLexer(new LatteLexer());
		lexer.start("{$object->getFoo()}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_OBJECT_OPERATOR, "->"),
			Pair.create(T_PHP_METHOD, "getFoo"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{$object->getFoo()->getBar($var)}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_OBJECT_OPERATOR, "->"),
			Pair.create(T_PHP_METHOD, "getFoo"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_PHP_OBJECT_OPERATOR, "->"),
			Pair.create(T_PHP_METHOD, "getBar"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testStaticProperty() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{$object::$foo}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_MACRO_ARGS_VAR, "$foo"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{\\Foo\\Bar::$foo::$bar}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_MACRO_ARGS_VAR, "$foo"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_MACRO_ARGS_VAR, "$bar"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{Foo\\Bar::$foo}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_IDENTIFIER, "Bar"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_MACRO_ARGS_VAR, "$foo"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testStaticMethod() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{$object::getFoo()}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_PHP_IDENTIFIER, "getFoo"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{\\Foo\\Bar::getFoo($var, 'test', 123)::getBar()}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Bar"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_PHP_IDENTIFIER, "getFoo"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_MACRO_ARGS_VAR, "$var"),
			Pair.create(T_MACRO_ARGS, ","),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "test"),
			Pair.create(T_PHP_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_MACRO_ARGS, ","),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_NUMBER, "123"),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_PHP_IDENTIFIER, "getBar"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{Foo\\Bar::getFoo()::$bar}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_PHP_NAMESPACE_REFERENCE, "Foo"),
			Pair.create(T_PHP_NAMESPACE_RESOLUTION, "\\"),
			Pair.create(T_PHP_IDENTIFIER, "Bar"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_PHP_IDENTIFIER, "getFoo"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_PHP_DOUBLE_COLON, "::"),
			Pair.create(T_MACRO_ARGS_VAR, "$bar"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSpecialCases() throws Exception {
		Lexer lexer = new LatteHighlightingLexer(new LatteLexer());
		lexer.start("{count($ccc)}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_PHP_METHOD, "count"),
			Pair.create(T_PHP_LEFT_NORMAL_BRACE, "("),
			Pair.create(T_MACRO_ARGS_VAR, "$ccc"),
			Pair.create(T_PHP_RIGHT_NORMAL_BRACE, ")"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{123}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_NUMBER, "123"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testArrayItemsWithDoubleArrow() throws Exception {
		Lexer lexer = new LatteHighlightingLexer(new LatteLexer());
		lexer.start("{block test, te => $item, test . 1 => 123}{/block}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "block"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_IDENTIFIER, "test"),
			Pair.create(T_MACRO_ARGS, ","),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_IDENTIFIER, "te"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS, "=>"), //todo: weird behavior, it must be T_PHP_DOUBLE_ARROW (it is because is workaround for "=>" in LatteParser.bnf)
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_VAR, "$item"),
			Pair.create(T_MACRO_ARGS, ","),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_IDENTIFIER, "test"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_CONCATENATION, "."),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_NUMBER, "1"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_DOUBLE_ARROW, "=>"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_NUMBER, "123"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
			Pair.create(T_MACRO_CLOSE_TAG_OPEN, "{/"),
			Pair.create(T_MACRO_NAME, "block"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testBlockName() throws Exception {
		Lexer lexer = new LatteHighlightingLexer(new LatteLexer());
		lexer.start("{include #test}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "include"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_BLOCK_NAME, "#"),
			Pair.create(T_BLOCK_NAME, "test"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testLinkDestination() throws Exception {
		Lexer lexer = new LatteLookAheadLexer(new LatteLexer());
		lexer.start("{link default}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "link"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_LINK_DESTINATION, "default"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{link Presenter:default}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "link"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_LINK_DESTINATION, "Presenter"),
			Pair.create(T_LINK_DESTINATION, ":"),
			Pair.create(T_LINK_DESTINATION, "default"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{link :Presenter:default}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "link"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_LINK_DESTINATION, ":"),
			Pair.create(T_LINK_DESTINATION, "Presenter"),
			Pair.create(T_LINK_DESTINATION, ":"),
			Pair.create(T_LINK_DESTINATION, "default"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("<a n:href=\"default\"></a>");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_HTML_OPEN_TAG_OPEN, "<"),
			Pair.create(T_TEXT, "a "),
			Pair.create(T_HTML_TAG_NATTR_NAME, "n:href"),
			Pair.create(T_HTML_TAG_ATTR_EQUAL_SIGN, "="),
			Pair.create(T_HTML_TAG_ATTR_DQ, "\""),
			Pair.create(T_LINK_DESTINATION, "default"),
			Pair.create(T_HTML_TAG_ATTR_DQ, "\""),
			Pair.create(T_HTML_TAG_CLOSE, ">"),
			Pair.create(T_HTML_CLOSE_TAG_OPEN, "</"),
			Pair.create(T_TEXT, "a"),
			Pair.create(T_HTML_TAG_CLOSE, ">"),
		});

		lexer.start("{link default id => 123}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_NAME, "link"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_LINK_DESTINATION, "default"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_PHP_IDENTIFIER, "id"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS, "=>"),
			Pair.create(T_WHITESPACE, " "),
			Pair.create(T_MACRO_ARGS_NUMBER, "123"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testModifiers() throws Exception {
		Lexer lexer = new LatteLexer();
		lexer.start("{$object|bytes}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_MACRO_SEPARATOR, "|"),
			Pair.create(T_MACRO_FILTERS, "bytes"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{$object|bytes:2:'string'}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_MACRO_SEPARATOR, "|"),
			Pair.create(T_MACRO_FILTERS, "bytes"),
			Pair.create(T_PHP_EXPRESSION, ":"),
			Pair.create(T_MACRO_ARGS_NUMBER, "2"),
			Pair.create(T_PHP_EXPRESSION, ":"),
			Pair.create(T_PHP_SINGLE_QUOTE_LEFT, "'"),
			Pair.create(T_MACRO_ARGS_STRING, "string"),
			Pair.create(T_PHP_SINGLE_QUOTE_RIGHT, "'"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});

		lexer.start("{$object|bytes:2|noescape}");
		assertTokens(lexer, new Pair[] {
			Pair.create(T_MACRO_OPEN_TAG_OPEN, "{"),
			Pair.create(T_MACRO_ARGS_VAR, "$object"),
			Pair.create(T_PHP_MACRO_SEPARATOR, "|"),
			Pair.create(T_MACRO_FILTERS, "bytes"),
			Pair.create(T_PHP_EXPRESSION, ":"),
			Pair.create(T_MACRO_ARGS_NUMBER, "2"),
			Pair.create(T_PHP_MACRO_SEPARATOR, "|"),
			Pair.create(T_MACRO_FILTERS, "noescape"),
			Pair.create(T_MACRO_TAG_CLOSE, "}"),
		});
	}
}

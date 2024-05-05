package org.nette.latte.syntaxHighlighter;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.nette.latte.icons.LatteIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class LatteColorSettingsPage implements ColorSettingsPage {
	private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
		new AttributesDescriptor("Tag name", LatteSyntaxHighlighter.MACRO_NAME),
		new AttributesDescriptor("Tag filters", LatteSyntaxHighlighter.MACRO_MODIFIERS),
		new AttributesDescriptor("Tag delimiters", LatteSyntaxHighlighter.MACRO_DELIMITERS),
		new AttributesDescriptor("Tag comment", LatteSyntaxHighlighter.MACRO_COMMENT),
		new AttributesDescriptor("Block name", LatteSyntaxHighlighter.MACRO_BLOCK_NAME),
		new AttributesDescriptor("Destination link", LatteSyntaxHighlighter.MACRO_LINK_DESTINATION),
		new AttributesDescriptor("Destination file", LatteSyntaxHighlighter.MACRO_FILE_PATH),
		new AttributesDescriptor("Attribute n:tag Name", LatteSyntaxHighlighter.HTML_NATTR_NAME),
		new AttributesDescriptor("Attribute n:tag Value", LatteSyntaxHighlighter.HTML_NATTR_VALUE),
		new AttributesDescriptor("PHP variable", LatteSyntaxHighlighter.MACRO_ARGS_VAR),
		new AttributesDescriptor("PHP string", LatteSyntaxHighlighter.MACRO_ARGS_STRING),
		new AttributesDescriptor("PHP number", LatteSyntaxHighlighter.MACRO_ARGS_NUMBER),
		new AttributesDescriptor("PHP class", LatteSyntaxHighlighter.PHP_CLASS_NAME),
		new AttributesDescriptor("PHP method, function", LatteSyntaxHighlighter.PHP_METHOD),
		new AttributesDescriptor("PHP keyword", LatteSyntaxHighlighter.PHP_KEYWORD),
		new AttributesDescriptor("PHP property, constant", LatteSyntaxHighlighter.PHP_IDENTIFIER),
		new AttributesDescriptor("PHP cast", LatteSyntaxHighlighter.PHP_CAST),
		new AttributesDescriptor("PHP type", LatteSyntaxHighlighter.PHP_TYPE),
		new AttributesDescriptor("PHP null", LatteSyntaxHighlighter.PHP_NULL),
		new AttributesDescriptor("Content type", LatteSyntaxHighlighter.PHP_CONTENT_TYPE),
		new AttributesDescriptor("Assignment Operator (=, +=, ...)", LatteSyntaxHighlighter.PHP_ASSIGNMENT_OPERATOR),
		new AttributesDescriptor("Logic Operator (&&, ||)", LatteSyntaxHighlighter.PHP_LOGIC_OPERATOR),
		new AttributesDescriptor("Other PHP Operators", LatteSyntaxHighlighter.PHP_OPERATOR),
		new AttributesDescriptor("Concatenation (.)", LatteSyntaxHighlighter.PHP_CONCATENATION),
	};

	@Nullable
	@Override
	public Icon getIcon() {
		return LatteIcons.FILE;
	}

	@NotNull
	@Override
	public SyntaxHighlighter getHighlighter() {
		return new LatteSyntaxHighlighter();
	}

	@NotNull
	@Override
	public String getDemoText() {
		return "{contentType text/html}\n" +
			"{* comment *}\n" +
			"{var $string = \"abc\", $number = 123}\n" +
			"<div class=\"perex\" n:if=\"$content\">\n" +
			"    {$content|truncate:250}\n" +
			"</div>\n\n" +
			"{link :Presenter:default}\n\n" +
			"<a n:href=\"default\">go</a>\n\n" +
			"{varType \\Foo\\Bar|string|null $var}\n\n" +
			"{var $bar = $object->getFoo()->entity}\n" +
			"{var $fox = (string) $object}\n" +
			"{$foo::$staticVariable::CONSTANT}\n\n" +
			"{count($arr)}\n" +
			"{foreach $data as $key => $value}\n" +
			"    {$key} {$value}\n" +
			"{/foreach}\n\n" +
			"{var $text = 'text' . 'suffix'}\n\n" +
			"{var $y = 25 - 46}\n\n" +
			"{if $var !== 25 && $val <= 'foo' || $y % 2 === 0}\n" +
			"    {$value|noescape}\n" +
			"{/if}";
	}

	@Nullable
	@Override
	public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
		return null;
	}

	@NotNull
	@Override
	public AttributesDescriptor[] getAttributeDescriptors() {
		return DESCRIPTORS;
	}

	@NotNull
	@Override
	public ColorDescriptor[] getColorDescriptors() {
		return ColorDescriptor.EMPTY_ARRAY;
	}

	@NotNull
	@Override
	public String getDisplayName() {
		return "Latte";
	}
}

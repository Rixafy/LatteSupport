package org.nette.latte.completion.handlers;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.TokenType;
import org.nette.latte.LatteLanguage;
import org.nette.latte.config.LatteConfiguration;
import org.nette.latte.psi.LatteMacroCloseTag;
import org.nette.latte.psi.LatteMacroTag;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.settings.LatteTagSettings;
import org.nette.latte.utils.LatteUtil;
import org.jetbrains.annotations.NotNull;

public class MacroInsertHandler implements InsertHandler<LookupElement> {

	private static final MacroInsertHandler instance = new MacroInsertHandler();

	public MacroInsertHandler() {
		super();
	}

	public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement lookupElement) {
		PsiElement element = context.getFile().findElementAt(context.getStartOffset());
		if (element != null && element.getLanguage() == LatteLanguage.INSTANCE) {
			PsiElement parent = element.getParent();

			String spacesBefore = "";
			boolean resolvePairMacro = false;
			boolean lastError = parent.getLastChild().getNode().getElementType() == TokenType.ERROR_ELEMENT;
			String macroName = null;
			LatteTagSettings macro = null;
			if (lastError && element.getNode().getElementType() == LatteTypes.T_MACRO_NAME) {
				macroName = element.getText();
				macro = LatteConfiguration.getInstance(element.getProject()).getTag(macroName);

			} else if (parent instanceof LatteMacroTag) {
				macroName = ((LatteMacroTag) parent).getMacroName();
				macro = LatteConfiguration.getInstance(element.getProject()).getTag(macroName);
			}

			boolean isCloseTag = parent instanceof LatteMacroCloseTag;
			if (!isCloseTag && macro != null && macro.getType() == LatteTagSettings.Type.PAIR) {
				resolvePairMacro = true;
			}

			if (macroName != null) {
				if (resolvePairMacro && macro.isMultiLine()) {
					spacesBefore += LatteUtil.getSpacesBeforeCaret(context.getEditor());
				}

				Editor editor = context.getEditor();
				CaretModel caretModel = editor.getCaretModel();
				String text = editor.getDocument().getText();

				int spaceInserted = 0;
				int offset = caretModel.getOffset();

				if (macro != null && !isCloseTag && macro.hasParameters() && !LatteUtil.isStringAtCaret(editor, " ")) {
					EditorModificationUtil.insertStringAtCaret(editor, " ");
					spaceInserted = 1;
				}

				int lastBraceOffset = text.indexOf("}", offset);
				int endOfLineOffset = text.indexOf("\n", offset);

				if (endOfLineOffset == -1) {
					endOfLineOffset = text.length();
				}
				if (lastBraceOffset == -1 || lastBraceOffset > endOfLineOffset) {
					caretModel.moveToOffset(endOfLineOffset + spaceInserted);
					EditorModificationUtil.insertStringAtCaret(editor, "}");
					lastBraceOffset = endOfLineOffset;
					endOfLineOffset++;
				}

				if (resolvePairMacro) {
					String endTag = "";
					if (macro.isMultiLine()) {
						endTag += "\n\n" + spacesBefore;
					}
					endTag += "{/" + macroName + "}";

					int endTagOffset = text.indexOf(endTag, offset);
					if (endTagOffset == -1 || endTagOffset > endOfLineOffset) {
						caretModel.moveToOffset(lastBraceOffset + spaceInserted + 1);
						EditorModificationUtil.insertStringAtCaret(editor, endTag);
					}
				}

				caretModel.moveToOffset(offset + 1);
				PsiDocumentManager.getInstance(context.getProject()).commitDocument(editor.getDocument());
			}
		}
	}

	public static MacroInsertHandler getInstance() {
		return instance;
	}
}
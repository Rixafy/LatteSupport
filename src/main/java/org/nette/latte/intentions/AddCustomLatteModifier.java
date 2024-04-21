package org.nette.latte.intentions;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.nette.latte.LatteLanguage;
import org.nette.latte.settings.LatteFilterSettings;
import org.nette.latte.settings.LatteSettings;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for intentions which will register a custom variable.
 */
public class AddCustomLatteModifier extends BaseIntentionAction {

	/** custom macro which will be registered on invocation */
	protected final LatteFilterSettings defaultModifier;

	@NotNull
	@Override
	public String getText() {
		return "Add custom filter |" + defaultModifier.getModifierName();
	}

	public AddCustomLatteModifier(String modifierName) {
		this.defaultModifier = new LatteFilterSettings(modifierName);
	}

	@NotNull
	@Override
	public String getFamilyName() {
		return "Latte";
	}

	@Override
	public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
		return file.getLanguage() == LatteLanguage.INSTANCE;
	}

	@Override
	public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
		LatteSettings.getInstance(project).filterSettings.add(defaultModifier);
		DaemonCodeAnalyzer.getInstance(project).restart(); // force re-analyzing
	}
}
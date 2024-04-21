package org.nette.latte.inspections;

import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.codeInspection.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.nette.latte.config.LatteConfiguration;
import org.nette.latte.intentions.AddCustomLatteModifier;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.LatteMacroModifier;
import org.nette.latte.settings.LatteFilterSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ModifierDefinitionInspection extends LocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LatteModifierDefinition";
	}

	@Nullable
	@Override
	public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
		if (!(file instanceof LatteFile)) {
			return null;
		}

		final List<ProblemDescriptor> problems = new ArrayList<>();
		file.acceptChildren(new PsiRecursiveElementWalkingVisitor() {
			@Override
			public void visitElement(@NotNull PsiElement element) {
				if (element instanceof LatteMacroModifier) {
					String filterName = ((LatteMacroModifier) element).getModifierName();
					LatteFilterSettings latteFilter = LatteConfiguration.getInstance(element.getProject()).getFilter(filterName);
					if (latteFilter == null) {
						LocalQuickFix addModifierFix = IntentionManager.getInstance().convertToFix(new AddCustomLatteModifier(filterName));
						ProblemHighlightType type = ProblemHighlightType.GENERIC_ERROR_OR_WARNING;
						String description = "Undefined latte filter '" + filterName + "'";
						ProblemDescriptor problem = manager.createProblemDescriptor(element, description, true, type, isOnTheFly, addModifierFix);
						problems.add(problem);

					} else if (((LatteMacroModifier) element).getMacroModifierPartList().size() < latteFilter.getModifierInsert().length()) {
						ProblemHighlightType type = ProblemHighlightType.WARNING;
						String description = "Missing required filter parameters (" + latteFilter.getModifierInsert().length() + " required)";
						ProblemDescriptor problem = manager.createProblemDescriptor(element, description, true, type, isOnTheFly);
						problems.add(problem);
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[0]);
	}
}

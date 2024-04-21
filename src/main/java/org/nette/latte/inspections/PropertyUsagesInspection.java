package org.nette.latte.inspections;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementWalkingVisitor;
import org.nette.latte.php.LattePhpVariableUtil;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.LattePhpProperty;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.php.lang.psi.elements.PhpModifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PropertyUsagesInspection extends BaseLocalInspectionTool {

	@NotNull
	@Override
	public String getShortName() {
		return "LattePropertyUsages";
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
				if (element instanceof LattePhpProperty) {
					NettePhpType phpType = ((LattePhpProperty) element).getPrevReturnType();

					Collection<PhpClass> phpClasses = phpType.getPhpClasses(element.getProject());
					if (phpClasses.size() == 0) {
						return;
					}

					boolean isFound = false;
					String variableName = ((LattePhpProperty) element).getPropertyName();
					for (PhpClass phpClass : phpClasses) {
						for (Field field : phpClass.getFields()) {
							if (!field.isConstant() && field.getName().equals(LattePhpVariableUtil.normalizePhpVariable(variableName))) {
								PhpModifier modifier = field.getModifier();
								if (modifier.isPrivate()) {
									addProblem(manager, problems, element, "Used private property '" + variableName + "'", isOnTheFly);
								} else if (modifier.isProtected()) {
									addProblem(manager, problems, element, "Used protected property '" + variableName + "'", isOnTheFly);
								} else if (field.isDeprecated()) {
									addDeprecated(manager, problems, element, "Used property '" + variableName + "' is marked as deprecated", isOnTheFly);
								} else if (field.isInternal()) {
									addDeprecated(manager, problems, element, "Used property '" + variableName + "' is marked as internal", isOnTheFly);
								}

								if (modifier.isStatic()) {
									String description = "Property '" + variableName + "' is static but used non statically";
									addProblem(manager, problems, element, description, isOnTheFly);

								}
								isFound = true;
							}
						}
					}

					if (!isFound) {
						addProblem(manager, problems, element, "Property '" + variableName + "' not found for type '" + phpType.toString() + "'", isOnTheFly);
					}

				} else {
					super.visitElement(element);
				}
			}
		});

		return problems.toArray(new ProblemDescriptor[0]);
	}
}

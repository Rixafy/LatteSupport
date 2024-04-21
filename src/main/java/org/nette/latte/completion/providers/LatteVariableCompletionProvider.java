package org.nette.latte.completion.providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.nette.latte.completion.handlers.PhpVariableInsertHandler;
import org.nette.latte.config.LatteConfiguration;
import org.nette.latte.php.NettePhpType;
import org.nette.latte.psi.elements.LattePsiElement;
import org.nette.latte.settings.LatteVariableSettings;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.LattePhpVariable;
import org.nette.latte.utils.LatteUtil;
import org.nette.latte.utils.LattePhpCachedVariable;
import com.jetbrains.php.PhpIcons;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LatteVariableCompletionProvider extends BaseLatteCompletionProvider {

	public LatteVariableCompletionProvider() {
		super();
	}

	@Override
	protected void addCompletions(
			@NotNull CompletionParameters parameters,
			@NotNull ProcessingContext context,
			@NotNull CompletionResultSet result
	) {
		PsiElement element = parameters.getPosition().getParent();
		if ((element instanceof LattePhpVariable) && ((LattePhpVariable) element).isDefinition()) {
			return;
		}

		List<LookupElement> elements = attachPhpVariableCompletions(element);
		result.addAllElements(elements);

		if (parameters.getOriginalFile() instanceof LatteFile) {
			attachTemplateTypeCompletions(result, element.getProject(), (LatteFile) parameters.getOriginalFile());
		}
	}

	private void attachTemplateTypeCompletions(@NotNull CompletionResultSet result, @NotNull Project project, @NotNull LatteFile file) {
		NettePhpType type = LatteUtil.findFirstLatteTemplateType(file);
		if (type == null) {
			return;
		}

		Collection<PhpClass> phpClasses = type.getPhpClasses(project);
		for (PhpClass phpClass : phpClasses) {
			for (Field field : phpClass.getFields()) {
				if (!field.isConstant() && field.getModifier().isPublic()) {
					LookupElementBuilder builder = LookupElementBuilder.create(field, "$" + field.getName());
					builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
					builder = builder.withTypeText(NettePhpType.create(field.getType()).toString());
					builder = builder.withIcon(PhpIcons.VARIABLE);
					if (field.isDeprecated() || field.isInternal()) {
						builder = builder.withStrikeoutness(true);
					}
					result.addElement(builder);
				}
			}
		}
	}

	private List<LookupElement> attachPhpVariableCompletions(@NotNull PsiElement psiElement) {
		PsiFile file = psiElement instanceof LattePsiElement ? ((LattePsiElement) psiElement).getLatteFile() : psiElement.getContainingFile();
		if (!(file instanceof LatteFile)) {
			return Collections.emptyList();
		}

		List<LookupElement> lookupElements = new ArrayList<>();
		List<String> foundVariables = new ArrayList<>();

		for (LattePhpCachedVariable element : ((LatteFile) file).getCachedVariableDefinitions(psiElement.getTextOffset())) {
			String variableName = element.getElement().getVariableName();
			if (foundVariables.stream().anyMatch(variableName::equals)) {
				continue;
			}

			LookupElementBuilder builder = LookupElementBuilder.create(element.getElement(), "$" + variableName);
			builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
			builder = builder.withTypeText(element.getElement().getPrevReturnType().toString());
			builder = builder.withIcon(PhpIcons.VARIABLE);
			builder = builder.withBoldness(true);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}

		Collection<LatteVariableSettings> defaultVariables = LatteConfiguration.getInstance(psiElement.getProject()).getVariables();
		for (LatteVariableSettings variable : defaultVariables) {
			String variableName = variable.getVarName();
			if (foundVariables.stream().anyMatch(variableName::equals)) {
				continue;
			}

			LookupElementBuilder builder = LookupElementBuilder.create("$" + variableName);
			builder = builder.withInsertHandler(PhpVariableInsertHandler.getInstance());
			builder = builder.withTypeText(variable.toPhpType().toString());
			builder = builder.withIcon(PhpIcons.VARIABLE);
			builder = builder.withBoldness(false);
			lookupElements.add(builder);

			foundVariables.add(variableName);
		}
		return lookupElements;
	}

}
package org.nette.latte.reference.references;

import com.intellij.codeInsight.highlighting.HighlightedReference;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.elements.LatteLinkDestinationElement;

import java.util.ArrayList;
import java.util.List;

public class LatteLinkDestinationReference extends PsiReferenceBase<LatteLinkDestinationElement> implements HighlightedReference {
    private final String text;
    private final String currentPresenter;
    private final String previousPresenter;

    public LatteLinkDestinationReference(@NotNull LatteLinkDestinationElement element, TextRange rangeInElement, boolean soft, String text, String currentPresenter, String previousPresenter) {
        super(element, rangeInElement, soft);
        this.text = text;
        this.currentPresenter = currentPresenter;
        this.previousPresenter = previousPresenter;
    }

    @Override
    public @Nullable PsiElement resolve() {
        LatteFile file = myElement.getLatteFile();
        if (file == null || text.equals(":")) {
            return null;
        }

        if (text.endsWith("!")) {
            return file.getLinkResolver().resolveSignal(text.substring(0, text.length() - 1), currentPresenter);

        } else if (!text.equals(Strings.capitalize(text))) {
            return file.getLinkResolver().resolveAction(text, currentPresenter);

        } else {
            return file.getLinkResolver().resolvePresenter(text, !text.equals(currentPresenter));
        }
    }

    @Override
    public Object @NotNull [] getVariants() {
        List<LookupElement> variants = new ArrayList<>();

        LatteFile file = myElement.getLatteFile();
        if (file == null) {
            return variants.toArray();
        }

        PhpClass presenter = null;
        if (currentPresenter != null) {
            presenter = file.getLinkResolver().resolvePresenter(currentPresenter, false);
        } else if (previousPresenter != null) {
            presenter = file.getLinkResolver().resolvePresenter(previousPresenter, false);
        }

        String cleanLinkDestination = myElement.getLinkDestination().replace("IntellijIdeaRulezzz", "");

        if (text.isEmpty() || text.equals(StringUtils.capitalize(text))) {
            variants.addAll(file.getLinkResolver().getPresentersForAutoComplete(previousPresenter, myElement.getLinkDestination().startsWith(":"), !cleanLinkDestination.trim().contains(":")));
        }

        if ((presenter == null || !presenter.isAbstract()) && (text.isEmpty() || !text.equals(StringUtils.capitalize(text))) && !cleanLinkDestination.equals(":")) {
            PhpClass templatePresenter = file.getLinkResolver().findPresenter(null, false);
            if (presenter == null && templatePresenter != null) {
                presenter = templatePresenter;
            }

            boolean isCurrent = presenter != null && templatePresenter != null && presenter.isEquivalentTo(templatePresenter);

            if (isCurrent) {
                variants.add(LookupElementBuilder.create("this").withTailText(" in " + presenter.getName()).withIcon(AllIcons.Actions.Execute));
            }

            if (presenter != null) {
                variants.addAll(file.getLinkResolver().getActionsForAutoComplete(presenter));

                if (isCurrent) {
                    variants.addAll(file.getLinkResolver().getSignalsForAutoComplete(presenter));
                }
            }
        }

        return variants.toArray();
    }
}

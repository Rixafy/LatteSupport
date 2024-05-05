package org.nette.latte.psi.impl.elements;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteFile;
import org.nette.latte.psi.LatteTypes;
import org.nette.latte.psi.elements.LatteLinkDestinationElement;
import org.nette.latte.psi.impl.LattePsiElementImpl;
import org.nette.latte.psi.impl.LattePsiImplUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class LatteLinkDestinationElementImpl extends LattePsiElementImpl implements LatteLinkDestinationElement {
	private @Nullable List<PsiReference> references = null;
	private @Nullable PsiElement identifier = null;

	public LatteLinkDestinationElementImpl(@NotNull ASTNode node) {
		super(node);
	}

	@Override
	public void subtreeChanged() {
		super.subtreeChanged();
		identifier = null;
		references = null;
	}

	@Override
	public @Nullable PsiElement getNameIdentifier() {
		if (identifier == null) {
			identifier = LattePsiImplUtil.findFirstChildWithType(this, LatteTypes.T_LINK_DESTINATION);
		}

		return identifier;
	}

	@Override
	public @NotNull String getLinkDestination() {
		return this.getText();
	}

	@Override
	public PsiReference @NotNull [] getReferences() {
		if (references == null) {
			references = new ArrayList<>();
			List<String> presenters = new ArrayList<>();
			for (String presenter : this.getLinkDestination().trim().split(":")) {
				if (!presenter.isEmpty() && presenter.equals(StringUtils.capitalize(presenter))) {
					presenters.add(presenter);
				}
			}

			String lastPresenter = !presenters.isEmpty() ? presenters.get(presenters.size() - 1) : null;

			int textRangeIndex = 0;

			for (String entity : this.getLinkDestination().split(":")) {
				if (!entity.isEmpty()) {
					int finalTextRangeIndex = textRangeIndex;

					references.add(new PsiReferenceBase<PsiElement>(this, new TextRange(finalTextRangeIndex, finalTextRangeIndex + entity.length()), true) {
						private final String text = entity;

						@Override
						public @Nullable PsiElement resolve() {
							LatteFile file = getLatteFile();
							if (file == null) {
								return null;
							}

							if (text.endsWith("!")) {
								return file.getLinkResolver().resolveSignal(text.substring(0, text.length() - 1), lastPresenter);

							} else if (!text.equals(Strings.capitalize(text))) {
								return file.getLinkResolver().resolveAction(text, lastPresenter);

							} else {
								return file.getLinkResolver().resolvePresenter(text, !text.equals(lastPresenter));
							}
                        }

						@Override
						public Object @NotNull [] getVariants() {
							return new Object[0];
						}
					});
					textRangeIndex += entity.length() + 1;

				} else {
					textRangeIndex++;
				}
			}
		}

		return references.toArray(new PsiReference[0]);
	}
}

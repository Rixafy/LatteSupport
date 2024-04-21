package org.nette.latte.reference;

import com.intellij.lang.cacheBuilder.*;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.*;
import com.intellij.psi.tree.TokenSet;
import org.nette.latte.lexer.LatteLexer;
import org.nette.latte.lexer.LatteLookAheadLexer;
import org.nette.latte.psi.LattePhpVariable;
import org.nette.latte.psi.LatteTypes;
import org.jetbrains.annotations.*;

public class LatteFindUsagesProvider implements FindUsagesProvider {
    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(
                new LatteLookAheadLexer(new LatteLexer()),
                TokenSet.create(LatteTypes.PHP_VARIABLE),
                TokenSet.create(LatteTypes.MACRO_COMMENT),
                TokenSet.EMPTY
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof LattePhpVariable && ((LattePhpVariable) psiElement).isDefinition();
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return null;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable) {
            return "latte variable";
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement element) {
        if (element instanceof LattePhpVariable) {
            return ((LattePhpVariable) element).getVariableName();
        } else {
            return "";
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
        if (element instanceof LattePhpVariable) {
            return ((LattePhpVariable) element).getVariableName();
        } else {
            return "";
        }
    }
}

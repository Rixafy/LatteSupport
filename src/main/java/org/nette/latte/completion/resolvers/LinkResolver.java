package org.nette.latte.completion.resolvers;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteFile;

import java.util.ArrayList;
import java.util.List;

public class LinkResolver extends PresenterResolver {
    //private final HashMap<String, @Nullable PsiElement> cache = new HashMap<>();

    public LinkResolver(LatteFile file) {
        super(file);
    }

    public void reset() {
        //cache.clear();
    }

    public @Nullable PsiElement resolveAction(String action, @Nullable String presenter) {
        String key = presenter + ":" + action;
        /*if (cache.containsKey(key)) {
            return cache.get(key);
        }*/

        PsiElement result = calculateAction(action, presenter);
        //cache.put(key, result);
        return result;
    }

    private @Nullable PsiElement calculateAction(String action, @Nullable String presenter) {
        List<String> actions = new ArrayList<>(action.equals("this") ? guessActionName() : List.of(action));
        List<String> presenterNames = guessPresenterNames(presenter);
        List<PhpClass> matchingPresenters = getMatchingPresenters(presenterNames, false);

        if (matchingPresenters.isEmpty()) {
            return null;
        }

        for (PhpClass presenterClass : matchingPresenters) {
            for (String actionName : actions) {
                Method method = findMethod(presenterClass, List.of("action" + StringUtils.capitalize(actionName), "render" + StringUtils.capitalize(actionName), "startup"));
                if (method != null && (!method.getName().equals("startup") || method.getClass().getName().equals(presenterClass.getName()))) {
                    return method;
                }
            }
        }

        return matchingPresenters.get(0);
    }

    public @Nullable PsiElement resolveSignal(String signal, @Nullable String presenter) {
        String key = presenter + ":" + signal + "!";
        /*if (cache.containsKey(key)) {
            return cache.get(key);
        }*/

        PsiElement result = calculateSignal(signal, presenter);
        //cache.put(key, result);
        return result;
    }

    private @Nullable PsiElement calculateSignal(String signal, String presenter) {
        List<String> presenterNames = guessPresenterNames(presenter);
        List<PhpClass> matchingPresenters = getMatchingPresenters(presenterNames, false);

        if (matchingPresenters.isEmpty()) {
            return null;
        }

        for (PhpClass presenterClass : matchingPresenters) {
            PsiElement method = findMethod(presenterClass, List.of("handle" + StringUtils.capitalize(signal)));
            if (method != null) {
                return method;
            }
        }

        return matchingPresenters.get(0);
    }

    public List<LookupElement> getActionsForAutoComplete(PhpClass presenter) {
        List<LookupElement> actions = new ArrayList<>();
        for (Method method : presenter.getMethods()) {
            if (method.getName().startsWith("action") || method.getName().startsWith("render")) {
                String className = method.getContainingClass() != null ? method.getContainingClass().getName() : presenter.getName();
                actions.add(LookupElementBuilder.create(StringUtils.uncapitalize(method.getName().substring(6))).withTailText(" in " + className).withIcon(AllIcons.Actions.Execute));
            }
        }

        return actions;
    }

    public List<LookupElement> getSignalsForAutoComplete(PhpClass presenter) {
        List<LookupElement> signals = new ArrayList<>();
        for (Method method : presenter.getMethods()) {
            if (method.getName().startsWith("handle") && !method.getName().equals("handleInvalidLink")) {
                String className = method.getContainingClass() != null ? method.getContainingClass().getName() : presenter.getName();
                signals.add(LookupElementBuilder.create(StringUtils.uncapitalize(method.getName().substring(6)) + "!").withTailText(" in " + className).withIcon(AllIcons.Actions.Lightning));
            }
        }

        return signals;
    }

    public List<String> guessActionName() {
        List<String> actions = new ArrayList<>();

        String fileName = file.getOriginalFile().getName().replace(".latte", "");

        for (String separator : List.of("-", "_")) {
            if (fileName.contains(separator)) {
                String[] parts = fileName.split(separator);
                if (parts.length > 1) {
                    actions.add(parts[1]);
                }
            }
        }

        actions.add(StringUtils.uncapitalize(fileName));

        if (!actions.contains("default")) {
            actions.add("default");
        }

        return actions;
    }
}

package org.nette.latte.reference;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LatteLinkResolver {
    private final LatteFile file;
    //private final HashMap<String, @Nullable PsiElement> cache = new HashMap<>();
    //private static List<PhpClass> presenters = null;

    public LatteLinkResolver(LatteFile file) {
        this.file = file;
    }

    public void reset() {
        //cache.clear();
        //presenters = null;
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

        Logger.getInstance(LatteLinkResolver.class).warn("Matching presenters: " + matchingPresenters);

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

    public @Nullable PsiElement resolvePresenter(String presenter, boolean preferAbstract) {
        String key = presenter + ":" + preferAbstract;
        /*if (cache.containsKey(key)) {
            return cache.get(key);
        }*/

        PsiElement result = calculatePresenter(presenter, preferAbstract);
        //cache.put(key, result);
        return result;
    }

    private @Nullable PsiElement calculatePresenter(String presenter, boolean preferAbstract) {
        List<PhpClass> matchingPresenters = getMatchingPresenters(List.of(presenter), preferAbstract);

        if (matchingPresenters.isEmpty()) {
            return null;
        }

        return matchingPresenters.get(0);
    }

    private @Nullable Method findMethod(PhpClass phpClass, List<String> methodNames) {
        for (String methodName : methodNames) {
            Method method = phpClass.findMethodByName(methodName);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    private List<PhpClass> getMatchingPresenters(List<String> presenterNames, boolean preferAbstract) {
        List<PhpClass> presenters = getPresenters();

        HashMap<String, PhpClass> matchingPresenters = new HashMap<>();
        HashMap<String, Integer> collisionScore = new HashMap<>();
        for (String presenterName : presenterNames) {
            for (PhpClass presenter : presenters) {
                if (presenter.getName().equals(presenterName + "Presenter") || (presenter.isAbstract() && presenter.getName().contains(presenterName + "Presenter"))) {
                    Integer howDifferent = StringUtils.difference(file.getOriginalFile().getContainingDirectory().getVirtualFile().getPath(), presenter.getContainingFile().getContainingDirectory().getVirtualFile().getPath()).length();

                    if (matchingPresenters.containsKey(presenterName)) {
                        if (howDifferent < collisionScore.get(presenterName)) {
                            matchingPresenters.put(presenterName, presenter);
                            collisionScore.put(presenterName, howDifferent);
                        }

                    } else {
                        matchingPresenters.put(presenterName, presenter);
                        collisionScore.put(presenterName, howDifferent);
                    }
                }
            }
        }

        if (preferAbstract) {
            List<PhpClass> abstracts = matchingPresenters.values().stream().filter(PhpClass::isAbstract).toList();
            if (!abstracts.isEmpty()) {
                return abstracts;
            }
        }

        return matchingPresenters.values().stream().toList();
    }

    private List<String> guessPresenterNames(@Nullable String fallback) {
        List<String> names = new ArrayList<>(fallback == null ? List.of() : List.of(fallback));

        if (this.file.getFirstLatteTemplateType() != null) {
            String namespace = this.file.getFirstLatteTemplateType().getTypes().get(0);
            String[] parts = namespace.split("\\\\");
            String className = parts[parts.length - 1];
            names.add(className.replace("Template", ""));
        }

        String directoryName = file.getOriginalFile().getContainingDirectory().getName();
        directoryName = directoryName.substring(0, 1).toUpperCase() + directoryName.substring(1);

        if (!names.contains(directoryName)) {
            names.add(directoryName);
        }

        String fileName = file.getOriginalFile().getName().replace(".latte", "");
        fileName = fileName.substring(0, 1).toUpperCase() + fileName.substring(1);

        for (String separator : List.of("-", "_")) {
            if (fileName.contains(separator)) {
                names.add(fileName.split(separator)[0]);
            }
        }

        if (!names.contains(fileName)) {
            names.add(fileName);
        }

        return names;
    }

    private List<String> guessActionName() {
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

    private List<PhpClass> getPresenters() {
        PhpClass presenterParent = PhpIndex.getInstance(file.getProject()).getAnyByFQN("\\Nette\\Application\\UI\\Presenter").stream().findFirst().orElse(null);
        if (presenterParent == null) {
            return new ArrayList<>();
        }

        return getSubclassTree(presenterParent);
    }

    private List<PhpClass> getSubclassTree(PhpClass phpClass) {
        List<PhpClass> out = new ArrayList<>();

        for (PhpClass subclass : PhpIndex.getInstance(file.getProject()).getDirectSubclasses(phpClass.getFQN())) {
            out.add(subclass);
            out.addAll(getSubclassTree(subclass));
        }

        return out;
    }
}

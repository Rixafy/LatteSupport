package org.nette.latte.completion.resolvers;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.nette.latte.psi.LatteFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class PresenterResolver {
    protected final LatteFile file;
    //private static List<PhpClass> presenterCache = null;

    public PresenterResolver(LatteFile file) {
        this.file = file;
    }

    public void reset() {
        //presenters = null;
    }

    public @Nullable PhpClass resolvePresenter(String presenter, boolean preferAbstract) {
        String key = presenter + preferAbstract;
        /*if (presenterCache.containsKey(key)) {
            return presenterCache.get(key);
        }*/

        PhpClass result = calculatePresenter(presenter, preferAbstract);

        //presenterCache.put(key, result);
        return result;
    }

    private @Nullable PhpClass calculatePresenter(String presenter, boolean preferAbstract) {
        List<PhpClass> matchingPresenters = getMatchingPresenters(List.of(presenter), preferAbstract);

        if (matchingPresenters.isEmpty()) {
            return null;
        }

        return matchingPresenters.get(0);
    }

    protected @Nullable Method findMethod(PhpClass phpClass, List<String> methodNames) {
        for (String methodName : methodNames) {
            Method method = phpClass.findMethodByName(methodName);
            if (method != null) {
                return method;
            }
        }

        return null;
    }

    public @Nullable PhpClass findPresenter(@Nullable String presenter, boolean onlyAbstract) {
        List<String> presenterNames = guessPresenterNames(presenter);
        List<PhpClass> matchingPresenters = getMatchingPresenters(presenterNames, onlyAbstract);

        if (matchingPresenters.isEmpty()) {
            return null;
        }

        return matchingPresenters.get(0);
    }

    public List<LookupElement> getPresentersForAutoComplete(@Nullable String parent, boolean fullQualified, boolean root) {
        List<PhpClass> presenters = getPresenters();

        PhpClass parentClass = null;
        if (parent != null) {
            parentClass = getMatchingPresenters(List.of(parent), true).stream().findFirst().orElse(null);
        } else {
            String guessFromTemplate = guessPresenterNames(null).stream().findFirst().orElse(null);
            if (guessFromTemplate != null) {
                PhpClass templatePresenter = getMatchingPresenters(List.of(guessFromTemplate), true).stream().findFirst().orElse(null);
                if (templatePresenter != null) {
                    if (templatePresenter.isAbstract() && presenters.contains(templatePresenter)) {
                        parentClass = templatePresenter;
                    } else if (templatePresenter.getSuperClass() != null && presenters.contains(templatePresenter.getSuperClass())) {
                        parentClass = templatePresenter.getSuperClass();
                    }
                }
            }
        }

        boolean atLeastOneChild = parentClass != null && getSubclassTree(parentClass).size() > 1;

        boolean searchOnlyBaseModules = false;
        if (fullQualified && parent == null) {
            for (PhpClass presenter : presenters) {
                if (presenter.isAbstract() && !getPresenterNameForAutoCompletion(presenter).isEmpty()) {
                    searchOnlyBaseModules = true;
                    break;
                }
            }
        }

        List<LookupElement> result = new ArrayList<>();
        for (PhpClass presenter : presenters) {
            if (parent != null && parentClass != null && presenter.isEquivalentTo(parentClass)) {
                continue;
            }

            String name = getPresenterNameForAutoCompletion(presenter);

            if (searchOnlyBaseModules || root) {
                if (presenter.isAbstract() && !name.isEmpty()) {
                    PhpClass superClass = presenter.getSuperClass();
                    if (superClass == null || !presenters.contains(superClass) || getPresenterNameForAutoCompletion(superClass).isEmpty()) {
                        result.add(LookupElementBuilder
                                .create((root ? ":" : "") + name + ":")
                                .withPresentableText(":" + name)
                                .withTailText(": from " + presenter.getName())
                                .withIcon(AllIcons.Actions.GroupByModule)
                        );
                    }
                }
            }

            if (atLeastOneChild) {
                PhpClass superClass = presenter.getSuperClass();

                if (superClass != null && superClass.isEquivalentTo(parentClass) && !name.isEmpty()) {
                    result.add(LookupElementBuilder
                            .create(name + ":")
                            .withPresentableText(name)
                            .withTailText(": from " + presenter.getName())
                            .withIcon(presenter.isAbstract() ? AllIcons.Actions.GroupByModule : AllIcons.Nodes.Property)
                    );
                }

            }

            if (!atLeastOneChild || root) {
                if (parentClass != null) {
                    if (presenter.getSuperClass() != null && presenter.getSuperClass().isEquivalentTo(parentClass)) {
                        result.add(LookupElementBuilder
                                .create(name + ":")
                                .withPresentableText(name)
                                .withTailText(": from " + presenter.getName())
                                .withIcon(presenter.isAbstract() ? AllIcons.Actions.GroupByModule : AllIcons.Nodes.Property)
                        );
                    }

                } else {
                    result.add(LookupElementBuilder
                            .create(name + ":")
                            .withPresentableText(name)
                            .withTailText(": from " + presenter.getName())
                            .withIcon(presenter.isAbstract() ? AllIcons.Actions.GroupByModule : AllIcons.Nodes.Property)
                    );
                }
            }
        }

        return result;
    }

    private String getPresenterNameForAutoCompletion(PhpClass presenter) {
        if (presenter.isAbstract()) {
            return presenter.getName().replace("Abstract", "").replace("Base", "").replace("Presenter", "");
        }

        return presenter.getName().replace("Presenter", "");
    }

    protected List<PhpClass> getMatchingPresenters(List<String> presenterNames, boolean preferAbstract) {
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
            List<PhpClass> abstracts = new ArrayList<>();

            for (PhpClass presenter : matchingPresenters.values()) {
                if (presenter.isAbstract()) {
                    abstracts.add(presenter);
                }
            }

            if (!abstracts.isEmpty()) {
                return abstracts;
            }
        }

        return matchingPresenters.isEmpty() ? new ArrayList<>() : new ArrayList<>(matchingPresenters.values());
    }

    protected List<String> guessPresenterNames(@Nullable String fallback) {
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

    protected List<PhpClass> getPresenters() {
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

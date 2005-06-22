package com.siyeh.ig.naming;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.fixes.RenameFix;
import com.siyeh.ig.psiutils.ClassUtils;
import org.jetbrains.annotations.NotNull;

public class JUnitAbstractTestClassNamingConventionInspection extends ConventionInspection {
    private static final int DEFAULT_MIN_LENGTH = 12;
    private static final int DEFAULT_MAX_LENGTH = 64;
    private final RenameFix fix = new RenameFix();

    public String getDisplayName() {
        return "JUnit abstract test class naming convention";
    }

    public String getGroupDisplayName() {
        return GroupNames.JUNIT_GROUP_NAME;
    }

    protected InspectionGadgetsFix buildFix(PsiElement location) {
        return fix;
    }

    protected boolean buildQuickFixesOnlyForOnTheFlyErrors() {
        return true;
    }

    public String buildErrorString(PsiElement location) {
        final PsiClass aClass = (PsiClass) location.getParent();
        assert aClass != null;
        final String className = aClass.getName();
        if (className.length() < getMinLength()) {
            return "Abstract JUnit test class name '#ref' is too short #loc";
        } else if (className.length() > getMaxLength()) {
            return "Abstract JUnit test class name '#ref' is too long #loc";
        }
        return "Abstract JUnit test class name '#ref' doesn't match regex '" + getRegex() + "' #loc";
    }

    protected String getDefaultRegex() {
        return "[A-Z][A-Za-z]*TestCase";
    }

    protected int getDefaultMinLength() {
        return DEFAULT_MIN_LENGTH;
    }

    protected int getDefaultMaxLength() {
        return DEFAULT_MAX_LENGTH;
    }

    public BaseInspectionVisitor buildVisitor() {
        return new NamingConventionsVisitor();
    }

    public ProblemDescriptor[] doCheckClass(PsiClass aClass, InspectionManager manager, boolean isOnTheFly) {
        if (!aClass.isPhysical()) {
            return super.doCheckClass(aClass, manager, isOnTheFly);
        }
        final BaseInspectionVisitor visitor = createVisitor(manager, isOnTheFly);
        aClass.accept(visitor);
        return visitor.getErrors();
    }

    private class NamingConventionsVisitor extends BaseInspectionVisitor {


        public void visitClass(@NotNull PsiClass aClass) {
            if (aClass.isInterface() || aClass.isEnum() || aClass.isAnnotationType()) {
                return;
            }
            if(aClass instanceof PsiTypeParameter ||
                    aClass instanceof PsiAnonymousClass){
                return;
            }
            if(!aClass.hasModifierProperty(PsiModifier.ABSTRACT))
            {
                return;
            }
            if(!ClassUtils.isSubclass(aClass, "junit.framework.TestCase")) {
                return;
            }
            final String name = aClass.getName();
            if (name == null) {
                return;
            }
            if (isValid(name)) {
                return;
            }
            registerClassError(aClass);
        }

    }
}

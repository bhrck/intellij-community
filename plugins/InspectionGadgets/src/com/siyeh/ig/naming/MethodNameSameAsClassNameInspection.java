package com.siyeh.ig.naming;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.MethodInspection;
import com.siyeh.ig.fixes.RenameFix;
import org.jetbrains.annotations.NotNull;

public class MethodNameSameAsClassNameInspection extends MethodInspection {
    private final RenameFix fix = new RenameFix();

    public String getDisplayName() {
        return "Method name same as class name";
    }

    public String getGroupDisplayName() {
        return GroupNames.NAMING_CONVENTIONS_GROUP_NAME;
    }

    protected InspectionGadgetsFix buildFix(PsiElement location) {
        return fix;
    }

    protected boolean buildQuickFixesOnlyForOnTheFlyErrors() {
        return true;
    }

    public String buildErrorString(PsiElement location) {
        return "Method name '#ref' is the same as its class name #loc";
    }

    public BaseInspectionVisitor buildVisitor() {
        return new MethodNameSameAsClassNameVisitor();
    }

    private static class MethodNameSameAsClassNameVisitor extends BaseInspectionVisitor {

        public void visitMethod(@NotNull PsiMethod method) {
            // no call to super, so it doesn't drill down into inner classes
            if (method.isConstructor()) {
                return;
            }
            final String methodName = method.getName();
            if (methodName == null) {
                return;
            }
            final PsiClass containingClass = method.getContainingClass();
            if (containingClass == null) {
                return;
            }
            final String className = containingClass.getName();
            if (className == null) {
                return;
            }
            if (!methodName.equals(className)) {
                return;
            }
            registerMethodError(method);
        }

    }

}

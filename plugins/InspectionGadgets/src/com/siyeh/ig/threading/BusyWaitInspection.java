package com.siyeh.ig.threading;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ExpressionInspection;
import com.siyeh.ig.psiutils.ClassUtils;
import com.siyeh.ig.psiutils.ControlFlowUtils;
import org.jetbrains.annotations.NotNull;

public class BusyWaitInspection extends ExpressionInspection {

    public String getDisplayName() {
        return "Busy wait";
    }

    public String getGroupDisplayName() {
        return GroupNames.THREADING_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Call to Thread.#ref() in a loop, probably busy-waiting #loc";
    }

    public BaseInspectionVisitor buildVisitor() {
        return new BusyWaitVisitor();
    }

    private static class BusyWaitVisitor extends BaseInspectionVisitor {

        public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            if (methodExpression == null) {
                return;
            }
            final String methodName = methodExpression.getReferenceName();
            if (!"sleep".equals(methodName)) {
                return;
            }
            if (!ControlFlowUtils.isInLoop(expression)) {
                return;
            }

            final PsiMethod method = expression.resolveMethod();
            if (method == null) {
                return;
            }
            final PsiClass methodClass = method.getContainingClass();
            if(methodClass == null ||
                       !ClassUtils.isSubclass(methodClass, "java.lang.Thread")){
                return;
            }
            registerMethodCallError(expression);
        }

    }

}

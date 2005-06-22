package com.siyeh.ig.style;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ExpressionInspection;
import com.siyeh.ig.InspectionGadgetsFix;
import com.siyeh.ig.psiutils.ClassUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class UnnecessarilyQualifiedStaticUsageInspection extends ExpressionInspection {
    /** @noinspection PublicField*/
    public boolean m_ignoreStaticFieldAccesses = false;
    /** @noinspection PublicField*/
    public boolean m_ignoreStaticMethodCalls = false;
    private final UnnecessarilyQualifiedStaticCallFix fix = new UnnecessarilyQualifiedStaticCallFix();

    public String getDisplayName() {
        return "Unnecessarily qualified static usage";
    }

    public String getGroupDisplayName() {
        return GroupNames.STYLE_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        final PsiElement parent = location.getParent();
        if (parent instanceof PsiMethodCallExpression) {
            return "Unnecessarily qualified static method call '#ref()' #loc";
        } else {
            return "Unnecessarily qualified static field access '#ref' #loc";
        }
    }

    public JComponent createOptionsPanel() {
        final JPanel panel = new JPanel(new GridBagLayout());
        final JCheckBox ignoreFieldAccessesCheckBox = new JCheckBox("Ignore unnecessarily qualified field accesses",
                m_ignoreStaticFieldAccesses);
        final ButtonModel ignoreFieldAccessesModel = ignoreFieldAccessesCheckBox.getModel();
        ignoreFieldAccessesModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                m_ignoreStaticFieldAccesses = ignoreFieldAccessesModel.isSelected();
            }
        });
        final JCheckBox ignoreMethodCallsCheckBox = new JCheckBox("Ignore unnecessarily qualified method calls",
                m_ignoreStaticMethodCalls);
        final ButtonModel ignoreMethodCallsModel = ignoreMethodCallsCheckBox.getModel();
        ignoreMethodCallsModel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                m_ignoreStaticMethodCalls = ignoreMethodCallsModel.isSelected();
            }
        });
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1.0;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        panel.add(ignoreFieldAccessesCheckBox, constraints);
        constraints.gridy = 1;
        panel.add(ignoreMethodCallsCheckBox, constraints);
        return panel;
    }

    public BaseInspectionVisitor buildVisitor() {
        return new UnnecessarilyQualifiedStaticCallVisitor();
    }

    public InspectionGadgetsFix buildFix(PsiElement location) {
        return fix;
    }

    private static class UnnecessarilyQualifiedStaticCallFix extends InspectionGadgetsFix {
        public String getName() {
            return "Remove unnecessary qualifier";
        }

        public void doFix(Project project, ProblemDescriptor descriptor)
                                                                         throws IncorrectOperationException{
            final PsiReferenceExpression expression =
                    (PsiReferenceExpression) descriptor.getPsiElement();
            final String newExpression = expression.getReferenceName();
            replaceExpression(expression, newExpression);
        }
    }

    private class UnnecessarilyQualifiedStaticCallVisitor extends BaseInspectionVisitor {

        public void visitMethodCallExpression(@NotNull PsiMethodCallExpression expression) {
            super.visitMethodCallExpression(expression);
            if (m_ignoreStaticMethodCalls) {
                return;
            }
            final PsiReferenceExpression methodExpression = expression.getMethodExpression();
            if (!isUnnecessarilyQualifiedMethodCall(methodExpression)) {
                return;
            }
            registerError(methodExpression);
        }

        public void visitReferenceExpression(@NotNull PsiReferenceExpression expression) {
            super.visitReferenceExpression(expression);
            if (m_ignoreStaticFieldAccesses) {
                return;
            }
            final PsiElement element = expression.resolve();
            if (!(element instanceof PsiField)) {
                return;
            }
            if (!isUnnecessarilyQualifiedFieldAccess(expression)) {
                return;
            }
            registerError(expression);
        }

        private boolean isUnnecessarilyQualifiedFieldAccess(PsiReferenceExpression expression) {
            final PsiExpression qualifierExpression =
                    expression.getQualifierExpression();
            if(!(qualifierExpression instanceof PsiJavaCodeReferenceElement)){
                return false;
            }
            final PsiElement element = expression.resolve();
            if (!(element instanceof PsiField) && !(element instanceof PsiMethod)) {
                return false;
            }
            final PsiMember member = (PsiMember) element;
            if (!member.hasModifierProperty(PsiModifier.STATIC)) {
                return false;
            }

            final PsiElement qualifierElement =
                    ((PsiReference) qualifierExpression).resolve();
            if (!(qualifierElement instanceof PsiClass)) {
                return false;
            }
            final String referenceName = expression.getReferenceName();
            PsiClass parentClass = ClassUtils.getContainingClass(expression);
            PsiClass containingClass = parentClass;
            while (parentClass != null) {
                containingClass = parentClass;
                final PsiField[] fields = containingClass.getAllFields();
                for(final PsiField field : fields){
                    final String name = field.getName();
                    if(referenceName.equals(name) &&
                            !containingClass.equals(qualifierElement)){
                        return false;
                    }
                }
                parentClass = ClassUtils.getContainingClass(containingClass);
            }
            PsiMethod containingMethod =
                    PsiTreeUtil.getParentOfType(expression,
                                                            PsiMethod.class);
            while (containingMethod != null) {
                final PsiParameterList parameterList = containingMethod.getParameterList();
                final PsiParameter[] parameters = parameterList.getParameters();
                for(final PsiParameter parameter : parameters){
                    final String name = parameter.getName();
                    if(referenceName.equals(name)){
                        return false;
                    }
                }
                containingMethod =
                        PsiTreeUtil.getParentOfType(containingMethod,
                                                                PsiMethod.class);
            }
            return qualifierElement.equals(containingClass);
        }

        private boolean isUnnecessarilyQualifiedMethodCall(PsiReferenceExpression expression) {
            final PsiExpression qualifierExpression =
                    expression.getQualifierExpression();
            if(!(qualifierExpression instanceof PsiJavaCodeReferenceElement)){
                return false;
            }
            final PsiElement element = expression.resolve();
            if (!(element instanceof PsiField) && !(element instanceof PsiMethod)) {
                return false;
            }
            final PsiMember member = (PsiMember) element;
            if (!member.hasModifierProperty(PsiModifier.STATIC)) {
                return false;
            }

            final PsiElement qualifierElement = ((PsiReference) qualifierExpression).resolve();
            if (!(qualifierElement instanceof PsiClass)) {
                return false;
            }
            final String referenceName = expression.getReferenceName();
            PsiClass parentClass = ClassUtils.getContainingClass(expression);
            PsiClass containingClass = parentClass;
            while (parentClass != null) {
                containingClass = parentClass;
                final PsiMethod[] methods = containingClass.getAllMethods();
                for(final PsiMethod method : methods){
                    final String name = method.getName();
                    if(referenceName.equals(name) &&
                            !containingClass.equals(qualifierElement)){
                        return false;
                    }
                }
                parentClass = ClassUtils.getContainingClass(containingClass);
            }
            return qualifierElement.equals(containingClass);
        }
    }
}
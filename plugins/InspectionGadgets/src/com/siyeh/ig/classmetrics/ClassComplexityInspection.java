package com.siyeh.ig.classmetrics;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.siyeh.ig.BaseInspectionVisitor;
import org.jetbrains.annotations.NotNull;

public class ClassComplexityInspection
        extends ClassMetricInspection {
    public String getID(){
        return "OverlyComplexClass";
    }
    private static final int DEFAULT_COMPLEXITY_LIMIT = 80;

    public String getDisplayName() {
        return "Overly complex class";
    }

    public String getGroupDisplayName() {
        return GroupNames.CLASSMETRICS_GROUP_NAME;
    }

    protected int getDefaultLimit() {
        return DEFAULT_COMPLEXITY_LIMIT;
    }

    protected String getConfigurationLabel() {
        return "Cyclomatic complexity limit:";
    }

    public String buildErrorString(PsiElement location) {
        final PsiClass aClass = (PsiClass) location.getParent();
        final int totalComplexity = calculateTotalComplexity(aClass);
        return "#ref is overly complex (cyclomatic complexity = " + totalComplexity + ") #loc";
    }

    public BaseInspectionVisitor buildVisitor() {
        return new ClassComplexityVisitor();
    }

    private class ClassComplexityVisitor extends BaseInspectionVisitor {


        public void visitClass(@NotNull PsiClass aClass) {
            // note: no call to super
            final int totalComplexity = calculateTotalComplexity(aClass);
            if (totalComplexity <= getLimit()) {
                return;
            }
            registerClassError(aClass);
        }

    }

    private static int calculateTotalComplexity(PsiClass aClass) {
        final PsiMethod[] methods = aClass.getMethods();
        int totalComplexity = calculateComplexityForMethods(methods);
        totalComplexity += calculateInitializerComplexity(aClass);
        return totalComplexity;
    }

    private static int calculateInitializerComplexity(PsiClass aClass) {
        final ComplexityVisitor visitor = new ComplexityVisitor();
        int complexity = 0;
        final PsiClassInitializer[] initializers = aClass.getInitializers();
        for(final PsiClassInitializer initializer : initializers){
            visitor.reset();
            initializer.accept(visitor);
            complexity += visitor.getComplexity();
        }
        return complexity;
    }

    private static int calculateComplexityForMethods(PsiMethod[] methods) {
        final ComplexityVisitor visitor = new ComplexityVisitor();
        int complexity = 0;
        for(final PsiMethod method : methods){
            visitor.reset();
            method.accept(visitor);
            complexity += visitor.getComplexity();
        }
        return complexity;
    }

}

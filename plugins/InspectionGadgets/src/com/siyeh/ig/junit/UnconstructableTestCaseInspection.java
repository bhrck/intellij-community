package com.siyeh.ig.junit;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.psiutils.ClassUtils;
import com.siyeh.ig.psiutils.TypeUtils;
import org.jetbrains.annotations.NotNull;

public class UnconstructableTestCaseInspection extends ClassInspection{
    public String getID(){
        return "UnconstructableJUnitTestCase";
    }

    public String getDisplayName(){
        return "Unconstructable JUnit TestCase";
    }

    public String getGroupDisplayName(){
        return GroupNames.JUNIT_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location){
        return "Test case #ref is unusable by most test runners #loc";
    }

    public BaseInspectionVisitor buildVisitor(){
        return new UnconstructableTestCaseVisitor();
    }

    private static class UnconstructableTestCaseVisitor
            extends BaseInspectionVisitor{

        public void visitClass(@NotNull PsiClass aClass){
            if(aClass.isInterface() || aClass.isEnum() ||
                       aClass.isAnnotationType() ||
                       aClass.hasModifierProperty(PsiModifier.ABSTRACT)){
                return;
            }
            if(aClass instanceof PsiTypeParameter ||
                    aClass instanceof PsiAnonymousClass){
                return;
            }
            if(!ClassUtils.isSubclass(aClass, "junit.framework.TestCase")){
                return;
            }
            boolean hasConstructor = false;
            boolean hasNoArgConstructor = false;
            boolean hasStringConstructor = false;

            final PsiMethod[] constructors = aClass.getConstructors();
            if(constructors == null){
                return;
            }

            for(final PsiMethod constructor : constructors){
                hasConstructor = true;
                if(!constructor.hasModifierProperty(PsiModifier.PUBLIC)){
                    continue;
                }
                final PsiParameterList parameterList =
                        constructor.getParameterList();
                final PsiParameter[] parameters = parameterList.getParameters();
                if(parameters.length == 0){
                    hasNoArgConstructor = true;
                }
                if(parameters.length == 1){
                    final PsiType type = parameters[0].getType();
                    if(TypeUtils.typeEquals("java.lang.String", type)){
                        hasStringConstructor = true;
                    }
                }
            }

            if(!hasConstructor){
                return;
            }
            if(hasNoArgConstructor || hasStringConstructor){
                return;
            }
            registerClassError(aClass);
        }
    }
}

package com.siyeh.ig.bugs;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ExpressionInspection;
import org.jetbrains.annotations.NotNull;

public class NullArgumentToVariableArgMethodInspection extends ExpressionInspection{
    public String getDisplayName(){
        return "Confusing 'null' argument to var-arg method";
    }

    public String getGroupDisplayName(){
        return GroupNames.BUGS_GROUP_NAME;
    }

    public boolean isEnabledByDefault(){
        return true;
    }

    public String buildErrorString(PsiElement location){
        return "Confusing '#ref' argument to var-arg method #loc";
    }

    public BaseInspectionVisitor buildVisitor(){
        return new NullArgumentToVariableArgVisitor();
    }

    private static class NullArgumentToVariableArgVisitor
            extends BaseInspectionVisitor{


        public void visitMethodCallExpression(@NotNull PsiMethodCallExpression call){
            super.visitMethodCallExpression(call);
            final PsiManager manager = call.getManager();
            final LanguageLevel languageLevel =
                    manager.getEffectiveLanguageLevel();
            if(languageLevel.equals(LanguageLevel.JDK_1_3) ||
                       languageLevel.equals(LanguageLevel.JDK_1_4)){
                return;
            }
            final PsiExpressionList argumentList = call.getArgumentList();
            if(argumentList == null){
                return;
            }
            final PsiExpression[] args = argumentList.getExpressions();
            if(args.length == 0){
                return;
            }
            final PsiExpression lastArg = args[args.length - 1];
            if(!isNull(lastArg)){
                return;
            }
            final PsiMethod method = call.resolveMethod();
            if(method == null){
                return;
            }
            final PsiParameterList parameterList = method.getParameterList();
            if(parameterList == null){
                return;
            }
            final PsiParameter[] parameters = parameterList.getParameters();
            if(parameters == null){
                return;
            }
            if(parameters.length != args.length){
                return;
            }
            final PsiParameter lastParameter = parameters[parameters.length - 1];
            if(!lastParameter.isVarArgs())
            {
                return;
            }
            registerError(lastArg);
        }

        private static boolean isNull(PsiExpression arg){
            if(!(arg instanceof PsiLiteralExpression)){
                return false;
            }
            final String text = arg.getText();
            return "null".equals(text);
        }
    }
}

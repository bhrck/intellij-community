package com.siyeh.ig.threading;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.MethodInspection;
import org.jetbrains.annotations.NotNull;

public class SynchronizeOnLockInspection extends MethodInspection{
    public String getID(){
        return "SynchroniziationOnLockObject";
    }

    public String getDisplayName(){
        return "Synchronization on a java.util.concurrent.locks.Lock object";
    }

    public String getGroupDisplayName(){
        return GroupNames.THREADING_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location){
        return "Synchronization on a java.util.concurrent.locks.Lock object is unlikely to be intentional #loc";
    }

    public BaseInspectionVisitor buildVisitor(){
        return new SynchronizeOnLockVisitor();
    }

    private static class SynchronizeOnLockVisitor extends BaseInspectionVisitor{


        public void visitSynchronizedStatement(@NotNull PsiSynchronizedStatement statement){
            super.visitSynchronizedStatement(statement);
            final PsiExpression lockExpression = statement.getLockExpression();
            if(lockExpression == null){
                return;
            }
            final PsiType type = lockExpression.getType();
            if(type == null){
                return;
            }
            final PsiManager manager = lockExpression.getManager();
            final Project project = manager.getProject();
            final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            final PsiClass javaUtilLockClass =
                    manager.findClass("java.util.concurrent.locks.Lock", scope);
            if(javaUtilLockClass == null){
                return;
            }
            final PsiElementFactory elementFactory =
                    manager.getElementFactory();
            final PsiClassType javaUtilLockType =
                    elementFactory.createType(javaUtilLockClass);
            if(!javaUtilLockType.isAssignableFrom(type)){
                return;
            }
            registerError(lockExpression);
        }
    }
}

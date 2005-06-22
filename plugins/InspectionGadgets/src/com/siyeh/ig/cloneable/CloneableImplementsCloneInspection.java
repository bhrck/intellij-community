package com.siyeh.ig.cloneable;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.psiutils.CloneUtils;
import com.siyeh.ig.ui.SingleCheckboxOptionsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class CloneableImplementsCloneInspection extends ClassInspection {
    /** @noinspection PublicField*/
    public boolean m_ignoreCloneableDueToInheritance = false;

    public String getID(){
        return "CloneableClassWithoutClone";
    }
    public String getDisplayName() {
        return "Cloneable class without 'clone()'";
    }

    public String getGroupDisplayName() {
        return GroupNames.CLONEABLE_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "#ref doesn't define clone() #loc";
    }

    public JComponent createOptionsPanel() {
        return new SingleCheckboxOptionsPanel("Ignore classes cloneable due to inheritance",
                this, "m_ignoreCloneableDueToInheritance");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new CloneableDefinesCloneVisitor();
    }

    private class CloneableDefinesCloneVisitor extends BaseInspectionVisitor {


        public void visitClass(@NotNull PsiClass aClass) {
            // no call to super, so it doesn't drill down
            if (aClass.isInterface()  || aClass.isAnnotationType()
                    || aClass.isEnum()) {
                return;
            }
            if(aClass instanceof PsiTypeParameter ||
                    aClass instanceof PsiAnonymousClass){
                return;
            }
            if (m_ignoreCloneableDueToInheritance) {
                if (!CloneUtils.isDirectlyCloneable(aClass)) {
                    return;
                }
            } else {
                if (!CloneUtils.isCloneable(aClass)) {
                    return;
                }
            }
            final PsiMethod[] methods = aClass.getMethods();
            for(final PsiMethod method : methods){
                if(CloneUtils.isClone(method)){
                    return;
                }
            }
            registerClassError(aClass);
        }

    }

}

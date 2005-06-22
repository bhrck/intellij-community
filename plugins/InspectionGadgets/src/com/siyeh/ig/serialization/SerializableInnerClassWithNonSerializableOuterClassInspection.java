package com.siyeh.ig.serialization;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifier;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.ClassInspection;
import com.siyeh.ig.psiutils.SerializationUtils;
import com.siyeh.ig.ui.SingleCheckboxOptionsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class SerializableInnerClassWithNonSerializableOuterClassInspection extends ClassInspection {
    /** @noinspection PublicField*/
    public boolean m_ignoreSerializableDueToInheritance = true;

    public String getDisplayName() {
        return "Serializable non-static inner class without non-Serializable outer class";
    }

    public String getGroupDisplayName() {
        return GroupNames.SERIALIZATION_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Inner class #ref is serializable while its outer class is not #loc";
    }

    public JComponent createOptionsPanel() {
        return new SingleCheckboxOptionsPanel("Ignore classes serializable due to inheritance",
                this, "m_ignoreSerializableDueToInheritance");
    }

    public BaseInspectionVisitor buildVisitor() {
        return new SerializableDefinesSerialVersionUIDVisitor();
    }

    private class SerializableDefinesSerialVersionUIDVisitor extends BaseInspectionVisitor {

        public void visitClass(@NotNull PsiClass aClass) {
            // no call to super, so it doesn't drill down

            if (aClass.isInterface() || aClass.isAnnotationType()) {
                return;
            }
            final PsiClass containingClass = aClass.getContainingClass();
            if(containingClass==null)
            {
                return;
            }
            if(aClass.hasModifierProperty(PsiModifier.STATIC))
            {
                return;
            }
            if (m_ignoreSerializableDueToInheritance) {
                if (!SerializationUtils.isDirectlySerializable(aClass)) {
                    return;
                }
            } else {
                if (!SerializationUtils.isSerializable(aClass)) {
                    return;
                }
            }
            if (SerializationUtils.isSerializable(containingClass)) {
                return;
            }
            registerClassError(aClass);
        }

    }

}

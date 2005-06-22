package com.siyeh.ig.controlflow;

import com.intellij.codeInsight.daemon.GroupNames;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiIfStatement;
import com.intellij.psi.PsiStatement;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.StatementInspection;
import com.siyeh.ig.StatementInspectionVisitor;
import com.siyeh.ig.ui.SingleIntegerFieldOptionsPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class IfStatementWithTooManyBranchesInspection extends StatementInspection {
    private static final int DEFAULT_BRANCH_LIMIT = 3;

    /** @noinspection PublicField*/
    public int m_limit = DEFAULT_BRANCH_LIMIT;  //this is public for the DefaultJDOMExternalizer thingy

    public String getDisplayName() {
        return "'if' statement with too many branches";
    }

    public String getGroupDisplayName() {
        return GroupNames.CONTROL_FLOW_GROUP_NAME;
    }

    private int getLimit() {
        return m_limit;
    }

    public JComponent createOptionsPanel() {
        return new SingleIntegerFieldOptionsPanel("Maximum number of branches:",
                this, "m_limit");
    }

    protected String buildErrorString(PsiElement location) {
        final PsiIfStatement statement = (PsiIfStatement) location.getParent();
        final int branches = calculateNumBranches(statement);
        return "'#ref' has too many branches (" + branches + ") #loc";
    }

    private int calculateNumBranches(PsiIfStatement statement) {
        final PsiStatement branch = statement.getElseBranch();
        if (branch == null) {
            return 1;
        }
        if (!(branch instanceof PsiIfStatement)) {
            return 2;
        }
        return 1 + calculateNumBranches((PsiIfStatement) branch);
    }

    public BaseInspectionVisitor buildVisitor() {
        return new IfStatementWithTooManyBranchesVisitor();
    }

    private class IfStatementWithTooManyBranchesVisitor extends StatementInspectionVisitor {

        public void visitIfStatement(@NotNull PsiIfStatement statement) {
            super.visitIfStatement(statement);
            final PsiElement parent = statement.getParent();
            if (parent instanceof PsiIfStatement) {
                final PsiIfStatement parentStatement = (PsiIfStatement) parent;
                final PsiStatement elseBranch = parentStatement.getElseBranch();
                if (statement.equals(elseBranch)) {
                    return;
                }
            }
            final int branches = calculateNumBranches(statement);
            if (branches <= getLimit()) {
                return;
            }
            registerStatementError(statement);
        }

    }
}

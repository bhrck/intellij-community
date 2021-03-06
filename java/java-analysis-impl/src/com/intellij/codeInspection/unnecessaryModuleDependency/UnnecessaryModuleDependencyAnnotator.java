package com.intellij.codeInspection.unnecessaryModuleDependency;

import com.intellij.codeInspection.reference.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.InheritanceUtil;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class UnnecessaryModuleDependencyAnnotator extends RefGraphAnnotator {
  public static final Key<Set<Module>> DEPENDENCIES = Key.create("inspection.dependencies");

  private final RefManager myManager;

  public UnnecessaryModuleDependencyAnnotator(final RefManager manager) {
    myManager = manager;
  }

  @Override
  public void onMarkReferenced(PsiElement what, PsiElement from, boolean referencedFromClassInitializer) {
    onReferenced(what, from);
  }

  @Override
  public void onMarkReferenced(RefElement refWhat, RefElement refFrom, boolean referencedFromClassInitializer) {
    final PsiElement onElement = refWhat.getElement();
    final PsiElement fromElement = refFrom.getElement();
    onReferenced(onElement, fromElement);
  }

  @Override
  public void onInitialize(RefElement refElement) {
    if (refElement instanceof RefClass) {
      PsiElement currentClass = refElement.getElement();
      if (currentClass instanceof PsiClass) {
        LinkedHashSet<PsiClass> superClasses = new LinkedHashSet<>();
        InheritanceUtil.getSuperClasses((PsiClass)currentClass, superClasses, false);
        for (PsiClass superClass : superClasses) {
          onReferenced(superClass, currentClass);
        }
      }
    }
  }

  private void onReferenced(PsiElement onElement, PsiElement fromElement) {
    if (onElement != null && fromElement!= null){
      final Module onModule = ModuleUtilCore.findModuleForPsiElement(onElement);
      final Module fromModule = ModuleUtilCore.findModuleForPsiElement(fromElement);
      if (onModule != null && fromModule != null && onModule != fromModule){
        final RefModule refModule = myManager.getRefModule(fromModule);
        if (refModule != null) {
          Set<Module> modules = refModule.getUserData(DEPENDENCIES);
          if (modules == null){
            modules = new HashSet<>();
            refModule.putUserData(DEPENDENCIES, modules);
          }
          modules.add(onModule);
        }
      }
    }
  }
}

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.tests.gui.fixtures.newProjectWizard;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.roots.ui.configuration.JdkComboBox;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.tests.gui.fixtures.FrameworksTreeFixture;
import com.intellij.tests.gui.fixtures.SelectSdkDialogFixture;
import com.intellij.tests.gui.framework.GuiTests;
import com.intellij.ui.components.JBList;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JListFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.fest.swing.timing.Condition;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

import static org.fest.swing.timing.Pause.pause;

public class NewProjectWizardFixture extends AbstractWizardFixture<NewProjectWizardFixture> {
  @NotNull
  public static NewProjectWizardFixture find(@NotNull Robot robot) {
    JDialog dialog = robot.finder().find(new GenericTypeMatcher<JDialog>(JDialog.class) {
      @Override
      protected boolean isMatching(@NotNull JDialog dialog) {
        return IdeBundle.message("title.new.project").equals(dialog.getTitle()) && dialog.isShowing();
      }
    });
    return new NewProjectWizardFixture(robot, dialog);
  }

  private NewProjectWizardFixture(@NotNull Robot robot, @NotNull JDialog target) {
    super(NewProjectWizardFixture.class, robot, target);
  }

  @NotNull
  public boolean isJdkEmpty(){
    final JdkComboBox jdkComboBox = robot().finder().findByType(JdkComboBox.class);
    return (jdkComboBox.getSelectedJdk() == null);
  }

  @NotNull
  public NewProjectWizardFixture selectProjectType(String projectTypeName){
    JListFixture projectTypeList = new JListFixture(robot(), robot().finder().findByType(JBList.class, true));
    projectTypeList.clickItem(projectTypeName);
    return this;
  }

  @NotNull
  public NewProjectWizardFixture selectFramework(String frameworkName){
    final FrameworksTreeFixture frameworksTreeFixture = FrameworksTreeFixture.find(robot());
    frameworksTreeFixture.selectFramework(frameworkName);
    return this;
  }

  public NewProjectWizardFixture selectSdkPath(@NotNull File sdkPath, String sdkType){
    final SelectSdkDialogFixture sdkDialogFixture = SelectSdkDialogFixture.find(robot(), sdkType);
    sdkDialogFixture.selectPathToSdk(sdkPath).clickOk();
    pause(new Condition("Waiting for the returning of focus to dialog: " + target().getTitle()) {
      @Override
      public boolean test() {
        return target().isFocused();
      }
    }, GuiTests.SHORT_TIMEOUT);
    return this;
  }

  @NotNull
  public ConfigureAndroidProjectStepFixture getConfigureAndroidProjectStep() {
    JRootPane rootPane = findStepWithTitle("Configure your new project");
    return new ConfigureAndroidProjectStepFixture(robot(), rootPane);
  }

  @NotNull
  public ConfigureFormFactorStepFixture getConfigureFormFactorStep() {
    JRootPane rootPane = findStepWithTitle("Select the form factors your app will run on");
    return new ConfigureFormFactorStepFixture(robot(), rootPane);
  }

  @NotNull
  public ChooseOptionsForNewFileStepFixture getChooseOptionsForNewFileStep() {
    JRootPane rootPane = findStepWithTitle("Customize the Activity");
    return new ChooseOptionsForNewFileStepFixture(robot(), rootPane);
  }
}

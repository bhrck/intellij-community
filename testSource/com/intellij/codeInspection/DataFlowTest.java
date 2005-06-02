/*
 * Created by IntelliJ IDEA.
 * User: max
 * Date: Apr 11, 2002
 * Time: 6:50:50 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.intellij.codeInspection;

import com.intellij.codeInspection.dataFlow.DataFlowInspection;
import com.intellij.codeInspection.ex.LocalInspectionToolWrapper;

public class DataFlowTest extends InspectionTestCase {
  private void doTest() throws Exception {
    final LocalInspectionToolWrapper tool = new LocalInspectionToolWrapper(new DataFlowInspection());
    tool.initialize(getManager());
    doTest("dataFlow/" + getTestName(false),
           tool);
  }

  private void doTest15() throws Exception {
    final LocalInspectionToolWrapper tool = new LocalInspectionToolWrapper(new DataFlowInspection());
    tool.initialize(getManager());
    doTest("dataFlow/" + getTestName(false),
           tool, "java 1.5");
  }

  public void testnpe1() throws Exception {
    doTest();
  }

  public void testcaseAndNpe() throws Exception {
    doTest();
  }

  public void testcce() throws Exception {
    doTest();
  }

  public void testexceptionCFG() throws Exception {
    doTest();
  }

  public void testinst() throws Exception {
    doTest();
  }

  public void testwrongEqualTypes() throws Exception {
    doTest();
  }

  public void testSCR13702() throws Exception {
    doTest();
  }

  public void testSCR13626() throws Exception {
    doTest();
  }

  public void testSCR13871() throws Exception {
    doTest();
  }

  public void testInstanceof() throws Exception {
    doTest();
  }

  public void testorBug() throws Exception {
    doTest();
  }

  public void testSCR14819() throws Exception {
    doTest();
  }

  public void testSCR14314() throws Exception {
    doTest();
  }

  public void testSCR15162() throws Exception {
    doTest();
  }

  public void testCatchParameterCantBeNull() throws Exception {
    doTest();
  }

  public void testxor() throws Exception {
    doTest();
  }

  public void testGenericInstanceof() throws Exception {
    doTest();
  }

  public void testthisInstanceof() throws Exception {
    doTest();
  }

  public void testandEq() throws Exception {
    doTest();
  }

  public void testnullableField() throws Exception {
    doTest();
  }

  public void testSCR39950() throws Exception {
    doTest();
  }

  public void testscrIDEA1() throws Exception {
    doTest();
  }
  public void testSCR18186() throws Exception {
    doTest();
  }
  //public void testSCR15406() throws Exception {
  //  doTest();
  //}
  public void testconstantExpr() throws Exception {
    doTest();
  }

  public void testNotNullable() throws Exception {
    doTest15();
  }

  public void testNotNullableParameter() throws Exception {
    doTest15();
  }

  public void testNotNullableParameter2() throws Exception {
    doTest15();
  }

  public void testNullable() throws Exception {
    doTest15();
  }
  public void testNullableThroughCast() throws Exception {
    doTest15();
  }
  public void testNullableThroughVariable() throws Exception {
    doTest15();
  }
  public void testNullableThroughVariableShouldNotBeReported() throws Exception {
    doTest15();
  }
  public void testNullableAssignment() throws Exception {
    doTest15();
  }

  public void testNullableLocalVariable() throws Exception {
    doTest15();
  }

  public void testNotNullLocalVariable() throws Exception {
    doTest15();
  }

  public void testNullableReturn() throws Exception {
    doTest15();
  }
}

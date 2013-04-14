package org.mechsoul.stremplate;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({ StringTemplateTest.class, StringExpressionTest.class, StringTemplateMapperTest.class })
@RunWith(Suite.class)
public class TestSuite {

}

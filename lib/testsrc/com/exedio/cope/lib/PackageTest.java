
package com.exedio.cope.lib;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


public class PackageTest extends TestCase
{

	public PackageTest(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite();
		suite.addTest(new TestSuite(TrimTest.class));
		suite.addTest(new TestSuite(Id2PkTest.class));
		suite.addTest(new TestSuite(ModelTest.class));
		suite.addTest(new TestSuite(ItemTest.class));
		
		suite.addTest(new TestSuite(AttributeStringTest.class));
		suite.addTest(new TestSuite(AttributeIntegerTest.class));
		suite.addTest(new TestSuite(AttributeLongTest.class));
		suite.addTest(new TestSuite(AttributeDoubleTest.class));
		suite.addTest(new TestSuite(AttributeBooleanTest.class));
		suite.addTest(new TestSuite(AttributeDateTest.class));
		suite.addTest(new TestSuite(AttributeItemTest.class));
		suite.addTest(new TestSuite(AttributeEnumerationTest.class));
		suite.addTest(new TestSuite(AttributeMediaTest.class));
		suite.addTest(new TestSuite(AttributeQualifiedTest.class));
		
		suite.addTest(new TestSuite(StringTest.class));
		suite.addTest(new TestSuite(MediaTest.class));
		suite.addTest(new TestSuite(NullEmptyTest.class));
		suite.addTest(new TestSuite(UniqueItemTest.class));
		suite.addTest(new TestSuite(HierarchyTest.class));
		suite.addTest(new TestSuite(SearchTest.class));
		suite.addTest(new TestSuite(SumTest.class));
		suite.addTest(new TestSuite(SumOrderTest.class));
		suite.addTest(new TestSuite(OrderByTest.class));
		suite.addTest(new TestSuite(SelectTest.class));
		suite.addTest(new TestSuite(FunctionTest.class));
		suite.addTest(new TestSuite(QualifierTest.class));
		suite.addTest(new TestSuite(LiteralConditionTest.class));
		suite.addTest(new TestSuite(JoinTest.class));
		suite.addTest(new TestSuite(JoinOuterTest.class));
		suite.addTest(new TestSuite(FindByIDTest.class));
		suite.addTest(new TestSuite(ReportTest.class));
		suite.addTest(new TestSuite(StatementInfoTest.class));
		return suite;
	}

}


package com.exedio.cope.lib;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.exedio.cope.lib.hierarchy.HierarchyTest;

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
		suite.addTest(new TestSuite(AttributesTest.class));
		suite.addTest(new TestSuite(StringTest.class));
		suite.addTest(new TestSuite(MediaTest.class));
		suite.addTest(new TestSuite(NullEmptyTest.class));
		suite.addTest(new TestSuite(UniqueItemTest.class));
		suite.addTest(new TestSuite(HierarchyTest.class));
		suite.addTest(new TestSuite(SearchTest.class));
		suite.addTest(new TestSuite(QualifierTest.class));
		suite.addTest(new TestSuite(LiteralConditionTest.class));
		suite.addTest(new TestSuite(JoinTest.class));
		suite.addTest(new TestSuite(FindByIDTest.class));
		return suite;
	}

}

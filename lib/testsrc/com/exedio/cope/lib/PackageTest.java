
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
		suite.addTest(new TestSuite(ItemTest.class));
		suite.addTest(new TestSuite(LibTest.class));
		return suite;
	}

}

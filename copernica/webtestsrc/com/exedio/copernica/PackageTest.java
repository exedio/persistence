package com.exedio.copernica;

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
		suite.addTest(new TestSuite(AuthorizationTest.class));
		suite.addTest(new TestSuite(WebTest.class));
		suite.addTest(new TestSuite(StringTest.class));
		suite.addTest(new TestSuite(SaveButtonExistTest.class));
		return suite;
	}

}

package com.exedio.copernica;

import net.sourceforge.jwebunit.WebTestCase;

public class AbstractWebTest extends WebTestCase
{

	public AbstractWebTest(String name)
	{
		super(name);
	}

	public void setUp() throws Exception
	{
		super.setUp();
		getTestContext().setBaseUrl("http://127.0.0.1:8080/copetest-hsqldb/");
		beginAt("admin.jsp");
		submit("CREATE");
	}
	
	public void tearDown() throws Exception
	{
		beginAt("admin.jsp");
		submit("DROP");
		super.tearDown();
	}
	
}

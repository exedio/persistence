package com.exedio.copernica;

import net.sourceforge.jwebunit.TestContext;
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
		final TestContext ctx = getTestContext();
		ctx.setBaseUrl("http://127.0.0.1:8080/copetest-hsqldb/");
		ctx.setAuthorization("admin", "nimda");
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

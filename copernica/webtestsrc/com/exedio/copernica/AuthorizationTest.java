
package com.exedio.copernica;

import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.WebTestCase;

import com.meterware.httpunit.AuthorizationRequiredException;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebRequest;

public class AuthorizationTest extends WebTestCase
{
	public void setUp() throws Exception
	{
		super.setUp();
		final TestContext ctx = getTestContext();
		ctx.setBaseUrl("http://127.0.0.1:8080/copetest-hsqldb/");
		beginAt("admin.jsp");
		submit("CREATE");
	}
	
	public void tearDown() throws Exception
	{
		beginAt("admin.jsp");
		submit("DROP");
		super.tearDown();
	}
	
	public void testNoUser() throws Exception
	{
		assertAuthorizationFails(null, null);
		assertAuthorizationFails("", "nimda");
		assertAuthorizationFails("noadmin", "nimda");
		assertAuthorizationFails("admin", "");
		assertAuthorizationFails("admin", "nonimda");
	}

	public void assertAuthorizationFails(final String login, final String password) throws Exception
	{
		final WebClient client = getTestContext().getWebClient();
		if(login!=null)
			client.setAuthorization(login, password);
		final WebRequest request = new GetMethodWebRequest("http://127.0.0.1:8080/copetest-hsqldb/copernica.jsp");
		try
		{
			client.sendRequest(request);
		}
		catch(AuthorizationRequiredException e)
		{
			assertEquals("Basic authentication required: realm=\"Copernica\"", e.getMessage());
		}
	}

}

/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.copernica;

import net.sourceforge.jwebunit.TestContext;
import net.sourceforge.jwebunit.WebTestCase;

import com.meterware.httpunit.AuthorizationRequiredException;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebClient;
import com.meterware.httpunit.WebRequest;

public class AuthorizationTest extends WebTestCase
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		final TestContext ctx = getTestContext();
		ctx.setBaseUrl("http://127.0.0.1:8080/copetest-hsqldb/");
		beginAt("console?t=schema");
		submit("CREATE");
	}
	
	@Override
	public void tearDown() throws Exception
	{
		beginAt("console?t=schema");
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

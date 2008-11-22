/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.pattern;

import java.util.Arrays;
import java.util.Date;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public class PasswordRecoveryTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(PasswordRecoveryItem.TYPE);
	private final int EXPIRY_MILLIS = 60*1000;
	private final int SMALL_EXPIRY_MILLIS = 1;
	
	public PasswordRecoveryTest()
	{
		super(MODEL);
	}
	
	PasswordRecoveryItem i;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		i = deleteOnTearDown(new PasswordRecoveryItem("oldpass"));
	}
	
	public void testIt() throws Exception
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				i.TYPE.getThis(),
				i.password,
				i.password.getStorage(),
				i.passwordRecovery,
				i.passwordRecovery.getToken(),
				i.passwordRecovery.getExpires(),
			}), i.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				i.TYPE.getThis(),
				i.password,
				i.password.getStorage(),
				i.passwordRecovery,
				i.passwordRecovery.getToken(),
				i.passwordRecovery.getExpires(),
			}), i.TYPE.getDeclaredFeatures());
		
		assertEquals(i.TYPE, i.password.getType());
		assertEquals(i.TYPE, i.passwordRecovery.getToken().getType());
		assertEquals(i.TYPE, i.passwordRecovery.getExpires().getType());
		assertEquals("password", i.password.getName());
		assertEquals("passwordRecoveryToken", i.passwordRecovery.getToken().getName());
		assertEquals("passwordRecoveryExpires", i.passwordRecovery.getExpires().getName());
		
		assertEquals(list(i.passwordRecovery.getToken(), i.passwordRecovery.getExpires()), i.passwordRecovery.getSourceFields());
		assertEquals(i.passwordRecovery, i.passwordRecovery.getToken().getPattern());
		assertEquals(i.passwordRecovery, i.passwordRecovery.getExpires().getPattern());
		
		assertSame(i.password, i.passwordRecovery.getPassword());
		
		try
		{
			new PasswordRecovery(null);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("password must not be null", e.getMessage());
		}
		
		// test persistence
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryExpires());
		
		final Date before = new Date();
		final long token = i.issuePasswordRecovery(EXPIRY_MILLIS);
		final Date after = new Date();
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		final Date expires = i.getPasswordRecoveryExpires();
		assertWithin(new Date(before.getTime() + EXPIRY_MILLIS), new Date(after.getTime() + EXPIRY_MILLIS), expires);
		
		assertEquals(null, i.redeemPasswordRecovery(token+1));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertEquals(expires, i.getPasswordRecoveryExpires());
		
		final String newPassword = i.redeemPasswordRecovery(token);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryExpires());
		
		assertEquals(null, i.redeemPasswordRecovery(token));
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryExpires());
		
		final Date beforeExpired = new Date();
		final long tokenExpired = i.issuePasswordRecovery(SMALL_EXPIRY_MILLIS);
		final Date afterExpired = new Date();
		Thread.sleep(SMALL_EXPIRY_MILLIS + 1);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(tokenExpired, i.getPasswordRecoveryToken());
		final Date expiresExpired = i.getPasswordRecoveryExpires();
		assertWithin(new Date(beforeExpired.getTime() + SMALL_EXPIRY_MILLIS), new Date(afterExpired.getTime() + SMALL_EXPIRY_MILLIS), expiresExpired);
		
		assertEquals(null, i.redeemPasswordRecovery(tokenExpired));
		assertTrue(i.checkPassword(newPassword));
		assertEquals(tokenExpired, i.getPasswordRecoveryToken());
		assertEquals(expiresExpired, i.getPasswordRecoveryExpires());
		
		try
		{
			i.issuePasswordRecovery(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expiryMillis must be greater zero, but was 0", e.getMessage());
		}
		try
		{
			i.redeemPasswordRecovery(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not a valid token: 0", e.getMessage());
		}
	}
}

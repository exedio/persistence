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
				i.passwordRecovery.getDate(),
			}), i.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				i.TYPE.getThis(),
				i.password,
				i.password.getStorage(),
				i.passwordRecovery,
				i.passwordRecovery.getToken(),
				i.passwordRecovery.getDate(),
			}), i.TYPE.getDeclaredFeatures());
		
		assertEquals(i.TYPE, i.password.getType());
		assertEquals(i.TYPE, i.passwordRecovery.getToken().getType());
		assertEquals(i.TYPE, i.passwordRecovery.getDate().getType());
		assertEquals("password", i.password.getName());
		assertEquals("passwordRecoveryToken", i.passwordRecovery.getToken().getName());
		assertEquals("passwordRecoveryDate", i.passwordRecovery.getDate().getName());
		
		assertEquals(list(i.passwordRecovery.getToken(), i.passwordRecovery.getDate()), i.passwordRecovery.getSourceFields());
		assertEquals(i.passwordRecovery, i.passwordRecovery.getToken().getPattern());
		assertEquals(i.passwordRecovery, i.passwordRecovery.getDate().getPattern());
		
		assertSame(i.password, i.passwordRecovery.getPassword());
		assertEquals(15*60*1000, i.passwordRecovery.getExpiryMillis());
		assertEquals(15,         i.passwordRecovery.getExpiryMinutes());
		
		try
		{
			new PasswordRecovery(null, 1);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("password must not be null", e.getMessage());
		}
		try
		{
			new PasswordRecovery(i.password, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expiryMillis must be greater zero, but was 0", e.getMessage());
		}
		
		// test persistence
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryDate());
		
		final Date before = new Date();
		final long token = i.issuePasswordRecovery();
		final Date after = new Date();
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertWithin(before, after, i.getPasswordRecoveryDate());
		
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertWithin(before, after, i.getPasswordRecoveryDate());
		
		assertEquals(null, i.redeemPasswordRecovery(token+1));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertWithin(before, after, i.getPasswordRecoveryDate());
		
		final String newPassword = i.redeemPasswordRecovery(token);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryDate());
		
		assertEquals(null, i.redeemPasswordRecovery(token));
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryDate());
		
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

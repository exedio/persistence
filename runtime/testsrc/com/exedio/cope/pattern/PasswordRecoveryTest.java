/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
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
	
	PasswordRecoveryItem c;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		c = deleteOnTearDown(new PasswordRecoveryItem("oldpass"));
	}
	
	public void testIt() throws Exception
	{
		// test model
		assertEquals(Arrays.asList(new Feature[]{
				c.TYPE.getThis(),
				c.password,
				c.password.getStorage(),
				c.passwordRecovery,
				c.passwordRecovery.getToken(),
				c.passwordRecovery.getDate(),
			}), c.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				c.TYPE.getThis(),
				c.password,
				c.password.getStorage(),
				c.passwordRecovery,
				c.passwordRecovery.getToken(),
				c.passwordRecovery.getDate(),
			}), c.TYPE.getDeclaredFeatures());
		
		assertEquals(c.TYPE, c.password.getType());
		assertEquals(c.TYPE, c.passwordRecovery.getToken().getType());
		assertEquals(c.TYPE, c.passwordRecovery.getDate().getType());
		assertEquals("password", c.password.getName());
		assertEquals("passwordRecoveryToken", c.passwordRecovery.getToken().getName());
		assertEquals("passwordRecoveryDate", c.passwordRecovery.getDate().getName());
		
		assertEquals(list(c.passwordRecovery.getToken(), c.passwordRecovery.getDate()), c.passwordRecovery.getSourceFields());
		assertEquals(c.passwordRecovery, c.passwordRecovery.getToken().getPattern());
		assertEquals(c.passwordRecovery, c.passwordRecovery.getDate().getPattern());
		
		assertSame(c.password, c.passwordRecovery.getPassword());
		assertEquals(15*60*1000, c.passwordRecovery.getExpiryMillis());
		
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
			new PasswordRecovery(c.password, 0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("expiryMillis must be greater zero, but was 0", e.getMessage());
		}
		
		// test persistence
		assertTrue(c.checkPassword("oldpass"));
		assertEquals(0, c.getPasswordRecoveryToken());
		assertEquals(null, c.getPasswordRecoveryDate());
		
		final Date before = new Date();
		final long token = c.createPasswordRecoveryToken();
		final Date after = new Date();
		assertTrue(c.checkPassword("oldpass"));
		assertEquals(token, c.getPasswordRecoveryToken());
		assertWithin(before, after, c.getPasswordRecoveryDate());
		
		assertTrue(c.checkPassword("oldpass"));
		assertEquals(token, c.getPasswordRecoveryToken());
		assertWithin(before, after, c.getPasswordRecoveryDate());
		
		assertEquals(null, c.tryPasswordRecovery(token+1));
		assertTrue(c.checkPassword("oldpass"));
		assertEquals(token, c.getPasswordRecoveryToken());
		assertWithin(before, after, c.getPasswordRecoveryDate());
		
		final String newPassword = c.tryPasswordRecovery(token);
		assertNotNull(newPassword);
		assertTrue(c.checkPassword(newPassword));
		assertEquals(0, c.getPasswordRecoveryToken());
		assertEquals(null, c.getPasswordRecoveryDate());
		
		assertEquals(null, c.tryPasswordRecovery(token));
		assertNotNull(newPassword);
		assertTrue(c.checkPassword(newPassword));
		assertEquals(0, c.getPasswordRecoveryToken());
		assertEquals(null, c.getPasswordRecoveryDate());
		
		try
		{
			c.tryPasswordRecovery(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not a valid token: 0", e.getMessage());
		}
	}
}

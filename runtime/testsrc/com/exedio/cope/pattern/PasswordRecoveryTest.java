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
		final long token = i.createPasswordRecoveryToken();
		final Date after = new Date();
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertWithin(before, after, i.getPasswordRecoveryDate());
		
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertWithin(before, after, i.getPasswordRecoveryDate());
		
		assertEquals(null, i.tryPasswordRecovery(token+1));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(token, i.getPasswordRecoveryToken());
		assertWithin(before, after, i.getPasswordRecoveryDate());
		
		final String newPassword = i.tryPasswordRecovery(token);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryDate());
		
		assertEquals(null, i.tryPasswordRecovery(token));
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertEquals(0, i.getPasswordRecoveryToken());
		assertEquals(null, i.getPasswordRecoveryDate());
		
		try
		{
			i.tryPasswordRecovery(0);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("not a valid token: 0", e.getMessage());
		}
	}
}

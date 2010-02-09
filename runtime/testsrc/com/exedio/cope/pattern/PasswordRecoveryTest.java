/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.PasswordRecovery.Token;
import com.exedio.cope.util.Interrupter;

public class PasswordRecoveryTest extends AbstractRuntimeTest
{
	private static final Model MODEL = new Model(PasswordRecoveryItem.TYPE);
	
	static
	{
		MODEL.enableSerialization(PasswordRecoveryTest.class, "MODEL");
	}
	
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
		assertEquals(Arrays.asList(new Type[]{
				i.TYPE,
				i.passwordRecovery.getTokenType(),
		}), MODEL.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				i.TYPE.getThis(),
				i.password,
				i.password.getStorage(),
				i.passwordRecovery,
			}), i.TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				i.TYPE.getThis(),
				i.password,
				i.password.getStorage(),
				i.passwordRecovery,
			}), i.TYPE.getDeclaredFeatures());
		
		assertEquals(i.TYPE, i.password.getType());
		assertEquals(i.passwordRecovery.getTokenType(), i.passwordRecovery.getSecret().getType());
		assertEquals(i.passwordRecovery.getTokenType(), i.passwordRecovery.getExpires().getType());
		assertEquals("password", i.password.getName());
		assertEquals("secret", i.passwordRecovery.getSecret().getName());
		assertEquals("expires", i.passwordRecovery.getExpires().getName());
		
		assertEquals(list(), i.passwordRecovery.getSourceFields());
		assertEquals(null, i.passwordRecovery.getSecret().getPattern());
		assertEquals(null, i.passwordRecovery.getExpires().getPattern());
		
		assertSame(i.password, i.passwordRecovery.getPassword());
		
		assertFalse(i.password                       .isAnnotationPresent(Computed.class));
		assertFalse(i.passwordRecovery               .isAnnotationPresent(Computed.class));
		assertTrue (i.passwordRecovery.getTokenType().isAnnotationPresent(Computed.class));
		
		assertSerializedSame(i.passwordRecovery, 406);
		
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
		final int EXPIRY_MILLIS = 60*1000;
		
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), i.passwordRecovery.getTokenType().search());
		
		final Date before = new Date();
		final Token token = i.issuePasswordRecovery(EXPIRY_MILLIS);
		final Date after = new Date();
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertWithin(new Date(before.getTime() + EXPIRY_MILLIS), new Date(after.getTime() + EXPIRY_MILLIS), expires);
		assertEquals(list(token), i.passwordRecovery.getTokenType().search());
		
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret+1));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());
		
		final String newPassword = i.redeemPasswordRecovery(tokenSecret);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());
		
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());
		
		model.commit();
		assertEquals(0, i.purgePasswordRecovery(null));
		model.startTransaction("PasswordRecoveryTest");
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());
	}
	
	public void testExpired() throws Exception
	{
		final int EXPIRY_MILLIS = 1;
		
		final Date before = new Date();
		final Token token = i.issuePasswordRecovery(EXPIRY_MILLIS);
		final Date after = new Date();
		final long tokenSecret = token.getSecret();
		sleepLongerThan( EXPIRY_MILLIS );
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertWithin(new Date(before.getTime() + EXPIRY_MILLIS), new Date(after.getTime() + EXPIRY_MILLIS), expires);
		assertEquals(list(token), i.passwordRecovery.getTokenType().search());
		
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());
		assertEquals(list(token), i.passwordRecovery.getTokenType().search());
		
		model.commit();
		assertEquals(1, i.purgePasswordRecovery(null));
		model.startTransaction("PasswordRecoveryTest");
		assertTrue(i.checkPassword("oldpass"));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());
		
		assertPurge(  0, 1);
		assertPurge(  1, 1);
		assertPurge(100, 2);
		assertPurge(101, 2);
		
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
			assertEquals("not a valid secret: 0", e.getMessage());
		}
	}
	
	public void assertPurge(final int tokenNumber, final int transactionNumber) throws Exception
	{
		final int EXPIRY_MILLIS = 1;
		final ArrayList<Token> tokens = new ArrayList<Token>();
		for(int n = 0; n<tokenNumber; n++)
			tokens.add(i.issuePasswordRecovery(EXPIRY_MILLIS));
		sleepLongerThan( EXPIRY_MILLIS );
		model.commit();
		final Interrupter interrupter = new Interrupter(){
			public boolean isRequested()
			{
				return false;
			}
		};
		final long t = model.getNextTransactionId();
		assertEquals(tokenNumber, i.purgePasswordRecovery(interrupter));
		assertEquals(t+transactionNumber, model.getNextTransactionId());
		model.startTransaction("PasswordRecoveryTest");
		for(final Token token : tokens)
			assertFalse(token.existsCopeItem());
	}

	/**
	 * This method will not return until the result of System.currentTimeMillis() has increased
	 * by the given amount of milli seconds.
	 */
	private void sleepLongerThan( long millis ) throws InterruptedException
	{
		long start = System.currentTimeMillis();
		// The loop double-checks that currentTimeMillis() really returns a sufficiently higher
		// value ... needed for Windows.
		do
		{
			Thread.sleep( millis+1 );
		}
		while ( (System.currentTimeMillis()-start)<=millis );
	}
}

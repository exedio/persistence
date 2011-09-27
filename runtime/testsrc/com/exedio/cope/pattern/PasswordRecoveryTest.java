/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.PasswordRecovery.Config;
import com.exedio.cope.pattern.PasswordRecovery.Token;

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
	PasswordLimiterMockClockSource clock;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		i = deleteOnTearDown(new PasswordRecoveryItem("oldpass"));
		clock = new PasswordLimiterMockClockSource();
		PasswordRecovery.clock.setSource(clock);
	}

	@Override
	protected void tearDown() throws Exception
	{
		PasswordRecovery.clock.removeSource();
		super.tearDown();
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

		assertEquals(list(), i.passwordRecovery.getSourceFeatures());
		assertEquals(null, i.passwordRecovery.getSecret().getPattern());
		assertEquals(i.passwordRecovery.getTokens(), i.passwordRecovery.getExpires().getPattern());

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
		catch(final NullPointerException e)
		{
			assertEquals("password", e.getMessage());
		}

		// test persistence
		final Config config = new Config(60*1000);

		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), i.passwordRecovery.getTokenType().search());

		final long issueTime = clock.addNow();
		final Token token = i.issuePasswordRecovery(config);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEquals(new Date(issueTime + config.getExpiryMillis()), expires);
		assertEquals(list(token), i.passwordRecovery.getTokenType().search());

		clock.addOffset(config.getExpiryMillis());
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret+1));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());

		clock.addNow();
		final String newPassword = i.redeemPasswordRecovery(tokenSecret);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(0, purge());
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());
	}

	public void testExpired()
	{
		final Config config = new Config(20);

		final long issueTime = clock.addNow();
		final Token token = i.issuePasswordRecovery(config);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEquals(new Date(issueTime + config.getExpiryMillis()), expires);
		assertEquals(list(token), i.passwordRecovery.getTokenType().search());

		clock.addOffset(config.getExpiryMillis() + 1);
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());
		assertEquals(list(token), i.passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(1, purge());
		assertTrue(i.checkPassword("oldpass"));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), i.passwordRecovery.getTokenType().search());

		try
		{
			i.redeemPasswordRecovery(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a valid secret: 0", e.getMessage());
		}
	}
	
	public void testReuse()
	{
		final Config config = new Config(15*60*1000, 10*1000);

		final long issueTime1 = clock.addNow();
		final Token token1 = i.issuePasswordRecovery(config);
		assertEquals(new Date(issueTime1 + config.getExpiryMillis()), token1.getExpires());
		assertContains(token1, i.passwordRecovery.getTokenType().search());
		
		clock.addNow();
		assertEquals(token1, i.issuePasswordRecovery(config));
		clock.assertEmpty();
		assertContains(token1, i.passwordRecovery.getTokenType().search());
		
		clock.addOffset(config.getReuseMillis());
		assertEquals(token1, i.issuePasswordRecovery(config));
		clock.assertEmpty();
		assertContains(token1, i.passwordRecovery.getTokenType().search());
		
		final long issueTime3 = clock.addOffset(1);
		final Token token3 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		assertFalse(token3.equals(token1));
		assertEquals(new Date(issueTime3 + config.getExpiryMillis()), token3.getExpires());
		assertContains(token1, token3, i.passwordRecovery.getTokenType().search());
		assertFalse(token3.equals(token1));
	}

	private final int purge()
	{
		final CountJobContext ctx = new CountJobContext();
		model.commit();
		i.purgePasswordRecovery(ctx);
		model.startTransaction("PasswordRecoveryTest");
		return ctx.progress;
	}

	@Deprecated
	public void testDeprecated()
	{
		try
		{
			i.issuePasswordRecovery(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expiryMillis must be greater zero, but was 0", e.getMessage());
		}
	}
}

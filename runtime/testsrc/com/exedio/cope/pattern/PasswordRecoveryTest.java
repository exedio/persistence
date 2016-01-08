/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.RuntimeAssert.assertSerializedSame;
import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.PasswordRecoveryItem.TYPE;
import static com.exedio.cope.pattern.PasswordRecoveryItem.password;
import static com.exedio.cope.pattern.PasswordRecoveryItem.passwordRecovery;
import static com.exedio.cope.pattern.PasswordRecoveryItem.purgePasswordRecovery;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.exedio.cope.ClockRule;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.Type;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.PasswordRecovery.Config;
import com.exedio.cope.pattern.PasswordRecovery.Token;
import java.util.Arrays;
import java.util.Date;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

public class PasswordRecoveryTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(TYPE);

	static
	{
		MODEL.enableSerialization(PasswordRecoveryTest.class, "MODEL");
	}

	public PasswordRecoveryTest()
	{
		super(MODEL);
	}

	private final ClockRule clockRule = new ClockRule();

	@Rule public final RuleChain ruleChain = RuleChain.outerRule(clockRule);

	PasswordRecoveryItem i;
	private final RelativeMockClockStrategy clock = new RelativeMockClockStrategy();

	@Before public final void setUp()
	{
		i = new PasswordRecoveryItem("oldpass");
		clockRule.override(clock);
	}

	@Test public void testIt() throws Exception
	{
		// test model
		assertEquals(Arrays.asList(new Type<?>[]{
				TYPE,
				passwordRecovery.getTokenType(),
		}), MODEL.getTypes());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordRecovery,
			}), TYPE.getFeatures());
		assertEquals(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				password,
				password.getStorage(),
				passwordRecovery,
			}), TYPE.getDeclaredFeatures());

		assertEquals(TYPE, password.getType());
		assertEquals(passwordRecovery.getTokenType(), passwordRecovery.getSecret().getType());
		assertEquals(passwordRecovery.getTokenType(), passwordRecovery.getExpires().getType());
		assertEquals("password", password.getName());
		assertEquals("secret", passwordRecovery.getSecret().getName());
		assertEquals("expires", passwordRecovery.getExpires().getName());

		assertEquals(list(), passwordRecovery.getSourceFeatures());
		assertEquals(null, passwordRecovery.getSecret().getPattern());
		assertEquals(passwordRecovery.getTokens(), passwordRecovery.getExpires().getPattern());

		assertSame(password, passwordRecovery.getPassword());

		assertFalse(password                       .isAnnotationPresent(Computed.class));
		assertFalse(passwordRecovery               .isAnnotationPresent(Computed.class));
		assertTrue (passwordRecovery.getTokenType().isAnnotationPresent(Computed.class));

		assertSerializedSame(passwordRecovery, 406);

		// test persistence
		assertNoUpdateCounterColumn(passwordRecovery.getTokenType());

		final Config config = new Config(60*1000);

		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), passwordRecovery.getTokenType().search());

		final long issueTime = clock.addNow();
		final Token token = i.issuePasswordRecovery(config);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEquals(new Date(issueTime + config.getExpiryMillis()), expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

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
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(0, purge());
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());
	}

	@Test public void testExpired()
	{
		final Config config = new Config(20);

		final long issueTime = clock.addNow();
		final Token token = i.issuePasswordRecovery(config);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEquals(new Date(issueTime + config.getExpiryMillis()), expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.addOffset(config.getExpiryMillis() + 1);
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(1, purge());
		assertTrue(i.checkPassword("oldpass"));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		try
		{
			i.redeemPasswordRecovery(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a valid secret: 0", e.getMessage());
		}
		try
		{
			i.issuePasswordRecovery(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}

	@Test public void testReuse()
	{
		final Config config = new Config(15*60*1000, 10*1000);

		final long issueTime1 = clock.addNow();
		final Token token1 = i.issuePasswordRecovery(config);
		assertEquals(new Date(issueTime1 + config.getExpiryMillis()), token1.getExpires());
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.addNow();
		assertEquals(token1, i.issuePasswordRecovery(config));
		clock.assertEmpty();
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.addOffset(config.getReuseMillis());
		assertEquals(token1, i.issuePasswordRecovery(config));
		clock.assertEmpty();
		assertContains(token1, passwordRecovery.getTokenType().search());

		final long issueTime3 = clock.addOffset(1);
		final Token token3 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		assertFalse(token3.equals(token1));
		assertEquals(new Date(issueTime3 + config.getExpiryMillis()), token3.getExpires());
		assertContains(token1, token3, passwordRecovery.getTokenType().search());
		assertFalse(token3.equals(token1));
	}

	@Test public void testNoReuse()
	{
		final Config config = new Config(15*60*1000, 0);

		final long issueTime1 = clock.addNow();
		final Token token1 = i.issuePasswordRecovery(config);
		assertEquals(new Date(issueTime1 + config.getExpiryMillis()), token1.getExpires());
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.addNow();
		final Token token3 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		assertFalse(token3.equals(token1));
		assertEquals(new Date(issueTime1 + config.getExpiryMillis()), token3.getExpires());
		assertContains(token1, token3, passwordRecovery.getTokenType().search());
		assertFalse(token3.equals(token1));
	}

	private final int purge()
	{
		final CountJobContext ctx = new CountJobContext();
		model.commit();
		purgePasswordRecovery(ctx);
		model.startTransaction("PasswordRecoveryTest");
		return ctx.progress;
	}

	@Deprecated
	@Test public void testDeprecated()
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

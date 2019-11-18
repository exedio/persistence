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

import static com.exedio.cope.SchemaInfoAssert.assertNoUpdateCounterColumn;
import static com.exedio.cope.pattern.PasswordRecoveryItem.passwordRecovery;
import static com.exedio.cope.pattern.PasswordRecoveryItem.purgePasswordRecovery;
import static com.exedio.cope.pattern.PasswordRecoveryModelTest.MODEL;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.list;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.Duration.ofSeconds;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.junit.AbsoluteMockClockStrategy;
import com.exedio.cope.misc.DeleteJobContext;
import com.exedio.cope.pattern.PasswordRecovery.Config;
import com.exedio.cope.pattern.PasswordRecovery.Token;
import com.exedio.cope.tojunit.ClockRule;
import com.exedio.cope.tojunit.MainRule;
import java.time.Duration;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@MainRule.Tag
public class PasswordRecoveryTest extends TestWithEnvironment
{
	public PasswordRecoveryTest()
	{
		super(MODEL);
	}

	private final AbsoluteMockClockStrategy clock = new AbsoluteMockClockStrategy();
	private final ClockRule clockRule = new ClockRule();

	PasswordRecoveryItem i;

	@BeforeEach final void setUp()
	{
		i = new PasswordRecoveryItem("oldpass");
		clockRule.override(clock);
	}

	@Test void testNoUpdateCounterColumn()
	{
		assertNoUpdateCounterColumn(passwordRecovery.getTokenType());
	}

	@Test void testGetValidTokenAndRedeemWithNewPassword()
	{
		final Config config = new Config(ofMinutes(1), ofSeconds(10));
		assertEquals(ofMinutes(1), config.getExpiry());
		assertEquals(ofSeconds(10), config.getReuse());

		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.333");
		final Token token = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEqualsDate("2005-05-12 13:12:22.333", expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(null, i.getValidPasswordRecoveryToken(tokenSecret+1));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		final Token aToken = i.getValidPasswordRecoveryToken(tokenSecret);
		assertEquals(token, aToken);
		clock.assertEmpty();
		redeemC.assertCount(1);
		clock.add("2005-05-12 13:12:22.333");  // at expiry
		final String newPassword = aToken.redeemAndSetNewPassword();
		clock.assertEmpty();
		setPasswordC.assertCount(1);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(null, i.getValidPasswordRecoveryToken(tokenSecret));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(0, purge());
		clock.assertEmpty();
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testGetValidTokenAndRedeem()
	{
		final Config config = new Config(ofMinutes(1), ofSeconds(10));
		assertEquals(ofMinutes(1), config.getExpiry());
		assertEquals(ofSeconds(10), config.getReuse());

		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.333");
		final Token token = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEqualsDate("2005-05-12 13:12:22.333", expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		final Token aToken = i.getValidPasswordRecoveryToken(tokenSecret);
		assertEquals(token, aToken);
		clock.assertEmpty();
		redeemC.assertCount(1);
		clock.add("2005-05-12 13:12:22.333"); // at expiry
		aToken.redeem();
		clock.assertEmpty();
		assertTrue(i.checkPassword("oldpass"));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(null, i.getValidPasswordRecoveryToken(tokenSecret));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(0, purge());
		clock.assertEmpty();
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testRedeem()
	{
		final Config config = new Config(ofMinutes(1), ofSeconds(10));
		assertEquals(ofMinutes(1), config.getExpiry());
		assertEquals(ofSeconds(10), config.getReuse());

		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.333");
		final Token token = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEqualsDate("2005-05-12 13:12:22.333", expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret+1));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		clock.add("2005-05-12 13:12:22.333"); // OK: this is for deleting tokens in Token#redeem
		final String newPassword = i.redeemPasswordRecovery(tokenSecret);
		clock.assertEmpty();
		redeemC.assertCount(1);
		setPasswordC.assertCount(1);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		assertEquals(0, purge());
		clock.assertEmpty();
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testGetExpiredToken()
	{
		final Config config = new Config(ofMillis(20), ofMillis(20));
		assertEquals(ofMillis(20), config.getExpiry());
		assertEquals(ofMillis(20), config.getReuse());

		clock.add("2005-05-12 13:11:22.333");
		final Token token = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEqualsDate("2005-05-12 13:11:22.353", expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.354"); // exactly after expiry
		assertEquals(null, i.getValidPasswordRecoveryToken(tokenSecret));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.354"); // exactly after expiry
		assertEquals(1, purge());
		clock.assertEmpty();
		assertTrue(i.checkPassword("oldpass"));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testExpiredRedeem()
	{
		final Config config = new Config(ofMillis(20), ofMillis(20));
		assertEquals(ofMillis(20), config.getExpiry());
		assertEquals(ofMillis(20), config.getReuse());

		clock.add("2005-05-12 13:11:22.333");
		final Token token = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEqualsDate("2005-05-12 13:11:22.353", expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.354"); // exactly after expiry
		assertEquals(null, i.redeemPasswordRecovery(tokenSecret));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertTrue(i.checkPassword("oldpass"));
		assertEquals(tokenSecret, token.getSecret());
		assertEquals(expires, token.getExpires());
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.354"); // exactly after expiry
		assertEquals(1, purge());
		clock.assertEmpty();
		assertTrue(i.checkPassword("oldpass"));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testPostponedRedemption()
	{
		final Config config = new Config(ofMinutes(1), ofSeconds(10));
		assertEquals(ofMinutes(1), config.getExpiry());
		assertEquals(ofSeconds(10), config.getReuse());

		assertTrue(i.checkPassword("oldpass"));
		assertEquals(list(), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.333");
		final Token token = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		final long tokenSecret = token.getSecret();
		assertTrue(i.checkPassword("oldpass"));
		final Date expires = token.getExpires();
		assertEqualsDate("2005-05-12 13:12:22.333", expires);
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.333"); // at expiry
		final Token aToken = i.getValidPasswordRecoveryToken(tokenSecret);
		assertEquals(token, aToken);
		clock.assertEmpty();
		redeemC.assertCount(1);
		clock.add("2005-05-12 13:12:22.353");  // exactly after expiry
		final String newPassword = aToken.redeemAndSetNewPassword();
		clock.assertEmpty();
		setPasswordC.assertCount(1);
		assertNotNull(newPassword);
		assertTrue(i.checkPassword(newPassword));
		assertTrue(token.existsCopeItem());
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.353"); // exactly after expiry
		assertEquals(null, i.getValidPasswordRecoveryToken(tokenSecret));
		clock.assertEmpty();
		redeemFailC.assertCount(1);
		assertTrue(i.checkPassword(newPassword));
		assertTrue(token.existsCopeItem());
		assertEquals(list(token), passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:12:22.353"); // at expiry
		assertEquals(1, purge());
		clock.assertEmpty();
		assertTrue(i.checkPassword(newPassword));
		assertFalse(token.existsCopeItem());
		assertEquals(list(), passwordRecovery.getTokenType().search());

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testGetValidTokenWithNotASecret()
	{
		try
		{
			i.getValidPasswordRecoveryToken(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("not a valid secret: 0", e.getMessage());
		}
	}

	@Test void testRedeemWithNotASecret()
	{
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

	@Test void testIssueWithNullConfig()
	{
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

	@Test void testReuse()
	{
		final Config config = new Config(ofMinutes(15), ofSeconds(10));
		assertEquals(ofMinutes(15), config.getExpiry());
		assertEquals(ofSeconds(10), config.getReuse());

		clock.add("2005-05-12 13:11:22.333");
		final Token token1 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		assertEqualsDate("2005-05-12 13:26:22.333", token1.getExpires());
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.333"); // same time
		assertEquals(token1, i.issuePasswordRecovery(config));
		clock.assertEmpty();
		issueReuseC.assertCount(1);
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:32.333"); // almost out of reuse
		assertEquals(token1, i.issuePasswordRecovery(config));
		clock.assertEmpty();
		issueReuseC.assertCount(1);
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:32.334"); // out of reuse
		final Token token3 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		assertNotEquals(token3, token1);
		assertEqualsDate("2005-05-12 13:26:32.334", token3.getExpires());
		assertContains(token1, token3, passwordRecovery.getTokenType().search());
		assertNotEquals(token3, token1);

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Test void testNoReuse()
	{
		final Config config = new Config(ofMinutes(15), Duration.ZERO);
		assertEquals(ofMinutes(15), config.getExpiry());
		assertEquals(Duration.ZERO, config.getReuse());

		clock.add("2005-05-12 13:11:22.333");
		final Token token1 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		assertEqualsDate("2005-05-12 13:26:22.333", token1.getExpires());
		assertContains(token1, passwordRecovery.getTokenType().search());

		clock.add("2005-05-12 13:11:22.333"); // same time
		final Token token3 = i.issuePasswordRecovery(config);
		clock.assertEmpty();
		issueC.assertCount(1);
		assertNotEquals(token3, token1);
		assertEqualsDate("2005-05-12 13:26:22.333", token3.getExpires());
		assertContains(token1, token3, passwordRecovery.getTokenType().search());
		assertNotEquals(token3, token1);

		issueC.assertCount(0);
		issueReuseC.assertCount(0);
		redeemC.assertCount(0);
		redeemFailC.assertCount(0);
		setPasswordC.assertCount(0);
	}

	@Deprecated
	@Test void testIssueDeprecated()
	{
		try
		{
			i.issuePasswordRecovery(0);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("expiry must be at least PT0.001S, but was PT0S", e.getMessage());
		}
	}

	private int purge()
	{
		final DeleteJobContext ctx = new DeleteJobContext(MODEL);
		model.commit();
		purgePasswordRecovery(ctx);
		model.startTransaction("PasswordRecoveryTest");
		return ctx.getProgress();
	}

	private void assertEqualsDate(final String expected, final Date actual)
	{
		clock.assertEqualsFormatted(expected, actual);
	}

	private final FeatureCounterTester issueC       = new FeatureCounterTester(passwordRecovery, "issue", "reuse", "no");
	private final FeatureCounterTester issueReuseC  = new FeatureCounterTester(passwordRecovery, "issue", "reuse", "yes");
	private final FeatureTimerTester   redeemC      = new FeatureTimerTester  (passwordRecovery, "redeem");
	private final FeatureCounterTester redeemFailC  = new FeatureCounterTester(passwordRecovery, "redeemFail");
	private final FeatureCounterTester setPasswordC = new FeatureCounterTester(passwordRecovery, "setPassword");
}

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

package com.exedio.cope.misc;

import static com.exedio.cope.misc.SlowTransactionLogger.run;
import static com.exedio.cope.misc.SlowTransactionLoggerTest.MODEL;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.TestSources.single;
import static java.time.Duration.ofSeconds;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.misc.SlowTransactionLogger.Properties;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import org.junit.jupiter.api.Test;

public class SlowTransactionLoggerUnconnectedTest
{
	@Test void modelNull()
	{
		assertFails(
				() -> run(null, null),
				NullPointerException.class,
				"model");
	}
	@Test void propertiesNull()
	{
		assertFails(
				() -> run(MODEL, null),
				NullPointerException.class,
				"properties");
	}
	@Test void normal()
	{
		run(MODEL, Properties.factory().create(Sources.EMPTY));
	}

	@Test void propsDefault()
	{
		final Properties p =  Properties.factory().create(Sources.EMPTY);
		assertEquals(ofSeconds(10), p.thresholdWarn);
		assertEquals(ofSeconds(30), p.thresholdError);
	}
	@Test void propsCustomWarn()
	{
		final Properties p =  Properties.factory().create(Sources.cascade(
				single("threshold.warn", ofSeconds(22))));
		assertEquals(ofSeconds(22), p.thresholdWarn);
		assertEquals(ofSeconds(30), p.thresholdError);
	}
	@Test void propsCustomWarnAboveErrorDefault()
	{
		final Properties p =  Properties.factory().create(Sources.cascade(
				single("threshold.warn", ofSeconds(31))));
		assertEquals(ofSeconds(31), p.thresholdWarn);
		assertEquals(ofSeconds(31), p.thresholdError);
	}
	@Test void propsCustom()
	{
		final Properties p =  Properties.factory().create(Sources.cascade(
				single("threshold.warn", ofSeconds(44)),
				single("threshold.error", ofSeconds(55))));
		assertEquals(ofSeconds(44), p.thresholdWarn);
		assertEquals(ofSeconds(55), p.thresholdError);
	}
	@Test void propsCustomSame()
	{
		final Properties p =  Properties.factory().create(Sources.cascade(
				single("threshold.warn", ofSeconds(34)),
				single("threshold.error", ofSeconds(34))));
		assertEquals(ofSeconds(34), p.thresholdWarn);
		assertEquals(ofSeconds(34), p.thresholdError);
	}
	@Test void propsCustomInvalid()
	{
		final Source s = Sources.cascade(
				single("threshold.warn", ofSeconds(34)),
				single("threshold.error", ofSeconds(33)));
		assertFails(
				() -> Properties.factory().create(s),
				IllegalPropertiesException.class,
				"property threshold.error " +
				"in threshold.warn=PT34S / threshold.error=PT33S " +
				"must be a duration greater or equal PT34S, " +
				"but was PT33S");
	}
}

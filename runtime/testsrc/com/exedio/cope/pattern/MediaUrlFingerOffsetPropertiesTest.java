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

import static com.exedio.cope.tojunit.EqualsAssert.assertEqualBits;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.util.IllegalPropertiesException;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import org.junit.jupiter.api.Test;

public class MediaUrlFingerOffsetPropertiesTest
{
	@Test void testDefault()
	{
		final Source source = source(null);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());
		assertEquals(true, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setValueAndResetRamp(55);
		assertEquals("55 (initially 0)", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setRamp(0.0);
		assertEquals("55 (initially 0)", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setRamp(1.0);
		assertEquals("55 (initially 0) ramp 999/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().reset();
		assertEquals("0", p.mediaFingerprintOffset().getInfo());
		assertEquals(true, p.mediaFingerprintOffset().isInitial());
	}

	@Test void testCustom()
	{
		final Source source = source(55);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
		assertEquals(true, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setValueAndResetRamp(0);
		assertEquals("0 (initially 55)", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setRamp(0.0);
		assertEquals("0 (initially 55)", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setRamp(1.0);
		assertEquals("0 (initially 55) ramp 999/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().reset();
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
		assertEquals(true, p.mediaFingerprintOffset().isInitial());
	}

	@Test void testRampFunction()
	{
		final Source source = source(0);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());
		assertEquals(true, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.000, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(1/1000d);
		assertEquals("0 ramp 1/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.001, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(2/1000d);
		assertEquals("0 ramp 2/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.002, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(33/1000d);
		assertEquals("0 ramp 33/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.033, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(450/1000d);
		assertEquals("0 ramp 450/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setRamp(997/1000d);
		assertEquals("0 ramp 997/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.997, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(998/1000d);
		assertEquals("0 ramp 998/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.998, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(999/1000d);
		assertEquals("0 ramp 999/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.999, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(0.99999000);
		assertEquals("0 ramp 999/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEqualBits(0.999, p.mediaFingerprintOffset().getRamp());
	}

	@Test void testSetValueResetsRamp()
	{
		final Source source = source(0);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().setValueAndResetRamp(0);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().setRamp(1.0);
		assertEquals("0 ramp 999/1000", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().setValueAndResetRamp(5);
		assertEquals("5 (initially 0)", p.mediaFingerprintOffset().getInfo());
	}

	@Test void testNewFail()
	{
		try
		{
			new MediaFingerprintOffset(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("value must not be negative, but was -1", e.getMessage());
		}
	}

	@Test void testSetValueFail()
	{
		final Source source = source(55);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
		try
		{
			p.mediaFingerprintOffset().setValueAndResetRamp(-1);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("value must not be negative, but was -1", e.getMessage());
		}
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
	}

	@Test void testSetRampNegative()
	{
		final Source source = source(55);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
		try
		{
			p.mediaFingerprintOffset().setRamp(-0.0001);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("ramp must be between 0.0 and 1.0, but was -1.0E-4", e.getMessage());
		}
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
	}

	@Test void testSetRampTooLarge()
	{
		final Source source = source(55);
		final ConnectProperties p = ConnectProperties.create(source);
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
		try
		{
			p.mediaFingerprintOffset().setRamp(1.0001);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("ramp must be between 0.0 and 1.0, but was 1.0001", e.getMessage());
		}
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
	}

	@Test void testInvalid()
	{
		final String propKey = "media.fingerprintOffset";
		final Source source = source(-1);
		try
		{
			ConnectProperties.create(source);
			fail();
		}
		catch(final IllegalPropertiesException e)
		{
			assertEquals(
					"property " + propKey + " in MediaUrlFingerOffsetPropertiesTest " +
					"must be an integer greater or equal 0, but was -1",
					e.getMessage());
		}
	}

	@Test void testDummy()
	{
		final Source source = source(55);
		final ConnectProperties p = ConnectProperties.create(source);
		p.mediaFingerprintOffset().setValueAndResetRamp(66);
		p.mediaFingerprintOffset().setRamp(0.333);
		assertEquals("66 (initially 55) ramp 333/1000", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().enableDummy();
		assertEquals("66 (initially 55) ramp 333/1000 OVERRIDDEN BY DUMMY", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().disableDummy();
		assertEquals("66 (initially 55) ramp 333/1000", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().enableDummy();
		assertEquals("66 (initially 55) ramp 333/1000 OVERRIDDEN BY DUMMY", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().reset();
		assertEquals("55", p.mediaFingerprintOffset().getInfo());
	}


	private static Source source(final Integer offset)
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:MediaUrlFingerOffsetPropertiesTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		if(offset!=null)
			source.setProperty("media.fingerprintOffset", String.valueOf(offset));
		return Sources.view(source , "MediaUrlFingerOffsetPropertiesTest");
	}
}

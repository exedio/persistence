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

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.util.Properties.Source;
import com.exedio.cope.util.Sources;
import java.util.Properties;
import junit.framework.TestCase;

public class MediaUrlFingerOffsetPropertiesTest extends TestCase
{
	public void testDefault()
	{
		final Source source = source(null);
		final ConnectProperties p = new ConnectProperties(source, null);
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

	public void testCustom()
	{
		final Source source = source(55);
		final ConnectProperties p = new ConnectProperties(source, null);
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

	public void testRampFunction()
	{
		final Source source = source(0);
		final ConnectProperties p = new ConnectProperties(source, null);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());
		assertEquals(true, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.000, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(1/1000d);
		assertEquals("0 ramp 1/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.001, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(2/1000d);
		assertEquals("0 ramp 2/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.002, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(33/1000d);
		assertEquals("0 ramp 33/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.033, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(450/1000d);
		assertEquals("0 ramp 450/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());

		p.mediaFingerprintOffset().setRamp(997/1000d);
		assertEquals("0 ramp 997/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.997, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(998/1000d);
		assertEquals("0 ramp 998/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.998, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(999/1000d);
		assertEquals("0 ramp 999/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.999, p.mediaFingerprintOffset().getRamp());

		p.mediaFingerprintOffset().setRamp(0.99999000);
		assertEquals("0 ramp 999/1000", p.mediaFingerprintOffset().getInfo());
		assertEquals(false, p.mediaFingerprintOffset().isInitial());
		assertEquals(0.999, p.mediaFingerprintOffset().getRamp());
	}

	public void testSetValueResetsRamp()
	{
		final Source source = source(0);
		final ConnectProperties p = new ConnectProperties(source, null);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().setValueAndResetRamp(0);
		assertEquals("0", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().setRamp(1.0);
		assertEquals("0 ramp 999/1000", p.mediaFingerprintOffset().getInfo());

		p.mediaFingerprintOffset().setValueAndResetRamp(5);
		assertEquals("5 (initially 0)", p.mediaFingerprintOffset().getInfo());
	}

	public void testNewFail()
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

	public void testSetValueFail()
	{
		final Source source = source(55);
		final ConnectProperties p = new ConnectProperties(source, null);
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

	public void testSetRampNegative()
	{
		final Source source = source(55);
		final ConnectProperties p = new ConnectProperties(source, null);
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

	public void testSetRampTooLarge()
	{
		final Source source = source(55);
		final ConnectProperties p = new ConnectProperties(source, null);
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

	public void testInvalid()
	{
		final String propKey = "media.fingerprintOffset";
		final Source source = source(-1);
		try
		{
			new ConnectProperties(source, null);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
					"property " + propKey + " in MediaUrlFingerOffsetPropertiesTest has invalid value, expected an integer greater or equal 0, but got -1.",
					e.getMessage());
			// TODO use IllegalPropertiesException when available in copeutil
			assertEquals(IllegalArgumentException.class, e.getClass());
		}
	}


	private static Source source(final Integer offset)
	{
		final Properties source = new Properties();
		source.setProperty("connection.url", "jdbc:hsqldb:mem:MediaUrlFingerOffsetPropertiesTest");
		source.setProperty("connection.username", "sa");
		source.setProperty("connection.password", "");
		if(offset!=null)
			source.setProperty("media.fingerprintOffset", "" + offset);
		return Sources.view(source , "MediaUrlFingerOffsetPropertiesTest");
	}
}

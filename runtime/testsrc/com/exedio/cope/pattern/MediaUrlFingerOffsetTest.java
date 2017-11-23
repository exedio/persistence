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

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnValueL;
import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.MediaUrlItem.fileFinger;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.TestWithEnvironment;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class MediaUrlFingerOffsetTest extends TestWithEnvironment
{
	public MediaUrlFingerOffsetTest()
	{
		super(MediaUrlModelTest.MODEL);
	}

	private final MediaUrlItem[] item = new MediaUrlItem[1007];

	@BeforeEach public void setUp()
	{
		Arrays.setAll(item, i -> create());
	}

	private static MediaUrlItem create()
	{
		final MediaUrlItem item = new MediaUrlItem(null);
		final byte[] bytes  = {-86,122,-8,23};
		item.setFileFinger(bytes, "foo/bar");
		fileFinger.getLastModified().set(item, new Date(23 + 192)); // XD
		return item;
	}

	@Test void testTouchLastModified()
	{
		assertIt(".fXD", item[0]);

		fileFinger.getLastModified().set(item[0], new Date(24 + 192)); // YD
		assertIt(".fYD", item[0]);
	}

	@Test void testGlobalOffsetValue()
	{
		assertEquals(   0, getPrimaryKeyColumnValueL(item[   0]));
		assertEquals(   1, getPrimaryKeyColumnValueL(item[   1]));
		assertEquals(1005, getPrimaryKeyColumnValueL(item[1005]));
		assertEquals(1006, getPrimaryKeyColumnValueL(item[1006]));

		assertIt(".fXD", item[0]);
		assertIt(".fXD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fXD", item[1000]);
		assertIt(".fXD", item[1001]);
		assertIt(".fXD", item[1002]);

		setValueAndResetRamp(1, "1 (initially 0)");
		assertIt(".fYD", item[0]);
		assertIt(".fYD", item[1]);
		assertIt(".fYD", item[2]);
		assertIt(".fYD", item[3]);
		assertIt(".fYD", item[4]);
		assertIt(".fYD", item[ 998]);
		assertIt(".fYD", item[ 999]);
		assertIt(".fYD", item[1000]);
		assertIt(".fYD", item[1001]);
		assertIt(".fYD", item[1002]);

		setValueAndResetRamp(3, "3 (initially 0)");
		assertIt(".faD", item[0]);
		assertIt(".faD", item[1]);
		assertIt(".faD", item[2]);
		assertIt(".faD", item[3]);
		assertIt(".faD", item[4]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".faD", item[1000]);
		assertIt(".faD", item[1001]);
		assertIt(".faD", item[1002]);

		setValueAndResetRamp(0, "0");
		assertIt(".fXD", item[0]);
		assertIt(".fXD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fXD", item[1000]);
		assertIt(".fXD", item[1001]);
		assertIt(".fXD", item[1002]);
	}

	@Test void testGlobalOffsetRamp()
	{
		assertIt(".fXD", item[0]);
		assertIt(".fXD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fXD", item[1000]);
		assertIt(".fXD", item[1001]);
		assertIt(".fXD", item[1002]);
		assertIt(".fXD", item[1003]);
		assertIt(".fXD", item[1004]);
		assertIt(".fXD", item[1005]);
		assertIt(".fXD", item[1006]);

		setRamp(1, "0 ramp 1/1000");
		assertIt(".fYD", item[0]);
		assertIt(".fXD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fYD", item[1000]);
		assertIt(".fXD", item[1001]);
		assertIt(".fXD", item[1002]);
		assertIt(".fXD", item[1003]);
		assertIt(".fXD", item[1004]);
		assertIt(".fXD", item[1005]);
		assertIt(".fXD", item[1006]);

		setRamp(2, "0 ramp 2/1000");
		assertIt(".fYD", item[0]);
		assertIt(".fYD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fYD", item[1000]);
		assertIt(".fYD", item[1001]);
		assertIt(".fXD", item[1002]);
		assertIt(".fXD", item[1003]);
		assertIt(".fXD", item[1004]);
		assertIt(".fXD", item[1005]);
		assertIt(".fXD", item[1006]);

		setRamp(4, "0 ramp 4/1000");
		assertIt(".fYD", item[0]);
		assertIt(".fYD", item[1]);
		assertIt(".fYD", item[2]);
		assertIt(".fYD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fYD", item[1000]);
		assertIt(".fYD", item[1001]);
		assertIt(".fYD", item[1002]);
		assertIt(".fYD", item[1003]);
		assertIt(".fXD", item[1004]);
		assertIt(".fXD", item[1005]);
		assertIt(".fXD", item[1006]);

		setRamp(998, "0 ramp 998/1000");
		assertIt(".fYD", item[0]);
		assertIt(".fYD", item[1]);
		assertIt(".fYD", item[2]);
		assertIt(".fYD", item[3]);
		assertIt(".fYD", item[4]);
		assertIt(".fYD", item[ 996]);
		assertIt(".fYD", item[ 997]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fYD", item[1000]);
		assertIt(".fYD", item[1001]);
		assertIt(".fYD", item[1002]);
		assertIt(".fYD", item[1003]);
		assertIt(".fYD", item[1004]);
		assertIt(".fYD", item[1005]);
		assertIt(".fYD", item[1006]);

		setRamp(999, "0 ramp 999/1000");
		assertIt(".fYD", item[0]);
		assertIt(".fYD", item[1]);
		assertIt(".fYD", item[2]);
		assertIt(".fYD", item[3]);
		assertIt(".fYD", item[4]);
		assertIt(".fYD", item[ 996]);
		assertIt(".fYD", item[ 997]);
		assertIt(".fYD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fYD", item[1000]);
		assertIt(".fYD", item[1001]);
		assertIt(".fYD", item[1002]);
		assertIt(".fYD", item[1003]);
		assertIt(".fYD", item[1004]);
		assertIt(".fYD", item[1005]);
		assertIt(".fYD", item[1006]);

		setRamp(0, "0");
		assertIt(".fXD", item[0]);
		assertIt(".fXD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fXD", item[1000]);
		assertIt(".fXD", item[1001]);
		assertIt(".fXD", item[1002]);
		assertIt(".fXD", item[1003]);
		assertIt(".fXD", item[1004]);
		assertIt(".fXD", item[1005]);
		assertIt(".fXD", item[1006]);
	}

	@Test void testGlobalOffsetValueAndRamp()
	{
		assertIt(".fXD", item[0]);
		assertIt(".fXD", item[1]);
		assertIt(".fXD", item[2]);
		assertIt(".fXD", item[3]);
		assertIt(".fXD", item[4]);
		assertIt(".fXD", item[ 998]);
		assertIt(".fXD", item[ 999]);
		assertIt(".fXD", item[1000]);
		assertIt(".fXD", item[1001]);
		assertIt(".fXD", item[1002]);
		assertIt(".fXD", item[1003]);
		assertIt(".fXD", item[1004]);
		assertIt(".fXD", item[1005]);
		assertIt(".fXD", item[1006]);

		setValueAndResetRamp(3, "3 (initially 0)");
		assertIt(".faD", item[0]);
		assertIt(".faD", item[1]);
		assertIt(".faD", item[2]);
		assertIt(".faD", item[3]);
		assertIt(".faD", item[4]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".faD", item[1000]);
		assertIt(".faD", item[1001]);
		assertIt(".faD", item[1002]);
		assertIt(".faD", item[1003]);
		assertIt(".faD", item[1004]);
		assertIt(".faD", item[1005]);
		assertIt(".faD", item[1006]);

		setRamp(1, "3 (initially 0) ramp 1/1000");
		assertIt(".fbD", item[0]);
		assertIt(".faD", item[1]);
		assertIt(".faD", item[2]);
		assertIt(".faD", item[3]);
		assertIt(".faD", item[4]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".fbD", item[1000]);
		assertIt(".faD", item[1001]);
		assertIt(".faD", item[1002]);
		assertIt(".faD", item[1003]);
		assertIt(".faD", item[1004]);
		assertIt(".faD", item[1005]);
		assertIt(".faD", item[1006]);

		setRamp(2, "3 (initially 0) ramp 2/1000");
		assertIt(".fbD", item[0]);
		assertIt(".fbD", item[1]);
		assertIt(".faD", item[2]);
		assertIt(".faD", item[3]);
		assertIt(".faD", item[4]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".fbD", item[1000]);
		assertIt(".fbD", item[1001]);
		assertIt(".faD", item[1002]);
		assertIt(".faD", item[1003]);
		assertIt(".faD", item[1004]);
		assertIt(".faD", item[1005]);
		assertIt(".faD", item[1006]);

		setRamp(4, "3 (initially 0) ramp 4/1000");
		assertIt(".fbD", item[0]);
		assertIt(".fbD", item[1]);
		assertIt(".fbD", item[2]);
		assertIt(".fbD", item[3]);
		assertIt(".faD", item[4]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".fbD", item[1000]);
		assertIt(".fbD", item[1001]);
		assertIt(".fbD", item[1002]);
		assertIt(".fbD", item[1003]);
		assertIt(".faD", item[1004]);
		assertIt(".faD", item[1005]);
		assertIt(".faD", item[1006]);

		setRamp(998, "3 (initially 0) ramp 998/1000");
		assertIt(".fbD", item[0]);
		assertIt(".fbD", item[1]);
		assertIt(".fbD", item[2]);
		assertIt(".fbD", item[3]);
		assertIt(".fbD", item[4]);
		assertIt(".fbD", item[ 996]);
		assertIt(".fbD", item[ 997]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".fbD", item[1000]);
		assertIt(".fbD", item[1001]);
		assertIt(".fbD", item[1002]);
		assertIt(".fbD", item[1003]);
		assertIt(".fbD", item[1004]);
		assertIt(".fbD", item[1005]);
		assertIt(".fbD", item[1006]);

		setRamp(999, "3 (initially 0) ramp 999/1000");
		assertIt(".fbD", item[0]);
		assertIt(".fbD", item[1]);
		assertIt(".fbD", item[2]);
		assertIt(".fbD", item[3]);
		assertIt(".fbD", item[4]);
		assertIt(".fbD", item[ 996]);
		assertIt(".fbD", item[ 997]);
		assertIt(".fbD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".fbD", item[1000]);
		assertIt(".fbD", item[1001]);
		assertIt(".fbD", item[1002]);
		assertIt(".fbD", item[1003]);
		assertIt(".fbD", item[1004]);
		assertIt(".fbD", item[1005]);
		assertIt(".fbD", item[1006]);

		setRamp(0, "3 (initially 0)");
		assertIt(".faD", item[0]);
		assertIt(".faD", item[1]);
		assertIt(".faD", item[2]);
		assertIt(".faD", item[3]);
		assertIt(".faD", item[4]);
		assertIt(".faD", item[ 998]);
		assertIt(".faD", item[ 999]);
		assertIt(".faD", item[1000]);
		assertIt(".faD", item[1001]);
		assertIt(".faD", item[1002]);
		assertIt(".faD", item[1003]);
		assertIt(".faD", item[1004]);
		assertIt(".faD", item[1005]);
		assertIt(".faD", item[1006]);
	}

	@Test void testDummy()
	{
		assertIt(".fXD", item[0]);

		enableDummy("0 OVERRIDDEN BY DUMMY");
		assertIt(".fDUMMY", item[0]);

		disableDummy("0");
		assertIt(".fXD", item[0]);
	}

	private static void assertIt(final String fingerprint, final MediaUrlItem item)
	{
		assertLocator(
				fileFinger, "MediaUrlItem/fileFinger/" + fingerprint + "/" + item,
				item.getFileFingerLocator());
	}

	private void setValueAndResetRamp(final int offset, final String info)
	{
		offset().setValueAndResetRamp(offset);
		assertEquals(info, offset().getInfo());
	}

	private void setRamp(final int ramp, final String info)
	{
		offset().setRamp(ramp/1000d);
		assertEquals(info, offset().getInfo());
	}

	private void enableDummy(final String info)
	{
		offset().enableDummy();
		assertEquals(info, offset().getInfo());
	}

	private void disableDummy(final String info)
	{
		offset().disableDummy();
		assertEquals(info, offset().getInfo());
	}

	private MediaFingerprintOffset offset()
	{
		return model.getConnectProperties().mediaFingerprintOffset();
	}
}

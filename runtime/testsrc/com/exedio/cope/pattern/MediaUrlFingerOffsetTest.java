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

import static com.exedio.cope.pattern.MediaLocatorAssert.assertLocator;
import static com.exedio.cope.pattern.MediaUrlItem.fileFinger;

import com.exedio.cope.AbstractRuntimeModelTest;
import java.util.Date;

public final class MediaUrlFingerOffsetTest extends AbstractRuntimeModelTest
{
	public MediaUrlFingerOffsetTest()
	{
		super(MediaUrlModelTest.MODEL);
	}

	private MediaUrlItem item0, item1, item2, item3, item4;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item0 = create();
		item1 = create();
		item2 = create();
		item3 = create();
		item4 = create();
	}

	private static MediaUrlItem create()
	{
		final MediaUrlItem item = new MediaUrlItem(null);
		final byte[] bytes  = {-86,122,-8,23};
		item.setFileFinger(bytes, "foo/bar");
		fileFinger.getLastModified().set(item, new Date(23 + 192)); // XD
		return item;
	}

	public void testTouchLastModified()
	{
		assertIt(".fXD", item0);

		fileFinger.getLastModified().set(item0, new Date(24 + 192)); // YD
		assertIt(".fYD", item0);
	}

	public void testGlobalOffsetValue()
	{
		assertIt(".fXD", item0);
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);

		setValueAndResetRamp(1, "1 (initially 0)");
		assertIt(".fYD", item0);
		assertIt(".fYD", item1);
		assertIt(".fYD", item2);
		assertIt(".fYD", item3);
		assertIt(".fYD", item4);

		setValueAndResetRamp(3, "3 (initially 0)");
		assertIt(".faD", item0);
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);

		setValueAndResetRamp(0, "0");
		assertIt(".fXD", item0);
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
	}

	public void testGlobalOffsetRamp()
	{
		assertIt(".fXD", item0);
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);

		setRamp(1, "0 ramp 1/1000");
		assertIt(".fYD", item0);
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);

		setRamp(2, "0 ramp 2/1000");
		assertIt(".fYD", item0);
		assertIt(".fYD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);

		setRamp(4, "0 ramp 4/1000");
		assertIt(".fYD", item0);
		assertIt(".fYD", item1);
		assertIt(".fYD", item2);
		assertIt(".fYD", item3);
		assertIt(".fXD", item4);

		setRamp(0, "0");
		assertIt(".fXD", item0);
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
	}

	public void testGlobalOffsetValueAndRamp()
	{
		assertIt(".fXD", item0);
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);

		setValueAndResetRamp(3, "3 (initially 0)");
		assertIt(".faD", item0);
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);

		setRamp(1, "3 (initially 0) ramp 1/1000");
		assertIt(".fbD", item0);
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);

		setRamp(2, "3 (initially 0) ramp 2/1000");
		assertIt(".fbD", item0);
		assertIt(".fbD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);

		setRamp(4, "3 (initially 0) ramp 4/1000");
		assertIt(".fbD", item0);
		assertIt(".fbD", item1);
		assertIt(".fbD", item2);
		assertIt(".fbD", item3);
		assertIt(".faD", item4);

		setRamp(0, "3 (initially 0)");
		assertIt(".faD", item0);
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);
	}

	private static void assertIt(final String fingerprint, final MediaUrlItem item)
	{
		assertLocator(
				fileFinger, "MediaUrlItem/fileFinger/" + fingerprint + "/" + item,
				item.getFileFingerLocator());
	}

	private void setValueAndResetRamp(final int offset, final String info)
	{
		model.getConnectProperties().mediaFingerprintOffset().setValueAndResetRamp(offset);
		assertEquals(info, model.getConnectProperties().mediaFingerprintOffset().getInfo());
	}

	private void setRamp(final int ramp, final String info)
	{
		model.getConnectProperties().mediaFingerprintOffset().setRamp(ramp/1000d);
		assertEquals(info, model.getConnectProperties().mediaFingerprintOffset().getInfo());
	}
}

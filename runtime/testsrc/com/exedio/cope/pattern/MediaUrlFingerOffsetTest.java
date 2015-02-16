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

	private MediaUrlItem item1, item2, item3, item4, item5;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item1 = create();
		item2 = create();
		item3 = create();
		item4 = create();
		item5 = create();
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
		assertIt(".fXD", item1);

		fileFinger.getLastModified().set(item1, new Date(24 + 192)); // YD
		assertIt(".fYD", item1);
	}

	public void testGlobalOffsetValue()
	{
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);

		setOffset(1, 0, "1 initially 0");
		assertIt(".fYD", item1);
		assertIt(".fYD", item2);
		assertIt(".fYD", item3);
		assertIt(".fYD", item4);
		assertIt(".fYD", item5);

		setOffset(3, 0, "3 initially 0");
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);
		assertIt(".faD", item5);

		setOffset(0, 0, "0");
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);
	}

	public void testGlobalOffsetRamp()
	{
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);

		setOffset(0, 1, "0 ramp 1/1000");
		assertIt(".fYD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);

		setOffset(0, 2, "0 ramp 2/1000");
		assertIt(".fYD", item1);
		assertIt(".fYD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);

		setOffset(0, 4, "0 ramp 4/1000");
		assertIt(".fYD", item1);
		assertIt(".fYD", item2);
		assertIt(".fYD", item3);
		assertIt(".fYD", item4);
		assertIt(".fXD", item5);

		setOffset(0, 0, "0");
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);
	}

	public void testGlobalOffsetValueAndRamp()
	{
		assertIt(".fXD", item1);
		assertIt(".fXD", item2);
		assertIt(".fXD", item3);
		assertIt(".fXD", item4);
		assertIt(".fXD", item5);

		setOffset(3, 0, "3 initially 0");
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);
		assertIt(".faD", item5);

		setOffset(3, 1, "3 ramp 1/1000 initially 0");
		assertIt(".fbD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);
		assertIt(".faD", item5);

		setOffset(3, 2, "3 ramp 2/1000 initially 0");
		assertIt(".fbD", item1);
		assertIt(".fbD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);
		assertIt(".faD", item5);

		setOffset(3, 4, "3 ramp 4/1000 initially 0");
		assertIt(".fbD", item1);
		assertIt(".fbD", item2);
		assertIt(".fbD", item3);
		assertIt(".fbD", item4);
		assertIt(".faD", item5);

		setOffset(3, 0, "3 initially 0");
		assertIt(".faD", item1);
		assertIt(".faD", item2);
		assertIt(".faD", item3);
		assertIt(".faD", item4);
		assertIt(".faD", item5);
	}

	private static void assertIt(final String fingerprint, final MediaUrlItem item)
	{
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/" + fingerprint + "/" + item, item.getFileFingerLocator());
	}

	private void setOffset(final int offset, final int ramp, final String info)
	{
		model.getConnectProperties().mediaFingerprintOffset().set(offset, ramp);
		assertEquals(info, model.getConnectProperties().mediaFingerprintOffset().getInfo());
	}
}

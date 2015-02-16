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
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item1, item1.getFileFingerLocator());

		fileFinger.getLastModified().set(item1, new Date(24 + 192)); // YD
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item1, item1.getFileFingerLocator());
	}

	public void testGlobalOffset()
	{
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item5, item5.getFileFingerLocator());

		setOffset(1, 0);
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item5, item5.getFileFingerLocator());

		setOffset(3, 0);
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.faD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.faD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.faD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.faD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.faD/" + item5, item5.getFileFingerLocator());

		setOffset(0, 0);
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item5, item5.getFileFingerLocator());

		setOffset(0, 1);
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item5, item5.getFileFingerLocator());

		setOffset(0, 2);
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item5, item5.getFileFingerLocator());

		setOffset(0, 4);
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item1, item1.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item2, item2.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item3, item3.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fYD/" + item4, item4.getFileFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item5, item5.getFileFingerLocator());
	}

	private void setOffset(final int offset, final int ramp)
	{
		model.getConnectProperties().mediaFingerprintOffset().set(offset, ramp);
	}
}

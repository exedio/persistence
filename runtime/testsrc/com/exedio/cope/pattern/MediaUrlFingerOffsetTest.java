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
import static com.exedio.cope.pattern.MediaUrlItem.fotoFinger;

import com.exedio.cope.AbstractRuntimeModelTest;
import java.util.Date;

public final class MediaUrlFingerOffsetTest extends AbstractRuntimeModelTest
{
	public MediaUrlFingerOffsetTest()
	{
		super(MediaUrlModelTest.MODEL);
	}

	private MediaUrlItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = new MediaUrlItem(null);
	}

	public void testIt()
	{
		final byte[] bytes  = {-86,122,-8,23};
		item.setFotoFinger(bytes, "image/jpeg");
		item.setFileFinger(bytes, "foo/bar");
		fotoFinger.getLastModified().set(item, new Date(23 + 128)); // XC
		fileFinger.getLastModified().set(item, new Date(23 + 192)); // XD

		assertLocator(fotoFinger, "MediaUrlItem/fotoFinger/.fXC/" + item + ".jpg", item.getFotoFingerLocator());
		assertLocator(fileFinger, "MediaUrlItem/fileFinger/.fXD/" + item,          item.getFileFingerLocator());
	}
}

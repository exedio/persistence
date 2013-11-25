/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.EqualsAssert.assertNotEqualsAndHash;

import com.exedio.cope.AbstractRuntimeModelTest;

public final class MediaPathLocatorTest extends AbstractRuntimeModelTest
{
	public MediaPathLocatorTest()
	{
		super(MediaPathTest.MODEL);
	}

	@SuppressWarnings("static-method")
	public void testIt()
	{
		final MediaPathItem i1 = new MediaPathItem();
		final MediaPathItem i2 = new MediaPathItem();
		i1.setNormalContentType("one/normal");
		i1.setFingerContentType("one/finger");
		i2.setNormalContentType("two/normal");
		i2.setFingerContentType("two/finger");

		final MediaPath.Locator l1a = i1.getNormalLocator();
		final MediaPath.Locator l1b = i1.getFingerLocator();
		final MediaPath.Locator l2a = i2.getNormalLocator();
		final MediaPath.Locator l2b = i2.getFingerLocator();
		final MediaPath.Locator l1aX = i1.getNormalLocator();

		// locator methods must work without transaction
		model.commit();

		assertNotSame      (l1a, l1aX);
		assertEqualsAndHash(l1a, l1aX);
		assertNotEqualsAndHash(l1a, l1b, l2a, l2b);
	}
}

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

import static com.exedio.cope.tojunit.EqualsAssert.assertEqualsAndHash;
import static com.exedio.cope.tojunit.EqualsAssert.assertNotEqualsAndHash;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.MediaPath.Locator;
import org.junit.Test;

public final class MediaPathLocatorTest extends TestWithEnvironment
{
	public MediaPathLocatorTest()
	{
		super(MediaPathTest.MODEL);
	}

	@Test public void testIt()
	{
		final MediaPathItem i1 = new MediaPathItem();
		final MediaPathItem i2 = new MediaPathItem();
		i1.setNormalContentType("one/normal");
		i1.setFingerContentType("one/finger");
		i2.setNormalContentType("two/normal");
		i2.setFingerContentType("two/finger");

		final Locator l1a = i1.getNormalLocator();
		final Locator l1b = i1.getFingerLocator();
		final Locator l2a = i2.getNormalLocator();
		final Locator l2b = i2.getFingerLocator();
		final Locator l1aX = i1.getNormalLocator();

		// locator methods must work without transaction
		model.commit();

		assertSame(MediaPathItem.normal, l1a.getFeature());
		assertSame(MediaPathItem.finger, l1b.getFeature());
		assertSame(MediaPathItem.normal, l2a.getFeature());
		assertSame(MediaPathItem.finger, l2b.getFeature());

		assertSame(i1, l1a.getItem());
		assertSame(i1, l1b.getItem());
		assertSame(i2, l2a.getItem());
		assertSame(i2, l2b.getItem());

		assertSame("one/normal", l1a.getContentType());
		assertSame("one/finger", l1b.getContentType());
		assertSame("two/normal", l2a.getContentType());
		assertSame("two/finger", l2b.getContentType());

		assertNotSame      (l1a, l1aX);
		assertEqualsAndHash(l1a, l1aX);
		assertNotEqualsAndHash(l1a, l1b, l2a, l2b);

		assertEquals("MediaPathItem/normal/MediaPathItem-0", l1a.toString());
		assertEquals("MediaPathItem/finger/MediaPathItem-0", l1b.toString());
		assertEquals("MediaPathItem/normal/MediaPathItem-1", l2a.toString());
		assertEquals("MediaPathItem/finger/MediaPathItem-1", l2b.toString());
	}
}

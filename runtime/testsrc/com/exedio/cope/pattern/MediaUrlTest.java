/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.pattern.MediaUrlItem.TYPE;
import static com.exedio.cope.pattern.MediaUrlItem.name;
import static com.exedio.cope.pattern.MediaUrlItem.photo;
import static com.exedio.cope.pattern.MediaUrlItem.tokened;

import java.util.Arrays;

import com.exedio.cope.AbstractRuntimeTest;
import com.exedio.cope.Feature;
import com.exedio.cope.Model;

public final class MediaUrlTest extends AbstractRuntimeTest
{
	static final Model MODEL = new Model(MediaUrlItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaUrlTest.class, "MODEL");
	}

	public MediaUrlTest()
	{
		super(MODEL);
	}

	protected MediaUrlItem item;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		item = deleteOnTearDown(new MediaUrlItem("test media item"));
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				name,
				photo,
				photo.getBody(),
				photo.getLastModified(),
				photo.getUnison(),
				tokened,
				tokened.getBody(),
				tokened.getContentType(),
				tokened.getLastModified(),
				tokened.getUnison(),
			}), TYPE.getFeatures());

		// token

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertFalse(photo.isUrlGuessingPrevented());
		assertTrue(tokened.isUrlGuessingPrevented());
		assertEquals(null, item.getTokenedURL());
		assertEquals(null, item.getTokenedLocator());

		item.setPhoto(data4, "image/jpeg");
		item.setTokened(data4, "image/jpeg");
		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertEquals(mediaRootUrl + "MediaUrlItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoURL());
		assertEquals(               "MediaUrlItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/tokened/" + item.getCopeID() +      ".jpg?t=MediaUrlItem.tokened-MediaUrlItem-0", item.getTokenedURL());
		assertEquals(               "MediaUrlItem/tokened/" + item.getCopeID() +      ".jpg?t=MediaUrlItem.tokened-MediaUrlItem-0", item.getTokenedLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/tokened/" + item.getCopeID() + "/name.jpg?t=MediaUrlItem.tokened-MediaUrlItem-0", item.getTokenedURL("name"));

		System.setProperty("media.url.secret", "valueOfMediaUrlSecret");
		assertTrue(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertEquals(mediaRootUrl + "MediaUrlItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoURL());
		assertEquals(               "MediaUrlItem/photo/" + item.getCopeID() + ".jpg", item.getPhotoLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/tokened/" + item.getCopeID() +      ".jpg?t=2e5e2a171aedcfc0d04f", item.getTokenedURL());
		assertEquals(               "MediaUrlItem/tokened/" + item.getCopeID() +      ".jpg?t=2e5e2a171aedcfc0d04f", item.getTokenedLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/tokened/" + item.getCopeID() + "/name.jpg?t=2e5e2a171aedcfc0d04f", item.getTokenedURL("name"));
	}
}

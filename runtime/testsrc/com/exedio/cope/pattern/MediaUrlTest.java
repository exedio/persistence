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
import static com.exedio.cope.pattern.MediaUrlItem.foto;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecure;

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

	protected MediaUrlItem named, anond;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		named = deleteOnTearDown(new MediaUrlItem("name"));
		anond = deleteOnTearDown(new MediaUrlItem(null));
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				name,
				foto,
				foto.getBody(),
				foto.getLastModified(),
				foto.getUnison(),
				fotoSecure,
				fotoSecure.getBody(),
				fotoSecure.getContentType(),
				fotoSecure.getLastModified(),
				fotoSecure.getUnison(),
			}), TYPE.getFeatures());

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertFalse(foto.isUrlGuessingPrevented());
		assertTrue(fotoSecure.isUrlGuessingPrevented());
		assertEquals(null, named.getFotoSecureURL());
		assertEquals(null, named.getFotoSecureLocator());
		assertEquals(null, anond.getFotoSecureURL());
		assertEquals(null, anond.getFotoSecureLocator());

		named.setFoto(data4, "image/jpeg");
		named.setFotoSecure(data4, "image/jpeg");
		anond.setFoto(data4, "image/jpeg");
		anond.setFotoSecure(data4, "image/jpeg");
		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertEquals(mediaRootUrl + "MediaUrlItem/foto/" + named.getCopeID() + ".jpg", named.getFotoURL());
		assertEquals(               "MediaUrlItem/foto/" + named.getCopeID() + ".jpg", named.getFotoLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/foto/" + anond.getCopeID() + ".jpg", anond.getFotoURL());
		assertEquals(               "MediaUrlItem/foto/" + anond.getCopeID() + ".jpg", anond.getFotoLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + named.getCopeID() +      ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-0", fotoSecure.getURL(named));
		assertEquals(               "MediaUrlItem/fotoSecure/" + named.getCopeID() +      ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-0", named.getFotoSecureLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + named.getCopeID() + "/name.jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-0", named.getFotoSecureURL());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + anond.getCopeID() +      ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-1", fotoSecure.getURL(anond));
		assertEquals(               "MediaUrlItem/fotoSecure/" + anond.getCopeID() +      ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-1", anond.getFotoSecureLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + anond.getCopeID() +      ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-1", anond.getFotoSecureURL());

		System.setProperty("media.url.secret", "valueOfMediaUrlSecret");
		assertTrue(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertEquals(mediaRootUrl + "MediaUrlItem/foto/" + named.getCopeID() + ".jpg", named.getFotoURL());
		assertEquals(               "MediaUrlItem/foto/" + named.getCopeID() + ".jpg", named.getFotoLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/foto/" + anond.getCopeID() + ".jpg", anond.getFotoURL());
		assertEquals(               "MediaUrlItem/foto/" + anond.getCopeID() + ".jpg", anond.getFotoLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + named.getCopeID() +      ".jpg?t=c156b63cb8a39ae5f16c", fotoSecure.getURL(named));
		assertEquals(               "MediaUrlItem/fotoSecure/" + named.getCopeID() +      ".jpg?t=c156b63cb8a39ae5f16c", named.getFotoSecureLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + named.getCopeID() + "/name.jpg?t=c156b63cb8a39ae5f16c", named.getFotoSecureURL());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + anond.getCopeID() +      ".jpg?t=f3da9d7e6856a2f9df6c", fotoSecure.getURL(anond));
		assertEquals(               "MediaUrlItem/fotoSecure/" + anond.getCopeID() +      ".jpg?t=f3da9d7e6856a2f9df6c", anond.getFotoSecureLocator().getPath());
		assertEquals(mediaRootUrl + "MediaUrlItem/fotoSecure/" + anond.getCopeID() +      ".jpg?t=f3da9d7e6856a2f9df6c", anond.getFotoSecureURL());
	}
}

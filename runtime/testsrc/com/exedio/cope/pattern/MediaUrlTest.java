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
import static com.exedio.cope.pattern.MediaUrlItem.file;
import static com.exedio.cope.pattern.MediaUrlItem.fileSecure;
import static com.exedio.cope.pattern.MediaUrlItem.foto;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecure;
import static com.exedio.cope.pattern.MediaUrlItem.name;

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
				fotoSecure.getLastModified(),
				fotoSecure.getUnison(),
				file,
				file.getBody(),
				file.getLastModified(),
				file.getUnison(),
				fileSecure,
				fileSecure.getBody(),
				fileSecure.getLastModified(),
				fileSecure.getUnison(),
			}), TYPE.getFeatures());

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertFalse(foto.isUrlGuessingPrevented());
		assertFalse(file.isUrlGuessingPrevented());
		assertTrue(fotoSecure.isUrlGuessingPrevented());
		assertTrue(fileSecure.isUrlGuessingPrevented());
		assertEquals(null, named.getFotoSecureURL());
		assertEquals(null, named.getFotoSecureLocator());
		assertEquals(null, anond.getFotoSecureURL());
		assertEquals(null, anond.getFotoSecureLocator());
		assertEquals(null, named.getFileSecureURL());
		assertEquals(null, named.getFileSecureLocator());
		assertEquals(null, anond.getFileSecureURL());
		assertEquals(null, anond.getFileSecureLocator());

		named.setFoto(data4, "image/jpeg");
		named.setFotoSecure(data4, "image/jpeg");
		anond.setFoto(data4, "image/jpeg");
		anond.setFotoSecure(data4, "image/jpeg");
		named.setFile(data4, "foo/bar");
		named.setFileSecure(data4, "foo/bar");
		anond.setFile(data4, "foo/bar");
		anond.setFileSecure(data4, "foo/bar");
		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertIt("MediaUrlItem/foto/", foto, named, "/name.jpg");
		assertIt("MediaUrlItem/foto/", foto, anond,      ".jpg");
		assertIt("MediaUrlItem/file/", file, named, "/name"    );
		assertIt("MediaUrlItem/file/", file, anond,      ""    );
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, named, "/name.jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-0");
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, anond,      ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-1");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, named, "/name?t=MediaUrlItem.fileSecure-MediaUrlItem-0");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, anond,      "?t=MediaUrlItem.fileSecure-MediaUrlItem-1");

		System.setProperty("media.url.secret", "valueOfMediaUrlSecret");
		assertTrue(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertIt("MediaUrlItem/foto/", foto, named, "/name.jpg");
		assertIt("MediaUrlItem/foto/", foto, anond,      ".jpg");
		assertIt("MediaUrlItem/file/", file, named, "/name"    );
		assertIt("MediaUrlItem/file/", file, anond,       ""   );
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, named, "/name.jpg?t=c156b63cb8a39ae5f16c");
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, anond,      ".jpg?t=f3da9d7e6856a2f9df6c");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, named, "/name"+ "?t=91f7b44e250a56f61ae9");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, anond,          "?t=faf24676503317102086");
	}

	private void assertIt(final String prefix, final Media path, final MediaUrlItem item, final String postfix)
	{
		final String pathInfo = prefix + item.getCopeID() + postfix;
		assertEquals(mediaRootUrl + pathInfo, path.getURL(item));

		final MediaPath.Locator locator = path.getLocator(item);
		assertEquals(pathInfo, locator.getPath());
		assertEquals(pathInfo, locator.toString());

		final StringBuilder bf = new StringBuilder();
		locator.appendPath(bf);
		assertEquals(pathInfo, bf.toString());
	}
}

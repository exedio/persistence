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

import static com.exedio.cope.pattern.MediaUrlItem.TYPE;
import static com.exedio.cope.pattern.MediaUrlItem.fileName;
import static com.exedio.cope.pattern.MediaUrlItem.fileNameSecure;
import static com.exedio.cope.pattern.MediaUrlItem.fotoName;
import static com.exedio.cope.pattern.MediaUrlItem.fotoNameSecure;
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

	private MediaUrlItem named, anond;

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
				fotoName,
				fotoName.getBody(),
				fotoName.getLastModified(),
				fotoName.getUnison(),
				fotoNameSecure,
				fotoNameSecure.getBody(),
				fotoNameSecure.getLastModified(),
				fotoNameSecure.getUnison(),
				fileName,
				fileName.getBody(),
				fileName.getLastModified(),
				fileName.getUnison(),
				fileNameSecure,
				fileNameSecure.getBody(),
				fileNameSecure.getLastModified(),
				fileNameSecure.getUnison(),
			}), TYPE.getFeatures());

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertFalse(fotoName.isUrlGuessingPrevented());
		assertFalse(fileName.isUrlGuessingPrevented());
		assertTrue(fotoNameSecure.isUrlGuessingPrevented());
		assertTrue(fileNameSecure.isUrlGuessingPrevented());
		assertEquals(null, named.getFotoNameSecureURL());
		assertEquals(null, named.getFotoNameSecureLocator());
		assertEquals(null, anond.getFotoNameSecureURL());
		assertEquals(null, anond.getFotoNameSecureLocator());
		assertEquals(null, named.getFileNameSecureURL());
		assertEquals(null, named.getFileNameSecureLocator());
		assertEquals(null, anond.getFileNameSecureURL());
		assertEquals(null, anond.getFileNameSecureLocator());

		named.setFotoName(bytes4, "image/jpeg");
		named.setFotoNameSecure(bytes4, "image/jpeg");
		anond.setFotoName(bytes4, "image/jpeg");
		anond.setFotoNameSecure(bytes4, "image/jpeg");
		named.setFileName(bytes4, "foo/bar");
		named.setFileNameSecure(bytes4, "foo/bar");
		anond.setFileName(bytes4, "foo/bar");
		anond.setFileNameSecure(bytes4, "foo/bar");
		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertIt("MediaUrlItem/fotoName/", fotoName, named, "/name.jpg");
		assertIt("MediaUrlItem/fotoName/", fotoName, anond,      ".jpg");
		assertIt("MediaUrlItem/fileName/", fileName, named, "/name"    );
		assertIt("MediaUrlItem/fileName/", fileName, anond,      ""    );
		assertIt("MediaUrlItem/fotoNameSecure/", fotoNameSecure, named, "/name.jpg?t=MediaUrlItem.fotoNameSecure-MediaUrlItem-0");
		assertIt("MediaUrlItem/fotoNameSecure/", fotoNameSecure, anond,      ".jpg?t=MediaUrlItem.fotoNameSecure-MediaUrlItem-1");
		assertIt("MediaUrlItem/fileNameSecure/", fileNameSecure, named, "/name"+ "?t=MediaUrlItem.fileNameSecure-MediaUrlItem-0");
		assertIt("MediaUrlItem/fileNameSecure/", fileNameSecure, anond,          "?t=MediaUrlItem.fileNameSecure-MediaUrlItem-1");

		// TODO separate tests
		model.commit();
		model.disconnect();
		System.setProperty("media.url.secret", "valueOfMediaUrlSecret");
		model.connect(getConnectProperties());
		assertTrue(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		model.startTransaction("MediaUrlTest");
		assertIt("MediaUrlItem/fotoName/", fotoName, named, "/name.jpg");
		assertIt("MediaUrlItem/fotoName/", fotoName, anond,      ".jpg");
		assertIt("MediaUrlItem/fileName/", fileName, named, "/name"    );
		assertIt("MediaUrlItem/fileName/", fileName, anond,      ""    );
		assertIt("MediaUrlItem/fotoNameSecure/", fotoNameSecure, named, "/name.jpg?t=7231ebc6fdfa8f9230c4");
		assertIt("MediaUrlItem/fotoNameSecure/", fotoNameSecure, anond,      ".jpg?t=e7e9ffd94558828377b9");
		assertIt("MediaUrlItem/fileNameSecure/", fileNameSecure, named, "/name"+ "?t=61fc960d681bee64248c");
		assertIt("MediaUrlItem/fileNameSecure/", fileNameSecure, anond,          "?t=36894b09b797b511ecb1");
	}

	private void assertIt(final String prefix, final Media path, final MediaUrlItem item, final String postfix)
	{
		final String pathInfo = prefix + item.getCopeID() + postfix;
		assertEquals(mediaRootUrl + pathInfo, path.getURL(item));

		{
			@SuppressWarnings("deprecation") // OK: testing deprecated api
			final String url = path.getNamedURL(item, null);
			assertEquals(mediaRootUrl + pathInfo, url);
		}
		{
			@SuppressWarnings("deprecation") // OK: testing deprecated api
			final String url = path.getNamedURL(item, "");
			assertEquals(mediaRootUrl + pathInfo, url);
		}
		{
			@SuppressWarnings("deprecation") // OK: testing deprecated api
			final String url = path.getNamedURL(item, "hallo");
			assertEquals(mediaRootUrl + pathInfo, url);
		}

		assertLocator(path, pathInfo, path.getLocator(item));
	}
}

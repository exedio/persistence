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
import static com.exedio.cope.pattern.MediaUrlItem.catchphrase;
import static com.exedio.cope.pattern.MediaUrlItem.file;
import static com.exedio.cope.pattern.MediaUrlItem.fileFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fileSecure;
import static com.exedio.cope.pattern.MediaUrlItem.foto;
import static com.exedio.cope.pattern.MediaUrlItem.fotoFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecure;

import java.util.Arrays;
import java.util.Date;

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
		named = deleteOnTearDown(new MediaUrlItem("phrase"));
		anond = deleteOnTearDown(new MediaUrlItem(null));
	}

	public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				catchphrase,
				foto,
				foto.getBody(),
				foto.getLastModified(),
				foto.getUnison(),
				fotoSecure,
				fotoSecure.getBody(),
				fotoSecure.getLastModified(),
				fotoSecure.getUnison(),
				fotoFinger,
				fotoFinger.getBody(),
				fotoFinger.getLastModified(),
				fotoFinger.getUnison(),
				file,
				file.getBody(),
				file.getLastModified(),
				file.getUnison(),
				fileSecure,
				fileSecure.getBody(),
				fileSecure.getLastModified(),
				fileSecure.getUnison(),
				fileFinger,
				fileFinger.getBody(),
				fileFinger.getLastModified(),
				fileFinger.getUnison(),
			}), TYPE.getFeatures());

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertFalse(foto.isUrlGuessingPrevented());
		assertFalse(file.isUrlGuessingPrevented());
		assertFalse(fotoFinger.isUrlGuessingPrevented());
		assertFalse(fileFinger.isUrlGuessingPrevented());
		assertTrue(fotoSecure.isUrlGuessingPrevented());
		assertTrue(fileSecure.isUrlGuessingPrevented());

		assertFalse(foto.isUrlFingerPrinted());
		assertFalse(file.isUrlFingerPrinted());
		assertFalse(fotoSecure.isUrlFingerPrinted());
		assertFalse(fileSecure.isUrlFingerPrinted());
		assertTrue(fotoFinger.isUrlFingerPrinted());
		assertTrue(fileFinger.isUrlFingerPrinted());

		assertEquals(null, named.getFotoSecureURL());
		assertEquals(null, named.getFotoSecureLocator());
		assertEquals(null, anond.getFotoSecureURL());
		assertEquals(null, anond.getFotoSecureLocator());
		assertEquals(null, named.getFileSecureURL());
		assertEquals(null, named.getFileSecureLocator());
		assertEquals(null, anond.getFileSecureURL());
		assertEquals(null, anond.getFileSecureLocator());

		assertEquals(null, named.getFotoFingerURL());
		assertEquals(null, named.getFotoFingerLocator());
		assertEquals(null, anond.getFotoFingerURL());
		assertEquals(null, anond.getFotoFingerLocator());
		assertEquals(null, named.getFileFingerURL());
		assertEquals(null, named.getFileFingerLocator());
		assertEquals(null, anond.getFileFingerURL());
		assertEquals(null, anond.getFileFingerLocator());

		named.setFoto(bytes4, "image/jpeg");
		named.setFotoSecure(bytes4, "image/jpeg");
		named.setFotoFinger(bytes4, "image/jpeg");
		anond.setFoto(bytes4, "image/jpeg");
		anond.setFotoSecure(bytes4, "image/jpeg");
		anond.setFotoFinger(bytes4, "image/jpeg");
		named.setFile(bytes4, "foo/bar");
		named.setFileSecure(bytes4, "foo/bar");
		named.setFileFinger(bytes4, "foo/bar");
		anond.setFile(bytes4, "foo/bar");
		anond.setFileSecure(bytes4, "foo/bar");
		anond.setFileFinger(bytes4, "foo/bar");

		fotoFinger.getLastModified().set(named, new Date(23 + 128)); // XC
		fotoFinger.getLastModified().set(anond, new Date(23 + 192)); // XD
		fileFinger.getLastModified().set(named, new Date(24 + 128)); // YC
		fileFinger.getLastModified().set(anond, new Date(24 + 192)); // YD

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertIt("MediaUrlItem/foto/", foto, named, "/phrase.jpg");
		assertIt("MediaUrlItem/foto/", foto, anond,        ".jpg");
		assertIt("MediaUrlItem/file/", file, named, "/phrase"    );
		assertIt("MediaUrlItem/file/", file, anond,        ""    );
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, named, "/phrase.jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-0");
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, anond,        ".jpg?t=MediaUrlItem.fotoSecure-MediaUrlItem-1");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, named, "/phrase"+ "?t=MediaUrlItem.fileSecure-MediaUrlItem-0");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, anond,            "?t=MediaUrlItem.fileSecure-MediaUrlItem-1");
		assertIt("MediaUrlItem/fotoFinger/.fXC/", fotoFinger, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoFinger/.fXD/", fotoFinger, anond,        ".jpg");
		assertIt("MediaUrlItem/fileFinger/.fYC/", fileFinger, named, "/phrase"    );
		assertIt("MediaUrlItem/fileFinger/.fYD/", fileFinger, anond,        ""    );

		// TODO separate tests
		model.commit();
		model.disconnect();
		System.setProperty("media.url.secret", "valueOfMediaUrlSecret");
		model.connect(getConnectProperties());
		assertTrue(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		model.startTransaction("MediaUrlTest");
		assertIt("MediaUrlItem/foto/", foto, named, "/phrase.jpg");
		assertIt("MediaUrlItem/foto/", foto, anond,        ".jpg");
		assertIt("MediaUrlItem/file/", file, named, "/phrase"    );
		assertIt("MediaUrlItem/file/", file, anond,        ""    );
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, named, "/phrase.jpg?t=c156b63cb8a39ae5f16c");
		assertIt("MediaUrlItem/fotoSecure/", fotoSecure, anond,        ".jpg?t=f3da9d7e6856a2f9df6c");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, named, "/phrase"+ "?t=91f7b44e250a56f61ae9");
		assertIt("MediaUrlItem/fileSecure/", fileSecure, anond,            "?t=faf24676503317102086");
		assertIt("MediaUrlItem/fotoFinger/.fXC/", fotoFinger, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoFinger/.fXD/", fotoFinger, anond,        ".jpg");
		assertIt("MediaUrlItem/fileFinger/.fYC/", fileFinger, named, "/phrase"    );
		assertIt("MediaUrlItem/fileFinger/.fYD/", fileFinger, anond,        ""    );
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

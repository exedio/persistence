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
import static com.exedio.cope.pattern.MediaUrlItem.file;
import static com.exedio.cope.pattern.MediaUrlItem.fileFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fileSecure;
import static com.exedio.cope.pattern.MediaUrlItem.foto;
import static com.exedio.cope.pattern.MediaUrlItem.fotoFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecure;

import com.exedio.cope.AbstractRuntimeModelTest;
import java.util.Date;

public final class MediaUrlTest extends AbstractRuntimeModelTest
{
	public MediaUrlTest()
	{
		super(MediaUrlModelTest.MODEL);
	}

	private MediaUrlItem named, anond;

	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		named = new MediaUrlItem("phrase");
		anond = new MediaUrlItem(null);
	}

	@Test public void testIt()
	{
		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));

		assertEquals(null, named.getFotoSecureLocator());
		assertEquals(null, anond.getFotoSecureLocator());
		assertEquals(null, named.getFileSecureLocator());
		assertEquals(null, anond.getFileSecureLocator());

		assertEquals(null, named.getFotoFingerLocator());
		assertEquals(null, anond.getFotoFingerLocator());
		assertEquals(null, named.getFileFingerLocator());
		assertEquals(null, anond.getFileFingerLocator());

		final byte[] bytes  = {-86,122,-8,23};
		named.setFoto(bytes, "image/jpeg");
		named.setFotoSecure(bytes, "image/jpeg");
		named.setFotoFinger(bytes, "image/jpeg");
		anond.setFoto(bytes, "image/jpeg");
		anond.setFotoSecure(bytes, "image/jpeg");
		anond.setFotoFinger(bytes, "image/jpeg");
		named.setFile(bytes, "foo/bar");
		named.setFileSecure(bytes, "foo/bar");
		named.setFileFinger(bytes, "foo/bar");
		anond.setFile(bytes, "foo/bar");
		anond.setFileSecure(bytes, "foo/bar");
		anond.setFileFinger(bytes, "foo/bar");

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

	@Test public void testFingerprintLimit()
	{
		if(model.getConnectProperties().isSupportDisabledForPreparedStatements() || postgresql)
			return;

		fileFinger.getLastModified().set(anond, new Date(Long.MIN_VALUE + 2));
		assertIt("MediaUrlItem/fileFinger/.f.-_________H/", fileFinger, anond, "");

		fileFinger.getLastModified().set(anond, new Date(Long.MIN_VALUE + 1));
		assertIt("MediaUrlItem/fileFinger/.f.__________H/", fileFinger, anond, "");

		fileFinger.getLastModified().set(anond, new Date(Long.MIN_VALUE));
		assertEquals(new Date(Long.MIN_VALUE), fileFinger.getLastModified().get(anond));
		assertIt("MediaUrlItem/fileFinger/.f.__________H/", fileFinger, anond, "");
	}

	private static void assertIt(final String prefix, final Media path, final MediaUrlItem item, final String postfix)
	{
		final String pathInfo = prefix + item.getCopeID() + postfix;
		assertLocator(path, pathInfo, path.getLocator(item));
		assertSame(item, path.getLocator(item).getItem());
	}
}

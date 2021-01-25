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
import static com.exedio.cope.pattern.MediaUrlItem.fileSecFin;
import static com.exedio.cope.pattern.MediaUrlItem.fileSecure;
import static com.exedio.cope.pattern.MediaUrlItem.foto;
import static com.exedio.cope.pattern.MediaUrlItem.fotoFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecFin;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecure;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.exedio.cope.ConnectProperties;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.util.Sources;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class MediaUrlTest extends TestWithEnvironment
{
	public MediaUrlTest()
	{
		super(MediaUrlModelTest.MODEL);
	}

	private MediaUrlItem named, anond;

	@BeforeEach void setUp()
	{
		named = new MediaUrlItem("phrase");
		anond = new MediaUrlItem(null);
	}

	@Test void testIt()
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

		assertEquals(null, named.getFotoSecFinLocator());
		assertEquals(null, anond.getFotoSecFinLocator());
		assertEquals(null, named.getFileSecFinLocator());
		assertEquals(null, anond.getFileSecFinLocator());

		final byte[] bytes  = {-86,122,-8,23};
		named.setFoto(bytes, "image/jpeg");
		named.setFotoSecure(bytes, "image/jpeg");
		named.setFotoFinger(bytes, "image/jpeg");
		named.setFotoSecFin(bytes, "image/jpeg");
		anond.setFoto(bytes, "image/jpeg");
		anond.setFotoSecure(bytes, "image/jpeg");
		anond.setFotoFinger(bytes, "image/jpeg");
		anond.setFotoSecFin(bytes, "image/jpeg");
		named.setFile(bytes, "foo/bar");
		named.setFileSecure(bytes, "foo/bar");
		named.setFileFinger(bytes, "foo/bar");
		named.setFileSecFin(bytes, "foo/bar");
		anond.setFile(bytes, "foo/bar");
		anond.setFileSecure(bytes, "foo/bar");
		anond.setFileFinger(bytes, "foo/bar");
		anond.setFileSecFin(bytes, "foo/bar");

		fotoFinger.getLastModified().set(named, new Date(23 + 128)); // XC
		fotoFinger.getLastModified().set(anond, new Date(23 + 192)); // XD
		fileFinger.getLastModified().set(named, new Date(24 + 128)); // YC
		fileFinger.getLastModified().set(anond, new Date(24 + 192)); // YD
		fotoSecFin.getLastModified().set(named, new Date(49 + 128)); // xC
		fotoSecFin.getLastModified().set(anond, new Date(49 + 192)); // xD
		fileSecFin.getLastModified().set(named, new Date(50 + 128)); // yC
		fileSecFin.getLastModified().set(anond, new Date(50 + 192)); // yD

		assertFalse(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		assertIt("MediaUrlItem/foto/", foto, named, "/phrase.jpg");
		assertIt("MediaUrlItem/foto/", foto, anond,        ".jpg");
		assertIt("MediaUrlItem/file/", file, named, "/phrase"    );
		assertIt("MediaUrlItem/file/", file, anond,        ""    );
		assertIt("MediaUrlItem/fotoSecure/.tMediaUrlItem.fotoSecure-MediaUrlItem-0/", fotoSecure, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoSecure/.tMediaUrlItem.fotoSecure-MediaUrlItem-1/", fotoSecure, anond,        ".jpg");
		assertIt("MediaUrlItem/fileSecure/.tMediaUrlItem.fileSecure-MediaUrlItem-0/", fileSecure, named, "/phrase"    );
		assertIt("MediaUrlItem/fileSecure/.tMediaUrlItem.fileSecure-MediaUrlItem-1/", fileSecure, anond, ""           );
		assertIt("MediaUrlItem/fotoFinger/.fXC/", fotoFinger, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoFinger/.fXD/", fotoFinger, anond,        ".jpg");
		assertIt("MediaUrlItem/fileFinger/.fYC/", fileFinger, named, "/phrase"    );
		assertIt("MediaUrlItem/fileFinger/.fYD/", fileFinger, anond,        ""    );
		assertIt("MediaUrlItem/fotoSecFin/.fxC/.tMediaUrlItem.fotoSecFin-MediaUrlItem-0/", fotoSecFin, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoSecFin/.fxD/.tMediaUrlItem.fotoSecFin-MediaUrlItem-1/", fotoSecFin, anond,        ".jpg");
		assertIt("MediaUrlItem/fileSecFin/.fyC/.tMediaUrlItem.fileSecFin-MediaUrlItem-0/", fileSecFin, named, "/phrase"    );
		assertIt("MediaUrlItem/fileSecFin/.fyD/.tMediaUrlItem.fileSecFin-MediaUrlItem-1/", fileSecFin, anond, ""           );

		// TODO separate tests
		model.commit();
		model.disconnect();
		final java.util.Properties secretProperties = new java.util.Properties();
		secretProperties.setProperty("media.url.secret", "valueOfMediaUrlSecret");
		model.connect(ConnectProperties.create(Sources.cascade(
				Sources.view(secretProperties, "Media Secret"),
				copeRule.getConnectProperties().getSourceObject()
		)));
		assertTrue(MediaPath.isUrlGuessingPreventedSecurely(model.getConnectProperties()));
		model.startTransaction("MediaUrlTest");
		assertIt("MediaUrlItem/foto/", foto, named, "/phrase.jpg");
		assertIt("MediaUrlItem/foto/", foto, anond,        ".jpg");
		assertIt("MediaUrlItem/file/", file, named, "/phrase"    );
		assertIt("MediaUrlItem/file/", file, anond,        ""    );
		assertIt("MediaUrlItem/fotoSecure/.tc156b63cb8a39ae5f16c/", fotoSecure, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoSecure/.tf3da9d7e6856a2f9df6c/", fotoSecure, anond,        ".jpg");
		assertIt("MediaUrlItem/fileSecure/.t91f7b44e250a56f61ae9/", fileSecure, named, "/phrase"    );
		assertIt("MediaUrlItem/fileSecure/.tfaf24676503317102086/", fileSecure, anond, ""           );
		assertIt("MediaUrlItem/fotoFinger/.fXC/", fotoFinger, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoFinger/.fXD/", fotoFinger, anond,        ".jpg");
		assertIt("MediaUrlItem/fileFinger/.fYC/", fileFinger, named, "/phrase"    );
		assertIt("MediaUrlItem/fileFinger/.fYD/", fileFinger, anond,        ""    );
		assertIt("MediaUrlItem/fotoSecFin/.fxC/.t3a497d702eb06e975e57/", fotoSecFin, named, "/phrase.jpg");
		assertIt("MediaUrlItem/fotoSecFin/.fxD/.t4f6869b35d6e4d073ef6/", fotoSecFin, anond,        ".jpg");
		assertIt("MediaUrlItem/fileSecFin/.fyC/.tb15a5b9c299166a4f772/", fileSecFin, named, "/phrase"    );
		assertIt("MediaUrlItem/fileSecFin/.fyD/.tce0766a16ac118054214/", fileSecFin, anond, ""           );

		model.commit();
		model.disconnect();
		model.connect(ConnectProperties.create(copeRule.getConnectProperties().getSourceObject()));
	}

	private static void assertIt(final String prefix, final Media path, final MediaUrlItem item, final String postfix)
	{
		final String pathInfo = prefix + item.getCopeID() + postfix;
		assertLocator(path, pathInfo, path.getLocator(item));
		assertSame(item, path.getLocator(item).getItem());
	}
}

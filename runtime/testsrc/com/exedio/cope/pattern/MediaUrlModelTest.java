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

import static com.exedio.cope.pattern.MediaUrlItem.TYPE;
import static com.exedio.cope.pattern.MediaUrlItem.catchphrase;
import static com.exedio.cope.pattern.MediaUrlItem.file;
import static com.exedio.cope.pattern.MediaUrlItem.fileFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fileSecure;
import static com.exedio.cope.pattern.MediaUrlItem.foto;
import static com.exedio.cope.pattern.MediaUrlItem.fotoFinger;
import static com.exedio.cope.pattern.MediaUrlItem.fotoSecure;

import com.exedio.cope.Feature;
import com.exedio.cope.Model;
import com.exedio.cope.junit.CopeAssert;
import java.util.Arrays;

public final class MediaUrlModelTest extends CopeAssert
{
	static final Model MODEL = new Model(MediaUrlItem.TYPE);

	static
	{
		MODEL.enableSerialization(MediaUrlModelTest.class, "MODEL");
	}

	@SuppressWarnings("static-method")
	@Test public void testIt()
	{
		assertEqualsUnmodifiable(Arrays.asList(new Feature[]{
				TYPE.getThis(),
				catchphrase,
				foto,
				foto.getBody(),
				foto.getLastModified(),
				fotoSecure,
				fotoSecure.getBody(),
				fotoSecure.getLastModified(),
				fotoFinger,
				fotoFinger.getBody(),
				fotoFinger.getLastModified(),
				file,
				file.getBody(),
				file.getLastModified(),
				fileSecure,
				fileSecure.getBody(),
				fileSecure.getLastModified(),
				fileFinger,
				fileFinger.getBody(),
				fileFinger.getLastModified(),
			}), TYPE.getFeatures());

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
	}
}

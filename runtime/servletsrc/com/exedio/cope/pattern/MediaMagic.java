/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;

final class MediaMagic
{
	private static Condition bodyMismatchesContentType(final Media media, final byte[] magic, final String... contentTypes)
	{
		final Condition[] contentTypeConditions = new Condition[contentTypes.length];
		for(int i = 0; i<contentTypes.length; i++)
			contentTypeConditions[i] = media.contentTypeEqual(contentTypes[i]);
		return Cope.or(contentTypeConditions).and(media.getBody().startsWith(magic).not());
	}

	static Condition bodyMismatchesContentType(final Media media)
	{
		return Cope.or(
				bodyMismatchesContentType(
						media,
						// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
						new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
						"image/jpeg", "image/pjpeg"),
				bodyMismatchesContentType(
						media,
						// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
						new byte[]{(byte)'G', (byte)'I', (byte)'F', (byte)'8'}, // TODO test for "GIF89a" or "GIF87a"
						"image/gif"),
				bodyMismatchesContentType(
						media,
						// RFC 2083 section 3.1. PNG file signature
						new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10},
						"image/png"),
				bodyMismatchesContentType(
						media,
						// http://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
						new byte[]{0, 0, 1, 0},
						"image/icon", "image/x-icon", "image/vnd.microsoft.icon"),
				bodyMismatchesContentType(
						media,
						// http://en.wikipedia.org/wiki/ZIP_(file_format)
						new byte[]{(byte)'P', (byte)'K', 0x03, 0x04},
						"application/zip", "application/java-archive"),
				bodyMismatchesContentType(
						media,
						// http://en.wikipedia.org/wiki/PDF
						new byte[]{(byte)'%', (byte)'P', (byte)'D', (byte)'F'},
						"application/pdf"));
	}

	private MediaMagic()
	{
		// prevent instantiation
	}
}

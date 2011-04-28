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
	private final byte[] magic;
	private final String type;
	private final String[] akaTypes;

	private MediaMagic(final byte[] magic, final String type, final String... akaTypes)
	{
		this.magic = magic;
		this.type = type;
		this.akaTypes = akaTypes;
	}

	String getType()
	{
		return type;
	}

	private boolean matches(final byte[] m)
	{
		if(m.length<magic.length)
			return false;

		for(int i = 0; i<magic.length; i++)
			if(magic[i]!=m[i])
				return false;

		return true;
	}

	private Condition mismatchesInstance(final Media media)
	{
		final Condition[] typeConditions = new Condition[1 + akaTypes.length];
		typeConditions[0] = media.contentTypeEqual(type);
		for(int i = 0; i<akaTypes.length; i++)
			typeConditions[i+1] = media.contentTypeEqual(akaTypes[i]);
		return Cope.or(typeConditions).and(media.getBody().startsWith(magic).not());
	}

	@Override
	public String toString()
	{
		return type;
	}


	private static final MediaMagic[] magics = new MediaMagic[]{

			new MediaMagic(
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
					"image/jpeg", "image/pjpeg"),
			new MediaMagic(
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)'G', (byte)'I', (byte)'F', (byte)'8'}, // TODO test for "GIF89a" or "GIF87a"
					"image/gif"),
			new MediaMagic(
					// RFC 2083 section 3.1. PNG file signature
					new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10},
					"image/png"),
			new MediaMagic(
					// http://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
					new byte[]{0, 0, 1, 0},
					"image/icon", "image/x-icon", "image/vnd.microsoft.icon"),
			new MediaMagic(
					// http://en.wikipedia.org/wiki/ZIP_(file_format)
					new byte[]{(byte)'P', (byte)'K', 0x03, 0x04},
					"application/zip", "application/java-archive"),
			new MediaMagic(
					// http://en.wikipedia.org/wiki/PDF
					new byte[]{(byte)'%', (byte)'P', (byte)'D', (byte)'F'},
					"application/pdf"),
	};

	static Condition mismatches(final Media media)
	{
		final Condition[] conditions = new Condition[magics.length];
		for(int i = 0; i<conditions.length; i++)
			conditions[i] = magics[i].mismatchesInstance(media);
		return Cope.or(conditions);
	}

	static MediaMagic forType(final String type)
	{
		if(type==null)
			throw new NullPointerException("type");

		for(final MediaMagic m : magics)
			if(type.equals(m.type))
				return m;

		return null;
	}

	static MediaMagic forMagic(final byte[] magic)
	{
		if(magic==null)
			throw new NullPointerException("magic");
		if(magic.length==0)
			throw new IllegalArgumentException("empty");

		for(final MediaMagic m : magics)
			if(m.matches(magic))
				return m;

		return null;
	}
}

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
	private final String name;
	private final String[] aliases;

	private MediaMagic(final byte[] magic, final String name, final String... aliases)
	{
		this.magic = magic;
		this.name = name;
		this.aliases = aliases;
	}

	String getName()
	{
		return name;
	}

	String getAllowedType(final Media media)
	{
		if(media.checkContentType(name))
			return name;

		for(final String alias : aliases)
			if(media.checkContentType(alias))
				return alias;

		return null;
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
		final Condition[] nameConditions = new Condition[1 + aliases.length];
		nameConditions[0] = media.contentTypeEqual(name);
		for(int i = 0; i<aliases.length; i++)
			nameConditions[i+1] = media.contentTypeEqual(aliases[i]);
		return Cope.or(nameConditions).and(media.getBody().startsWith(magic).not());
	}

	@Override
	public String toString()
	{
		return name;
	}


	private static final MediaMagic[] magics = new MediaMagic[]{

			new MediaMagic(
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
					"image/jpeg", "image/pjpeg"),
			new MediaMagic(
					// RFC 2083 section 3.1. PNG file signature
					new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10},
					"image/png"),
			new MediaMagic(
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)'G', (byte)'I', (byte)'F', (byte)'8'}, // TODO test for "GIF89a" or "GIF87a"
					"image/gif"),
			new MediaMagic(
					// http://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
					new byte[]{0, 0, 1, 0},
					"image/vnd.microsoft.icon", "image/icon", "image/x-icon"),
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

	static MediaMagic forName(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");

		for(final MediaMagic m : magics)
			if(name.equals(m.name))
				return m;

		return null;
	}

	static MediaMagic forNameAndAliases(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");

		for(final MediaMagic m : magics)
		{
			if(name.equals(m.name))
				return m;
			for(final String alias : m.aliases)
				if(name.equals(alias))
					return m;
		}

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

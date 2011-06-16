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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.util.Hex;

public final class MediaType
{
	private final byte[] magic;
	private final String name;
	private final String[] aliases;

	private MediaType(final byte[] magic, final String name, final String... aliases)
	{
		this.magic = magic;
		this.name = name;
		this.aliases = aliases;
		assert magic==null || magic.length<=MAGIC_MAX_LENGTH : Hex.encodeLower(magic);
	}

	public String getName()
	{
		return name;
	}

	public List<String> getAliases()
	{
		if( aliases.length>0 )
			return Collections.unmodifiableList( Arrays.asList(aliases) );

		return Collections.<String>emptyList();
	}

	public String getAllowed(final Media media)
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


	private static final int MAGIC_MAX_LENGTH = 8;

	public static int magicMaxLength() // use method to prevent the compiler from inlining
	{
		return MAGIC_MAX_LENGTH;
	}

	public static final String JPEG = "image/jpeg";
	public static final String PNG  = "image/png";
	public static final String GIF  = "image/gif";
	public static final String ICON = "image/vnd.microsoft.icon";
	public static final String ZIP  = "application/zip";
	public static final String PDF  = "application/pdf";
	public static final String JAVASCRIPT = "application/javascript";

	private static final MediaType[] types = new MediaType[]{

			new MediaType(
					null,
					// RFC 4329 section 7. JavaScript Media Types
					JAVASCRIPT, "text/javascript"),
			new MediaType(
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
					JPEG, "image/pjpeg"),
			new MediaType(
					// RFC 2083 section 3.1. PNG file signature
					new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10},
					PNG),
			new MediaType(
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)'G', (byte)'I', (byte)'F', (byte)'8'}, // TODO test for "GIF89a" or "GIF87a"
					GIF),
			new MediaType(
					// http://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
					new byte[]{0, 0, 1, 0},
					ICON, "image/icon", "image/x-icon"),
			new MediaType(
					// http://en.wikipedia.org/wiki/ZIP_(file_format)
					new byte[]{(byte)'P', (byte)'K', 0x03, 0x04},
					ZIP, "application/java-archive"),
			new MediaType(
					// http://en.wikipedia.org/wiki/PDF
					new byte[]{(byte)'%', (byte)'P', (byte)'D', (byte)'F'},
					PDF),
	};

	private static final MediaType[] typesWithMagic = retainMagic(types);

	private static MediaType[] retainMagic(final MediaType[] source)
	{
		final ArrayList<MediaType> result = new ArrayList<MediaType>(source.length);
		for(final MediaType t : source)
			if(t.magic!=null)
				result.add(t);
		return result.toArray(new MediaType[result.size()]);
	}

	static Condition mismatches(final Media media)
	{
		final Condition[] conditions = new Condition[typesWithMagic.length];
		for(int i = 0; i<conditions.length; i++)
			conditions[i] = typesWithMagic[i].mismatchesInstance(media);
		return Cope.or(conditions);
	}

	public static MediaType forName(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");

		for(final MediaType type : types)
			if(name.equals(type.name))
				return type;

		return null;
	}

	public static MediaType forNameAndAliases(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");

		for(final MediaType type : types)
		{
			if(name.equals(type.name))
				return type;
			for(final String alias : type.aliases)
				if(name.equals(alias))
					return type;
		}

		return null;
	}

	/**
	 * @param magic
	 *        must contain at least the first
	 *        {@link #magicMaxLength()} bytes of the file
	 *        and must not be empty.
	 */
	public static MediaType forMagic(final byte[] magic)
	{
		if(magic==null)
			throw new NullPointerException("magic");
		if(magic.length==0)
			throw new IllegalArgumentException("empty");

		for(final MediaType type : typesWithMagic)
			if(type.matches(magic))
				return type;

		return null;
	}

	public static MediaType forMagic(final File file) throws IOException
	{
		if(file==null)
			throw new NullPointerException("file");

		final byte[] bytes = new byte[(int)Math.min(file.length(), MAGIC_MAX_LENGTH)];
		final FileInputStream stream = new FileInputStream(file);
		try
		{
			final int bytesRead = stream.read(bytes);
			if(bytesRead!=bytes.length)
				throw new IOException("expected " + bytes.length + " bytes, but read " + bytesRead);
		}
		finally
		{
			stream.close();
		}
		return forMagic(bytes);
	}
}

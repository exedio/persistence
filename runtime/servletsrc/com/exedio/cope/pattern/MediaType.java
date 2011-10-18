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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.util.Hex;

public final class MediaType
{
	private final byte[] magic;
	final String extension;
	private final String name;
	private final String[] aliases;

	private MediaType(final String extension, final String name, final String... aliases)
	{
		this(extension, null, name, aliases);
	}

	private MediaType(final String extension, final byte[] magic, final String name, final String... aliases)
	{
		this.magic = magic;
		this.extension = extension;
		this.name = name;
		this.aliases = aliases;
		assert magic==null || magic.length<=MAGIC_MAX_LENGTH : Hex.encodeLower(magic);
		assert extension!=null;
	}

	public boolean hasMagic()
	{
		return magic!=null;
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

	void addNameAndAliases(final ArrayList<String> list)
	{
		list.add(name);
		list.addAll(getAliases());
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

			new MediaType(".csv",  "text/csv",
					"text/comma-separated-values",
					"application/csv"),
			new MediaType(".xml",  "text/xml",
					"text/xml-external-parsed-entity",
					"application/xml",
					"application/xhtml+xml",
					"application/xml-external-parsed-entity",
					"application/xml-dtd"),
			new MediaType(".html", "text/html"),
			new MediaType(".txt",  "text/plain"),
			new MediaType(".css",  "text/css",
					"application/x-pointplus"), // deprecated mime-type for files with extension .css
			new MediaType(
					".js",
					// RFC 4329 section 7. JavaScript Media Types
					JAVASCRIPT, "text/javascript", "application/x-javascript"),
			new MediaType(
					".jpg",
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
					JPEG, "image/pjpeg"),
			new MediaType(
					".png",
					// RFC 2083 section 3.1. PNG file signature
					new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10},
					PNG),
			new MediaType(
					".gif",
					// http://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)'G', (byte)'I', (byte)'F', (byte)'8'}, // TODO test for "GIF89a" or "GIF87a"
					GIF),
			new MediaType(
					".ico",
					// http://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
					new byte[]{0, 0, 1, 0},
					ICON, "image/icon", "image/x-icon"),
			new MediaType(
					".zip",
					// http://en.wikipedia.org/wiki/ZIP_(file_format)
					new byte[]{(byte)'P', (byte)'K', 0x03, 0x04},
					ZIP),
			new MediaType(
					".jar",
					new byte[]{(byte)'P', (byte)'K', 0x03, 0x04}, // same as ZIP
					"application/java-archive"),
			new MediaType(
					".pdf",
					// http://en.wikipedia.org/wiki/PDF
					new byte[]{(byte)'%', (byte)'P', (byte)'D', (byte)'F'},
					// http://tools.ietf.org/html/rfc3778
					PDF,
					"text/pdf" // seen on Firefox 5.0
			),
	};

	private static final HashMap<String, MediaType> typesByName         = new HashMap<String, MediaType>();
	private static final HashMap<String, MediaType> typesByNameAndAlias = new HashMap<String, MediaType>();

	static
	{
		for(final MediaType type : types)
		{
			put(typesByName, type.name, type);
			put(typesByNameAndAlias, type.name, type);
			for(final String alias : type.aliases)
				put(typesByNameAndAlias, alias, type);
		}
	}

	private static void put(
			final HashMap<String, MediaType> map,
			final String key,
			final MediaType value)
	{
		if(map.put(key, value)!=null)
			throw new RuntimeException(key);
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

		return typesByName.get(name);
	}

	public static MediaType forNameAndAliases(final String name)
	{
		if(name==null)
			throw new NullPointerException("name");

		return typesByNameAndAlias.get(name);
	}

	// magic

	/**
	 * @param magic
	 *        must contain at least the first
	 *        {@link #magicMaxLength()} bytes of the file
	 *        and must not be empty.
	 */
	public static Set<MediaType> forMagics(final byte[] magic)
	{
		if(magic==null)
			throw new NullPointerException("magic");
		if(magic.length==0)
			throw new IllegalArgumentException("empty");

		final LinkedHashSet<MediaType> result = new LinkedHashSet<MediaType>();
		for(final Magic type : typesWithMagic)
			if(type.matches(magic))
				type.addAllTypes(result);

		return Collections.unmodifiableSet(result);
	}

	public static Set<MediaType> forMagics(final File file) throws IOException
	{
		return forMagics(readMagic(file));
	}

	private static byte[] readMagic(final File file) throws IOException
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
		return bytes;
	}

	private static final class Magic
	{
		private final byte[] magic;
		private final MediaType[] types;
		private final String[] typeNames;

		Magic(final byte[] magic, final ArrayList<MediaType> types)
		{
			this.magic = magic;
			this.types = types.toArray(new MediaType[types.size()]);
			this.typeNames = names(this.types);
			assert magic!=null && magic.length<=MAGIC_MAX_LENGTH : Hex.encodeLower(magic);
		}

		private static final String[] names(final MediaType[] types)
		{
			final ArrayList<String> result = new ArrayList<String>();
			for(final MediaType type : types)
				type.addNameAndAliases(result);
			return result.toArray(new String[result.size()]);
		}

		void addAllTypes(final LinkedHashSet<MediaType> set)
		{
			set.addAll(Arrays.asList(types));
		}

		boolean matches(final byte[] m)
		{
			final int l = magic.length;
			if(m.length<l)
				return false;

			for(int i = 0; i<l; i++)
				if(magic[i]!=m[i])
					return false;

			return true;
		}

		Condition mismatchesInstance(final Media media)
		{
			final Condition[] nameConditions = new Condition[typeNames.length];
			for(int i = 0; i<typeNames.length; i++)
				nameConditions[i] = media.contentTypeEqual(typeNames[i]);
			return Cope.or(nameConditions).and(media.getBody().startsWith(magic).not());
		}
	}

	private static final Magic[] typesWithMagic = retainMagic(types);

	private static Magic[] retainMagic(final MediaType[] source)
	{
		final LinkedHashMap<String, ArrayList<MediaType>> map =
			new LinkedHashMap<String, ArrayList<MediaType>>();
		for(final MediaType t : source)
			if(t.magic!=null)
			{
				final String magicString = Hex.encodeLower(t.magic);
				ArrayList<MediaType> list = map.get(magicString);
				if(list==null)
				{
					list = new ArrayList<MediaType>();
					map.put(magicString, list);
				}
				list.add(t);
			}
		final Magic[] result = new Magic[map.size()];
		int i = 0;
		for(final Map.Entry<String, ArrayList<MediaType>> e : map.entrySet())
			result[i++] = new Magic(Hex.decodeLower(e.getKey()), e.getValue());
		return result;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link MediaType#forMagics(byte[])} instead.
	 */
	@Deprecated
	public static MediaType forMagic(final byte[] magic)
	{
		return first(forMagics(magic));
	}

	/**
	 * @deprecated Use {@link MediaType#forMagics(File)} instead.
	 */
	@Deprecated
	public static MediaType forMagic(final File file) throws IOException
	{
		return first(forMagics(file));
	}

	@Deprecated
	private static MediaType first(final Set<MediaType> set)
	{
		return set.isEmpty() ? null : set.iterator().next();
	}
}

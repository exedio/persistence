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

import static java.util.Objects.requireNonNull;

import com.exedio.cope.Condition;
import com.exedio.cope.Cope;
import com.exedio.cope.util.Hex;
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

public final class MediaType
{
	private static final MediaType[] EMPTY_MEDIA_TYPE_ARRAY = new MediaType[0];
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private final byte[] magic;
	private final String[] extensions;
	private final String name;
	private final String[] aliases;

	private MediaType(final String extension, final String name, final String... aliases)
	{
		this(extension, null, name, aliases);
	}

	private MediaType(final String extension, final byte[] magic, final String name, final String... aliases)
	{
		this(new String[]{extension}, magic, name, aliases);
	}

	private MediaType(final String[] extensions, final byte[] magic, final String name, final String... aliases)
	{
		this.magic = magic;
		this.extensions = extensions;
		this.name = name;
		this.aliases = aliases;
		assert magic==null || magic.length<=MAGIC_MAX_LENGTH : Hex.encodeLower(magic);
		assert extensions!=null;
		assert extensions.length>0;
	}

	public boolean hasMagic()
	{
		return magic!=null;
	}

	/**
	 * Returns a list of file extensions for this media type. The most common is used assign to index 0.
	 * The result does include the leading dot, for example ".jpg".
	 */
	public List<String> getExtensions()
	{
		return Collections.unmodifiableList( Arrays.asList(extensions) );
	}

	/**
	 * Returns the typical file extension for this media type.
	 * The result does include the leading dot, for example ".jpg".
	 */
	public String getDefaultExtension()
	{
		return extensions[0];
	}

	/**
	 * @deprecated
	 * There are multiple extensions now.
	 * Please use {@link #getDefaultExtension()} or {@link #getExtensions()}.
	 */
	@Deprecated
	public String getExtension()
	{
		return getDefaultExtension();
	}

	public String getName()
	{
		return name;
	}

	public List<String> getAliases()
	{
		if( aliases.length>0 )
			return Collections.unmodifiableList( Arrays.asList(aliases) );

		return Collections.emptyList();
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
	public static final String WEBP = "image/webp";
	public static final String TIFF = "image/tiff";
	public static final String ICON = "image/vnd.microsoft.icon";
	public static final String SVG  = "image/svg+xml";
	public static final String ZIP  = "application/zip";
	public static final String JAR  = "application/java-archive";
	public static final String PDF  = "application/pdf";
	public static final String JAVASCRIPT = "application/javascript";
	public static final String EOT  = "application/vnd.ms-fontobject";
	public static final String WOFF = "application/font-woff"; // TODO rename to WOFF1
	public static final String WOFF2= "font/woff2";
	public static final String TTF  = "application/x-font-ttf";
	public static final String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	public static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	private static final byte[] ZIP_MAGIC = {(byte)'P', (byte)'K', 0x03, 0x04};

	private static final MediaType[] types = {

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
					new String[]{".jpg",".jpeg"},
					// https://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF},
					JPEG, "image/pjpeg"),
			new MediaType(
					".png",
					// RFC 2083 section 3.1. PNG file signature
					new byte[]{(byte)137, 80, 78, 71, 13, 10, 26, 10},
					PNG, "image/x-png"),
			new MediaType(
					".gif",
					// https://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)'G', (byte)'I', (byte)'F', (byte)'8'}, // TODO test for "GIF89a" or "GIF87a"
					GIF),
			new MediaType(
					".webp",
					// https://en.wikipedia.org/wiki/WebP
					new byte[]{(byte)'R', (byte)'I', (byte)'F', (byte)'F'},
					WEBP),
			new MediaType(
					new String[]{".tif",".tiff"},
					// https://en.wikipedia.org/wiki/Magic_number_(programming)#Magic_numbers_in_files
					new byte[]{(byte)'I', (byte)'I', 42, 0}, // TODO allow MM (big endian) as well
					TIFF),
			new MediaType(
					".ico",
					// https://en.wikipedia.org/wiki/ICO_(icon_image_file_format)
					new byte[]{0, 0, 1, 0},
					ICON, "image/icon", "image/x-icon"),
			new MediaType(
					".zip",
					// https://en.wikipedia.org/wiki/ZIP_(file_format)
					ZIP_MAGIC,
					ZIP),
			new MediaType(
					".jar",
					ZIP_MAGIC,
					JAR),
			new MediaType(
					".docx",
					ZIP_MAGIC,
					DOCX),
			new MediaType(
					".svg",
					// https://www.w3.org/TR/SVG/mimereg.html
					SVG),
			new MediaType(
					".eot",
					// https://www.w3.org/Submission/EOT/
					EOT),
			new MediaType(
					".woff",
					// https://www.w3.org/TR/WOFF/
					new byte[]{(byte)'w', (byte)'O', (byte)'F', (byte)'F'},
					WOFF,
					"font/woff", // https://tools.ietf.org/html/rfc8081#section-4.4.5
					"font/x-woff"),
			new MediaType(
					// still a draft
					".woff2",
					// https://www.w3.org/TR/2016/CR-WOFF2-20160315/
					new byte[]{(byte)'w', (byte)'O', (byte)'F', (byte)'2'},
					WOFF2),
			new MediaType(
					".ttf",
					// https://www.microsoft.com/typography/tt/ttf_spec/ttch02.doc
					new byte[]{0x00,0x01,0x00,0x00,0x00},
					TTF,
					"application/x-font-truetype",
					"font/ttf"),
			new MediaType(
					".pdf",
					// https://en.wikipedia.org/wiki/PDF
					new byte[]{(byte)'%', (byte)'P', (byte)'D', (byte)'F'},
					// https://tools.ietf.org/html/rfc3778
					PDF,
					"text/pdf" // seen on Firefox 5.0
			),
			new MediaType(
					// https://en.wikipedia.org/wiki/Office_Open_XML
					new String[]{".xlsx", ".xlsm"},
					ZIP_MAGIC,
					XLSX,
					"application/x-zip-compressed" // seen on IE8, Windows XP
			),
	};

	private static final HashMap<String, MediaType> typesByExtension    = new HashMap<>();
	private static final HashMap<String, MediaType> typesByName         = new HashMap<>();
	private static final HashMap<String, MediaType> typesByNameAndAlias = new HashMap<>();

	static
	{
		for(final MediaType type : types)
		{
			for (final String extension: type.extensions)
			{
				put(typesByExtension, extension, type);
			}
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
		if(map.putIfAbsent(key, value)!=null)
			throw new RuntimeException(">"+key+"< already exists");
	}


	static Condition mismatchesIfSupported(final Media media)
	{
		final Condition[] conditions = new Condition[typesWithMagic.length];
		//noinspection Java8ArraySetAll OK: performance
		for(int i = 0; i<conditions.length; i++)
			conditions[i] = typesWithMagic[i].mismatchesInstanceIfSupported(media);
		return Cope.or(conditions);
	}

	/**
	 * @param fileName the file name where to look for the extension
	 */
	public static MediaType forFileName(final String fileName)
	{
		final int pos = fileName.lastIndexOf('.');
		if(pos<=0) // dot at start is not an extension
			return null;

		return typesByExtension.get(fileName.substring(pos));
	}

	public static MediaType forName(final String name)
	{
		requireNonNull(name, "name");

		return typesByName.get(name);
	}

	public static MediaType forNameAndAliases(final String name)
	{
		requireNonNull(name, "name");

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

		final LinkedHashSet<MediaType> result = new LinkedHashSet<>();
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
		requireNonNull(file, "file");

		final byte[] bytes = new byte[(int)Math.min(file.length(), MAGIC_MAX_LENGTH)];
		try(FileInputStream stream = new FileInputStream(file))
		{
			final int bytesRead = stream.read(bytes);
			if(bytesRead!=bytes.length)
				throw new IOException("expected " + bytes.length + " bytes, but read " + bytesRead);
		}
		return bytes;
	}

	private static final class Magic
	{
		private final byte[] magic;
		private final MediaType[] types;
		private final String[] typeNames;

		Magic(final String magic, final ArrayList<MediaType> types)
		{
			this.magic = Hex.decodeLower(magic);
			this.types = types.toArray(EMPTY_MEDIA_TYPE_ARRAY);
			this.typeNames = names(this.types);
			assert magic!=null && this.magic.length<=MAGIC_MAX_LENGTH : magic;
		}

		private static String[] names(final MediaType[] types)
		{
			final ArrayList<String> result = new ArrayList<>();
			for(final MediaType type : types)
				type.addNameAndAliases(result);
			return result.toArray(EMPTY_STRING_ARRAY);
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

		Condition mismatchesInstanceIfSupported(final Media media)
		{
			final Condition[] nameConditions = new Condition[typeNames.length];
			for(int i = 0; i<typeNames.length; i++)
				nameConditions[i] = media.contentTypeEqual(typeNames[i]);
			return Cope.or(nameConditions).and(media.getBody().startsWithIfSupported(magic).not());
		}
	}

	private static final Magic[] typesWithMagic = retainMagic(types);

	private static Magic[] retainMagic(final MediaType[] source)
	{
		final LinkedHashMap<String, ArrayList<MediaType>> map = new LinkedHashMap<>();
		for(final MediaType t : source)
			if(t.magic!=null)
				map.computeIfAbsent(Hex.encodeLower(t.magic), k -> new ArrayList<>()).add(t);

		final Magic[] result = new Magic[map.size()];
		int i = 0;
		for(final Map.Entry<String, ArrayList<MediaType>> e : map.entrySet())
			result[i++] = new Magic(e.getKey(), e.getValue());
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

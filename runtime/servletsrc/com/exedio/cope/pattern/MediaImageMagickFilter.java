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

import static com.exedio.cope.util.StrictFile.delete;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrap;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class MediaImageMagickFilter extends MediaFilter implements MediaTestable
{
	private static final long serialVersionUID = 1l;

	public static final String ENABLE_PROPERTY = "com.exedio.cope.media.imagemagick";
	public static final String CONVERT_COMMAND_PROPERTY = "com.exedio.cope.media.convertcommand";

	private static final String DEFAULT_COMMAND_BINARY = "convert";

	private static final boolean enabled = Boolean.valueOf(System.getProperty(ENABLE_PROPERTY));
	private static final String convertCommand = System.getProperty(CONVERT_COMMAND_PROPERTY);
	private static final boolean osIsWindows = System.getProperty("os.name").startsWith("Windows");

	public static boolean isEnabled()
	{
		return enabled;
	}

	private static String getConvertBinary()
	{
		if ( convertCommand==null||convertCommand.equals("") )
		{
			if ( osIsWindows )
			{
				throw new RuntimeException(
					"on windows systems, the property \""+CONVERT_COMMAND_PROPERTY+"\" has to be set "+
					"when enabling imagemagick"
				);
			}
			return DEFAULT_COMMAND_BINARY;
		}
		else
		{
			return convertCommand;
		}
	}


	private static final HashSet<MediaType> supportedContentTypes =
			new HashSet<MediaType>(Arrays.asList(
					MediaType.forName(MediaType.JPEG),
					MediaType.forName(MediaType.PNG),
					MediaType.forName(MediaType.GIF)
			));

	private final Media source;
	private final MediaImageioFilter fallback;
	@SuppressFBWarnings("SE_BAD_FIELD") // OK: writeReplace
	private final MediaType constantOutputContentType;
	private final String[] options;

	public MediaImageMagickFilter(final Media source, final MediaImageioFilter fallback, final String[] options)
	{
		this(source, fallback, "image/jpeg", options);
	}

	public MediaImageMagickFilter(
			final Media source,
			final MediaImageioFilter fallback,
			final String outputContentType,
			final String[] options)
	{
		super(source);
		this.source = source;
		this.fallback = fallback;
		this.options = com.exedio.cope.misc.Arrays.copyOf(options);

		if(fallback==null)
			throw new RuntimeException(); // TODO test
		if(outputContentType!=null)
		{
			this.constantOutputContentType = supported(MediaType.forName(outputContentType));
			if(constantOutputContentType==null)
				throw new IllegalArgumentException("unsupported outputContentType >" + outputContentType + '<');
		}
		else
		{
			this.constantOutputContentType = null;
		}
	}

	private static MediaType supported(final MediaType type)
	{
		if(type==null)
			return null;
		return
			supportedContentTypes.contains(type) ? type : null;
	}

	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		final HashSet<String> result = new HashSet<String>();
		for(final MediaType type : supportedContentTypes)
		{
			result.add(type.getName());
			result.addAll(type.getAliases());
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Returns the content type of this filter.
	 * Return the same as {@link #getContentType(Item)}
	 * iff {@link #getContentType(Item)} return the same values for all items not return null.
	 * Otherwise returns null.
	 */
	public final String getOutputContentType()
	{
		return constantOutputContentType!=null ? constantOutputContentType.getName() : null;
	}

	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = source.getContentType(item);

		if(contentType==null)
			return null;

		final MediaType type = supported(MediaType.forNameAndAliases(contentType));
		if(type==null)
			return null;

		return (constantOutputContentType!=null?constantOutputContentType:type).getName();
	}

	@Override
	public final boolean isContentTypeWrapped()
	{
		return constantOutputContentType==null;
	}

	@Override
	public final Media.Log doGetIfModified(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item)
	throws IOException
	{
		if(!isEnabled())
			return fallback.doGetIfModified(request, response, item);

		final String contentType = source.getContentType(item);
		if(contentType==null)
			return isNull;

		final MediaType type = supported(MediaType.forNameAndAliases(contentType));
		if(type==null)
			return notComputable;

		final File outFile = execute(item, type);

		final long contentLength = outFile.length();
		if(contentLength<=0)
			throw new RuntimeException(String.valueOf(contentLength));
		if(contentLength<=Integer.MAX_VALUE)
			response.setContentLength((int)contentLength);

		response.setContentType(outputContentType(type).getName());

		final byte[] b = new byte[DataField.min(100*1024, contentLength)];
		final FileInputStream body = new FileInputStream(outFile);
		try
		{
			final ServletOutputStream out = response.getOutputStream();
			try
			{
				for(int len = body.read(b); len>=0; len = body.read(b))
					out.write(b, 0, len);

				return delivered;
			}
			finally
			{
				if(out!=null)
					out.close();
			}
		}
		finally
		{
			body.close();
			delete(outFile);
		}
	}

	@SuppressFBWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
	@Wrap(order=10, doc="Returns the body of {0}.", thrown=@Wrap.Thrown(IOException.class))
	public final byte[] get(final Item item) throws IOException
	{
		if(!isEnabled())
			return fallback.get(item);

		final String contentType = source.getContentType(item);
		if(contentType==null)
			return null;

		final MediaType type = supported(MediaType.forNameAndAliases(contentType));
		if(type==null)
			return null;

		final File outFile = execute(item, type);

		final long contentLength = outFile.length();
		if(contentLength<=0)
			throw new RuntimeException(String.valueOf(contentLength));
		if(contentLength>=Integer.MAX_VALUE)
			throw new RuntimeException("too large");

		final byte[] result = new byte[(int)contentLength];

		final FileInputStream body = new FileInputStream(outFile);
		try
		{
			final int readResult = body.read(result);
			if(readResult!=contentLength)
				throw new RuntimeException(String.valueOf(contentLength) + '/' + readResult);
		}
		finally
		{
			body.close();
			delete(outFile);
		}
		return result;
	}

	public void test() throws IOException
	{
		if(!isEnabled())
			return;

		final File  inFile = File.createTempFile(MediaImageMagickThumbnail.class.getName() + ".in."  + getID(), ".data");
		final File outFile = File.createTempFile(MediaImageMagickThumbnail.class.getName() + ".out." + getID(), outputContentType(MediaType.forName(MediaType.JPEG)).getExtension());

		final String[] command = new String[options.length+4];
		command[0] = getConvertBinary();
		command[1] = "-quiet";
		for(int i = 0; i<options.length; i++)
			command[i+2] = options[i];
		command[command.length-2] = inFile.getAbsolutePath();
		command[command.length-1] = outFile.getAbsolutePath();
		//System.out.println("-----------------"+Arrays.toString(command));

		final byte[] b = new byte[1580]; // size of the file plus 2 to detect larger file
		{
			final InputStream inStream = MediaImageMagickFilter.class.getResourceAsStream("MediaImageMagickFilter-test.jpg");
			try
			{
				final int inLength = inStream.read(b);
				if(inLength!=1578) // size of the file
					throw new RuntimeException(String.valueOf(inLength));
			}
			finally
			{
				inStream.close();
			}
		}
		{
			final FileOutputStream outStream = new FileOutputStream(inFile);
			try
			{
				outStream.write(b);
			}
			finally
			{
				outStream.close();
			}
		}

		final int exitValue = execute(command);
		if(exitValue!=0)
			throw new RuntimeException(
					"command " + Arrays.asList(command) +
					" exited with " + exitValue +
					" for feature " + getID() +
					", left " + inFile.getAbsolutePath() +
					" and " + outFile.getAbsolutePath() +
					( exitValue==4 ?
						" (if running on Windows, make sure ImageMagick convert.exe and " +
							"not \\Windows\\system32\\convert.exe is called)"
						: ""
					) );

		delete(inFile);
		delete(outFile);
	}

	private MediaType outputContentType(final MediaType inputContentType)
	{
		return
				this.constantOutputContentType!=null
				? this.constantOutputContentType
				: inputContentType;
	}

	private final File execute(final Item item, final MediaType contentType) throws IOException
	{
		final File  inFile = File.createTempFile(MediaImageMagickThumbnail.class.getName() + ".in."  + getID(), ".data");
		final File outFile = File.createTempFile(MediaImageMagickThumbnail.class.getName() + ".out." + getID(), outputContentType(contentType).getExtension());

		final String[] command = new String[options.length+4];
		command[0] = getConvertBinary();
		command[1] = "-quiet";
		for(int i = 0; i<options.length; i++)
			command[i+2] = options[i];
		command[command.length-2] = inFile.getAbsolutePath();
		command[command.length-1] = outFile.getAbsolutePath();
		//System.out.println("-----------------"+Arrays.toString(command));

		source.getBody(item, inFile);

		final int exitValue = execute(command);
		if(exitValue!=0)
			throw new RuntimeException(
					"command " + Arrays.asList(command) +
					" exited with " + exitValue +
					" for feature " + getID() +
					" and item " + item.getCopeID() +
					", left " + inFile.getAbsolutePath() +
					" and " + outFile.getAbsolutePath() +
					( exitValue==4 ?
						" (if running on Windows, make sure ImageMagick convert.exe and " +
							"not \\Windows\\system32\\convert.exe is called)"
						: ""
					) );

		delete(inFile);

		return outFile;
	}

	private int execute(final String[] command) throws IOException
	{
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		final int exitValue;
		final Process process = processBuilder.start();
		try
		{
			exitValue = process.waitFor();
		}
		catch(final InterruptedException e)
		{
			throw new RuntimeException(toString(), e);
		}

		// IMPLEMENTATION NOTE
		// Without the following three lines each run of this code will leave
		// three open file descriptors in the system. Using utility "lsof"
		// you will see the following:
		//    java <pid> <user> 52w FIFO 0,8 0t0 141903 pipe
		//    java <pid> <user> 53r FIFO 0,8 0t0 141904 pipe
		//    java <pid> <user> 54w FIFO 0,8 0t0 142576 pipe
		process.getInputStream ().close();
		process.getOutputStream().close();
		process.getErrorStream ().close();

		return exitValue;
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #isEnabled()} instead
	 */
	@Deprecated
	public static boolean isAvailable()
	{
		return isEnabled();
	}

	/**
	 * @deprecated Is no longer supported and returns an empty string.
	 */
	@Deprecated
	public static String getAvailabilityMessage()
	{
		return "";
	}
}

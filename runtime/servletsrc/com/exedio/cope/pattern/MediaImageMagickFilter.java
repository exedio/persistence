/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.SafeFile.delete;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.DataField;
import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;

import java.util.Arrays;

public class MediaImageMagickFilter extends MediaFilter
{
	private static final long serialVersionUID = 1l;
	
	public static final String ENABLE_PROPERTY = "com.exedio.cope.media.imagemagick";
	public static final String CONVERT_COMMAND_PROPERTY = "com.exedio.cope.media.convertcommand";
	
	private static final String DEFAULT_COMMAND_BINARY = "convert";
	// private static final String COMMAND_BINARY = "C:\\Programme\\ImageMagick-6.3.7-Q16\\convert.exe";

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
	
	
	private static final HashMap<String,String> supportedContentTypes = new HashMap<String,String>();
	
	static
	{
		supportedContentTypes.put("image/jpeg",  ".jpg");
		supportedContentTypes.put("image/pjpeg", ".jpg");
		supportedContentTypes.put("image/png",   ".png");
		supportedContentTypes.put("image/x-png", ".png");
		supportedContentTypes.put("image/gif",   ".gif");
	}
	
	private final Media source;
	private final MediaImageioFilter fallback;
	private final String outputContentType;
	private final String outputExtension;
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
		this.outputContentType = outputContentType;
		this.outputExtension = supportedContentTypes.get(outputContentType);
		this.options = com.exedio.cope.misc.Arrays.copyOf(options);
		
		if(fallback==null)
			throw new RuntimeException(); // TODO test
		if(outputContentType==null)
			throw new RuntimeException(); // TODO test
		if(outputExtension==null)
			throw new RuntimeException(outputContentType); // TODO test
	}

	@Override
	public List<Wrapper> getWrappers()
	{
		final ArrayList<Wrapper> result = new ArrayList<Wrapper>();
		result.addAll(super.getWrappers());

		result.add(
			new Wrapper("get").
			addComment("Returns the body of {0}.").
			setReturn(byte[].class).
			addThrows(IOException.class));
		
		return Collections.unmodifiableList(result);
	}
	
	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.unmodifiableSet(supportedContentTypes.keySet());
	}
	
	public final String getOutputContentType()
	{
		return outputContentType;
	}

	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = source.getContentType(item);

		return (contentType!=null&&supportedContentTypes.containsKey(contentType)) ? outputContentType : null;
	}

	@Override
	public final Media.Log doGetIfModified(
			final HttpServletResponse response,
			final Item item)
	throws IOException
	{
		if(!isEnabled())
			return fallback.doGetIfModified(response, item);
		
		final String contentType = source.getContentType(item);
		if(contentType==null)
			return isNull;
		
		if(!supportedContentTypes.containsKey(contentType))
			return notComputable;
		
		final File outFile = execute(item);

		final long contentLength = outFile.length();
		if(contentLength<=0)
			throw new RuntimeException(String.valueOf(contentLength));
		if(contentLength<=Integer.MAX_VALUE)
			response.setContentLength((int)contentLength);
		
		response.setContentType(outputContentType);
		
		final byte[] b = new byte[DataField.min(100*1024, contentLength)];
		FileInputStream body = null;
		try
		{
			body = new FileInputStream(outFile);
			ServletOutputStream out = null;
			try
			{
				out = response.getOutputStream();
	
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
			if(body!=null)
				body.close();
			delete(outFile);
		}
	}

	public final byte[] get(final Item item) throws IOException
	{
		if(!isEnabled())
			return fallback.get(item);
		
		final String contentType = source.getContentType(item);
		if(contentType==null)
			return null;
		
		if(!supportedContentTypes.containsKey(contentType))
			return null;
		
		final File outFile = execute(item);

		final long contentLength = outFile.length();
		if(contentLength<=0)
			throw new RuntimeException(String.valueOf(contentLength));
		if(contentLength>=Integer.MAX_VALUE)
			throw new RuntimeException("too large");
		
		final byte[] result = new byte[(int)contentLength];
		
		FileInputStream body = null;
		try
		{
			body = new FileInputStream(outFile);
			final int readResult = body.read(result);
			if(readResult!=contentLength)
				throw new RuntimeException(String.valueOf(contentLength) + '/' + readResult);
		}
		finally
		{
			if(body!=null)
				body.close();
			delete(outFile);
		}
		return result;
	}
	
	private final File execute(final Item item) throws IOException
	{
		final File inFile  = File.createTempFile("MediaImageMagickThumbnail.in." + getID(), ".data");
		final File outFile = File.createTempFile("MediaImageMagickThumbnail.out." + getID(), outputExtension);

		final String[] command = new String[options.length+4];
		command[0] = getConvertBinary();
		command[1] = "-quiet";
		for(int i = 0; i<options.length; i++)
			command[i+2] = options[i];
		command[command.length-2] = inFile.getAbsolutePath();
		command[command.length-1] = outFile.getAbsolutePath();
		//System.out.println("-----------------"+Arrays.toString(command));
		
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		
		source.getBody(item, inFile);
		final Process process = processBuilder.start();
		try { process.waitFor(); } catch(InterruptedException e) { throw new RuntimeException(toString(), e); }
		
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
		
		final int exitValue = process.exitValue();
		if(exitValue!=0)
			throw new RuntimeException(
					"process " + process +
					" (command " + Arrays.asList(command) + ")" +
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

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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.DataField;
import com.exedio.cope.Item;
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
	private final MediaFilter fallback;
	private final String outputContentType;
	private final String outputExtension;
	private final String[] options;

	public MediaImageMagickFilter(final Media source, final MediaFilter fallback, final String[] options)
	{
		this(source, fallback, "image/jpeg", options);
	}
	
	public MediaImageMagickFilter(
			final Media source,
			final MediaFilter fallback,
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

	private static final String COMMAND_QUIET  = "-quiet";

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
		
		final File inFile  = File.createTempFile("MediaImageMagickThumbnail.in." + getID(), ".data");
		final File outFile = File.createTempFile("MediaImageMagickThumbnail.out." + getID(), outputExtension);

		final String[] command = new String[options.length+4];
		command[0] = getConvertBinary();
		command[1] = COMMAND_QUIET;
		for(int i = 0; i<options.length; i++)
			command[i+2] = options[i];
		command[command.length-2] = inFile.getAbsolutePath();
		command[command.length-1] = outFile.getAbsolutePath();
		//System.out.println("-----------------"+Arrays.toString(command));
		
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		
		source.getBody(item, inFile);
		final Process process = processBuilder.start();
		try { process.waitFor(); } catch(InterruptedException e) { throw new RuntimeException(toString(), e); }
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
		
		if(!inFile.delete())
			throw new RuntimeException(inFile.toString());
		
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
			if(!outFile.delete())
				throw new RuntimeException(outFile.toString());
		}
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

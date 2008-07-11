/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

public class MediaImageMagickFilter extends MediaFilter
{
	private static String checkAvailable()
	{
		if(!"Linux".equals(System.getProperty("os.name")))
			return "its not a Linux system.";

		final ProcessBuilder processBuilder = new ProcessBuilder(COMMAND_BINARY, COMMAND_QUIET);
		
		final Process process;
		try
		{
			process = processBuilder.start();
		}
		catch(IOException e)
		{
			return COMMAND_BINARY + ' ' + COMMAND_QUIET + " does throw an IOException:" + e.getMessage();
		}
		
		try
		{
			process.waitFor();
		}
		catch(InterruptedException e)
		{
			return COMMAND_BINARY + ' ' + COMMAND_QUIET + " does throw an InterruptedException:" + e.getMessage();
		}
		
		final int exitValue = process.exitValue();
		if(exitValue!=0)
			return COMMAND_BINARY + ' ' + COMMAND_QUIET + " does return an exit value of " + exitValue + '.';
		
		return null;
	}
	
	private static volatile boolean availableChecked = false;
	private static final Object availableLock = new Object();
	private static volatile boolean available;
	private static volatile String availabilityMessage = null;
	
	public static boolean isAvailable()
	{
		if(availableChecked)
			return available;

		synchronized(availableLock)
		{
			// double checking
			if(availableChecked)
				return available;

			final String reasonNotAvailable = checkAvailable();
			available = reasonNotAvailable==null;
			availableChecked = true;
			
			availabilityMessage = "MediaImageMagickFilter " + ((reasonNotAvailable!=null) ? ("is NOT available because " + reasonNotAvailable) : "is available.");
			System.out.println(availabilityMessage);
			
			return available;
		}
	}
	
	public static String getAvailabilityMessage()
	{
		return availabilityMessage;
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
		this.options = options;
		
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

	private static final String COMMAND_BINARY = "convert";
	private static final String COMMAND_QUIET  = "-quiet";

	@Override
	public final Media.Log doGetIfModified(
			final HttpServletResponse response,
			final Item item,
			final String extension)
	throws IOException
	{
		if(!isAvailable())
			return fallback.doGetIfModified(response, item, extension);
		
		final String contentType = source.getContentType(item);
		if(contentType==null)
			return isNull;
		
		if(!supportedContentTypes.containsKey(contentType))
			return notComputable;
		
		final File inFile  = File.createTempFile("MediaImageMagickThumbnail.in." + getID(), ".data");
		final File outFile = File.createTempFile("MediaImageMagickThumbnail.out." + getID(), outputExtension);

		final String[] command = new String[options.length+4];
		command[0] = COMMAND_BINARY;
		command[1] = COMMAND_QUIET;
		for(int i = 0; i<options.length; i++)
			command[i+2] = options[i];
		command[command.length-2] = inFile.getAbsolutePath();
		command[command.length-1] = outFile.getAbsolutePath();
		//System.out.println("-----------------"+Arrays.toString(command));
		
		final ProcessBuilder processBuilder = new ProcessBuilder(command);
		
		source.getBody(item, inFile);
		final Process process = processBuilder.start();
		try { process.waitFor(); } catch(InterruptedException e) { throw new RuntimeException(e); }
		final int exitValue = process.exitValue();
		if(exitValue!=0)
			throw new RuntimeException(
					"process " + process +
					" exited with " + exitValue +
					", left " + inFile.getAbsolutePath() +
					" and " + outFile.getAbsolutePath());
		
		inFile.delete();
		
		final long contentLength = outFile.length();
		if(contentLength<=0)
			throw new RuntimeException(String.valueOf(contentLength));
		if(contentLength<=Integer.MAX_VALUE)
			response.setContentLength((int)contentLength);
		
		response.setContentType(outputContentType);
		
		final byte[] b = new byte[DataField.min(100*1024, contentLength)];
		FileInputStream body = null;
		ServletOutputStream out = null;
		try
		{
			body = new FileInputStream(outFile);
			out = response.getOutputStream();

			for(int len = body.read(b); len>=0; len = body.read(b))
				out.write(b, 0, len);

			return delivered;
		}
		finally
		{
			if(out!=null)
				out.close();
			if(body!=null)
				body.close();
			outFile.delete();
		}
	}
}

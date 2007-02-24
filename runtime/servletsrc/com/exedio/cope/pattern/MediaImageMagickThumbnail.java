/*
 * Copyright (C) 2004-2007  exedio GmbH (www.exedio.com)
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.DataField;
import com.exedio.cope.Item;

public final class MediaImageMagickThumbnail extends MediaFilter
{
	private final Media source;
	private final int boundX;
	private final int boundY;
	private final String[] options;

	private static final int MIN_BOUND = 5;
	private static final String outputContentType = "image/jpeg";
	
	private static final HashSet<String> supportedSourceContentTypes =
		new HashSet<String>(Arrays.asList("image/jpeg", "image/pjpeg", "image/png", "image/x-png", "image/gif"));
	
	public MediaImageMagickThumbnail(final Media source, final int boundX, final int boundY)
	{
		super(source);
		this.source = source;
		this.boundX = boundX;
		this.boundY = boundY;
		
		if(boundX<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundX);
		if(boundY<MIN_BOUND)
			throw new IllegalArgumentException("boundY must be " + MIN_BOUND + " or greater, but was " + boundY);

		this.options = new String[]{"-resize", String.valueOf(boundX) + 'x' + String.valueOf(boundY) + '>'};
	}
	
	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.unmodifiableSet(supportedSourceContentTypes);
	}

	public int getBoundX()
	{
		return boundX;
	}
	
	public int getBoundY()
	{
		return boundY;
	}
	
	@Override
	public final String getContentType(final Item item)
	{
		final String contentType = source.getContentType(item);

		return (contentType!=null&&supportedSourceContentTypes.contains(contentType)) ? outputContentType : null;
	}

	@Override
	public final Media.Log doGetIfModified(
			final HttpServletResponse response,
			final Item item,
			final String extension)
	throws ServletException, IOException
	{
		final String contentType = source.getContentType(item);
		if(contentType==null)
			return isNull;
		
		if(!supportedSourceContentTypes.contains(contentType))
			return notComputable;
		
		final File inFile  = File.createTempFile("MediaImageMagickThumbnail.in." + getID(), ".data");
		final File outFile = File.createTempFile("MediaImageMagickThumbnail.out." + getID(), ".jpg");

		final String[] command = new String[options.length+4];
		command[0] = "convert";
		command[1] = "-quiet";
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
			throw new RuntimeException("process " + process + " exited with " + exitValue + ", left " + inFile.getAbsolutePath() + " and " + outFile.getAbsolutePath());
		
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

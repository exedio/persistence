/*
 * Copyright (C) 2004-2006  exedio GmbH (www.exedio.com)
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exedio.cope.Item;

public final class MediaPnmThumbnail extends MediaFilter
{
	private final Media source;
	private final int boundX;
	private final int boundY;
	private final ProcessBuilder scaleBuilder;

	private static final int MIN_BOUND = 5;
	private static final String outputContentType = "image/jpeg";
	private static final ProcessBuilder encodeBuilder = new ProcessBuilder("pnmtojpeg");
	
	private static final HashMap<String, ProcessBuilder> decodeBuilders = new HashMap<String, ProcessBuilder>();
	
	static
	{
		final ProcessBuilder jpeg = new ProcessBuilder("jpegtopnm");
		decodeBuilders.put("image/jpeg", jpeg);
		// fix for MSIE behaviour
		decodeBuilders.put("image/pjpeg", jpeg);
		final ProcessBuilder png = new ProcessBuilder("pngtopnm");
		decodeBuilders.put("image/png", png);
		decodeBuilders.put("image/x-png", png);
		final ProcessBuilder gif = new ProcessBuilder("giftopnm");
		decodeBuilders.put("image/gif", gif);
	}
	
	public MediaPnmThumbnail(final Media source, final int boundX, final int boundY)
	{
		super(source);
		this.source = source;
		this.boundX = boundX;
		this.boundY = boundY;
		
		if(boundX<MIN_BOUND)
			throw new IllegalArgumentException("boundX must be " + MIN_BOUND + " or greater, but was " + boundX);
		if(boundY<MIN_BOUND)
			throw new IllegalArgumentException("boundY must be " + MIN_BOUND + " or greater, but was " + boundY);

		this.scaleBuilder = new ProcessBuilder("pnmscale", "-xysize", String.valueOf(boundX), String.valueOf(boundY));
	}
	
	@Override
	public final Set<String> getSupportedSourceContentTypes()
	{
		return Collections.unmodifiableSet(decodeBuilders.keySet());
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

		return (contentType!=null&&decodeBuilders.containsKey(contentType)) ? outputContentType : null;
	}

	@Override
	public final Media.Log doGetIfModified(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Item item,
			final String extension)
	throws ServletException, IOException
	{
		final String contentType = source.getContentType(item);
		if(contentType==null)
			return isNull;
		
		final ProcessBuilder decodeBuilder = decodeBuilders.get(contentType);
		if(decodeBuilder==null)
			return notComputable;
		
		try
		{
			final Process decode = decodeBuilder.start();

			final Process scale = scaleBuilder.start();
			final Thread decode2scale = transfer("1:decode2scale", 80000, decode.getInputStream(), scale.getOutputStream());

			final Process encode = encodeBuilder.start();
			final Thread scale2encode = transfer("2:scale2encode", 20000, scale.getInputStream(), encode.getOutputStream());

			final ByteArrayOutputStream body = new ByteArrayOutputStream();
			final Thread encode2out = transfer("3:encode2out", 2000, encode.getInputStream(), body);
			
			final OutputStream decodeOut = decode.getOutputStream();
			source.getBody(item, decodeOut);
			decodeOut.close();
			
			waitFor(encode);
			waitFor(scale);
			waitFor(decode);
			encode2out.join();
			scale2encode.join();
			decode2scale.join();
			
			response.setContentLength(body.size());

			final ServletOutputStream out = response.getOutputStream();
			try
			{
				body.writeTo(out);
				return delivered;
			}
			finally
			{
				out.close();
			}
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private static final void waitFor(final Process process) throws InterruptedException
	{
		process.waitFor();
		final int exitValue = process.exitValue();
		if(exitValue!=0)
			throw new RuntimeException("process " + process + " exited with " + exitValue);
	}
	
	private static final Thread transfer(final String name, final int bufferSize, final InputStream in, final OutputStream out)
	{
		final Thread result = new Thread(new StreamTransferer(name, bufferSize, in, out));
		result.start();
		return result;
	}
	
	private static final class StreamTransferer implements Runnable
	{
		private final String name;
		private final int bufferSize;
		private final InputStream in;
		private final OutputStream out;
		
		StreamTransferer(final String name, final int bufferSize, final InputStream in, final OutputStream out)
		{
			this.name = name;
			this.bufferSize = bufferSize;
			this.in = in;
			this.out = out;
		}
		
		public void run()
		{
			InputStream in = this.in;
			OutputStream out = this.out;
			final byte[] buf = new byte[bufferSize];
			int hasReadSum = 0;
			int hasReadCount = 0;
			try
			{
				while(true)
				{
					final int hasRead = in.read(buf);
					if(hasRead==-1)
						break;

					//System.out.println("-----" + name + "----------r"+hasRead);
					hasReadSum += hasRead;
					hasReadCount++;
					
					out.write(buf, 0, hasRead);
				}
				in.close(); in = null;
				out.close(); out = null;
				//System.out.println("-----" + name + '(' + buf.length + ") copied " + hasReadSum + " bytes " + " in " + hasReadCount + " cycles.");
			}
			catch(IOException e)
			{
				throw new RuntimeException();
			}
			finally
			{
				if(in!=null)
				{
					try
					{
						in.close();
					}
					catch(IOException e)
					{
						// already throwing exception
					}
				}
				if(out!=null)
				{
					try
					{
						out.close();
					}
					catch(IOException e)
					{
						// already throwing exception
					}
				}
			}
		}
	}
}

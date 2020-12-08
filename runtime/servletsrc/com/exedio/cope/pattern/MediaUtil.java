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

import static com.exedio.cope.util.Check.requireNonNegative;

import com.exedio.cope.DataField;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

public final class MediaUtil
{
	public static void send(
			final String contentType,
			final Charset charset,
			final String body,
			final HttpServletResponse response)
		throws IOException
	{
		response.setCharacterEncoding(charset.name());
		send(contentType, body.getBytes(charset), response);
	}

	public static void send(
			final String contentType,
			final String charsetName,
			final String body,
			final HttpServletResponse response)
		throws IOException
	{
		response.setCharacterEncoding(charsetName);
		send(contentType, body.getBytes(charsetName), response);
	}

	public static void send(
			final String contentType,
			final byte[] body,
			final HttpServletResponse response)
		throws IOException
	{
		response.setContentType(contentType);
		response.setContentLength(body.length);
		if(body.length==0)
			return;

		try(ServletOutputStream out = response.getOutputStream())
		{
			out.write(body);
		}
	}

	public static void send(
			final String contentType,
			final ByteArrayOutputStream body,
			final HttpServletResponse response)
		throws IOException
	{
		response.setContentType(contentType);
		final int contentLength = body.size();
		response.setContentLength(contentLength);
		if(contentLength==0)
			return;

		try(ServletOutputStream out = response.getOutputStream())
		{
			body.writeTo(out);
		}
	}

	public static void send(
			final String contentType,
			final File body,
			final HttpServletResponse response)
		throws IOException
	{
		response.setContentType(contentType);

		final long contentLength = body.length();
		if(contentLength<0)
			throw new RuntimeException(String.valueOf(contentLength));
		setContentLengthLong(response, contentLength);
		if(contentLength==0)
			return;

		final byte[] b = new byte[DataField.min(8*1024, contentLength)];
		try(
			FileInputStream in = new FileInputStream(body);
			ServletOutputStream out = response.getOutputStream())
		{
			for(int len = in.read(b); len>=0; len = in.read(b))
				out.write(b, 0, len);
		}
	}

	public static void send(
			final String contentType,
			final Path body,
			final HttpServletResponse response)
		throws IOException
	{
		response.setContentType(contentType);

		final long contentLength = Files.size(body);
		if(contentLength<0)
			throw new RuntimeException(String.valueOf(contentLength));
		setContentLengthLong(response, contentLength);
		if(contentLength==0)
			return;

		try(ServletOutputStream out = response.getOutputStream())
		{
			Files.copy(body, out);
		}
	}

	/**
	 * TODO
	 * To be replaced by {@code HttpServletResponse#setContentLengthLong(long)}
	 * in javax.servlet#javax.servlet-api;3.1.0 (Apache Tomcat 8.0.x).
	 */
	public static void setContentLengthLong(
			final HttpServletResponse response,
			final long contentLength)
	{
		requireNonNegative(contentLength, "contentLength");

		response.setHeader("Content-Length", Long.toString(contentLength));
	}


	private MediaUtil()
	{
		// prevent instantiation
	}
}

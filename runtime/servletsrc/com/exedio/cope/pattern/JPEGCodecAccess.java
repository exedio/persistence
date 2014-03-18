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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class JPEGCodecAccess
{
	static boolean available()
	{
		return create!=null && decode!=null;
	}

	static BufferedImage convert(final byte[] srcBytes)
	{
		try
		{
			return (BufferedImage)decode.invoke(create.invoke(null, new ByteArrayInputStream(srcBytes)));
		}
		catch(final IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch(final InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static final Method create = method(
			"com.sun.image.codec.jpeg.JPEGCodec",
			"createJPEGDecoder",
			InputStream.class);

	private static final Method decode = method(
			"com.sun.image.codec.jpeg.JPEGImageDecoder",
			"decodeAsBufferedImage");

	private static Method method(
			final String className,
			final String methodName,
			final Class<?>... parameterTypes)
	{
		try
		{
			final Method result = Class.forName(className).getMethod(methodName, parameterTypes);
			System.out.println("JPEGCodecAccess available: " + className + '#' + methodName);
			return result;
		}
		catch(final ClassNotFoundException e)
		{
			System.out.println("JPEGCodecAccess not available: " + className);
			return null;
		}
		catch(final NoSuchMethodException e)
		{
			System.out.println("JPEGCodecAccess not available: " + className + '#' + methodName);
			return null;
		}
	}

	private JPEGCodecAccess()
	{
		// prevent instantiation
	}
}

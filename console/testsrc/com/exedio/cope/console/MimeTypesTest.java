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

package com.exedio.cope.console;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import junit.framework.TestCase;

public class MimeTypesTest extends TestCase
{
	public void testIt() throws UnsupportedEncodingException
	{
		assertIt("image/png, text/plain", "image/png", "text/plain");
		assertIt("image/png, text/plain", "text/plain", "image/png");
		assertIt("image/jpeg, ~/png, text/plain", "image/jpeg", "text/plain", "image/png");
		assertIt("image/[p]jpeg, ~/png, text/plain", "image/jpeg", "text/plain", "image/png", "image/pjpeg");
		assertIt("image/[p]jpeg, ~/[x-]png, text/plain", "image/x-png", "image/jpeg", "text/plain", "image/png", "image/pjpeg");
	}
	
	private void assertIt(final String expected, final String... actual) throws UnsupportedEncodingException
	{
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final PrintStream out = new PrintStream(baos, false, "UTF8");
		MediaStatsCop.printContentTypes(out, Arrays.asList(actual));
		assertEquals(expected, new String(baos.toByteArray(), "UTF8"));
	}
}

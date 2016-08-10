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

package com.exedio.cope.instrument;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.tools.JavaFileObject;

class DummyJavaFileObject implements JavaFileObject
{
	private final String name;
	private int dummyByteCount=-1;

	DummyJavaFileObject(final String name)
	{
		this.name=name;
	}

	DummyJavaFileObject withDummyBytes(final int byteCount)
	{
		dummyByteCount=byteCount;
		return this;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public Kind getKind()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean isNameCompatible(final String simpleName, final Kind kind)
	{
		throw new RuntimeException();
	}

	@Override
	public NestingKind getNestingKind()
	{
		throw new RuntimeException();
	}

	@Override
	public Modifier getAccessLevel()
	{
		throw new RuntimeException();
	}

	@Override
	public URI toUri()
	{
		throw new RuntimeException();
	}

	@Override
	public InputStream openInputStream()
	{
		if (dummyByteCount==-1)
		{
			throw new RuntimeException();
		}
		final byte[] bytes=new byte[dummyByteCount];
		for (int i=0; i<dummyByteCount; i++)
		{
			bytes[i]=(byte)('0'+i%10);
		}
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public OutputStream openOutputStream()
	{
		throw new RuntimeException();
	}

	@Override
	public Reader openReader(final boolean ignoreEncodingErrors)
	{
		throw new RuntimeException();
	}

	@Override
	public CharSequence getCharContent(final boolean ignoreEncodingErrors)
	{
		throw new RuntimeException();
	}

	@Override
	public Writer openWriter()
	{
		throw new RuntimeException();
	}

	@Override
	public long getLastModified()
	{
		throw new RuntimeException();
	}

	@Override
	public boolean delete()
	{
		throw new RuntimeException();
	}
}

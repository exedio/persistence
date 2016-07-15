package com.exedio.cope.instrument;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
	public InputStream openInputStream() throws IOException
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
	public OutputStream openOutputStream() throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public Reader openReader(final boolean ignoreEncodingErrors) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws IOException
	{
		throw new RuntimeException();
	}

	@Override
	public Writer openWriter() throws IOException
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

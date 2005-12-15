/*
 * Copyright (C) 2004-2005  exedio GmbH (www.exedio.com)
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

package com.exedio.cope;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public final class DataAttribute extends Attribute
{

	public DataAttribute(final Option option)
	{
		super(option);

		if(option.unique)
			throw new RuntimeException("DataAttribute cannot be unique");
		if(option.mandatory)
			throw new RuntimeException("DataAttribute cannot be mandatory");
		if(option.readOnly)
			throw new RuntimeException("DataAttribute cannot be read-only");
	}
	
	String filePath = null;
	
	void initialize(final Type type, final String name)
	{
		super.initialize(type, name);

		filePath = type.id + '/' + name + '/';
	}

	// second initialization phase ---------------------------------------------------

	Column createColumn(final Table table, final String name, final boolean notNull)
	{
		// make sure, data configuration properties are set
		getType().getModel().getProperties().getDatadirPath();
		// TODO on some fine day, the BLOB column will be created here
		return null;
	}
	
	private final File getPrivateStorageFile(final Item item)
	{
		final File directory = item.type.getModel().getProperties().getDatadirPath();
		return new File(directory, filePath + item.type.getPkSource().pk2id(item.pk));
	}
	
	// public methods ---------------------------------------------------------------
	
	/**
	 * Returns, whether there is no data for this attribute.
	 */
	public final boolean isNull(final Item item)
	{
		final File file = getPrivateStorageFile(item);
		return !file.exists();
	}

	/**
	 * Returns a stream for fetching the data of this persistent data attribute.
	 * <b>You are responsible for closing the stream, when you are finished!</b>
	 * Returns null, if there is no data for this attribute.
	 */
	public final InputStream get(final Item item)
	{
		final File file = getPrivateStorageFile(item);
		try
		{
			return new FileInputStream(file);
		}
		catch(FileNotFoundException e)
		{
			return null;
		}
	}

	/**
	 * Returns the length of the data of this persistent data attribute.
	 * Returns -1, if there is no data for this attribute.
	 */
	public final long getLength(final Item item)
	{
		final File file = getPrivateStorageFile(item);

		return file.exists() ? file.length() : -1l;
	}

	/**
	 * Provides data for this persistent data attribute.
	 * Closes <data>data</data> after reading the contents of the stream.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and attribute is {@link Attribute#isMandatory() mandatory}.
	 * @throws IOException if reading data throws an IOException.
	 */
	public final void set(final Item item, final InputStream data)
	throws MandatoryViolationException, IOException
	{
		OutputStream out = null;
		try
		{
			final File file = getPrivateStorageFile(item);

			if(data!=null)
			{
				out = new FileOutputStream(file);
				final byte[] b = new byte[20*1024];
				for(int len = data.read(b); len>=0; len = data.read(b))
					out.write(b, 0, len);
				out.close();
				data.close();
			}
			else
			{
				if(file.exists())
				{
					if(!file.delete())
						throw new RuntimeException("deleting "+file+" failed.");
				}
			}
		}
		finally
		{
			if(data!=null)
				data.close();
			if(out!=null)
				out.close();
		}
	}
	

	// access by file -----------------------------------
	
	private static final void copy(final File source, final File target) throws IOException
	{
		final long length = source.length();
		if(length>0)
		{
			InputStream sourceS = null;
			OutputStream targetS = null;
			try
			{
				sourceS = new FileInputStream(source);
				targetS = new FileOutputStream(target);
				final byte[] b = new byte[Math.min(1024*1024, (int)Math.min((long)Integer.MAX_VALUE, length))];
				//System.out.println("-------------- "+length+" ----- "+b.length);
				for(int len = sourceS.read(b); len>=0; len = sourceS.read(b))
					targetS.write(b, 0, len);
			}
			finally
			{
				if(sourceS!=null)
					sourceS.close();
				if(targetS!=null)
					targetS.close();
			}
		}
		else if(length==0)
		{
			if(target.exists())
			{
				final long targetLength = target.length();
				if(targetLength==0)
					; // do nothing
				else if(targetLength<0)
					throw new RuntimeException(String.valueOf(targetLength));
				else
				{
					target.delete();
					target.createNewFile();
				}
			}
			else
				target.createNewFile();
		}
		else
			throw new RuntimeException(String.valueOf(length));
	}
	
	/**
	 * Reads data for this persistent data attribute
	 * and writes it into the given file.
	 * Does nothing, if there is no data for this attribute.
	 * @throws NullPointerException
	 *         if data is null.
	 * @throws IOException if writing data throws an IOException.
	 */
	public final void get(final Item item, final File data) throws IOException
	{
		if(data==null)
			throw new NullPointerException();
		
		final File file = getPrivateStorageFile(item);
		if(file.exists())
			copy(file, data);
	}
	
	/**
	 * Provides data for this persistent data attribute.
	 * @param data give null to remove data.
	 * @throws MandatoryViolationException
	 *         if data is null and attribute is {@link Attribute#isMandatory() mandatory}.
	 * @throws IOException if reading data throws an IOException.
	 */
	public final void set(final Item item, final File data)
	throws MandatoryViolationException, IOException
	{
		final File file = getPrivateStorageFile(item);

		if(data!=null)
			copy(data, file);
		else
		{
			if(file.exists())
			{
				if(!file.delete())
					throw new RuntimeException("deleting "+file+" failed.");
			}
		}
	}
	
}

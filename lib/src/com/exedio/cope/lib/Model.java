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
package com.exedio.cope.lib;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public final class Model
{
	private final Type[] types;
	private final List typeList;
	private final HashMap typesByID = new HashMap();
	
	public Model(final Type[] types)
	{
		this.types = types;
		this.typeList = Collections.unmodifiableList(Arrays.asList(types));

		for(int i = 0; i<types.length; i++)
		{
			final Type type = types[i];
			type.initialize(this);
		}
	}
	
	private Properties properties;
	private Database database;

	/**
	 * Initially sets the properties for this model.
	 * Can be called multiple times, but only the first time
	 * takes effect.
	 * Any subsequent calls must give properties equal to properties given
	 * on the first call, otherwise a RuntimeException is thrown.
	 * <p>
	 * Usually you may want to use this method, if you want to initialize model
	 * from different servlets with equal properties in an undefined order.
	 * 
	 * @throws RuntimeException if a subsequent call provides properties different
	 * 									to the first call.
	 */
	public final void setPropertiesInitially(final Properties properties)
	{
		if(properties==null)
			throw new NullPointerException();

		if(this.properties!=null)
		{
			this.properties.ensureEquality(properties);
		}
		else
		{
			if(this.database!=null)
				throw new RuntimeException();
	
			this.properties = properties;
			this.database = properties.createDatabase();
	
			for(int i = 0; i<types.length; i++)
			{
				final Type type = types[i];
				type.materialize(database);
				typesByID.put(type.getID(), type);
			}
		}
	}

	public final List getTypes()
	{
		return typeList;
	}
	
	public final Type findTypeByID(final String id)
	{
		if(this.properties==null)
			throw new RuntimeException();

		return (Type)typesByID.get(id);
	}
	
	public final Properties getProperties()
	{
		if(properties==null)
			throw new RuntimeException();

		return properties;
	}
	
	final Database getDatabase()
	{
		if(database==null)
			throw new RuntimeException();

		return database;
	}
	
	public void createDatabase()
	{
		for(int i = 0; i<types.length; i++)
			createMediaDirectories(types[i]);

		database.createDatabase();
	}

	private void createMediaDirectories(final Type type)
	{
		File typeDirectory = null;

		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			if(attribute instanceof MediaAttribute)
			{
				if(typeDirectory==null)
				{
					final File directory = properties.getMediaDirectory();
					typeDirectory = new File(directory, type.getID());
					typeDirectory.mkdir();
				}
				final File attributeDirectory = new File(typeDirectory, attribute.getName());
				attributeDirectory.mkdir();
			}
		}
	}

	/**
	 * Checks the database,
	 * whether the database tables representing the types do exist.
	 * Issues a single database statement,
	 * that touches all tables and columns,
	 * that would have been created by
	 * {@link #createDatabase()}.
	 * @throws RuntimeException
	 * 	if something is wrong with the database.
	 * 	TODO: use a more specific exception.
	 */
	public void checkDatabase()
	{
		// TODO: check for media attribute directories
		database.checkDatabase();
	}

	public void checkEmptyDatabase()
	{
		database.checkEmptyDatabase();
	}

	public void dropDatabase()
	{
		// TODO: rework this method
		final List types = typeList;
		for(ListIterator i = types.listIterator(types.size()); i.hasPrevious(); )
			((Type)i.previous()).onDropTable();

		database.dropDatabase();
		// TODO: remove media directories
	}

	public void tearDownDatabase()
	{
		database.tearDownDatabase();
		// TODO: remove media directories
	}

	public Report reportDatabase()
	{
		// TODO: report media directories
		return database.reportDatabase();
	}

	/**
	 * Returns the item with the given ID.
	 * Always returns {@link Item#activeItem() active} objects.
	 * @see Item#getCopeID()
	 * @throws NoSuchIDException if there is no item with the given id.
	 */
	public final Item findByID(final String id)
			throws NoSuchIDException
	{
		final int pos = id.lastIndexOf('.');
		if(pos<=0)
			throw new NoSuchIDException(id, "no dot in id");

		final String typeID = id.substring(0, pos);
		final Type type = findTypeByID(typeID);
		if(type==null)
			throw new NoSuchIDException(id, "no such type "+typeID);
		
		final String idString = id.substring(pos+1);

		final long idNumber;
		try
		{
			idNumber = Long.parseLong(idString);
		}
		catch(NumberFormatException e)
		{
			throw new NoSuchIDException(id, e, idString);
		}

		final int pk = type.getPrimaryKeyIterator().id2pk(idNumber);
		
		if(!database.check(type, pk))
			throw new NoSuchIDException(id, "item <"+idNumber+"> does not exist");

		final Item result = type.getItem(pk);
		return result;
	}
	
}

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
			type.setModel (this);
			typesByID.put(type.id, type);
		}
	}

	public final List getTypes()
	{
		return typeList;
	}
	
	public final Type findTypeByID(final String id)
	{
		return (Type)typesByID.get(id);
	}
	
	public void createDatabase()
	{
		for(int i = 0; i<types.length; i++)
			createMediaDirectories(types[i]);

		Database.theInstance.createDatabase();
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
					final File directory = Properties.getInstance().getMediaDirectory();
					typeDirectory = new File(directory, type.id);
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
	 * @throws SystemException
	 * 	if something is wrong with the database.
	 * 	TODO: use a more specific exception.
	 */
	public void checkDatabase()
	{
		// TODO: check for media atribute directories
		Database.theInstance.checkDatabase();
	}

	public void dropDatabase()
	{
		// TODO: rework this method
		final List types = typeList;
		for(ListIterator i = types.listIterator(types.size()); i.hasPrevious(); )
			((Type)i.previous()).onDropTable();

		Database.theInstance.dropDatabase();
	}

	public void tearDownDatabase()
	{
		Database.theInstance.tearDownDatabase();
	}

	/**
	 * Returns the item with the given ID.
	 * Always returns {@link Item#activeItem() active} objects.
	 * @see Item#getID()
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

		final int pk = Search.id2pk(idNumber);
		
		if(!Database.theInstance.check(type, pk))
			throw new NoSuchIDException(id, "item <"+idNumber+"> does not exist");

		final Item result = type.getItem(pk);
		return result;
	}
	
}

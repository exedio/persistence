package com.exedio.cope.lib;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

	public final void setProperties(final Properties properties)
	{
		if(properties==null)
			throw new NullPointerException();

		if(this.properties!=null)
			throw new RuntimeException();
		if(this.database!=null)
			throw new RuntimeException();

		this.properties = properties;
		this.database = createDatabaseInstance();

		for(int i = 0; i<types.length; i++)
		{
			final Type type = types[i];
			type.materialize(database);
			typesByID.put(type.getID(), type);
		}
	}

	private final Database createDatabaseInstance()
	{
		final String databaseName = properties.getDatabase();

		final Class databaseClass;
		try
		{
			databaseClass = Class.forName(databaseName);
		}
		catch(ClassNotFoundException e)
		{
			final String m = "ERROR: class "+databaseName+" from cope.properties not found.";
			System.err.println(m);
			throw new RuntimeException(m);
		}
		
		if(!Database.class.isAssignableFrom(databaseClass))
		{
			final String m = "ERROR: class "+databaseName+" from cope.properties not a subclass of "+Database.class.getName()+".";
			System.err.println(m);
			throw new RuntimeException(m);
		}

		final Constructor constructor;
		try
		{
			constructor = databaseClass.getDeclaredConstructor(new Class[]{Properties.class});
		}
		catch(NoSuchMethodException e)
		{
			final String m = "ERROR: class "+databaseName+" from cope.properties has no constructor with a single Properties argument.";
			System.err.println(m);
			throw new RuntimeException(m);
		}

		try
		{
			return (Database)constructor.newInstance(new Object[]{properties});
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw new SystemException(e);
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw new SystemException(e);
		}
		catch(InvocationTargetException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
			throw new SystemException(e);
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
	
	public final boolean hasProperties()
	{
		return properties!=null;
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
	 * @throws SystemException
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
	}

	public void tearDownDatabase()
	{
		database.tearDownDatabase();
	}

	public Report reportDatabase()
	{
		return database.reportDatabase();
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
		
		if(!database.check(type, pk))
			throw new NoSuchIDException(id, "item <"+idNumber+"> does not exist");

		final Item result = type.getItem(pk);
		return result;
	}
	
}

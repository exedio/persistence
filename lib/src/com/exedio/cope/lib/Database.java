
package com.exedio.cope.lib;

import com.exedio.cope.lib.Attribute;
import com.exedio.cope.lib.AttributeMapping;
import com.exedio.cope.lib.BooleanAttribute;
import com.exedio.cope.lib.EnumerationAttribute;
import com.exedio.cope.lib.EnumerationValue;
import com.exedio.cope.lib.IntegerAttribute;
import com.exedio.cope.lib.Item;
import com.exedio.cope.lib.ItemAttribute;
import com.exedio.cope.lib.MediaAttribute;
import com.exedio.cope.lib.StringAttribute;
import com.exedio.cope.lib.SystemException;
import com.exedio.cope.lib.Type;
import com.exedio.cope.lib.database.OracleDatabase;
import com.exedio.cope.lib.search.Condition;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public abstract class Database
{
	public static final Database theInstance = new OracleDatabase();
	
	protected Database()
	{
	}
	
	protected abstract char getNameDelimiterStart();
	protected abstract char getNameDelimiterEnd();
	
	private String getSyntheticPrimaryKeyQualifier()
	{
		return "\"PK\"";
	}
	
	private String getSyntheticPrimaryKeyType()
	{
		return "number(10,0)";
	}
	
	public void createTables()
	{
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			createTable((Type)i.next());
	}

	public void dropTables()
	{
		for(Iterator i = Type.getTypes().iterator(); i.hasNext(); )
			dropTable((Type)i.next());
	}

	private void createTable(final Type type)
	{
		executeSQL(getCreateTableStatement(type));
	}
	
	private void dropTable(final Type type)
	{
		executeSQL(getDropTableStatement(type));
	}
	
	public static final class Statement
	{
		final StringBuffer text = new StringBuffer();
		
		private Statement()
		{
		}

		public Statement append(final String text)
		{
			this.text.append(text);
			return this;
		}
		
		public Statement append(final char text)
		{
			this.text.append(text);
			return this;
		}
		
		public Statement append(Attribute attribute)
		{
			ArrayList mappingEnds = null;
			while(attribute.mapping!=null)
			{
				final AttributeMapping mapping = attribute.mapping;
				this.text.append(mapping.sqlMappingStart);
				if(mappingEnds==null)
					mappingEnds = new ArrayList();
				mappingEnds.add(mapping.sqlMappingEnd);
				attribute = mapping.sourceAttribute;
			}
			
			this.text.append(attribute.getPersistentQualifier());
			
			if(mappingEnds!=null)
			{
				for(ListIterator i = mappingEnds.listIterator(mappingEnds.size()); i.hasPrevious(); )
					this.text.append((String)i.previous());
			}
			
			return this;
		}
		
		public Statement appendValue(final Attribute attribute, final Object value)
		{
			this.text.append(attribute.cacheToDatabase(attribute.surfaceToCache(value)));
			return this;
		}
		
		public String getText()
		{
			return text.toString();
		}

		public String toString()
		{
			return text.toString();
		}
	}

	Collection search(final Type type, final Condition condition)
	{
		final Statement bf = new Statement();
		bf.append("select ").
			append(getSyntheticPrimaryKeyQualifier()).
			append(" from ").
			append(type.getPersistentQualifier()).
			append(" where ");
		condition.appendStatement(bf);
		
		System.out.println("searching "+bf.toString());
		return executeSQLQuery(bf.getText());
	}

	void load(final Type type, final int pk, final HashMap itemCache)
	{
		final List attributes = type.getAttributes();

		// TODO: use prepared statements and reuse the statement.
		final StringBuffer bf = new StringBuffer();
		bf.append("select ");

		boolean first = true;
		for(Iterator i = attributes.iterator(); i.hasNext(); )
		{
			if(first)
				first = false;
			else
				bf.append(',');
			final Attribute attribute = (Attribute)i.next();
			bf.append(attribute.getPersistentQualifier());
		}
		bf.append(" from ").
			append(type.getPersistentQualifier()).
			append(" where ").
			append(getSyntheticPrimaryKeyQualifier()).
			append('=').
			append(pk);

		System.out.println("loading "+bf.toString());

		final ArrayList row = executeSQLLoad(bf.toString(), attributes.size());
		final Iterator ir = row.iterator();
		for(Iterator i = attributes.iterator(); i.hasNext(); )
		{
			final Attribute attribute = (Attribute)i.next();
			final Object cell = ir.next();
			itemCache.put(attribute, attribute.databaseToCache(cell));
		}
	}

	void store(final Type type, final int pk, final HashMap itemCache, final boolean present)
	{
		final List attributes = type.getAttributes();

		// TODO: use prepared statements and reuse the statement.
		final StringBuffer bf = new StringBuffer();
		if(present)
		{
			bf.append("update ").
				append(type.getPersistentQualifier()).
				append(" set ");

			boolean first = true;
			for(Iterator i = attributes.iterator(); i.hasNext(); )
			{
				if(first)
					first = false;
				else
					bf.append(',');

				final Attribute attribute = (Attribute)i.next();
				bf.append(attribute.getPersistentQualifier()).
					append('=');

				final Object value = itemCache.get(attribute);
				bf.append(attribute.cacheToDatabase(value));
			}
			bf.append(" where ").
				append(getSyntheticPrimaryKeyQualifier()).
				append('=').
				append(pk);
		}
		else
		{
			bf.append("insert into ").
				append(type.getPersistentQualifier()).
				append("(").
				append(getSyntheticPrimaryKeyQualifier());

			boolean first = true;
			for(Iterator i = attributes.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Attribute attribute = (Attribute)i.next();
				bf.append(attribute.getPersistentQualifier());
			}

			bf.append(")values(").
				append(pk);
			for(Iterator i = attributes.iterator(); i.hasNext(); )
			{
				bf.append(',');
				final Attribute attribute = (Attribute)i.next();
				final Object value = itemCache.get(attribute);
				bf.append(attribute.cacheToDatabase(value));
			}
			bf.append(')');
		}

		System.out.println("storing "+bf.toString());

		executeSQL(bf.toString());
	}

	private void executeSQL(final String sql)
	{
		executeSQLInternal(sql, 0, 0);
	}
	
	private ArrayList executeSQLQuery(final String sql)
	{
		return executeSQLInternal(sql, 1, 0);
	}
		
	private ArrayList executeSQLLoad(final String sql, final int columns)
	{
		return executeSQLInternal(sql, 2, columns);
	}
		
	private ArrayList executeSQLInternal(final String sql, final int type, final int columns)
	{
		final String driver = "oracle.jdbc.driver.OracleDriver";
		final String url = "jdbc:oracle:thin:@database3.exedio.com:1521:DB3";
		final String user = "wiebicke";
		final String password = "wiebicke1234";
		
		try
		{
			Class.forName(driver);
		}
		catch(ClassNotFoundException e)
		{
			throw new SystemException(e);
		}
		
		Connection connection = null;
		java.sql.Statement statement = null;
		ResultSet resultSet = null;
		try
		{
			connection = DriverManager.getConnection(url, user, password);
			statement = connection.createStatement();
			switch(type)
			{
				case 0:
				{
					statement.execute(sql);
					return null;
				}
				case 1:
				{
					resultSet = statement.executeQuery(sql);
					// TODO: use special list for integers
					final ArrayList result = new ArrayList();
					while(resultSet.next())
					{
						final Integer pk = new Integer(resultSet.getInt(1));
						//System.out.println("pk:"+pk);
						result.add(pk);
					}
					return result;
				}
				case 2:
				{
					resultSet = statement.executeQuery(sql);
					final ArrayList result = new ArrayList(columns);
					if(!resultSet.next())
						return null;
					for(int i = 1; i<=columns; i++)
						result.add(resultSet.getObject(i));
					return result;
				}
				default:
					throw new RuntimeException("type"+type);
			}
		}
		catch(SQLException e)
		{
			throw new SystemException(e, sql);
		}
		finally
		{
			if(resultSet!=null)
			{
				try
				{
					resultSet.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(statement!=null)
			{
				try
				{
					statement.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
			if(connection!=null)
			{
				try
				{
					connection.close();
				}
				catch(SQLException e)
				{
					// exception is already thrown
				}
			}
		}
	}
	
	String makePersistentQualifier(final Type type)
	{
		final String className = type.getJavaClass().getName();
		final int pos = className.lastIndexOf('.');
		return getNameDelimiterStart() + className.substring(pos+1) + getNameDelimiterEnd();
	}
	
	String makePersistentQualifier(final Attribute attribute)
	{
		return getNameDelimiterStart() + attribute.getName() + getNameDelimiterEnd();
	}
	
	private String getPersistentType(final Attribute attribute)
	{
		if(attribute instanceof StringAttribute)
			return "varchar2(4000)";
		else if(attribute instanceof BooleanAttribute)
			return "number(1,0)";
		else if(attribute instanceof IntegerAttribute)
			return "number(20,0)";
		else if(attribute instanceof EnumerationAttribute)
			return "number(10,0)";
		else if(attribute instanceof ItemAttribute)
			return getSyntheticPrimaryKeyType();
		else if(attribute instanceof MediaAttribute)
			// TODO, separate major/minor mime type and introduce width/height for images
			return "varchar(30)";
		else
			throw new RuntimeException(attribute.toString());
	}
	
	private String getCreateTableStatement(final Type type)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("create table ").
			append(type.getPersistentQualifier()).
			append('(');

		bf.append(getSyntheticPrimaryKeyQualifier()).
			append(' ').
			append(getSyntheticPrimaryKeyType()).
			append(" primary key");
		
		for(Iterator i = type.getAttributes().iterator(); i.hasNext(); )
		{
			bf.append(',');
			final Attribute attribute = (Attribute)i.next();
			// TODO: do not create columns for mapped attributes
			bf.append(attribute.getPersistentQualifier()).
				append(' ').
				append(getPersistentType(attribute));
		}
		bf.append(')');
		return bf.toString();
	}
	
	private String getDropTableStatement(final Type type)
	{
		final StringBuffer bf = new StringBuffer();
		bf.append("drop table ").
			append(type.getPersistentQualifier());
		return bf.toString();
	}
	
}

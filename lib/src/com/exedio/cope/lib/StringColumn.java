package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class StringColumn extends Column
{
	static final int JDBC_TYPE = Types.VARCHAR;
	
	final int minimumLength;
	final int maximumLength;
	final String[] allowedValues;

	StringColumn(
			final Table table, final String id, final boolean notNull,
			final int minimumLength, final int maximumLength)
	{
		super(table, id, false, notNull, JDBC_TYPE);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.allowedValues = null;
	}
	
	StringColumn(
			final Table table, final String id, final boolean notNull,
			final String[] allowedValues)
	{
		super(table, id, false, notNull, JDBC_TYPE);
		this.minimumLength = 0;
		this.maximumLength = Integer.MAX_VALUE;
		this.allowedValues = allowedValues;

		if(allowedValues.length<2)
			throw new RuntimeException(id);
		for(int i = 0; i<allowedValues.length; i++)
			allowedValues[i] = allowedValues[i].intern();
	}
	
	final String getDatabaseType()
	{
		return table.database.getStringType(maximumLength);
	}

	final String getCheckConstraintIfNotNull()
	{
		final StringBuffer bf = new StringBuffer();
		boolean first = true;

		if(minimumLength>0)
		{
			first = false;
			bf.append("(LENGTH(" + protectedID + ")>=" + minimumLength + ')');
		}

		if(maximumLength!=Integer.MAX_VALUE)
		{
			if(first)
				first = false;
			else
				bf.append(" AND ");
			
			bf.append("(LENGTH(" + protectedID + ")<=" + maximumLength + ')');
		}

		if(allowedValues!=null)
		{
			if(first)
				first = false;
			else
				bf.append(" AND ");

			bf.append(protectedID + " IN (");

			for(int j = 0; j<allowedValues.length; j++)
			{
				if(j>0)
					bf.append(',');

				bf.append('\'').
					append(allowedValues[j]).
					append('\'');
			}
			bf.append(')');
			return bf.toString();
		}
		
		return first ? null : bf.toString();
	}

	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final String loadedString = resultSet.getString(columnIndex);
		//System.out.println("StringColumn.load:"+loadedString);
		if(loadedString!=null)
			row.load(this, loadedString);
	}

	Object load(final ResultSet resultSet, final int columnIndex)
			throws SQLException
	{
		return resultSet.getString(columnIndex);
	}

	Object cacheToDatabase(final Object cache)
	{
		return cacheToDatabaseStatic(cache);
	}
	
	static Object cacheToDatabaseStatic(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
		{
			final String taintedCache = (String)cache;

			final String cleanCache;
			if(taintedCache.indexOf('\'')>=0)
			{
				// TODO: sql injection protection just swallows apostrophes,
				// should be escaped or wrapped into prepared statements
				final StringBuffer buf = new StringBuffer(taintedCache.length());
				int pos;
				int lastpos = 0;
				for(pos = taintedCache.indexOf('\''); pos>=0; pos = taintedCache.indexOf('\'', lastpos))
				{
					//System.out.println("---"+lastpos+"-"+pos+">"+taintedCache.substring(lastpos, pos)+"<");
					buf.append(taintedCache.substring(lastpos, pos));
					lastpos = pos+1;
				}
				//System.out.println("---"+lastpos+"-END>"+taintedCache.substring(lastpos)+"<");
				buf.append(taintedCache.substring(lastpos));
				cleanCache = buf.toString();
			}
			else
				cleanCache = taintedCache;

			return "'" + cleanCache + '\'';
		}
	}

}



package com.exedio.cope.lib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

final class StringColumn extends Column
{
	static final Integer JDBC_TYPE = new Integer(Types.VARCHAR);
	
	final int minimumLength;
	final int maximumLength;

	StringColumn(
			final Table table, final String id, final boolean notNull,
			final int minimumLength, final int maximumLength)
	{
		super(table, id, false, notNull, JDBC_TYPE);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
	}
	
	final String getDatabaseType()
	{
		return table.database.getStringType(maximumLength);
	}

	final String getMinimumLengthConstraintID()
	{
		if(minimumLength<=0)
			throw new RuntimeException(id);

		return table.database.trimName(table.id + "_" + id+ "_Min");
	}
	
	final String getMaximumLengthConstraintID()
	{
		if(maximumLength==Integer.MAX_VALUE)
			throw new RuntimeException(id);

		return table.database.trimName(table.id + "_" + id+ "_Max");
	}
	
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		final String loadedString = resultSet.getString(columnIndex);
		//System.out.println("StringColumn.load:"+loadedString);
		if(loadedString!=null)
			row.load(this, loadedString);
	}

	Object cacheToDatabase(final Object cache)
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



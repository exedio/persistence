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

		if(allowedValues!=null)
		{
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
		
		if(minimumLength>0)
		{
			if(maximumLength!=Integer.MAX_VALUE)
			{
				if(minimumLength==maximumLength)
					bf.append("LENGTH(" + protectedID + ")="  + minimumLength );
				else
					bf.append("(LENGTH(" + protectedID + ")>=" + minimumLength + ") AND (LENGTH(" + protectedID + ")<=" + maximumLength + ')');
			}
			else
				bf.append("LENGTH(" + protectedID + ")>=" + minimumLength);
		}
		else
		{
			if(maximumLength!=Integer.MAX_VALUE)
				bf.append("LENGTH(" + protectedID + ")<=" + maximumLength);
		}

		return bf.length()==0 ? null : bf.toString();
	}

	void load(final ResultSet resultSet, final int columnIndex, final PersistentState state)
			throws SQLException
	{
		final String loadedString = resultSet.getString(columnIndex);
		//System.out.println("StringColumn.load:"+loadedString);
		if(loadedString!=null)
			state.load(this, loadedString);
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



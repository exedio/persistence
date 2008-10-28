/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.CharacterSet;

final class StringColumn extends Column
{
	final int minimumLength;
	final int maximumLength;
	final CharacterSet characterSet;
	final String[] allowedValues;

	StringColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional,
			final int minimumLength,
			final int maximumLength,
			final CharacterSet characterSet)
	{
		super(table, field, id, false, optional);
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.characterSet  = characterSet;
		this.allowedValues = null;
		
		assert minimumLength<=maximumLength;
	}
	
	StringColumn(
			final Table table,
			final Field field,
			final String id,
			final boolean optional,
			final String[] allowedValues)
	{
		super(table, field, id, false, optional);
		this.minimumLength = 0;
		this.maximumLength = maxLength(allowedValues);
		this.characterSet  = null;
		this.allowedValues = allowedValues;

		if(allowedValues.length<2)
			throw new RuntimeException(id);
		final Database database = table.database;
		for(int i = 0; i<allowedValues.length; i++)
			allowedValues[i] = database.intern(allowedValues[i]);
		
		assert minimumLength<=maximumLength;
	}
	
	private static final int maxLength(final String[] strings)
	{
		int result = 0;
		
		for(int i = 0; i<strings.length; i++)
		{
			final int length = strings[i].length();
			if(result<length)
				result = length;
		}
		
		return result;
	}
	
	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getStringType(maximumLength);
	}

	@Override
	final String getCheckConstraintIgnoringMandatory()
	{
		final StringBuilder bf = new StringBuilder();

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
		else
		{
			final String length = table.database.dialect.stringLength;
			if(minimumLength>0)
			{
				if(minimumLength==maximumLength)
					bf.append(length + '(' + protectedID + ")=" + minimumLength );
				else
					bf.append(
							'(' + length + '(' + protectedID + ")>=" + minimumLength + ") AND " +
							'(' + length + "(" + protectedID + ")<=" + maximumLength + ')');
			}
			else
			{
				bf.append(length + '(' + protectedID + ")<=" + maximumLength);
			}
			if(characterSet!=null)
			{
				final String clause = table.database.dialect.getClause(protectedID, characterSet);
				if(clause!=null)
					bf.append(" AND (").
						append(clause).
						append(')');
			}
		}

		return bf.length()==0 ? null : bf.toString();
	}

	@Override
	void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		// TODO: should have numbers in cache instead of strings if allowedValues!=null
		row.put(this, resultSet.getString(columnIndex));
	}

	@Override
	String cacheToDatabase(final Object cache)
	{
		return cacheToDatabaseStatic(cache);
	}
	
	static String cacheToDatabaseStatic(final Object cache)
	{
		if(cache==null)
			return "NULL";
		else
		{
			final String taintedCache = (String)cache;

			final String cleanCache;
			if(taintedCache.indexOf('\'')>=0)
			{
				final StringBuilder buf = new StringBuilder(taintedCache.length());
				int pos;
				int lastpos = 0;
				for(pos = taintedCache.indexOf('\''); pos>=0; pos = taintedCache.indexOf('\'', lastpos))
				{
					//System.out.println("---"+lastpos+"-"+pos+">"+taintedCache.substring(lastpos, pos)+"<");
					buf.append(taintedCache.substring(lastpos, pos)).append("''");
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

	@Override
	Object cacheToDatabasePrepared(final Object cache)
	{
		assert cache==null || cache instanceof String;
		return cache;
	}
	
	@Override
	Object getCheckValue()
	{
		return "z";
	}
	
}

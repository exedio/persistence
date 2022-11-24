/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import com.exedio.cope.util.CharSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.SortedSet;

class StringColumn extends Column
{
	final int minimumLength;
	final int maximumLength;
	final CharSet charSet;
	final String regexpPattern;
	final SortedSet<String> allowedValues;
	private final MysqlExtendedVarchar mysqlExtendedVarchar;

	StringColumn(
			final Table table,
			final String id,
			final boolean optional,
			final int minimumLength,
			final int maximumLength,
			final CharSet charSet,
			final String regexpPattern,
			final MysqlExtendedVarchar mysqlExtendedVarchar)
	{
		super(table, id, false, Kind.nonPrimaryKey(optional));
		this.minimumLength = minimumLength;
		this.maximumLength = maximumLength;
		this.charSet = charSet;
		this.regexpPattern = regexpPattern;
		this.allowedValues = null;
		this.mysqlExtendedVarchar = mysqlExtendedVarchar;

		assert minimumLength<=maximumLength;
	}

	@SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
	StringColumn(
			final Table table,
			final String id,
			final boolean synthetic,
			final boolean optional,
			final int minLength,
			final SortedSet<String> allowedValues)
	{
		super(table, id, synthetic, Kind.nonPrimaryKey(optional));
		this.minimumLength = 0;
		this.maximumLength = Math.max(minLength, maxLength(allowedValues));
		this.charSet  = null;
		this.regexpPattern = null;
		this.allowedValues = allowedValues;
		this.mysqlExtendedVarchar = null;

		if(allowedValues.size()<2)
			throw new RuntimeException(id);

		assert minimumLength<=maximumLength;
	}

	private static int maxLength(final SortedSet<String> strings)
	{
		int result = 0;

		for(final String string : strings)
		{
			final int length = string.length();
			if(result<length)
				result = length;
		}

		return result;
	}

	@Override
	final String getDatabaseType()
	{
		return table.database.dialect.getStringType(maximumLength, mysqlExtendedVarchar);
	}

	@Override
	void makeSchema(final com.exedio.dsmf.Column dsmf)
	{
		if(allowedValues!=null)
		{
			final boolean parenthesis = table.database.dialect.inRequiresParenthesis();
			final String comma = table.database.dialect.getInComma();
			final StringBuilder bf = new StringBuilder();

			if(parenthesis)
				bf.append('(');
			bf.append(quotedID);
			if(parenthesis)
				bf.append(')');
			bf.append(" IN (");

			boolean first = true;
			for(final String allowedValue : allowedValues)
			{
				if(first)
					first = false;
				else
					bf.append(comma);

				if(parenthesis)
					bf.append('(');
				bf.append('\'').
					append(allowedValue).
					append('\'');
				if(parenthesis)
					bf.append(')');
			}
			bf.append(')');

			newCheck(dsmf, "EN", bf.toString());
		}
		else
		{
			final String length = table.database.dialect.getStringLength();
			final boolean exact = minimumLength==maximumLength;
			if(minimumLength>0)
				newCheck(dsmf, "MN", length + '(' + quotedID + (exact?")=":")>=") + minimumLength);
			if(!exact)
				newCheck(dsmf, "MX", length + '(' + quotedID +             ")<="  + maximumLength);

			if(charSet!=null)
			{
				final String clause = table.database.dialect.getClause(quotedID, charSet);
				if(clause!=null)
					newCheck(dsmf, "CS", clause);
			}
			if (regexpPattern!=null)
			{
				final String clause = table.database.dialect.getClause(quotedID, regexpPattern);
				newCheck(dsmf, "RE", clause);
			}
		}
	}

	@Override
	final void load(final ResultSet resultSet, final int columnIndex, final Row row)
			throws SQLException
	{
		// TODO: should have numbers in cache instead of strings if allowedValues!=null
		row.put(this, resultSet.getString(columnIndex));
	}

	@Override
	final String cacheToDatabase(final Object cache)
	{
		return cacheToDatabaseStatic(cache);
	}

	static final String cacheToDatabaseStatic(final Object cache)
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
					buf.append(taintedCache, lastpos, pos).append("''");
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
	final Object cacheToDatabasePrepared(final Object cache)
	{
		assert cache==null || cache instanceof String;
		return cache;
	}
}

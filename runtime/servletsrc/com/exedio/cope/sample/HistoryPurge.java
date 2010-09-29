/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.console;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ConnectProperties;
import com.exedio.cope.DateField;
import com.exedio.cope.Function;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.Query;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.dsmf.SQLRuntimeException;

final class HistoryPurge extends Item
{
	private static final StringField type = new StringField().toFinal();
	private static final DateField limit = new DateField().toFinal();
	private static final DateField finished = new DateField().toFinal().defaultToNow();
	private static final IntegerField rows  = new IntegerField().toFinal().min(0);
	private static final IntegerField elapsed  = new IntegerField().toFinal().min(0);

	static Query<HistoryPurge> newQuery()
	{
		final Query<HistoryPurge> q = TYPE.newQuery();
		q.setOrderBy(new Function[]{finished, TYPE.getThis()}, new boolean[]{false, false});
		return q;
	}

	static int purge(final int days)
	{
		if(days<=0)
			throw new IllegalArgumentException(String.valueOf(days));

		final GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.add(cal.DATE, -days);
		return purge(cal.getTime());
	}

	static int purge(final Date limit) // non-private for testing
	{
		int result = 0;
		for(final Type type : TYPE.getModel().getTypes())
			if(HistoryModel.TYPE!=type && // purge HistoryModel at the end
				TYPE!=type)
				result += purge(type, limit);

		result += purge(HistoryModel.TYPE, limit);

		return result;
	}

	private static int purge(final Type type, final Date limit)
	{
		final DateField field = (DateField)type.getFeature("date");
		if(field==null)
			throw new RuntimeException(type.getID());
		final Model model = type.getModel();
		final ConnectProperties p = model.getConnectProperties();
		final String bf =
			"delete from " + SchemaInfo.quoteName(model, SchemaInfo.getTableName (type )) +
			" where "      + SchemaInfo.quoteName(model, SchemaInfo.getColumnName(field)) + "<?";
		Connection con = null;
		final int rows;
		final long start = System.nanoTime();
		try
		{
			con = DriverManager.getConnection(p.getDatabaseUrl(), p.getDatabaseUser(), p.getDatabasePassword());
			PreparedStatement stat = null;
			try
			{
				stat = con.prepareStatement(bf);

				if(SchemaInfo.supportsNativeDate(model))
					stat.setTimestamp(1, new Timestamp(limit.getTime())); else
					stat.setLong     (1,               limit.getTime() );

				rows = stat.executeUpdate();

				if(stat!=null)
				{
					stat.close();
					stat = null;
				}
				if(con!=null)
				{
					con.close();
					con = null;
				}
			}
			finally
			{
				if(stat!=null)
				{
					stat.close();
					stat = null;
				}
			}
		}
		catch(final SQLException e)
		{
			throw new SQLRuntimeException(e, bf);
		}
		finally
		{
			if(con!=null)
			{
				try
				{
					con.close();
					con = null;
				}
				catch(final SQLException e)
				{
					// exception is already thrown
				}
			}
		}
		final long end = System.nanoTime();

		try
		{
			model.startTransaction("history analyze dates");
			new HistoryPurge(type, limit, rows, (int)((end-start)/1000000));
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}

		return rows;
	}


	HistoryPurge(
			final Type type,
			final Date limit,
			final int rows,
			final int elapsed)
	{
		super(
			HistoryPurge.type   .map(type.getID()),
			HistoryPurge.limit  .map(limit),
			HistoryPurge.rows   .map(rows),
			HistoryPurge.elapsed.map(elapsed));
	}

	@SuppressWarnings("unused")
	private HistoryPurge(final ActivationParameters ap)
	{
		super(ap);
	}

	String getType()
	{
		return type.get(this);
	}

	Date getLimit()
	{
		return limit.get(this);
	}

	Date getFinished()
	{
		return finished.get(this);
	}

	int getRows()
	{
		return rows.get(this);
	}

	int getElapsed()
	{
		return elapsed.get(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<HistoryPurge> TYPE = TypesBound.newType(HistoryPurge.class);
}

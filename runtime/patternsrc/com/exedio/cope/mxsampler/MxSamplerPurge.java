/*
 * Copyright (C) 2004-2012  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.mxsampler;

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.SchemaInfo.newConnection;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.SchemaInfo.supportsNativeDate;
import static com.exedio.cope.misc.TimeUtil.toMillies;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

final class MxSamplerPurge extends Item
{
	private static final StringField type = new StringField().toFinal();
	private static final DateField limit = new DateField().toFinal();
	private static final DateField finished = new DateField().toFinal().defaultToNow();
	private static final IntegerField rows  = new IntegerField().toFinal().min(0);
	private static final LongField elapsed  = new LongField().toFinal().min(0);

	@SuppressFBWarnings("SQL_PREPARED_STATEMENT_GENERATED_FROM_NONCONSTANT_STRING")
	static void purge(
			final Type<?> type,
			final Date limit,
			final JobContext ctx,
			final String samplerString)
	throws SQLException
	{
		ctx.stopIfRequested();
		final DateField field = MxSamplerGlobal.replaceByCopy(MxSamplerGlobal.date, type);
		final Model model = type.getModel();
		final String bf =
			"delete from " + quoteName(model, getTableName (type )) +
			" where "      + quoteName(model, getColumnName(field)) + "<?";
		final int rows;
		final long start = System.nanoTime();
		try(Connection con = newConnection(model))
		{
			try(PreparedStatement stat = con.prepareStatement(bf))
			{
				if(supportsNativeDate(model))
					stat.setTimestamp(1, new Timestamp(limit.getTime())); else
					stat.setLong     (1,               limit.getTime() );

				rows = stat.executeUpdate();
			}
		}
		final long end = System.nanoTime();

		try
		{
			model.startTransaction(samplerString + " purge register");
			new MxSamplerPurge(type, limit, rows, toMillies(end, start));
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}

		ctx.incrementProgress(rows);
	}


	MxSamplerPurge(
			final Type<?> type,
			final Date limit,
			final int rows,
			final long elapsed)
	{
		super(
			MxSamplerPurge.type   .map(type.getID()),
			MxSamplerPurge.limit  .map(limit),
			MxSamplerPurge.rows   .map(rows),
			MxSamplerPurge.elapsed.map(elapsed));
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
		return rows.getMandatory(this);
	}

	long getElapsed()
	{
		return elapsed.getMandatory(this);
	}

	private MxSamplerPurge(final ActivationParameters ap) { super(ap); }

	private static final long serialVersionUID = 1l;

	static final Type<MxSamplerPurge> TYPE = TypesBound.newType(MxSamplerPurge.class);
}

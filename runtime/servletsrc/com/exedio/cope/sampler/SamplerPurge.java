/*
 * Copyright (C) 2004-2011  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.sampler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import com.exedio.cope.ActivationParameters;
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
import com.exedio.cope.util.JobContext;

final class SamplerPurge extends Item
{
	private static final StringField type = new StringField().toFinal();
	private static final DateField limit = new DateField().toFinal();
	private static final DateField finished = new DateField().toFinal().defaultToNow();
	private static final IntegerField rows  = new IntegerField().toFinal().min(0);
	private static final IntegerField elapsed  = new IntegerField().toFinal().min(0);

	static Query<SamplerPurge> newQuery()
	{
		final Query<SamplerPurge> q = TYPE.newQuery();
		q.setOrderBy(new Function[]{finished, TYPE.getThis()}, new boolean[]{false, false});
		return q;
	}

	static void purge(
			final Type type,
			final Date limit,
			final JobContext ctx,
			final String samplerString)
	throws SQLException
	{
		if(ctx.requestedToStop())
			return;

		final DateField field = (DateField)type.getFeature("date");
		if(field==null)
			throw new RuntimeException(type.getID());
		final Model model = type.getModel();
		final String bf =
			"delete from " + SchemaInfo.quoteName(model, SchemaInfo.getTableName (type )) +
			" where "      + SchemaInfo.quoteName(model, SchemaInfo.getColumnName(field)) + "<?";
		final int rows;
		final long start = System.nanoTime();
		final Connection con = SchemaInfo.newConnection(model);
		try
		{
			final PreparedStatement stat = con.prepareStatement(bf);
			try
			{
				if(SchemaInfo.supportsNativeDate(model))
					stat.setTimestamp(1, new Timestamp(limit.getTime())); else
					stat.setLong     (1,               limit.getTime() );

				rows = stat.executeUpdate();
			}
			finally
			{
				stat.close();
			}
		}
		finally
		{
			con.close();
		}
		final long end = System.nanoTime();

		try
		{
			model.startTransaction(samplerString + " purge register");
			new SamplerPurge(type, limit, rows, (int)((end-start)/1000000));
			model.commit();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}

		ctx.incrementProgress(rows);
	}


	SamplerPurge(
			final Type type,
			final Date limit,
			final int rows,
			final int elapsed)
	{
		super(
			SamplerPurge.type   .map(type.getID()),
			SamplerPurge.limit  .map(limit),
			SamplerPurge.rows   .map(rows),
			SamplerPurge.elapsed.map(elapsed));
	}

	private SamplerPurge(final ActivationParameters ap)
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
		return rows.getMandatory(this);
	}

	int getElapsed()
	{
		return elapsed.getMandatory(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<SamplerPurge> TYPE = TypesBound.newType(SamplerPurge.class);
}

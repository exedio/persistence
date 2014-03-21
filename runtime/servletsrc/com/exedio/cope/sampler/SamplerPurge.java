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

package com.exedio.cope.sampler;

import static com.exedio.cope.SchemaInfo.getPrimaryKeyColumnName;
import static com.exedio.cope.SchemaInfo.quoteName;
import static com.exedio.cope.SchemaInfo.search;
import static com.exedio.cope.misc.TimeUtil.toMillies;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TransactionTry;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.util.JobContext;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

final class SamplerPurge extends Item
{
	private static final StringField type = new StringField().toFinal();
	private static final DateField limit = new DateField().toFinal();
	private static final DateField finished = new DateField().toFinal().defaultToNow();
	private static final IntegerField rows  = new IntegerField().toFinal().min(0);
	private static final LongField elapsed  = new LongField().toFinal().min(0);

	@SuppressFBWarnings("SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE")
	static void purge(
			final Connection con,
			final Type<?> type,
			final Date limit,
			final JobContext ctx,
			final String samplerString)
	throws SQLException
	{
		ctx.stopIfRequested();
		final DateField field = (DateField)type.getDeclaredFeature("date");
		final Model model = type.getModel();
		final String bf =
				"DELETE " + removePrefix(
						"SELECT " + quoteName(model, getPrimaryKeyColumnName(type)) + ' ',
						search(type.newQuery(field.less(limit)))
				);
		final int rows;
		final long start = System.nanoTime();
		try(Statement stat = con.createStatement())
		{
			rows = stat.executeUpdate(bf);
		}
		final long end = System.nanoTime();

		try(TransactionTry tx = model.startTransactionTry(samplerString + " purge register"))
		{
			new SamplerPurge(type, limit, rows, toMillies(end, start));
			tx.commit();
		}

		ctx.incrementProgress(rows);
	}

	private static String removePrefix(final String prefix, final String pattern)
	{
		if(!pattern.startsWith(prefix))
			throw new RuntimeException(prefix + "---" + pattern);
		return pattern.substring(prefix.length());
	}


	SamplerPurge(
			final Type<?> type,
			final Date limit,
			final int rows,
			final long elapsed)
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

	long getElapsed()
	{
		return elapsed.getMandatory(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<SamplerPurge> TYPE = TypesBound.newType(SamplerPurge.class);
}

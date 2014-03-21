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

package com.exedio.cope.revstat;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.Model;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.misc.ModelTransaction;
import com.exedio.cope.util.JobContext;
import java.util.Date;
import java.util.List;

@CopeName("CopeRevstat")
final class Revstat extends Item
{
	private static final IntegerField number = new IntegerField().toFinal().unique().min(0);
	private static final DateField date = new DateField().toFinal();
	private static final IntegerField size = new IntegerField().toFinal().min(0);
	private static final IntegerField rows = new IntegerField().toFinal().min(0);
	private static final LongField elapsed = new LongField().toFinal().min(0);
	private static final StringField comment = new StringField().toFinal().lengthMax(5000);

	static void write(
			final Model model,
			final int number,
			final RevisionInfoRevise revision,
			final JobContext ctx)
	{
		if(number!=revision.getNumber())
			throw new IllegalArgumentException("" + number + '/' + revision.getNumber());

		ctx.stopIfRequested();

		final List<Body> bodies = revision.getBody();
		int rows = 0;
		long elapsed = 0;
		for(final Body body : bodies)
		{
			rows += body.getRows();
			elapsed += body.getElapsed();
		}

		String comment = revision.getComment();
		if(comment==null)
			comment = "FOUND NULL BY CopeRevstat";
		else if(comment.isEmpty())
			comment = "FOUND EMPTY BY CopeRevstat";

		try(ModelTransaction tx = ModelTransaction.startTransaction(model, RevisionStatistics.class.getName() + '#' + number))
		{
			final Revstat result;
			try
			{
				result = TYPE.newItem(
					Revstat.number.map(number),
					Revstat.date.map(revision.getDate()),
					Revstat.size.map(bodies.size()),
					Revstat.rows.map(rows),
					Revstat.elapsed.map(elapsed),
					Revstat.comment.map(comment));
			}
			catch(final UniqueViolationException e)
			{
				return;
			}

			int bodyNumber = 0;
			for(final Body body : bodies)
				RevstatBody.get(result, bodyNumber++, body);

			tx.commit();
			ctx.incrementProgress();
		}
	}

	int getNumber()
	{
		return number.getMandatory(this);
	}

	static Revstat forNumber(final int number)
	{
		return Revstat.number.searchUnique(Revstat.class, number);
	}

	Date getDate()
	{
		return date.get(this);
	}

	int getSize()
	{
		return size.getMandatory(this);
	}

	int getRows()
	{
		return rows.getMandatory(this);
	}

	String getComment()
	{
		return comment.get(this);
	}

	List<RevstatBody> getBody()
	{
		return RevstatBody.getBodyParts(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<Revstat> TYPE = TypesBound.newType(Revstat.class);

	private Revstat(final ActivationParameters ap) { super(ap); }
}

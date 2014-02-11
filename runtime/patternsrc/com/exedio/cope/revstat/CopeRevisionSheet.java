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
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.RevisionInfoRevise;
import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueViolationException;
import com.exedio.cope.util.JobContext;
import java.util.Date;
import java.util.List;

final class CopeRevisionSheet extends Item
{
	private static final IntegerField number = new IntegerField().toFinal().unique().min(0);
	private static final DateField date = new DateField().toFinal();
	private static final StringField comment = new StringField().toFinal().lengthMax(5000);

	static void write(
			final Model model,
			final int number,
			final RevisionInfoRevise revision,
			final JobContext ctx)
	{
		if(number!=revision.getNumber())
			throw new IllegalArgumentException("" + number + '/' + revision.getNumber());

		try
		{
			ctx.stopIfRequested();
			model.startTransaction(RevisionStatistics.class.getName() + '#' + number);

			final CopeRevisionSheet result;
			try
			{
				result = TYPE.newItem(
					CopeRevisionSheet.number.map(number),
					CopeRevisionSheet.date.map(revision.getDate()),
					CopeRevisionSheet.comment.map(revision.getComment()));
			}
			catch(final UniqueViolationException e)
			{
				return;
			}

			int bodyNumber = 0;
			for(final Body body : revision.getBody())
				CopeRevisionSheetBody.get(result, bodyNumber++, body);

			model.commit();
			ctx.incrementProgress();
		}
		finally
		{
			model.rollbackIfNotCommitted();
		}
	}

	int getNumber()
	{
		return number.getMandatory(this);
	}

	static CopeRevisionSheet forNumber(final int number)
	{
		return CopeRevisionSheet.number.searchUnique(CopeRevisionSheet.class, number);
	}

	Date getDate()
	{
		return date.get(this);
	}

	String getComment()
	{
		return comment.get(this);
	}

	List<CopeRevisionSheetBody> getBody()
	{
		return CopeRevisionSheetBody.getBodyParts(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<CopeRevisionSheet> TYPE = TypesBound.newType(CopeRevisionSheet.class);

	private CopeRevisionSheet(final ActivationParameters ap) { super(ap); }
}

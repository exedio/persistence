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

package com.exedio.cope.revstat;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.misc.Computed;
import com.exedio.cope.pattern.PartOf;
import java.util.List;

@Computed()
@CopeName("CopeRevstatBody")
final class RevstatBody extends Item
{
	private static final ItemField<Revstat> revision = ItemField.create(Revstat.class).toFinal();

	private static final IntegerField number = new IntegerField().toFinal().min(0).copyFrom(revision);
	private static final DateField date = new DateField().toFinal().copyFrom(revision);

	private static final IntegerField bodyNumber = new IntegerField().toFinal().min(0);
	@SuppressWarnings("unused")
	private static final UniqueConstraint revisionAndBodyNumber = new UniqueConstraint(revision, bodyNumber);
	private static final PartOf<Revstat> body = PartOf.create(revision, bodyNumber);
	private static final IntegerField rows = new IntegerField().toFinal().min(0);
	private static final LongField elapsed = new LongField().toFinal().min(0);
	private static final StringField sql = new StringField().toFinal().lengthMax(100000);

	static void get(final Revstat revision, final int bodyNumber, final Body body)
	{
		TYPE.newItem(
				RevstatBody.revision.map(revision),
				RevstatBody.number.map(revision.getNumber()),
				RevstatBody.date.map(revision.getDate()),
				RevstatBody.bodyNumber.map(bodyNumber),
				RevstatBody.rows.map(body.getRows()),
				RevstatBody.elapsed.map(body.getElapsed()),
				Util.cutAndMap(RevstatBody.sql, body.getSQL()));
	}

	int getBodyNumber()
	{
		return bodyNumber.getMandatory(this);
	}

	static List<RevstatBody> getBodyParts(final Revstat container)
	{
		return RevstatBody.body.getParts(RevstatBody.class, container);
	}

	int getRows()
	{
		return rows.getMandatory(this);
	}

	String getSQL()
	{
		return sql.get(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<RevstatBody> TYPE = TypesBound.newType(RevstatBody.class);

	private RevstatBody(final ActivationParameters ap) { super(ap); }
}

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

package com.exedio.cope.revsheet;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.RevisionInfoRevise.Body;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;

final class CopeRevisionSheetBody extends Item
{
	private static final ItemField<CopeRevisionSheet> revision = ItemField.create(CopeRevisionSheet.class).toFinal();
	private static final IntegerField number = new IntegerField().toFinal();
	@SuppressWarnings("unused")
	private static final UniqueConstraint revisionAndNumber = new UniqueConstraint(revision, number);
	private static final StringField sql = new StringField().toFinal();
	private static final IntegerField rows = new IntegerField().toFinal();
	private static final LongField elapsed = new LongField().toFinal();

	static void get(final CopeRevisionSheet revision, final int number, final Body body)
	{
		String sql = body.getSQL();
		if(sql.length()>1000)
			sql = sql.substring(0, 1000) + " SHORTENED";

		TYPE.newItem(
				CopeRevisionSheetBody.revision.map(revision),
				CopeRevisionSheetBody.number.map(number),
				CopeRevisionSheetBody.sql.map(sql),
				CopeRevisionSheetBody.rows.map(body.getRows()),
				CopeRevisionSheetBody.elapsed.map(body.getElapsed()));
	}

	static CopeRevisionSheetBody forNumber(final int number)
	{
		return CopeRevisionSheetBody.number.searchUnique(CopeRevisionSheetBody.class, number);
	}

	private static final long serialVersionUID = 1l;

	static final Type<CopeRevisionSheetBody> TYPE = TypesBound.newType(CopeRevisionSheetBody.class);

	private CopeRevisionSheetBody(final ActivationParameters ap) { super(ap); }
}

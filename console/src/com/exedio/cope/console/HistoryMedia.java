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

package com.exedio.cope.console;

import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class HistoryMedia extends Item
{
	static final ItemField<HistoryModel> model = newItemField(HistoryModel.class).toFinal();
	static final StringField media = new StringField().toFinal();
	
	static final DateField date = new DateField().toFinal();
	static final UniqueConstraint mediaAndDate = new UniqueConstraint(media, date);
	static final IntegerField running = new IntegerField().toFinal().min(0);
	
	static final IntegerField exception = new IntegerField().toFinal().min(0);
	static final IntegerField notAnItem = new IntegerField().toFinal().min(0);
	static final IntegerField noSuchItem = new IntegerField().toFinal().min(0);
	static final IntegerField isNull = new IntegerField().toFinal().min(0);
	static final IntegerField notComputable = new IntegerField().toFinal().min(0);
	static final IntegerField notModified = new IntegerField().toFinal().min(0);
	static final IntegerField delivered = new IntegerField().toFinal().min(0);
	
	HistoryMedia(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused")
	private HistoryMedia(final ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}
	
	private static final long serialVersionUID = 1l;
	
	static final Type TYPE = newType(HistoryMedia.class);
}

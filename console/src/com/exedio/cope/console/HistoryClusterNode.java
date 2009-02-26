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

import com.exedio.cope.CopyConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.util.ReactivationConstructorDummy;

final class HistoryClusterNode extends Item
{
	static final ItemField<HistoryModel> model = newItemField(HistoryModel.class).toFinal();
	static final IntegerField id = new IntegerField().toFinal();
	
	static final DateField date = new DateField().toFinal();
	static final UniqueConstraint idAndDate = new UniqueConstraint(id, date);
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();
	static final IntegerField thread = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);
	
	static final CopyConstraint dateCC = new CopyConstraint(model, date);
	static final CopyConstraint initializeDateCC = new CopyConstraint(model, initializeDate);
	static final CopyConstraint connectDateCC = new CopyConstraint(model, connectDate);
	static final CopyConstraint threadCC = new CopyConstraint(model, thread);
	static final CopyConstraint runningCC = new CopyConstraint(model, running);
	
	static final DateField firstEncounter = new DateField().toFinal();
	static final StringField fromAddress = new StringField().toFinal();
	static final IntegerField fromPort = new IntegerField().toFinal().range(0, 0xffff);
	static final Composite<SequenceInfo> ping = Composite.newComposite(SequenceInfo.class);
	static final Composite<SequenceInfo> pong = Composite.newComposite(SequenceInfo.class);
	static final Composite<SequenceInfo> invalidate = Composite.newComposite(SequenceInfo.class);
	
	HistoryClusterNode(final SetValue... setValues)
	{
		super(setValues);
	}
	
	@SuppressWarnings("unused")
	private HistoryClusterNode(final ReactivationConstructorDummy d, final int pk)
	{
		super(d,pk);
	}
	
	private static final long serialVersionUID = 1l;
	
	static final Type<HistoryClusterNode> TYPE = newType(HistoryClusterNode.class);
}

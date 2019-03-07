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

package com.exedio.cope.sampler;

import static com.exedio.cope.SetValue.map;
import static com.exedio.cope.sampler.Util.field;
import static com.exedio.cope.sampler.Util.maC;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Transaction;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.CompositeField;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Purgeable
@CopeExternal
@CopeSchemaName("DiffTransaction")
final class SamplerTransaction extends Item
{
	private static final ItemField<SamplerModel> model = ItemField.create(SamplerModel.class).toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model);

	static SetValue<?> mapIt(final SamplerModel m)
	{
		return map(model, m);
	}


	private static final LongField id  = new LongField().toFinal().min(0);
	private static final StringField name = new StringField().toFinal().optional().lengthMax(500);
	private static final DateField startDate = new DateField().toFinal();
	private static final CompositeField<SamplerThread> thread  = CompositeField.create(SamplerThread.class).toFinal().optional();

	private static final IntegerField invalidationSize         = field(0);
	private static final IntegerField preCommitHookCount       = field(0);
	private static final IntegerField preCommitHookDuplicates  = field(0);
	private static final IntegerField postCommitHookCount      = field(0);
	private static final IntegerField postCommitHookDuplicates = field(0);

	static List<SetValue<?>> mapIt(final Transaction transaction)
	{
		return Arrays.asList(
			map(id,        transaction.getID()),
			maC(name,      transaction.getName()),
			map(startDate, transaction.getStartDate()),
			map(thread,    SamplerThread.create(transaction.getBoundThread())),
			map(invalidationSize,         transaction.getInvalidationSize()),
			map(preCommitHookCount,       transaction.getPreCommitHookCount()),
			map(preCommitHookDuplicates,  transaction.getPreCommitHookDuplicates()),
			map(postCommitHookCount,      transaction.getPostCommitHookCount()),
			map(postCommitHookDuplicates, transaction.getPostCommitHookDuplicates()));
	}


	@SuppressWarnings("unused")
	private SamplerTransaction(final ActivationParameters ap)
	{
		super(ap);
	}

	SamplerModel getModel()
	{
		return model.get(this);
	}

	Date getDate()
	{
		return date.get(this);
	}

	long getID()
	{
		return id.getMandatory(this);
	}

	String getName()
	{
		return name.get(this);
	}

	Date getStartDate()
	{
		return startDate.get(this);
	}

	SamplerThread getThread()
	{
		return thread.get(this);
	}

	private static final long serialVersionUID = 1l;

	static final Type<SamplerTransaction> TYPE = TypesBound.newType(SamplerTransaction.class);
}

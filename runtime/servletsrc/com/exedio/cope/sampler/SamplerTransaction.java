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

import static com.exedio.cope.sampler.StringUtil.cutAndMap;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.CopyConstraint;
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

final class SamplerTransaction extends Item
{
	private static final ItemField<SamplerModel> model = ItemField.create(SamplerModel.class).toFinal();

	private static final DateField date = new DateField().toFinal();
	private static final DateField initializeDate = new DateField().toFinal();
	private static final DateField connectDate = new DateField().toFinal();
	@CopeSchemaName("thread") private static final IntegerField sampler = new IntegerField().toFinal();
	private static final IntegerField running = new IntegerField().toFinal().min(0);

	@SuppressWarnings("unused") private static final CopyConstraint dateCC = new CopyConstraint(model, date);
	@SuppressWarnings("unused") private static final CopyConstraint initializeDateCC = new CopyConstraint(model, initializeDate);
	@SuppressWarnings("unused") private static final CopyConstraint connectDateCC = new CopyConstraint(model, connectDate);
	@SuppressWarnings("unused") private static final CopyConstraint samplerCC = new CopyConstraint(model, sampler);
	@SuppressWarnings("unused") private static final CopyConstraint runningCC = new CopyConstraint(model, running);

	static List<SetValue> map(final SamplerModel m)
	{
		return Arrays.asList((SetValue)
			model         .map(m),
			date          .map(SamplerModel.date.get(m)),
			initializeDate.map(SamplerModel.initializeDate.get(m)),
			connectDate   .map(SamplerModel.connectDate.get(m)),
			sampler       .map(SamplerModel.sampler.get(m)),
			running       .map(SamplerModel.running.get(m)));
	}


	private static final LongField id  = new LongField().toFinal().min(0);
	private static final StringField name = new StringField().toFinal().optional().lengthMax(500);
	private static final DateField startDate = new DateField().toFinal();
	private static final CompositeField<SamplerThread> thread  = CompositeField.create(SamplerThread.class).toFinal().optional();

	static List<SetValue> map(final Transaction transaction)
	{
		return Arrays.asList((SetValue)
			id       .map(transaction.getID()),
			cutAndMap(name, transaction.getName()),
			startDate.map(transaction.getStartDate()),
			thread   .map(SamplerThread.create(transaction.getBoundThread())));
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

	Date getInitalizeDate()
	{
		return initializeDate.get(this);
	}

	Date getConnectDate()
	{
		return connectDate.get(this);
	}

	int getSampler()
	{
		return sampler.getMandatory(this);
	}

	int getRunning()
	{
		return running.getMandatory(this);
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

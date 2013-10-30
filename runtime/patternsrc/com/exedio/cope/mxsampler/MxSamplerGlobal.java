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

package com.exedio.cope.mxsampler;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopyConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;

final class MxSamplerGlobal extends Item
{
	static final DateField date = new DateField().toFinal().unique();
	static final LongField duration = new LongField().toFinal();
	static final IntegerField sampler = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);

	static final LongField classTotalLoaded = new LongField().toFinal().min(0);
	static final IntegerField classLoaded = new IntegerField().toFinal().min(0);
	static final LongField classUnloaded = new LongField().toFinal().min(0);
	static final IntegerField objectPendingFinalizationCount = new IntegerField().toFinal().min(0);
	static final LongField totalCompilationTime = new LongField().toFinal().min(0);
	static final IntegerField availableProcessors = new IntegerField().toFinal().min(1);
	static final DoubleField systemLoadAverage = new DoubleField().optional().toFinal().min(0);

	static final IntegerField threadCount             = new IntegerField().toFinal().min(0);
	static final IntegerField peakThreadCount         = new IntegerField().toFinal().min(0);
	static final LongField    totalStartedThreadCount = new LongField   ().toFinal().min(0);
	static final IntegerField daemonThreadCount       = new IntegerField().toFinal().min(0);


	static <F extends FunctionField<?>> F replaceByCopy(final F field, final Type<?> type)
	{
		if(field.getType()!=TYPE)
			throw new IllegalArgumentException(field.getID());
		if(type==TYPE)
			return field;

		for(final CopyConstraint cc : type.getCopyConstraints())
		{
			if(cc.getTemplate()==field)
			{
				final FunctionField<?> result = cc.getCopy();
				@SuppressWarnings("unchecked")
				final F resultCasted = (F)result;
				return resultCasted;
			}
		}
		throw new RuntimeException(field.getID());
	}


	@SuppressWarnings("unused")
	private MxSamplerGlobal(final ActivationParameters ap)
	{
		super(ap);
	}

	private static final long serialVersionUID = 1l;

	static final Type<MxSamplerGlobal> TYPE = TypesBound.newType(MxSamplerGlobal.class);
}

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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.CopyConstraint;
import com.exedio.cope.DateField;
import com.exedio.cope.FunctionField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.LongField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.pattern.CompositeField;

@SuppressWarnings("unused") // OK: deprecated item
@Purgeable(last=true)
@CopeSchemaName("SamplerModel")
final class AbsoluteModel extends Item
{
	static final DateField date = new DateField().toFinal().unique();
	@AbsoluteNoDifferentiate
	static final LongField duration = new LongField().toFinal();
	static final DateField initializeDate = new DateField().toFinal();
	static final DateField connectDate = new DateField().toFinal();
	@CopeSchemaName("thread") static final IntegerField sampler = new IntegerField().toFinal();
	static final IntegerField running = new IntegerField().toFinal().min(0);

	@AbsoluteNoDifferentiate
	private static final IntegerField connectionPoolIdle = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolPut = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnGet = new IntegerField().toFinal().min(0);
	private static final IntegerField connectionPoolInvalidOnPut = new IntegerField().toFinal().min(0);


	static final LongField nextTransactionId = new LongField().toFinal().min(0);

	@CopeSchemaName("commitOutConnection")
	private static final LongField commitWithoutConnection = new LongField().toFinal().min(0);
	private static final LongField commitWithConnection    = new LongField().toFinal().min(0);
	@CopeSchemaName("rollbackOutConnection")
	private static final LongField rollbackWithoutConnection = new LongField().toFinal().min(0);
	private static final LongField rollbackWithConnection    = new LongField().toFinal().min(0);


	private static final LongField itemCacheHits = new LongField().toFinal().min(0);
	private static final LongField itemCacheMisses = new LongField().toFinal().min(0);
	private static final LongField itemCacheConcurrentLoads = new LongField().toFinal().min(0);
	private static final IntegerField itemCacheReplacementRuns = new IntegerField().toFinal().min(0);
	private static final IntegerField itemCacheReplacements = new IntegerField().toFinal().min(0);
	private static final LongField itemCacheInvalidationsOrdered = new LongField().toFinal().min(0);
	private static final LongField itemCacheInvalidationsDone = new LongField().toFinal().min(0);

	@AbsoluteNoDifferentiate
	private static final IntegerField itemCacheInvalidateLastSize = new IntegerField().toFinal().min(0);
	private static final LongField itemCacheInvalidateLastHits = new LongField().toFinal().min(0);
	private static final LongField itemCacheInvalidateLastPurged = new LongField().toFinal().min(0);


	private static final LongField queryCacheHits = new LongField().toFinal().min(0);
	private static final LongField queryCacheMisses = new LongField().toFinal().min(0);
	private static final LongField queryCacheReplacements = new LongField().toFinal().min(0);
	private static final LongField queryCacheInvalidations = new LongField().toFinal().min(0);


	private static final IntegerField changeListenerCleared  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerRemoved  = new IntegerField().toFinal().min(0);
	private static final IntegerField changeListenerFailed   = new IntegerField().toFinal().min(0);


	private static final    LongField changeListenerOverflow  = new LongField   ().toFinal().min(0);
	private static final    LongField changeListenerException = new LongField   ().toFinal().min(0);
	@AbsoluteNoDifferentiate
	private static final IntegerField changeListenerPending   = new IntegerField().toFinal().min(0);


	static final IntegerField mediasNoSuchPath = new IntegerField().toFinal().min(0);

	private static final IntegerField mediasRedirectFrom  = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasException     = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasGuessedUrl    = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotAnItem     = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNoSuchItem    = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasMoved         = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasIsNull        = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotComputable = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasNotModified   = new IntegerField().toFinal().min(0);
	private static final IntegerField mediasDelivered     = new IntegerField().toFinal().min(0);


	private static final LongField clusterSenderInvalidationSplit = new LongField().toFinal().min(0);


	private static final CompositeField<AbsoluteClusterListener> clusterListener = CompositeField.create(AbsoluteClusterListener.class).toFinal().optional();


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
	private AbsoluteModel(final ActivationParameters ap)
	{
		super(ap);
	}

	private static final long serialVersionUID = 1l;

	static final Type<AbsoluteModel> TYPE = TypesBound.newType(AbsoluteModel.class);
}

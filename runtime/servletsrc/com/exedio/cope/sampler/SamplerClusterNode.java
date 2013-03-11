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

import static com.exedio.cope.sampler.StringUtil.cutAndMap;

import java.util.Arrays;
import java.util.List;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.ClusterListenerInfo;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.CompositeField;

final class SamplerClusterNode extends Item
{
	private static final ItemField<SamplerModel> model = ItemField.create(SamplerModel.class).toFinal();
	private static final IntegerField id = new IntegerField().toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model);
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndId = new UniqueConstraint(date, id); // date must be first, so purging can use the index
	private static final DateField initializeDate = new DateField().toFinal().copyFrom(model);
	private static final DateField connectDate = new DateField().toFinal().copyFrom(model);
	@CopeSchemaName("thread") private static final IntegerField sampler = new IntegerField().toFinal().copyFrom(model);
	private static final IntegerField running = new IntegerField().toFinal().copyFrom(model).min(0);

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(final SamplerModel m)
	{
		return Arrays.asList((SetValue<?>)
			model         .map(m),
			date          .map(SamplerModel.date.get(m)),
			initializeDate.map(SamplerModel.initializeDate.get(m)),
			connectDate   .map(SamplerModel.connectDate.get(m)),
			sampler       .map(SamplerModel.sampler.get(m)),
			running       .map(SamplerModel.running.get(m)));
	}


	private static final DateField firstEncounter = new DateField().toFinal();
	private static final StringField fromAddress = new StringField().toFinal();
	@NoDifferentiate
	private static final IntegerField fromPort = new IntegerField().toFinal().range(0, 0xffff);
	private static final CompositeField<SequenceInfo> invalidate = CompositeField.create(SequenceInfo.class).toFinal();
	private static final CompositeField<SequenceInfo> ping = CompositeField.create(SequenceInfo.class).toFinal();
	private static final CompositeField<SequenceInfo> pong = CompositeField.create(SequenceInfo.class).toFinal();

	@SuppressWarnings("unchecked") static List<SetValue<?>> map(final ClusterListenerInfo.Node node)
	{
		return Arrays.asList((SetValue<?>)
				id            .map(node.getID()),
				firstEncounter.map(node.getFirstEncounter()),
				cutAndMap(fromAddress, node.getAddress().toString()),
				fromPort      .map(node.getPort()),

				invalidate.map(new SequenceInfo(node.getInvalidateInfo())),
				ping      .map(new SequenceInfo(node.getPingInfo())),
				pong      .map(new SequenceInfo(node.getPongInfo())));
	}


	@SuppressWarnings("unused")
	private SamplerClusterNode(final ActivationParameters ap)
	{
		super(ap);
	}

	private static final long serialVersionUID = 1l;

	static final Type<SamplerClusterNode> TYPE = TypesBound.newType(SamplerClusterNode.class);
}

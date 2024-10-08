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

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.CompositeField;
import java.io.Serial;

@Purgeable
@CopeExternal
@CopeSchemaName("DiffClusterNode")
final class SamplerClusterNode extends Item
{
	@UsageEntryPoint private static final ItemField<SamplerModel> model = ItemField.create(SamplerModel.class).toFinal();
	private static final IntegerField id = new IntegerField().toFinal();

	private static final DateField date = new DateField().toFinal().copyFrom(model, () -> SamplerModel.date);
	@UsageEntryPoint private static final UniqueConstraint dateAndId = UniqueConstraint.create(date, id); // date must be first, so purging can use the index

	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final DateField    firstEncounter = new DateField   ().toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final StringField  fromAddress    = new StringField ().toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField fromPort       = new IntegerField().toFinal().range(0, 0xffff);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final CompositeField<SequenceInfo> invalidate = CompositeField.create(SequenceInfo.class).toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final CompositeField<SequenceInfo> ping       = CompositeField.create(SequenceInfo.class).toFinal();
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final CompositeField<SequenceInfo> pong       = CompositeField.create(SequenceInfo.class).toFinal();

	@SuppressWarnings("unused")
	private SamplerClusterNode(final ActivationParameters ap){ super(ap); }
	@Serial
	private static final long serialVersionUID = 1l;
	static final Type<SamplerClusterNode> TYPE = TypesBound.newType(SamplerClusterNode.class, SamplerClusterNode::new);
}

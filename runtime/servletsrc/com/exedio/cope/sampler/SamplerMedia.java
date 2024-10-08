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

import static com.exedio.cope.sampler.Util.field;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import java.io.Serial;

@Purgeable
@CopeExternal
@CopeSchemaName("DiffMedia")
final class SamplerMedia extends Item
{
	@UsageEntryPoint private static final ItemField<SamplerModel  > model = field(SamplerModel  .class);
	@UsageEntryPoint private static final ItemField<SamplerMediaId> media = field(SamplerMediaId.class);

	private static final DateField date = new DateField().toFinal().copyFrom(model, () -> SamplerModel.date);
	@UsageEntryPoint private static final UniqueConstraint dateAndMedia = UniqueConstraint.create(date, media); // date must be first, so purging can use the index

	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField redirectFrom   = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField exception      = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField invalidSpecial = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField guessedUrl     = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField notAnItem      = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField noSuchItem     = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField moved          = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField isNull         = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField notComputable  = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField notModified    = field(0);
	@SuppressWarnings("unused") // OK: just for keeping metrics sampled in the past
	private static final IntegerField delivered      = field(0);


	@Serial
	private static final long serialVersionUID = 1l;
	static final Type<SamplerMedia> TYPE = TypesBound.newType(SamplerMedia.class, SamplerMedia::new);
	@SuppressWarnings("unused") private SamplerMedia(final ActivationParameters ap){ super(ap); }
}

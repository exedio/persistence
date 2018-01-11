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
import static com.exedio.cope.sampler.Util.maD;
import static com.exedio.cope.sampler.Util.maS;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.Cope;
import com.exedio.cope.CopeExternal;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.DateField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.SetValue;
import com.exedio.cope.Settable;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.UniqueConstraint;
import com.exedio.cope.pattern.MediaInfo;
import com.exedio.cope.pattern.MediaPath;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Purgeable
@CopeExternal
@CopeSchemaName("DiffMedia")
final class SamplerMedia extends Item
{
	private static final ItemField<SamplerModel  > model = field(SamplerModel  .class);
	private static final ItemField<SamplerMediaId> media = field(SamplerMediaId.class);

	private static final DateField date = new DateField().toFinal().copyFrom(model);
	@SuppressWarnings("unused") private static final UniqueConstraint dateAndMedia = UniqueConstraint.create(date, media); // date must be first, so purging can use the index

	static SetValue<?> mapIt(final SamplerModel m)
	{
		return map(model, m);
	}

	static SamplerMedia forModelAndType(final SamplerModel model, final MediaPath media)
	{
		return TYPE.searchSingleton(Cope.and(
				SamplerMedia.model.equal(model),
				SamplerMedia.media.equal(SamplerMediaId.forId(media.getID()))
		));
	}


	private static final IntegerField redirectFrom   = field(0);
	private static final IntegerField exception      = field(0);
	private static final IntegerField invalidSpecial = field(0);
	private static final IntegerField guessedUrl     = field(0);
	private static final IntegerField notAnItem      = field(0);
	private static final IntegerField noSuchItem     = field(0);
	private static final IntegerField moved          = field(0);
	private static final IntegerField isNull         = field(0);
	private static final IntegerField notComputable  = field(0);
	private static final IntegerField notModified    = field(0);
	private static final IntegerField delivered      = field(0);

	static List<SetValue<?>> mapIt(
			final MediaInfo from,
			final MediaInfo to)
	{
		final List<SetValue<?>> result = Arrays.asList(
			maS(media,          from.getPath          (), to.getPath          ()),
			maD(redirectFrom,   from.getRedirectFrom  (), to.getRedirectFrom  ()),
			maD(exception,      from.getException     (), to.getException     ()),
			maD(invalidSpecial, from.getInvalidSpecial(), to.getInvalidSpecial()),
			maD(guessedUrl,     from.getGuessedUrl    (), to.getGuessedUrl    ()),
			maD(notAnItem,      from.getNotAnItem     (), to.getNotAnItem     ()),
			maD(noSuchItem,     from.getNoSuchItem    (), to.getNoSuchItem    ()),
			maD(moved,          from.getMoved         (), to.getMoved         ()),
			maD(isNull,         from.getIsNull        (), to.getIsNull        ()),
			maD(notComputable,  from.getNotComputable (), to.getNotComputable ()),
			maD(notModified,    from.getNotModified   (), to.getNotModified   ()),
			maD(delivered,      from.getDelivered     (), to.getDelivered     ()));

		if(isDefault(result))
			return null;

		return result;
	}

	private static boolean isDefault(final List<SetValue<?>> result)
	{
		for(final SetValue<?> sv : result)
		{
			final Settable<?> s = sv.settable;
			if(s==media)
				continue;

			if(s instanceof IntegerField)
			{
				if((Integer)sv.value != 0)
					return false;
			}
			else
				throw new RuntimeException("" + sv);
		}
		return true;
	}


	int getDelivered()
	{
		return delivered.getMandatory(this);
	}

	SamplerModel getModel()
	{
		return model.get(this);
	}

	String getMedia()
	{
		return media.get(this).getID();
	}

	Date getDate()
	{
		return date.get(this);
	}

	private static final long serialVersionUID = 1l;
	static final Type<SamplerMedia> TYPE = TypesBound.newType(SamplerMedia.class);
	@SuppressWarnings("unused") private SamplerMedia(final ActivationParameters ap){ super(ap); }
}

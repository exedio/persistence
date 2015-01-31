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
import static com.exedio.cope.sampler.Util.maD;

import com.exedio.cope.IntegerField;
import com.exedio.cope.SetValue;
import com.exedio.cope.pattern.Composite;
import com.exedio.cope.util.SequenceChecker;

final class SequenceInfo extends Composite
{
	private static final long serialVersionUID = 1l;

	private static final IntegerField inOrder    = new IntegerField().min(0);
	private static final IntegerField outOfOrder = new IntegerField().min(0);
	private static final IntegerField duplicate  = new IntegerField().min(0);
	private static final IntegerField lost       = new IntegerField().min(0);
	private static final IntegerField late       = new IntegerField().min(0);
	private static final IntegerField pending    = new IntegerField().min(0);

	SequenceInfo(
			final SequenceChecker.Info from,
			final SequenceChecker.Info to)
	{
		this(
			maD(inOrder,    from.getInOrder   (), to.getInOrder   ()),
			maD(outOfOrder, from.getOutOfOrder(), to.getOutOfOrder()),
			maD(duplicate,  from.getDuplicate (), to.getDuplicate ()),
			maD(lost,       from.getLost      (), to.getLost      ()),
			maD(late,       from.getLate      (), to.getLate      ()),
			map(pending,    to.getPending     ()  ));
	}

	private SequenceInfo(final SetValue<?>... setValues)
	{
		super(setValues);
	}
}

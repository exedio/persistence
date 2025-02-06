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

package com.exedio.cope;

import static java.lang.Math.toIntExact;

import com.exedio.cope.instrument.Wrap;
import com.exedio.cope.instrument.WrapFeature;
import java.io.Serial;

@WrapFeature
public final class Sequence extends Feature
{
	@Serial
	private static final long serialVersionUID = 1l;

	private final int start;
	private final int end;
	final SequenceX sequenceX;

	public Sequence(final int start)
	{
		this(start, Integer.MAX_VALUE);
	}

	public Sequence(final int start, final int end)
	{
		this(null, start, start, end);
	}

	Sequence(final IntegerField defaultToNextField, final int start, final int min, final int end)
	{
		if(start<0)
			throw new IllegalArgumentException("start must be positive, but was " + start + '.');
		if(start>=end)
			throw new IllegalArgumentException("start must be less than end, but was " + start + " and " + end + '.');

		this.start = start;
		this.end = end;
		this.sequenceX = new SequenceX(
				defaultToNextField!=null ? defaultToNextField : this,
				com.exedio.dsmf.Sequence.Type.bit31,
				start, min, end);
	}

	public int getStart()
	{
		return start;
	}

	public int getEnd()
	{
		return end;
	}

	/**
	 * The result of this method is not managed by a {@link Transaction},
	 * and you don't need one for calling this method.
	 */
	@Wrap(
			order=10,
			doc={"Generates a new sequence number.",
					"The result is not managed by a '{@link com.exedio.cope.Transaction}'."})
	public int next()
	{
		return toIntExact(sequenceX.next());
	}

	public SequenceInfo getInfo()
	{
		return sequenceX.getInfo();
	}

	void connect(final Database database, final ModelMetrics metrics)
	{
		sequenceX.connectSequence(database, database.properties.trimmer(TrimClass.legacy).trimString(
				getType().schemaId + '_' + getDeclaredSchemaName()), metrics);
		database.addSequence(sequenceX);
	}

	void disconnect()
	{
		sequenceX.disconnect();
	}
}

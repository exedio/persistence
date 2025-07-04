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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.dsmf.Node.Color.ERROR;
import static com.exedio.dsmf.Node.Color.OK;
import static com.exedio.dsmf.Sequence.Type.bit31;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Node;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Sequence;
import org.junit.jupiter.api.Test;

public class SchemaMismatchSequenceStartTest extends SchemaMismatchTest
{
	public SchemaMismatchSequenceStartTest()
	{
		super(modelA, modelB);
	}

	@Test void testIt()
	{
		assertIt(null, OK, OK, modelA.getVerifiedSchema());

		assertEquals(name(ItemA.sequence), name(ItemB.sequence));

		final Schema schema = modelB.getVerifiedSchema();
		assertIt(null, OK, supported(ERROR), schema);

		final Sequence sequence = schema.getSequence(name(ItemA.sequence));
		assertIt(supported("unexpected start 55"), supported(ERROR), supported(ERROR), sequence);
		assertExistance(true, true, sequence);
		assertEquals(bit31, sequence.getType());
		assertEquals(null, sequence.getMismatchingType());
		assertEquals(66, sequence.getStartL());
		assertEquals(mysql ? null : Long.valueOf(55), sequence.getMismatchingStart());

		assertEqualsUnmodifiable(
				model.getConnectProperties().primaryKeyGenerator.persistent
				? asList(schema.getSequence(nameSeq(ItemB.TYPE.getThis())), sequence)
				: asList(                                                   sequence),
				schema.getSequences());
	}

	private String supported(final String s)
	{
		return mysql ? null : s;
	}

	private Node.Color supported(final Node.Color c)
	{
		return mysql ? OK : c;
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemA extends Item
	{
		@WrapperIgnore
		static final com.exedio.cope.Sequence sequence = new com.exedio.cope.Sequence(55);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemA> TYPE = com.exedio.cope.TypesBound.newType(ItemA.class,ItemA::new);

		@com.exedio.cope.instrument.Generated
		private ItemA(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("ItemAB")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class ItemB extends Item
	{
		@WrapperIgnore
		static final com.exedio.cope.Sequence sequence = new com.exedio.cope.Sequence(66);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ItemB> TYPE = com.exedio.cope.TypesBound.newType(ItemB.class,ItemB::new);

		@com.exedio.cope.instrument.Generated
		private ItemB(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final Model modelA = new Model(ItemA.TYPE);
	static final Model modelB = new Model(ItemB.TYPE);
}

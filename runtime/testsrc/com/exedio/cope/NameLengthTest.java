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

import static com.exedio.cope.SchemaInfo.getColumnName;
import static com.exedio.cope.SchemaInfo.getDefaultToNextSequenceName;
import static com.exedio.cope.SchemaInfo.getSequenceName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.dsmf.Constraint.Type.Check;
import static com.exedio.dsmf.Constraint.Type.ForeignKey;
import static com.exedio.dsmf.Constraint.Type.PrimaryKey;
import static com.exedio.dsmf.Constraint.Type.Unique;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.Test;

public class NameLengthTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE, LongItem.TYPE);

	public NameLengthTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test void testIt()
	{
		assertIt(AnItem  .TYPE, "AnItem");
		assertIt(LongItem.TYPE, "LooooooooooooooooooooItem");

		assertPrimaryKeySequenceName("AnItem_this_Seq", AnItem.TYPE);
		assertPrimaryKeySequenceName("LoooooooooooItem_this_Seq", LongItem.TYPE);

		assertIt(AnItem.fieldShort, "fieldShort");
		assertIt(AnItem.fieldLong , "fieldLooooooooooooooooooo");

		assertIt(AnItem.foreignShort, "foreignShort");
		assertIt(AnItem.foreignLong , "foreignLooooooooooooooooo");

		assertSequence(AnItem.nextShort, "AnItem_nextShort_Seq");
		assertSequence(AnItem.nextLong , "AnItem_nextLooooooooo_Seq");

		assertIt(AnItem.sequenceShort, "AnItem_sequenceShort");
		assertIt(AnItem.sequenceLong , "AnItem_sequenceLooooooooo");

		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(AnItem.TYPE));

		assertIt(table, PrimaryKey, "AnItem_PK");
		assertIt(table, ForeignKey, "AnItem_foreignShort_Fk");
		assertIt(table, Unique,     "AnItem_fieldShort_Unq");
		assertIt(table, Check,      "AnItem_fieldShort_EN");
		assertIt(table, Check,      "AnItem_checkShort");

		assertIt(table, ForeignKey, "AnItem_foreignLoooooooooooooooooooooooooooooooooooooooooo_Fk");
		assertIt(table, Unique,     "AnItem_fieldLooooooooooooooooooooooooooooooooooooooooooo_Unq");
		assertIt(table, Check,      "AnItem_fieldLoooooooooooooooooooooooooooooooooooooooooooo_EN");
		assertIt(table, Check,      "AnItem_checkLooooooooooooooooooooooooooooooooooooooooooooooo");

		final Table longTable = schema.getTable(getTableName(LongItem.TYPE));
		assertIt(longTable, PrimaryKey, "LooooooooooooooooooooItem_PK");

		assertEquals(OK, table.getCumulativeColor());
		assertEquals(OK, schema.getCumulativeColor());
	}


	private static void assertIt(final Type<?> type, final String name)
	{
		assertEquals(name, getTableName(type));
	}

	private static void assertIt(final Field<?> field, final String name)
	{
		assertEquals(name, getColumnName(field));
	}

	private static void assertSequence(final IntegerField field, final String name)
	{
		assertEquals(name, getDefaultToNextSequenceName(field));
	}

	private static void assertIt(final Sequence sequence, final String name)
	{
		assertEquals(name, getSequenceName(sequence));
	}

	private static void assertIt(final Table table, final Constraint.Type type, final String name)
	{
		final Constraint result = table.getConstraint(name);
		assertNotNull(result, name);
		assertEquals(type, result.getType(), name);
		assertEquals(OK, result.getCumulativeColor(), name);
	}


	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum AnEnum
	{
		eins, zwei
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@CopeName("fieldLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		@WrapperIgnore static final EnumField<AnEnum> fieldLong  = EnumField.create(AnEnum.class).toFinal().unique();
		@WrapperIgnore static final EnumField<AnEnum> fieldShort = EnumField.create(AnEnum.class).toFinal().unique();

		@CopeName("foreignLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		@WrapperIgnore static final ItemField<AnItem> foreignLong  = ItemField.create(AnItem.class).toFinal();
		@WrapperIgnore static final ItemField<AnItem> foreignShort = ItemField.create(AnItem.class).toFinal();

		@CopeName("checkLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		@SuppressWarnings("unused") // OK: CheckConstraint
		static final CheckConstraint checkLong  = new CheckConstraint(fieldShort.isNotNull());
		@SuppressWarnings("unused") // OK: CheckConstraint
		static final CheckConstraint checkShort = new CheckConstraint(fieldShort.isNotNull());

		@CopeName("sequenceLooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		@WrapperIgnore static final Sequence sequenceLong  = new Sequence(7);
		@WrapperIgnore static final Sequence sequenceShort = new Sequence(7);

		@CopeName("nextLooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		@WrapperIgnore static final IntegerField nextLong  = new IntegerField().toFinal().defaultToNext(5);
		@WrapperIgnore static final IntegerField nextShort = new IntegerField().toFinal().defaultToNext(5);

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@CopeName("LoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooongItem")
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class LongItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<LongItem> TYPE = com.exedio.cope.TypesBound.newType(LongItem.class,LongItem::new);

		@com.exedio.cope.instrument.Generated
		private LongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

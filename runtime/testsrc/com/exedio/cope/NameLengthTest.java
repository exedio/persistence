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
import static com.exedio.cope.SchemaInfo.getTypeColumnName;
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
import com.exedio.cope.tojunit.ModelConnector;
import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

public abstract class NameLengthTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE, SubItem.TYPE, LongItem.TYPE);

	private final boolean defaulT;

	protected NameLengthTest(final boolean defaulT)
	{
		super(MODEL);
		this.defaulT = defaulT;
		copeRule.omitTransaction();
	}

	@Test void testIt()
	{
		assertIt(AnItem  .TYPE, "AnItem", synthetic("class", "AnItem"));
		assertIt(LongItem.TYPE, defaulT
				? l60("LoooooooooooooooooooooooooooooooooooooooooooooooooooooooItem")
				: l20("LoooooooooooooooItem"));

		assertPrimaryKeySequenceName("AnItem_this_Seq", AnItem.TYPE);
		assertPrimaryKeySequenceName(defaulT
				? l60("LooooooooooooooooooooooooooooooooooooooooooooooItem_this_Seq")
				: l20("LooooooItem_this_Seq"),
				LongItem.TYPE);

		assertIt(AnItem.fieldShort, "fieldShort");
		assertIt(AnItem.fieldLong , defaulT
				? l60("fieldLoooooooooooooooooooooooooooooooooooooooooooooooooooooo")
				: l20("fieldLoooooooooooooo"));

		assertIt(AnItem.foreignShort,
				"foreignShort",
				"foreignShortType");
		assertIt(AnItem.foreignLong,
				defaulT
				? l60("foreignLoooooooooooooooooooooooooooooooooooooooooooooooooooo")
				: l20("foreignLoooooooooooo"),
				defaulT
				? l60("foreignLooooooooooooooooooooooooooooooooooooooooooooooooType")
				: l20("foreignLooooooooType"));

		assertSequence(AnItem.nextShort, "AnItem_nextShort_Seq");
		assertSequence(AnItem.nextLong , defaulT
				? l60("AnItem_nextLoooooooooooooooooooooooooooooooooooooooooooo_Seq")
				: l20("AnItem_nextLoooo_Seq"));

		assertIt(AnItem.sequenceShort, "AnItem_sequenceShort");
		assertIt(AnItem.sequenceLong , defaulT
				? l60("AnItem_sequenceLoooooooooooooooooooooooooooooooooooooooooooo")
				: l20("AnItem_sequenLoooooo"));

		final Schema schema = model.getVerifiedSchema();

		final Table table = schema.getTable(getTableName(AnItem.TYPE));

		assertIt(table, PrimaryKey, "AnItem_PK");
		assertIt(table, ForeignKey, "AnItem_foreignShort_Fk");
		assertIt(table, Unique,     "AnItem_fieldShort_Unq");
		assertIt(table, Check,      "AnItem_fieldShort_EN");
		assertIt(table, Check,      "AnItem_checkShort");

		assertIt(table, ForeignKey, defaulT
				? l60("AnItem_foreignLoooooooooooooooooooooooooooooooooooooooooo_Fk")
				: l30("AnItem_foreignLoooooooooooo_Fk"));
		assertIt(table, Unique,     defaulT
				? l60("AnItem_fieldLooooooooooooooooooooooooooooooooooooooooooo_Unq")
				: l30("AnItem_fieldLooooooooooooo_Unq"));
		assertIt(table, Check,      defaulT
				? l60("AnItem_fieldLoooooooooooooooooooooooooooooooooooooooooooo_EN")
				: l30("AnItem_fieldLoooooooooooooo_EN"));
		assertIt(table, Check,      defaulT
				? l60("AnItem_checkLooooooooooooooooooooooooooooooooooooooooooooooo")
				: l30("AnItem_checkLooooooooooooooooo"));

		final Table longTable = schema.getTable(getTableName(LongItem.TYPE));
		assertIt(longTable, PrimaryKey, defaulT
				? assertLength(60, "LooooooooooooooooooooooooooooooooooooooooooooooooooooItem_PK")
				: assertLength(23, "LoooooooooooooooItem_PK")); // table name was trimmed to 20 before appending "_PK"

		assertSchema(schema);
	}


	private static void assertIt(final Type<?> type, final String name)
	{
		assertEquals(name, getTableName(type));
	}

	private static void assertIt(final Type<?> type, final String name, final String typeColumnName)
	{
		assertIt(type, name);
		assertEquals(typeColumnName, getTypeColumnName(type));
	}

	private static void assertIt(final Field<?> field, final String name)
	{
		assertEquals(name, getColumnName(field));
	}

	private static void assertIt(final ItemField<?> field, final String name, final String typeColumnName)
	{
		assertIt(field, name);
		assertEquals(typeColumnName, getTypeColumnName(field));
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
		assertEquals(OK, result.getParticularColor(), name);
		assertEquals(OK, result.getCumulativeColor(), name);
	}

	private static String l20(final String actualName)
	{
		return assertLength(20, actualName);
	}

	private static String l30(final String actualName)
	{
		return assertLength(30, actualName);
	}

	private static String l60(final String actualName)
	{
		return assertLength(60, actualName);
	}

	private static String assertLength(final int expectedLength, final String actualName)
	{
		assertEquals(expectedLength, actualName.length(), actualName);
		return actualName;
	}

	@AfterEach final void afterEach()
	{
		ModelConnector.reset();
	}


	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum AnEnum
	{
		eins, zwei
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class AnItem extends Item
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
		protected AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class SubItem extends AnItem
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<SubItem> TYPE = com.exedio.cope.TypesBound.newType(SubItem.class,SubItem::new);

		@com.exedio.cope.instrument.Generated
		private SubItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
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

	static void assertProperties(final Model model)
	{
		assertProperties(60, 60, model);
	}

	static void assertProperties(final int standard, final int legacy, final Model model)
	{
		final ConnectProperties p = model.getConnectProperties();
		assertEquals(Integer.valueOf(standard), p.getField("schema.nameLength"      ).get());
		assertEquals(Integer.valueOf(legacy  ), p.getField("schema.nameLengthLegacy").get());
	}
}

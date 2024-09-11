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
import static com.exedio.cope.SchemaInfo.getPrimaryKeySequenceName;
import static com.exedio.cope.SchemaInfo.getSequenceName;
import static com.exedio.cope.SchemaInfo.getTableName;
import static com.exedio.dsmf.Constraint.Type.Check;
import static com.exedio.dsmf.Constraint.Type.ForeignKey;
import static com.exedio.dsmf.Constraint.Type.PrimaryKey;
import static com.exedio.dsmf.Constraint.Type.Unique;
import static com.exedio.dsmf.Node.Color.OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

		assertSequence(AnItem  .TYPE, "AnItem_this_Seq",           "AnItem_this_Seq6");
		assertSequence(LongItem.TYPE, "LoooooooooooItem_this_Seq", "LooooooooooItem_this_Seq6");

		assertIt(AnItem.fieldShort, "fieldShort");
		assertIt(AnItem.fieldLong , "fieldLooooooooooooooooooo");

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

	private void assertSequence(final Type<?> type, final String name, final String batchedName)
	{
		final PrimaryKeyGenerator primaryKeyGenerator = model.getConnectProperties().primaryKeyGenerator;
		switch(primaryKeyGenerator)
		{
			case memory -> {
			}
			case sequence ->
				assertEquals(name, getPrimaryKeySequenceName(type));

			case batchedSequence ->
				assertEquals(batchedName, getPrimaryKeySequenceName(type));

			default ->
				throw new RuntimeException(String.valueOf(primaryKeyGenerator));
		}
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

	private static final class AnItem extends Item
	{
		@CopeName("fieldLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final EnumField<AnEnum> fieldLong  = EnumField.create(AnEnum.class).toFinal().unique();
		static final EnumField<AnEnum> fieldShort = EnumField.create(AnEnum.class).toFinal().unique();

		@CopeName("foreignLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final ItemField<AnItem> foreignLong  = ItemField.create(AnItem.class).toFinal();
		static final ItemField<AnItem> foreignShort = ItemField.create(AnItem.class).toFinal();

		@CopeName("checkLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		@SuppressWarnings("unused") // OK: CheckConstraint
		static final CheckConstraint checkLong  = new CheckConstraint(fieldShort.isNotNull());
		@SuppressWarnings("unused") // OK: CheckConstraint
		static final CheckConstraint checkShort = new CheckConstraint(fieldShort.isNotNull());

		@CopeName("sequenceLooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final Sequence sequenceLong  = new Sequence(7);
		static final Sequence sequenceShort = new Sequence(7);

		@CopeName("nextLooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final IntegerField nextLong  = new IntegerField().toFinal().defaultToNext(5);
		static final IntegerField nextShort = new IntegerField().toFinal().defaultToNext(5);

	/**
	 * Creates a new AnItem with all the fields initially needed.
	 * @param fieldLong the initial value for field {@link #fieldLong}.
	 * @param fieldShort the initial value for field {@link #fieldShort}.
	 * @param foreignLong the initial value for field {@link #foreignLong}.
	 * @param foreignShort the initial value for field {@link #foreignShort}.
	 * @throws com.exedio.cope.MandatoryViolationException if fieldLong, fieldShort, foreignLong, foreignShort is null.
	 * @throws com.exedio.cope.UniqueViolationException if fieldLong, fieldShort is not unique.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private AnItem(
				@javax.annotation.Nonnull final AnEnum fieldLong,
				@javax.annotation.Nonnull final AnEnum fieldShort,
				@javax.annotation.Nonnull final AnItem foreignLong,
				@javax.annotation.Nonnull final AnItem foreignShort)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			com.exedio.cope.SetValue.map(AnItem.fieldLong,fieldLong),
			com.exedio.cope.SetValue.map(AnItem.fieldShort,fieldShort),
			com.exedio.cope.SetValue.map(AnItem.foreignLong,foreignLong),
			com.exedio.cope.SetValue.map(AnItem.foreignShort,foreignShort),
		});
	}

	/**
	 * Creates a new AnItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private AnItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #fieldLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AnEnum getFieldLong()
	{
		return AnItem.fieldLong.get(this);
	}

	/**
	 * Finds a anItem by its {@link #fieldLong}.
	 * @param fieldLong shall be equal to field {@link #fieldLong}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static AnItem forFieldLong(@javax.annotation.Nonnull final AnEnum fieldLong)
	{
		return AnItem.fieldLong.searchUnique(AnItem.class,fieldLong);
	}

	/**
	 * Finds a anItem by its {@link #fieldLong}.
	 * @param fieldLong shall be equal to field {@link #fieldLong}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static AnItem forFieldLongStrict(@javax.annotation.Nonnull final AnEnum fieldLong)
			throws
				java.lang.IllegalArgumentException
	{
		return AnItem.fieldLong.searchUniqueStrict(AnItem.class,fieldLong);
	}

	/**
	 * Returns the value of {@link #fieldShort}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AnEnum getFieldShort()
	{
		return AnItem.fieldShort.get(this);
	}

	/**
	 * Finds a anItem by its {@link #fieldShort}.
	 * @param fieldShort shall be equal to field {@link #fieldShort}.
	 * @return null if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="for")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nullable
	static AnItem forFieldShort(@javax.annotation.Nonnull final AnEnum fieldShort)
	{
		return AnItem.fieldShort.searchUnique(AnItem.class,fieldShort);
	}

	/**
	 * Finds a anItem by its {@link #fieldShort}.
	 * @param fieldShort shall be equal to field {@link #fieldShort}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="forStrict")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static AnItem forFieldShortStrict(@javax.annotation.Nonnull final AnEnum fieldShort)
			throws
				java.lang.IllegalArgumentException
	{
		return AnItem.fieldShort.searchUniqueStrict(AnItem.class,fieldShort);
	}

	/**
	 * Returns the value of {@link #foreignLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AnItem getForeignLong()
	{
		return AnItem.foreignLong.get(this);
	}

	/**
	 * Returns the value of {@link #foreignShort}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	AnItem getForeignShort()
	{
		return AnItem.foreignShort.get(this);
	}

	/**
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="next")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int nextSequenceLong()
	{
		return AnItem.sequenceLong.next();
	}

	/**
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="next")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int nextSequenceShort()
	{
		return AnItem.sequenceShort.next();
	}

	/**
	 * Returns the value of {@link #nextLong}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getNextLong()
	{
		return AnItem.nextLong.getMandatory(this);
	}

	/**
	 * Returns the value of {@link #nextShort}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int getNextShort()
	{
		return AnItem.nextShort.getMandatory(this);
	}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for anItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class,AnItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	@CopeName("LoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooongItem")
	private static final class LongItem extends Item
	{


	/**
	 * Creates a new LongItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private LongItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new LongItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private LongItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	@java.io.Serial
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for longItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<LongItem> TYPE = com.exedio.cope.TypesBound.newType(LongItem.class,LongItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private LongItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}

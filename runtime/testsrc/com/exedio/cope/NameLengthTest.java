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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.exedio.dsmf.Constraint;
import com.exedio.dsmf.Schema;
import com.exedio.dsmf.Table;
import org.junit.Test;

public class NameLengthTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(AnItem.TYPE, LongItem.TYPE);

	public NameLengthTest()
	{
		super(MODEL);
		copeRule.omitTransaction();
	}

	@Test public void testIt()
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
		final boolean cc = oracle;
		final boolean c  = oracle || (mysql && !model.getConnectProperties().mysqlLongConstraintNames);

		assertIt(table, PrimaryKey, "AnItem_PK");
		assertIt(table, ForeignKey, "AnItem_foreignShort_Fk");
		assertIt(table, Unique,     "AnItem_fieldShort_Unq");
		assertIt(table, Check,      "AnItem_fieldShort_EN");
		assertIt(table, Check,      "AnItem_checkShort");

		assertIt(table, ForeignKey, c ? "AnItem_foreignLooooooo_Fk" : "AnItem_foreignLoooooooooooooooooooooooooooooooooooooooooo_Fk");
		assertIt(table, Unique,     c ? "AnItem_fieldLoooooooo_Unq" : "AnItem_fieldLooooooooooooooooooooooooooooooooooooooooooo_Unq");
		assertIt(table, Check,     cc ? "AnItem_fieldLooooooooo_EN" : "AnItem_fieldLoooooooooooooooooooooooooooooooooooooooooooo_EN");
		assertIt(table, Check,     cc ? "AnItem_checkLoooooooooooo" : "AnItem_checkLooooooooooooooooooooooooooooooooooooooooooooooo");

		final Table longTable = schema.getTable(getTableName(LongItem.TYPE));
		assertIt(longTable, PrimaryKey, cc
				? "LoooooooooooooooooItem_PK"
				: "LooooooooooooooooooooItem_PK");

		assertEquals(OK, table.getCumulativeColor());
		assertEquals(OK, schema.getCumulativeColor());
	}


	private void assertIt(final Type<?> type, final String name)
	{
		assertEquals(filterTableName(name), getTableName(type));
	}

	private void assertSequence(final Type<?> type, final String name, final String batchedName)
	{
		final PrimaryKeyGenerator primaryKeyGenerator = model.getConnectProperties().primaryKeyGenerator;
		switch(primaryKeyGenerator)
		{
			case memory:
				break;
			case sequence:
				assertEquals(filterTableName(name), getPrimaryKeySequenceName(type));
				break;
			case batchedSequence:
				assertEquals(filterTableName(batchedName), getPrimaryKeySequenceName(type));
				break;
			default:
				throw new RuntimeException("" + primaryKeyGenerator);
		}
	}

	private static void assertIt(final Field<?> field, final String name)
	{
		assertEquals(name, getColumnName(field));
	}

	private void assertSequence(final IntegerField field, final String name)
	{
		assertEquals(filterTableName(name), getDefaultToNextSequenceName(field));
	}

	private void assertIt(final Sequence sequence, final String name)
	{
		assertEquals(filterTableName(name), getSequenceName(sequence));
	}

	private static void assertIt(final Table table, final Constraint.Type type, final String name)
	{
		final Constraint result = table.getConstraint(name);
		assertNotNull(name, result);
		assertEquals(name, type, result.getType());
		assertEquals(name, OK, result.getCumulativeColor());
	}


	enum AnEnum
	{
		eins, zwei;
	}

	static final class AnItem extends Item
	{
		@CopeName("fieldLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final EnumField<AnEnum> fieldLong  = EnumField.create(AnEnum.class).toFinal().unique();
		static final EnumField<AnEnum> fieldShort = EnumField.create(AnEnum.class).toFinal().unique();

		@CopeName("foreignLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final ItemField<AnItem> foreignLong  = ItemField.create(AnItem.class).toFinal();
		static final ItemField<AnItem> foreignShort = ItemField.create(AnItem.class).toFinal();

		@CopeName("checkLoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final CheckConstraint checkLong  = new CheckConstraint(fieldShort.isNotNull());
		static final CheckConstraint checkShort = new CheckConstraint(fieldShort.isNotNull());

		@CopeName("sequenceLooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final Sequence sequenceLong  = new Sequence(7);
		static final Sequence sequenceShort = new Sequence(7);

		@CopeName("nextLooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
		static final IntegerField nextLong  = new IntegerField().toFinal().defaultToNext(5);
		static final IntegerField nextShort = new IntegerField().toFinal().defaultToNext(5);

	/**

	 **
	 * Creates a new AnItem with all the fields initially needed.
	 * @param fieldLong the initial value for field {@link #fieldLong}.
	 * @param fieldShort the initial value for field {@link #fieldShort}.
	 * @param foreignLong the initial value for field {@link #foreignLong}.
	 * @param foreignShort the initial value for field {@link #foreignShort}.
	 * @throws com.exedio.cope.MandatoryViolationException if fieldLong, fieldShort, foreignLong, foreignShort is null.
	 * @throws com.exedio.cope.UniqueViolationException if fieldLong, fieldShort is not unique.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	AnItem(
				@javax.annotation.Nonnull final AnEnum fieldLong,
				@javax.annotation.Nonnull final AnEnum fieldShort,
				@javax.annotation.Nonnull final AnItem foreignLong,
				@javax.annotation.Nonnull final AnItem foreignShort)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			AnItem.fieldLong.map(fieldLong),
			AnItem.fieldShort.map(fieldShort),
			AnItem.foreignLong.map(foreignLong),
			AnItem.foreignShort.map(foreignShort),
		});
	}/**

	 **
	 * Creates a new AnItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private AnItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #fieldLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final AnEnum getFieldLong()
	{
		return AnItem.fieldLong.get(this);
	}/**

	 **
	 * Finds a anItem by it's {@link #fieldLong}.
	 * @param fieldLong shall be equal to field {@link #fieldLong}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.for public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	static final AnItem forFieldLong(@javax.annotation.Nonnull final AnEnum fieldLong)
	{
		return AnItem.fieldLong.searchUnique(AnItem.class,fieldLong);
	}/**

	 **
	 * Returns the value of {@link #fieldShort}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final AnEnum getFieldShort()
	{
		return AnItem.fieldShort.get(this);
	}/**

	 **
	 * Finds a anItem by it's {@link #fieldShort}.
	 * @param fieldShort shall be equal to field {@link #fieldShort}.
	 * @return null if there is no matching item.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.for public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nullable()
	static final AnItem forFieldShort(@javax.annotation.Nonnull final AnEnum fieldShort)
	{
		return AnItem.fieldShort.searchUnique(AnItem.class,fieldShort);
	}/**

	 **
	 * Returns the value of {@link #foreignLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final AnItem getForeignLong()
	{
		return AnItem.foreignLong.get(this);
	}/**

	 **
	 * Returns the value of {@link #foreignShort}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@javax.annotation.Nonnull()
	final AnItem getForeignShort()
	{
		return AnItem.foreignShort.get(this);
	}/**

	 **
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.next public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final int nextSequenceLong()
	{
		return AnItem.sequenceLong.next();
	}/**

	 **
	 * Generates a new sequence number.
	 * The result is not managed by a {@link com.exedio.cope.Transaction}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.next public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final int nextSequenceShort()
	{
		return AnItem.sequenceShort.next();
	}/**

	 **
	 * Returns the value of {@link #nextLong}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final int getNextLong()
	{
		return AnItem.nextLong.getMandatory(this);
	}/**

	 **
	 * Returns the value of {@link #nextShort}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	final int getNextShort()
	{
		return AnItem.nextShort.getMandatory(this);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for anItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<AnItem> TYPE = com.exedio.cope.TypesBound.newType(AnItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

	@CopeName("LoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooongItem")
	static final class LongItem extends Item
	{
	/**

	 **
	 * Creates a new LongItem with all the fields initially needed.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	LongItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}/**

	 **
	 * Creates a new LongItem and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private LongItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;/**

	 **
	 * The persistent type information for longItem.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.type public|package|protected|private|none</tt> in the class comment.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	static final com.exedio.cope.Type<LongItem> TYPE = com.exedio.cope.TypesBound.newType(LongItem.class);/**

	 **
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private LongItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}
}

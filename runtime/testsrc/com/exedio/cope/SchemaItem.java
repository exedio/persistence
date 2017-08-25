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

import com.exedio.cope.testmodel.AttributeItem.SomeEnum;
import com.exedio.cope.util.CharSet;

@CopeName("Main")
final class SchemaItem extends Item
{
	static final IntegerField nonFinalInteger = new IntegerField();

	static final StringField string = new StringField();
	static final IntegerField integ = new IntegerField().range(-10, 10);
	static final DoubleField doub = new DoubleField().range(-11.1, 11.1);
	static final DateField date = new DateField();
	static final DayField day = new DayField();
	static final BooleanField bool = new BooleanField();
	static final EnumField<SomeEnum> anEnum = EnumField.create(SomeEnum.class);
	static final ItemField<SchemaTargetItem> item = ItemField.create(SchemaTargetItem.class);
	static final ItemField<SchemaTargetPolymorphicItem> poly = ItemField.create(SchemaTargetPolymorphicItem.class);

	static final StringField stringOpt = new StringField().optional();
	static final IntegerField integOpt = new IntegerField().range(-10, 10).optional();
	static final DoubleField doubOpt = new DoubleField().range(-11.1, 11.1).optional();
	static final DateField dateOpt = new DateField().optional();
	static final DayField dayOpt = new DayField().optional();
	static final BooleanField boolOpt = new BooleanField().optional();
	static final EnumField<SomeEnum> enumOpt = EnumField.create(SomeEnum.class).optional();
	static final ItemField<SchemaTargetItem> itemOpt = ItemField.create(SchemaTargetItem.class).optional();
	static final ItemField<SchemaTargetPolymorphicItem> polyOpt = ItemField.create(SchemaTargetPolymorphicItem.class).optional();

	static final StringField uniqueString = new StringField().optional().unique();
	static final UniqueConstraint doubleUnique = new UniqueConstraint(string, anEnum);

	static final StringField stringMin4 = new StringField().optional().lengthMin(4);
	static final StringField stringMax4 = new StringField().optional().lengthMax(4);
	static final StringField stringMin4Max8 = new StringField().optional().lengthRange(4, 8);
	static final StringField stringExact6 = new StringField().optional().lengthExact(6);
	static final StringField stringUpper6 = new StringField().optional().lengthExact(6).charSet(CharSet.ALPHA_UPPER);
	static final StringField stringEmpty = new StringField().optional().lengthMin(0);
	static final StringField stringLong = new StringField().lengthMax(100000);

	static final DataField data = new DataField().optional();

	/**
	 * Creates a new SchemaItem with all the fields initially needed.
	 * @param nonFinalInteger the initial value for field {@link #nonFinalInteger}.
	 * @param string the initial value for field {@link #string}.
	 * @param integ the initial value for field {@link #integ}.
	 * @param doub the initial value for field {@link #doub}.
	 * @param date the initial value for field {@link #date}.
	 * @param day the initial value for field {@link #day}.
	 * @param bool the initial value for field {@link #bool}.
	 * @param anEnum the initial value for field {@link #anEnum}.
	 * @param item the initial value for field {@link #item}.
	 * @param poly the initial value for field {@link #poly}.
	 * @param stringLong the initial value for field {@link #stringLong}.
	 * @throws com.exedio.cope.DoubleRangeViolationException if doub violates its range constraint.
	 * @throws com.exedio.cope.IntegerRangeViolationException if integ violates its range constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if string, date, day, anEnum, item, poly, stringLong is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string, stringLong violates its length constraint.
	 * @throws com.exedio.cope.UniqueViolationException if string, anEnum is not unique.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	SchemaItem(
				final int nonFinalInteger,
				@javax.annotation.Nonnull final java.lang.String string,
				final int integ,
				final double doub,
				@javax.annotation.Nonnull final java.util.Date date,
				@javax.annotation.Nonnull final com.exedio.cope.util.Day day,
				final boolean bool,
				@javax.annotation.Nonnull final SomeEnum anEnum,
				@javax.annotation.Nonnull final SchemaTargetItem item,
				@javax.annotation.Nonnull final SchemaTargetPolymorphicItem poly,
				@javax.annotation.Nonnull final java.lang.String stringLong)
			throws
				com.exedio.cope.DoubleRangeViolationException,
				com.exedio.cope.IntegerRangeViolationException,
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.UniqueViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			SchemaItem.nonFinalInteger.map(nonFinalInteger),
			SchemaItem.string.map(string),
			SchemaItem.integ.map(integ),
			SchemaItem.doub.map(doub),
			SchemaItem.date.map(date),
			SchemaItem.day.map(day),
			SchemaItem.bool.map(bool),
			SchemaItem.anEnum.map(anEnum),
			SchemaItem.item.map(item),
			SchemaItem.poly.map(poly),
			SchemaItem.stringLong.map(stringLong),
		});
	}

	/**
	 * Creates a new SchemaItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private SchemaItem(final com.exedio.cope.SetValue<?>... setValues)
	{
		super(setValues);
	}

	/**
	 * Returns the value of {@link #nonFinalInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getNonFinalInteger()
	{
		return SchemaItem.nonFinalInteger.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #nonFinalInteger}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setNonFinalInteger(final int nonFinalInteger)
	{
		SchemaItem.nonFinalInteger.set(this,nonFinalInteger);
	}

	/**
	 * Returns the value of {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.lang.String getString()
	{
		return SchemaItem.string.get(this);
	}

	/**
	 * Sets a new value for {@link #string}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setString(@javax.annotation.Nonnull final java.lang.String string)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.string.set(this,string);
	}

	/**
	 * Returns the value of {@link #integ}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final int getInteg()
	{
		return SchemaItem.integ.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #integ}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setInteg(final int integ)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		SchemaItem.integ.set(this,integ);
	}

	/**
	 * Returns the value of {@link #doub}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final double getDoub()
	{
		return SchemaItem.doub.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #doub}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDoub(final double doub)
			throws
				com.exedio.cope.DoubleRangeViolationException
	{
		SchemaItem.doub.set(this,doub);
	}

	/**
	 * Returns the value of {@link #date}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.util.Date getDate()
	{
		return SchemaItem.date.get(this);
	}

	/**
	 * Sets a new value for {@link #date}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDate(@javax.annotation.Nonnull final java.util.Date date)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		SchemaItem.date.set(this,date);
	}

	/**
	 * Sets the current date for the date field {@link #date}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDate()
	{
		SchemaItem.date.touch(this);
	}

	/**
	 * Returns the value of {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final com.exedio.cope.util.Day getDay()
	{
		return SchemaItem.day.get(this);
	}

	/**
	 * Sets a new value for {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDay(@javax.annotation.Nonnull final com.exedio.cope.util.Day day)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		SchemaItem.day.set(this,day);
	}

	/**
	 * Sets today for the date field {@link #day}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDay(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		SchemaItem.day.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #bool}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final boolean getBool()
	{
		return SchemaItem.bool.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #bool}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setBool(final boolean bool)
	{
		SchemaItem.bool.set(this,bool);
	}

	/**
	 * Returns the value of {@link #anEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final SomeEnum getAnEnum()
	{
		return SchemaItem.anEnum.get(this);
	}

	/**
	 * Sets a new value for {@link #anEnum}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setAnEnum(@javax.annotation.Nonnull final SomeEnum anEnum)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.UniqueViolationException
	{
		SchemaItem.anEnum.set(this,anEnum);
	}

	/**
	 * Returns the value of {@link #item}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final SchemaTargetItem getItem()
	{
		return SchemaItem.item.get(this);
	}

	/**
	 * Sets a new value for {@link #item}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItem(@javax.annotation.Nonnull final SchemaTargetItem item)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		SchemaItem.item.set(this,item);
	}

	/**
	 * Returns the value of {@link #poly}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final SchemaTargetPolymorphicItem getPoly()
	{
		return SchemaItem.poly.get(this);
	}

	/**
	 * Sets a new value for {@link #poly}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setPoly(@javax.annotation.Nonnull final SchemaTargetPolymorphicItem poly)
			throws
				com.exedio.cope.MandatoryViolationException
	{
		SchemaItem.poly.set(this,poly);
	}

	/**
	 * Returns the value of {@link #stringOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringOpt()
	{
		return SchemaItem.stringOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #stringOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringOpt(@javax.annotation.Nullable final java.lang.String stringOpt)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringOpt.set(this,stringOpt);
	}

	/**
	 * Returns the value of {@link #integOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Integer getIntegOpt()
	{
		return SchemaItem.integOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #integOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setIntegOpt(@javax.annotation.Nullable final java.lang.Integer integOpt)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		SchemaItem.integOpt.set(this,integOpt);
	}

	/**
	 * Returns the value of {@link #doubOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Double getDoubOpt()
	{
		return SchemaItem.doubOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #doubOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDoubOpt(@javax.annotation.Nullable final java.lang.Double doubOpt)
			throws
				com.exedio.cope.DoubleRangeViolationException
	{
		SchemaItem.doubOpt.set(this,doubOpt);
	}

	/**
	 * Returns the value of {@link #dateOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.util.Date getDateOpt()
	{
		return SchemaItem.dateOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #dateOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDateOpt(@javax.annotation.Nullable final java.util.Date dateOpt)
	{
		SchemaItem.dateOpt.set(this,dateOpt);
	}

	/**
	 * Sets the current date for the date field {@link #dateOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDateOpt()
	{
		SchemaItem.dateOpt.touch(this);
	}

	/**
	 * Returns the value of {@link #dayOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final com.exedio.cope.util.Day getDayOpt()
	{
		return SchemaItem.dayOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #dayOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDayOpt(@javax.annotation.Nullable final com.exedio.cope.util.Day dayOpt)
	{
		SchemaItem.dayOpt.set(this,dayOpt);
	}

	/**
	 * Sets today for the date field {@link #dayOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="touch")
	final void touchDayOpt(@javax.annotation.Nonnull final java.util.TimeZone zone)
	{
		SchemaItem.dayOpt.touch(this,zone);
	}

	/**
	 * Returns the value of {@link #boolOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.Boolean getBoolOpt()
	{
		return SchemaItem.boolOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #boolOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setBoolOpt(@javax.annotation.Nullable final java.lang.Boolean boolOpt)
	{
		SchemaItem.boolOpt.set(this,boolOpt);
	}

	/**
	 * Returns the value of {@link #enumOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final SomeEnum getEnumOpt()
	{
		return SchemaItem.enumOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #enumOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setEnumOpt(@javax.annotation.Nullable final SomeEnum enumOpt)
	{
		SchemaItem.enumOpt.set(this,enumOpt);
	}

	/**
	 * Returns the value of {@link #itemOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final SchemaTargetItem getItemOpt()
	{
		return SchemaItem.itemOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #itemOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setItemOpt(@javax.annotation.Nullable final SchemaTargetItem itemOpt)
	{
		SchemaItem.itemOpt.set(this,itemOpt);
	}

	/**
	 * Returns the value of {@link #polyOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final SchemaTargetPolymorphicItem getPolyOpt()
	{
		return SchemaItem.polyOpt.get(this);
	}

	/**
	 * Sets a new value for {@link #polyOpt}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setPolyOpt(@javax.annotation.Nullable final SchemaTargetPolymorphicItem polyOpt)
	{
		SchemaItem.polyOpt.set(this,polyOpt);
	}

	/**
	 * Returns the value of {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getUniqueString()
	{
		return SchemaItem.uniqueString.get(this);
	}

	/**
	 * Sets a new value for {@link #uniqueString}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setUniqueString(@javax.annotation.Nullable final java.lang.String uniqueString)
			throws
				com.exedio.cope.UniqueViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.uniqueString.set(this,uniqueString);
	}

	/**
	 * Finds a schemaItem by it's {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="for")
	@javax.annotation.Nullable
	static final SchemaItem forUniqueString(@javax.annotation.Nonnull final java.lang.String uniqueString)
	{
		return SchemaItem.uniqueString.searchUnique(SchemaItem.class,uniqueString);
	}

	/**
	 * Finds a schemaItem by its {@link #uniqueString}.
	 * @param uniqueString shall be equal to field {@link #uniqueString}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="forStrict")
	@javax.annotation.Nonnull
	static final SchemaItem forUniqueStringStrict(@javax.annotation.Nonnull final java.lang.String uniqueString)
			throws
				java.lang.IllegalArgumentException
	{
		return SchemaItem.uniqueString.searchUniqueStrict(SchemaItem.class,uniqueString);
	}

	/**
	 * Finds a schemaItem by it's unique fields.
	 * @param string shall be equal to field {@link #string}.
	 * @param anEnum shall be equal to field {@link #anEnum}.
	 * @return null if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finder")
	@javax.annotation.Nullable
	static final SchemaItem forDoubleUnique(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final SomeEnum anEnum)
	{
		return SchemaItem.doubleUnique.search(SchemaItem.class,string,anEnum);
	}

	/**
	 * Finds a schemaItem by its unique fields.
	 * @param string shall be equal to field {@link #string}.
	 * @param anEnum shall be equal to field {@link #anEnum}.
	 * @throws java.lang.IllegalArgumentException if there is no matching item.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="finderStrict")
	@javax.annotation.Nonnull
	static final SchemaItem forDoubleUniqueStrict(@javax.annotation.Nonnull final java.lang.String string,@javax.annotation.Nonnull final SomeEnum anEnum)
			throws
				java.lang.IllegalArgumentException
	{
		return SchemaItem.doubleUnique.searchStrict(SchemaItem.class,string,anEnum);
	}

	/**
	 * Returns the value of {@link #stringMin4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringMin4()
	{
		return SchemaItem.stringMin4.get(this);
	}

	/**
	 * Sets a new value for {@link #stringMin4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringMin4(@javax.annotation.Nullable final java.lang.String stringMin4)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringMin4.set(this,stringMin4);
	}

	/**
	 * Returns the value of {@link #stringMax4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringMax4()
	{
		return SchemaItem.stringMax4.get(this);
	}

	/**
	 * Sets a new value for {@link #stringMax4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringMax4(@javax.annotation.Nullable final java.lang.String stringMax4)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringMax4.set(this,stringMax4);
	}

	/**
	 * Returns the value of {@link #stringMin4Max8}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringMin4Max8()
	{
		return SchemaItem.stringMin4Max8.get(this);
	}

	/**
	 * Sets a new value for {@link #stringMin4Max8}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringMin4Max8(@javax.annotation.Nullable final java.lang.String stringMin4Max8)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringMin4Max8.set(this,stringMin4Max8);
	}

	/**
	 * Returns the value of {@link #stringExact6}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringExact6()
	{
		return SchemaItem.stringExact6.get(this);
	}

	/**
	 * Sets a new value for {@link #stringExact6}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringExact6(@javax.annotation.Nullable final java.lang.String stringExact6)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringExact6.set(this,stringExact6);
	}

	/**
	 * Returns the value of {@link #stringUpper6}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringUpper6()
	{
		return SchemaItem.stringUpper6.get(this);
	}

	/**
	 * Sets a new value for {@link #stringUpper6}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringUpper6(@javax.annotation.Nullable final java.lang.String stringUpper6)
			throws
				com.exedio.cope.StringLengthViolationException,
				com.exedio.cope.StringCharSetViolationException
	{
		SchemaItem.stringUpper6.set(this,stringUpper6);
	}

	/**
	 * Returns the value of {@link #stringEmpty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	final java.lang.String getStringEmpty()
	{
		return SchemaItem.stringEmpty.get(this);
	}

	/**
	 * Sets a new value for {@link #stringEmpty}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringEmpty(@javax.annotation.Nullable final java.lang.String stringEmpty)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringEmpty.set(this,stringEmpty);
	}

	/**
	 * Returns the value of {@link #stringLong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	final java.lang.String getStringLong()
	{
		return SchemaItem.stringLong.get(this);
	}

	/**
	 * Sets a new value for {@link #stringLong}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setStringLong(@javax.annotation.Nonnull final java.lang.String stringLong)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		SchemaItem.stringLong.set(this,stringLong);
	}

	/**
	 * Returns, whether there is no data for field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="isNull")
	final boolean isDataNull()
	{
		return SchemaItem.data.isNull(this);
	}

	/**
	 * Returns the length of the data of the data field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getLength")
	final long getDataLength()
	{
		return SchemaItem.data.getLength(this);
	}

	/**
	 * Returns the value of the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getArray")
	@javax.annotation.Nullable
	final byte[] getDataArray()
	{
		return SchemaItem.data.getArray(this);
	}

	/**
	 * Writes the data of this persistent data field into the given stream.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final void getData(@javax.annotation.Nonnull final java.io.OutputStream data)
			throws
				java.io.IOException
	{
		SchemaItem.data.get(this,data);
	}

	/**
	 * Writes the data of this persistent data field into the given file.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final void getData(@javax.annotation.Nonnull final java.io.File data)
			throws
				java.io.IOException
	{
		SchemaItem.data.get(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nullable final com.exedio.cope.DataField.Value data)
	{
		SchemaItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nullable final byte[] data)
	{
		SchemaItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nullable final java.io.InputStream data)
			throws
				java.io.IOException
	{
		SchemaItem.data.set(this,data);
	}

	/**
	 * Sets a new value for the persistent field {@link #data}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setData(@javax.annotation.Nullable final java.io.File data)
			throws
				java.io.IOException
	{
		SchemaItem.data.set(this,data);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for schemaItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<SchemaItem> TYPE = com.exedio.cope.TypesBound.newType(SchemaItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private SchemaItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

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
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.util.CharSet;
import com.exedio.cope.util.Day;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

public class FieldModifierTest
{
	@Test void testBoolean()
	{
		testField(new BooleanField().defaultTo(true), f -> {
			assertEquals(Boolean.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(Boolean.TRUE, f.getDefaultConstant());
		});
	}
	@Test void testBooleanUnique()
	{
		testField(new BooleanField().unique(), f -> {
			assertEquals(Boolean.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
		});
	}
	@Test void testBooleanDefaultFalse()
	{
		testField(new BooleanField().defaultTo(false), f -> {
			assertEquals(Boolean.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(Boolean.FALSE, f.getDefaultConstant());
		});
	}

	@Test void testData()
	{
		testField(new DataField().lengthMax(5555), f -> {
			assertEquals(DataField.Value.class, f.getValueClass());
			assertEquals(5555, f.getMaximumLength());
		});
	}

	@Test void testDate()
	{
		final Date def = Date.from(LocalDateTime.of(2014, 3, 14, 11, 0).toInstant(UTC));
		testField(new DateField().defaultTo(def).precisionMinute(), f -> {
			assertEquals(Date.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(def, f.getDefaultConstant());
			assertEquals(false, f.isDefaultNow());
			assertEquals(DateField.Precision.MINUTE, f.getPrecision());
			assertEquals(DateField.RoundingMode.UNNECESSARY, f.getRoundingMode());
		});
	}
	@Test void testDateUnique()
	{
		testField(new DateField().unique(), f -> {
			assertEquals(Date.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(false, f.isDefaultNow());
			assertEquals(DateField.Precision.MILLI, f.getPrecision());
			assertEquals(DateField.RoundingMode.UNNECESSARY, f.getRoundingMode());
		});
	}
	@Test void testDateDefaultNow()
	{
		testField(new DateField().defaultToNow().roundingMode(DateField.RoundingMode.FUTURE).precisionSecond(), f -> {
			assertEquals(Date.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(true, f.isDefaultNow());
			assertEquals(DateField.Precision.SECOND, f.getPrecision());
			assertEquals(DateField.RoundingMode.FUTURE, f.getRoundingMode());
		});
	}

	@Test void testDay()
	{
		testField(new DayField().defaultTo(new Day(2024, 3, 14)), f -> {
			assertEquals(Day.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(new Day(2024, 3, 14), f.getDefaultConstant());
			assertEquals(false, f.isDefaultNow());
		});
	}
	@Test void testDayUnique()
	{
		testField(new DayField().unique(), f -> {
			assertEquals(Day.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(false, f.isDefaultNow());
		});
	}
	@Test void testDayDefaultNow()
	{
		testField(new DayField().defaultToNow(UTC), f -> {
			assertEquals(Day.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(true, f.isDefaultNow());
		});
	}

	@Test void testInteger()
	{
		testField(new IntegerField().defaultTo(77).min(66).max(88), f -> {
			assertEquals(Integer.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(Integer.valueOf(77), f.getDefaultConstant());
			assertEquals(false, f.isDefaultNext());
			assertEquals(66, f.getMinimum());
			assertEquals(88, f.getMaximum());
		});
	}
	@Test void testIntegerUnique()
	{
		testField(new IntegerField().unique().min(66).max(88), f -> {
			assertEquals(Integer.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(false, f.isDefaultNext());
			assertEquals(66, f.getMinimum());
			assertEquals(88, f.getMaximum());
		});
	}
	@Test void testIntegerDefaultToNext()
	{
		testField(new IntegerField().defaultToNext(77).min(66).max(88), f -> {
			assertEquals(Integer.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(true, f.isDefaultNext());
			assertEquals(77, f.getDefaultNextStartX());
			assertEquals(66, f.getMinimum());
			assertEquals(88, f.getMaximum());
		});
	}

	@Test void testLong()
	{
		testField(new LongField().defaultTo(77l).min(66).max(88), f -> {
			assertEquals(Long.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(Long.valueOf(77), f.getDefaultConstant());
			assertEquals(false, f.isDefaultRandom());
			assertEquals(66, f.getMinimum());
			assertEquals(88, f.getMaximum());
		});
	}
	@Test void testLongUnique()
	{
		testField(new LongField().unique().min(66).max(88), f -> {
			assertEquals(Long.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(false, f.isDefaultRandom());
			assertEquals(66, f.getMinimum());
			assertEquals(88, f.getMaximum());
		});
	}
	@Test void testLongDefaultToRandom()
	{
		testField(new LongField().defaultToRandom(new Random()).min(0), f -> {
			assertEquals(Long.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(true, f.isDefaultRandom());
			assertEquals(0, f.getMinimum());
			assertEquals(Long.MAX_VALUE, f.getMaximum());
		});
	}

	@Test void testDouble()
	{
		testField(new DoubleField().defaultTo(77.7).min(66.6).max(88.8), f -> {
			assertEquals(Double.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(Double.valueOf(77.7), f.getDefaultConstant());
			assertEquals(66.6, f.getMinimum());
			assertEquals(88.8, f.getMaximum());
		});
	}
	@Test void testDoubleUnique()
	{
		testField(new DoubleField().unique().min(66.6).max(88.8), f -> {
			assertEquals(Double.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(66.6, f.getMinimum());
			assertEquals(88.8, f.getMaximum());
		});
	}

	@Test void testString()
	{
		testField(new StringField().defaultTo("defaultV").lengthRange(6, 88).charSet(CharSet.ALPHA_NUMERIC).regexp("[A-z]*"), f -> {
			assertEquals(String.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals("defaultV", f.getDefaultConstant());
			assertEquals(6, f.getMinimumLength());
			assertEquals(88, f.getMaximumLength());
			assertEquals(CharSet.ALPHA_NUMERIC, f.getCharSet());
			assertEquals("[A-z]*", f.getRegexp());
		});
	}
	@Test void testStringUnique()
	{
		testField(new StringField().unique().lengthRange(6, 88).charSet(CharSet.ALPHA_NUMERIC).regexp("[A-z]*"), f -> {
			assertEquals(String.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(6, f.getMinimumLength());
			assertEquals(88, f.getMaximumLength());
			assertEquals(CharSet.ALPHA_NUMERIC, f.getCharSet());
			assertEquals("[A-z]*", f.getRegexp());
		});
	}

	@Test void testEnum()
	{
		testField(EnumField.create(AnEnum.class).defaultTo(AnEnum.beta), f -> {
			assertEquals(AnEnum.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(AnEnum.beta, f.getDefaultConstant());
		});
	}
	@Test void testEnumUnique()
	{
		testField(EnumField.create(AnEnum.class).unique(), f -> {
			assertEquals(AnEnum.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
		});
	}
	private enum AnEnum { alpha, beta }

	@Test void testItem()
	{
		testField(ItemField.create(AnItem.class).cascade(), f -> {
			assertEquals(AnItem.class, f.getValueClass());
			assertNull(f.getImplicitUniqueConstraint());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(ItemField.DeletePolicy.CASCADE, f.getDeletePolicy());
		});
	}
	@Test void testItemUnique()
	{
		testField(ItemField.create(AnItem.class).unique(), f -> {
			assertEquals(AnItem.class, f.getValueClass());
			assertEquals(List.of(f), f.getImplicitUniqueConstraint().getFields());
			assertEquals(null, f.getDefaultConstant());
			assertEquals(ItemField.DeletePolicy.FORBID, f.getDeletePolicy());
		});
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static final class AnItem extends Item
	{
		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private AnItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	private static <F extends Field<?>> void testField(final F initial, final Consumer<F> constantAssertions)
	{
		assertEquals(false, initial.isFinal());
		assertEquals(true, initial.isMandatory());
		constantAssertions.accept(initial);

		@SuppressWarnings("unchecked")
		final F finalField = (F)initial.toFinal();
		assertEquals(true, finalField.isFinal());
		assertEquals(true, finalField.isMandatory());
		constantAssertions.accept(finalField);

		@SuppressWarnings("unchecked")
		final F finalOptional = (F)finalField.optional();
		assertEquals(true, finalOptional.isFinal());
		assertEquals(false, finalOptional.isMandatory());
		constantAssertions.accept(finalOptional);

		@SuppressWarnings("unchecked")
		final F finalMandatory = (F)finalOptional.mandatory();
		assertEquals(true, finalMandatory.isFinal());
		assertEquals(true, finalMandatory.isMandatory());
		constantAssertions.accept(finalMandatory);

		@SuppressWarnings("unchecked")
		final F optional = (F)initial.optional();
		assertEquals(false, optional.isFinal());
		assertEquals(false, optional.isMandatory());
		constantAssertions.accept(optional);

		@SuppressWarnings("unchecked")
		final F mandatory = (F)optional.mandatory();
		assertEquals(false, mandatory.isFinal());
		assertEquals(true, mandatory.isMandatory());
		constantAssertions.accept(mandatory);
	}
}

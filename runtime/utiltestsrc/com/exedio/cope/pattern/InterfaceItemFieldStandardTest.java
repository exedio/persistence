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

package com.exedio.cope.pattern;

import static java.util.Arrays.asList;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.junit.CopeAssert;
import java.util.List;
import org.junit.Test;

public class InterfaceItemFieldStandardTest extends CopeAssert
{
	static final class AnMandatoryItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		static final InterfaceItemField<InterfaceItemFieldInterface> field = InterfaceItemField.create(
				InterfaceItemFieldInterface.class,
				InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class);

		static final Type<AnMandatoryItem> TYPE = TypesBound.newType(AnMandatoryItem.class);
		private AnMandatoryItem(final ActivationParameters ap) { super(ap); }
	}

	static final class AnOptionalItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		static final InterfaceItemField<InterfaceItemFieldInterface> field = InterfaceItemField.create(
				InterfaceItemFieldInterface.class,
				InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class).optional();

		static final Type<AnOptionalItem> TYPE = TypesBound.newType(AnOptionalItem.class);
		private AnOptionalItem(final ActivationParameters ap) { super(ap); }
	}

	static final class AnFinalItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		static final InterfaceItemField<InterfaceItemFieldInterface> field = InterfaceItemField.create(
				InterfaceItemFieldInterface.class,
				InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class).toFinal();
	}

	static final class ThreeItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		/** @cope.ignore */
		static final InterfaceItemField<InterfaceItemFieldInterface> mandatory = InterfaceItemField.create(
				InterfaceItemFieldInterface.class,
				InterfaceItemFieldInterfaceImplementationA.class,
				InterfaceItemFieldInterfaceImplementationB.class,
				InterfaceItemFieldInterfaceImplementationC.class);

		/** @cope.ignore */
		static final InterfaceItemField<InterfaceItemFieldInterface> optional = InterfaceItemField.create(
				InterfaceItemFieldInterface.class,
				InterfaceItemFieldInterfaceImplementationA.class,
				InterfaceItemFieldInterfaceImplementationB.class,
				InterfaceItemFieldInterfaceImplementationC.class).
				optional();

		static final Type<ThreeItem> TYPE = TypesBound.newType(ThreeItem.class);
		private ThreeItem(final ActivationParameters ap) { super(ap); }
	}

	@Test
	public void testGetClasses()
	{
		assertEqualsUnmodifiable(
				asList(
						InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class),
				AnMandatoryItem.field.getClasses());
		assertEqualsUnmodifiable(
				asList(
						InterfaceItemFieldInterfaceImplementationA.class,
						InterfaceItemFieldInterfaceImplementationB.class,
						InterfaceItemFieldInterfaceImplementationC.class),
				ThreeItem.mandatory.getClasses());
	}

	@Test
	public void testOf()
	{
		final List<ItemField<?>> c = AnMandatoryItem.field.getComponents();
		assertEquals(2, c.size());

		assertSame(c.get(0), AnMandatoryItem.field.of(InterfaceItemFieldInterfaceImplementationA.class));
		assertSame(c.get(1), AnMandatoryItem.field.of(InterfaceItemFieldInterfaceImplementationB.class));
		try
		{
			AnMandatoryItem.field.of(InterfaceItemFieldInterfaceImplementationC.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"class >class com.exedio.cope.pattern.InterfaceItemFieldInterfaceImplementationC< is not supported by AnMandatoryItem.field",
				e.getMessage());
		}
		assertSame(c.get(0), AnMandatoryItem.field.of(InterfaceItemFieldInterfaceImplementationASub.class)); // TODO should not work
	}

	@Test
	public void testGetInitialExceptionsMandatory()
	{
		assertContains(MandatoryViolationException.class, AnMandatoryItem.field.getInitialExceptions());
	}

	@Test
	public void testGetInitialExceptionsOptional()
	{
		assertContains(AnOptionalItem.field.getInitialExceptions());
	}

	@Test
	public void testGetInitialType()
	{
		assertEquals(InterfaceItemFieldInterface.class, AnMandatoryItem.field.getInitialType());
	}

	@Test
	public void testIsFinal()
	{
		assertEquals(true, AnFinalItem.field.isFinal());
	}

	@Test
	public void testIsFinalFalse()
	{
		assertEquals(false, AnMandatoryItem.field.isFinal());
	}

	public void testIsInitial()
	{
		assertEquals(true, AnMandatoryItem.field.isInitial());
	}

	@Test
	public void testIsMandatory()
	{
		assertEquals(true, AnMandatoryItem.field.isMandatory());
	}

	@Test
	public void testIsMandatoryFalse()
	{
		assertEquals(false, AnOptionalItem.field.isMandatory());
	}

	@Test
	public void testMandatoryCheckConstraint()
	{
		assertEquals(
			"(" +
			"(AnMandatoryItem.field-InterfaceItemFieldInterfaceImplementationA is not null AND" +
			" AnMandatoryItem.field-InterfaceItemFieldInterfaceImplementationB is null) OR " +
			"(AnMandatoryItem.field-InterfaceItemFieldInterfaceImplementationA is null AND" +
			" AnMandatoryItem.field-InterfaceItemFieldInterfaceImplementationB is not null)" +
			")",
			check(AnMandatoryItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationA is not null AND" +
			" ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationB is null AND" +
			" ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationC is null) OR " +
			"(ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationA is null AND" +
			" ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationB is not null AND" +
			" ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationC is null) OR " +
			"(ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationA is null AND" +
			" ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationB is null AND" +
			" ThreeItem.mandatory-InterfaceItemFieldInterfaceImplementationC is not null)" +
			")",
			check(ThreeItem.mandatory).getCondition().toString());
	}

	@Test
	public void testOptionalCheckConstraint()
	{
		assertEquals(
			"(AnOptionalItem.field-InterfaceItemFieldInterfaceImplementationB is null OR" +
			" AnOptionalItem.field-InterfaceItemFieldInterfaceImplementationA is null)",
			check(AnOptionalItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.optional-InterfaceItemFieldInterfaceImplementationB is null AND" +
			" ThreeItem.optional-InterfaceItemFieldInterfaceImplementationC is null) OR " +
			"(ThreeItem.optional-InterfaceItemFieldInterfaceImplementationA is null AND" +
			" ThreeItem.optional-InterfaceItemFieldInterfaceImplementationC is null) OR " +
			"(ThreeItem.optional-InterfaceItemFieldInterfaceImplementationA is null AND" +
			" ThreeItem.optional-InterfaceItemFieldInterfaceImplementationB is null)" +
			")",
			check(ThreeItem.optional).getCondition().toString());
	}

	private static final CheckConstraint check(final InterfaceItemField<?> field)
	{
		return (CheckConstraint)field.getSourceFeatures().get(field.getSourceFeatures().size()-1);
	}
}

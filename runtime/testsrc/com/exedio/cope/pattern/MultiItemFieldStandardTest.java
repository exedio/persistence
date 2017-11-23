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

import static com.exedio.cope.ItemField.DeletePolicy.CASCADE;
import static com.exedio.cope.ItemField.DeletePolicy.FORBID;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.Assert.fail;

import com.exedio.cope.ActivationParameters;
import com.exedio.cope.CheckConstraint;
import com.exedio.cope.ItemField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.Type;
import com.exedio.cope.TypesBound;
import com.exedio.cope.instrument.WrapperIgnore;
import java.util.List;
import org.junit.jupiter.api.Test;

public class MultiItemFieldStandardTest
{
	@WrapperIgnore
	static final class AnMandatoryItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class);

		static final Type<AnMandatoryItem> TYPE = TypesBound.newType(AnMandatoryItem.class);
		private AnMandatoryItem(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static final class AnOptionalItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class).optional();

		static final Type<AnOptionalItem> TYPE = TypesBound.newType(AnOptionalItem.class);
		private AnOptionalItem(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static final class AnFinalItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class).toFinal();
	}

	@WrapperIgnore
	static final class ThreeItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		static final MultiItemField<MultiItemFieldValue> mandatory = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class,
				MultiItemFieldComponentC.class);

		static final MultiItemField<MultiItemFieldValue> optional = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class,
				MultiItemFieldComponentC.class).
				optional();

		static final Type<ThreeItem> TYPE = TypesBound.newType(ThreeItem.class);
		private ThreeItem(final ActivationParameters ap) { super(ap); }
	}

	@WrapperIgnore
	static final class AnCascadeItem extends com.exedio.cope.Item
	{
		private static final long serialVersionUID = 1l;

		static final MultiItemField<MultiItemFieldValue> field = MultiItemField.create(
				MultiItemFieldValue.class,
				MultiItemFieldComponentA.class,
				MultiItemFieldComponentB.class).cascade();

		static final Type<AnCascadeItem> TYPE = TypesBound.newType(AnCascadeItem.class);
		private AnCascadeItem(final ActivationParameters ap) { super(ap); }
	}

	@Test public void testGetComponentClasses()
	{
		assertEqualsUnmodifiable(
				asList(
						MultiItemFieldComponentA.class,
						MultiItemFieldComponentB.class),
				AnMandatoryItem.field.getComponentClasses());
		assertEqualsUnmodifiable(
				asList(
						MultiItemFieldComponentA.class,
						MultiItemFieldComponentB.class,
						MultiItemFieldComponentC.class),
				ThreeItem.mandatory.getComponentClasses());
	}

	@Test public void testOf()
	{
		final List<ItemField<?>> c = AnMandatoryItem.field.getComponents();
		assertEquals(2, c.size());

		assertSame(c.get(0), AnMandatoryItem.field.of(MultiItemFieldComponentA.class));
		assertSame(c.get(1), AnMandatoryItem.field.of(MultiItemFieldComponentB.class));
		try
		{
			AnMandatoryItem.field.of(MultiItemFieldComponentC.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"class >class com.exedio.cope.pattern.MultiItemFieldComponentC< is not supported by AnMandatoryItem.field",
				e.getMessage());
		}
		try
		{
			AnMandatoryItem.field.of(MultiItemFieldComponentASub.class);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals(
				"class >class com.exedio.cope.pattern.MultiItemFieldComponentASub< is not supported by AnMandatoryItem.field",
				e.getMessage());
		}
	}

	@Test public void testGetInitialExceptionsMandatory()
	{
		assertContains(MandatoryViolationException.class, AnMandatoryItem.field.getInitialExceptions());
	}

	@Test public void testGetInitialExceptionsOptional()
	{
		assertContains(AnOptionalItem.field.getInitialExceptions());
	}

	@Test public void testGetInitialType()
	{
		assertEquals(MultiItemFieldValue.class, AnMandatoryItem.field.getInitialType());
	}

	@Test public void testIsFinal()
	{
		assertEquals(true, AnFinalItem.field.isFinal());
	}

	@Test public void testIsFinalFalse()
	{
		assertEquals(false, AnMandatoryItem.field.isFinal());
	}

	@Test public void testIsInitial()
	{
		assertEquals(true, AnMandatoryItem.field.isInitial());
		assertEquals(false, AnOptionalItem.field.isInitial());
		assertEquals(true, AnFinalItem.field.isInitial());
	}

	@Test public void testIsMandatory()
	{
		assertEquals(true, AnMandatoryItem.field.isMandatory());
	}

	@Test public void testIsMandatoryFalse()
	{
		assertEquals(false, AnOptionalItem.field.isMandatory());
	}

	@Test public void testMandatoryCheckConstraint()
	{
		assertEquals(
			"(" +
			"(AnMandatoryItem.field-MultiItemFieldComponentA is not null AND" +
			" AnMandatoryItem.field-MultiItemFieldComponentB is null) OR " +
			"(AnMandatoryItem.field-MultiItemFieldComponentA is null AND" +
			" AnMandatoryItem.field-MultiItemFieldComponentB is not null)" +
			")",
			check(AnMandatoryItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is not null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is not null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.mandatory-MultiItemFieldComponentA is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentB is null AND" +
			" ThreeItem.mandatory-MultiItemFieldComponentC is not null)" +
			")",
			check(ThreeItem.mandatory).getCondition().toString());
	}

	@Test public void testOptionalCheckConstraint()
	{
		assertEquals(
			"(AnOptionalItem.field-MultiItemFieldComponentB is null OR" +
			" AnOptionalItem.field-MultiItemFieldComponentA is null)",
			check(AnOptionalItem.field).getCondition().toString());
		assertEquals(
			"(" +
			"(ThreeItem.optional-MultiItemFieldComponentB is null AND" +
			" ThreeItem.optional-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.optional-MultiItemFieldComponentA is null AND" +
			" ThreeItem.optional-MultiItemFieldComponentC is null) OR " +
			"(ThreeItem.optional-MultiItemFieldComponentA is null AND" +
			" ThreeItem.optional-MultiItemFieldComponentB is null)" +
			")",
			check(ThreeItem.optional).getCondition().toString());
	}

	private static CheckConstraint check(final MultiItemField<?> field)
	{
		return (CheckConstraint)field.getSourceFeatures().get(field.getSourceFeatures().size()-1);
	}

	@Test public void testDefaultPolicyForbid()
	{
		assertEquals(FORBID, AnMandatoryItem.field.getDeletePolicy());
		assertEquals(FORBID, AnMandatoryItem.field.getComponents().get(0).getDeletePolicy());
		assertEquals(FORBID, AnMandatoryItem.field.getComponents().get(1).getDeletePolicy());
	}

	@Test public void testCascadePolicy()
	{
		assertEquals(CASCADE, AnCascadeItem.field.getDeletePolicy());
		assertEquals(CASCADE, AnCascadeItem.field.getComponents().get(0).getDeletePolicy());
		assertEquals(CASCADE, AnCascadeItem.field.getComponents().get(1).getDeletePolicy());
	}

	@Test public void testEqualConditionNull3Classes()
	{
		assertEquals(
				"(ThreeItem.mandatory-MultiItemFieldComponentA is null AND" +
				" ThreeItem.mandatory-MultiItemFieldComponentB is null AND" +
				" ThreeItem.mandatory-MultiItemFieldComponentC is null)",
				ThreeItem.mandatory.equal(null).toString());
	}
}

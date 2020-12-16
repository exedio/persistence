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

import static com.exedio.cope.TypesBound.newType;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.exedio.cope.instrument.WrapperIgnore;
import com.exedio.cope.instrument.WrapperType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.junit.jupiter.api.Test;

@SuppressFBWarnings("NP_NULL_PARAM_DEREF_NONVIRTUAL")
public class TypesBoundErrorTest
{
	@Test void classNull()
	{
		assertFails(
				() -> newType(null),
				NullPointerException.class,
				"javaClass");
	}


	@Test void classItem()
	{
		assertFails(
				() -> newType(Item.class),
				IllegalArgumentException.class,
				"Cannot make a type for " + Item.class + " itself, but only for subclasses.");
	}


	@Test void classNoItem()
	{
		assertFails(
				() -> newType(castItemClass(NoItem.class)),
				IllegalArgumentException.class,
				NoItem.class + " is not a subclass of Item");
	}
	@SuppressWarnings({"unchecked", "rawtypes"}) // OK: test bad API usage
	private static Class<Item> castItemClass(final Class c)
	{
		return c;
	}
	static class NoItem
	{
		// just an empty class
	}


	@Test void noActivationConstructor()
	{
		final Exception e = assertFails(
				() -> newType(NoActivationConstructor.class),
				IllegalArgumentException.class,
				NoActivationConstructor.class.getName() +
				" does not have an activation constructor NoActivationConstructor(" + ActivationParameters.class.getName() + ")");
		assertEquals(NoSuchMethodException.class, e.getCause().getClass());
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class NoActivationConstructor extends Item
	{
		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void wrongActivationConstructor()
	{
		final Type<WrongActivationConstructor> wrongActivationConstructor = newType(WrongActivationConstructor.class);
		assertFails(
				() -> new Model(wrongActivationConstructor),
				IllegalArgumentException.class,
				"WrongActivationConstructor/" + WrongActivationConstructor.class.getName());
		assertFails(
				() -> newType(WrongActivationConstructor.class),
				IllegalArgumentException.class,
				"class is already bound to a type: " + WrongActivationConstructor.class.getName());
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class WrongActivationConstructor extends Item
	{
		WrongActivationConstructor(final ActivationParameters ap)
		{
			super(new ActivationParameters(ap.type, ap.pk-1));
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void featureNull()
	{
		assertFails(
				() -> newType(NullFeature.class),
				NullPointerException.class,
				NullFeature.class.getName() + "#nullFeature");
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class NullFeature extends Item
	{
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Feature nullFeature = null;

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void featureDuplicate()
	{
		assertFails(
				() -> newType(DuplicateFeature.class),
				IllegalArgumentException.class,
				DuplicateFeature.class.getName() + "#duplicate is same as #origin");
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, activationConstructor=NONE, indent=2, comments=false)
	private static class DuplicateFeature extends Item
	{
		static final Feature origin = new IntegerField();
		@SuppressWarnings("unused") // OK: test bad API usage
		static final Feature duplicate = origin;

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;
	}


	@Test void nonResolvingItemField()
	{
		assertFails(
				NonResolvingItemField.itemField::getValueType,
				IllegalArgumentException.class,
				"there is no type for class " + NullFeature.class.getName());
		final Type<NonResolvingItemField> nonResolvingItemField = newType(NonResolvingItemField.class);
		assertFails(
				NonResolvingItemField.itemField::getValueType,
				IllegalStateException.class,
				"item field " + NonResolvingItemField.itemField + " (" + NullFeature.class.getName() + ") does not belong to any model");
		assertFails(() ->
				new Model(nonResolvingItemField),
				IllegalArgumentException.class,
				"there is no type for class " + NullFeature.class.getName());
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class NonResolvingItemField extends Item
	{
		@WrapperIgnore
		static final ItemField<NullFeature> itemField = ItemField.create(NullFeature.class);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected NonResolvingItemField(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void beforeNewNotStatic()
	{
		assertFails(
				() -> new Model(BeforeNewNotStatic.TYPE),
				IllegalArgumentException.class,
				"method beforeNewCopeItem(SetValue[]) " +
				"in class " + BeforeNewNotStatic.class.getName() +
				" must be static");
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class BeforeNewNotStatic extends Item
	{
		@SuppressWarnings("MethodMayBeStatic")
		@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
		private SetValue<?>[] beforeNewCopeItem(final SetValue<?>[] setValues)
		{
			throw new AssertionError();
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BeforeNewNotStatic> TYPE = com.exedio.cope.TypesBound.newType(BeforeNewNotStatic.class);

		@com.exedio.cope.instrument.Generated
		protected BeforeNewNotStatic(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@Test void beforeNewWrongReturn()
	{
		assertFails(
				() -> new Model(BeforeNewWrongReturn.TYPE),
				IllegalArgumentException.class,
				"method beforeNewCopeItem(SetValue[]) " +
				"in class " + BeforeNewWrongReturn.class.getName() +
				" must return SetValue[], " +
				"but returns java.lang.String");
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class BeforeNewWrongReturn extends Item
	{
		@SuppressFBWarnings("UPM_UNCALLED_PRIVATE_METHOD")
		private static String beforeNewCopeItem(final SetValue<?>[] setValues)
		{
			throw new AssertionError();
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<BeforeNewWrongReturn> TYPE = com.exedio.cope.TypesBound.newType(BeforeNewWrongReturn.class);

		@com.exedio.cope.instrument.Generated
		protected BeforeNewWrongReturn(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}


	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Test void uniqueConstraintOnInheritedFeature()
	{
		// initialize class, otherwise test fails if executed alone (without other tests in this test class):
		// IllegalArgumentException: there is no type for class com.exedio.cope.TypesBoundErrorTest$UniqueConstraintOnInheritedFeatureSuper
		@SuppressWarnings("unused")
		final Type<?> ignored = UniqueConstraintOnInheritedFeatureSuper.TYPE;

		assertFails(
				() -> newType(UniqueConstraintOnInheritedFeatureSub.class),
				IllegalArgumentException.class,
				"UniqueConstraint UniqueConstraintOnInheritedFeatureSub.superAndSub cannot include field UniqueConstraintOnInheritedFeatureSuper.superField");
	}
	@WrapperType(constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	@SuppressWarnings("UnnecessarilyQualifiedStaticallyImportedElement") // OK: instrumented code
	private static class UniqueConstraintOnInheritedFeatureSuper extends Item
	{
		@WrapperIgnore static final IntegerField superField=new IntegerField();

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<UniqueConstraintOnInheritedFeatureSuper> TYPE = com.exedio.cope.TypesBound.newType(UniqueConstraintOnInheritedFeatureSuper.class);

		@com.exedio.cope.instrument.Generated
		protected UniqueConstraintOnInheritedFeatureSuper(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
	@WrapperType(type=NONE, constructor=NONE, genericConstructor=NONE, indent=2, comments=false)
	private static class UniqueConstraintOnInheritedFeatureSub extends UniqueConstraintOnInheritedFeatureSuper
	{
		@WrapperIgnore private static final IntegerField subField=new IntegerField();

		@SuppressWarnings("unused")
		@WrapperIgnore private static final UniqueConstraint superAndSub=UniqueConstraint.create(superField, subField);

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		protected UniqueConstraintOnInheritedFeatureSub(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

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

import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.pattern.Composite.getTemplateName;
import static com.exedio.cope.tojunit.Assert.assertEqualsUnmodifiable;
import static com.exedio.cope.tojunit.Assert.assertFails;
import static com.exedio.cope.tojunit.Assert.reserialize;
import static java.lang.annotation.ElementType.FIELD;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.IntegerField;
import com.exedio.cope.LongField;
import com.exedio.cope.SetValue;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.misc.Computed;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

public class CompositeMountTest
{
	@Test void testAbstractType()
	{
		assertSame(type, MyComposite.string4.getAbstractType());
		assertSame(type, MyComposite.intMax4.getAbstractType());

		final LongField negative = new LongField();
		try
		{
			negative.getAbstractType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Test void testType()
	{
		try
		{
			MyComposite.string4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"feature not mounted to a type, but to com.exedio.cope.pattern.CompositeType: " + valueName + "string4",
					e.getMessage());
		}
		try
		{
			MyComposite.intMax4.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"feature not mounted to a type, but to com.exedio.cope.pattern.CompositeType: " + valueName + "intMax4",
					e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getType();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Test void testName()
	{
		assertEquals("string4", MyComposite.string4.getName());
		assertEquals("intMax4", MyComposite.intMax4.getName());

		final LongField negative = new LongField();
		try
		{
			negative.getName();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Test void testTemplateName()
	{
		assertEquals("string4", getTemplateName(MyComposite.string4));
		assertEquals("intMax4", getTemplateName(MyComposite.intMax4));

		try
		{
			getTemplateName(null);
			fail();
		}
		catch(final NullPointerException e)
		{
			assertEquals("template", e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			getTemplateName(negative);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted to a composite: " + negative, e.getMessage());
		}
	}

	@Test void testID()
	{
		try
		{
			MyComposite.string4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"feature not mounted to a type, but to com.exedio.cope.pattern.CompositeType: " + valueName + "string4",
					e.getMessage());
		}
		try
		{
			MyComposite.intMax4.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals(
					"feature not mounted to a type, but to com.exedio.cope.pattern.CompositeType: " + valueName + "intMax4",
					e.getMessage());
		}

		final LongField negative = new LongField();
		try
		{
			negative.getID();
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Test void testToString()
	{
		assertEquals(valueName + "string4", MyComposite.string4.toString());
		assertEquals(valueName + "intMax4", MyComposite.intMax4.toString());

		final LongField negative = new LongField();
		assertTrue(negative.toString().startsWith(LongField.class.getName() + '@'));
	}

	@Test void testGetAnnotation()
	{
		assertEquals("stringAnno", MyComposite.string4.getAnnotation(Anno.class).value());
		assertEquals("intAnno", MyComposite.intMax4.getAnnotation(Anno.class).value());
		assertNull(MyComposite.string4.getAnnotation(Anno2.class));
		assertNull(MyComposite.intMax4.getAnnotation(Anno2.class));

		final LongField negative = new LongField();
		try
		{
			negative.getAnnotation(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Test void testIsAnnotation()
	{
		assertTrue (MyComposite.string4.isAnnotationPresent(Anno.class));
		assertTrue (MyComposite.intMax4.isAnnotationPresent(Anno.class));
		assertFalse(MyComposite.string4.isAnnotationPresent(Anno2.class));
		assertFalse(MyComposite.intMax4.isAnnotationPresent(Anno2.class));

		final LongField negative = new LongField();
		try
		{
			negative.isAnnotationPresent(Computed.class);
			fail();
		}
		catch(final IllegalStateException e)
		{
			assertEquals("feature not mounted", e.getMessage());
		}
	}

	@Target(FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno
	{
		String value();
	}

	@Target(FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	@interface Anno2
	{
	}

	@Test void testSerialization() throws IOException
	{
		assertSerializedSame(MyComposite.string4, 291);
		assertSerializedSame(MyComposite.intMax4, 291);

		final LongField negative = new LongField();
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try(ObjectOutputStream oos = new ObjectOutputStream(bos))
		{
			oos.writeObject(negative);
		}
		catch(final NotSerializableException e)
		{
			assertEquals("com.exedio.cope.LongField", e.getMessage());
		}
	}

	private static void assertSerializedSame(final Serializable value, final int expectedSize)
	{
		assertSame(value, reserialize(value, expectedSize));
	}


	@Test void testCompositeType()
	{
		assertEquals(MyComposite.class, type.getJavaClass());
		assertEquals(null, type.getSupertype());
		assertEqualsUnmodifiable(asList(), type.getSubtypes());
		assertEqualsUnmodifiable(asList(MyComposite.string4, MyComposite.intMax4), type.getDeclaredFeatures());
		assertEqualsUnmodifiable(asList(MyComposite.string4, MyComposite.intMax4), type.getFeatures());
		assertSame(MyComposite.intMax4, type.getDeclaredFeature("intMax4"));
		assertSame(MyComposite.intMax4, type.getFeature("intMax4"));
		assertSame(null, type.getDeclaredFeature(""));
		assertSame(null, type.getFeature(""));
		assertSame(null, type.getDeclaredFeature(null));
		assertSame(null, type.getFeature(null));
	}

	@Test void testNewValue()
	{
		final MyComposite value = type.newValue(
				SetValue.map(MyComposite.string4, "1234"),
				SetValue.map(MyComposite.intMax4, 4));
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
	}

	@Test void testNewValueConstraintViolation()
	{
		assertFails(
				() -> type.newValue(
						SetValue.map(MyComposite.string4, "12345"),
						SetValue.map(MyComposite.intMax4, 4)),
				StringLengthViolationException.class,
				"length violation, '12345' is too long for " + MyComposite.string4 + ", must be at most 4 characters, but was 5");
	}

	@Test void testNewValueNull()
	{
		final RuntimeException re = assertFails(
				() -> type.newValue((SetValue<?>[])null),
				RuntimeException.class,
				"java.lang.reflect.InvocationTargetException");
		final Throwable npe = re.getCause().getCause();
		assertEquals(NullPointerException.class, npe.getClass());
		assertEquals("Cannot read the array length because \"<local5>\" is null", npe.getMessage());
	}


	@WrapperType(constructor=NONE, indent=2, comments=false)
	private static final class MyComposite extends Composite
	{
		@Anno("stringAnno")
		static final StringField string4 = new StringField().lengthMax(4);
		@Anno("intAnno")
		static final IntegerField intMax4 = new IntegerField().max(4);

		@com.exedio.cope.instrument.Generated
		private MyComposite(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		java.lang.String getString4()
		{
			return get(MyComposite.string4);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setString4(@javax.annotation.Nonnull final java.lang.String string4)
				throws
					com.exedio.cope.MandatoryViolationException,
					com.exedio.cope.StringLengthViolationException
		{
			set(MyComposite.string4,string4);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		int getIntMax4()
		{
			return getMandatory(MyComposite.intMax4);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		void setIntMax4(final int intMax4)
				throws
					com.exedio.cope.IntegerRangeViolationException
		{
			set(MyComposite.intMax4,intMax4);
		}

		@com.exedio.cope.instrument.Generated
		@java.io.Serial
		private static final long serialVersionUID = 1l;
	}

	private static final String valueName = MyComposite.class.getName() + '#';

	/**
	 * Needed to instantiate {@link CompositeType}.
	 */
	static final CompositeType<MyComposite> type = CompositeType.newTypeOrExisting(MyComposite.class);
}

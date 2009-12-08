/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

import static java.lang.Boolean.valueOf;
import static java.lang.Double.valueOf;
import static java.lang.Integer.valueOf;
import static java.lang.Long.valueOf;

import com.exedio.cope.BooleanField;
import com.exedio.cope.DoubleField;
import com.exedio.cope.IntegerField;
import com.exedio.cope.IntegerRangeViolationException;
import com.exedio.cope.LongField;
import com.exedio.cope.MandatoryViolationException;
import com.exedio.cope.StringField;
import com.exedio.cope.StringLengthViolationException;
import com.exedio.cope.junit.CopeAssert;

public class CompositeTest extends CopeAssert
{
	public void testCheck()
	{
		try
		{
			new Value("12345", 5, 0l, 0.0, false);
			fail();
		}
		catch(StringLengthViolationException e)
		{
			// TODO make a nice exception message with feature name
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}
		
		try
		{
			new Value(null, 5, 0l, 0.0, false);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}
		
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEquals(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
		
		try
		{
			value.setString4("12345");
			fail();
		}
		catch(StringLengthViolationException e)
		{
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEquals(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
		
		try
		{
			value.setString4(null);
			fail();
		}
		catch(MandatoryViolationException e)
		{
			assertSame(Value.string4, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEquals(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
		
		try
		{
			value.setIntMax4(5);
			fail();
		}
		catch(IntegerRangeViolationException e)
		{
			assertSame(Value.intMax4, e.getFeature());
			assertSame(null, e.getItem());
		}
		assertEquals("1234", value.getString4());
		assertEquals(4, value.getIntMax4());
		assertEquals(5l, value.getLongField());
		assertEquals(6.6, value.getDoubleField());
		assertEquals(false, value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
	}
	
	public void testGetSet()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		
		assertEquals("1234", value.getString4());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEquals(6.6,    value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(null, value.getIntOptional());
		assertEquals(null, value.getLongOptional());
		assertEquals(null, value.getDoubleOptional());
		assertEquals(null, value.getBooleanOptional());
		
		assertEquals("1234",         value.get(Value.string4));
		assertEquals(valueOf(4),     value.get(Value.intMax4));
		assertEquals(valueOf(5l),    value.get(Value.longField));
		assertEquals(valueOf(6.6),   value.get(Value.doubleField));
		assertEquals(valueOf(false), value.get(Value.booleanField));
		assertEquals(4,     value.getMandatory(Value.intMax4));
		assertEquals(5l,    value.getMandatory(Value.longField));
		assertEquals(6.6,   value.getMandatory(Value.doubleField));
		assertEquals(false, value.getMandatory(Value.booleanField));
		assertEquals(null, value.get(Value.intOptional));
		assertEquals(null, value.get(Value.longOptional));
		assertEquals(null, value.get(Value.doubleOptional));
		assertEquals(null, value.get(Value.booleanOptional));
		try
		{
			value.getMandatory(Value.intOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.longOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.doubleOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.booleanOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		
		
		value.setIntOptional    (valueOf(44));
		value.setLongOptional   (valueOf(55l));
		value.setDoubleOptional (valueOf(66.0));
		value.setBooleanOptional(valueOf(true));
		
		assertEquals("1234", value.getString4());
		assertEquals(4,      value.getIntMax4());
		assertEquals(5l,     value.getLongField());
		assertEquals(6.6,    value.getDoubleField());
		assertEquals(false,  value.getBooleanField());
		assertEquals(valueOf(44),   value.getIntOptional());
		assertEquals(valueOf(55l),  value.getLongOptional());
		assertEquals(valueOf(66.0), value.getDoubleOptional());
		assertEquals(valueOf(true), value.getBooleanOptional());

		assertEquals("1234",         value.get(Value.string4));
		assertEquals(valueOf(4),     value.get(Value.intMax4));
		assertEquals(valueOf(5l),    value.get(Value.longField));
		assertEquals(valueOf(6.6),   value.get(Value.doubleField));
		assertEquals(valueOf(false), value.get(Value.booleanField));
		assertEquals(4,     value.getMandatory(Value.intMax4));
		assertEquals(5l,    value.getMandatory(Value.longField));
		assertEquals(6.6,   value.getMandatory(Value.doubleField));
		assertEquals(false, value.getMandatory(Value.booleanField));
		assertEquals(valueOf(44),   value.get(Value.intOptional));
		assertEquals(valueOf(55l),  value.get(Value.longOptional));
		assertEquals(valueOf(66.0), value.get(Value.doubleOptional));
		assertEquals(valueOf(true), value.get(Value.booleanOptional));
		try
		{
			value.getMandatory(Value.intOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.longOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.doubleOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
		try
		{
			value.getMandatory(Value.booleanOptional);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			assertEquals("member is not mandatory", e.getMessage()); // TODO message with member name
		}
	}
	
	public void testWrong()
	{
		final Value value = new Value("1234", 4, 5l, 6.6, false);
		
		// TODO better exceptions
		try
		{
			value.get(ValueX.stringField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.get(ValueX.intField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.get(ValueX.longField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.get(ValueX.doubleField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.get(ValueX.booleanField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		
		// getMandatory
		try
		{
			value.getMandatory(ValueX.intField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.getMandatory(ValueX.longField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.getMandatory(ValueX.doubleField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
		try
		{
			value.getMandatory(ValueX.booleanField);
			fail();
		}
		catch(NullPointerException e)
		{
			assertEquals(null, e.getMessage());
		}
	}
	
	static final class Value extends Composite
	{
		public static final StringField string4 = new StringField().lengthMax(4);
		public static final IntegerField intMax4 = new IntegerField().max(4);
		public static final LongField longField = new LongField();
		public static final DoubleField doubleField = new DoubleField();
		public static final BooleanField booleanField = new BooleanField();
		
		public static final IntegerField intOptional = new IntegerField().optional();
		public static final LongField longOptional = new LongField().optional();
		public static final DoubleField doubleOptional = new DoubleField().optional();
		public static final BooleanField booleanOptional = new BooleanField().optional();
		
	/**

	 **
	 * Creates a new Value with all the fields initially needed.
	 * @param string4 the initial value for field {@link #string4}.
	 * @param intMax4 the initial value for field {@link #intMax4}.
	 * @param longField the initial value for field {@link #longField}.
	 * @param doubleField the initial value for field {@link #doubleField}.
	 * @param booleanField the initial value for field {@link #booleanField}.
	 * @throws com.exedio.cope.IntegerRangeViolationException if intMax4 violates its range constraint.
	 * @throws com.exedio.cope.MandatoryViolationException if string4 is null.
	 * @throws com.exedio.cope.StringLengthViolationException if string4 violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	Value(
				final java.lang.String string4,
				final int intMax4,
				final long longField,
				final double doubleField,
				final boolean booleanField)
			throws
				com.exedio.cope.IntegerRangeViolationException,
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue[]{
			Value.string4.map(string4),
			Value.intMax4.map(intMax4),
			Value.longField.map(longField),
			Value.doubleField.map(doubleField),
			Value.booleanField.map(booleanField),
		});
	}/**

	 **
	 * Creates a new Value and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private Value(final com.exedio.cope.SetValue... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #string4}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final java.lang.String getString4()
	{
		return get(Value.string4);
	}/**

	 **
	 * Sets a new value for {@link #string4}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setString4(final java.lang.String string4)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(Value.string4,string4);
	}/**

	 **
	 * Returns the value of {@link #intMax4}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final int getIntMax4()
	{
		return getMandatory(Value.intMax4);
	}/**

	 **
	 * Sets a new value for {@link #intMax4}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setIntMax4(final int intMax4)
			throws
				com.exedio.cope.IntegerRangeViolationException
	{
		set(Value.intMax4,intMax4);
	}/**

	 **
	 * Returns the value of {@link #longField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final long getLongField()
	{
		return getMandatory(Value.longField);
	}/**

	 **
	 * Sets a new value for {@link #longField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setLongField(final long longField)
	{
		set(Value.longField,longField);
	}/**

	 **
	 * Returns the value of {@link #doubleField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final double getDoubleField()
	{
		return getMandatory(Value.doubleField);
	}/**

	 **
	 * Sets a new value for {@link #doubleField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setDoubleField(final double doubleField)
	{
		set(Value.doubleField,doubleField);
	}/**

	 **
	 * Returns the value of {@link #booleanField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the field.
	 */
	public final boolean getBooleanField()
	{
		return getMandatory(Value.booleanField);
	}/**

	 **
	 * Sets a new value for {@link #booleanField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setBooleanField(final boolean booleanField)
	{
		set(Value.booleanField,booleanField);
	}/**

	 **
	 * Returns the value of {@link #intOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final java.lang.Integer getIntOptional()
	{
		return get(Value.intOptional);
	}/**

	 **
	 * Sets a new value for {@link #intOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setIntOptional(final java.lang.Integer intOptional)
	{
		set(Value.intOptional,intOptional);
	}/**

	 **
	 * Returns the value of {@link #longOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final java.lang.Long getLongOptional()
	{
		return get(Value.longOptional);
	}/**

	 **
	 * Sets a new value for {@link #longOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setLongOptional(final java.lang.Long longOptional)
	{
		set(Value.longOptional,longOptional);
	}/**

	 **
	 * Returns the value of {@link #doubleOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final java.lang.Double getDoubleOptional()
	{
		return get(Value.doubleOptional);
	}/**

	 **
	 * Sets a new value for {@link #doubleOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setDoubleOptional(final java.lang.Double doubleOptional)
	{
		set(Value.doubleOptional,doubleOptional);
	}/**

	 **
	 * Returns the value of {@link #booleanOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the field.
	 */
	public final java.lang.Boolean getBooleanOptional()
	{
		return get(Value.booleanOptional);
	}/**

	 **
	 * Sets a new value for {@link #booleanOptional}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setBooleanOptional(final java.lang.Boolean booleanOptional)
	{
		set(Value.booleanOptional,booleanOptional);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;}
	
	static final class ValueX extends Composite
	{
		public static final  StringField  stringField = new  StringField();
		public static final IntegerField     intField = new IntegerField();
		public static final    LongField    longField = new    LongField();
		public static final  DoubleField  doubleField = new  DoubleField();
		public static final BooleanField booleanField = new BooleanField();
	/**

	 **
	 * Creates a new ValueX with all the fields initially needed.
	 * @param stringField the initial value for field {@link #stringField}.
	 * @param intField the initial value for field {@link #intField}.
	 * @param longField the initial value for field {@link #longField}.
	 * @param doubleField the initial value for field {@link #doubleField}.
	 * @param booleanField the initial value for field {@link #booleanField}.
	 * @throws com.exedio.cope.MandatoryViolationException if stringField is null.
	 * @throws com.exedio.cope.StringLengthViolationException if stringField violates its length constraint.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tags <tt>@cope.constructor public|package|protected|private|none</tt> in the class comment and <tt>@cope.initial</tt> in the comment of fields.
	 */
	ValueX(
				final java.lang.String stringField,
				final int intField,
				final long longField,
				final double doubleField,
				final boolean booleanField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue[]{
			ValueX.stringField.map(stringField),
			ValueX.intField.map(intField),
			ValueX.longField.map(longField),
			ValueX.doubleField.map(doubleField),
			ValueX.booleanField.map(booleanField),
		});
	}/**

	 **
	 * Creates a new ValueX and sets the given fields initially.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.generic.constructor public|package|protected|private|none</tt> in the class comment.
	 */
	private ValueX(final com.exedio.cope.SetValue... setValues)
	{
		super(setValues);
	}/**

	 **
	 * Returns the value of {@link #stringField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final java.lang.String getStringField()
	{
		return get(ValueX.stringField);
	}/**

	 **
	 * Sets a new value for {@link #stringField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setStringField(final java.lang.String stringField)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		set(ValueX.stringField,stringField);
	}/**

	 **
	 * Returns the value of {@link #intField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final int getIntField()
	{
		return getMandatory(ValueX.intField);
	}/**

	 **
	 * Sets a new value for {@link #intField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setIntField(final int intField)
	{
		set(ValueX.intField,intField);
	}/**

	 **
	 * Returns the value of {@link #longField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final long getLongField()
	{
		return getMandatory(ValueX.longField);
	}/**

	 **
	 * Sets a new value for {@link #longField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setLongField(final long longField)
	{
		set(ValueX.longField,longField);
	}/**

	 **
	 * Returns the value of {@link #doubleField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final double getDoubleField()
	{
		return getMandatory(ValueX.doubleField);
	}/**

	 **
	 * Sets a new value for {@link #doubleField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setDoubleField(final double doubleField)
	{
		set(ValueX.doubleField,doubleField);
	}/**

	 **
	 * Returns the value of {@link #booleanField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.get public|package|protected|private|none|non-final|boolean-as-is</tt> in the comment of the field.
	 */
	public final boolean getBooleanField()
	{
		return getMandatory(ValueX.booleanField);
	}/**

	 **
	 * Sets a new value for {@link #booleanField}.
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 *       It can be customized with the tag <tt>@cope.set public|package|protected|private|none|non-final</tt> in the comment of the field.
	 */
	public final void setBooleanField(final boolean booleanField)
	{
		set(ValueX.booleanField,booleanField);
	}/**

	 **
	 * @cope.generated This feature has been generated by the cope instrumentor and will be overwritten by the build process.
	 */
	private static final long serialVersionUID = 1l;}

}

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

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.DataField.ArrayValue;
import com.exedio.cope.DataField.Value;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperInitial;
import com.exedio.cope.instrument.WrapperType;
import com.exedio.cope.junit.AssertionErrorChangeHook;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

public class ChangeHookDataBeforeTest extends TestWithEnvironment
{
	@Test void testDataOnlyCreate()
	{
		final MyItem i = new MyItem(encode("fieldVal"));
		assertAll(
				() -> assertEvents("Hook#new(field=fieldVal)"),
				() -> assertEquals("fieldVal / Hook#new", i.getField()),
				() -> assertEquals(null, i.getOther()));
	}

	@Test void testDataOnlyCreateDrop()
	{
		final MyItem i = new MyItem(encode("fieldVal(DROP)"));
		assertAll(
				() -> assertEvents("Hook#new(field=fieldVal(DROP))"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals(null, i.getOther()));
	}

	@Test void testDataOnlyCreateOther()
	{
		final MyItem i = new MyItem(encode("fieldVal(OTHER)"));
		assertAll(
				() -> assertEvents("Hook#new(field=fieldVal(OTHER))"),
				() -> assertEquals("fieldVal(OTHER) / Hook#new", i.getField()),
				() -> assertEquals("(OTHERnew)", i.getOther()));
	}

	@Test void testDataOnlyCreateOtherDrop()
	{
		final MyItem i = new MyItem(encode("fieldVal(OTHER)(DROP)"));
		assertAll(
				() -> assertEvents("Hook#new(field=fieldVal(OTHER)(DROP))"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals("(OTHERnew)", i.getOther()));
	}

	@Test void testDataOnlyMset()
	{
		final MyItem i = newMyItem();
		i.setFieldMulti(encode("fieldVal"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal)"),
				() -> assertEquals("fieldVal / Hook#set", i.getField()),
				() -> assertEquals(null, i.getOther()));
	}

	@Test void testDataOnlyMsetDrop()
	{
		final MyItem i = newMyItem();
		i.setFieldMulti(encode("fieldVal(DROP)"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal(DROP))"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals(null, i.getOther()));
	}

	@Test void testDataOnlyMsetOther()
	{
		final MyItem i = newMyItem();
		i.setFieldMulti(encode("fieldVal(OTHER)"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal(OTHER))"),
				() -> assertEquals("fieldVal(OTHER) / Hook#set", i.getField()),
				() -> assertEquals("(OTHERset)", i.getOther()));
	}

	@Test void testDataOnlyMsetOtherDrop()
	{
		final MyItem i = newMyItem();
		i.setFieldMulti(encode("fieldVal(OTHER)(DROP)"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal(OTHER)(DROP))"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals("(OTHERset)", i.getOther()));
	}

	@Test void testDataOnlySet()
	{
		final MyItem i = newMyItem();
		i.setField(encode("fieldVal"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal)"),
				() -> assertEquals("fieldVal / Hook#set", i.getField()),
				() -> assertEquals(null, i.getOther()));
	}

	@Test void testDataOnlySetDrop()
	{
		final MyItem i = newMyItem();
		i.setField(encode("fieldVal(DROP)"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal(DROP))"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals(null, i.getOther()));
	}

	@Test void testDataOnlySetOther()
	{
		final MyItem i = newMyItem();
		i.setField(encode("fieldVal(OTHER)"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal(OTHER))"),
				() -> assertEquals("fieldVal(OTHER) / Hook#set", i.getField()),
				() -> assertEquals("(OTHERset)", i.getOther()));
	}

	@Test void testDataOnlySetOtherDrop()
	{
		final MyItem i = newMyItem();
		i.setField(encode("fieldVal(OTHER)(DROP)"));
		assertAll(
				() -> assertEvents("Hook#set("+i+",field=fieldVal(OTHER)(DROP))"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals("(OTHERset)", i.getOther()));
	}

	@Test void testOtherOnlyCreate()
	{
		final MyItem i = new MyItem("otherVal");
		assertAll(
				() -> assertEvents("Hook#new(other=otherVal)"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals("otherVal / Hook#new", i.getOther()));
	}

	@Test void testOtherOnlyCreateField()
	{
		final MyItem i = new MyItem("otherVal(FIELD)");
		assertAll(
				() -> assertEvents("Hook#new(other=otherVal(FIELD))"),
				() -> assertEquals("(FIELDnew)", i.getField()),
				() -> assertEquals("otherVal(FIELD) / Hook#new", i.getOther()));
	}

	@Test void testOtherOnlySet()
	{
		final MyItem i = newMyItem();
		i.setOther("otherVal");
		assertAll(
				() -> assertEvents("Hook#set("+i+",other=otherVal)"),
				() -> assertEquals(null, i.getField()),
				() -> assertEquals("otherVal / Hook#set", i.getOther()));
	}

	@Test void testOtherOnlySetField()
	{
		final MyItem i = newMyItem();
		i.setOther("otherVal(FIELD)");
		assertAll(
				() -> assertEvents("Hook#set("+i+",other=otherVal(FIELD))"),
				() -> assertEquals("(FIELDset)", i.getField()),
				() -> assertEquals("otherVal(FIELD) / Hook#set", i.getOther()));
	}


	private static MyItem newMyItem()
	{
		final MyItem result = new MyItem();
		assertEvents("Hook#new()");
		assertEquals(null, result.getField());
		assertEquals(null, result.getOther());
		return result;
	}

	@WrapperType(indent=2, comments=false)
	static final class MyItem extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		@Wrapper(wrap="getArray", visibility=DEFAULT)
		@Wrapper(wrap="set", parameters=Value.class, visibility=DEFAULT)
		@WrapperInitial
		static final DataField field = new DataField().optional().lengthMax(500);

		@WrapperInitial
		static final StringField other = new StringField().optional();

		String getField()
		{
			final byte[] array = getFieldArray();
			return array!=null ? new String(array, US_ASCII) : null;
		}

		void setFieldMulti(final Value field)
		{
			set(MyItem.field.map(field));
		}

		MyItem()
		{
			this(new SetValue<?>[0]);
		}

		MyItem(final Value field)
		{
			this(MyItem.field.map(field));
		}

		MyItem(final String other)
		{
			this(MyItem.other.map(other));
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		MyItem(
					@javax.annotation.Nullable final com.exedio.cope.DataField.Value field,
					@javax.annotation.Nullable final java.lang.String other)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				MyItem.field.map(field),
				MyItem.other.map(other),
			});
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		byte[] getFieldArray()
		{
			return MyItem.field.getArray(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setField(@javax.annotation.Nullable final com.exedio.cope.DataField.Value field)
		{
			MyItem.field.set(this,field);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		@javax.annotation.Nullable
		java.lang.String getOther()
		{
			return MyItem.other.get(this);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		void setOther(@javax.annotation.Nullable final java.lang.String other)
				throws
					com.exedio.cope.StringLengthViolationException
		{
			MyItem.other.set(this,other);
		}

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private static final long serialVersionUID = 1l;

		@javax.annotation.Generated("com.exedio.cope.instrument")
		static final com.exedio.cope.Type<MyItem> TYPE = com.exedio.cope.TypesBound.newType(MyItem.class);

		@javax.annotation.Generated("com.exedio.cope.instrument")
		private MyItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	static final class MyHook extends AssertionErrorChangeHook
	{
		private final Type<?> type;
		private final DataField field;
		private final StringField other;

		MyHook(final Model model)
		{
			type = model.getType("MyItem");
			assertNotNull(type);
			field = (DataField)type.getFeature("field");
			assertNotNull(field);
			other = (StringField)type.getFeature("other");
			assertNotNull(other);
		}

		@Override public SetValue<?>[] beforeNew(final Type<?> type, final SetValue<?>[] sv)
		{
			assertSame(this.type, type);
			return before("new", null, sv);
		}

		@Override public SetValue<?>[] beforeSet(final Item item, final SetValue<?>[] sv)
		{
			return before("set", item, sv);
		}

		private SetValue<?>[] before(final String operation, final Item item, final SetValue<?>[] sv)
		{
			final StringBuilder bf = new StringBuilder("Hook#" + operation + "(");
			final ArrayList<SetValue<?>> result = new ArrayList<>();
			if(item!=null)
				bf.append(item);
			for(final SetValue<?> s : sv)
			{
				final Field<?> settable = (Field<?>)s.settable;
				final String value;
				if(bf.charAt(bf.length()-1)!='(')
					bf.append(",");
				bf.append(settable.getName() + '=');
				if(settable==field)
				{
					value = decode(s.value);
					if(!value.contains("(DROP)"))
						result.add(field.map(encode(value + " / Hook#" + operation)));
				}
				else if(settable==other)
				{
					value = (String)s.value;
					if(!value.contains("(DROP)"))
						result.add(other.map(value + " / Hook#" + operation));
				}
				else
				{
					throw new AssertionFailedError(settable.getID());
				}

				bf.append(value);
				if(value.contains("(FIELD)"))
					result.add(field.map(encode("(FIELD" + operation + ")")));
				if(value.contains("(OTHER)"))
					result.add(other.map("(OTHER" + operation + ")"));
			}
			bf.append(")");
			addEvent(bf.toString());
			return result.toArray(new SetValue<?>[0]);
		}

		@Override public void afterNew    (final Item item) {}
		@Override public void beforeDelete(final Item item) {}
	}

	private static final ArrayList<String> events = new ArrayList<>();

	@BeforeEach
	final void cleanEvents()
	{
		events.clear();
	}

	private static void assertEvents(final String... logs)
	{
		//noinspection MisorderedAssertEqualsArguments
		assertEquals(Arrays.asList(logs), events);
		events.clear();
	}

	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private static void addEvent(final String event)
	{
		assertNotNull(event);
		events.add(event);
	}

	private static final Model MODEL = Model.builder().
			add(MyItem.TYPE).
			changeHooks(MyHook::new).
			build();

	public ChangeHookDataBeforeTest()
	{
		super(MODEL);
	}

	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private static String decode(final Object v)
	{
		return new String(((ArrayValue)v).array, US_ASCII);
	}

	private static ArrayValue encode(final String s)
	{
		return (ArrayValue)DataField.toValue(s.getBytes(US_ASCII));
	}
}

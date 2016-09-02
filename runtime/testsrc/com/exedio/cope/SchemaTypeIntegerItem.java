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

import com.exedio.cope.instrument.WrapperType;

@WrapperType(constructor=NONE, genericConstructor=NONE)
public final class SchemaTypeIntegerItem extends Item
{
	static final LongField byte1  = new LongField().range(-128, 127);
	static final LongField byte1l = new LongField().range(-129, 127);
	static final LongField byte1u = new LongField().range(-128, 128);

	static final LongField byte2  = new LongField().range(-32768, 32767);
	static final LongField byte2l = new LongField().range(-32769, 32767);
	static final LongField byte2u = new LongField().range(-32768, 32768);

	static final LongField byte3  = new LongField().range(-8388608, 8388607);
	static final LongField byte3l = new LongField().range(-8388609, 8388607);
	static final LongField byte3u = new LongField().range(-8388608, 8388608);

	static final LongField byte4  = new LongField().range(-2147483648l, 2147483647 );
	static final LongField byte4l = new LongField().range(-2147483649l, 2147483647 );
	static final LongField byte4u = new LongField().range(-2147483648l, 2147483648l);

	static final LongField byte8  = new LongField();


	static final LongField decimal1  = new LongField().range(- 9,  9);
	static final LongField decimal1l = new LongField().range(-10,  9);
	static final LongField decimal1u = new LongField().range(- 9, 10);

	static final LongField decimal2  = new LongField().range(- 99,  99);
	static final LongField decimal2l = new LongField().range(-100,  99);
	static final LongField decimal2u = new LongField().range(- 99, 100);

	static final LongField decimal3  = new LongField().range(- 999,  999);
	static final LongField decimal3l = new LongField().range(-1000,  999);
	static final LongField decimal3u = new LongField().range(- 999, 1000);

	static final LongField decimal4  = new LongField().range(- 9999,  9999);
	static final LongField decimal4l = new LongField().range(-10000,  9999);
	static final LongField decimal4u = new LongField().range(- 9999, 10000);

	static final LongField decimal11  = new LongField().range(- 99999999999l,  99999999999l);
	static final LongField decimal11l = new LongField().range(-100000000000l,  99999999999l);
	static final LongField decimal11u = new LongField().range(- 99999999999l, 100000000000l);

	/**
	 * Returns the value of {@link #byte1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte1()
	{
		return SchemaTypeIntegerItem.byte1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte1(final long byte1)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte1.set(this,byte1);
	}

	/**
	 * Returns the value of {@link #byte1l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte1l()
	{
		return SchemaTypeIntegerItem.byte1l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte1l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte1l(final long byte1l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte1l.set(this,byte1l);
	}

	/**
	 * Returns the value of {@link #byte1u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte1u()
	{
		return SchemaTypeIntegerItem.byte1u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte1u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte1u(final long byte1u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte1u.set(this,byte1u);
	}

	/**
	 * Returns the value of {@link #byte2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte2()
	{
		return SchemaTypeIntegerItem.byte2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte2(final long byte2)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte2.set(this,byte2);
	}

	/**
	 * Returns the value of {@link #byte2l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte2l()
	{
		return SchemaTypeIntegerItem.byte2l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte2l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte2l(final long byte2l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte2l.set(this,byte2l);
	}

	/**
	 * Returns the value of {@link #byte2u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte2u()
	{
		return SchemaTypeIntegerItem.byte2u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte2u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte2u(final long byte2u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte2u.set(this,byte2u);
	}

	/**
	 * Returns the value of {@link #byte3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte3()
	{
		return SchemaTypeIntegerItem.byte3.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte3(final long byte3)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte3.set(this,byte3);
	}

	/**
	 * Returns the value of {@link #byte3l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte3l()
	{
		return SchemaTypeIntegerItem.byte3l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte3l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte3l(final long byte3l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte3l.set(this,byte3l);
	}

	/**
	 * Returns the value of {@link #byte3u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte3u()
	{
		return SchemaTypeIntegerItem.byte3u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte3u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte3u(final long byte3u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte3u.set(this,byte3u);
	}

	/**
	 * Returns the value of {@link #byte4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte4()
	{
		return SchemaTypeIntegerItem.byte4.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte4(final long byte4)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte4.set(this,byte4);
	}

	/**
	 * Returns the value of {@link #byte4l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte4l()
	{
		return SchemaTypeIntegerItem.byte4l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte4l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte4l(final long byte4l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte4l.set(this,byte4l);
	}

	/**
	 * Returns the value of {@link #byte4u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte4u()
	{
		return SchemaTypeIntegerItem.byte4u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte4u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte4u(final long byte4u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte4u.set(this,byte4u);
	}

	/**
	 * Returns the value of {@link #byte8}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getByte8()
	{
		return SchemaTypeIntegerItem.byte8.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte8}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setByte8(final long byte8)
	{
		SchemaTypeIntegerItem.byte8.set(this,byte8);
	}

	/**
	 * Returns the value of {@link #decimal1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal1()
	{
		return SchemaTypeIntegerItem.decimal1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal1}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal1(final long decimal1)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal1.set(this,decimal1);
	}

	/**
	 * Returns the value of {@link #decimal1l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal1l()
	{
		return SchemaTypeIntegerItem.decimal1l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal1l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal1l(final long decimal1l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal1l.set(this,decimal1l);
	}

	/**
	 * Returns the value of {@link #decimal1u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal1u()
	{
		return SchemaTypeIntegerItem.decimal1u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal1u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal1u(final long decimal1u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal1u.set(this,decimal1u);
	}

	/**
	 * Returns the value of {@link #decimal2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal2()
	{
		return SchemaTypeIntegerItem.decimal2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal2}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal2(final long decimal2)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal2.set(this,decimal2);
	}

	/**
	 * Returns the value of {@link #decimal2l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal2l()
	{
		return SchemaTypeIntegerItem.decimal2l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal2l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal2l(final long decimal2l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal2l.set(this,decimal2l);
	}

	/**
	 * Returns the value of {@link #decimal2u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal2u()
	{
		return SchemaTypeIntegerItem.decimal2u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal2u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal2u(final long decimal2u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal2u.set(this,decimal2u);
	}

	/**
	 * Returns the value of {@link #decimal3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal3()
	{
		return SchemaTypeIntegerItem.decimal3.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal3}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal3(final long decimal3)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal3.set(this,decimal3);
	}

	/**
	 * Returns the value of {@link #decimal3l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal3l()
	{
		return SchemaTypeIntegerItem.decimal3l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal3l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal3l(final long decimal3l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal3l.set(this,decimal3l);
	}

	/**
	 * Returns the value of {@link #decimal3u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal3u()
	{
		return SchemaTypeIntegerItem.decimal3u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal3u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal3u(final long decimal3u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal3u.set(this,decimal3u);
	}

	/**
	 * Returns the value of {@link #decimal4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal4()
	{
		return SchemaTypeIntegerItem.decimal4.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal4}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal4(final long decimal4)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal4.set(this,decimal4);
	}

	/**
	 * Returns the value of {@link #decimal4l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal4l()
	{
		return SchemaTypeIntegerItem.decimal4l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal4l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal4l(final long decimal4l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal4l.set(this,decimal4l);
	}

	/**
	 * Returns the value of {@link #decimal4u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal4u()
	{
		return SchemaTypeIntegerItem.decimal4u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal4u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal4u(final long decimal4u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal4u.set(this,decimal4u);
	}

	/**
	 * Returns the value of {@link #decimal11}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal11()
	{
		return SchemaTypeIntegerItem.decimal11.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal11}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal11(final long decimal11)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal11.set(this,decimal11);
	}

	/**
	 * Returns the value of {@link #decimal11l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal11l()
	{
		return SchemaTypeIntegerItem.decimal11l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal11l}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal11l(final long decimal11l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal11l.set(this,decimal11l);
	}

	/**
	 * Returns the value of {@link #decimal11u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	final long getDecimal11u()
	{
		return SchemaTypeIntegerItem.decimal11u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #decimal11u}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	final void setDecimal11u(final long decimal11u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.decimal11u.set(this,decimal11u);
	}

	/**
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for schemaTypeIntegerItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SchemaTypeIntegerItem> TYPE = com.exedio.cope.TypesBound.newType(SchemaTypeIntegerItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	@SuppressWarnings("unused") private SchemaTypeIntegerItem(final com.exedio.cope.ActivationParameters ap){super(ap);
}}

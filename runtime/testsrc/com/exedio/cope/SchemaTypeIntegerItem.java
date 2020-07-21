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

	/**
	 * Returns the value of {@link #byte1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte1()
	{
		return SchemaTypeIntegerItem.byte1.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte1}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte1(final long byte1)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte1.set(this,byte1);
	}

	/**
	 * Returns the value of {@link #byte1l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte1l()
	{
		return SchemaTypeIntegerItem.byte1l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte1l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte1l(final long byte1l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte1l.set(this,byte1l);
	}

	/**
	 * Returns the value of {@link #byte1u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte1u()
	{
		return SchemaTypeIntegerItem.byte1u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte1u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte1u(final long byte1u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte1u.set(this,byte1u);
	}

	/**
	 * Returns the value of {@link #byte2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte2()
	{
		return SchemaTypeIntegerItem.byte2.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte2}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte2(final long byte2)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte2.set(this,byte2);
	}

	/**
	 * Returns the value of {@link #byte2l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte2l()
	{
		return SchemaTypeIntegerItem.byte2l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte2l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte2l(final long byte2l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte2l.set(this,byte2l);
	}

	/**
	 * Returns the value of {@link #byte2u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte2u()
	{
		return SchemaTypeIntegerItem.byte2u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte2u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte2u(final long byte2u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte2u.set(this,byte2u);
	}

	/**
	 * Returns the value of {@link #byte3}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte3()
	{
		return SchemaTypeIntegerItem.byte3.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte3}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte3(final long byte3)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte3.set(this,byte3);
	}

	/**
	 * Returns the value of {@link #byte3l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte3l()
	{
		return SchemaTypeIntegerItem.byte3l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte3l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte3l(final long byte3l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte3l.set(this,byte3l);
	}

	/**
	 * Returns the value of {@link #byte3u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte3u()
	{
		return SchemaTypeIntegerItem.byte3u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte3u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte3u(final long byte3u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte3u.set(this,byte3u);
	}

	/**
	 * Returns the value of {@link #byte4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte4()
	{
		return SchemaTypeIntegerItem.byte4.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte4}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte4(final long byte4)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte4.set(this,byte4);
	}

	/**
	 * Returns the value of {@link #byte4l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte4l()
	{
		return SchemaTypeIntegerItem.byte4l.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte4l}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte4l(final long byte4l)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte4l.set(this,byte4l);
	}

	/**
	 * Returns the value of {@link #byte4u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte4u()
	{
		return SchemaTypeIntegerItem.byte4u.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte4u}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte4u(final long byte4u)
			throws
				com.exedio.cope.LongRangeViolationException
	{
		SchemaTypeIntegerItem.byte4u.set(this,byte4u);
	}

	/**
	 * Returns the value of {@link #byte8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	long getByte8()
	{
		return SchemaTypeIntegerItem.byte8.getMandatory(this);
	}

	/**
	 * Sets a new value for {@link #byte8}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void setByte8(final long byte8)
	{
		SchemaTypeIntegerItem.byte8.set(this,byte8);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for schemaTypeIntegerItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<SchemaTypeIntegerItem> TYPE = com.exedio.cope.TypesBound.newType(SchemaTypeIntegerItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private SchemaTypeIntegerItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

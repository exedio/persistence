package com.exedio.cope.instrument.parameters;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;

/** item without initial fields */
@SuppressWarnings({"UnnecessarilyQualifiedStaticUsage", "ConstantForZeroLengthArrayAllocation"})
class EmptyConstructorItem extends Item
{
	static final StringField field = new StringField().optional();

	/**
	 * Creates a new EmptyConstructorItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"constructor-first","constructor-second"})
	EmptyConstructorItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new EmptyConstructorItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(genericConstructor=...)
	protected EmptyConstructorItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #field}.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings("wrapper-single")
	@javax.annotation.Nullable
	final java.lang.String getField()
	{
		return EmptyConstructorItem.field.get(this);
	}

	/**
	 * Sets a new value for {@link #field}.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings("wrapper-single")
	final void setField(@javax.annotation.Nullable final java.lang.String field)
			throws
				com.exedio.cope.StringLengthViolationException
	{
		EmptyConstructorItem.field.set(this,field);
	}

	@com.exedio.cope.instrument.GeneratedClass
	private static final long serialVersionUID = 1;

	/**
	 * The persistent type information for emptyConstructorItem.
	 */
	@com.exedio.cope.instrument.GeneratedClass // customize with @WrapperType(type=...)
	@java.lang.SuppressWarnings("type-single")
	static final com.exedio.cope.Type<EmptyConstructorItem> TYPE = com.exedio.cope.TypesBound.newType(EmptyConstructorItem.class,EmptyConstructorItem::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.GeneratedClass
	protected EmptyConstructorItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

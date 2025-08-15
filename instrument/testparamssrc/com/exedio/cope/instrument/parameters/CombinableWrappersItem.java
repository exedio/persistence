package com.exedio.cope.instrument.parameters;

import static com.exedio.cope.instrument.Visibility.*;

import com.exedio.cope.Item;
import com.exedio.cope.StringField;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;

@SuppressWarnings("UnnecessarilyQualifiedStaticUsage")
@WrapperType(constructor=NONE, genericConstructor=NONE, comments=false)
class CombinableWrappersItem extends Item
{
	@Wrapper(wrap="get", visibility=DEFAULT) // test case for not setting <instrument warnWrapperCombinable="true"/>
	@Wrapper(wrap="set", visibility=DEFAULT) // test case for not setting <instrument warnWrapperCombinable="true"/>
	public static final StringField string = new StringField();

	@com.exedio.cope.instrument.GeneratedClass
	@java.lang.SuppressWarnings("wrapper-single")
	@javax.annotation.Nonnull
	public final java.lang.String getString()
	{
		return CombinableWrappersItem.string.get(this);
	}

	@com.exedio.cope.instrument.GeneratedClass
	@java.lang.SuppressWarnings("wrapper-single")
	public final void setString(@javax.annotation.Nonnull final java.lang.String string)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		CombinableWrappersItem.string.set(this,string);
	}

	@com.exedio.cope.instrument.GeneratedClass
	@java.io.Serial
	private static final long serialVersionUID = 1;

	@com.exedio.cope.instrument.GeneratedClass
	@java.lang.SuppressWarnings("type-single")
	static final com.exedio.cope.Type<CombinableWrappersItem> TYPE = com.exedio.cope.TypesBound.newType(CombinableWrappersItem.class,CombinableWrappersItem::new);

	@com.exedio.cope.instrument.GeneratedClass
	protected CombinableWrappersItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

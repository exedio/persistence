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

package com.exedio.cope.instrument.testmodel;

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.PRIVATE;
import static com.exedio.cope.instrument.Visibility.PROTECTED;
import static com.exedio.cope.instrument.Visibility.PUBLIC;

import com.exedio.cope.Item;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.testfeature.WrapperParametersFeature;
import com.exedio.cope.instrument.testfeature.WrapperParametersFeatureGeneric;

class WrapperParametersItem extends Item
{
	@Wrapper(wrap="param", parameters={}, internal=true)
	@Wrapper(wrap="param", parameters=WrapperParametersFeature.class, visibility=PUBLIC)
	@Wrapper(wrap="param", parameters=Integer.class, visibility=DEFAULT)
	@Wrapper(wrap="param", parameters=int.class, visibility=PROTECTED)
	@Wrapper(wrap="param", parameters=WrapperParametersFeature.SomeEnum.class, visibility=PRIVATE)
	@Wrapper(wrap="param", parameters=byte[].class, internal=true, visibility=PUBLIC)
	@Wrapper(wrap="param", parameters=Item[][].class, internal=true, visibility=PROTECTED)
	@Wrapper(wrap="param", parameters=float.class, internal=true, visibility=PUBLIC)
	@Wrapper(wrap="param", parameters=double.class, internal=true, visibility=PROTECTED)
	static final WrapperParametersFeature feature = new WrapperParametersFeature();

	@Wrapper(wrap="method",       parameters=Number.class, visibility=PUBLIC)
	@Wrapper(wrap="methodStatic", parameters=Number.class, visibility=PROTECTED)
	@Wrapper(wrap="methodParent", parameters=Number.class, visibility=PRIVATE)
	@Wrapper(wrap="methodEnum",   parameters=Enum.class, visibility=PUBLIC)
	static final WrapperParametersFeatureGeneric<Float,MyEnum> generic = new WrapperParametersFeatureGeneric<>();

	@SuppressWarnings("unused")
	enum MyEnum { a }

	/**
	 * Creates a new WrapperParametersItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	WrapperParametersItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new WrapperParametersItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected WrapperParametersItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void paramFeatureInternal()
	{
		WrapperParametersItem.feature.param(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void paramFeature(final WrapperParametersFeature feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void paramFeature(final java.lang.Integer feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected final void paramFeature(final int feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private void paramFeature(final com.exedio.cope.instrument.testfeature.WrapperParametersFeature.SomeEnum feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void paramFeatureInternal(final byte[] feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected final void paramFeatureInternal(final com.exedio.cope.Item[][] feature)
	{
		WrapperParametersItem.feature.param(this,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public static final void paramFeatureInternal(final float feature)
	{
		WrapperParametersItem.feature.param(feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="param")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected static final void paramFeatureInternal(final double feature)
	{
		WrapperParametersItem.feature.param(WrapperParametersItem.class,feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void methodGeneric(final Float generic)
	{
		WrapperParametersItem.generic.method(this,generic);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="methodStatic")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	protected static final void methodGenericStatic(final Float generic)
	{
		WrapperParametersItem.generic.methodStatic(generic);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="methodParent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	private static void methodGenericParent(final Float generic)
	{
		WrapperParametersItem.generic.methodParent(WrapperParametersItem.class,generic);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="methodEnum")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	public final void methodGenericEnum(final MyEnum generic)
	{
		WrapperParametersItem.generic.methodEnum(this,generic);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapperParametersItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapperParametersItem> TYPE = com.exedio.cope.TypesBound.newType(WrapperParametersItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected WrapperParametersItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

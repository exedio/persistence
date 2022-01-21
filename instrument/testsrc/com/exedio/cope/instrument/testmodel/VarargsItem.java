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

import com.exedio.cope.instrument.testfeature.SettableVarargs;
import com.exedio.cope.instrument.testfeature.SettableVarargsInteger;
import com.exedio.cope.instrument.testfeature.WrapVarargs;
import java.util.List;

final class VarargsItem extends VarargsSuper
{
	static final SettableVarargsInteger integerValue = new SettableVarargsInteger(false);
	static final SettableVarargsInteger integerMandatoryValue = new SettableVarargsInteger(true);
	static final SettableVarargs<Double> doubleValue = new SettableVarargs<>(Double.class);
	@SuppressWarnings("unchecked")
	static final SettableVarargs<List<?>> listValue = new SettableVarargs<>((Class<List<?>>)(Class<?>)List.class);

	static final WrapVarargs integerOnly = new WrapVarargs(integerValue);
	static final WrapVarargs integerAndDouble = new WrapVarargs(integerValue, doubleValue);
	static final WrapVarargs integerMandatoryOnly = new WrapVarargs(integerMandatoryValue);
	static final WrapVarargs listOnly = new WrapVarargs(listValue);

	static final WrapVarargs onlySuper = new WrapVarargs(superInteger);
	static final WrapVarargs subAndSuper = new WrapVarargs(superInteger, doubleValue);

	static final WrapVarargs onlyLib = new WrapVarargs(libInteger);

	/**
	 * Creates a new VarargsItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	VarargsItem()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new VarargsItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private VarargsItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleIntegerOnly(final java.lang.Integer integerValue)
	{
		VarargsItem.integerOnly.simple(integerValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreIntegerOnlyParameters(final int integerOnly,final java.lang.Integer integerValue)
	{
		VarargsItem.integerOnly.moreParameters(integerOnly,integerValue);
	}

	/**
	 * @param integerValue myDoc/{@link #integerValue}/integerValue/varargsItem/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticIntegerOnlyToken(final java.lang.Integer integerValue)
	{
		return VarargsItem.integerOnly.staticToken(VarargsItem.class,integerValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleIntegerAndDouble(final java.lang.Integer integerValue,final Double doubleValue)
	{
		VarargsItem.integerAndDouble.simple(integerValue,doubleValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreIntegerAndDoubleParameters(final int integerAndDouble,final java.lang.Integer integerValue,final Double doubleValue)
	{
		VarargsItem.integerAndDouble.moreParameters(integerAndDouble,integerValue,doubleValue);
	}

	/**
	 * @param integerValue myDoc/{@link #integerValue}/integerValue/varargsItem/
	 * @param doubleValue myDoc/{@link #doubleValue}/doubleValue/varargsItem/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticIntegerAndDoubleToken(final java.lang.Integer integerValue,final Double doubleValue)
	{
		return VarargsItem.integerAndDouble.staticToken(VarargsItem.class,integerValue,doubleValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleIntegerMandatoryOnly(final int integerMandatoryValue)
	{
		VarargsItem.integerMandatoryOnly.simple(integerMandatoryValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreIntegerMandatoryOnlyParameters(final int integerMandatoryOnly,final int integerMandatoryValue)
	{
		VarargsItem.integerMandatoryOnly.moreParameters(integerMandatoryOnly,integerMandatoryValue);
	}

	/**
	 * @param integerMandatoryValue myDoc/{@link #integerMandatoryValue}/integerMandatoryValue/varargsItem/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticIntegerMandatoryOnlyToken(final int integerMandatoryValue)
	{
		return VarargsItem.integerMandatoryOnly.staticToken(VarargsItem.class,integerMandatoryValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleListOnly(final List<?> listValue)
	{
		VarargsItem.listOnly.simple(listValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreListOnlyParameters(final int listOnly,final List<?> listValue)
	{
		VarargsItem.listOnly.moreParameters(listOnly,listValue);
	}

	/**
	 * @param listValue myDoc/{@link #listValue}/listValue/varargsItem/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticListOnlyToken(final List<?> listValue)
	{
		return VarargsItem.listOnly.staticToken(VarargsItem.class,listValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleOnlySuper(final java.lang.Integer superInteger)
	{
		VarargsItem.onlySuper.simple(superInteger);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreOnlySuperParameters(final int onlySuper,final java.lang.Integer superInteger)
	{
		VarargsItem.onlySuper.moreParameters(onlySuper,superInteger);
	}

	/**
	 * @param superInteger myDoc/{@link #superInteger}/superInteger/varargsSuper/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticOnlySuperToken(final java.lang.Integer superInteger)
	{
		return VarargsItem.onlySuper.staticToken(VarargsItem.class,superInteger);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleSubAndSuper(final java.lang.Integer superInteger,final Double doubleValue)
	{
		VarargsItem.subAndSuper.simple(superInteger,doubleValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreSubAndSuperParameters(final int subAndSuper,final java.lang.Integer superInteger,final Double doubleValue)
	{
		VarargsItem.subAndSuper.moreParameters(subAndSuper,superInteger,doubleValue);
	}

	/**
	 * @param superInteger myDoc/{@link #superInteger}/superInteger/varargsSuper/
	 * @param doubleValue myDoc/{@link #doubleValue}/doubleValue/varargsItem/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticSubAndSuperToken(final java.lang.Integer superInteger,final Double doubleValue)
	{
		return VarargsItem.subAndSuper.staticToken(VarargsItem.class,superInteger,doubleValue);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleOnlyLib(final java.lang.Integer libInteger)
	{
		VarargsItem.onlyLib.simple(libInteger);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="moreParameters")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void moreOnlyLibParameters(final int onlyLib,final java.lang.Integer libInteger)
	{
		VarargsItem.onlyLib.moreParameters(onlyLib,libInteger);
	}

	/**
	 * @param libInteger myDoc/{@link #libInteger}/libInteger/varargsLib/
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="staticToken")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static VarargsItem staticOnlyLibToken(final java.lang.Integer libInteger)
	{
		return VarargsItem.onlyLib.staticToken(VarargsItem.class,libInteger);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for varargsItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<VarargsItem> TYPE = com.exedio.cope.TypesBound.newType(VarargsItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private VarargsItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

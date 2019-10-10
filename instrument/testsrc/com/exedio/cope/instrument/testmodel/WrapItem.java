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

import com.exedio.cope.Item;
import com.exedio.cope.instrument.testfeature.WrapFeature;
import com.exedio.cope.instrument.testfeature.WrapGeneric;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;

final class WrapItem extends Item
{
	static final WrapFeature feature = new WrapFeature();

	static final WrapGeneric<ZipEntry, InputStream, OutputStream> generic = new WrapGeneric<>();


	/**
	 * Creates a new WrapItem with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	WrapItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new WrapItem and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	private WrapItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simple")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	int simpleFeature()
	{
		return WrapItem.feature.simple(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simpleVoid")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	void simpleFeatureVoid()
	{
		WrapItem.feature.simpleVoid(this);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simpleStatic")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int simpleFeatureStatic()
	{
		return WrapItem.feature.simpleStatic();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="simpleStaticVoid")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void simpleFeatureStaticVoid()
	{
		WrapItem.feature.simpleStaticVoid();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="myOptionTagname")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int optionFeatureTagname()
	{
		return WrapItem.feature.optionTagname();
	}

	/**
	 * method documentation
	 * @param feature parameter documentation
	 * @return return documentation
	 * @throws java.lang.RuntimeException throws documentation
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="documented")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int documentedFeature(final int feature)
			throws
				java.lang.RuntimeException
	{
		return WrapItem.feature.documented(feature);
	}

	/**
	 * method documentation line 1 {@link #feature} feature wrapItem Feature {4}
	 * method documentation line 2 {@link #feature} feature wrapItem Feature {4}
	 *
	 * method documentation line 3 {@link #feature} feature wrapItem Feature {4}
	 * @param paramNameXfeatureXwrapItemXFeature parameter documentation line 1 {@link #feature} feature wrapItem Feature {4}
	 *        parameter documentation line 2 {@link #feature} feature wrapItem Feature {4}
	 *
	 *        parameter documentation line 3 {@link #feature} feature wrapItem Feature {4}
	 * @return return documentation line 1 {@link #feature} feature wrapItem Feature {4}
	 *         return documentation line 2 {@link #feature} feature wrapItem Feature {4}
	 *
	 *         return documentation line 3 {@link #feature} feature wrapItem Feature {4}
	 * @throws java.lang.RuntimeException throws documentation RuntimeException {@link #feature} feature wrapItem Feature {4}
	 * @throws java.lang.IllegalArgumentException throws documentation IllegalArgumentException line 1 {@link #feature} feature wrapItem Feature {4}
	 *         throws documentation IllegalArgumentException line 2 {@link #feature} feature wrapItem Feature {4}
	 *
	 *         throws documentation IllegalArgumentException line 3 {@link #feature} feature wrapItem Feature {4}
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="documentedMulti")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int documentedFeatureMulti(final int paramNameXfeatureXwrapItemXFeature)
			throws
				java.lang.RuntimeException,
				java.lang.IllegalArgumentException
	{
		return WrapItem.feature.documentedMulti(paramNameXfeatureXwrapItemXFeature);
	}

	/**
	 *
	 * method documentation line 2 {@link #feature} feature wrapItem Feature {4}
	 * @param feature
	 *        parameter documentation line 2 {@link #feature} feature wrapItem Feature {4}
	 * @return
	 *         return documentation line 2 {@link #feature} feature wrapItem Feature {4}
	 * @throws java.lang.IllegalArgumentException
	 *         throws documentation IllegalArgumentException line 2 {@link #feature} feature wrapItem Feature {4}
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="documentedFirstLineEmpty")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int documentedFeatureFirstLineEmpty(final int feature)
			throws
				java.lang.IllegalArgumentException
	{
		return WrapItem.feature.documentedFirstLineEmpty(feature);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="notHidden")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static int notFeatureHidden()
	{
		return WrapItem.feature.notHidden();
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="varargsMethod")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	byte[] varargsFeatureMethod(final java.lang.String array,final java.lang.Integer... varargs)
	{
		return WrapItem.feature.varargsMethod(this,array,varargs);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="arrayMethod")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	byte[] arrayFeatureMethod(final java.lang.Integer[] array)
	{
		return WrapItem.feature.arrayMethod(this,array);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="arrayAndVarargsMethod")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	byte[] arrayFeatureAndVarargsMethod(final java.lang.Integer[] array,final java.lang.Integer... varargs)
	{
		return WrapItem.feature.arrayAndVarargsMethod(this,array,varargs);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void methodGeneric(final InputStream a,final OutputStream b,final ZipEntry z,final java.lang.Double f,final java.io.Reader x)
	{
		WrapItem.generic.method(a,b,z,f,x);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="method")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void methodGeneric(final InputStream m,final OutputStream n,final ZipEntry z,final java.io.Writer x)
	{
		WrapItem.generic.method(m,n,z,x);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="varargsMethod")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static java.util.function.Supplier<?>[] varargsGenericMethod(final java.util.Collection<?>... varargs)
	{
		return WrapItem.generic.varargsMethod(varargs);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="arrayMethod")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void arrayGenericMethod(final java.util.Collection<?>[] varargs)
	{
		WrapItem.generic.arrayMethod(varargs);
	}

	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="arrayAndVarargsMethod")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	static void arrayGenericAndVarargsMethod(final java.util.Collection<?>[] array,final java.util.Collection<?>... varargs)
	{
		WrapItem.generic.arrayAndVarargsMethod(array,varargs);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for wrapItem.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<WrapItem> TYPE = com.exedio.cope.TypesBound.newType(WrapItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	private WrapItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

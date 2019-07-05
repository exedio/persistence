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
import com.exedio.cope.instrument.WrapInterim;
import com.exedio.cope.instrument.testfeature.MethodReferenceFunctionFeature;
import com.exedio.cope.instrument.testfeature.MethodReferenceRunnableFeature;

public final class MethodReferenceItem extends Item
{
	static final MethodReferenceFunctionFeature<Double,String> function = new MethodReferenceFunctionFeature<>(
			MethodReferenceItem::functionReferencedMethod);

	@WrapInterim(methodBody=false)
	private static String functionReferencedMethod(final double argument)
	{
		return methodUsedInFunctionReferencedMethod(argument);
	}

	private static String methodUsedInFunctionReferencedMethod(final double argument)
	{
		return "methodUsedInFunctionReferencedMethod " + argument;
	}


	static final MethodReferenceFunctionFeature<String,MethodReferenceValue> constructor = new MethodReferenceFunctionFeature<>(
			MethodReferenceValue::new);


	static final MethodReferenceRunnableFeature runnable = new MethodReferenceRunnableFeature(
			MethodReferenceItem::runnableReferencedMethod);

	@WrapInterim(methodBody=false)
	private static void runnableReferencedMethod()
	{
		methodUsedInRunnableReferencedMethod();
	}

	private static void methodUsedInRunnableReferencedMethod()
	{
		throw new IllegalStateException("methodUsedInRunnableReferencedMethod failure");
	}


	/**
	 * Creates a new MethodReferenceItem with all the fields initially needed.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	public MethodReferenceItem()
	{
		this(new com.exedio.cope.SetValue<?>[]{
		});
	}

	/**
	 * Creates a new MethodReferenceItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private MethodReferenceItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	static String getFunction(final Double function)
	{
		return MethodReferenceItem.function.get(function);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	static MethodReferenceValue getConstructor(final String constructor)
	{
		return MethodReferenceItem.constructor.get(constructor);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="run")
	static void runRunnable()
	{
		MethodReferenceItem.runnable.run();
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for methodReferenceItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	public static final com.exedio.cope.Type<MethodReferenceItem> TYPE = com.exedio.cope.TypesBound.newType(MethodReferenceItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private MethodReferenceItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

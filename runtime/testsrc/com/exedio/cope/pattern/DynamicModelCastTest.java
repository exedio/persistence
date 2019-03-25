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

package com.exedio.cope.pattern;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.EnumField;
import com.exedio.cope.Item;
import com.exedio.cope.Model;
import com.exedio.cope.StringField;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.pattern.DynamicModel.Enum;
import com.exedio.cope.pattern.DynamicModel.Field;
import com.exedio.cope.pattern.DynamicModel.Type;
import org.junit.jupiter.api.Test;

public class DynamicModelCastTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(ModelItem.TYPE);

	static
	{
		MODEL.enableSerialization(DynamicModelCastTest.class, "MODEL");
	}

	public DynamicModelCastTest()
	{
		super(MODEL);
	}

	@Test void testIt()
	{
		final Type<Locale> alpha1 = ModelItem.alpha.createType("alpha1");
		final Type<Locale> alpha2 = ModelItem.alpha.createType("alpha2");
		final Type<Locale> alfax  = ModelItem.alfa .createType("alfax");
		final Type<String> betax  = ModelItem.beta .createType("betax");
		final Field<Locale> alphaS  = alpha1.addStringField("alphaS");
		final Field<Locale> alpha1a = alpha1.addEnumField("alpha1a");
		final Field<Locale> alpha1b = alpha1.addEnumField("alpha1b");
		final Field<Locale> alpha2x = alpha2.addEnumField("alpha2x");
		final Field<Locale> alfaxx  = alfax .addEnumField("alfaxx");
		final Field<String> betaxx  = betax .addEnumField("betaxx");
		final Enum<Locale> alpha1aEnum = alpha1a.addEnumValue("alpha1a1");

		assertSame(alpha1aEnum, alpha1a.as(alpha1aEnum));
		assertSame(alpha1aEnum, alpha1b.as(alpha1aEnum));
		assertSame(alpha1aEnum, alpha2x.as(alpha1aEnum));
		assertSame(alpha1aEnum, alfaxx .as(alpha1aEnum));

		try
		{
			alphaS.as(alpha1aEnum);
			fail();
		}
		catch(final IllegalArgumentException e)
		{
			assertEquals("operation allowed for getValueType()==ENUM fields only, but was STRING", e.getMessage());
		}

		try
		{
			betaxx.as(alpha1aEnum);
			fail();
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a java.lang.String, but was a com.exedio.cope.pattern.DynamicModelCastTest$Locale", e.getMessage());
		}
	}

	@SuppressWarnings("unused") // OK: Enum for EnumField must not be empty
	enum Locale
	{
		ONE, TWO
	}

	static final class ModelItem extends Item
	{
		static final StringField name = new StringField().toFinal();

		static final DynamicModel<Locale> alpha = DynamicModel.create(EnumField.create(Locale.class), 1, 1, 1, 1, 2);
		@CopeSchemaName("xalfa")
		static final DynamicModel<Locale> alfa  = DynamicModel.create(EnumField.create(Locale.class), 1, 1, 1, 1, 2);
		static final DynamicModel<String> beta  = DynamicModel.create(new StringField(), 1, 1, 1, 1, 2);


	/**
	 * Creates a new ModelItem with all the fields initially needed.
	 * @param name the initial value for field {@link #name}.
	 * @throws com.exedio.cope.MandatoryViolationException if name is null.
	 * @throws com.exedio.cope.StringLengthViolationException if name violates its length constraint.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(constructor=...) and @WrapperInitial
	ModelItem(
				@javax.annotation.Nonnull final java.lang.String name)
			throws
				com.exedio.cope.MandatoryViolationException,
				com.exedio.cope.StringLengthViolationException
	{
		this(new com.exedio.cope.SetValue<?>[]{
			ModelItem.name.map(name),
		});
	}

	/**
	 * Creates a new ModelItem and sets the given fields initially.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(genericConstructor=...)
	private ModelItem(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #name}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nonnull
	java.lang.String getName()
	{
		return ModelItem.name.get(this);
	}

	/**
	 * Returns the dynamic type of this item in the model {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getType")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.DynamicModel.Type<Locale> getAlphaType()
	{
		return ModelItem.alpha.getType(this);
	}

	/**
	 * Sets the dynamic type of this item in the model {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setType")
	void setAlphaType(@javax.annotation.Nullable final com.exedio.cope.pattern.DynamicModel.Type<Locale> type)
	{
		ModelItem.alpha.setType(this,type);
	}

	/**
	 * Returns the value of {@code field} for this item in the model {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Object getAlpha(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<Locale> field)
	{
		return ModelItem.alpha.get(this,field);
	}

	/**
	 * Sets the value of {@code field} for this item in the model {@link #alpha}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAlpha(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<Locale> field,@javax.annotation.Nullable final java.lang.Object value)
	{
		ModelItem.alpha.set(this,field,value);
	}

	/**
	 * Returns the dynamic type of this item in the model {@link #alfa}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getType")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.DynamicModel.Type<Locale> getAlfaType()
	{
		return ModelItem.alfa.getType(this);
	}

	/**
	 * Sets the dynamic type of this item in the model {@link #alfa}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setType")
	void setAlfaType(@javax.annotation.Nullable final com.exedio.cope.pattern.DynamicModel.Type<Locale> type)
	{
		ModelItem.alfa.setType(this,type);
	}

	/**
	 * Returns the value of {@code field} for this item in the model {@link #alfa}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Object getAlfa(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<Locale> field)
	{
		return ModelItem.alfa.get(this,field);
	}

	/**
	 * Sets the value of {@code field} for this item in the model {@link #alfa}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setAlfa(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<Locale> field,@javax.annotation.Nullable final java.lang.Object value)
	{
		ModelItem.alfa.set(this,field,value);
	}

	/**
	 * Returns the dynamic type of this item in the model {@link #beta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="getType")
	@javax.annotation.Nullable
	com.exedio.cope.pattern.DynamicModel.Type<String> getBetaType()
	{
		return ModelItem.beta.getType(this);
	}

	/**
	 * Sets the dynamic type of this item in the model {@link #beta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="setType")
	void setBetaType(@javax.annotation.Nullable final com.exedio.cope.pattern.DynamicModel.Type<String> type)
	{
		ModelItem.beta.setType(this,type);
	}

	/**
	 * Returns the value of {@code field} for this item in the model {@link #beta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="get")
	@javax.annotation.Nullable
	java.lang.Object getBeta(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<String> field)
	{
		return ModelItem.beta.get(this,field);
	}

	/**
	 * Sets the value of {@code field} for this item in the model {@link #beta}.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @Wrapper(wrap="set")
	void setBeta(@javax.annotation.Nonnull final com.exedio.cope.pattern.DynamicModel.Field<String> field,@javax.annotation.Nullable final java.lang.Object value)
	{
		ModelItem.beta.set(this,field,value);
	}

	@javax.annotation.Generated("com.exedio.cope.instrument")
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for modelItem.
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument") // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<ModelItem> TYPE = com.exedio.cope.TypesBound.newType(ModelItem.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@javax.annotation.Generated("com.exedio.cope.instrument")
	private ModelItem(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}

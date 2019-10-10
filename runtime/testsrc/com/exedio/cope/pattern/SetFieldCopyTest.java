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

import static com.exedio.cope.instrument.Visibility.DEFAULT;
import static com.exedio.cope.instrument.Visibility.NONE;
import static com.exedio.cope.tojunit.Assert.assertContains;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.exedio.cope.CopeName;
import com.exedio.cope.CopeSchemaName;
import com.exedio.cope.CopyConstraint;
import com.exedio.cope.CopyViolationException;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.ItemField;
import com.exedio.cope.Model;
import com.exedio.cope.SchemaInfo;
import com.exedio.cope.TestWithEnvironment;
import com.exedio.cope.instrument.Wrapper;
import com.exedio.cope.instrument.WrapperType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SetFieldCopyTest extends TestWithEnvironment
{
	private static final Model MODEL = new Model(Catalog.TYPE, ParentInCatalog.TYPE, ElementInCatalog.TYPE, ParentWithNumber.TYPE, ElementWithNumber.TYPE);

	private static final CopyConstraint constraintCatalogParent = (CopyConstraint)ParentInCatalog.elementsSameCatalog.getRelationType().getFeature("copeNameCatalogCopyFromparent");
	private static final CopyConstraint constraintCatalogElement = (CopyConstraint)ParentInCatalog.elementsSameCatalog.getRelationType().getFeature("copeNameCatalogCopyFromelement");
	private static final CopyConstraint constraintNumberParent = (CopyConstraint)ParentWithNumber.elements.getRelationType().getFeature("numberCopyFromparent");
	private static final CopyConstraint constraintNumberElement = (CopyConstraint)ParentWithNumber.elements.getRelationType().getFeature("numberCopyFromelement");

	public SetFieldCopyTest()
	{
		super(MODEL);
	}

	@Test void names()
	{
		final ItemField<Catalog> relationCatalog = (ItemField<Catalog>)ParentInCatalog.elementsSameCatalog.getCopyWithCopyField(ParentInCatalog.catalog);
		assertEquals("copeNameCatalog", relationCatalog.getName());
		assertEquals("schemaNameAtParent", SchemaInfo.getColumnName(relationCatalog));

		assertEquals("copeNameCatalog", ElementInCatalog.javaNameIsNotCatalog.getName());
		assertEquals("schemaNameAtElement", SchemaInfo.getColumnName(ElementInCatalog.javaNameIsNotCatalog));

		assertEquals("copeNameCatalog", ParentInCatalog.catalog.getName());
		assertEquals("schemaNameAtParent", SchemaInfo.getColumnName(ParentInCatalog.catalog));
	}

	@Test void create()
	{
		final Catalog c = new Catalog();
		final ParentInCatalog p = new ParentInCatalog(c);
		final ElementInCatalog e = new ElementInCatalog(c);
		assertEquals(true, p.addToElementsSameCatalog(e));
		final Item relationItem = ParentInCatalog.elementsSameCatalog.getRelationType().searchSingletonStrict(
			ParentInCatalog.elementsSameCatalog.getCopyWithCopyField(ParentInCatalog.catalog).equal(c)
		);
		assertEquals(c, relationItem.get(ParentInCatalog.elementsSameCatalog.getCopyWithCopyField(ParentInCatalog.catalog)));
	}

	@Test void addToUnorderedItems()
	{
		final Catalog c1 = new Catalog();
		final Catalog c2 = new Catalog();
		final ParentInCatalog p = new ParentInCatalog(c1);
		final ElementInCatalog e1a = new ElementInCatalog(c1);
		final ElementInCatalog e1b = new ElementInCatalog(c1);
		final ElementInCatalog e2 = new ElementInCatalog(c2);
		assertEquals(true, p.addToElementsSameCatalog(e1a));
		assertEquals(true, p.addToElementsSameCatalog(e1b));
		try
		{
			p.addToElementsSameCatalog(e2);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for "+constraintCatalogParent+" and "+constraintCatalogElement+", "+
					"expected '"+c1+"' from target "+p+" but also '"+c2+"' from target "+e2,
				e.getMessage()
			);
		}
		assertContains(e1a, e1b, p.getElementsSameCatalog());
	}

	@Test void setUnorderedItems()
	{
		final Catalog c1 = new Catalog();
		final Catalog c2 = new Catalog();
		final ParentInCatalog p = new ParentInCatalog(c1);
		final ElementInCatalog e1a = new ElementInCatalog(c1);
		final ElementInCatalog e1b = new ElementInCatalog(c1);
		final ElementInCatalog e2 = new ElementInCatalog(c2);

		p.setElementsSameCatalog(singletonList(e1a));
		assertContains(e1a, p.getElementsSameCatalog());

		p.setElementsSameCatalog(singletonList(e1b));
		assertContains(e1b, p.getElementsSameCatalog());

		try
		{
			p.setElementsSameCatalog(singletonList(e2));
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on ParentInCatalog-elementsSameCatalog-0 for "+constraintCatalogElement+", "+
					"expected '"+c2+"' from target "+e2+", but was '"+c1+"'",
				e.getMessage()
			);
		}
		assertContains(e1b, p.getElementsSameCatalog());

		try
		{
			p.setElementsSameCatalog(asList(e1a, e2));
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for "+constraintCatalogParent+" and "+constraintCatalogElement+", "+
					"expected '"+c1+"' from target "+p+" but also '"+c2+"' from target "+e2,
				e.getMessage()
			);
		}
		// TODO: should be e1b
		assertContains(e1a, p.getElementsSameCatalog());

		p.setElementsSameCatalog(singletonList(e1b));
		try
		{
			p.setElementsSameCatalog(asList(e2, e1a));
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation on ParentInCatalog-elementsSameCatalog-0 for "+constraintCatalogElement+", "+
					"expected '"+c2+"' from target "+e2+", but was '"+c1+"'",
				e.getMessage()
			);
		}
		assertContains(e1b, p.getElementsSameCatalog());
	}

	@Test void addToOrderedNumber()
	{
		assertNotNull(constraintNumberParent, model.getType("ParentWithNumber-elements").getFeatures().toString());
		final ElementWithNumber e0a = new ElementWithNumber(0);
		final ElementWithNumber e0b = new ElementWithNumber(0);
		final ElementWithNumber e1 = new ElementWithNumber(1);
		final ParentWithNumber p0 = new ParentWithNumber(0);
		assertEquals(true, p0.addToElements(e0b));
		assertEquals(true, p0.addToElements(e0a));
		assertContainsOrdered(asList(e0b, e0a), p0.getElements());

		try
		{
			p0.addToElements(e1);
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for "+constraintNumberParent+" and "+constraintNumberElement+", "+
					"expected '0' from target "+p0+" but also '1' from target "+e1,
				e.getMessage()
			);
		}
		assertContainsOrdered(asList(e0b, e0a), p0.getElements());
	}

	@Test void setOrderedNumber()
	{
		final ElementWithNumber e0a = new ElementWithNumber(0);
		final ElementWithNumber e0b = new ElementWithNumber(0);
		final ElementWithNumber e1 = new ElementWithNumber(1);
		final ParentWithNumber p0 = new ParentWithNumber(0);

		p0.setElements(asList(e0a, e0b));
		assertContainsOrdered(asList(e0a, e0b), p0.getElements());

		try
		{
			p0.setElements(asList(e0b, e1));
			fail();
		}
		catch (final CopyViolationException e)
		{
			assertEquals(
				"copy violation for "+constraintNumberParent+" and "+constraintNumberElement+", "+
					"expected '0' from target "+p0+" but also '1' from target "+e1,
				e.getMessage()
			);
		}
		// TODO should be asList(e0a, e0b)
		assertContainsOrdered(asList(e0b), p0.getElements());
	}

	private static <T> void assertContainsOrdered(final List<T> expected, final Set<T> actual)
	{
		assertEquals(expected, new ArrayList<>(actual));
	}

	@WrapperType(comments=false, indent=2)
	private static class Catalog extends Item
	{

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private Catalog()
		{
			this(new com.exedio.cope.SetValue<?>[]{
			});
		}

		@com.exedio.cope.instrument.Generated
		protected Catalog(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<Catalog> TYPE = com.exedio.cope.TypesBound.newType(Catalog.class);

		@com.exedio.cope.instrument.Generated
		protected Catalog(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static class ParentInCatalog extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		@CopeName("copeNameCatalog")
		@CopeSchemaName("schemaNameAtParent")
		private static final ItemField<Catalog> catalog = ItemField.create(Catalog.class).toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		@Wrapper(wrap="get", visibility=DEFAULT)
		@Wrapper(wrap="set", visibility=DEFAULT)
		@Wrapper(wrap="addTo", visibility=DEFAULT)
		private static final SetField<ElementInCatalog> elementsSameCatalog = SetField.create(ItemField.create(ElementInCatalog.class)).copyWith(catalog);

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ParentInCatalog(
					@javax.annotation.Nonnull final Catalog catalog)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ParentInCatalog.catalog.map(catalog),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ParentInCatalog(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		private java.util.Set<ElementInCatalog> getElementsSameCatalog()
		{
			return ParentInCatalog.elementsSameCatalog.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setElementsSameCatalog(@javax.annotation.Nonnull final java.util.Collection<? extends ElementInCatalog> elementsSameCatalog)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			ParentInCatalog.elementsSameCatalog.set(this,elementsSameCatalog);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private boolean addToElementsSameCatalog(@javax.annotation.Nonnull final ElementInCatalog element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return ParentInCatalog.elementsSameCatalog.add(this,element);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ParentInCatalog> TYPE = com.exedio.cope.TypesBound.newType(ParentInCatalog.class);

		@com.exedio.cope.instrument.Generated
		protected ParentInCatalog(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static class ElementInCatalog extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		@CopeName("copeNameCatalog")
		@CopeSchemaName("schemaNameAtElement")
		private static final ItemField<Catalog> javaNameIsNotCatalog = ItemField.create(Catalog.class).toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ElementInCatalog(
					@javax.annotation.Nonnull final Catalog javaNameIsNotCatalog)
				throws
					com.exedio.cope.MandatoryViolationException
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ElementInCatalog.javaNameIsNotCatalog.map(javaNameIsNotCatalog),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ElementInCatalog(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ElementInCatalog> TYPE = com.exedio.cope.TypesBound.newType(ElementInCatalog.class);

		@com.exedio.cope.instrument.Generated
		protected ElementInCatalog(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static class ElementWithNumber extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField number = new IntegerField().optional().toFinal();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ElementWithNumber(
					@javax.annotation.Nullable final java.lang.Integer number)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ElementWithNumber.number.map(number),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ElementWithNumber(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ElementWithNumber> TYPE = com.exedio.cope.TypesBound.newType(ElementWithNumber.class);

		@com.exedio.cope.instrument.Generated
		protected ElementWithNumber(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}

	@WrapperType(comments=false, indent=2)
	private static class ParentWithNumber extends Item
	{
		@Wrapper(wrap="*", visibility=NONE)
		private static final IntegerField number = new IntegerField().optional().toFinal();

		@Wrapper(wrap="*", visibility=NONE)
		@Wrapper(wrap="set", visibility=DEFAULT)
		@Wrapper(wrap="addTo", visibility=DEFAULT)
		@Wrapper(wrap="get", visibility=DEFAULT)
		private static final SetField<ElementWithNumber> elements = SetField.create(ItemField.create(ElementWithNumber.class)).copyWith(number).ordered();

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
		private ParentWithNumber(
					@javax.annotation.Nullable final java.lang.Integer number)
		{
			this(new com.exedio.cope.SetValue<?>[]{
				ParentWithNumber.number.map(number),
			});
		}

		@com.exedio.cope.instrument.Generated
		protected ParentWithNumber(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		@javax.annotation.Nonnull
		private java.util.Set<ElementWithNumber> getElements()
		{
			return ParentWithNumber.elements.get(this);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private void setElements(@javax.annotation.Nonnull final java.util.Collection<? extends ElementWithNumber> elements)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			ParentWithNumber.elements.set(this,elements);
		}

		@com.exedio.cope.instrument.Generated
		@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
		private boolean addToElements(@javax.annotation.Nonnull final ElementWithNumber element)
				throws
					com.exedio.cope.MandatoryViolationException,
					java.lang.ClassCastException
		{
			return ParentWithNumber.elements.add(this,element);
		}

		@com.exedio.cope.instrument.Generated
		private static final long serialVersionUID = 1l;

		@com.exedio.cope.instrument.Generated
		private static final com.exedio.cope.Type<ParentWithNumber> TYPE = com.exedio.cope.TypesBound.newType(ParentWithNumber.class);

		@com.exedio.cope.instrument.Generated
		protected ParentWithNumber(final com.exedio.cope.ActivationParameters ap){super(ap);}
	}
}

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

import static com.exedio.cope.JoinWithInheritanceTest.Container.articles;
import static com.exedio.cope.JoinWithInheritanceTest.Container.specificArticles;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.exedio.cope.pattern.ListField;
import org.junit.jupiter.api.Test;

@SuppressWarnings("OverlyStrongTypeCast")
public class JoinWithInheritanceTest extends TestWithEnvironment
{
	static final Model MODEL = new Model(
			Container.TYPE,
			Article.TYPE,
			SpecificArticle.TYPE,
			ReallySpecificArticle.TYPE);

	static
	{
		MODEL.enableSerialization(JoinWithInheritanceTest.class, "MODEL");
	}

	public JoinWithInheritanceTest()
	{
		super(MODEL);
	}

	@Test void testCorrectSubtypeOfJoinWillBeUsed()
	{
		final Container container = new Container();
		container.addToArticles(new SpecificArticle());

		final Query<Container> query = Container.TYPE.newQuery();
		query.joinOuterLeft(articles.getRelationType(), articles.getParent().equalTarget());
		final Join articleJoin = query.join(SpecificArticle.TYPE);
		articleJoin.setCondition(((ItemField<?>)articles.getElement()).equalTarget(articleJoin));
		assertEquals(
				"select this from Container " +
				"left join Container-articles c1 on Container-articles.parent=this " +
				"join SpecificArticle s2 on Container-articles.element=s2.SpecificArticle.this",
				query.toString());
		assertEquals(asList(container), query.search());
	}

	@Test void testCorrectSubtypeOfJoinWillBeUsedEmptyResult()
	{
		final Container container = new Container();
		container.addToArticles(new SpecificArticle());

		final Query<Container> query = Container.TYPE.newQuery();
		query.joinOuterLeft(specificArticles.getRelationType(), specificArticles.getParent()
				.equalTarget());
		final Join articleJoin = query.join(ReallySpecificArticle.TYPE);
		articleJoin.setCondition(((ItemField<?>)specificArticles.getElement()).equalTarget(articleJoin));
		assertEquals(
				"select this from Container " +
				"left join Container-specificArticles c1 on Container-specificArticles.parent=this " +
				"join ReallySpecificArticle r2 on Container-specificArticles.element=r2.ReallySpecificArticle.this",
				query.toString());
		assertEquals(asList(), query.search());
	}

	@Test void testSuperTypeCastNotAllowed()
	{
		final Container container = new Container();
		container.addToArticles(new SpecificArticle());

		final Query<Container> query = Container.TYPE.newQuery();
		query.joinOuterLeft(specificArticles.getRelationType(), specificArticles.getParent().equalTarget());
		final Join articleJoin = query.join(Article.TYPE);
		try
		{
			((ItemField<?>)specificArticles.getElement()).equalTarget(articleJoin);
			fail("exception expected");
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a SpecificArticle, but was a Article", e.getMessage());
		}
	}

	@Test void testInvalidJoinIsBound()
	{
		final Container container = new Container();
		container.addToArticles(new SpecificArticle());

		final Query<Container> query = Container.TYPE.newQuery();
		final Join test = query.joinOuterLeft(articles.getRelationType(), articles.getParent()
				.equalTarget());
		query.join(SpecificArticle.TYPE);
		try
		{
			((ItemField<?>)articles.getElement()).equalTarget(test);
			fail("exception expected");
		}
		catch(final ClassCastException e)
		{
			assertEquals("expected a Article, but was a Container-articles", e.getMessage());
		}
	}

	static class Container extends Item
	{
		static final ListField<Article> articles = ListField.create(ItemField.create(Article.class));
		static final ListField<SpecificArticle> specificArticles = ListField.create(ItemField.create(SpecificArticle.class));

	/**
	 * Creates a new Container with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	Container()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new Container and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Container(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	/**
	 * Returns the value of {@link #articles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.util.List<Article> getArticles()
	{
		return Container.articles.get(this);
	}

	/**
	 * Returns a query for the value of {@link #articles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<Article> getArticlesQuery()
	{
		return Container.articles.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #articles} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.List<Container> getDistinctParentsOfArticles(final Article element)
	{
		return Container.articles.getDistinctParents(Container.class,element);
	}

	/**
	 * Adds a new value for {@link #articles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void addToArticles(@javax.annotation.Nonnull final Article articles)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		Container.articles.add(this,articles);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #articles}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean removeAllFromArticles(@javax.annotation.Nonnull final Article articles)
	{
		return Container.articles.removeAll(this,articles);
	}

	/**
	 * Sets a new value for {@link #articles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setArticles(@javax.annotation.Nonnull final java.util.Collection<? extends Article> articles)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		Container.articles.set(this,articles);
	}

	/**
	 * Returns the parent field of the type of {@link #articles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<Container> articlesParent()
	{
		return Container.articles.getParent(Container.class);
	}

	/**
	 * Returns the value of {@link #specificArticles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="get")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final java.util.List<SpecificArticle> getSpecificArticles()
	{
		return Container.specificArticles.get(this);
	}

	/**
	 * Returns a query for the value of {@link #specificArticles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getQuery")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	final com.exedio.cope.Query<SpecificArticle> getSpecificArticlesQuery()
	{
		return Container.specificArticles.getQuery(this);
	}

	/**
	 * Returns the items, for which field list {@link #specificArticles} contains the given element.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="getDistinctParentsOf")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final java.util.List<Container> getDistinctParentsOfSpecificArticles(final SpecificArticle element)
	{
		return Container.specificArticles.getDistinctParents(Container.class,element);
	}

	/**
	 * Adds a new value for {@link #specificArticles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="addTo")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void addToSpecificArticles(@javax.annotation.Nonnull final SpecificArticle specificArticles)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		Container.specificArticles.add(this,specificArticles);
	}

	/**
	 * Removes all occurrences of {@code element} from {@link #specificArticles}.
	 * @return {@code true} if the field set changed as a result of the call.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="removeAllFrom")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final boolean removeAllFromSpecificArticles(@javax.annotation.Nonnull final SpecificArticle specificArticles)
	{
		return Container.specificArticles.removeAll(this,specificArticles);
	}

	/**
	 * Sets a new value for {@link #specificArticles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="set")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	final void setSpecificArticles(@javax.annotation.Nonnull final java.util.Collection<? extends SpecificArticle> specificArticles)
			throws
				com.exedio.cope.MandatoryViolationException,
				java.lang.ClassCastException
	{
		Container.specificArticles.set(this,specificArticles);
	}

	/**
	 * Returns the parent field of the type of {@link #specificArticles}.
	 */
	@com.exedio.cope.instrument.Generated // customize with @Wrapper(wrap="Parent")
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedStaticUsage"})
	@javax.annotation.Nonnull
	static final com.exedio.cope.ItemField<Container> specificArticlesParent()
	{
		return Container.specificArticles.getParent(Container.class);
	}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for container.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	static final com.exedio.cope.Type<Container> TYPE = com.exedio.cope.TypesBound.newType(Container.class,Container::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Container(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	private abstract static class Article extends Item
	{


	/**
	 * Creates a new Article with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private Article()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new Article and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected Article(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 2l;

	/**
	 * The persistent type information for article.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<Article> TYPE = com.exedio.cope.TypesBound.newTypeAbstract(Article.class);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected Article(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	private static class SpecificArticle extends Article
	{


	/**
	 * Creates a new SpecificArticle with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private SpecificArticle()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new SpecificArticle and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected SpecificArticle(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for specificArticle.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<SpecificArticle> TYPE = com.exedio.cope.TypesBound.newType(SpecificArticle.class,SpecificArticle::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected SpecificArticle(final com.exedio.cope.ActivationParameters ap){super(ap);}
}

	private static class ReallySpecificArticle extends SpecificArticle
	{


	/**
	 * Creates a new ReallySpecificArticle with all the fields initially needed.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(constructor=...) and @WrapperInitial
	@java.lang.SuppressWarnings({"RedundantSuppression","TypeParameterExtendsFinalClass","UnnecessarilyQualifiedInnerClassAccess"})
	private ReallySpecificArticle()
	{
		this(com.exedio.cope.SetValue.EMPTY_ARRAY);
	}

	/**
	 * Creates a new ReallySpecificArticle and sets the given fields initially.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(genericConstructor=...)
	protected ReallySpecificArticle(final com.exedio.cope.SetValue<?>... setValues){super(setValues);}

	@com.exedio.cope.instrument.Generated
	private static final long serialVersionUID = 1l;

	/**
	 * The persistent type information for reallySpecificArticle.
	 */
	@com.exedio.cope.instrument.Generated // customize with @WrapperType(type=...)
	private static final com.exedio.cope.Type<ReallySpecificArticle> TYPE = com.exedio.cope.TypesBound.newType(ReallySpecificArticle.class,ReallySpecificArticle::new);

	/**
	 * Activation constructor. Used for internal purposes only.
	 * @see com.exedio.cope.Item#Item(com.exedio.cope.ActivationParameters)
	 */
	@com.exedio.cope.instrument.Generated
	protected ReallySpecificArticle(final com.exedio.cope.ActivationParameters ap){super(ap);}
}
}

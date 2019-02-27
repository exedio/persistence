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

package com.exedio.cope.tojunit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

public abstract class MainRule
{
	private boolean beforeCalled = false;

	protected void before()
	{
		// empty default implementation
	}

	protected void before(final ExtensionContext context)
	{
		// empty default implementation
	}

	protected void after() throws Exception
	{
		// empty default implementation
	}

	public void assertBeforeCalled()
	{
		assertTrue(beforeCalled);
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@ExtendWith(Extension.class)
	public @interface Tag {}

	private static final class Extension implements BeforeEachCallback, AfterEachCallback
	{
		private ArrayList<MainRule> rules;

		@Override
		@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
		public void beforeEach(final ExtensionContext context) throws Exception
		{
			assertNull(rules);
			this.rules = new ArrayList<>();

			@SuppressWarnings("OptionalGetWithoutIsPresent")
			final Class<?> testClass = context.getTestClass().get();
			final LinkedList<Class<?>> classesFromTop = new LinkedList<>();
			for(Class<?> clazz = testClass; clazz!=null; clazz = clazz.getSuperclass())
				classesFromTop.add(0, clazz);
			for(final Class<?> clazz : classesFromTop)
				for(final Field field : clazz.getDeclaredFields())
					if(MainRule.class.isAssignableFrom(field.getType()))
					{
						assertTrue (Modifier.isFinal (field.getModifiers()));
						assertFalse(Modifier.isStatic(field.getModifiers()));
						@SuppressWarnings("OptionalGetWithoutIsPresent")
						final Object instance = context.getTestInstance().get();
						field.setAccessible(true);
						final MainRule rule = (MainRule)field.get(instance);
						rules.add(rule);
						assertFalse(rule.beforeCalled);
						rule.before();
						rule.before(context);
						rule.beforeCalled = true;
					}
			assertFalse(rules.isEmpty(), "no rule in " + testClass);
		}

		@Override
		@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
		public void afterEach(final ExtensionContext context) throws Exception
		{
			if(rules!=null)
			{
				for(final ListIterator<MainRule> i = rules.listIterator(rules.size()); i.hasPrevious(); )
				{
					final MainRule rule = i.previous();
					rule.after();
					i.remove();
				}
				rules = null;
			}
		}
	}
}

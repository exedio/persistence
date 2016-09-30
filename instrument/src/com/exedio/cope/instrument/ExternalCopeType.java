package com.exedio.cope.instrument;

import com.exedio.cope.Item;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

final class ExternalCopeType extends CopeType<ExternalCopeFeature>
{
	private final Class<?> itemClass;

	ExternalCopeType(final Class<?> itemClass)
	{
		super(true, false, false);
		if (!Item.class.isAssignableFrom(itemClass)) throw new RuntimeException();
		this.itemClass=itemClass;
		registerFeatures();
	}

	private void registerFeatures()
	{
		for (Field declaredField: itemClass.getDeclaredFields())
		{
			final int modifiers=declaredField.getModifiers();
			if (!Modifier.isFinal(modifiers) || !Modifier.isStatic(modifiers))
				continue;
			if (declaredField.getAnnotation(WrapperIgnore.class)!=null)
				continue;
			if (declaredField.getType().isAnnotationPresent(WrapFeature.class))
				register(new ExternalCopeFeature(this, declaredField));
		}
	}

	@Override
	String getName()
	{
		return itemClass.getSimpleName();
	}

	@Override
	WrapperType getOption()
	{
		throw new RuntimeException("unexpected call - should only be needed for generating the class itself");
	}

	@Override
	boolean isInterface()
	{
		return itemClass.isInterface();
	}

	@Override
	Evaluatable getField(final String name)
	{
		try
		{
			final Field field=itemClass.getDeclaredField(name);
			if (Modifier.isStatic(field.getModifiers()))
			{
				return new ExternalEvaluatable(field);
			}
			else
			{
				return null;
			}
		}
		catch (final NoSuchFieldException e)
		{
			return null;
		}
	}

	@Override
	ExternalCopeType getSuperclass()
	{
		if (itemClass.getSuperclass()==Item.class)
			return null;
		else
			return new ExternalCopeType(itemClass.getSuperclass());
	}

	@Override
	int getTypeParameters()
	{
		throw new RuntimeException("unexpected call - should only be needed for generating the class itself");
	}

	@Override
	String getFullName()
	{
		return itemClass.getName();
	}

	@Override
	int getModifier()
	{
		return itemClass.getModifiers();
	}

	@Override
	void assertNotBuildStage()
	{
	}

	@Override
	void assertGenerateStage()
	{
	}

	@Override
	void assertNotGenerateStage()
	{
	}

	static class ExternalEvaluatable implements Evaluatable
	{
		private final Field field;

		private ExternalEvaluatable(final Field field)
		{
			if (!Modifier.isStatic(field.getModifiers())) throw new RuntimeException();
			this.field=field;
		}

		@Override
		@SuppressFBWarnings("DP_DO_INSIDE_DO_PRIVILEGED")
		public Object evaluate()
		{
			field.setAccessible(true);
			try
			{
				return field.get(null);
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}

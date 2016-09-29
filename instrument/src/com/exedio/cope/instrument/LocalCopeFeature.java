package com.exedio.cope.instrument;

import java.lang.annotation.Annotation;

final class LocalCopeFeature extends CopeFeature
{
	private final JavaField javaField;
	private final String docComment;
	private final Boolean initialByConfiguration;

	LocalCopeFeature(final LocalCopeType parent, final JavaField javaField)
	{
		super(parent);
		this.javaField=javaField;
		this.docComment = javaField.docComment;
		final WrapperInitial initialConfig = Tags.cascade(javaField, Tags.forInitial(docComment), javaField.wrapperInitial, null);
		this.initialByConfiguration = initialConfig==null ? null : initialConfig.value();
	}

	@Override
	String getName()
	{
		return javaField.name;
	}

	@Override
	int getModifier()
	{
		return javaField.modifier;
	}

	@Override
	Boolean getInitialByConfiguration()
	{
		return initialByConfiguration;
	}

	@Override
	String getType()
	{
		return javaField.type;
	}

	@Override
	Object evaluate()
	{
		return javaField.evaluate();
	}

	Wrapper getOption(final String modifierTag)
	{
		return Tags.cascade(
				javaField,
				Tags.forFeature(docComment, modifierTag),
				javaField.getWrappers(modifierTag),
				OPTION_DEFAULT);
	}

	private static final Wrapper OPTION_DEFAULT = new Wrapper()
	{
		@Override public Class<? extends Annotation> annotationType() { throw new RuntimeException(); }
		@Override public String wrap() { throw new RuntimeException(); }
		@Override public Visibility visibility() { return Visibility.DEFAULT; }
		@Override public boolean internal() { return false; }
		@Override public boolean booleanAsIs() { return false; }
		@Override public boolean asFinal() { return true; }
		@Override public boolean override() { return false; }
	};

	final JavaClass getParent()
	{
		return javaField.parent;
	}
}

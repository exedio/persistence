package com.exedio.cope.instrument;

import com.exedio.cope.Item;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashMap;

final class LocalCopeType extends CopeType
{
	private static final WrapperType OPTION_DEFAULT = new WrapperType()
	{
		@Override public Class<? extends Annotation> annotationType() { throw new RuntimeException(); }
		@Override public Visibility type() { return Visibility.DEFAULT; }
		@Override public Visibility constructor() { return Visibility.DEFAULT; }
		@Override public Visibility genericConstructor() { return Visibility.DEFAULT; }
		@Override public Visibility activationConstructor() { return Visibility.DEFAULT; }
		@Override public int indent() { return 1; }
		@Override public boolean comments() { return true; }
	};

	private static final HashMap<JavaClass, LocalCopeType> copeTypeByJavaClass = new HashMap<>();

	static final LocalCopeType getCopeType(final JavaClass javaClass)
	{
		final LocalCopeType result = copeTypeByJavaClass.get(javaClass);
		//System.out.println("getCopeClass "+javaClass.getFullName()+" "+(result==null?"NULL":result.getName()));
		return result;
	}


	final JavaClass javaClass;
	private final String name;
	private final InternalVisibility visibility;
	private final WrapperType option;

	private CopeType supertype;

	LocalCopeType(final JavaClass javaClass, final boolean isItem, final boolean isBlock, final boolean isComposite)
	{
		super(isItem, isBlock, isComposite);
		this.javaClass=javaClass;
		if (javaClass.classExtends==null) throw new RuntimeException();
		this.name = javaClass.name;
		this.visibility = javaClass.getVisibility();
		this.option = Tags.cascade(
				javaClass,
				Tags.forType(javaClass.docComment),
				javaClass.typeOption,
				OPTION_DEFAULT);
		copeTypeByJavaClass.put(javaClass, this);

		javaClass.nameSpace.importStatic(Item.class);
		javaClass.file.repository.add(this);

		registerFeatures();
	}

	private void registerFeatures()
	{
		feature: for(final JavaField javaField : javaClass.getFields())
		{
			final int modifier = javaField.modifier;
			if(!Modifier.isFinal(modifier) || !Modifier.isStatic(modifier))
				continue feature;

			final String docComment = javaField.docComment;
			if(Tags.cascade(javaField, Tags.forIgnore(docComment), javaField.wrapperIgnore, null)!=null)
				continue feature;

			final Class<?> typeClass = javaField.file.findTypeExternally(javaField.typeRaw);
			if(typeClass==null)
				continue feature;

			if(typeClass.isAnnotationPresent(WrapFeature.class))
			{
				register(new LocalCopeFeature(this, javaField));
			}
		}
	}

	@Override
	String getName()
	{
		return name;
	}

	@Override
	WrapperType getOption()
	{
		return option;
	}

	@Override
	InternalVisibility getVisibility()
	{
		return visibility;
	}

	@Override
	boolean isFinal()
	{
		return javaClass.isFinal();
	}

	@Override
	public boolean isInterface()
	{
		return javaClass.isInterface();
	}

	/** @return null if the type has no field with that name */
	@Override
	JavaField getField(final String name)
	{
		return javaClass.getField(name);
	}

	void endBuildStage()
	{
		assert !javaClass.file.repository.isBuildStage();
		assert javaClass.file.repository.isGenerateStage();

		if(!isItem())
			return;

		final Class<?> externalType = javaClass.file.findTypeExternally(javaClass.classExtends);
		if(externalType==Item.class)
		{
			supertype = null;
		}
		else if (externalType!=null)
		{
			supertype = new ExternalCopeType(externalType);
		}
		else
		{
			supertype = javaClass.file.repository.getCopeType(javaClass.classExtends);
			if (!supertype.isItem()) throw new RuntimeException();
		}
	}

	@Override
	CopeType getSuperclass()
	{
		assert !javaClass.file.repository.isBuildStage();

		return supertype;
	}

	@Override
	int getTypeParameters()
	{
		return javaClass.typeParameters;
	}

	@Override
	String getFullName()
	{
		return javaClass.getFullName();
	}

	@Override
	int getModifier()
	{
		return javaClass.modifier;
	}

	@Override
	void assertNotBuildStage()
	{
		assert !javaClass.file.repository.isBuildStage();
	}

	@Override
	void assertGenerateStage()
	{
		assert javaClass.file.repository.isGenerateStage();
	}

	@Override
	void assertNotGenerateStage()
	{
		assert !javaClass.file.repository.isGenerateStage();
	}
}

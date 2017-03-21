package com.exedio.cope.instrument;

final class ComponentFeature extends CopeFeature
{
	private final CopeFeature container;
	private final Object component;
	private final String postfix;

	ComponentFeature(final CopeFeature container, final Object component, final String postfix)
	{
		super(container.parent);
		this.container = container;
		this.component = component;
		this.postfix = postfix;
	}

	@Override
	String getName()
	{
		return container.getName()+"_"+postfix;
	}

	@Override
	int getModifier()
	{
		return container.getModifier();
	}

	@Override
	String getJavadocReference()
	{
		return "'"+postfix+"' of "+container.getJavadocReference();
	}

	@Override
	Boolean getInitialByConfiguration()
	{
		return false;
	}

	@Override
	String getType()
	{
		return "";
	}

	@Override
	Object evaluate()
	{
		return component;
	}

}

package com.exedio.cope.pattern;

import java.util.LinkedHashMap;

import com.exedio.cope.BooleanField;
import com.exedio.cope.Feature;
import com.exedio.cope.IntegerField;
import com.exedio.cope.Item;
import com.exedio.cope.Pattern;
import com.exedio.cope.StringField;
import com.exedio.cope.Type;

class TypeInheritanceTestPattern extends Pattern
{
	private Type<PatternItem> abstractType = null;
	public static final String ABSTRACTTYPE_POSTFIX = "AbstractType";

	private StringField superTypeString = null;
	public static final String ABSTRACTTYPE_STRING = "string";
	
	private BooleanField superTypeBoolean = null;
	public static final String ABSTRACTTYPE_BOOLEAN = "boolean";
	
	private Type<? extends Item> subType = null;
	public static final String SUBTYPE_POSTFIX = "SubType";
	
	private IntegerField subTypeInteger = null;
	public static final String SUBTYPE_INTEGER= "integer";
	
	@Override
	protected void initialize()
	{
		super.initialize();
		
		//Create the super type.
		LinkedHashMap<String, Feature> features = new LinkedHashMap<String, Feature>(); 
		this.superTypeString = new StringField();
		features.put(ABSTRACTTYPE_STRING, this.superTypeString);
		this.superTypeBoolean = new BooleanField();
		features.put(ABSTRACTTYPE_BOOLEAN, this.superTypeBoolean);
		this.abstractType = newSourceType(PatternItem.class, features, ABSTRACTTYPE_POSTFIX, null, Boolean.valueOf(true));
		
		//Create sub type
		features = new LinkedHashMap<String, Feature>();
		this.subTypeInteger = new IntegerField();
		features.put(SUBTYPE_INTEGER, subTypeInteger);
		this.subType = newSourceType(PatternItem.class, features, SUBTYPE_POSTFIX, abstractType, Boolean.valueOf(false));
	}
	
	public Type<? extends Item> getAbstractType()
	{
		return abstractType;
	}

	public Type<? extends Item> getSubType()
	{
		return subType;
	}
}

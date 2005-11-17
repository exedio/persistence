/*
Some initial test comment. 
*/

package// hallo
  com.exedio.cope.instrument;

import java.util.*;
import java.text.Format;

/**
	Represents an attribute or association partner of a class.
	Note: type==Model.AMIGOUS means, the attribute cannot be used in OCL due to attribute ambiguities.
	See OCL spec 5.4.1. for details.
*/
public abstract class Example implements Runnable
{
  private String name;
  private Integer type=new Integer(5);
  private volatile Integer[] qualifiers;
  String hallo="hallo";
  
  /**TestCommentCommaSeparated123*/
  int commaSeparated1,commaSeparated2=0,commaSeparated3; 
  /**TestCommentCommaSeparated456*/
  int commaSeparated4=80,commaSeparated5,commaSeparated6=200; 

  // these attributes test the ability of the parser
  // to skip more complex (ugly) attribute initializers
  String   uglyAttribute1="some'Thing{some\"Thing;Else";
  char     uglyAttribute2=';';
  char     uglyAttribute3='{';
  char     uglyAttribute4='"';
  char     uglyAttribute5='\'';
  String[] uglyAttribute6=
  {
	 "some'Thing{some\"Thing;Else", // ugly ; { " ' comment
	 "some'Thing{some\"Thing;Else"
  };
  char[]   uglyAttribute7={';','{','"','\''};
  Runnable uglyAttribute8=new Runnable()
  {
	 // ugly ; { " ' comment
	 String   uglyInnerAttribute1="some'Thing{some\"Thing;Else";
	 char     uglyInnerAttribute2=';';
	 char     uglyInnerAttribute3='{';
	 char     uglyInnerAttribute4='"';
	 char     uglyInnerAttribute5='\'';
	 String[] uglyInnerAttribute6=
	 {
		"some'Thing{some\"Thing;Else", // ugly ; { " ' comment
		"some'Thing{some\"Thing;Else"
	 };
	 char[]   uglyInnerAttribute7={';','{','"','\''};
	 public void run()
	 {
		// ugly ; { " ' comment
		String   uglyVariable1="some'Thing{some\"Thing;Else";
		char     uglyVariable2=';';
		char     uglyVariable3='{';
		char     uglyVariable4='"';
		char     uglyVariable5='\'';
		String[] uglyVariable6=
		{
		  "some'Thing{some\"Thing;Else", // ugly ; { " ' comment
		  "some'Thing{some\"Thing;Else"
		};
		char[]   uglyAttribute7={';','{','"','\''};
		
		System.out.println(uglyVariable1+uglyVariable2+uglyVariable3+uglyVariable4+uglyVariable5+uglyVariable6+uglyAttribute7[0]);
	 }
	 // ugly ; { " ' comment
  };
  // end of ugly attributes
  

  class Inner implements Runnable
  {
	 class Drinner implements Runnable
	 {
		boolean someDrinnerBoolean=true;
    
		public void run()
		{
		}
	 }

	 boolean someInnerBoolean=true;
    
	 public void run()
	 {
	 }
  }  

  static class InnerSub extends ExampleTest implements java.io.Serializable, Cloneable
  {
  }

  public Example()
  {
	 new Integer(5);
  }
  
  private Example(String name, Integer type)
  {
	 super();
  }

  public void set(String name, Integer type,// what a cool parameter
	final Integer[] qualifiers)
	{
		// ugly comment : { {
		String x="ugly { string \" { literal";
		char c='{';
		
		/**
		ugly comment *
		**/
		
		int a=20;// some other comment
		int b=10;
		a=a/(a+b); // ugly expression
		
		System.out.println(x+c);
	}

  abstract void abstractMethod();

  /**
	  Some example doc-comment.
  */
  public void run()
  {}

	public boolean getBoolean(int someInterface)
	{
		return true;
	}
	
	public Integer[] getIntegers()
	{
		return null;
	}
	
	/** DO_DISCARD */
	int discardAttribute = 0;
	
	/** DO_DISCARD */
	void discardMethod() {}
	
	public Integer getUnqualifiedType() throws IllegalArgumentException
	{
		return null;
	}
	
	public void setParent  (Object parent)
		throws
			IllegalArgumentException,
			NullPointerException
	{
	}

	public void printData
		(java.io.PrintStream o)
	{
	}
  
	private void accessifierPrivate() {}
	protected void accessifierProtected() {}
	void accessifierPackage() {}
	public void accessifierPublic() {}
  
	static public void main(String[] args)
	{
		// use imports
		List l;
		Format f;
		l=null;
		f=null;
		System.out.println(l.toString()+f.toString());
	}

}

class SecondExample extends Example{void abstractMethod(){}}


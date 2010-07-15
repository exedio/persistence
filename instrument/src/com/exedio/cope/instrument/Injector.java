/*
 * Copyright (C) 2000  Ralf Wiebicke
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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

package com.exedio.cope.instrument;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.exedio.cope.instrument.Tokenizer.CharToken;
import com.exedio.cope.instrument.Tokenizer.CommentToken;
import com.exedio.cope.instrument.Tokenizer.EndException;
import com.exedio.cope.instrument.Tokenizer.ParseException;
import com.exedio.cope.instrument.Tokenizer.StringToken;
import com.exedio.cope.instrument.Tokenizer.Token;

/**
 * Implements a modifying java parser.
 * This means, the input stream is continuously written
 * into an output stream, and may be modified before writing.
 *
 * The parser recognizes java meta information only,
 * which is anything outside method bodies and attribute
 * initializers.
 *
 * To use the parser, provide an implementation of the
 * InjectionConsumer interface to the constructor.
 * @see InjectionConsumer
 *
 * @author Ralf Wiebicke
 */
final class Injector
{
	final Tokenizer tokenizer;
	private final InjectionConsumer consumer;

	private String docComment = null;

	final JavaFile javaFile;

	/**
	 * Constructs a new java parser.
	 * @param inputFile
	 * the input stream to be parsed.
	 * @param consumer
	 * an implementation of InjectionConsumer,
	 * listening to parsed elements of the input stream.
	 * @see InjectionConsumer
	 */
	public Injector(final Tokenizer tokenizer,
								final InjectionConsumer consumer, final JavaFile javaFile)
	{
		this.tokenizer = tokenizer;
		this.consumer = consumer;
		this.javaFile = javaFile;
	}

	private void scheduleBlock(final boolean collect_when_blocking)
	{
		tokenizer.scheduleBlock(collect_when_blocking);
	}

	private boolean do_block()
	{
		return tokenizer.do_block();
	}

	private boolean collect_when_blocking()
	{
		return tokenizer.collect_when_blocking();
	}

	private String getCollector()
	{
		return tokenizer.getCollector();
	}

	private void discardNextFeature(final boolean b)
	{
		tokenizer.discardNextFeature(b);
	}

	private void flushOutbuf()
	{
		tokenizer.flushOutbuf();
	}

	private void write(final String s)
	{
		tokenizer.write(s);
	}

	/**
	 * Splits the character stream into tokens.
	 * This tokenizer works only outside of method bodys.
	 */
	private Token readToken() throws EndException
	{
		return tokenizer.readToken();
	}

	/**
	 * Parses a method body or an attribute initializer,
	 * depending on the parameter.
	 * For method bodys, the input stream must be directly behind
	 * the first opening curly bracket of the body.
	 * For attribute initializers, the input stream must be directly
	 * behind the '='.
	 * @return
	 * the delimiter, which terminated the attribute initializer
	 * (';' or ',') or '}' for methods.
	 */
	private CharToken parseBody(final boolean attribute, final InitializerConsumer tokenConsumer)
		throws EndException, ParseException
	{
		return tokenizer.parseBody(attribute, tokenConsumer);
	}

	/**
	 * Parses a class feature. May be an attribute, a method or a inner
	 * class. May even be a normal class, in this case parent==null.
	 * @param parent the class that contains the class feature
	 * if null, there is no containing class, and
	 * the feature must be a class itself.
	 */
	private JavaFeature[] parseFeature(final JavaClass parent, final StringToken bufs)
		throws IOException, EndException, InjectorParseException
	{
		return parseFeature(parent, bufs.value);
	}

	/**
	 * The same as parseFeature(JavaClass) but the first token has
	 * already been fetched from the input stream.
	 * @param bufs the first token of the class feature.
	 * @see #parseFeature(JavaClass,StringToken)
	 */
	private JavaFeature[] parseFeature(final JavaClass parent, String bufs)
		throws IOException, EndException, InjectorParseException
	{
		int modifiers = 0;

		String featuretype;
		while (true)
		{
			//System.out.println("bufs >"+bufs+"<");
			if ("public".equals(bufs))
				modifiers |= Modifier.PUBLIC;
			else if ("protected".equals(bufs))
				modifiers |= Modifier.PROTECTED;
			else if ("private".equals(bufs))
				modifiers |= Modifier.PRIVATE;
			else if ("static".equals(bufs))
				modifiers |= Modifier.STATIC;
			else if ("final".equals(bufs))
				modifiers |= Modifier.FINAL;
			else if ("synchronized".equals(bufs))
				modifiers |= Modifier.SYNCHRONIZED;
			else if ("volatile".equals(bufs))
				modifiers |= Modifier.VOLATILE;
			else if ("transient".equals(bufs))
				modifiers |= Modifier.TRANSIENT;
			else if ("native".equals(bufs))
				modifiers |= Modifier.NATIVE;
			else if ("abstract".equals(bufs))
				modifiers |= Modifier.ABSTRACT;
			else if ("interface".equals(bufs))
			{
				modifiers |= Modifier.INTERFACE;
				final JavaClass[] jcarray = { parseClass(parent, modifiers)};
				return jcarray;
			}
			else if ("class".equals(bufs))
			{
				final JavaClass[] jcarray = { parseClass(parent, modifiers)};
				return jcarray;
			}
			else if ("enum".equals(bufs))
			{
				final String enumName = readToken().getString("enum name expected");
				readToken().expect('{');
				parseBody(false, null);
				final JavaClass result = new JavaClass(javaFile, parent, modifiers, true, enumName, null, Collections.<String>emptyList());

				consumer.onClass(result);
				result.setClassEndPosition(tokenizer.outputLength());
				consumer.onClassEnd(result);

				discardNextFeature(false);

				if(collect_when_blocking())
					write(getCollector());
				if(do_block())
					getCollector();

				return new JavaFeature[]{result};
			}
			else
			{
				if (parent == null)
					throw newParseException("'class' or 'interface' expected.");
				featuretype = bufs;
				break;
			}

			final Token c = readToken();
			if(!(c instanceof StringToken))
			{
				if (parent == null)
					throw newParseException("'class' or 'interface' expected.");
				else
				{
					if(c.contains('{') && modifiers == Modifier.STATIC)
					{
						// this is a static initializer
						if(collect_when_blocking())
							write(getCollector());
						flushOutbuf();
						parseBody(false, null);
						scheduleBlock(true);
						docComment = null;
						return new JavaClass[0];
					}
					else
					{
						throw newParseException("modifier expected.");
					}
				}
			}
			bufs = ((StringToken)c).value;
		}
		String featurename;

		Token c = readToken();

		if(!(c instanceof StringToken))
		{
			c.expect('('); // it's a constructor !
			featurename = featuretype;
			featuretype = null;
			if (!parent.name.equals(featurename))
				throw newParseException(
					"constructor '"
						+ featurename
						+ "' must have the classes name '"
						+ parent.name
						+ '\'');
		}
		else
		{
			featurename = ((StringToken)c).value;
			c = readToken();
		}

		if(c.contains('(')) // it's a method/constructor
		{
			final JavaBehaviour jb =
				(featuretype == null)
					? (JavaBehaviour)new JavaConstructor(parent,
						modifiers,
						featurename)
					: new JavaMethod(parent, modifiers, featuretype, featurename);
			parseBehaviour(jb);
			final JavaFeature[] jbarray = { jb };
			return jbarray;
		}
		else // it's an attribute
		{
			final JavaAttribute ja =
				new JavaAttribute(parent, modifiers, featuretype, featurename);
			return parseAttribute(ja, c);
		}
	}

	private void parseBehaviour(final JavaBehaviour jb)
		throws EndException, ParseException
	{
		Token c = readToken();
		// parsing parameter list
		while (true)
		{
			String parametertype;
			if(c.contains(')'))
			{
				break;
			}
			else if(c instanceof StringToken)
			{
				parametertype = ((StringToken)c).value;
				if ("final".equals(parametertype))
				{
					c = readToken();
					parametertype = c.getString("parameter type expected.");
				}
			}
			else
				throw newParseException("')' expected.");
			c = readToken();
			//System.out.println("addParameter("+parametertype+", "+buf.toString()+")");
			jb.addParameter(parametertype, c.getString("parameter name expected."));
			c = readToken();
			if(c.contains(','))
			{
				c = readToken();
				continue;
			}
			else if(c.contains(')'))
			{
				break;
			}
			else
				throw newParseException("')' expected.");
		}
		// parsing throws clauses
		c = readToken();
		ti : while (true)
		{
			if(c instanceof CharToken)
			{
				switch(((CharToken)c).value)
				{
					case '{' :
						if(collect_when_blocking())
						{
							write(getCollector());
							consumer.onBehaviourHeader(jb);
						}
						parseBody(false, null);
						flushOutbuf();
						break ti;
					case ';' :
						if(collect_when_blocking())
						{
							write(getCollector());
							consumer.onBehaviourHeader(jb);
						}
						flushOutbuf();
						break ti;
					default :
						throw newParseException("'{' expected.");
				}
			}
			else
			{
				if(((StringToken)c).value.equals("throws"))
				{
					do
					{
						c = readToken();
						jb.addThrowable(c.getString("class name expected."));
						c = readToken();
					}
					while(c.contains(','));
				}
				else
					throw newParseException("'throws' expected.");
			}
		}
		if(do_block())
			getCollector();
		else
		{
			//jb.print(System.out);
		}
	}

	private JavaAttribute[] parseAttribute(JavaAttribute ja, Token c)
		throws EndException, InjectorParseException
	{
		consumer.onAttributeHeader(ja);

		final ArrayList<JavaAttribute> commaSeparatedAttributes = new ArrayList<JavaAttribute>();
		commaSeparatedAttributes.add(ja);
		//if(!do_block) ja.print(System.out);

		while (true)
		{
			switch(((CharToken)c).value)
			{
				case ';' :
					if(collect_when_blocking())
						write(getCollector());
					flushOutbuf();
					if(do_block())
						getCollector();
					final JavaAttribute[] jaarray =
						new JavaAttribute[commaSeparatedAttributes.size()];
					commaSeparatedAttributes.toArray(jaarray);
					return jaarray;
				case ',' :
					c = readToken();
					ja = new JavaAttribute(ja, c.getString("attribute name expected."));
					commaSeparatedAttributes.add(ja);
					//if(!do_block) ja.print(System.out);
					c = readToken();
					break;
				case '=' :
					if(collect_when_blocking())
						write(getCollector());
					c = parseBody(true, ja);
					flushOutbuf();
					break;
				default :
					throw newParseException("';', '=' or ',' expected, but was '" + c + '\'');
			}
		}
	}

	private JavaClass parseClass(final JavaClass parent, final int modifiers)
		throws IOException, EndException, InjectorParseException
	{
		final String classname = readToken().getString("class name expected.");
		//System.out.println("class ("+Modifier.toString(modifiers)+") >"+classname+"<");

		Token imc;
		char extendsOrImplements = '-';
		String classExtends = null;
		final ArrayList<String> classImplements = new ArrayList<String>();
		while(!(imc=readToken()).contains('{'))
		{
			if(imc instanceof StringToken)
			{
				final String s = ((StringToken)imc).value;

				if("extends".equals(s))
					extendsOrImplements = 'e';
				else if("implements".equals(s))
					extendsOrImplements = 'i';
				else
				{
					switch(extendsOrImplements)
					{
						case '-':
							throw newParseException("expected extends or implements");
						case 'e':
							if(classExtends!=null)
								throw newParseException("more than one type in extends clause");
							classExtends = s;
							break;
						case 'i':
							classImplements.add(s);
							break;
						default:
							throw new RuntimeException(String.valueOf(extendsOrImplements));
					}
				}

				//System.out.println("---------------"+s+"---"+extendsOrImplements+"---------------"+classExtends+"--------"+classImplements);
			}
		}

		final JavaClass jc = new JavaClass(javaFile, parent, modifiers, false, classname, classExtends, classImplements);
		//cc.print(System.out);

		consumer.onClass(jc);
		discardNextFeature(false);

		if(collect_when_blocking())
			write(getCollector());
		if(do_block())
			getCollector();

		scheduleBlock(true);
		ml : while (true)
		{
			final Token token = readToken();
			if(token instanceof CommentToken)
			{
				final String comment = ((CommentToken)token).comment;
				if (comment.startsWith("/**"))
				{
					docComment = comment;
					//System.out.println("docComment: "+docComment);
					final boolean onDocCommentResult = consumer.onDocComment(docComment);
					discardNextFeature(!onDocCommentResult);
					if(onDocCommentResult)
						write(docComment);
					scheduleBlock(onDocCommentResult);
				}
				else
				{
					//System.out.println("comment: "+comment);
					write(comment);
					scheduleBlock(true);
				}
			}
			else if(token instanceof StringToken)
			{
				final JavaFeature[] jfarray = parseFeature(jc, (StringToken)token);
				for(final JavaFeature jf : jfarray)
					consumer.onClassFeature(jf, docComment);
				discardNextFeature(false);
				docComment = null;
				scheduleBlock(true);
			}
			else
			{
				switch(((CharToken)token).value)
				{
					case '}' :
						getCollector();
						break ml;
					case ';' :
						// javac (but not jikes) accepts semicolons on class level,
						// so do we.
						getCollector();
						break;
					case '{' :
						// this is an object initializer as defined
						// in Java Language Specification D.1.3
						if(collect_when_blocking())
							write(getCollector());
						flushOutbuf();
						parseBody(false, null);
						scheduleBlock(true);
						break;
					case '@':
						parseAnnotation();
						break;
					default :
						throw newParseException("class member expected.");
				}
			}
		}

		jc.setClassEndPosition(tokenizer.outputLength());
		consumer.onClassEnd(jc);
		return jc;
	}

	public void parseFile() throws IOException, InjectorParseException
	{
		try
		{
			Token c;
			while (true)
			{
				scheduleBlock(true);
				try
				{
					c = readToken();
				}
				catch (final EndException e)
				{
					return;
				}

				if(collect_when_blocking())
					write(getCollector());
				if(do_block())
					getCollector();

				if(c instanceof StringToken)
				{
					final String bufs = ((StringToken)c).value;
					if ("package".equals(bufs))
					{
						c = readToken();
						javaFile.setPackage(c.getString("package name expected."));
						consumer.onPackage(javaFile);
						//System.out.println("package >"+((StringToken)c).value+"<");
						c = readToken();
						c.expect(';');
					}
					else if ("import".equals(bufs))
					{
						c = readToken();
						if(!(c instanceof StringToken))
							throw newParseException("class name expected.");
						if("static".equals(((StringToken)c).value))
						{
							c = readToken();
							if(!(c instanceof StringToken))
								throw newParseException("static import expected.");
						}
						else
						{
							final String importstring = ((StringToken)c).value;
							//System.out.println("import >"+importstring+"<");
							javaFile.addImport(importstring);
							consumer.onImport(importstring);
						}
						c = readToken();
						c.expect(';');
					}
					else
						parseFeature(null, bufs);
					// null says, its a top-level class
				}
				else if(c instanceof CommentToken)
				{
					final String comment = ((CommentToken)c).comment;
					if (comment.startsWith("/**"))
					{
						docComment = comment;
						//System.out.println ("file level docComment: "+docComment);
						consumer.onFileDocComment(docComment);
						write(docComment);
						docComment = null; // Mark docComment as handled...
					}
					else
					{
						//System.out.println("comment: "+comment);
						write(comment);
					}
				}
				else
				{
					switch(((CharToken)c).value)
					{
						case '@':
							parseAnnotation();
							break;

						default :
							//System.out.println("bufc >" + c + "<");
							break;
					}
				}
			}
		}
		catch (final EndException e)
		{
			throw newParseException("Unexpected End-of-File.");
		}
	}

	private void parseAnnotation() throws EndException
	{
		readToken().getString("expected name of annotation");
		//System.out.println("---------name of annotation-------"+buf);

		final Token bracketToken = readToken();
		if(!bracketToken.contains('('))
			return; // TODO this is a bug, should push back the token

		while(!readToken().contains(')'))
			;
	}

	private ParseException newParseException(final String message)
	{
		return tokenizer.newParseException(message);
	}

	public final static boolean hasTag(final String doccomment, final String tagname)
	{
		if(doccomment==null)
			return false;

		final String s = '@' + tagname;
		final int pos = doccomment.indexOf(s);
		if(pos<0)
			return false;
		if(pos+s.length()==doccomment.length())
			return true;
		return Character.isWhitespace(doccomment.charAt(pos+s.length()));
	}

	/**
	 * @param tagname the tag name without the '@' prefix
	 * @return the first line following the tag
	 */
	public final static String findDocTagLine(final String doccomment, final String tagname)
	{
		if(doccomment==null)
			return null;

		final String s = '@' + tagname + ' ';
		int start = doccomment.indexOf(s);
		if (start < 0)
			return null;
		start += s.length();

		int end;
		li : for (end = start; end < doccomment.length(); end++)
		{
			switch (doccomment.charAt(end))
			{
				case '\n' :
				case '\r' :
				case '*' :
					break li;
			}
		}
		final String result = doccomment.substring(start, end).trim();
		//System.out.println("doctag:>"+tagname+"< >"+docComment.substring(start, end)+"<");
		return result;
	}

	public static final String removeGenerics(final String s)
	{
		final int lt = s.indexOf('<');
		//System.out.println("--------evaluate("+s+")"+lt);
		if(lt>=0)
		{
			final int gt = s.indexOf('>', lt);
			if(gt<0)
				throw new RuntimeException(s);

			//System.out.println("--------evaluate("+s+")"+gt);
			if(gt<s.length())
				return s.substring(0, lt) + s.substring(gt+1);
			else
				return s.substring(0, lt);
		}
		else
			return s;
	}

	public static final List<String> getGenerics(final String s)
	{
		final int lt = s.indexOf('<');
		if(lt>=0)
		{
			final ArrayList<String> result = new ArrayList<String>();

			final int gt = s.indexOf('>', lt);
			if(gt<0)
				throw new RuntimeException(s);

			int lastcomma = lt;
			for(int comma = s.indexOf(',', lt); comma>=0&&comma<gt; comma = s.indexOf(',', comma+1))
			{
				result.add(s.substring(lastcomma+1, comma).trim());
				lastcomma = comma;
			}
			result.add(s.substring(lastcomma+1, gt).trim());

			return result;
		}
		else
			return Collections.emptyList();
	}
}


package persistence;

/**
 * Is thrown, when a persistent modifivation violates a unique-constraint.
 */
public class ReadOnlyViolationException extends ConstraintViolationException
{
	
	public ReadOnlyViolationException()
	{
	}
	
}

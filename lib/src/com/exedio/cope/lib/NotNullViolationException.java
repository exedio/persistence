
package persistence;

/**
 * Is thrown, when a persistent modifivation violates a unique-constraint.
 */
public class NotNullViolationException extends ConstraintViolationException
{
	
	public NotNullViolationException()
	{
	}
	
}

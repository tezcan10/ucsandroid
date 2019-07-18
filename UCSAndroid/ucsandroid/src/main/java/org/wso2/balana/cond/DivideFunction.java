package org.wso2.balana.cond;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.wso2.balana.attr.AttributeValue;
import org.wso2.balana.attr.DoubleAttribute;
import org.wso2.balana.attr.IntegerAttribute;
import org.wso2.balana.ctx.EvaluationCtx;

public class DivideFunction
  extends FunctionBase
{
  /**
   * Standard identifier for the integer-divide function.
   */
  public static final String NAME_INTEGER_DIVIDE = FUNCTION_NS + "integer-divide";

  /**
   * Standard identifier for the double-divide function.
   */
  public static final String NAME_DOUBLE_DIVIDE = FUNCTION_NS + "double-divide";

  // inernal identifiers for each of the supported functions
  private static final int ID_INTEGER_DIVIDE = 0;
  private static final int ID_DOUBLE_DIVIDE = 1;

  /**
   * Creates a new <code>DivideFunction</code> object.
   *
   * @param functionName the standard XACML name of the function to be handled by this object,
   *            including the full namespace
   *
   * @throws IllegalArgumentException if the function is unknown
   */
  public DivideFunction(String functionName) {
    super(functionName, getId(functionName), getArgumentType(functionName), false, 2,
            getArgumentType(functionName), false);
  }

  /**
   * Private helper that returns the internal identifier used for the given standard function.
   */
  private static int getId(String functionName) {
    if (functionName.equals(NAME_INTEGER_DIVIDE))
      return ID_INTEGER_DIVIDE;
    else if (functionName.equals(NAME_DOUBLE_DIVIDE))
      return ID_DOUBLE_DIVIDE;
    else
      throw new IllegalArgumentException("unknown divide function " + functionName);
  }

  /**
   * Private helper that returns the type used for the given standard function. Note that this
   * doesn't check on the return value since the method always is called after getId, so we assume
   * that the function is present.
   */
  private static String getArgumentType(String functionName) {
    if (functionName.equals(NAME_INTEGER_DIVIDE))
      return IntegerAttribute.identifier;
    else
      return DoubleAttribute.identifier;
  }

  /**
   * Returns a <code>Set</code> containing all the function identifiers supported by this class.
   *
   * @return a <code>Set</code> of <code>String</code>s
   */
  public static Set getSupportedIdentifiers() {
    Set set = new HashSet();

    set.add(NAME_INTEGER_DIVIDE);
    set.add(NAME_DOUBLE_DIVIDE);

    return set;
  }

  /**
   * Evaluate the function, using the specified parameters.
   *
   * @param inputs a <code>List</code> of <code>Evaluatable</code> objects representing the
   *            arguments passed to the function
   * @param context an <code>EvaluationCtx</code> so that the <code>Evaluatable</code> objects can
   *            be evaluated
   * @return an <code>EvaluationResult</code> representing the function's result
   */
  public EvaluationResult evaluate(List inputs, EvaluationCtx context) {

    // Evaluate the arguments
    AttributeValue[] argValues = new AttributeValue[inputs.size()];
    EvaluationResult result = evalArgs(inputs, context, argValues);
    if (result != null)
      return result;

    // Now that we have real values, perform the divide operation
    // in the manner appropriate for the type of the arguments.
    switch (getFunctionId()) {
      case ID_INTEGER_DIVIDE: {
        long dividend = ((IntegerAttribute) argValues[0]).getValue();
        long divisor = ((IntegerAttribute) argValues[1]).getValue();

        if (divisor == 0) {
          result = makeProcessingError("divide by zero");
          break;
        }

        long quotient = dividend / divisor;

        result = new EvaluationResult(new IntegerAttribute(quotient));
        break;
      }
      case ID_DOUBLE_DIVIDE: {
        double dividend = ((DoubleAttribute) argValues[0]).getValue();
        double divisor = ((DoubleAttribute) argValues[1]).getValue();

        if (divisor == 0) {
          result = makeProcessingError("divide by zero");
          break;
        }

        double quotient = dividend / divisor;

        result = new EvaluationResult(new DoubleAttribute(quotient));
        break;
      }
    }

    return result;
  }
}

/* Location:
 * Qualified Name:     org.wso2.balana.cond.DivideFunction
 * Java Class Version: 5 (49.0)
 * JD-Core Version:    0.7.1
 */
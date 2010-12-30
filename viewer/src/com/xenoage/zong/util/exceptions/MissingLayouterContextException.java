package com.xenoage.zong.util.exceptions;


/**
 * This exception is thrown when an operation
 * needs the current layouter context, but
 * when it was not provided.
 *
 * @author Andreas Wenger
 */
public class MissingLayouterContextException
  extends IllegalStateException
{

}

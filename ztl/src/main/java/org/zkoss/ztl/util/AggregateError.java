package org.zkoss.ztl.util;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.lang.ArrayUtils;

/**
 * We use this class to keep multiple exceptions from executor framework.
 * So jUnit can get all of the exceptions.
 */
public class AggregateError extends Throwable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Throwable sibling;
	
	public AggregateError(Throwable self, AggregateError t) {
		super(self);
		sibling = t;
	}
	
	public AggregateError(Throwable self) {
		super(self);
		sibling = new Error("Root Error");
	}

	@Override
	public String getMessage() {
		return super.getMessage() + " | " + sibling.getMessage();
	}

	@Override
	public String getLocalizedMessage() {
		return super.getLocalizedMessage() + " | " + sibling.getLocalizedMessage();
	}

	@Override
	public String toString() {
		return super.toString() + " | " + sibling.toString();
	}

	@Override
	public void printStackTrace() {
		super.printStackTrace();
		sibling.printStackTrace();
	}

	@Override
	public void printStackTrace(PrintStream s) {
		super.printStackTrace(s);
		sibling.printStackTrace(s);
	}

	@Override
	public void printStackTrace(PrintWriter s) {
		super.printStackTrace(s);
		sibling.printStackTrace(s);
	}

	@Override
	public StackTraceElement[] getStackTrace() {
		return (StackTraceElement[]) ArrayUtils.addAll(super.getStackTrace(), sibling.getStackTrace());
	}
}

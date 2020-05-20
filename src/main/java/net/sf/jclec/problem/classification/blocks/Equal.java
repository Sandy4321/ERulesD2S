package net.sf.jclec.problem.classification.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.jclec.exprtree.fun.AbstractPrimitive;
import net.sf.jclec.exprtree.fun.ExprTreeFunction;
import net.sf.jclec.exprtree.IPrimitive;

/**
 * Primitive implementation
 * 
 * @author Alberto Cano 
 * @author Amelia Zafra
 * @author Sebastian Ventura
 * @author Jose M. Luna 
 * @author Juan Luis Olmo
 */

public class Equal extends AbstractPrimitive
{
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Argument types
	/////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = 2841972853741099901L;
	
	/** Argument types */
	
	private static final Class<?> [] ARG_TYPES = new Class<?> [] {Double.class, Double.class};
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 */
	
	public Equal()
	{
		super(ARG_TYPES,Boolean.class);
	}
	
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Execute this operation over the stack and context
	 * 
	 * @param context the ExprTreeFunction context
	 */

	public void evaluate(ExprTreeFunction context) 
	{
		Double arg1 = (Double) super.pop(context);
		Double arg2 = (Double) super.pop(context);
		
		if (arg1.compareTo(arg2) == 0) 
			super.push(context,true);
		else 
			super.push(context,false);
	}
	
	/**
	 * Default implementation of copy() return this.
	 * 
	 * {@inheritDoc}
	 */
	
	public IPrimitive copy() 
	{
		return this;
	}

	/**
	 * Default implementation of instance() return this.
	 * 
	 * {@inheritDoc}
	 */
	
	public IPrimitive instance() 
	{
		return this;
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------- Overwriting java.lang.Object methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare two objects
	 * 
	 * @param other object to compare
	 * 
	 * @return result of the comparison
	 * 
	 */
	
	public boolean equals(Object other)
	{
		return other instanceof Equal;
	}	

	/**
	 * Shows this operation identification
	 * 
	 * @return =
	 */
	
	public String toString()
	{
		return "=";
	}
	
	public List<Float> toGPUcode()
	{
		return new ArrayList<Float>(Arrays.asList(9.0f, 1.0f));
	}
}

package net.sf.jclec.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Date;
import java.util.List;
import java.util.Comparator;

import net.sf.jclec.IFitness;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IAlgorithmListener;

import net.sf.jclec.algorithm.PopulationAlgorithm;
import net.sf.jclec.problem.classification.evolutionarylearner.EvolutionaryRuleLearnerAlgorithm;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * This class is a listener for PopulationAlgorithms, that performs a report of 
 * the actual population. This report consists on ...
 * 
 * @author Sebastian Ventura
 */

public class PopulationReporter implements IAlgorithmListener, IConfigure 
{
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/** Generated by Eclipse */

	private static final long serialVersionUID = -6866004037911080430L;

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Name of the report*/

	private String reportTitle;

	/** Report frequency */

	private int reportFrequency;

	/** Show report on console? */

	private boolean reportOnConsole; 

	/** Write report on file? */

	protected boolean reportOnFile; 

	/** Save all population individuals? */

	private boolean saveCompletePopulation;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Internal variables
	/////////////////////////////////////////////////////////////////

	/** Report file */

	protected File reportFile;

	/** Report file writer */

	protected FileWriter reportFileWriter;

	/** Directory for saving complete populations */

	protected File reportDirectory;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	public PopulationReporter() 
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////	

	// Setting and getting properties

	public final String getReportTitle() 
	{
		return reportTitle;
	}

	public final void setReportTitle(String reportTitle) 
	{
		this.reportTitle = reportTitle;
	}

	public final int getReportFrequency() 
	{
		return reportFrequency;
	}

	public final void setReportFrequency(int reportFrequency) 
	{
		this.reportFrequency = reportFrequency;
	}

	public boolean isReportOnCconsole() {
		return reportOnConsole;
	}

	public final void setReportOnCconsole(boolean reportOnCconsole) 
	{
		this.reportOnConsole = reportOnCconsole;
	}

	public final boolean isReportOnFile() 
	{
		return reportOnFile;
	}

	public final void setReportOnFile(boolean reportOnFile) 
	{
		this.reportOnFile = reportOnFile;
	}

	public final boolean isSaveCompletePopulation() 
	{
		return saveCompletePopulation;
	}

	public final void setSaveCompletePopulation(boolean saveCompletePopulation) 
	{
		this.saveCompletePopulation = saveCompletePopulation;
	}

	// IConfigure interface

	@Override
	public void configure(Configuration settings) 
	{
		// Set report title (default "untitled")
		String reportTitle = settings.getString("report-title", "untitled");
		setReportTitle(reportTitle);
		// Set report frequency (default 10 generations)
		int reportFrequency = settings.getInt("report-frequency", 10); 
		setReportFrequency(reportFrequency);
		// Set console report (default on)
		boolean reportOnConsole = settings.getBoolean("report-on-console", true);
		setReportOnCconsole(reportOnConsole);
		// Set file report (default off)
		boolean reportOnFile = settings.getBoolean("report-on-file", false);
		setReportOnFile(reportOnFile);
		// Set save individuals (default false)
		boolean saveCompletePopulation = settings.getBoolean("save-complete-population", false);
		setSaveCompletePopulation(saveCompletePopulation);	
	}

	// IAlgorithmListener interface

	@Override
	public void algorithmStarted(AlgorithmEvent event) 
	{
		// Create report title for this instance
		String dateString = new Date(System.currentTimeMillis()).toString().replace(':','.');
		String actualReportTitle = reportTitle+dateString;
		// If save complete population create a directory for storing
		// individual population files 
		if (saveCompletePopulation) {
			reportDirectory = new File(actualReportTitle);
			if (!reportDirectory.mkdir()) {
				throw new RuntimeException("Error creating report directory");
			}
		}
		// If report is stored in a text file, create report file
		if (reportOnFile) {
			reportFile = new File(actualReportTitle+".report.txt");
			try {
				reportFileWriter = new FileWriter(reportFile);
				reportFileWriter.flush();
				reportFileWriter.write(dateString+"\n");
			} 
			catch (IOException e) {
				e.printStackTrace();
			}			
		}
		// Do an iteration report
		doIterationReport((PopulationAlgorithm) event.getAlgorithm(), true);
	}

	@Override
	public void iterationCompleted(AlgorithmEvent event)
	{
		doIterationReport((PopulationAlgorithm) event.getAlgorithm(), false);
	}

	@Override
	public void algorithmFinished(AlgorithmEvent event) 
	{
		// Do last generation report
		doIterationReport((PopulationAlgorithm) event.getAlgorithm(), true);
		// Close report file if necessary
		if (reportOnFile  && reportFile != null) {
			try {
				reportFileWriter.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void algorithmTerminated(AlgorithmEvent e) {
		// TODO Auto-generated method stub

	}

	// java.lang.Object methods

	@Override
	public boolean equals(Object other)
	{
		if (other instanceof PopulationReporter) {
			PopulationReporter cother = (PopulationReporter) other;
			EqualsBuilder eb = new EqualsBuilder();
			// reportTitle
			eb.append(reportTitle, cother.reportTitle);
			// reportFrequency
			eb.append(reportFrequency, cother.reportFrequency);
			// reportOnConsole
			eb.append(reportOnConsole, cother.reportOnConsole);
			// reportOnFile
			eb.append(reportOnFile, cother.reportOnFile);
			// saveCompletePopulation
			eb.append(saveCompletePopulation, cother.saveCompletePopulation);			
			return eb.isEquals();
		}
		else {
			return false;
		}
	}

	protected void doIterationReport(PopulationAlgorithm algorithm, boolean force)
	{
		// Fitness comparator
		Comparator<IFitness> comparator = algorithm.getEvaluator().getComparator();

		// Actual generation
		int generation = algorithm.getGeneration();

		// Check if this is correct generation
		if (!force && generation%reportFrequency != 0) {
			return;
		}
		
		// Population individuals
		List<IIndividual>[] inhabitants = ((EvolutionaryRuleLearnerAlgorithm) algorithm).getSolutions();


		/*System.out.println("Generation " + generation);
		for(int i = 0; i < inhabitants.length; i++)
		{
			System.out.println("Class " + i);
			
			for(IIndividual ind : inhabitants[i])
				System.out.println(((SyntaxTreeRuleIndividual) ind).getPhenotype().toString(((EvolutionaryRuleLearnerAlgorithm) algorithm).getContext()) + " " + ((SimpleValueFitness) ind.getFitness()).getValue());
		}*/
	}
}

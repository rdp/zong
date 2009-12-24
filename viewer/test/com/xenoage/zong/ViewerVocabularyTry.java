package com.xenoage.zong;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import com.xenoage.util.FileTools;
import com.xenoage.util.language.Lang;
import com.xenoage.util.language.Language;
import com.xenoage.util.language.VocByString;
import com.xenoage.util.language.VocabularyID;
import com.xenoage.zong.app.language.Voc;
import com.xenoage.zong.commands.Command;
import com.xenoage.zong.util.SubclassCollector;


/**
 * This class contains tests for the language packages.
 * 
 * The German package ("en") is defined as the reference package,
 * which should be complete (this is also tested here).
 * 
 * The tests check:
 * <ul>
 * 	<li>the usage of the defined vocabulary items in the program:</li>
 * 	<ul>
 * 		<li>each word in the {@link Voc} class must be referenced at least one time
 * 			by looking at all java files in the src/viewer folder</li>
 * 	</ul>
 *	<li>the reference package:</li>
 *	<ul>
 *		<li>for each word from {@link Voc}: is it defined in the reference package?</li>
 *		<li>for each word from the reference package: is it defined in {@link Voc}
 *			(some packages are ignored, see <code>testReferenceMinimality</code>)</li>
 *	</ul>
 *	<li>all language packs:</li>
 *	<ul>
 *		<li>for each word from the reference package: is it defined in this package?</li>
 *		<li>for each word from this package package: is it defined in the reference package?</li>
 *		<li>for each {@link Command} there must be a name</li>
 *	</ul>
 * </ul>
 * 
 * The result (including missing or unneeded words) is written to the
 * VocabularyTestResult.txt file.
 * 
 * @author Andreas Wenger
 */
public class ViewerVocabularyTry
{
	
	
	private String referenceID = "en";
	private String[] allIDs = new String[]{"de", "en"};
	
	private Language referencePackage = null;
	private Language[] allPackages = new Language[allIDs.length];
	
	private PrintStream logFile = null;
	
	
	public static void main(String... args)
		throws Exception
	{
		ViewerVocabularyTry test = new ViewerVocabularyTry();
		test.setUp();
		test.testAllVocWordsUsed();
		test.testReferenceCompleteness();
		test.testReferenceMinimality();
		test.testOtherCompleteness();
		test.testOtherMinimality();
		test.testCommandNames();
		test.close();
	}
	
	
	public void setUp()
		throws Exception
	{
		//open file for logging
		logFile = new PrintStream("ViewerVocabularyTryResult.txt", "UTF-8");
		//load packages
		referencePackage = new Language(Lang.defaultLangPath, referenceID, null);
		for (int i = 0; i < allPackages.length; i++)
		{
			allPackages[i] = new Language(Lang.defaultLangPath, allIDs[i], null);
		}
	}
	
	
	/**
	 * Gets the used vocabulary items of the Zong! Viewer.
	 */
	private LinkedList<VocabularyID> getAllVocabularyIDs()
	{
		LinkedList<VocabularyID> ret = new LinkedList<VocabularyID>();
		//viewer
		for (VocabularyID voc : Voc.values())
		{
			ret.add(voc);
		}
		//shared vocab from player
		for (VocabularyID voc : com.xenoage.zong.player.language.Voc.values())
		{
			ret.add(voc);
		}
		return ret;
	}
  
	
	/**
	 * Tests, if all words of the {@link Voc} class are used within the
	 * project (see class documentation).
	 * 
	 * No real Java parsing is done. Instead, just <code>"Voc."+id</code> is searched.
	 */
	public void testAllVocWordsUsed()
	{
		//collect all Voc values
		LinkedList<VocabularyID> remaining = getAllVocabularyIDs();
		//search in all .java files in src folder
		LinkedList<File> files = FileTools.listFilesDeep(new File("src"), ".java");
		for (File file : files)
		{
			String fileContent = FileTools.readFile(file.getAbsolutePath());
			for (Iterator<VocabularyID> vocIt = remaining.iterator(); vocIt.hasNext();)
			{
				VocabularyID voc = vocIt.next();
				if (fileContent.contains("Voc." + voc))
					vocIt.remove();
			}
		}
		//print all remaining elements
		if (remaining.size() > 0)
		{
			logFile.println("\nUnneeded Voc items:");
			for (VocabularyID voc : remaining)
			{
				logFile.println(voc);
			}
		}
		//assertEquals("There are unneeded Voc items", 0, remaining.size());
	}
	
	
	/**
	 * Tests, if each word from {@link Voc} is defined in the reference package
	 * (see class documentation).
	 */
	public void testReferenceCompleteness()
		throws Exception
	{
		//check reference language
		LinkedList<Voc> unknown = new LinkedList<Voc>();
		for (Voc voc : Voc.values())
		{
			if (referencePackage.getWithNull(voc) == null)
				unknown.add(voc);
		}
		//print all unknown elements
		if (unknown.size() > 0)
		{
			logFile.println("\nMissing elements in reference language:");
			for (Voc voc : unknown)
			{
				logFile.println(voc);
			}
		}
		//assertEquals("The reference language is incomplete", 0, unknown.size());
	}
	
	
	/**
	 * Tests, if each word from the reference package is defined in {@link Voc},
	 * except some packages (see class documentation).
	 */
	public void testReferenceMinimality()
		throws Exception
	{
		//list of ignored packages
		//TODO: write another test for these packages
		Set<String> uncheckedPackages = new HashSet<String>();
		uncheckedPackages.add("Command_");
		uncheckedPackages.add("CommandList_");
		uncheckedPackages.add("Instrument_");
		uncheckedPackages.add("InstrumentGroup_");
		uncheckedPackages.add("Key_");
		uncheckedPackages.add("Strategy_");
		//collect Voc items
		Set<String> vocItems = new HashSet<String>();
		for (Voc voc : Voc.values())
		{
			vocItems.add(voc.toString());
		}
		//check reference language
		LinkedList<String> unused = new LinkedList<String>();
		for (String key : referencePackage.getAllKeys())
		{
			boolean check = true;
			for (String un : uncheckedPackages)
			{
				if (key.startsWith(un))
				{
					check = false;
					break;
				}
			}
			if (check)
			{
				//look for a enum item with the same name
				if (!vocItems.contains(key))
				{
					unused.add(key);
				}
			}
		}
		//print all unused elements
		if (unused.size() > 0)
		{
			logFile.println("\nUnneeded elements in reference language:");
			for (String voc : unused)
			{
				logFile.println(voc);
			}
		}
		//assertEquals("The reference language is not minimal.", 0, unused.size());
	}
	
	
	/**
	 * Tests, if each word from the reference package is defined in the other packages
	 * (see class documentation).
	 */
	public void testOtherCompleteness()
		throws Exception
	{
		for (Language pack : allPackages)
		{
			if (pack.getID().equals(referencePackage.getID()))
				continue;
			//check other language
			LinkedList<String> unknown = new LinkedList<String>();
			for (String key : referencePackage.getAllKeys())
			{
				if (pack.getWithNull(key) == null)
					unknown.add(key);
			}
			//print all unknown elements
			if (unknown.size() > 0)
			{
				logFile.println("\nMissing elements in language \"" + pack.getID() + "\":");
				for (String voc : unknown)
				{
					logFile.println(voc);
				}
			}
			//assertEquals("The language \"" + pack.getID() + "\" is incomplete", 0, unknown.size());
		}
	}
	
	
	/**
	 * Tests, if each word from the reference package is defined in {@link Voc},
	 * except some packages (see class documentation).
	 */
	public void testOtherMinimality()
		throws Exception
	{
		for (Language pack : allPackages)
		{
			if (pack.getID().equals(referencePackage.getID()))
				continue;
			//check other language
			LinkedList<String> unused = new LinkedList<String>();
			for (String key : pack.getAllKeys())
			{
				//look for a reference language item with the same name
				if (!referencePackage.getAllKeys().contains(key))
				{
					unused.add(key);
				}
			}
			//print all unused elements
			if (unused.size() > 0)
			{
				logFile.println("\nUnneeded elements in language \"" + pack.getID() + "\":");
				for (String voc : unused)
				{
					logFile.println(voc);
				}
			}
			//assertEquals("The language \"" + pack.getID() + "\" is not minimal.", 0, unused.size());
		}
	}
  
  
  /**
   * Tests, if each class extending the {@link Command} class has a name
   * in all languages.
   */
  public void testCommandNames()
  {
  	for (Language pack : allPackages)
		{
	    String cmdPackage = "com.xenoage.zong.commands.";
	    @SuppressWarnings("unchecked") ArrayList<Class> commands =
	      SubclassCollector.getSubclasses(Command.class);
	    //if there are less than 20 commands, something went wrong
	    if (commands.size() < 20)
	    	logFile.println("Warning: Less then 20 commands - seems to be an error!");
	    //walk through all commands
	    for (@SuppressWarnings("unchecked") Class cls : commands)
	    {
	      //test only commands in cmdPackage
	      String clsName = cls.getName();
	      if (clsName.startsWith(cmdPackage))
	      {
	        clsName = clsName.substring(cmdPackage.length());
	        //ignore test commands in "test." package
	        if (!clsName.startsWith("test."))
	        {
	          //look for name
	        	if (pack.getWithNull(new VocByString("Command", clsName)) == null)
	        	{
		        	logFile.println("Vocabulary for \"Command_" + clsName +
		          	"\" not found in language \"" + pack.getID() + "\"");
	        	}
	        }
	      }
	    }
		}
  }
  
  
  public void close()
  {
  	logFile.close();
  }
  

}
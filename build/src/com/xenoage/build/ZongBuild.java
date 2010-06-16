package com.xenoage.build;

import static com.xenoage.build.entities.Dependencies.depends;
import static com.xenoage.build.entities.Library.library;
import static com.xenoage.build.entities.Project.distProject;
import static com.xenoage.build.entities.Project.project;
import static com.xenoage.build.entities.filesets.Excludes.excludes;
import static com.xenoage.build.entities.filesets.Includes.includes;
import static com.xenoage.build.entities.filesets.JarFiles.jarFiles;
import static com.xenoage.build.entities.filesets.NativeFiles.nativeFiles;
import static com.xenoage.build.entities.filesets.OtherFiles.otherFiles;

import java.io.PrintStream;

import com.xenoage.build.entities.Library;
import com.xenoage.build.entities.Project;


/**
 * Build configuration for Zong!.
 * 
 * @author Andreas Wenger
 */
public class ZongBuild
	extends BuildConfig
{
	
	String zong_name = "Zong!";
	String zong_prefix = "zong";
	String zong_version = "i52";
	
	
	//libraries
	Library lib_gervill = library("gervill",
		jarFiles("gervill.jar"),
		otherFiles("gervill-license.txt"));
	Library lib_itext = library("itext",
		jarFiles("iText-5.0.2.jar"),
		otherFiles("iText-5.0.2-license.txt"));
	Library lib_jogl = library("jogl",
		jarFiles("jogl.jar", "gluegen-rt.jar"),
		otherFiles("jogl-license.txt"),
		nativeFiles("jogl", "gluegen-rt"));
	Library lib_tablelayout = library("tablelayout",
		jarFiles("tablelayout.jar"),
		otherFiles("tablelayout-license.txt"));
	
	//projects
	Project pro_pdlib = project("pdlib");
	Project pro_util = project("util", depends(pro_pdlib));
	Project pro_core = project("core", depends(pro_util));
	Project pro_musicxml = project("musicxml", depends(pro_core));
	Project pro_musicxmlin = project("musicxml-in", depends(pro_musicxml));
	Project pro_midiout = project("midi-out", depends(pro_core, lib_gervill));
	Project pro_player = distProject("player", depends(pro_musicxmlin, pro_midiout),
		otherFiles("readme.txt", "license.txt", "gpl.txt", "zongplayer.html", "zongplayer.jnlp", "files", "data"));
	Project pro_viewer = distProject("viewer", depends(pro_player, lib_jogl, lib_itext, lib_tablelayout),
		otherFiles("readme.txt", "license.txt", "gpl.txt", "zongviewer.html", "zongviewer.html", "files", "data"));
	
	
	@Override public String getName()
	{
		return "Zong!";
	}
	
	
	@Override public String getPrefix()
	{
		return zong_prefix;
	}
	
	
	@Override public String getVersion()
	{
		return zong_version;
	}
	
	
	@Override public void distCustomStep(Project project)
	{
		String pn = project.getName();
		if (project == pro_player)
		{
			//create playerdata.jar
			Build.createJar("dist/share/" + pn + "/lib/playerdata.jar", "" + pn, true,
				includes("data/**"), excludes());
			//create skin.jar
			Build.createJar("dist/share/" + pn + "/lib/skin.jar", "" + pn + "/skin", false,
				includes("**"), excludes());
		}
		else if (project == pro_viewer)
		{
			//create symbolpool textures
			Build.runJava(project, "com.xenoage.zong.app.symbols.rasterizer.SymbolPoolRasterizer", "default");
			//create viewerdata.jar
			Build.createJar("dist/share/" + pn + "/lib/viewerdata.jar", "" + pn, true,
				includes("data/**"), excludes("data/fonts/**", "data/gui/**", "data/test/**"));
		}
	}
	
	
	@Override public void finalStep(PrintStream out)
	{
		out.print("Creating zongfree-src package...");
		//create zongfree-src package
		Build.createBzip2(".", "**/*.sh", "dist/packages/zongfree-" + getVersion() + "-src.tar.bz2",
			excludes("dist/**", "editor/**", "test/**", ".git/**", "**/.gitignore", ".metadata/**",
				"**/bin/**", "**/dist/**"));
		out.println(" Done.");
	}
	
	
	@Override public void execute()
	{
		Build.build(new ZongBuild(), buildType, projectname);
	}

}

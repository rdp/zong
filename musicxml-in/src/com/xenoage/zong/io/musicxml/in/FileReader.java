package com.xenoage.zong.io.musicxml.in;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.xenoage.util.FileTools;
import com.xenoage.util.StreamTools;
import com.xenoage.util.filter.Filter;
import com.xenoage.util.io.IO;
import com.xenoage.zong.core.Score;
import com.xenoage.zong.io.musicxml.FileType;
import com.xenoage.zong.io.musicxml.opus.Opus;


/**
 * This class reads single or multiple {@link Score}s from
 * a given file, which may be an XML score, an XML opus or
 * a compressed MXL file.
 * 
 * @author Andreas Wenger
 */
public class FileReader
{
	
	
	/**
	 * Loads a list of scores from the given file. XML scores,
	 * XML opera and compressed MusicXML files are supported.
	 * The given filter is used to select score files.
	 */
	public static List<Score> loadScores(String path, Filter<String> scoreFileFilter)
		throws IOException
	{
		List<Score> ret = new LinkedList<Score>();
		//get directory name
		String directory = FileTools.getDirectoryName(path);
		//open stream
		BufferedInputStream bis = new BufferedInputStream(IO.openInputStreamPreservePath(path));
		StreamTools.markInputStream(bis);
		//file type
		FileType fileType = FileTypeReader.getFileType(bis);
		bis.reset();
		//open file
		if (fileType == FileType.XMLScorePartwise)
		{
			Score score = new MusicXMLScoreFileInput().read(bis, path, null);
			ret.add(score);
		}
		else if (fileType == FileType.XMLOpus)
		{
			//opus
			OpusFileInput opusInput = new OpusFileInput();
			Opus opus = opusInput.readOpusFile(bis);
			opus = opusInput.resolveOpusLinks(opus, directory);
			List<String> filePaths = scoreFileFilter.filter(opus.getScoreFilenames());
			for (String filePath : filePaths)
			{
				String relativePath = directory + "/" + filePath;
				List<Score> scores = loadScores(relativePath, scoreFileFilter);
				ret.addAll(scores);
			}
		}
		else if (fileType == FileType.Compressed)
		{
			CompressedFileInput zip = new CompressedFileInput(bis, FileTools.getTempFolder());
			List<String> filePaths = scoreFileFilter.filter(zip.getScoreFilenames());
			for (String filePath : filePaths)
			{
				Score score = zip.loadScore(filePath);
				ret.add(score);
			}
			zip.close();
		}
		return ret;
	}

	
}

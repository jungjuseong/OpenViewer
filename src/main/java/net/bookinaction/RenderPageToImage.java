package net.bookinaction;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import net.bookinaction.utils.DotPattern;

/**
 * PDF 문서의 글자 색을 바꾸어 이미리 PDF로 만든다.
 *
 */
public class RenderPageToImage {

	static Logger log = Logger.getLogger(RenderPageToImage.class.getName());
	
	public RenderPageToImage() {
		
	}

	/**
	 * This will print the documents data
	 *
	 * @throws IOException
	 *             If there is an error parsing the document.
	 */
	static final String DOC_ROOT = "e:/pdf_jobs/";
	 
	static final String[] jobs = { 
			"stn/2016_TGEF로즈리파이널봉투모의고사_문제_1회내지_1쇄",
			"stn/B형_1차_문제지_아이콘",
			"stn/공단기-봉투모의고사_1회",
			"stn/공단기-봉투모의고사_2회",
			"color_sheet",
			"stn/0617 7월호(상)-문제(2-54)ok_아이콘",
			"stn/공단기-공무원_필수암기노트_한국사",
			"stn/영단기-토익 실전모의고사 1000제 Ver.2",
	};

    static final String[] dotPatternSize = {
            "B5",
            "A3",
            "B4",
            "B4",
            "B4",
            "B4",
            "B5",
            "A4",
    };
    
	private static void makeFolder(String folder) {
		
		File outFolder = new File(folder);
		if (!outFolder.exists()) {
			if (outFolder.mkdirs())
				log.info("Output Directory is created!");
			else {
				log.severe("Failed to create directory!");
				return;
			}
		
		}
	}
		
	public static void main(String[] args) throws IOException {
			
		for (int jobNum = 0; jobNum < jobs.length; jobNum++) {

			final String output = DOC_ROOT + jobs[jobNum] + "-rendered" + ".pdf";
			makeFolder(DOC_ROOT + jobs[jobNum]);

			final String job_file = DOC_ROOT + jobs[jobNum] + ".pdf";
			PageToDotPattern.renderDocument(job_file, output);
			
			final String final_pdf = DOC_ROOT + jobs[jobNum] + "-patterned" + ".pdf";			
			PageToDotPattern.addPatternImage(output, final_pdf, dotPatternSize[jobNum]);
					
		}
	}

}
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.bookinaction;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.bookinaction.model.StripperParam;

/**
 * This is an example on how to extract text from a specific area on the PDF document.
 */
public final class ExtractTextInfo {

    /**
     * This will print the documents text in a certain area.
     *
     * @param args The command line arguments.
     * @throws IOException If there is an error parsing the document.
     */
    /*
     * float averageCharTolerance;
     * float dropThreshold;
     * float SPACE_MARGINAL_SCALE; 
     * float SAME_LINE_THRESHOLD;
     */
    final static StripperParam S_Korean = new StripperParam(7.5f, 1.2f);
    final static StripperParam S_TOEIC = new StripperParam(3.0f, 2.0f);

    static final String doc_root = "e:/pdf_jobs/";

    static final List<String> jobs = Arrays.asList(
            new String("stn/0617 7월호(상)-문제(2-54)ok_아이콘"), 
            new String("stn/B형_1차_문제지_아이콘"), //, S_TOEIC),
            new String("changbee/contents-solution/컨텐츠솔루션 국3-1 샘플-본문"), //S_Korean),
            new String("stn/영단기-토익 실전모의고사 1000제 Ver.2"), // S_TOEIC),
            new String("stn/텍스트인식_박선우P 교재 샘플"), //S_Korean),
            new String("stn/텍스트인식 및 해설강의 설정_PEET 모의고사 샘플"), // S_Korean),
            new String("stn/2016_TGEF로즈리파이널봉투모의고사_문제_1회내지_1쇄"), // S_Korean),
            new String("stn/공단기-공무원_필수암기노트_한국사"), // S_Korean),
            new String("stn/공단기-봉투모의고사_1회"), // S_Korean),
            new String("stn/공단기-봉투모의고사_2회") //, S_Korean)
    );

    static StripperParam[] stripperParamArray = { S_Korean, S_TOEIC, S_Korean, S_TOEIC, S_Korean, S_Korean, S_Korean, S_Korean, S_Korean, S_Korean };
    
    public static void main(String[] args) throws IOException  {
    	
    	TextInfoExtractor extor = new TextInfoExtractor();
    	//String source, String coord_text, StripperParam sParam 
        int i = 0;
        for (String job : jobs) {
        	String source_job = doc_root + job + ".pdf";
        	
        	extor.doTextPosition(source_job, source_job + "-text-coord.txt", stripperParamArray[i++]);
            System.out.println(job + " Done!");
        }
    }
}

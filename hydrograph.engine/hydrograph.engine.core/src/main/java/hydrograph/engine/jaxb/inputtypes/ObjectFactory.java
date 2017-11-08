
/*
 * Copyright 2017 Capital One Services, LLC and Bitwise, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hydrograph.engine.jaxb.inputtypes;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hydrograph.engine.jaxb.inputtypes package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hydrograph.engine.jaxb.inputtypes
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AvroFile }
     * 
     */
    public AvroFile createAvroFile() {
        return new AvroFile();
    }

    /**
     * Create an instance of {@link TextFileFixedWidth }
     * 
     */
    public TextFileFixedWidth createTextFileFixedWidth() {
        return new TextFileFixedWidth();
    }

    /**
     * Create an instance of {@link TextFileDelimited }
     * 
     */
    public TextFileDelimited createTextFileDelimited() {
        return new TextFileDelimited();
    }

    /**
     * Create an instance of {@link TextFileMixedScheme }
     * 
     */
    public TextFileMixedScheme createTextFileMixedScheme() {
        return new TextFileMixedScheme();
    }

    /**
     * Create an instance of {@link GenerateRecord }
     * 
     */
    public GenerateRecord createGenerateRecord() {
        return new GenerateRecord();
    }

    /**
     * Create an instance of {@link HiveTextFile }
     * 
     */
    public HiveTextFile createHiveTextFile() {
        return new HiveTextFile();
    }

    /**
     * Create an instance of {@link XmlFile }
     * 
     */
    public XmlFile createXmlFile() {
        return new XmlFile();
    }

    /**
     * Create an instance of {@link SequenceInputFile }
     * 
     */
    public SequenceInputFile createSequenceInputFile() {
        return new SequenceInputFile();
    }

    /**
     * Create an instance of {@link ParquetFile }
     * 
     */
    public ParquetFile createParquetFile() {
        return new ParquetFile();
    }

    /**
     * Create an instance of {@link Subjob }
     * 
     */
    public Subjob createSubjob() {
        return new Subjob();
    }

    /**
     * Create an instance of {@link Oracle }
     * 
     */
    public Oracle createOracle() {
        return new Oracle();
    }

    /**
     * Create an instance of {@link Teradata }
     * 
     */
    public Teradata createTeradata() {
        return new Teradata();
    }

    /**
     * Create an instance of {@link SubjobInput }
     * 
     */
    public SubjobInput createSubjobInput() {
        return new SubjobInput();
    }

    /**
     * Create an instance of {@link Redshift }
     * 
     */
    public Redshift createRedshift() {
        return new Redshift();
    }

    /**
     * Create an instance of {@link Mysql }
     * 
     */
    public Mysql createMysql() {
        return new Mysql();
    }

    /**
     * Create an instance of {@link ParquetHiveFile }
     * 
     */
    public ParquetHiveFile createParquetHiveFile() {
        return new ParquetHiveFile();
    }

    /**
     * Create an instance of {@link Sparkredshift }
     * 
     */
    public Sparkredshift createSparkredshift() {
        return new Sparkredshift();
    }

    /**
     * Create an instance of {@link AvroFile.Path }
     * 
     */
    public AvroFile.Path createAvroFilePath() {
        return new AvroFile.Path();
    }

    /**
     * Create an instance of {@link TextFileFixedWidth.Path }
     * 
     */
    public TextFileFixedWidth.Path createTextFileFixedWidthPath() {
        return new TextFileFixedWidth.Path();
    }

    /**
     * Create an instance of {@link TextFileFixedWidth.Charset }
     * 
     */
    public TextFileFixedWidth.Charset createTextFileFixedWidthCharset() {
        return new TextFileFixedWidth.Charset();
    }

    /**
     * Create an instance of {@link TextFileDelimited.Path }
     * 
     */
    public TextFileDelimited.Path createTextFileDelimitedPath() {
        return new TextFileDelimited.Path();
    }

    /**
     * Create an instance of {@link TextFileDelimited.Delimiter }
     * 
     */
    public TextFileDelimited.Delimiter createTextFileDelimitedDelimiter() {
        return new TextFileDelimited.Delimiter();
    }

    /**
     * Create an instance of {@link TextFileDelimited.Charset }
     * 
     */
    public TextFileDelimited.Charset createTextFileDelimitedCharset() {
        return new TextFileDelimited.Charset();
    }

    /**
     * Create an instance of {@link TextFileDelimited.Quote }
     * 
     */
    public TextFileDelimited.Quote createTextFileDelimitedQuote() {
        return new TextFileDelimited.Quote();
    }

    /**
     * Create an instance of {@link TextFileMixedScheme.Path }
     * 
     */
    public TextFileMixedScheme.Path createTextFileMixedSchemePath() {
        return new TextFileMixedScheme.Path();
    }

    /**
     * Create an instance of {@link TextFileMixedScheme.Charset }
     * 
     */
    public TextFileMixedScheme.Charset createTextFileMixedSchemeCharset() {
        return new TextFileMixedScheme.Charset();
    }

    /**
     * Create an instance of {@link TextFileMixedScheme.Quote }
     * 
     */
    public TextFileMixedScheme.Quote createTextFileMixedSchemeQuote() {
        return new TextFileMixedScheme.Quote();
    }

    /**
     * Create an instance of {@link GenerateRecord.RecordCount }
     * 
     */
    public GenerateRecord.RecordCount createGenerateRecordRecordCount() {
        return new GenerateRecord.RecordCount();
    }

    /**
     * Create an instance of {@link HiveTextFile.Delimiter }
     * 
     */
    public HiveTextFile.Delimiter createHiveTextFileDelimiter() {
        return new HiveTextFile.Delimiter();
    }

    /**
     * Create an instance of {@link HiveTextFile.Quote }
     * 
     */
    public HiveTextFile.Quote createHiveTextFileQuote() {
        return new HiveTextFile.Quote();
    }

    /**
     * Create an instance of {@link XmlFile.Path }
     * 
     */
    public XmlFile.Path createXmlFilePath() {
        return new XmlFile.Path();
    }

    /**
     * Create an instance of {@link XmlFile.AbsoluteXPath }
     * 
     */
    public XmlFile.AbsoluteXPath createXmlFileAbsoluteXPath() {
        return new XmlFile.AbsoluteXPath();
    }

    /**
     * Create an instance of {@link XmlFile.RootTag }
     * 
     */
    public XmlFile.RootTag createXmlFileRootTag() {
        return new XmlFile.RootTag();
    }

    /**
     * Create an instance of {@link XmlFile.RowTag }
     * 
     */
    public XmlFile.RowTag createXmlFileRowTag() {
        return new XmlFile.RowTag();
    }

    /**
     * Create an instance of {@link XmlFile.Charset }
     * 
     */
    public XmlFile.Charset createXmlFileCharset() {
        return new XmlFile.Charset();
    }

    /**
     * Create an instance of {@link SequenceInputFile.Path }
     * 
     */
    public SequenceInputFile.Path createSequenceInputFilePath() {
        return new SequenceInputFile.Path();
    }

    /**
     * Create an instance of {@link ParquetFile.Path }
     * 
     */
    public ParquetFile.Path createParquetFilePath() {
        return new ParquetFile.Path();
    }

    /**
     * Create an instance of {@link Subjob.Path }
     * 
     */
    public Subjob.Path createSubjobPath() {
        return new Subjob.Path();
    }

}

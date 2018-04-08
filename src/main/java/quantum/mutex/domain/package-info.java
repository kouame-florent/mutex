/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


@AnalyzerDefs({
    @AnalyzerDef(name = "ngram",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
            @TokenFilterDef(factory = LowerCaseFilterFactory.class),
            @TokenFilterDef(factory = ElisionFilterFactory.class),
            @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
            @TokenFilterDef(factory = NGramFilterFactory.class,
                    params = {
                        @Parameter(name = "minGramSize",value = "3"),
                        @Parameter(name = "maxGramSize",value = "3")
                    })
        }),
    @AnalyzerDef(name = "french",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
        filters = {
            @TokenFilterDef(factory = LowerCaseFilterFactory.class),
            @TokenFilterDef(factory = ElisionFilterFactory.class),
            @TokenFilterDef(
                        factory = StopFilterFactory.class,
                        params = {
                                @Parameter(name = "words", value = "stopwords.properties"),
                                @Parameter(name = "ignoreCase", value = "true")
                        }
                ),
            @TokenFilterDef(factory = ASCIIFoldingFilterFactory.class),
            @TokenFilterDef(factory = FrenchLightStemFilterFactory.class)
        }),
    @AnalyzerDef(name="phonetic",
        tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class ),
        filters = {
            @TokenFilterDef(factory = StandardFilterFactory.class),
            @TokenFilterDef(factory = StopFilterFactory.class,
                params = @Parameter(name="words", value="stopwords.properties") ),
            @TokenFilterDef(factory = PhoneticFilterFactory.class,
                params = {
                @Parameter(name="encoder", value="DoubleMetaphone"),
                @Parameter(name="inject", value="false")
            } )
        } )

})

package quantum.mutex.domain;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.fr.FrenchLightStemFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.phonetic.PhoneticFilterFactory;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.util.ElisionFilterFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;


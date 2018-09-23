/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service;


import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import quantum.mutex.common.Nothing;
import quantum.mutex.common.Result;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.MetadataDAO;



/**
 *
 * @author Florent
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MetadataService {
   
    private static final Logger LOG = Logger.getLogger(MetadataService.class.getName());
    
    @Inject MetadataDAO metadataDAO;
    
    public Result<FileInfoDTO> handle(@NotNull FileInfoDTO fileInfoDTO){

        Result<org.apache.tika.metadata.Metadata> tikaMetas = Result.of(fileInfoDTO).flatMap(fl -> getFileInfoInput.apply(fl))
                 .flatMap(in -> newTikaMetadata.apply(in));
        
        List<String> nameKeys = tikaMetas.map(m ->  Arrays.asList(m.names()))
                .getOrElse(() -> Collections.EMPTY_LIST);

        quantum.mutex.common.List<String> names = quantum.mutex.common.List.fromCollection(nameKeys);
        names.map(n -> tikaMetas.flatMap(m -> newMutextMetadata.apply(n).apply(m)));
        
        quantum.mutex.common.List<quantum.mutex.domain.Metadata> mutexMetas = quantum.mutex.common.List
                .flattenResult(names.map(n -> tikaMetas.flatMap(m -> newMutextMetadata.apply(n).apply(m))));
             
        quantum.mutex.common.List<quantum.mutex.domain.Metadata> persistedMetas = 
                quantum.mutex.common.List.flattenResult(mutexMetas.map(metadataDAO::makePersistent));
        
        fileInfoDTO.getFileMetadatas().addAll(persistedMetas.toJavaList());
        
        return tikaMetas.flatMap(tm -> getContentType.apply(tm))
                .map(ct -> provideContentType.apply(fileInfoDTO).apply(ct))
                .flatMap(fi -> getLanguage(fi))
                .map(lg ->  provideLanguage.apply(fileInfoDTO).apply(lg));
     }
     
    private final Function<FileInfoDTO,Result<InputStream>> getFileInfoInput = fileInfoDTO -> {
       return fileInfoDTO.getFilePath().flatMap(this::getInput_);
    };
    
    private Result<InputStream> getInput_(Path path){
        try {
             return Result.success(Files.newInputStream(path));
          } catch (IOException ex) {
              Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex);
              return Result.failure(ex);
          }
    }
    
    private final Function<InputStream,Result<org.apache.tika.metadata.Metadata>> newTikaMetadata = inStr -> {
        return Result.of(newTikaMetadata_(inStr));
    };
    
    private org.apache.tika.metadata.Metadata newTikaMetadata_(InputStream inputStream){
        org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
        try {
            Parser parser = new AutoDetectParser();
            parser.parse(inputStream, new BodyContentHandler(-1), metadata, new ParseContext());
            return metadata;
        } catch (IOException | SAXException | TikaException ex) {
            Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex);
            
        }finally{
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return metadata;
    }
    
    final Function<String ,Function<org.apache.tika.metadata.Metadata, Result<quantum.mutex.domain.Metadata> >> 
            newMutextMetadata = name -> meta ->{
        return Result.of(new quantum.mutex.domain.Metadata(name, meta.get(name)));
    };

    public Function<FileInfoDTO,Function<String,FileInfoDTO>> provideContentType = fileInfo -> type ->{
        fileInfo.setFileContentType(type); return fileInfo;
    };
     
    public Function<org.apache.tika.metadata.Metadata,Result<String>> getContentType = meta ->{
         return Result.success(meta.get(HttpHeaders.CONTENT_TYPE));
    };
    
    public Function<FileInfoDTO,Function<String,FileInfoDTO>> provideLanguage = fileInfo -> lang ->{
        fileInfo.setFileLanguage(lang); return fileInfo;
    };
     
    
    private Result<String> getLanguage(FileInfoDTO fileInfoDTO){
        Result<InputStream> input = getFileInfoInput.apply(fileInfoDTO);
         
        Result<LanguageDetector> detector =  retrieveLanguageProfiles.apply(Nothing.instance)
                .flatMap(l -> retrieveLangDetector.apply(l));
         
        Result<TextObject> textObject = input.flatMap(in -> retrieveSample.apply(in))
                .flatMap(s -> retrieveTextObject.apply(s));
        
        return textObject.flatMap(t -> detector.flatMap(d -> detecteLang.apply(t).apply(d)));
        
    }

     
    Function<Nothing,Result<List<LanguageProfile>>> retrieveLanguageProfiles = n -> {
         try{
            return Result.success(new LanguageProfileReader().readAllBuiltIn());
        }catch(IOException ex){
            return Result.failure(ex);
        }
    };
     
     
    Function<List<LanguageProfile>,Result<LanguageDetector>> retrieveLangDetector = langProfils ->{
        return Result.success(LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(langProfils)
                    .build());
    };
    
    Function<InputStream,Result<String>> retrieveSample = in ->{
       try{
           return Result.success(new Tika().parseToString(in));
       }catch(IOException | TikaException ex){
           return Result.failure(ex);
       }
    };
    
    Function<String,Result<TextObject>> retrieveTextObject = sampleTxt -> {
        TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        return Result.success(textObjectFactory.forText(sampleTxt));
   };
    
    Function<TextObject,Function<LanguageDetector,Result<String>> > detecteLang = txtObj -> langDetector -> {
        com.google.common.base.Optional<LdLocale> lang = langDetector.detect(txtObj);
        return Result.success(lang.or(LdLocale.fromString("fr")).getLanguage());
    };
   
}

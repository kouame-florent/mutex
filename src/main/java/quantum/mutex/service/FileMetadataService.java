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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import quantum.mutex.domain.DocumentMetadata;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.DocumentMetadataDAO;


/**
 *
 * @author Florent
 */
@Stateless
public class FileMetadataService {

    private static final Logger LOG = Logger.getLogger(FileMetadataService.class.getName());
    
    @Inject DocumentMetadataDAO metadataDAO;
    
     public FileInfoDTO handle(FileInfoDTO fileInfoDTO){
        List<DocumentMetadata> fileMetadatas = new ArrayList<>();
        try(InputStream inputStream = Files.newInputStream(fileInfoDTO.getFilePath()); ) {
            
            Metadata metadata = getMetadata(inputStream);
            Arrays.stream(metadata.names()).forEach(name -> {
                Optional<DocumentMetadata> fileMeta
                        = metadataDAO.findByAttributeNameAndAttributeValue(name,metadata.get(name));
                if(!fileMeta.isPresent()){
                    DocumentMetadata newFileMeta = new DocumentMetadata(name, metadata.get(name));
                    DocumentMetadata fm = metadataDAO.makePersistent(newFileMeta);
                    fileMetadatas.add(fm);
                }else{
                    fileMetadatas.add(fileMeta.get());
                }
                
            });
            
            fileInfoDTO.getFileMetadatas().addAll(fileMetadatas);
            fileInfoDTO.setFileContentType(getContentType(metadata));
            fileInfoDTO.setFileLanguage(getLanguage(fileInfoDTO));
            
        } catch (IOException | TikaException ex) {
            Logger.getLogger(FileMetadataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fileInfoDTO;
    }
     

    
    private Metadata getMetadata(InputStream inputStream){
        Metadata metadata = new Metadata();
        try {
           Parser parser = new AutoDetectParser();
            parser.parse(inputStream, new BodyContentHandler(-1), metadata, new ParseContext());
            
            return metadata;
        } catch (IOException | SAXException | TikaException ex) {
            Logger.getLogger(FileMetadataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metadata;
    }
    
    private String getLanguage(FileInfoDTO fileInfoDTO) throws IOException, TikaException{
        
        String language ;
        try(InputStream inputStream = Files.newInputStream(fileInfoDTO.getFilePath());){
            //load all languages:
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

           //build language detector:
            LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(languageProfiles)
                    .build();

            //create a text object factory
            TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

            //query:
            TextObject textObject = textObjectFactory.forText(new Tika().parseToString(inputStream));
            com.google.common.base.Optional<LdLocale> lang = languageDetector.detect(textObject);
            language = lang.or(LdLocale.fromString("fr")).getLanguage();
            LOG.log(Level.INFO, "-|||->>FILE LANG: {0}", language);
        }
       
        
        return language;
    }
    
     
    public String getContentType(Metadata metadata){
        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }
    
}

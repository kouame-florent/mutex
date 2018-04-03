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
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.HttpHeaders;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import quantum.mutex.dto.FileInfoDTO;
import quantum.mutex.domain.dao.MetadataDAO;


/**
 *
 * @author Florent
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MetadataService {
    
    @Resource
    private UserTransaction userTransaction;
    
   
    private static final Logger LOG = Logger.getLogger(MetadataService.class.getName());
    
    @Inject MetadataDAO metadataDAO;
    
     public FileInfoDTO handle(FileInfoDTO fileInfoDTO){
        List<quantum.mutex.domain.Metadata> fileMetadatas = new ArrayList<>();
        try(InputStream inputStream = Files.newInputStream(fileInfoDTO.getFilePath()); ) {
            
            org.apache.tika.metadata.Metadata metadata = getMetadata(inputStream);
            Arrays.stream(metadata.names()).forEach(name -> {
                try {  
                    userTransaction.begin();
//                        Optional<quantum.mutex.domain.Metadata> fileMeta
//                                = metadataDAO.findByAttributeNameAndAttributeValue(name,metadata.get(name));
//                        if(!fileMeta.isPresent()){
                            quantum.mutex.domain.Metadata newFileMeta = new quantum.mutex.domain.Metadata(name, metadata.get(name));
                            quantum.mutex.domain.Metadata fm = metadataDAO.makePersistent(newFileMeta);
                            fileMetadatas.add(fm);  
//                        }else{
//                            fileMetadatas.add(fileMeta.get());
//                        }
                    userTransaction.commit();
                } catch (NotSupportedException | SystemException | HeuristicMixedException 
                        | HeuristicRollbackException | IllegalStateException | RollbackException | SecurityException ex) {
                    Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex);
                    try {
                        if(userTransaction.getStatus() == Status.STATUS_ACTIVE ||
                                userTransaction.getStatus() == Status.STATUS_MARKED_ROLLBACK ){
                            userTransaction.rollback();
                        }
                    } catch (SystemException ex1) {
                        Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
            });
            
            fileInfoDTO.getFileMetadatas().addAll(fileMetadatas);
            fileInfoDTO.setFileContentType(getContentType(metadata));
            fileInfoDTO.setFileLanguage(getLanguage(fileInfoDTO));
            
        } catch (IOException | TikaException ex) {
            Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return fileInfoDTO;
    }
     

    
    private org.apache.tika.metadata.Metadata getMetadata(InputStream inputStream){
        org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
        try {
           Parser parser = new AutoDetectParser();
            parser.parse(inputStream, new BodyContentHandler(-1), metadata, new ParseContext());
            
            return metadata;
        } catch (IOException | SAXException | TikaException ex) {
            Logger.getLogger(MetadataService.class.getName()).log(Level.SEVERE, null, ex);
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
    
     
    public String getContentType(org.apache.tika.metadata.Metadata metadata){
        return metadata.get(HttpHeaders.CONTENT_TYPE);
    }
    
}

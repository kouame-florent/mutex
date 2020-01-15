/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.web;


import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import io.mutex.user.repository.GroupDAO;
import io.mutex.user.valueobject.ContextIdParamKey;
import io.mutex.user.valueobject.ViewState;
import io.mutex.search.valueobject.FileInfo;
import io.mutex.user.entity.Group;
import io.mutex.index.service.FileIOService;
import io.mutex.index.service.FileUploadService;


/**
 *
 * @author Florent
 */
@Named(value = "uploadBacking")
@ViewScoped
public class UploadBacking extends BaseBacking{

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(UploadBacking.class.getName());
    
    @Inject FileUploadService fileUploadService;
    @Inject FileIOService fileIOService;
    @Inject GroupDAO groupDAO;
       
    private UploadedFile file;
    private Group currentGroup; 
    private final ContextIdParamKey groupParamKey = ContextIdParamKey.GROUP_UUID;
    private String groupUUID;
    private ViewState viewState = ViewState.CREATE;

    public void viewAction(){
        LOG.log(Level.INFO, "---> CURRENT GROUP UUID: {0}",groupUUID);
        currentGroup = retriveGroup(groupUUID);
        LOG.log(Level.INFO, "---||-- > CURRENT GROUP : {0}",currentGroup);
    }
    
    private Group retriveGroup(String groupUUID){
       return Optional.of(groupUUID)
                    .flatMap(groupDAO::findById).orElseGet(() -> new Group());
    } 
   
    public void handleFileUpload( FileUploadEvent uploadEvent){
        UploadedFile uploadedFile = uploadEvent.getFile();
        LOG.log(Level.INFO, "-->> FILE NAME: {0}", uploadedFile.getFileName());
        LOG.log(Level.INFO, "-->> CONTENT TYPE: {0}", uploadedFile.getContentType());
        LOG.log(Level.INFO, "-->> FILE SIZE: {0}", uploadedFile.getSize());
    
        List<Optional<FileInfo>> fileInfos = fileIOService.buildFilesInfo(uploadedFile,currentGroup);
        LOG.log(Level.INFO, "-||||->> FILE INFO LIST SIZE: {0}", fileInfos.size());
        
//        fileInfos.stream().filter(res -> res.isFailure())
//                .map(r -> r.failureValue()).forEach(f -> addGlobalErrorMessage(f.getMessage()));
//        
        fileInfos.forEach(res -> res.ifPresent(fi -> fileUploadService.indexContent(fi)));
    }
    
    private final Consumer<Group> returnToCaller = (group) ->
            PrimeFaces.current().dialog().closeDynamic(group);

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getGroupUUID() {
        return groupUUID;
    }

    public void setGroupUUID(String groupUUID) {
        this.groupUUID = groupUUID;
    }

    public ContextIdParamKey getGroupParamKey() {
        return groupParamKey;
    }
}

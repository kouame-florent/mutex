/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewID;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dto.ContentCriteria;
import quantum.mutex.domain.dto.DateRangeCriteria;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.domain.dto.MetaFragment;
import quantum.mutex.domain.dto.OwnerCreteria;
import quantum.mutex.domain.dto.SearchCriteria;
import quantum.mutex.domain.dto.SizeRangeCriteria;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.entity.UserGroup;
import quantum.mutex.service.FileIOService;
import quantum.mutex.service.TextHandlingService;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.search.SearchMetadataService;
import quantum.mutex.util.CriteriaName;

/**
 *
 * @author Florent
 */
@Named(value = "searchMetaBacking")
@ViewScoped
public class SearchMetaBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SearchMetaBacking.class.getName());
      
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserGroupService userGroupService;
    @Inject SearchMetadataService searchMetadataService;
    @Inject InodeDAO inodeDAO;
    @Inject TextHandlingService textService;
    @Inject FileIOService fileIOService;
    
    @Getter @Setter
    private List<Group> groups;
    
    @Getter @Setter
    private List<Group> selectedGroups = new ArrayList<>();
    
    @Getter
    private Set<MetaFragment> fragments = new LinkedHashSet<>();
    
    @Getter @Setter
    private String searchText;
    
    private final Map<CriteriaName,SearchCriteria> searchCriteria = new HashMap<>();
    
    @PostConstruct
    public void init(){
        initGroups();
    }
    
    private void initGroups(){
        List<UserGroup> ugs = getUser()
            .map(u -> userGroupDAO.findByUser(u))
            .getOrElse(() -> Collections.EMPTY_LIST);
        groups = ugs.stream().map(UserGroup::getGroup)
            .collect(Collectors.toList());
    }
    
    public void search(){
        if(selectedGroups.isEmpty()){
            getUser().map(u -> userGroupService.getAllGroups(u))
                    .forEach(gps -> processSearchStack(gps));
        }else{
//           
        }
//       
    }
    
    private void processSearchStack(List<Group> groups){
        searchCriteria.clear();
        addContentCriteria(searchText);
        fragments = searchMetadataService.search(searchCriteria,groups);
    }
    
    public void complete(){
       
    }
    
    public void addContentCriteria(String searchText){
        searchCriteria.putIfAbsent(CriteriaName.CONTENT, ContentCriteria.of(searchText));
    }
    
    public void openSelectDateCriteriaDialog(){
        LOG.log(Level.INFO, "OPEN DATE CRITERIA DLG...");
        Map<String,Object> options = getDialogOptions(40,55,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.DATE_CRITERIA_DIALOG.id(), options, null);
    }
    
    public void handleSelectDateCriteriaReturn(SelectEvent event){
        LOG.log(Level.INFO, "HANDLE SELECT DATE CRITERIA RETURN...");
        DateRangeCriteria drc = (DateRangeCriteria)event.getObject();
        searchCriteria.putIfAbsent(CriteriaName.DATE_RANGE, drc);
    }
    
    public void openSelectSizeCriteriaDialog(){
        LOG.log(Level.INFO, "OPEN DATE CRITERIA DLG...");
        Map<String,Object> options = getDialogOptions(40,55,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.SIZE_CRITERIA_DIALOG.id(), options, null);
    }
    
    public void handleSelectSizeCriteriaReturn(SelectEvent event){
        LOG.log(Level.INFO, "HANDLE SELECT SIZE CRITERIA RETURN...");
        SizeRangeCriteria src = (SizeRangeCriteria)event.getObject();
        searchCriteria.putIfAbsent(CriteriaName.SIZE_RANGE, src);
    }
    
    public void openSelectOwnerCriteriaDialog(){
        LOG.log(Level.INFO, "OPEN OWNER CRITERIA DLG...");
        Map<String,Object> options = getDialogOptions(40,65,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.OWNER_CRITERIA_DIALOG.id(), options, null);
    }
    
     public void handleSelectOwnerCriteriaReturn(SelectEvent event){
        LOG.log(Level.INFO, "HANDLE SELECT OWNER CRITERIA RETURN...");
        OwnerCreteria oc = (OwnerCreteria)event.getObject();
        searchCriteria.putIfAbsent(CriteriaName.OWNER, oc);
    } 
     
    public String getFileName(String uuid){
        return inodeDAO.findById(uuid)
                .map(Inode::getFileName).getOrElse(() -> "");
    }
    
    public String sanitize(@NotNull String text){
        return textService.sanitize(text);
    }
    
    public void download(Fragment fragment){
        fileIOService.download(getFacesContext(),fragment);
    }
}

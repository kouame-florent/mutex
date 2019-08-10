/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.backing.ViewID;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.type.criterion.TextCriterion;
import quantum.mutex.domain.type.criterion.DateRangeCriterion;
import quantum.mutex.domain.type.Fragment;
import quantum.mutex.domain.type.MetaFragment;
import quantum.mutex.domain.type.criterion.OwnerCreterion;
import quantum.mutex.domain.type.criterion.SizeRangeCriterion;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.service.FileIOService;
import quantum.mutex.service.TextHandlingService;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.search.SearchMetadataService;
import quantum.mutex.util.CriteriaType;
import quantum.mutex.domain.type.criterion.SearchCriterion;

/**
 *
 * @author Florent
 */
@Named(value = "searchMetaBacking")
@ViewScoped
public class SearchMetaBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SearchMetaBacking.class.getName());
    
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
    
    private final Map<CriteriaType,Object> searchCriteria = new HashMap<>();
     
    private Result<DateRangeCriterion> dateRangeLabel = Result.empty();
    private Result<SizeRangeCriterion> sizeRangeLabel = Result.empty();
    private Result<OwnerCreterion> ownersLabel = Result.empty();
    
    @PostConstruct
    public void init(){
        groups = initGroups();
    }
    
    private List<Group> initGroups(){
        return getUser().map(userGroupService::getGroups)
                .getOrElse(() -> Collections.EMPTY_LIST);
    }
    
    public void search(){
        Result<TextCriterion> c = TextCriterion.of(searchText);
        addCriterion(searchCriteria,CriteriaType.CONTENT, c);
        fragments = searchMetadataService.search(selectedGroups,searchCriteria);
    }
    
    public void complete(){
       
    }
 
    private <T extends Map<CriteriaType,Object>> void addCriterion(T crt,
                CriteriaType type, Object src){
        crt.put(type, src);
    }
 
    public void openSelectDateCriteriaDialog(){
        LOG.log(Level.INFO, "OPEN DATE CRITERIA DLG...");
        Map<String,Object> options = getDialogOptions(40,55,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.DATE_CRITERIA_DIALOG.id(), options, null);
    }
    
    public void handleSelectDateCriteriaReturn(SelectEvent event){
        LOG.log(Level.INFO, "HANDLE SELECT DATE CRITERIA RETURN...");
        var drc = (Result<DateRangeCriterion>)event.getObject();
        dateRangeLabel = drc;
        addCriterion(searchCriteria, CriteriaType.DATE_RANGE,drc);

    }
    
    public void openSelectSizeCriteriaDialog(){
        LOG.log(Level.INFO, "OPEN DATE CRITERIA DLG...");
        Map<String,Object> options = getDialogOptions(40,55,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.SIZE_CRITERIA_DIALOG.id(), options, null);
    }
    
    public void handleSelectSizeCriteriaReturn(SelectEvent event){
        LOG.log(Level.INFO, "HANDLE SELECT SIZE CRITERIA RETURN...");
        var src = (Result<SizeRangeCriterion>)event.getObject();
        sizeRangeLabel = src;
        addCriterion(searchCriteria, CriteriaType.SIZE_RANGE,src);
    }
    
    public void openSelectOwnerCriteriaDialog(){
        LOG.log(Level.INFO, "OPEN OWNER CRITERIA DLG...");
        Map<String,Object> options = getDialogOptions(40,65,true);
        PrimeFaces.current().dialog()
                .openDynamic(ViewID.OWNER_CRITERIA_DIALOG.id(), options, null);
    }
    
     public void handleSelectOwnerCriteriaReturn(SelectEvent event){
        LOG.log(Level.INFO, "HANDLE SELECT OWNER CRITERIA RETURN...");
        var oc = (Result<OwnerCreterion>)event.getObject();
        ownersLabel = oc;
        addCriterion(searchCriteria, CriteriaType.OWNER,oc);
    } 
     
    public String getFileName(String uuid){
        return inodeDAO.findById(uuid)
                .map(Inode::getFileName).getOrElse(() -> "");
    }
    
    public String sanitize( String text){
        return textService.sanitize(text);
    }
    
    public void download(Fragment fragment){
        fileIOService.download(getFacesContext(),fragment);
    }

    public String getDateRangeLabel() {
        return dateRangeLabel.map(this::mkDateRangeLabel)
                .getOrElse(() -> "Date personnalisée");
    }

    public String getSizeRangeLabel() {
        return sizeRangeLabel.map(this::mkSizeRangeLabel)
                .getOrElse(() -> "Taille personnalisée");
    }

    public String getOwnersLabel() {
        return ownersLabel.map(this::mkOwnersLabel)
                .getOrElse(() -> "Propriétaire personnalisée");
    }
    
    private String mkDateRangeLabel(DateRangeCriterion drc){
        return  "Date entre: " 
                + LocalDateTime.ofEpochSecond(drc.startDate(), 0, ZoneOffset.UTC).toString()
                + " et "
                + LocalDateTime.ofEpochSecond(drc.endDate(), 0, ZoneOffset.UTC).toString();
    }
    
    private String mkSizeRangeLabel(SizeRangeCriterion src){
        return "Taille entre: "
                + src.minSize()  + " et " + src.maxSize();
    }
    
    private String mkOwnersLabel(OwnerCreterion oc){
         return "Propriétaires: "
                + oc.owners().stream().limit(1L).collect(Collectors.joining("..."));
    }
    
}

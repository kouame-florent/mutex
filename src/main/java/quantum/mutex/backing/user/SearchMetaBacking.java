/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dto.ContentCriteria;
import quantum.mutex.domain.dto.DateRangeCriteria;
import quantum.mutex.domain.dto.OwnerCreteria;
import quantum.mutex.domain.dto.SearchCriteria;
import quantum.mutex.domain.dto.SizeRangeCriteria;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.UserGroup;
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
   
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserGroupService userGroupService;
    @Inject SearchMetadataService searchMetadataService;
    
    @Getter @Setter
    private List<Group> groups;
    
    @Getter @Setter
    private List<Group> selectedGroups = new ArrayList<>();
    
    @Getter @Setter
    private String searchText;
    
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
           
        }
       
    }
    
    private void processSearchStack(List<Group> groups){
        LocalDateTime startDate = LocalDateTime.of(2019, Month.MAY, 10, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2019, Month.MAY, 31, 0, 0);
        
        DateRangeCriteria drc = new DateRangeCriteria(startDate, endDate);
        SizeRangeCriteria src = new SizeRangeCriteria(1_000_000, 10_000_000);
        OwnerCreteria oc = new OwnerCreteria(List.of("bart@gmail.com"));
        ContentCriteria cc = new ContentCriteria("Adobe");
        
        Map<CriteriaName,SearchCriteria> criterias = 
                Map.of(CriteriaName.CONTENT,cc,
                       CriteriaName.DATE_RANGE, drc, 
                       CriteriaName.SIZE_RANGE, src, 
                       CriteriaName.OWNER, oc);
        searchMetadataService.search(criterias,groups);
    }
    
    public void complete(){
       
    }
}

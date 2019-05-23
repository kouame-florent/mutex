/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.backing.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import quantum.functional.api.Result;
import quantum.mutex.backing.BaseBacking;
import quantum.mutex.domain.dao.GroupDAO;
import quantum.mutex.domain.entity.Inode;
import quantum.mutex.domain.dto.Fragment;
import quantum.mutex.util.QueryUtils;
import quantum.mutex.service.search.ElasticResponseHandler;
import quantum.mutex.service.search.SearchVirtualPageService;
import quantum.mutex.util.Constants;
import quantum.mutex.domain.dao.InodeDAO;
import quantum.mutex.domain.dao.InodeGroupDAO;
import quantum.mutex.domain.dao.UserGroupDAO;
import quantum.mutex.domain.dto.MutexCompletionSuggestion;
import quantum.mutex.domain.dto.MutexPhraseSuggestion;
import quantum.mutex.domain.dto.MutexTermSuggestion;
import quantum.mutex.domain.dto.VirtualPage;
import quantum.mutex.domain.entity.Group;
import quantum.mutex.domain.entity.UserGroup;
import quantum.mutex.service.FileIOService;
import quantum.mutex.service.TextHandlingService;
import quantum.mutex.service.domain.UserGroupService;
import quantum.mutex.service.search.PreviewService;
import quantum.mutex.service.search.SuggestService;

/**
 *
 * @author Florent
 */
@Named(value = "searchBacking")
@ViewScoped
public class SearchBacking extends BaseBacking implements Serializable{

    private static final Logger LOG = Logger.getLogger(SearchBacking.class.getName());
     
    @Inject SearchVirtualPageService searchVirtualPageService;
    @Inject PreviewService searchPreviewService;
    @Inject QueryUtils elasticApiUtils;
    @Inject ElasticResponseHandler responseHandler;
    @Inject InodeDAO inodeDAO;
    @Inject InodeGroupDAO inodeGroupDAO;
    @Inject GroupDAO groupDAO;
    @Inject UserGroupDAO userGroupDAO;
    @Inject UserGroupService userGroupService;
    @Inject FileIOService fileIOService;
    @Inject TextHandlingService textService;
    @Inject SuggestService suggestService;
    @Inject PreviewService previewService;
    
    @Getter 
    private List<Group> groups;// = new ArrayList<>();
   
    @Getter @Setter
    private List<Group> selectedGroups = new ArrayList<>();
    @Getter @Setter
    private List<VirtualPage> previews = new ArrayList<>();
   
    @Getter @Setter
    private Group selectedGroup;
    @Getter @Setter
    private Fragment selectedFragment;
    private String searchText;
    private Set<Fragment> fragments = new LinkedHashSet<>();
    @Getter
    private List<MutexTermSuggestion> termSuggestions = new ArrayList<>();
    @Getter
    private List<MutexPhraseSuggestion> phraseSuggestions = new ArrayList<>();
    @Getter
    private List<MutexCompletionSuggestion> completionSuggestions = new ArrayList<>();
        
    @PostConstruct
    public void init(){
       groups = initGroups();
    }
    
    private List<Group> initGroups(){
        return getUser().map(userGroupService::getGroups)
                .getOrElse(() -> Collections.EMPTY_LIST);
//        List<UserGroup> ugs = getUser()
//            .map(u -> userGroupDAO.findByUser(u))
//            .getOrElse(() -> Collections.EMPTY_LIST);
//        return ugs.stream().map(UserGroup::getGroup)
//            .collect(Collectors.toList());
    }
    
    public void search(){
       fragments = searchVirtualPageService.search(selectedGroups, searchText);
    }
    
    public void suggest(){
        termSuggestions = suggestService.suggestTerm(selectedGroups, searchText);
        phraseSuggestions = suggestService.suggestPhrase(selectedGroups, searchText);
    }
     
    public void complete(){
       completionSuggestions = suggestService.complete(selectedGroups, searchText);
//        completeTerm();
    }

    public void prewiew(Fragment fragment){
        Result<VirtualPage> rVp =
                previewService.prewiew(fragment, selectedGroups, searchText);
        previews = rVp.map(vp -> List.of(vp))
                .getOrElse(() -> Collections.EMPTY_LIST);
  
    }
    
//    private boolean hasReachThreshold(Fragment fragment){
//        return fragments.stream()
//                    .filter(fg -> fg.getInodeUUID()
//                            .equals(fragment.getInodeUUID()))
//                    .count() >= Constants.MAX_FRAGMENT_PER_FILE;
//    }
    
    public String getFileName(String uuid){
        return inodeDAO.findById(UUID.fromString(uuid))
                .map(Inode::getFileName).getOrElse(() -> "");
    }
    
    public String sanitize(@NotNull String text){
        return textService.sanitize(text);
    }
    
    public void download(Fragment fragment){
        fileIOService.download(getFacesContext(),fragment);
    }
   
    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Set<Fragment> getFragments() {
        return fragments;
    }

}

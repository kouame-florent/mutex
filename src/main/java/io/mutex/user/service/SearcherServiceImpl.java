package io.mutex.user.service;

import io.mutex.shared.service.EncryptionService;
import io.mutex.shared.service.EnvironmentUtils;
import io.mutex.shared.service.StringUtil;
import io.mutex.user.entity.Searcher;
import io.mutex.user.exception.NotMatchingPasswordAndConfirmation;
import io.mutex.user.exception.UserLoginExistException;
import io.mutex.user.valueobject.RoleName;
import io.mutex.user.valueobject.UserStatus;
import java.util.Optional;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.mutex.user.repository.SearcherDAO;
import java.util.List;

@Stateless
public class SearcherServiceImpl implements SearcherService {
    
    @Inject EnvironmentUtils envUtils;
    @Inject SearcherDAO searcherDAO;
    @Inject EncryptionService encryptionService;
    @Inject UserRoleService userRoleService;
    @Inject UserGroupService userGroupService;
        
    @Override
    public Optional<Searcher> findByUuid(@NotBlank String uuid){
        return searcherDAO.findById(uuid);
    }
    
//    @Override
//    public List<Searcher> findBySpace(Space space){
//        return searcherDAO.findBySpace(space);
//    }
//    
//    @Override
//    public List<Searcher> findAllOrderBySpace() {
//       return searcherDAO.findAllOrderBySpace();
//    }
    
    @Override
    public List<Searcher> findAllOrderByName() {
       return searcherDAO.findAll();
    }

    
    @Override
    public Optional<Searcher> create(@NotNull Searcher user) throws NotMatchingPasswordAndConfirmation, 
            UserLoginExistException{
        
        if(!arePasswordsMatching(user)){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        if(isUserWithLoginExist(user.getLogin())){
            throw  new UserLoginExistException("Ce login existe déjà");
        }
        
        Optional<Searcher> oUser = Optional.ofNullable(user).map(this::setHashedPassword)
//                    .flatMap(this::setSpace)
                    .map(u -> changeStatus(u, UserStatus.DISABLED))
                    .map(this::loginToLowerCase)
                    .flatMap(searcherDAO::makePersistent);
        
        oUser.ifPresent(this::createUserRole);
        
        return oUser;

    }
    
    private void createUserRole(Searcher user){
        userRoleService.create(user, RoleName.USER);
    }
    
    @Override
    public Optional<Searcher> update(@NotNull Searcher user) throws NotMatchingPasswordAndConfirmation{
        if(!arePasswordsMatching(user)){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        Optional<Searcher> oUser = searcherDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        
        if((oUser.isPresent() && oUser.filter(a -> a.equals(user)).isEmpty()) ){
            throw new NotMatchingPasswordAndConfirmation("Le mot de passe est different de la confirmation");
        }
        
        return searcherDAO.makePersistent(loginToLowerCase(user));
        
    }
    
//    private Optional<Searcher> setSpace(@NotNull Searcher user){
//        return envUtils.getUserSpace()
//                 .map(t -> {user.setSpace(t); return user;});
//    }
    
    private Searcher loginToLowerCase(@NotNull Searcher user){
        user.setLogin(StringUtil.lowerCaseWithoutAccent(user.getLogin()));
        return user;
    }
    
    private boolean arePasswordsMatching(@NotNull Searcher searcher){
        return searcher.getPassword()
                .equals(searcher.getConfirmPassword());
    }
    
     private boolean isUserWithLoginExist(@NotBlank String login){
        return searcherDAO
                .findByLogin(StringUtil.lowerCaseWithoutAccent(login))
                .isPresent();
    }
     
    private Searcher setHashedPassword(@NotNull Searcher user) {
        user.setPassword(EncryptionService.hash(user.getPassword()));
        return user;
    };
    
    private Searcher changeStatus(@NotNull Searcher user,UserStatus status) {
        user.setStatus(status);
        return user;
    };
    
    @Override
    public void enable(@NotNull Searcher user){
      Searcher usr = changeStatus(user, UserStatus.ENABLED);
      searcherDAO.makePersistent(usr);
    }
    
    @Override
    public void disable(@NotNull Searcher user){
        Searcher usr = changeStatus(user, UserStatus.DISABLED);
        searcherDAO.makePersistent(usr);
    }
    
    @Override
    public void delete(@NotNull Searcher user) {
        deleteUsersGroups(user);
        deleteUserRoles(user);
        deleteUser(user);
    }
    
    private void deleteUsersGroups(@NotNull Searcher user){
        userGroupService.getByUser(user)
                .stream()
                .forEach(userGroupService::delete);
    }
    
    private void deleteUserRoles(@NotNull Searcher user){
        userRoleService.getByUser(user)
                .stream()
                .forEach(userRoleService::delete);
    }
    
    private void deleteUser(@NotNull Searcher user){
        searcherDAO.makeTransient(user);
    }

   
} 
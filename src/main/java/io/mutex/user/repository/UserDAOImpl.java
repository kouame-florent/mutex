/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import io.mutex.shared.repository.GenericDAOImpl;
import io.mutex.user.entity.Group;
import io.mutex.user.entity.Tenant;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import io.mutex.user.valueobject.UserStatus;
import io.mutex.shared.repository.GenericDAOImpl;


/**
 *
 * @author Florent
 */
@Stateless
public class UserDAOImpl extends GenericDAOImpl<User, String> implements UserDAO{
    
    @Inject UserGroupDAO  userGroupDAO;
    
    public UserDAOImpl() {
        super(User.class);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        TypedQuery<User> query = 
               em.createNamedQuery("User.findByLogin", User.class);
        query.setParameter("login", login);
       
        List<User> results =  query.getResultList();
        if(!results.isEmpty()){
            return Optional.of(results.get(0));
        }
        
        return Optional.empty();
    }

    @Override
    public List<User> findByTenant(Tenant tenant) {
        TypedQuery<User> query = 
               em.createNamedQuery("User.findByTenant", User.class);
        query.setParameter("tenant", tenant);
       
        return query.getResultList();
    }

    @Override
    public List<User> findAllUser(Group group) {
        return userGroupDAO.findByGroup(group).stream()
                .map(UserGroup::getUser).collect(Collectors.toList());
    }

    @Override
    public Optional<User> findWithStatus(String login, String password,UserStatus status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery criteria = cb.createQuery(User.class);
        Root<User> u = criteria.from(User.class);
        
        ParameterExpression<String> loginParam = cb.parameter(String.class);
        ParameterExpression<String> passwdParam = cb.parameter(String.class);
        ParameterExpression<UserStatus> statusParam = cb.parameter(UserStatus.class);
        
        Predicate predicate = cb.and(
                cb.equal(u.<String>get("login"), loginParam),
                cb.equal(u.<String>get("password"), passwdParam),
                cb.equal(u.<UserStatus>get("status"), statusParam)
        );
        
        criteria.select(u).where(predicate);
        
        TypedQuery<User> query = em.createQuery(criteria);
        query.setParameter(loginParam, login);
        query.setParameter(passwdParam, password);
        query.setParameter(statusParam, status);
        List<User> Optionals =  query.getResultList();
        if(!Optionals.isEmpty()){
            return Optional.of(Optionals.get(0));
        }
        return Optional.empty();

    }
    
}

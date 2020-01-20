/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.user.service;

import io.mutex.user.entity.Group;
import io.mutex.user.entity.StandardUser;
import io.mutex.user.entity.User;
import io.mutex.user.entity.UserGroup;
import io.mutex.user.valueobject.GroupType;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

/**
 *
 * @author root
 */
public interface UserGroupService {

    void associateGroups(List<Group> groups, @NotNull StandardUser user);
    long countAssociations(@NotNull User user);
    long countGroupMembers(@NotNull Group group);
    void createPrimaryUsersGroups(List<Group> groups, StandardUser user);
    List<UserGroup> findByGroup(@NotNull Group group);
    List<UserGroup> findByUser(@NotNull User user);
    List<UserGroup> findByUserAndGroupType(@NotNull StandardUser user, @NotNull GroupType groupType);
    Optional<UserGroup> findUserPrimaryGroup(@NotNull StandardUser user);
    List<Group> getAllGroups(@NotNull User user);
    List<Group> getGroups(@NotNull User user);
    void remove(@NotNull UserGroup ug);
    
}

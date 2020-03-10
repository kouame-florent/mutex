/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import io.mutex.search.valueobject.FileInfo;
import io.mutex.search.valueobject.Fragment;
import io.mutex.shared.event.GroupCreated;
import io.mutex.shared.event.GroupDeleted;
import io.mutex.user.entity.Group;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.faces.context.FacesContext;
import javax.validation.constraints.NotNull;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author florent
 */
public interface FileIOService {

    List<FileInfo> buildFilesInfo(@NotNull UploadedFile uploadedFile, @NotNull Group group);

    Optional<String> buildHash(Path path);

    Optional<Path> createGroupStoreDir(@Observes @GroupCreated Group group);

    void createHomeDir();

    void createIndexDir();

    void createStoreDir();

    void deleteGroupStoreDir(@Observes @GroupDeleted @NotNull Group group);

    void download(FacesContext facesContext, Fragment fragment);

    Path getHomeDir();

    Path getIndexDir();

    Path getInodeAbsolutePath(Group group, String fileName);

    Path getStoreDir();

    @PostConstruct void init();

    Optional<Path> writeToStore(InputStream inputStream, Group group);
    
}

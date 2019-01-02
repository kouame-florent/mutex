/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "MutexFileGroup.findByGroupAndHash",
        query = "SELECT m FROM MutexFileGroup m WHERE m.group = :group AND m.mutexFile.fileHash = :fileHash"
    ),
    
})
@Table(name = "mx_file_group")
@Entity
@Getter @Setter
public class MutexFileGroup extends BusinessEntity{
    
   @ManyToOne
   private Group group;
  
   @ManyToOne
   private MutexFile mutexFile;
   
    public MutexFileGroup() {
    }
  
    public MutexFileGroup(Group group, MutexFile mutexFile) {
        this.group = group;
        this.mutexFile = mutexFile;
    }
   
   
}

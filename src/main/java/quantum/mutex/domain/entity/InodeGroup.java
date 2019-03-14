/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Florent
 */
@NamedQueries({
    @NamedQuery(
        name = "InodeGroup.findByGroupAndHash",
        query = "SELECT ig FROM InodeGroup ig WHERE ig.group = :group AND ig.inode.fileHash = :fileHash"
    ),
    @NamedQuery(
        name = "InodeGroup.findByGroup",
        query = "SELECT ig FROM InodeGroup ig WHERE ig.group = :group"
    ),
    @NamedQuery(
        name = "InodeGroup.findByInode",
        query = "SELECT ig FROM InodeGroup ig WHERE ig.inode = :inode"
    ),
    
})
@Table(name = "mx_inode_group",uniqueConstraints =
            @UniqueConstraint(
                name = "UNQ_INODE_GROUP",
                columnNames = { "group_uuid", "inode_uuid"})
)
@Entity
@Getter @Setter
public class InodeGroup extends BusinessEntity{
    
   
   @ManyToOne
   private Group group;
  
   
   @ManyToOne
   private Inode inode;
   
    public InodeGroup() {
    }
  
    public InodeGroup(Group group, Inode inode) {
        this.group = group;
        this.inode = inode;
    }
   
   
}

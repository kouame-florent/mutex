/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantum.mutex.service.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;

/**
 *
 * @author Florent
 * @param <Path>
 */
@Dependent
public class DirectoryVisitor<Path> extends SimpleFileVisitor<Path> {
    
    private final Set<Path> visitedPaths = new HashSet<>();

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        return super.visitFileFailed(file, exc); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
       visitedPaths.add(file);
       return FileVisitResult.CONTINUE;
    }

    public Set<Path> getVisitedPaths() {
        return visitedPaths;
    }
    
    
}

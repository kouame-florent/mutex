/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.shared.service;

import io.mutex.user.entity.Nameable;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author root
 */
public class StringUtil {
    
    public static String upperCaseWithoutAccent(String name){
       String[] parts = removeAccent(name).map(StringUtils::split)
               .orElseGet(() -> new String[]{});
      return Arrays.stream(parts).map(StringUtils::strip).map(String::toUpperCase)
               .collect(Collectors.joining(" "));
    }
    
    public static String lowerCaseWithoutAccent(String name){
       String[] parts = removeAccent(name).map(StringUtils::split)
               .orElseGet(() -> new String[]{});
      return Arrays.stream(parts).map(StringUtils::strip).map(String::toLowerCase)
               .collect(Collectors.joining(" "));
    }
    
    public static Nameable nameToUpperCase(Nameable nameable){
        String newName = upperCaseWithoutAccent(nameable.getName());
        nameable.setName(newName);
        return nameable;
    }
    
    private static Optional<String> removeAccent(String name){
       return Optional.ofNullable(org.apache.commons.lang3.StringUtils.stripAccents(name));
    }
    
}

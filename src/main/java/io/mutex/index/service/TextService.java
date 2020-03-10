/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.index.service;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author florent
 */
public interface TextService {

    List<String> distinct(List<String> texts);

    List<List<String>> partition(String text, int size);

    List<String> toList(String text);

    Optional<String> toText(List<String> texts);
    
}

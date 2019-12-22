/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.mutex.search.valueobject;




/**
 *
 * @author Florent
 */

public class MutexSuggestion {
    protected String content;
    protected float score;

    public MutexSuggestion(String content, float score) {
        this.content = content;
        this.score = score;
    }

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}
    
}

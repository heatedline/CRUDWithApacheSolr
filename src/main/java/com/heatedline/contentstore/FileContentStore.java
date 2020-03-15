package com.heatedline.contentstore;

import org.springframework.content.commons.renditions.Renderable;
import org.springframework.content.commons.repository.ContentStore;
import org.springframework.content.commons.search.Searchable;

import com.heatedline.model.File;

public interface FileContentStore extends ContentStore<File, String>, Searchable<String>, Renderable<File> {

}

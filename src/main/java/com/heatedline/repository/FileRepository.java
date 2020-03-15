package com.heatedline.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.heatedline.model.File;

@RepositoryRestResource(path = "files", collectionResourceRel = "files")
public interface FileRepository extends JpaRepository<File, Long> {

	File findByContentId(String contentId);

}
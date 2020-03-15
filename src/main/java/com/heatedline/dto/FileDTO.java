package com.heatedline.dto;

import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

public class FileDTO {

	private Long id;
	private MultipartFile[] file;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MultipartFile[] getFile() {
		return file;
	}

	public void setFile(MultipartFile[] file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "FileDTO [id=" + id + ", file=" + Arrays.toString(file) + "]";
	}

}

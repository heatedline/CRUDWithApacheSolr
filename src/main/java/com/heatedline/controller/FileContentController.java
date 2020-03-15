package com.heatedline.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heatedline.contentstore.FileContentStore;
import com.heatedline.dto.FileDTO;
import com.heatedline.model.File;
import com.heatedline.repository.FileRepository;

@RestController
public class FileContentController {

	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private FileContentStore contentStore;

	@PostMapping("/saveFile")
	public ResponseEntity<?> saveFile(@RequestBody File file) {
		file = fileRepository.save(file);
		return new ResponseEntity<File>(file, HttpStatus.OK);
	}

	@RequestMapping(value = "/setContent", method = RequestMethod.POST)
	public ResponseEntity<?> setContent(@ModelAttribute FileDTO fileDTO)
			throws IOException {

		Optional<File> f = fileRepository.findById(fileDTO.getId());
		if (f.isPresent()) {
			f.get().setMimeType(fileDTO.getFile()[0].getContentType());

			contentStore.setContent(f.get(), fileDTO.getFile()[0].getInputStream());

			// save updated content-related info
			fileRepository.save(f.get());

			return new ResponseEntity<Object>(HttpStatus.OK);
		}
		return null;
	}

	@RequestMapping(value = "/files/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<?> getContent(@PathVariable("fileId") Long id) {

		Optional<File> f = fileRepository.findById(id);
		if (f.isPresent()) {
			InputStreamResource inputStreamResource = new InputStreamResource(contentStore.getContent(f.get()));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentLength(f.get().getContentLength());
			headers.set("Content-Type", f.get().getMimeType());
			return new ResponseEntity<Object>(inputStreamResource, headers, HttpStatus.OK);
		}
		return null;
	}
	
	@GetMapping("search")
	public ResponseEntity<?> search(@RequestParam(value = "searchTerm") String searchTerm) {
		Iterable<String> objList = contentStore.search(searchTerm);
		List<File> fileList = new ArrayList<File>();
		for(String s : objList) {
			File file = fileRepository.findByContentId(s);
			fileList.add(file);
		}
		
		return new ResponseEntity<List<File>>(fileList, HttpStatus.OK);
	}

}

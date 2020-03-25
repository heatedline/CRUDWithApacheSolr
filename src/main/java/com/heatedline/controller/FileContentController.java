package com.heatedline.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.heatedline.contentstore.FileContentStore;
import com.heatedline.dto.FileDTO;
import com.heatedline.model.File;
import com.heatedline.repository.FileRepository;

/**
 * @author heatedline
 *
 */
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
	
	@GetMapping("renderToImage")
	public ResponseEntity<?> renderFileToImage(@RequestParam(value = "fileId") Long id, HttpServletResponse response) {
		try {
			System.setProperty("sun.java2d.cmm", "sun.java2d.cmm.kcms.KcmsServiceProvider");
			Optional<File> f = fileRepository.findById(id);
			if (f.isPresent()) {
				byte[] imageByteArr = IOUtils.toByteArray(contentStore.getRendition(f.get(), "image/jpg"));
				ByteArrayInputStream bis = new ByteArrayInputStream(imageByteArr);
			    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			    IOUtils.copy(bis, response.getOutputStream());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/documentFiles/{fileId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<?> getDocumentContent(@PathVariable("fileId") Long id, @RequestHeader HttpHeaders headers,  HttpServletResponse response) {
		try {
			Optional<File> f = fileRepository.findById(id);
			if (f.isPresent()) {
				byte[] content = IOUtils.toByteArray(contentStore.getContent(f.get()));
				ByteArrayInputStream bis = new ByteArrayInputStream(content);
				
				IOUtils.copy(bis, response.getOutputStream());

			    response.setContentType(f.get().getMimeType());
			    response.setHeader("Content-disposition", " filename=" + f.get().getName());

			    response.flushBuffer();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/audioVideoFiles/{fileId}", method = RequestMethod.GET)
	public ResponseEntity<ResourceRegion> getAudioVideoContent(@PathVariable("fileId") Long id, @RequestHeader HttpHeaders headers) {
		try {
			Optional<File> f = fileRepository.findById(id);
			if (f.isPresent()) {
				byte[] content = IOUtils.toByteArray(contentStore.getContent(f.get())); 
				ByteArrayResource byteArrayResource = new ByteArrayResource(content);
				headers.setContentLength(f.get().getContentLength());
				headers.set("Content-Type", f.get().getMimeType());
				ResourceRegion region = resourceRegion(byteArrayResource, headers);
				return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
						.contentType(MediaTypeFactory
								.getMediaType(byteArrayResource)
								.orElse(MediaType.APPLICATION_OCTET_STREAM))
						.body(region);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private ResourceRegion resourceRegion(ByteArrayResource byteArrayResource, HttpHeaders headers) throws IOException {
		long contentLength = byteArrayResource.contentLength();
		if (headers.getRange() != null) {
			long start = headers.getRange().get(0).getRangeStart(contentLength);
			long end = headers.getRange().get(0).getRangeEnd(contentLength);
			long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1);
			return new ResourceRegion(byteArrayResource, start, rangeLength);
		} else {
			long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
			return new ResourceRegion(byteArrayResource, 0, rangeLength);
		}
	}

	
	@GetMapping("/getFiles")
	public ResponseEntity<?> getFiles() {
		List<File> fileListOutput = new ArrayList<File>();
		Iterable<File> fileList = fileRepository.findAll();
		for(File file : fileList) {
			fileListOutput.add(file);
		}
		
		return new ResponseEntity<List<File>>(fileListOutput, HttpStatus.OK);
	}
	
	@GetMapping("delete")
	public ResponseEntity<?> delete(@RequestParam(value = "fileId") Long fileId) {
		String status = "";
		Optional<File> f = fileRepository.findById(fileId);
		if (f.isPresent()) {
			fileRepository.delete(f.get());
			contentStore.unsetContent(f.get());
			status = "deleted";
		}
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
	
	@GetMapping("search")
	public ResponseEntity<?> search(@RequestParam(value = "searchTerm") String searchTerm) {
		File file = null;
		Iterable<String> objList = contentStore.search(searchTerm);
		List<File> fileList = new ArrayList<File>();
		for(String s : objList) {
			file = fileRepository.findByContentId(s);
			fileList.add(file);
		}
		
		if(fileList.size() == 0) {
			fileList = fileRepository.findByNameContainingIgnoreCase(searchTerm);
		}
		
		return new ResponseEntity<List<File>>(fileList, HttpStatus.OK);
	}

}

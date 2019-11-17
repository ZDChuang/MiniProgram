package com.dech.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dech.domain.Note;
import com.dech.repository.NoteRepository;

@RestController
public class NoteController {
	private static final Logger logger = LoggerFactory.getLogger(NoteController.class);

	@Autowired
	private NoteRepository noteRepository;

	@GetMapping(value = "/receive/note/add")
	public int add(@RequestParam String openId, @RequestParam String content) {

		if (openId == null || openId.equals("")) {
			logger.error("the openid is null");
			return -1;
		}
		if (content == null || content.trim().isEmpty()) {
			return -2;
		}

		Note note = new Note();
		note.setContent(content);
		note.setOpenid(openId);
		note.setStatus("A");
		note.setCategory("1");
		noteRepository.save(note);

		return 0;
	}

	@GetMapping(value = "/receive/note/findall")
	public List<Note> findAllData(@RequestParam String openid) {

		if (openid == null || openid.equals("")) {
			logger.info("the openid is null");
			return null;
		}

		List<Note> notes = noteRepository.findByOpenid(openid);
		return notes;
	}

	@GetMapping(value = "/receive/note/findfirst5")
	public List<Note> findFirst5(@RequestParam String openid) {

		if (openid == null || openid.equals("")) {
			logger.info("the openid is null");
			return null;
		}

		List<Note> notes = noteRepository.findFirst5(openid);
		return notes;
	}
}

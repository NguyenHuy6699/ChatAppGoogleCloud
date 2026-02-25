package com.cloudrun.microservicetemplate.huy.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.cloudrun.microservicetemplate.huy.constant.Paths;


@RestController
@RequestMapping(Paths.error)
public class ErrorController {
	@PostMapping
	public void handleError(@RequestParam String message, String url) {
		System.out.println(message);
		System.out.println(url);
	}
}

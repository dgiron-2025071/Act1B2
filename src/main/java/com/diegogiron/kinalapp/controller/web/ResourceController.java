package com.diegogiron.kinalapp.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ResourceController {

    @GetMapping(value = "/css/style.css", produces = "text/css")
    @ResponseBody
    public ResponseEntity<Resource> getCss() {
        Resource resource = new ClassPathResource("static/css/style.css");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/css"))
                .body(resource);
    }

    @GetMapping(value = "/images/logo.png", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> getLogo() {
        Resource resource = new ClassPathResource("static/images/logo.png");
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}
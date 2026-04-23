package com.example.AI.controller;

import com.example.AI.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class DocumentController {

    @Autowired
    private PdfService pdfService;

    @PostMapping("/upload")
    public String uploadPdf(@RequestParam("file") MultipartFile file) throws Exception {
//        pdfService.processPdf(null,file);
        return "PDF uploaded successfully";
    }
}

package com.example.AI.service;

import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.List;

@Service
public class PdfService {

    @Autowired
    private VectorStore vectorStore;

    public void processPdf(String filePath, String fromNumber) throws Exception {

        File pdfFile = new File(filePath);

        if (!pdfFile.exists()) {
            throw new RuntimeException("PDF file not found at path: " + filePath);
        }

        PagePdfDocumentReader pdfReader =
                new PagePdfDocumentReader(new FileSystemResource(pdfFile));

        List<Document> documents = pdfReader.get();

        // ✅ Split large text into chunks
        TokenTextSplitter splitter = new TokenTextSplitter(
                300,
                200,
                50,
                10000,
                true
        );


        List<Document> chunks = splitter.apply(documents);

        // ✅ Add metadata for user
        for (Document doc : chunks) {
            doc.getMetadata().put("user", fromNumber);
            doc.getMetadata().put("source", pdfFile.getName());
        }

        // ✅ Store chunks in vector db
        vectorStore.add(chunks);

        System.out.println("PDF processed and stored in Vector DB for user: " + fromNumber);
    }
}
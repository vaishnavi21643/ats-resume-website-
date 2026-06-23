package com.resumeMatcher.resume_matcher.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class PdfTextExtractor {

    /**
     * Extracts plain text from an uploaded PDF file.
     * Throws IllegalArgumentException if the file is empty or not a valid PDF.
     */
    public String extractText(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException(
                        "No text could be extracted from the PDF. It may be a scanned image without OCR.");
            }
            return text.trim();
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to read PDF file: " + e.getMessage(), e);
        }
    }
}
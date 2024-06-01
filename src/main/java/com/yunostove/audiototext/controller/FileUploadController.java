package com.yunostove.fileprocessoro.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.yunostove.fileprocessoro.model.FileInfo;
import com.yunostove.fileprocessoro.service.FileStorageService;
import com.yunostove.fileprocessoro.service.ShellService;

@Controller
public class FileUploadController {
    private FileStorageService storageService;
    private ShellService shellService;

    FileUploadController(FileStorageService storageService, ShellService shellService) {
        this.storageService = storageService;
        this.shellService = shellService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<FileInfo> fileInfos = storageService.loadAll().map(path -> {
            String filename = path.getFileName().toString();
            String url = MvcUriComponentsBuilder
                .fromMethodName(FileUploadController.class, "download", path.getFileName().toString()).build().toString();
            return new FileInfo(filename, url);
        }).collect(Collectors.toList());
        model.addAttribute("files", fileInfos);
        return "index";
    }

    @PostMapping("/upload")
    public RedirectView upload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String message = "";
        boolean error = false;
        try {
            this.storageService.save(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            error = true;
        }
        RedirectView redirectView = new RedirectView("/", true);
        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("error", error);
        return redirectView;
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> download(@PathVariable String filename) {
        Resource file = storageService.load(filename);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
            .body(file);
    }

    @PostMapping("/delete")
    public RedirectView delete(@RequestParam("file") String filename, RedirectAttributes redirectAttributes) {
        storageService.delete(filename);
        RedirectView redirectView = new RedirectView("/", true);
        redirectAttributes.addFlashAttribute("message", filename + " has been deleted.");
        redirectAttributes.addFlashAttribute("error", false);
        return redirectView;
    }

    @PostMapping("/process")
    public RedirectView process(@RequestParam("file") String filename, RedirectAttributes redirectAttributes) {
        String processFile = storageService.getFileFromName(filename);
        String audioTextOutput = shellService.executeCommand("python3 audio_to_text.py " + processFile);
        RedirectView redirectView = new RedirectView("/", true);
        if (audioTextOutput.length() > 0) {
            redirectAttributes.addFlashAttribute("audioTextOutput", audioTextOutput);
        } else {
            redirectAttributes.addFlashAttribute("message", "Failed to process audio file: " + filename);
            redirectAttributes.addFlashAttribute("error", audioTextOutput);
        }
        return redirectView;
    }

}

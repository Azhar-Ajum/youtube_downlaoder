package com.yt.down;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainController {

    @Value("${download.directory}")
    private String downloadDirectory;

    private final YoutubeDownloadService youTubeDownloadService;

    public MainController(YoutubeDownloadService youTubeDownloadService) {
        this.youTubeDownloadService = youTubeDownloadService;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/formats")
    public String showFormats(@RequestParam("url") String url, Model model) {
        try {
            List<VideoFormat> formats = youTubeDownloadService.fetchAvailableFormats(url);
            model.addAttribute("formats", formats);
            model.addAttribute("videoUrl", url);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching formats: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/download")
    public String downloadSelectedFormat(
            @RequestParam("url") String url,
            @RequestParam("formatCode") String formatCode,
            Model model
    ) {
        try {
            String savedFile = youTubeDownloadService.downloadSelectedFormat(url, formatCode, downloadDirectory);
            String downloadLink = "/downloads/" + savedFile;

            model.addAttribute("message", "Download completed successfully!");
            model.addAttribute("downloadLink", downloadLink);
            model.addAttribute("fileName", savedFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Download failed: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/download-best")
    public String downloadBest(
            @RequestParam("url") String url,
            Model model
    ) {
        try {
            String savedFile = youTubeDownloadService.downloadBest(url, downloadDirectory);
            String downloadLink = "/downloads/" + savedFile;

            model.addAttribute("message", "Download completed successfully!");
            model.addAttribute("downloadLink", downloadLink);
            model.addAttribute("fileName", savedFile);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Download failed: " + e.getMessage());
        }
        return "index";
    }

}







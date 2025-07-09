package com.yt.down;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubeDownloadService {

    @Value("${download.directory}")
    private String downloadDirectory;

    /**
     * Fetch available formats for a YouTube URL using yt-dlp.
     */
    public List<VideoFormat> fetchAvailableFormats(String url) throws Exception {
        List<VideoFormat> formats = new ArrayList<>();

        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp", "-F", url
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            boolean inFormatsSection = false;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);

                if (line.trim().startsWith("ID") && line.contains("EXT") && line.contains("RESOLUTION")) {
                    inFormatsSection = true;
                    continue;
                }

                if (inFormatsSection && !line.trim().isEmpty()) {
                    String trimmed = line.trim();
                    if (trimmed.contains("storyboard") || trimmed.contains("mhtml")) {
                        continue;
                    }

                    if (!Character.isDigit(trimmed.charAt(0))) {
                        continue;
                    }

                    String[] tokens = trimmed.split("\\s+");
                    if (tokens.length < 4) {
                        continue;
                    }

                    String formatCode = tokens[0];
                    String ext = tokens[1];
                    String resolution = tokens[2];

                    StringBuilder noteBuilder = new StringBuilder();
                    for (int i = 3; i < tokens.length; i++) {
                        noteBuilder.append(tokens[i]).append(" ");
                    }
                    String note = noteBuilder.toString().trim();

                    if (resolution.toLowerCase().contains("audio")) {
                        continue;
                    }

                    VideoFormat vf = new VideoFormat();
                    vf.setFormatCode(formatCode);
                    vf.setExtension(ext);
                    vf.setResolution(resolution);
                    vf.setNote(note);
                    formats.add(vf);
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("yt-dlp exited with code " + exitCode);
        }

        if (formats.isEmpty()) {
            throw new RuntimeException("No formats found.");
        }

        return formats;
    }
    
    
    
    public String downloadSelectedFormat(String url, String formatCode, String outputDir) throws Exception {
        String filenamePattern = "youtube_" + System.currentTimeMillis();
        String outputTemplate = outputDir + "/" + filenamePattern + ".%(ext)s";

        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-f", formatCode + "+bestaudio",            // 👈 select this video + best audio
                "--merge-output-format", "mp4",
                "--ffmpeg-location", "C:/ffmpeg/bin",       // 👈 ensure ffmpeg works
                "-o", outputTemplate,
                url
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            File mp4 = new File(outputDir, filenamePattern + ".mp4");
            if (mp4.exists()) return mp4.getName();
            throw new RuntimeException("Download completed but merged MP4 file not found.");
        } else {
            throw new RuntimeException("Download failed with exit code " + exitCode);
        }
    }

    
    
    
    
    
    
    
    
    
    
    public String downloadBest(String url, String outputDir) throws Exception {
        String filenamePattern = "youtube_" + System.currentTimeMillis();
        String outputTemplate = outputDir + "/" + filenamePattern + ".%(ext)s";

        ProcessBuilder pb = new ProcessBuilder(
                "yt-dlp",
                "-f", "bestvideo+bestaudio",
                "--merge-output-format", "mp4",
                "--ffmpeg-location", "C:/ffmpeg/bin",
                "-o", outputTemplate,
                url
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode == 0) {
            File mp4 = new File(outputDir, filenamePattern + ".mp4");
            if (mp4.exists()) return mp4.getName();
            throw new RuntimeException("Download completed but merged MP4 file not found.");
        } else {
            throw new RuntimeException("Download failed with exit code " + exitCode);
        }
    }

}
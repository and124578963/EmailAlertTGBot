package org.tgbot.service.adapters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tgbot.BotConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class EmailPythonAdapter {
    @Autowired
    private BotConfig botConfig;

    public void run() throws IOException {
        ProcessBuilder pb = new ProcessBuilder(botConfig.getPythonBin(), botConfig.getPathToMailModule());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (process.getInputStream(), StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        log.info(textBuilder.toString());
    }

}





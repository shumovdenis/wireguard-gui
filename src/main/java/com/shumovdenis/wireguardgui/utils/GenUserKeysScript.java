package com.shumovdenis.wireguardgui.utils;

import java.io.*;

public class GenUserKeysScript {

    public void executeCommands(String name) throws IOException {
        File tempScript = createTempScript(name);

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            //добавить обработку ошибок
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            tempScript.delete();
        }
    }

    private File createTempScript(String name) throws IOException {
        File tempScript = File.createTempFile("script", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
                tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println("wg genkey | tee /etc/wireguard/" + name + "_privatekey | wg pubkey | tee /etc/wireguard/" + name + "_publickey");
        printWriter.close();
        return tempScript;
    }
}


package com.shumovdenis.wireguardgui.utils;

import java.io.*;

public class GenUserKeysScript {

    public void executeCommands(String name) {
        File tempScript = createTempScript(name);

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            tempScript.delete();
        }
    }

    private File createTempScript(String username) {

        File tempScript = null;
        try {
            tempScript = File.createTempFile("script", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Writer streamWriter = null;
        try {
            streamWriter = new OutputStreamWriter(new FileOutputStream(
                    tempScript));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println("wg genkey | tee /etc/wireguard/" + username + "_privatekey | wg pubkey | tee /etc/wireguard/" + username + "_publickey");
        printWriter.close();
        return tempScript;
    }
}


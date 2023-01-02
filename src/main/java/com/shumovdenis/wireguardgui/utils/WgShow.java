package com.shumovdenis.wireguardgui.utils;

import java.io.*;

public class WgShow {

    public void executeCommands() throws IOException {
        File tempScriptWgShow = createTempScript();

        try {
            ProcessBuilder pb = new ProcessBuilder("bash", tempScriptWgShow.toString());
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            //добавить обработку ошибок
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            tempScriptWgShow.delete();
        }
    }

    private File createTempScript() throws IOException {
        File tempScript = File.createTempFile("scriptWgSow", null);

        Writer streamWriter = new OutputStreamWriter(new FileOutputStream(
                tempScript));
        PrintWriter printWriter = new PrintWriter(streamWriter);

        printWriter.println("#!/bin/bash");
        printWriter.println("systemctl status  wg-quick@wg0");
        printWriter.close();
        return tempScript;
    }
}

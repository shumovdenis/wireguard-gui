package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.utils.GenUserKeysScript;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class UserService {
    private final String FILE_PATH = "/etc/wireguard/";

    public void addUser(String username, String allowedIPs) {
        GenUserKeysScript genKeys = new GenUserKeysScript();

        try {
            genKeys.executeCommands(username);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String privateKey = null;
        String publicKey = null;

        try {
            publicKey = readFile(Path.of(FILE_PATH + username + "_publickey")).toString();
            privateKey = readFile(Path.of(FILE_PATH + username + "_privatekey")).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        User user = new User(username, allowedIPs, publicKey, privateKey);
        String peer = addPeerToConf(user.getUsername(), user.getPublicKey(), user.getAllowedIPs());
        writeFile(peer, FILE_PATH + "wg0.conf");
    }

    //чтение из консоли
    public void wgShow() {
        String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec("systemctl status  wg-quick@wg0");
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line: " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (Exception e) {
        }
    }

    public static String addPeerToConf(String username, String publicKey, String allowedIPs) {
        StringBuilder sb = new StringBuilder();
        sb.append("[Peer]\n")
                .append("#" + username + "\n")
                .append("PublicKey = " + publicKey + "\n")
                .append("AllowedIPs = " + allowedIPs + "\n\n");
        return sb.toString();
    }

    public List<String> readFile(Path path) throws IOException {
        List<String> list = Files.readAllLines(path);
        return list;
    }

    public void writeFile(String peer, String filepath) {
        try {
            Files.write(Paths.get(filepath), peer.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void deleteUser(String username) {
        try {

            File inFile = new File(FILE_PATH + "wg0.conf");

            if (!inFile.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(FILE_PATH + "wg0.conf"));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

            String line = null;

            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(username)) {
                    pw.println(line);
                    pw.flush();
                } else {
                    while (true) {
                        line = br.readLine();
                        if (line.isEmpty()) break;
                    }
                }
            }
            pw.close();
            br.close();

            if (!inFile.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            if (!tempFile.renameTo(inFile))
                System.out.println("Could not rename file");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


}

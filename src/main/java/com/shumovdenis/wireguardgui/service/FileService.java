package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.repository.UserRepository;
import com.shumovdenis.wireguardgui.utils.CreateClientConfig;
import com.shumovdenis.wireguardgui.utils.GenUserKeysScript;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

@Service
public class FileService {

    @Value("${wg.file.path}")
    private String FILE_PATH;

    @Value("${wg.server.ip}")
    private String WG_SERVER_IP;

    @Value("${wg.config.path}")
    private String WG_CONFIG_PATH;

    private final UserRepository userRepository;

    private FileService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public HashMap<String, String> createPublicAndPrivateKeys(String username) throws IOException {
        GenUserKeysScript genKeys = new GenUserKeysScript();
        genKeys.executeCommands(username);

        String publicKey = Files.readAllLines(Path.of(FILE_PATH + username + "_publickey")).toString();
        String privateKey = Files.readAllLines(Path.of(FILE_PATH + username + "_privatekey")).toString();

        publicKey = publicKey.substring(1, publicKey.length() - 1);
        privateKey = privateKey.substring(1, privateKey.length() - 1);
        HashMap<String, String> keys = new HashMap<>();
        keys.put("privateKey", privateKey);
        keys.put("publicKey", publicKey);
        return keys;
    }

    public void addUserToConfigFile(User user) throws IOException {
        String peer = peerTextBlock(user.getUsername(), user.getPublicKey(), user.getAllowedIPs());
        Files.write(Paths.get(FILE_PATH + "wg0.conf"), peer.getBytes(), StandardOpenOption.APPEND);
    }

    public void deletePeerFromConf(String username) {
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

                if (!line.trim().equals("#" + username)) {
                    pw.println(line);
                    pw.flush();
                } else {
                    // change condition
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

    public void deleteKeysFiles(String username) {
        File file1 = new File(FILE_PATH + username + "_publickey");
        File file2 = new File(FILE_PATH + username + "_privatekey");
        file1.delete();
        file2.delete();
    }

    public void downloadUserFileConfig(String username, HttpServletResponse response) {
        User user = userRepository.findUser(username);
        CreateClientConfig ccc = new CreateClientConfig();
        ccc.createUserConfig(user, WG_SERVER_IP);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + username + ".conf");

        File file = new File(WG_CONFIG_PATH + username + ".conf");

        FileInputStream fileIn = null;
        ServletOutputStream out = null;
        try {
            fileIn = new FileInputStream(file);
            out = response.getOutputStream();
            byte[] outputByte = new byte[(int) file.length()];
            while (true) {
                if (fileIn.read(outputByte, 0, (int) file.length()) == -1) break;
                out.write(outputByte, 0, (int) file.length());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String peerTextBlock(String username, String publicKey, String allowedIPs) {
        String sb = "#" + username + "\n" +
                "[Peer]\n" +
                "PublicKey = " + publicKey + "\n" +
                "AllowedIPs = " + allowedIPs + "\n\n";
        return sb;
    }


}

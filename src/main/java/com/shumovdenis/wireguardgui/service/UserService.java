package com.shumovdenis.wireguardgui.service;

import com.shumovdenis.wireguardgui.entity.User;
import com.shumovdenis.wireguardgui.repository.UserRepository;
import com.shumovdenis.wireguardgui.utils.CreateClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    @Value("${wg.file.path}")
    private String FILE_PATH;

    @Value("${wg.config.path}")
    private String WG_CONFIG_PATH;

    @Value("${wg.server.ip}")
    private String WG_SERVER_IP;
    private final UserRepository userRepository;
    private final FileService fileService;

    public UserService(UserRepository userRepository, FileService fileService) {
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    public List<User> getUsers() {
        SqlRowSet result = userRepository.getAllUsers();
        List<User> userList = new ArrayList<>();
        while (result.next()) {
            User user = new User(
                    result.getInt("id"),
                    result.getString("username"),
                    result.getString("email"),
                    result.getString("lastHandShake"),
                    result.getString("allowedIPs")
            );
            userList.add(user);
        }
        return userList;
    }


    public void addUser(String username, String allowedIPs) {
        HashMap<String, String> keys = fileService.createPublicAndPrivateKeys(username, allowedIPs);
        User user = new User(username, allowedIPs, keys.get("privateKey"), keys.get("publicKey").toString());
        fileService.addUserToConfigFile(user);
        userRepository.addUser(user);
    }

    public void deleteUser(String username) {
        userRepository.deleteUser(username);
        deletePeerFromConf(username);
        deleteKeysFiles(username);
    }

    public void downloadFile(String username, HttpServletResponse response) {
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

}

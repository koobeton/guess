package my.server.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileReaderHelper {

    public static String readTextFile(String fileName, String charset) {

        StringBuilder sb = new StringBuilder();

        try (FileInputStream fs = new FileInputStream(fileName);
             InputStreamReader sr = new InputStreamReader(fs, charset);
             BufferedReader br = new BufferedReader(sr)) {
            String string;
            while ((string = br.readLine()) != null) {
                sb.append(string);
                sb.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static byte[] readByteFile(String fileName) {

        List<Byte> list = new ArrayList<>();

        try (FileInputStream fs = new FileInputStream(fileName);
             BufferedInputStream bs = new BufferedInputStream(fs)) {
            int input;
            while ((input = bs.read()) != -1) {
                list.add((byte)input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] array = new byte[list.size()];

        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }

        return array;
    }
}

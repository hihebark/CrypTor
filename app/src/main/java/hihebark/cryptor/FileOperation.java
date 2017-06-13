package hihebark.cryptor;


import android.os.Environment;
import android.util.Base64;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class FileOperation {
    String PASSWORD_FILE;
    String SALT2 = "CryptorSALT2";
    public static byte[] IV_VECTOR = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public FileOperation(String PASSWORD_FILE){
        this.PASSWORD_FILE=PASSWORD_FILE;
    }

    public String EncryptionString(String text) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec keySpec = new SecretKeySpec(generateKey(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV_VECTOR);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] results = cipher.doFinal(text.getBytes());
        String result = Base64.encodeToString(results, Base64.NO_WRAP|Base64.DEFAULT);
        return result;

    }
    public String decryptionString(String text)throws Exception{

        byte[] encryted_bytes = Base64.decode(text, Base64.DEFAULT);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        SecretKeySpec keySpec = new SecretKeySpec(generateKey(), "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV_VECTOR);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encryted_bytes);
        String result = new String(decrypted);
        return result;
    }

    public byte[] generateKey() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] key = (PASSWORD_FILE + SALT2).getBytes("UTF-8");
        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        key = sha.digest(key);
        key = Arrays.copyOf(key, 16);
        return key;
    }

    public void EncryptionFile(String FileInputE) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException {

        FileInputStream fileinputStream = new FileInputStream(FileInputE);
        File fileE = new File(FileInputE);
        FileOutputStream fileoutputStream = new FileOutputStream(Environment.getExternalStorageDirectory()+"/Strongbox/"+
                                                                    ""+fileE.getName()+".ct");
        SecretKeySpec key = new SecretKeySpec(generateKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(fileoutputStream, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = fileinputStream.read(d)) != -1){
            cipherOutputStream.write(d, 0x0, b);
        }
        cipherOutputStream.flush();
        cipherOutputStream.close();
        fileE.delete();
        fileinputStream.close();

    }

    public void DecryptionFile(String FileInputD, String FileOutputD) throws IOException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException{

        FileInputStream fileinputStream = new FileInputStream(FileInputD);
        File fileD = new File(FileInputD);
        FileOutputStream fileoutputStream = new FileOutputStream(FileOutputD);
        SecretKeySpec key = new SecretKeySpec(generateKey(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        CipherInputStream cipherinputStream = new CipherInputStream(fileinputStream, cipher);
        int b;
        byte[] d = new byte[8];
        while ((b = cipherinputStream.read(d)) != -1){
            fileoutputStream.write(d, 0x0, b);
        }
        fileoutputStream.flush();
        fileoutputStream.close();
        if(fileD.getAbsoluteFile().toString().contains(".zip")){
            try {
                DecryptionDirectory(fileD.getAbsoluteFile().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        fileD.delete();
        cipherinputStream.close();

    }

    public void EncryptionDirectory(String FileInputE)throws Exception{

        final File Folder_to_Zip = new File(FileInputE);
        final ZipOutputStream ZipFolder = new ZipOutputStream(new FileOutputStream(Environment.getExternalStorageDirectory()
                +"/"+Folder_to_Zip.getName()+".zip"));
        CreateZipFolder(Folder_to_Zip, ZipFolder);
        ZipFolder.close();
        EncryptionFile(Environment.getExternalStorageDirectory()+"/"+Folder_to_Zip.getName()+".zip");

    }

    public void DecryptionDirectory(String FileInput) throws Exception{

        FileInputStream File_to_folder = new FileInputStream(FileInput);
        File filezip = new File(FileInput);
        ZipInputStream Folder = new ZipInputStream(new BufferedInputStream(File_to_folder));
        ZipEntry entry;
        while ((entry = Folder.getNextEntry()) != null) {
            int size;
            byte[] buffer = new byte[2048];
            FileOutputStream fileOutputStream = new FileOutputStream(entry.getName());
            BufferedOutputStream fileOutputStreamB = new BufferedOutputStream(fileOutputStream, buffer.length);
            while ((size = Folder.read(buffer, 0, buffer.length)) != -1) { fileOutputStreamB.write(buffer, 0, size); }
            fileOutputStreamB.flush();
            fileOutputStreamB.close();
        }
        filezip.delete();
        Folder.close();
    }

    static void CreateZipFolder(File Folder, ZipOutputStream ZipFile) throws IOException {
        File[] files = Folder.listFiles();
        byte[] tmpBuf = new byte[1024];
        for (int i = 0; i < files.length; i++) {
            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
            ZipFile.putNextEntry(new ZipEntry(files[i].getAbsolutePath()));
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                ZipFile.write(tmpBuf, 0, len);
            }
            ZipFile.closeEntry();
            in.close();
        }
    }
    public void writeToFile(String line) throws IOException {
        FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()+"/log.log", true);
        fos.write(line.getBytes());
        fos.flush();
        fos.close();
    }
    public ArrayList<String> readFromFile() throws IOException {
        FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory()+"/log.log");
        DataInputStream in = new DataInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String strLine;
        ArrayList<String> myData = new ArrayList<>();
        while ((strLine = br.readLine()) != null) {
            myData.add(strLine);
        }
        in.close();
        return myData;
    }
}
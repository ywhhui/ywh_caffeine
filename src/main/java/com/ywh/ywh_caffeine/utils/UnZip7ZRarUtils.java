package com.ywh.ywh_caffeine.utils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 解压带密码文件
 */
public class UnZip7ZRarUtils {
    private static final Logger logger = LoggerFactory.getLogger(UnZip7ZRarUtils.class);
    public static void main(String[] args) throws IOException {
//        String source = "D:\\ziyan\\code\\ywh_tool\\zip\\sdk技改上线.rar";
        String source = "D:\\ziyan\\code\\ywh_tool\\zip\\述职报告PPT模板【41套】.rar";
        String dest = "D:\\ziyan\\code\\ywh_tool\\zip\\1\\";
        //破解zip
//        String password = "1234";
//        boolean unZip = unZip(source, dest, password);
//        System.out.println(unZip);
        //破解rar
//        List<String> numberStr = PassWordUtil.getNumberStr(4);
//        List<String> numberStr = PassWordUtil.getNumberStr(3);
        //异步执行
//        CompletableFuture.runAsync(()->{
//            excuteTask(productTotal);
//        },executor).exceptionally(e->{
//            System.out.println("异步执行失败"+e);
//            return null;
//        });
//        List<String> numberStr = PassWordUtil.getNumberStr(5);
//        List<String> numberStr = PassWordUtil.getAlphabetStr(5);
        String zzpassword ="1";
        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 1000; i++) {
                String passwordStr = PassWordUtil.getStringRandom(6);
                boolean unRar = unRar(source, dest, passwordStr);
                if(unRar){
                    System.out.println("解密成功后退出线程："+unRar);
                    zzpassword =passwordStr;
                    break;
                }
            }
        }
        System.out.println("随机破解结束密码数："+zzpassword);

//        System.out.println("密码数："+numberStr.size());
//        for (String passwordStr:numberStr) {
//            boolean unRar = unRar(source, dest, passwordStr);
//            if(unRar){
//                System.out.println("解密成功后退出线程："+unRar);
//                break;
//            }
//        }

        //多线程破解rar
//        PasswordCrackService service = new ThreadProcessPasswordCrackServiceImpl();
//        String source = "E:\\BaiduNetdiskDownload\\test\\wzzz.zip";
//        String dest = "E:\\BaiduNetdiskDownload\\test";
//        service.run(source, dest);

    }

    /**
     * zip格式带密码解压
     * @param source   原始文件路径
     * @param dest     解压路径
     * @param password 解压文件密码(可以为空)
     */
    public static boolean unZip(String source, String dest, String password) {
        try {
            File zipFile = new File(source);
            // 首先创建ZipFile指向磁盘上的.zip文件
            ZipFile zFile = new ZipFile(zipFile);
            zFile.setFileNameCharset(StandardCharsets.UTF_8.name());
            // 解压目录
            File destDir = new File(dest);
            // 目标目录不存在时，创建该文件夹
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            if (zFile.isEncrypted()) {
                // 设置密码
                zFile.setPassword(password.toCharArray());
            }
            // 将文件抽出到解压目录(解压)
            zFile.extractAll(dest);
            List<FileHeader> headerList = zFile.getFileHeaders();
            List<File> extractedFileList = new ArrayList<>();
            for (FileHeader fileHeader : headerList) {
                if (!fileHeader.isDirectory()) {
                    extractedFileList.add(new File(destDir, fileHeader.getFileName()));
                }
            }
            File[] extractedFiles = new File[extractedFileList.size()];
            extractedFileList.toArray(extractedFiles);
            for (File f : extractedFileList) {
                System.out.println(f.getAbsolutePath() + "文件解压成功!");
            }
        } catch (ZipException e) {
            return false;
        }
        return true;
    }

    /**
     * rar格式带密码解压
     * @param sourceRarPath
     * @param destDirPath
     * @param passWord
     * @return
     */
    public static boolean unRar(String sourceRarPath, String destDirPath, String passWord) {
//        String rarDir = rootPath + sourceRarPath;
//        String outDir = rootPath + destDirPath + File.separator;
        String rarDir = sourceRarPath;
        String outDir =destDirPath;
        boolean resultStr = false;
        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            // 第一个参数是需要解压的压缩包路径，第二个参数参考JdkAPI文档的RandomAccessFile
            randomAccessFile = new RandomAccessFile(rarDir, "r");
            if (StringUtils.isNotBlank(passWord))
                inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), passWord);
            else
                inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));

            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                final int[] hash = new int[]{0};
                if (!item.isFolder()) {
                    ExtractOperationResult result;
                    final long[] sizeArray = new long[1];

                    File outFile = new File(outDir + item.getPath());
                    File parent = outFile.getParentFile();
                    if ((!parent.exists()) && (!parent.mkdirs())) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(passWord)) {
                        result = item.extractSlow(data -> {
                            try {
                                IOUtils.write(data, new FileOutputStream(outFile, true));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            sizeArray[0] += data.length;
                            return data.length; // Return amount of consumed
                        }, passWord);
                    } else {
                        result = item.extractSlow(data -> {
                            try {
                                IOUtils.write(data, new FileOutputStream(outFile, true));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            hash[0] ^= Arrays.hashCode(data); // Consume data
                            sizeArray[0] += data.length;
                            return data.length; // Return amount of consumed
                        });
                    }
                    if (result == ExtractOperationResult.OK) {
//                        log.error("解压rar成功...." + String.format("%9X | %10s | %s", hash[0], sizeArray[0], item.getPath()));
//                        System.out.println("解压rar成功的密码---"+passWord);
                        logger.info("共多少密码 解压rar成功的密码 passWord：{}",passWord);
                        resultStr = true;
                    } else if (StringUtils.isNotBlank(passWord)) {
//                        log.error("解压rar成功：密码错误或者其他错误...." + result);
//                        System.out.println("密码错误或者其他错误:"+passWord+"--"+result);
//                        return "password";
                        return false;
                    } else {
//                        return "rar error";
                        return false;
                    }
                }
            }
        } catch (Exception e) {
//            log.error("unRar error", e);
//            System.out.println(e+"--"+passWord);
//            return e.getMessage();
            logger.info("inArchive binfa e1,e:{},passWord:{}",e,passWord);
            return false;
        }
//        finally {
//            try {
//                inArchive.close();
//                randomAccessFile.close();
//            } catch (Exception e) {
//                logger.info("inArchive binfa2 报空,passWord:{}",passWord);
//            }
//        }
        return resultStr;
    }

    /**
     * 7z解压带密码
     * @param rootPath
     * @param sourceRarPath
     * @param destDirPath
     * @param passWord
     * @return
     */
    private static String un7z(String rootPath, String sourceRarPath, String destDirPath, String passWord) {
        try {
            File srcFile = new File(rootPath + sourceRarPath);//获取当前压缩文件
            // 判断源文件是否存在
            if (!srcFile.exists()) {
                throw new Exception(srcFile.getPath() + "所指文件不存在");
            }
            //开始解压
            SevenZFile zIn = null;
            if (StringUtils.isNotBlank(passWord)) {
                zIn = new SevenZFile(srcFile, passWord.toCharArray());
            }  else {
                zIn = new SevenZFile(srcFile);
            }
            SevenZArchiveEntry entry = null;
            File file = null;
            while ((entry = zIn.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    file = new File(rootPath + destDirPath, entry.getName());
                    if (!file.exists()) {
                        new File(file.getParent()).mkdirs();//创建此文件的上级目录
                    }
                    OutputStream out = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(out);
                    int len = -1;
                    byte[] buf = new byte[1024];
                    while ((len = zIn.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    // 关流顺序，先打开的后关闭
                    bos.close();
                    out.close();
                }
            }
        } catch (Exception e) {
//            log.error("un7z is error", e);
            return e.getMessage();
        }
        return "";
    }
}

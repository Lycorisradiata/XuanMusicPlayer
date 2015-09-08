package com.jw.cool.xuanmusicplayer.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.List;


//import org.apache.http.util.EncodingUtils;

public class HandlerFile {
	static String SDCardPath;
	static{
		SDCardPath = HandlerSDCard.getSDCardPath();
	}
	static String DEFAULT_ENCODE = "UTF-8";
	public static boolean isEmpty(String path){
		if(path == null || path.length() == 0){
			return true;
		}
		return false;
	}
	
	/** 
     * 新建文件. 
     *  
     * @param path 文件路径 
     * @throws Exception 
     */  
    public static File createFile(String path) throws Exception {  
        // 获得文件对象  
        File f = new File(path);  
        if (f.exists()) {  
        	// 如果路径不存在,则创建  
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();  
            }  
            f.createNewFile();
        }  
        
        return f;
    }  
	
	/** 
     * 新建目录. 
     *  
     * @param path 文件路径 
     * @throws Exception 
     */  
    public static File createDirectory(String path) throws Exception {
        // 获得文件对象  
        File f = new File(path);  
        if (!f.exists()) {  
            // 如果路径不存在,则创建  
            f.mkdirs();  
        }
        
        return f;
    }
    
    //读文件  
    public static String readFile(String fileName) throws IOException {    
        File file = new File(fileName);    
        FileInputStream fis = new FileInputStream(file);    
        int length = fis.available();
        byte [] buffer = new byte[length];  
		fis.read(buffer);       
		String res = new String(buffer, DEFAULT_ENCODE);  
		fis.close();       
		return res;    
    }    
      
    //写文件  
    public static void writeFile(String fileName, String write_str) throws IOException{
        File file = new File(fileName);    
        FileOutputStream fos = new FileOutputStream(file);    
        byte [] bytes = write_str.getBytes(DEFAULT_ENCODE);   
        fos.write(bytes);   
        fos.close();
    }
    
//    /**
//     * 读取文件中内容
//     * 
//     * @param path
//     * @return
//     * @throws IOException
//     */
//    public static String readFileToString(String path) throws IOException {
//        String resultStr = null;
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(path);
//            byte[] inBuf = new byte[2048];
//            int len = inBuf.length;
//            int off = 0;
//            int ret = 0;
//            while ((ret = fis.read(inBuf, off, len)) > 0) {
//                off += ret;
//                len -= ret;
//            }
//            resultStr = new String(new String(inBuf, 0, off, DEFAULT_ENCODE).getBytes());
//        } finally {
//            if (fis != null)
//                fis.close();
//        }
//        return resultStr;
//    }

    /**
     * 文件转成字节数组
     * 
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] readFileToBytes(String path) throws IOException {
        byte[] b = null;
        InputStream is = null;
        File f = new File(path);
        try {
            is = new FileInputStream(f);
            b = new byte[(int) f.length()];
            is.read(b);
        } finally {
            if (is != null)
                is.close();
        }
        return b;
    }

    /**
     * 将byte写入文件中
     * 
     * @param fileByte
     * @param filePath
     * @throws IOException
     */
    public static void byteToFile(byte[] fileByte, String filePath) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(filePath));
            os.write(fileByte);
            os.flush();
        } finally {
            if (os != null)
                os.close();
        }
    }
    
    /** 
     * 保存文件(文件不存在会自动创建). 
     *  
     * @param path 文件路径 
     * @param content 文件内容 
     * @throws Exception 
     */  
    public static void saveFile(String path, String content) throws Exception {  
        saveFile(path, content, DEFAULT_ENCODE);  
    }  
  
    /** 
     * 保存文件(文件不存在会自动创建). 
     *  
     * @param path 文件路径 
     * @param content 文件内容 
     * @param encoding 编码(UTF-8/gb2312/...) 
     * @throws Exception 
     */  
    public static void saveFile(String path, String content, String encoding) throws Exception {  
        FileOutputStream fileOutputStream = null;  
        BufferedOutputStream bw = null;  
        try {  
            // 获得文件对象  
            File f = new File(path);  
            // 默认编码UTF-8  
            encoding = (isEmpty(encoding)) ? DEFAULT_ENCODE : encoding;  
            // 如果路径不存在,则创建  
            if (!f.getParentFile().exists()) {  
                f.getParentFile().mkdirs();  
            }  
            // 开始保存文件  
            fileOutputStream = new FileOutputStream(path);  
            bw = new BufferedOutputStream(fileOutputStream);  
            bw.write(content.getBytes(encoding));  
        } catch (Exception e) {  
//            L.d("保存文件错误.path=" + path + ",content=" + content + ",encoding=" + encoding, e);
            throw e;  
        } finally {  
            if (bw != null) {  
                try {  
                    bw.close();  
                } catch (IOException e1) {  
                }  
            }  
            if (fileOutputStream != null) {  
                try {  
                    fileOutputStream.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
    }
    
    //递归删除该路径对应的所有文件
    public static boolean delete(String path){
    	System.out.println("path " + path);
    	Boolean isDelSuccessfully = true;
    	File file = new File(path);
    	if(file.isDirectory()){
    		String[] fileList = file.list();
    		for(int i = 0; i < fileList.length; i++){
    			isDelSuccessfully = isDelSuccessfully && delete(file.getAbsolutePath() + file.separator + fileList[i]);
    		}
    	}else{
    		System.out.println("file is not a dir " + file.getAbsolutePath());
    	}
    	
    	System.out.println("file " + file.getAbsolutePath());
    	file.delete();
    	return isDelSuccessfully;
    }

    //按文件路径删除多个文件
    public static boolean delete(List<String> pathList){
        Boolean isDelSuccessfully = true;
        File file;
        for (String path:pathList) {
            file = new File(path);
            if(file != null){
                isDelSuccessfully = isDelSuccessfully && file.delete();
            }
        }
        return true;
    }
    
    /**
     * 使用文件通道的方式复制文件
     * 
     * @param source 源文件
     * @param dest 复制到的新文件
     */

     public static void fileChannelCopy(File source, File dest) {
    	 System.out.println("source " + source.getAbsolutePath());
    	 System.out.println("dest " + dest.getAbsolutePath());
         FileInputStream fi = null;
         FileOutputStream fo = null;
         FileChannel in = null;
         FileChannel out = null;
         try {
             fi = new FileInputStream(source);
             fo = new FileOutputStream(dest);
             in = fi.getChannel();//得到对应的文件通道
             out = fo.getChannel();//得到对应的文件通道
             in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             try {
                 fi.close();
                 in.close();
                 fo.close();
                 out.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
    
    
  //复制一个文件夹下所有的文件到另一个文件夹下 
    public static void copy(String source, String destDir){
    	System.out.println("source " + source + " destDir " + destDir);
    	File tempSourceFile = new File(source);
    	System.out.println("tempSourceFile " + tempSourceFile.getAbsolutePath());
    	File tempDestFile = null;
    	
    	if(tempSourceFile.isDirectory()){
    		String[] fileList = tempSourceFile.list();
    		System.out.println("fileList " + fileList);
    		for(int i = 0; i < fileList.length; i++){
//    			System.out.println("fileList[i] " + fileList[i]);
//    			System.out.println("tempSourceFile.getname " + tempSourceFile.getName());
//    			System.out.println("tempSourceFile.getAbsolutePath " + tempSourceFile.getAbsolutePath());
    			copy(tempSourceFile.getAbsolutePath() + File.separator + fileList[i], destDir + File.separator + tempSourceFile.getName());
    		}
    	}else{
    		try {
				tempDestFile = createFile(destDir  + File.separator + tempSourceFile.getName());
				fileChannelCopy(tempSourceFile, tempDestFile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
//				System.out.println("destDir" + destDir);
				e.printStackTrace();
			}
    		
    	}
    }
  
//	void test(){
//
//		//7.读取文件 
//		//import java.io.*; 
//		// 逐行读取数据 
//		FileReader fr = new FileReader(str1); 
//		BufferedReader br = new BufferedReader(fr); 
//		String str2 = br.readLine(); 
//		while (str2 != null) { 
//			str2 = br.readLine(); 
//		} 
//		br.close(); 
//		fr.close();  
//
//		//8.写入文件 
//		//import java.io.*; 
//		// 将数据写入文件 
//		try { 
//			FileWriter fw = new FileWriter(str1); 
//			fw.write(str2); 
//			fw.flush(); 
//			fw.close();  
//		} catch (IOException e) { 
//			e.printStackTrace(); 
//		} 
//
//		//9.写入随机文件 
//		//import java.io.*; 
//		try { 
//			RandomAccessFile logFile=new RandomAccessFile(str1,"rw"); 
//			long lg=logFile.length(); 
//			logFile.seek(str2); 
//			logFile.writeByte(str3); 
//		}catch(IOException ioe){ 
//			System.out.println("无法写入文件："+ioe.getMessage()); 
//		}  
//
//		//10.读取文件属性 
//		//import java.io.*; 
//		// 文件属性的取得 
//		File f = new File(str1); 
//		if (af.exists()) { 
//			System.out.println(f.getName() + "的属性如下： 文件长度为：" + f.length()); 
//			System.out.println(f.isFile() ? "是文件" : "不是文件"); 
//			System.out.println(f.isDirectory() ? "是目录" : "不是目录"); 
//			System.out.println(f.canRead() ? "可读取" : "不"); 
//			System.out.println(f.canWrite() ? "是隐藏文件" : ""); 
//			System.out.println("文件夹的最后修改日期为：" + new Date(f.lastModified())); 
//			} else { 
//			System.out.println(f.getName() + "的属性如下："); 
//			System.out.println(f.isFile() ? "是文件" : "不是文件"); 
//			System.out.println(f.isDirectory() ? "是目录" : "不是目录"); 
//			System.out.println(f.canRead() ? "可读取" : "不"); 
//			System.out.println(f.canWrite() ? "是隐藏文件" : ""); 
//			System.out.println("文件的最后修改日期为：" + new Date(f.lastModified())); 
//		} 
//		if(f.canRead()){ 
//			str2 
//		} 
//		if(f.canWrite()){ 
//			str3 
//		} 
//
//		//11.写入属性 
//		//import java.io.*; 
//		File filereadonly=new File(str1); 
//		try { 
//			boolean b=filereadonly.setReadOnly(); 
//		} 
//		catch (Exception e) { 
//			System.out.println("拒绝写访问："+ e.printStackTrace()); 
//		}  
//
}

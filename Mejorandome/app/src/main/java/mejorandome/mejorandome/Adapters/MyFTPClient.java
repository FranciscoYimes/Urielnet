package mejorandome.mejorandome.Adapters;

/**
 * Created by franciscoyimesinostroza on 18-07-17.
 */

import android.content.Context;
import android.util.Log;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MyFTPClient {



//Now, declare a public FTP client object.

    private static final String TAG = "MyFTPClient";
    public FTPClient mFTPClient = null;

    //Method to connect to FTP server:
    public boolean ftpConnect(String host, String username , String password, int port)
    {
        try {
            mFTPClient = new FTPClient();
            // connecting to the host
            mFTPClient.connect(host, port);

            // now check the reply code, if positive mean connection success
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login using username & password
                boolean status = mFTPClient.login(username, password);

            /* Set File Transfer Mode
             *
             * To avoid corruption issue you must specified a correct
             * transfer mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE,
             * EBCDIC_FILE_TYPE .etc. Here, I use BINARY_FILE_TYPE
             * for transferring text, image, and compressed files.
             */
                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();

                return status;
            }
        } catch(Exception e) {
            Log.d(TAG, "Error: could not connect to host " + host );
        }

        return false;
    }

//Method to disconnect from FTP server:

    public boolean ftpDisconnect()
    {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
            return true;
        } catch (Exception e) {
            Log.d(TAG, "Error occurred while disconnecting from ftp server.");
        }

        return false;
    }

//Method to get current working directory:



//Method to change working directory:



//Method to list all files in a directory:



//Method to create new directory:



//Method to delete/remove a directory:



//Method to delete a file:

    public boolean ftpRemoveFile(String filePath)
    {
        try {
            boolean status = mFTPClient.deleteFile(filePath);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

//Method to rename a file:



//Method to download a file from FTP server:

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: path to the source file in FTP server
     * desFilePath: path to the destination file to be saved in sdcard
     */
    public boolean ftpDownload(String srcFilePath, String desFilePath)
    {
        boolean status = false;
        try {
            FileOutputStream desFileStream = new FileOutputStream(desFilePath);;
            status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();

            return status;
        } catch (Exception e) {
            Log.d(TAG, "download failed");
        }

        return status;
    }

//Method to upload a file to FTP server:

    /**
     * mFTPClient: FTP client connection object (see FTP connection example)
     * srcFilePath: source file path in sdcard
     * desFileName: file name to be stored in FTP server
     * desDirectory: directory path where the file should be upload to
     */
    public boolean ftpUpload(String srcFilePath, String desFileName,
                             String desDirectory, Context context)
    {
        boolean status = false;
        try {
            // FileInputStream srcFileStream = new FileInputStream(srcFilePath);

            FileInputStream srcFileStream = context.openFileInput(srcFilePath);

            // change working directory to the destination directory
            //if (ftpChangeDirectory(desDirectory)) {
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            //}

            srcFileStream.close();
            return status;
        }
        catch (Exception e) {
            Log.d(TAG, "upload failed: " + e);
        }

        return status;
    }
}
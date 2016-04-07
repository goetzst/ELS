package controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import play.mvc.Controller;
import play.mvc.Result;


/**
 * @author Stefan
 * class for providing images from server
 */
public class ServeImageController extends Controller{
    /**
     * method for handling images that were uploaded to server
     * @param filename name of file
     * @return byteArray of Image
     */
    public static Result at(String filename)
    {
        response().setContentType("image");
        byte[]                  i_file  = new byte[16384];
        int                     nRead;
        ByteArrayOutputStream   buffer  = new ByteArrayOutputStream();
        try {
            File                    file    = new File("public/uploads/" + filename);
            FileInputStream         fInput  = new FileInputStream(file);

            while((nRead = fInput.read(i_file, 0, i_file.length)) != -1){//buffering whole image into byteArrayOutputStream
                buffer.write(i_file, 0, nRead);
            }
            buffer.flush();
        } catch (IOException e) {
            badRequest();
        }

        return ok(buffer.toByteArray());
    }
}

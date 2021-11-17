/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.bean;

import infatlan.hn.generador.prenotas.process.ProcessXLS;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.file.UploadedFile;

/**
 *
 * @author enajera
 */
@ManagedBean
@ViewScoped
public class FileBean {
    
     private UploadedFile file;
     private String destination="/tmp/";
     
      public String upload() throws IOException {
        if (file != null) {
                       
            copyFile(file.getFileName(),file.getInputStream());
        }else{
          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage
          (FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un archivo de excel."));

        }
        
        return"index.html";
    }
      
       public void copyFile(String fileName, InputStream in) throws FileNotFoundException, IOException {
      
            // write the inputStream to a FileOutputStream
            File fi = new File(destination);
            if (!fi.exists()) {
                fi.mkdir();
            }
            OutputStream out = new FileOutputStream(new File(destination + fileName));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }

            in.close();
            out.flush();
            out.close();

            System.out.println("New file created!");
            ProcessXLS xls = new ProcessXLS(destination + fileName);
            xls.readXLS();
            FacesMessage message = new FacesMessage("Successful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
    
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
      
      
    
}

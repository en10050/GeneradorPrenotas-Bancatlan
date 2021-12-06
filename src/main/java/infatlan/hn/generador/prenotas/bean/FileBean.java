/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.bean;

import infatlan.hn.generador.prenotas.process.ProcessXLS;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.shaded.commons.io.FilenameUtils;

/**
 *
 * @author enajera
 */
@ManagedBean
@ViewScoped
public class FileBean {

    private UploadedFile file;
    private StreamedContent down;
    private String destination = "/tmp/";
    private String tipo = "ATM";
    private Boolean txt = true;

    public String upload() throws IOException {

        if (file != null) {
            if (!FilenameUtils.getExtension(file.getFileName()).equalsIgnoreCase("xlsx")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Archivo '" + file.getFileName() + "' no válido."));

            } else {
                copyFile(file.getFileName(), file.getInputStream());
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Seleccione un archivo de excel."));
        }

        return "index.html";
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

        ProcessXLS xls = new ProcessXLS(destination + fileName);
        String[] path = xls.readXLS(tipo,txt);

        if (path[0].equals("02")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Problemas con servicio web de Prenotas."));

        }
        else if(path[0].equals("03")){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Error de formato numerico en archivo excel."));

        }
        else {
            zipFile(path);
        }

    }

    public void zipFile(String[] srcFiles) throws FileNotFoundException, IOException {

        FileOutputStream fos = new FileOutputStream("prenotas.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {

            if (!srcFile.isEmpty()) {
                File fileToZip = new File(srcFile);

                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }
                fis.close();
            }
        }
        zipOut.close();
        fos.close();

        download("prenotas.zip");

    }

    public void download(String name) throws IOException {

        File file = new File(name);

        FacesContext facesContext = FacesContext.getCurrentInstance();

        HttpServletResponse response
                = (HttpServletResponse) facesContext.getExternalContext().getResponse();

        response.reset();
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());

        OutputStream responseOutputStream = response.getOutputStream();

        InputStream fileInputStream = new FileInputStream(file);

        byte[] bytesBuffer = new byte[2048];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(bytesBuffer)) > 0) {
            responseOutputStream.write(bytesBuffer, 0, bytesRead);
        }

        responseOutputStream.flush();

        fileInputStream.close();
        responseOutputStream.close();

        facesContext.responseComplete();

    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getDown() {
        return down;
    }

    public void setDown(StreamedContent down) {
        this.down = down;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Boolean getTxt() {
        return txt;
    }

    public void setTxt(Boolean txt) {
        this.txt = txt;
    }
    
    
    
    

}

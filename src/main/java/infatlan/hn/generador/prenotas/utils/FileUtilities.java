/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.utils;

import infatlan.hn.generador.prenotas.models.Trama;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author enajera
 */
public class FileUtilities {

    public String crearTxt(ArrayList<Trama> tramas, String nombre) throws IOException {

        //Crea el objeto File
        File inFile = new File(nombre);
        //Existe el archivo?
        if (!inFile.exists()) {
            inFile.createNewFile();
            System.out.println("Archivo Creado");
        }
        String path = "";
        //BufferedWriter se construye con un FileWriter, cuyo segundo parametro indica si escribe o sobreescribe
        BufferedWriter bw = new BufferedWriter(new FileWriter(inFile, false));//TRUE:=Append, FALSE:=Overwritting
        //Escribe en el archivo
        for (int i = 0; i < tramas.size(); i++) {
            if(tramas.get(i).getRespuesta().equals("00")){
                bw.write(tramas.get(i).toString());
            }else{
                bw.write(tramas.get(i).toString()+
                        "\nERROR:\n"+tramas.get(i).getMsg());
            }
           
            if (i < tramas.size()-1) {
                bw.newLine();
            }
        }
        //Se cierra el archivo
        bw.close();

        path = inFile.getAbsolutePath();
        System.out.println("Ruta:" + path);
        return path;

    }

}

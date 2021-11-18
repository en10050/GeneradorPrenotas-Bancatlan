/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.process;

import infatlan.hn.generador.prenotas.ws.EjecutarSrvBasa041;
import infatlan.hn.generador.prenotas.ws.PeticionSrvBasa041;
import infatlan.hn.generador.prenotas.ws.RespuestaSrvBasa041;
import infatlan.hn.generador.prenotas.ws.ServicioSrvBasa041Interfaz;
import infatlan.hn.generador.prenotas.ws.SrvBasa041ServiceService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author enajera
 */
public class ProcessXLS {

    public String fileName;
    private Properties propiedades = null;
    private static AtomicLong idCounter = new AtomicLong();

    public ProcessXLS(String name) {
        fileName = name;

        try {
            propiedades = new Properties();
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            try (InputStream resourceStream = loader.getResourceAsStream("config.properties")) {
                propiedades.load(resourceStream);

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void readXLS() throws FileNotFoundException, IOException {
        File f = new File(fileName);
        FileInputStream fis = new FileInputStream(f);
        XSSFWorkbook excelWorkbook = new XSSFWorkbook(fis);
        XSSFSheet excelSheet = excelWorkbook.getSheetAt(0);

        int rows = excelSheet.getPhysicalNumberOfRows();//3
        int cols = excelSheet.getRow(1).getPhysicalNumberOfCells();//2

        String data[][] = new String[rows][cols];
        String datos[] = new String[cols];
        XSSFCell cell;

        DataFormatter formatter = new DataFormatter();
        ArrayList<String[]> array = new ArrayList<String[]>();

        for (int i = 1; i < rows; i++) {
            datos = new String[cols];
            for (int j = 0; j < cols; j++) {

                cell = excelSheet.getRow(i).getCell(j);
                String cellContents = formatter.formatCellValue(cell);
                if (cellContents.isEmpty()) {
                    break;
                } else {
                    data[i][j] = cellContents;
                    datos[j] = cellContents;
                }
              
            }
           array.add(datos);
           
        }
        fis.close();

        SrvBasa041ServiceService ws
         = new SrvBasa041ServiceService(new URL(propiedades.getProperty("web.service.url")));
        
        ServicioSrvBasa041Interfaz servicio = ws.getSrvBasa041ServicePort();
      
        PeticionSrvBasa041 request;
        RespuestaSrvBasa041 response = null;

        for (String[] arreglos : array) {
            request = new PeticionSrvBasa041();

            request.setCodigoTransaccion("000");
            request.setCodigoCanal("01");
            request.setUsuarioPeticion("AT01");
            request.setCodigoPais("HN");
            request.setCodigoBanco("01");
            request.setCodigoCoreBanking("SAP");

            request.setGenerarCodigoPrenota(getCodigo());
            request.setNumeroCuenta(arreglos[0]);
            request.setMonto(Double.valueOf(arreglos[1]));
            request.setMoneda("LPS");
            request.setTipoPrenota(arreglos[7]);
            request.setFechaFinalizacionPlanificada(arreglos[6] + "-" + arreglos[5] + "-" + arreglos[4]);

            System.out.println("setGenerarCodigoPrenota:" + request.getGenerarCodigoPrenota());
            System.out.println("setNumeroCuenta:" + request.getNumeroCuenta());
            System.out.println("setMonto:" + request.getMonto());
            System.out.println("setMoneda:" + request.getMoneda());
            System.out.println("setTipoPrenota:" + request.getTipoPrenota());
            System.out.println("setFechaFinalizacionPlanificada:" + request.getFechaFinalizacionPlanificada());
            System.out.println("--------------------------------------------------------------------------");
            
//            response = servicio.ejecutarSrvBasa041(request);
//            System.out.println("RESPONSE");
//            System.out.println("CodigoMensaje:"+response.getCodigoMensaje());
//            System.out.println("Mensaje:"+response.getMensaje());
//            System.out.println("--------------------------------------------------------------------------");
            

        }
//        <generarCodigoPrenota>16112021015515</generarCodigoPrenota
//                > <tipoPrenota>230</tipoPrenota
//                > <numeroCuenta>1190022739</numeroCuenta
//                > <moneda>LPS</moneda
//                > <monto>500</monto
//                > <fechaFinalizacionPlanificada>2021-11-16</fechaFinalizacionPlanificada >

    }

    public String getCodigo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        return sdf.format(new Date())+ String.valueOf(idCounter.getAndIncrement());
    }
    
    

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
//            System.out.println(">" +getCodigo());
        }
    }

}

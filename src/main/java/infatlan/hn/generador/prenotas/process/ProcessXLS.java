/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infatlan.hn.generador.prenotas.process;

import infatlan.hn.generador.prenotas.models.Trama;
import infatlan.hn.generador.prenotas.utils.FileUtilities;
import infatlan.hn.generador.prenotas.ws.ItemError;
import infatlan.hn.generador.prenotas.ws.PeticionSrvBasa041;
import infatlan.hn.generador.prenotas.ws.RespuestaSrvBasa041;
import infatlan.hn.generador.prenotas.ws.ServicioSrvBasa041Interfaz;
import infatlan.hn.generador.prenotas.ws.SrvBasa041ServiceService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
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

    public String[] readXLS(String tipo, Boolean txt) throws FileNotFoundException, IOException {
        String[] pos;
        String resp = "00";

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
        Trama trama = new Trama();

        ArrayList<Trama> tramasBuenas = new ArrayList<Trama>();
        ArrayList<Trama> tramasMalas = new ArrayList<Trama>();

        Calendar c = Calendar.getInstance();
        String dia = Integer.toString(c.get(Calendar.DATE));
        String mes = Integer.toString(c.get(Calendar.MONTH) + 1);
        String anio = Integer.toString(c.get(Calendar.YEAR));

        String x = "00";

        String canal = propiedades.getProperty("codigoCanal");
        String usuarioPeticion = propiedades.getProperty("usuarioPeticion");
        String agencia = propiedades.getProperty("agencia");
        String codigoMoneda = propiedades.getProperty("codigoMoneda");
        String tipoTrn = propiedades.getProperty("tipoTrn");

        try {
            for (String[] arreglos : array) {
                request = new PeticionSrvBasa041();

                request.setCodigoTransaccion(propiedades.getProperty("codigoTransaccion"));
                request.setCodigoCanal(arreglos[8].equals("-") ? "0000" : addZero(arreglos[8].trim(), 4));
                request.setUsuarioPeticion(arreglos[12].equals("-") ? usuarioPeticion : arreglos[12].trim());
                request.setCodigoPais(propiedades.getProperty("codigoPais"));
                request.setCodigoBanco(propiedades.getProperty("codigoBanco"));
                request.setCodigoCoreBanking(propiedades.getProperty("codigoCoreBanking"));

                request.setGenerarCodigoPrenota(getCodigo());
                request.setNumeroCuenta(arreglos[0]);
                try {
                    request.setMonto(Double.valueOf(arreglos[1]));

                } catch (Exception e) {
                    e.printStackTrace();
                    pos = new String[1];
                    pos[0] = "03";
                    return pos;
                }

                request.setMoneda(propiedades.getProperty("moneda"));
                request.setTipoPrenota(arreglos[7]);
                request.setFechaFinalizacionPlanificada(arreglos[6] + "-" + arreglos[5] + "-" + arreglos[4]);
                String codigoTransaccion = arreglos[3];
                String decripcion = arreglos[2];

                //Evalua si va al servicio web
                if (!txt) {
                    response = servicio.ejecutarSrvBasa041(request);
                    resp = response.getCodigoMensaje();
                    System.out.println("RESPONSE");
                    System.out.println("CodigoMensaje:" + response.getCodigoMensaje());
                    System.out.println("Mensaje:" + response.getMensaje());
                    System.out.println("--------------------------------------------------------------------------");
                } else {
                    System.out.println("NO SE CONECTO A WS");
                }

                //Setear las tramas
                trama = new Trama();
                trama.setCanal(request.getCodigoCanal());
                trama.setAgencia(addZero(arreglos[9], 9));
                trama.setMoneda(arreglos[10].trim());
                trama.setCuenta(request.getNumeroCuenta());
                trama.setMonto(request.getMonto() + "");
                trama.setReferencia(request.getGenerarCodigoPrenota());
                trama.setDescripcion(decripcion.trim());
                trama.setCodigoTrn(codigoTransaccion);
                trama.setLlaveAdicional(propiedades.getProperty("llaveAdicional"));
                trama.setUserDos(arreglos[13].equals("-") ? "0000000000" : arreglos[13].trim());
                trama.setDebCreFlag(arreglos[11].trim());
                trama.setUser(request.getUsuarioPeticion());
                trama.setDia(dia);
                trama.setMes(mes);
                trama.setAnio(anio);

                trama.setRespuesta(resp);
                System.out.println("Trama:" + trama.toString());

                if (trama.getRespuesta().equals("00")) {
                    tramasBuenas.add(trama);
                } else {
                    String stb = "";
                    for (ItemError itm : response.getColeccionErrorSAP()) {
                        stb += ("{\n"
                                + "getDetalleError:" + itm.getDetalleError() + ",\n"
                                + "getIdMensajeError:" + itm.getIdMensajeError() + ",\n"
                                + "getSeveridad:" + itm.getSeveridad() + "\n"
                                + "}" + "\n");

                    }

                    trama.setMsg(stb);
                    tramasMalas.add(trama);
                }

                System.out.println("--------------------------------------------------------------------------");

            }

            pos = new String[2];

            if (!tramasMalas.isEmpty()) {
                pos[1] = crearTxt(tramasMalas, "MALAS_" + anio + mes + dia + ".txt");
            } else {
                pos[1] = "";
            }
            pos[0] = crearTxt(tramasBuenas, tipo + "_" + anio + mes + dia + ".txt");

        } catch (Exception e) {
            e.printStackTrace();
            pos = new String[1];
            pos[0] = "02";
        }

        return pos;

    }

    public String crearTxt(ArrayList<Trama> tramas, String nombre) throws IOException {
        FileUtilities filesU = new FileUtilities();
        return filesU.crearTxt(tramas, nombre);
    }

    public String getCodigo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmssSS");
        return sdf.format(new Date()) + String.valueOf(idCounter.getAndIncrement());
    }

    public String addZero(String dato, int n) {
        String nuevo = "";
        if (dato.length() < n) {
            int ciclo = dato.length() - n;
            for (int i = 0; i < ciclo; i++) {
                nuevo += "0";
            }
            return nuevo + dato;
        }

        return dato;

    }

    public static void main(String[] args) {
        Calendar c1 = Calendar.getInstance();
        System.out.println("Day:" + Integer.toString(c1.get(Calendar.DATE)));
        System.out.println("Mes:" + Integer.toString(c1.get(Calendar.MONTH) + 1));
        System.out.println("Anio:" + Integer.toString(c1.get(Calendar.YEAR)));

    }

}

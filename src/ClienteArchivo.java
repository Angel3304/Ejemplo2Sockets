import javax.swing.JFileChooser;
import java.io.*;
import java.net.*;

public class ClienteArchivo {

    public static void main(String[] args) {
        try {
            // 1. OBTENER DATOS DEL SERVIDOR

            // Crea un BufferedReader para leer la entrada del usuario desde la consola
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            // Pide al usuario la direccion (host) del servidor
            System.out.println("Escribe la direccion del servidor:");
            String host = br.readLine(); // Lee la dirección del servidor

            // Pide al usuario el puerto del servidor
            System.out.println("Escribe el puerto del servidor:");
            int pto = Integer.parseInt(br.readLine()); // Lee el puerto y lo convierte a entero

            // 2. CREAR SOCKET Y CONECTAR

            // Crea un nuevo Socket (cliente) y se conecta al host y puerto especificados
            Socket cl = new Socket(host, pto);

            // 3. SELECCIONAR ARCHIVOS (MODIFICADO PARA MULTIPLES)

            // Crea un objeto JFileChooser para que el usuario seleccione archivos
            JFileChooser jf = new JFileChooser();

            // Habilita la selección de múltiples archivos
            jf.setMultiSelectionEnabled(true);

            // Muestra el diálogo "Abrir" y guarda el resultado (qué boton presiono el usuario)
            int r = jf.showOpenDialog(null);

            // Comprueba si el usuario presiono el boton "Aceptar" (APPROVE_OPTION)
            if (r == JFileChooser.APPROVE_OPTION) {

                // Obtiene la lista de todos los archivos seleccionados
                File[] files = jf.getSelectedFiles();

                // Obtiene el numero de archivos seleccionados
                int numFiles = files.length;
                System.out.println("Enviando " + numFiles + " archivo(s)...");

                // 4. PREPARAR FLUJO DE SALIDA DEL SOCKET

                // Crea un DataOutputStream para enviar datos (primitivos y bytes) al servidor
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

                // 5. ENVIAR NUMERO DE ARCHIVOS

                // Envia la cantidad de archivos (un entero) al servidor
                dos.writeInt(numFiles);
                dos.flush(); // Asegura que los datos se envíen de inmediato

                // 6. BUCLE PARA ENVIAR CADA ARCHIVO

                // Inicia un bucle que se repite por cada archivo seleccionado
                for (File f : files) {
                    // Obtiene la ruta absoluta del archivo
                    String archivo = f.getAbsolutePath();
                    // Obtiene el nombre del archivo
                    String nombre = f.getName();
                    // Obtiene el tamaño (longitud) del archivo en bytes
                    long tam = f.length();

                    System.out.println("\nEnviando archivo: " + nombre + " (" + tam + " bytes)");

                    // Crea un DataInputStream para leer el archivo local
                    DataInputStream dis = new DataInputStream(new FileInputStream(archivo));

                    // Envia el nombre del archivo al servidor (en formato UTF)
                    dos.writeUTF(nombre);
                    dos.flush(); // Asegura el envío

                    // Envia el tamaño del archivo al servidor (como un long)
                    dos.writeLong(tam);
                    dos.flush(); // Asegura el envio

                    // Define un buffer de bytes (aqui de 1024 bytes o 1KB)
                    byte[] b = new byte[1024];
                    // Variable para contar el total de bytes enviados de este archivo
                    long enviados = 0;
                    // Variables para el porcentaje y el numero de bytes leidos (n)
                    int porcentaje, n;

                    // Bucle para leer y enviar el contenido del archivo
                    // Se ejecuta mientras los bytes enviados sean menores que el tamaño total
                    while (enviados < tam) {
                        // Lee bytes del archivo local y los guarda en el buffer 'b'
                        n = dis.read(b);
                        // Escribe los 'n' bytes leidos del buffer 'b' al flujo del socket
                        dos.write(b, 0, n);
                        dos.flush(); // Asegura el envio

                        // Incrementa el contador de bytes enviados
                        enviados = enviados + n;

                        // Calcula el porcentaje enviado
                        porcentaje = (int) (enviados * 100 / tam);
                        // Imprime el porcentaje en la consola (con \r para sobrescribir la línea)
                        System.out.print("Enviado " + porcentaje + "%\r");
                    } // Fin del while (envío de un archivo)

                    System.out.println("\nArchivo '" + nombre + "' enviado completamente.");

                    // Cierra el flujo de lectura del archivo local
                    dis.close();

                }
                System.out.println("\nTodos los archivos han sido enviados.");

                // 7. CERRAR FLUJOS PRINCIPALES Y SOCKET

                // Cierra el flujo de salida principal hacia el socket
                dos.close();
                // Cierra el socket del cliente
                cl.close();

            }

        } catch (Exception e) { // Captura cualquier excepcion que haya ocurrido
            // Imprime la traza del error en la consola
            e.printStackTrace();
        }
    }
}
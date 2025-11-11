import java.net.ServerSocket;
import java.net.*;
import java.io.*;

public class ServidorArchivo {
    public static void main(String[] args) {
        try {

            // 1. CREAR SOCKET DEL SERVIDOR
            // Crea un ServerSocket que escucha peticiones en el puerto 7000
            ServerSocket s = new ServerSocket(7000);
            System.out.println("Servidor iniciado en el puerto 7000. Esperando cliente...");

            // 2. BUCLE INFINITO PARA ACEPTAR CLIENTES

            // Inicia un bucle infinito (;; significa "para siempre")
            for (;;) {
                // El servidor se bloquea (espera) hasta que un cliente se conecta
                Socket cl = s.accept();

                // Mensaje de conexion: muestra la IP y puerto del cliente conectado
                System.out.println("\nConexion establecida desde " + cl.getInetAddress().getHostName() + ":" + cl.getPort());

                // 3. PREPARAR FLUJO DE ENTRADA DEL SOCKET

                // Crea un DataInputStream para recibir datos (primitivos y bytes) del cliente
                DataInputStream dis = new DataInputStream(cl.getInputStream());

                // 4. RECIBIR NUMERO DE ARCHIVOS

                // Lee el entero (int) que dice cuantos archivos se recibiran
                int numFiles = dis.readInt();
                System.out.println("Se recibiran " + numFiles + " archivo(s).");

                // 5. BUCLE PARA RECIBIR CADA ARCHIVO

                // Inicia un bucle que se repite 'numFiles' veces
                for (int i = 0; i < numFiles; i++) {

                    // --- Inicio de logica original por archivo ---

                    // Define un buffer de bytes (1024 bytes)
                    // Se define 'b' (en lugar de 'buffer' que no se usaba)
                    byte[] b = new byte[1024];

                    // Lee el nombre del archivo (enviado como UTF)
                    // Se usa readUTF() (en lugar de readLong() que era incorrecto)
                    String nombre = dis.readUTF();

                    // Lee el tamaño del archivo (enviado como long)
                    // Se define 'tam' que faltaba
                    long tam = dis.readLong();

                    System.out.println("Recibiendo archivo " + (i+1) + "/" + numFiles + ": " + nombre);

                    // Crea un DataOutputStream para escribir el archivo en el disco
                    // Se añade prefijo para no sobrescribir archivos locales
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream("Servidor_Copia_" + nombre));

                    // Variable para contar el total de bytes recibidos de este archivo
                    long recibidos = 0;
                    // Variables para el porcentaje y el número de bytes leídos (n)
                    int n, porcentaje;

                    // Bucle para leer y guardar el contenido del archivo
                    // Se ejecuta mientras los bytes recibidos sean menores que el tamaño total
                    while (recibidos < tam) {
                        // Lee bytes del socket y los guarda en el buffer 'b'
                        n = dis.read(b);

                        // Escribe los 'n' bytes leídos del buffer 'b' al archivo local
                        dos.write(b, 0, n);
                        dos.flush(); // Asegura la escritura en disco

                        // Incrementa el contador de bytes recibidos
                        recibidos = recibidos + n;

                        // Calcula el porcentaje recibido
                        porcentaje = (int) (recibidos * 100 / tam);
                        // Imprime el estado
                        System.out.print("Recibido " + porcentaje + "%\r");
                    } // Fin del while (recepción de un archivo)

                    System.out.println("\nArchivo '" + nombre + "' recibido completamente.");

                    // Cierra el flujo de escritura del archivo local
                    dos.close();

                    // --- Fin de logica original por archivo ---

                } // Fin del for (bucle de archivos)

                System.out.println("Todos los archivos de " + cl.getInetAddress().getHostName() + " han sido recibidos.");

                // 6. CERRAR FLUJOS DE ESTE CLIENTE Y SOCKET

                // Cierra el flujo de entrada del socket de este cliente
                dis.close();
                // Cierra el socket de este cliente
                cl.close();

            } // Fin del for (bucle infinito, vuelve a esperar un nuevo cliente)

        } catch (Exception e) { // Captura cualquier excepción
            // Imprime la traza del error
            e.printStackTrace();
        }
    }
}
import java.net.*;
import java.io.*;

public class ServidorArchivo {
    public static void main(String[] args){
        try {
            ServerSocket s = new ServerSocket(7000);
            for(;;){
                Socket cl = s.accept();
                System.out.println("Conexion establecida desde" + cl.getInetAddress().getHostName()+":"+cl.getPort());

                DataInputStream dis = new DataInputStream(cl.getInputStream());
                byte[] buffer = new byte[1024];
                String nombre = dis.readLong();
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));
                long recibidos = 0;
                int n, porcentaje;
                while (recibidos<tam){
                    n = dis.read(b);
                    dos.write(b,0,n);
                    dos.flush();
                    recibidos = recibidos + n;
                    porcentaje = (int)(recibidos*100/tam);
                    System.out.println("\n\nArchivo recibido");
                }
                dos.close();
                dis.close();
                cl.close();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

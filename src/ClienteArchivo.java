import javax.swing.JFileChooser;
import java.io.*;
import java.net.Socket;

public class ClienteArchivo {
    public static void main(String[] args) {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Escribe la direccion del servidor");
            String host=br.readLine();
            System.out.println("Escribe el puerto del servidor");
            int pto=Integer.parseInt(br.readLine());
            Socket cl=new Socket(host,pto);
            JFileChooser jf=new JFileChooser();
            int r=jf.showOpenDialog(null);
            if(r==JFileChooser.APPROVE_OPTION) {
                File f = jf.getSelectedFile(); //Manejador
                String archivo = f.getAbsolutePath(); //Direccion
                String nombre = f.getName(); //Nombre
                long tam = f.length(); //Tamaño
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(archivo));
                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();
                byte[] b = new byte[1024];
                long enviados = 0;
                int porcentaje, n;
                while (enviados < tam) {
                    n = dis.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados = enviados + n;
                    porcentaje = (int) (enviados * 100 / tam);
                    System.out.println("Enviado" + porcentaje + "%\r");
                }
                System.out.println("\n\nArchivo enviado");
                dos.close();
                dis.close();
                cl.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}

/*
Modelos de Programación 2 - Eje #3

Diego Andrés Lopez Rodriguez
Harold de Jesús Marimón Herrera
Juan Angel Velasquez Gomez
Liana Victoria Gaitán Higuera

*/

/*Importamos los paquetes necesarios para desarrollar el juego*/
import java.io.*;
import java.net.*;
import java.util.Random;

/*Nombramos la clase principal*/
public class modelosProgramacionEje3 {
    public static void main(String[] args) {

        /*Inicializamos el servidor con el constructor "Thread"*/
        Thread serverThread = new Thread(new Server());
        serverThread.start();

        /*
        Try: El servidor se inicia en 1 segundo
        Catch: Si el servidor no puede iniciarse por una excepción, se imprime el error
        * */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /*Inicializamos el cliente con el constructor "Thread"*/
        Thread clientThread = new Thread(new Client());
        clientThread.start();
    }
    /*Implementación de la logica del servidor
    Se crea un socket en el puerto 12345 para conectar al cliente
    La variable "fallosConsecutivos" se inicializa en 0 para llevar la cuenta de los fallos consecutivos
    Las variables "aciertos" y "fallos" se inicializan en 0 para llevar la cuenta de los aciertos y desaciertos
    * */

    static class Server implements Runnable {
        @Override
        public void run() {
            int port = 12345;
            int fallosConsecutivos = 0;
            int aciertos = 0;
            int fallos = 0;

            /*Try: Se crea un socket en el puerto 12345
            Catch: Si el socket no puede crearse por una excepción, se imprime el error*/
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("El servidor está en funcionamiento através del puerto " + port);
                Socket socket = serverSocket.accept();
                System.out.println("Cliente conectado");


                /*Sección para el almacenamiento de los datos*/
                /*Se crea un buffer de lectura y escritura para el socket*/
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                String mensajeDelCliente;
                while ((mensajeDelCliente = input.readLine()) != null) {
                    if (mensajeDelCliente.equalsIgnoreCase("terminar")) {
                        break;
                    }

                    /*Sección para la lógica del juego*/
                    int clientNumber = Integer.parseInt(mensajeDelCliente);
                    int serverGuess = (int) (Math.random() * 10) + 1; // Adivina un número entre 1 y 10

                    if (serverGuess == clientNumber) {
                        aciertos++;
                        fallosConsecutivos = 0;
                        output.println("Adiviné! Número: " + serverGuess);
                    } else {
                        fallos++;
                        fallosConsecutivos++;
                        output.println("Fallé. Mi número: " + serverGuess);
                    }

                    if (fallosConsecutivos == 3) {
                        output.println("perdiste");
                        break;
                    }
                }
                 /*Sección de resultados obtenidos*/
                System.out.println("Aciertos: " + aciertos);
                System.out.println("Desaciertos: " + fallos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*Implementación de la logica del cliente generando los numeros aleatorios*/
    static class Client implements Runnable {
        @Override
        public void run() {
            /*El cliente se conectará al juego mediante el localhost (su propio dispositivos)*/
            String hostname = "localhost";
            int port = 12345;

            /*Try: Se crea un socket en el puerto 12345
            Catch: Si el socket no puede crearse por una excepción, se imprime el error*/
            try (Socket socket = new Socket(hostname, port)) {

                /*Sección para el almacenamiento de los datos y lectura de los mismos en consola*/
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

                String userInput;
                Random random = new Random();

                /*Sección en ejecución mientras el cliente esté activo en el juego*/
                while (true) {
                    /*Variable "randonNumber" se encarga de generar un numero aleatorio entre 1 y 10 */
                    int randomNumber = random.nextInt(10) + 1;
                    System.out.println("Número generado: " + randomNumber);
                    output.println(randomNumber);

                    String serverResponse = input.readLine();
                    System.out.println("Respuesta del servidor: " + serverResponse);

                    if (serverResponse.equals("perdiste")) {
                        break;
                    }

                    /*Sección para finalizar el juego*/
                    System.out.print("Escribe 'terminar' para finalizar o presiona Enter para continuar: ");
                    userInput = consoleInput.readLine();
                    if (userInput.equalsIgnoreCase("terminar")) {
                        output.println("terminar");
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
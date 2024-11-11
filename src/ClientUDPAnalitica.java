import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientUDPAnalitica {
    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                // Configuración del servidor
                String ipServidor = args[0];
                int portServidor = Integer.parseInt(args[1]);

                DatagramSocket socket = new DatagramSocket();

                // Mostrar menú de opciones
                Scanner scanner = new Scanner(System.in);
                System.out.println("Selecciona una opció:");
                System.out.println("1. Analitzar tota l'analítica sencera");
                System.out.println("2. Analitzar certs paràmetres concrets");
                int opcio = scanner.nextInt();
                scanner.nextLine(); // Consumir el salt de línia

                // Iniciar la construcció del missatge a enviar
                StringBuilder analitica = new StringBuilder();

                // Opció 1: Analitzar tota l'analítica
                if (opcio == 1) {
                    // Solicitar sexe
                    String sexe = obtenirSexe(scanner);

                    // Afegir sexe a l'analítica
                    analitica.append("Sexe,").append(sexe).append(",");

                    // Paràmetres de l'analítica sencera
                    String[] parametres = {
                        "Hemoglobina", "Hematòcrit", "Eritròcits", "Leucòcits", 
                        "Plaquetes", "Glucosa", "Colesterol Total", 
                        "Colesterol HDL", "Colesterol LDL", "Triglicèrids", "Ferritina"
                    };

                    // Demanar els valors per a cada paràmetre
                    for (String parametre : parametres) {
                        System.out.print("Introdueix el valor per a " + parametre + ": ");
                        String valor = scanner.nextLine();
                        analitica.append(parametre).append(",").append(valor).append(",");
                    }

                } 
                // Opció 2: Analitzar certs paràmetres concrets
                else if (opcio == 2) {
                    // Solicitar sexe
                    String sexe = obtenirSexe(scanner);

                    // Afegir sexe a l'analítica
                    analitica.append("Sexe,").append(sexe).append(",");

                    // Sol·licitar els paràmetres específics
                    System.out.println("Selecciona els paràmetres que vols analitzar:");
                    String[] parametresDisponibles = {
                        "Hemoglobina", "Hematòcrit", "Eritròcits", "Leucòcits", 
                        "Plaquetes", "Glucosa", "Colesterol Total", 
                        "Colesterol HDL", "Colesterol LDL", "Triglicèrids", "Ferritina"
                    };

                    for (String parametre : parametresDisponibles) {
                        System.out.print("Vols analitzar " + parametre + "? (S/N): ");
                        String resposta = scanner.nextLine().trim().toUpperCase();
                        if (resposta.equals("S")) {
                            System.out.print("Introdueix el valor per a " + parametre + ": ");
                            String valor = scanner.nextLine();
                            analitica.append(parametre).append(",").append(valor).append(",");
                        }
                    }
                } else {
                    System.out.println("Opció no vàlida. Sortint...");
                    socket.close();
                    return;
                }

                // Eliminar la última coma
                if (analitica.length() > 0) {
                    analitica.setLength(analitica.length() - 1);
                }

                // Configuración per enviar les dades al servidor
                byte[] peticioBytes = analitica.toString().getBytes();
                System.out.println("Tamany del missatge: " + peticioBytes.length);
                InetAddress adrecaServidor = InetAddress.getByName(ipServidor);
                DatagramPacket paquetPeticio = new DatagramPacket(peticioBytes, peticioBytes.length, adrecaServidor, portServidor);
                
                System.out.println("Enviant missatge: " + analitica); 
                socket.send(paquetPeticio);
                System.out.println("Analítica enviada al servidor.");
                
                // Esperar resposta del servidor
                byte[] respostaBytes = new byte[512];
                DatagramPacket paquetResposta = new DatagramPacket(respostaBytes, respostaBytes.length);
                socket.receive(paquetResposta);
                String resposta = new String(paquetResposta.getData(), 0, paquetResposta.getLength()).trim();
                System.out.println("Resposta del servidor: " + resposta);

                // Tancar el socket però sense tancar el scanner
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("El nombre de paràmetres no és correcte! Introdueix l'adreça IP del servidor i el port.");
        }
    }

    // Mètode per obtenir el sexe del client
    private static String obtenirSexe(Scanner scanner) {
        String sexe = "";
        while (true) {
            System.out.print("Introdueix el sexe (M/F): ");
            String sexe2 = scanner.nextLine().trim().toUpperCase();
            if (sexe2.equals("M") || sexe2.equals("F")) {
                sexe = sexe2;
                break;
            } else {
                System.out.println("Entrada no vàlida. Si us plau, introdueix 'M' o 'F'.");
            }
        }
        return sexe;
    }
}

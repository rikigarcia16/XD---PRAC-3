import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServidorUDPAnalitica {
    public static final int MIDA_PAQUET = 512;

    public static void main(String[] args) {
        if (args.length == 1) {
            try {
                // Crear el socket UDP
                DatagramSocket socket = new DatagramSocket(Integer.parseInt(args[0]));
                System.out.println("Servidor UDP d'analítiques operatiu al port " + args[0] + "!");

                while (true) {
                    System.out.println("Esperant petició d'algun client...");
                    // Buffer per rebre el paquet del client
                    byte[] buffer = new byte[MIDA_PAQUET];
                    DatagramPacket paquetPeticio = new DatagramPacket(buffer, buffer.length);

                    // Rebre el paquet del client
                    socket.receive(paquetPeticio);
                    System.out.println("Paquet rebut!");

                    // Crear un fil nou per processar el paquet rebut
                    new Thread(new ClientHandler(socket, paquetPeticio)).start();
                }
            } catch (Exception e) {
                System.out.println("Hi ha un error en la classe clientHandler");
                e.printStackTrace();
            }
        } else {
            System.out.println("El nombre de paràmetres no és correcte! Introdueix el port del servidor.");
        }
    }
}

// Classe per manejar cada client en un fil separat i analitzar la informació
class ClientHandler implements Runnable {
    private DatagramSocket socket;
    private DatagramPacket paquetPeticio;

    public ClientHandler(DatagramSocket socket, DatagramPacket paquetPeticio) {
        this.socket = socket;
        this.paquetPeticio = paquetPeticio;
    }

    @Override
    public void run() {
        try {
            // Processar el missatge rebut del client
            String missatgeRebut = new String(paquetPeticio.getData(), 0, paquetPeticio.getLength()).trim();
            System.out.println("Missatge rebut del client: " + missatgeRebut);

            // Anàlisi de les dades de l'analítica
            String resposta = processarAnalitica(missatgeRebut);

            // Enviar la resposta analitzada al client
            byte[] respostaBytes = resposta.getBytes();
            InetAddress adrecaClient = paquetPeticio.getAddress();
            int portClient = paquetPeticio.getPort();
            DatagramPacket paquetResposta = new DatagramPacket(respostaBytes, respostaBytes.length, adrecaClient, portClient);

            socket.send(paquetResposta);
            System.out.println("Resposta enviada al client: " + resposta);
        } catch (Exception e) {
            System.out.println("Error en el servidor:");
            e.printStackTrace();
        }
    }

    // Mètode per processar i analitzar les dades de l'analítica
    private String processarAnalitica(String dades) {
        String[] campDades = dades.split(",");
        String sexe = campDades[1]; // Primer camp és "Sexe"
        StringBuilder resposta = new StringBuilder("Resultat de l'analítica:\nSexe: " + sexe + "\n");

        // Anàlisi detallada de cada paràmetre
        for (int i = 2; i < campDades.length; i += 2) {
            String parametre = campDades[i];
            String valor = campDades[i + 1];
            resposta.append(parametre).append(": ").append(valor).append("\n");

            try {
                double valorNumeric = Double.parseDouble(valor);

                switch (parametre) {
                    case "Hemoglobina":
                        if ((sexe.equals("M") && valorNumeric <= 13) || (sexe.equals("F") && valorNumeric <= 12)) {
                            resposta.append("-> Hemoglobina baixa\n");
                        } else if ((sexe.equals("M") && valorNumeric >= 17) || (sexe.equals("F") && valorNumeric >= 15)) {
                            resposta.append("-> Hemoglobina alta\n");
                        } else {
                            resposta.append("-> Hemoglobina dins dels límits normals\n");
                        }
                        break;

                    case "Hematòcrit":
                        if ((sexe.equals("M") && valorNumeric <= 38) || (sexe.equals("F") && valorNumeric <= 36)) {
                            resposta.append("-> Hematòcrit baix\n");
                        } else if ((sexe.equals("M") && valorNumeric >= 52) || (sexe.equals("F") && valorNumeric >= 46)) {
                            resposta.append("-> Hematòcrit alt\n");
                        } else {
                            resposta.append("-> Hematòcrit dins dels límits normals\n");
                        }
                        break;

                    case "Eritròcits":
                        if ((sexe.equals("M") && valorNumeric <= 4.3) || (sexe.equals("F") && valorNumeric <= 3.9)) {
                            resposta.append("-> Eritròcits baixos\n");
                        } else if ((sexe.equals("M") && valorNumeric >= 5.9) || (sexe.equals("F") && valorNumeric >= 5.2)) {
                            resposta.append("-> Eritròcits alts\n");
                        } else {
                            resposta.append("-> Eritròcits dins dels límits normals\n");
                        }
                        break;

                    case "Leucòcits":
                        if (valorNumeric <= 4.0) {
                            resposta.append("-> Leucòcits baixos\n");
                        } else if (valorNumeric >= 11.0) {
                            resposta.append("-> Leucòcits alts\n");
                        } else {
                            resposta.append("-> Leucòcits dins dels límits normals\n");
                        }
                        break;

                    case "Plaquetes":
                        if (valorNumeric <=150) {
                            resposta.append("-> Plaquetes baixes\n");
                        } else if (valorNumeric >= 450) {
                            resposta.append("-> Plaquetes altes\n");
                        } else {
                            resposta.append("-> Plaquetes dins dels límits normals\n");
                        }
                        break;
                    case "Glucosa":
                        if (valorNumeric<=70) {
                            resposta.append("-> Glucosa baixa\n");
                        } else if (valorNumeric >=99) {
                            resposta.append("-> Glucosa alta\n");
                        } else {
                            resposta.append("-> Glucosa dins dels límits normals\n");
                        }
                        break;
                        case "Colesterol Total":
                        if (valorNumeric<0) {
                            resposta.append("-> Colesterol total baix\n");
                        } else if (valorNumeric >= 200) {
                            resposta.append("-> Colesterol total alt\n");
                        } else {
                            resposta.append("-> Colesterol total dins dels límits normals\n");
                        }
                        break;
                        case "Colesterol HDL":
                        if (valorNumeric<=40) {
                            resposta.append("-> Colesterol HDL baix\n");
                        } else if (valorNumeric >= 130) {
                            resposta.append("-> Colesterol HDL alt\n");
                        } else {
                            resposta.append("-> Colestrol HDL dins dels límits normals\n");
                        }
                        break;
                        case "Colesterol LDL":
                        if (valorNumeric<=0) {
                            resposta.append("-> Colesterol LDL baix\n");
                        } else if (valorNumeric >= 100) {
                            resposta.append("-> Colesterol LDL alt\n");
                        } else {
                            resposta.append("-> Colestrol LDL dins dels límits normals\n");
                        }
                        break;
                        case "Triglicèrids":
                        if (valorNumeric<=0) {
                            resposta.append("-> Triglicèrids baixos\n");
                        } else if (valorNumeric >=150) {
                            resposta.append("-> Triglicèrids alts\n");
                        } else {
                            resposta.append("-> Triglicèrids dins dels límits normals\n");
                        }
                        break;
                        case "Ferritina":
                        if ((sexe.equals("M") && valorNumeric <= 20) || (sexe.equals("F") && valorNumeric <= 13)) {
                            resposta.append("-> Ferritina baixa\n");
                        } else if ((sexe.equals("M") && valorNumeric >= 500) || (sexe.equals("F") && valorNumeric >= 400)) {
                            resposta.append("-> Ferritina alta\n");
                        } else {
                            resposta.append("-> Ferritina dins dels límits normals\n");
                        }
                        break;
                    default:
                    resposta.append("-> Paràmetre no reconegut\n");
                }
            } catch (NumberFormatException e) {
                resposta.append("-> Valor no numèric o invàlid\n");
            }
        }
        return resposta.toString();
    }
}

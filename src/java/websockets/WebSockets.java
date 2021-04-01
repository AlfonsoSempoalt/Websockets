/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package websockets;

import domain.Mensaje;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.JSONObject;

/**
 *
 * @author lv1013 Es un ejemplo de websocket donde cada mensaje que llega un
 * cliente es replicado a quienes estén conectados
 */
@ServerEndpoint("/websocketchatExa")
public class WebSockets {
    //para guardar la sesión de cada cliente y poder replicar el mensaje a cada uno
    //se hace una colección sincronizada para el manejo de la concurrencia

    private static Set<Session> clients
            = Collections.synchronizedSet(new HashSet<Session>());

    @OnOpen
    public void onOpen(Session sesion) {
        System.out.println("Open Connection ...");
        //al conectarse un cliente se abre el websocket y se guarda su sesión.
        clients.add(sesion);
    }

    @OnClose
    public void onClose(Session sesion) {
        System.out.println("Close Connection ...");
        //al cerrarse la conexión por parte del cliente se elimina su sesión en el servidor
        clients.remove(sesion);
        Mensaje mensaje = new Mensaje("", "", "", "CLIENT_MESSAGES", "");
        clientsMessage(mensaje, sesion);
    }

    @OnMessage
    public void onMessage(String message, Session sesion) {

        System.out.println("Message from the client: " + message);
        Mensaje mensaje;
        JSONObject json = new JSONObject(message);
        if (json.getString("tipoMensaje").equals("NORMAL_MESSAGE") || json.getString("tipoMensaje").equals("MENSAJE_ENTIDAD_PRIVADA")) {
            mensaje = new Mensaje(json.getString("username"), json.getString("id"), json.get("mensaje").toString(), json.getString("tipoMensaje"), json.getString("destinatario"));
        } else {
            mensaje = new Mensaje(json.getString("username"), json.getString("id"), json.getString("mensaje"), json.getString("tipoMensaje"), json.getString("destinatario"));
        }
        System.out.println(mensaje.toString());
        JSONObject jsonObject = new JSONObject(mensaje);
        messageHandler(mensaje, sesion);
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    private ArrayList<String> getClientList() {
        ArrayList<String> list = new ArrayList<>();
        for (Session c : clients) {
            list.add((String) c.getUserProperties().get("username"));
        }
        return list;
    }

    private void messageHandler(Mensaje mensaje, Session sesion) {
        switch (mensaje.getTipoMensaje()) {
            case "MESSAGE_LOGIN":
                logInMessage(mensaje, sesion);
                break;
            case "NORMAL_MESSAGE":
                normalMessage(mensaje, sesion);
                break;
            case "PRIVATE_MESSAGE":
                privateMessage(mensaje, sesion);
                break;
            case "CLIENT_MESSAGES":
                clientsMessage(mensaje, sesion);
                break;
            case "MENSAJE_ENTIDAD_PRIVADA":
                privateEntityMessage(mensaje, sesion);
                break;
            default:
                break;
        }
    }

    private void logInMessage(Mensaje message, Session session) {
        session.getUserProperties().put("username", message.getUsername());
        synchronized (clients) {
            for (Session client : clients) {
                if (!client.equals(session)) {
                    try {
                        message.setMensaje(message.getMensaje());
                        JSONObject jsonObject = new JSONObject(message);
                         client.getBasicRemote().sendText(jsonObject.toString());
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }

    private void normalMessage(Mensaje message, Session session) {
        synchronized (clients) {
            for (Session client : clients) {
                if (!client.equals(session)) {
                    try {
                        message.setMensaje(message.getUsername() + ": " + message.getMensaje());
                        JSONObject jsonObject = new JSONObject(message);
                         client.getBasicRemote().sendText(jsonObject.toString());
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }

    private void privateEntityMessage(Mensaje mess, Session sesion) {
        synchronized (clients) {
            for (Session client : clients) {
                try {
                    if (client.getUserProperties().get("username").equals(mess.getDestinatario())) {
                        JSONObject json = new JSONObject(mess);
                        String jsonString = json.get("mensaje").toString();
                        mess.setMensaje(jsonString);
                        JSONObject person = new JSONObject(mess);
                        String message=person.toString().replace("\\\\", "");
                        client.getBasicRemote().sendText(message );
                    }
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    private void privateMessage(Mensaje message, Session session) {
        synchronized (clients) {
            for (Session client : clients) {
                if (client.getUserProperties().get("username").equals(message.getDestinatario())) {
                    try {
                        message.setMensaje("Private Message" + " " + message.getMensaje());
                        JSONObject messageJson = new JSONObject(message);
                        System.out.println(messageJson.toString());
                        client.getBasicRemote().sendText(messageJson.toString());
                    } catch (IOException ex) {
                        System.out.println(ex);
                    }
                }
            }
        }
    }

    private void clientsMessage(Mensaje message, Session session) {

        synchronized (clients) {
            clients.forEach(client -> {
                try {
                    message.setMensaje(getClientList().toString());
                    JSONObject jsonObject = new JSONObject(message);
                    client.getBasicRemote().sendText(jsonObject.toString());
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            });
        }
    }

 

}

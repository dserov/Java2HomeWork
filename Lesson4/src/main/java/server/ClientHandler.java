/*
 * Copyright (C) 2018 geekbrains homework lesson5
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package server;

import java.io.*;
import java.net.Socket;

/**
 * Клиенский поток
 *
 * @author DSerov
 * @version dated March 16, 2018
 */
class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer chatServer;
    private DataInputStream in;
    private DataOutputStream out;

    ClientHandler(ChatServer chatServer, Socket socket) {
        this.socket = socket;
        this.chatServer = chatServer;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            Thread thread = Thread.currentThread();
            // цикл, пока не закрыт сокет и пока не пришла команда от родителя прервать этот поток
            while (!socket.isClosed()) {
                // Нет ли попытки прервать поток?
                if (thread.isInterrupted()) break;

                String str = in.readUTF();
                if (str.equalsIgnoreCase("/quit")) {
                    // клиент хочет закрыть соединение
                    thread.interrupt();
                    break;
                }

                // разошлем сообщение всем клиентам
                chatServer.broadcastMessage(thread.getName() + ": " + str);
            }
        } catch (IOException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


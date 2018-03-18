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

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.*;

/**
 * Сервер чата
 *
 * @author DSerov
 * @version dated March 16, 2018
 */
class ChatServer {
    final private String SERVER_BIND_ADDRESS = "localhost";
    final private int SERVER_BIND_PORT = 8189;
    final private int THREADS_POOL_SIZE = 10;
    private Vector<ClientHandler> clients = new Vector<>();
    ExecutorService threadPool; // пул для управления потоками

    public static void main(String[] args) throws Exception {
        new ChatServer();
    }

    ChatServer() throws Exception {
        InetAddress inetAddress = InetAddress.getByName(SERVER_BIND_ADDRESS);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_BIND_PORT, 0, inetAddress)) {
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setDaemon(true);
                    return thread;
                }
            };
            threadPool = Executors.newFixedThreadPool(THREADS_POOL_SIZE, threadFactory);
            System.out.println("Сервер запущен с пулом в " + THREADS_POOL_SIZE + " подключений. Ожидаем подключения.");

            // цикл для подключения новых клиентов
            while (!serverSocket.isClosed()) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println("Подключился новый клиент.");

                    // создаем поток для обслуживания нового клиента
                    ClientHandler newClient = new ClientHandler(this, socket);

                    // попробуем добавить в пул и запустить
                    threadPool.execute(newClient);

                    // поскольку предыдущее не выкинуло исключение, значит поток для клиента создан успешно
                    System.out.println("Добавим клиента в список");
                    clients.add(newClient);
                } catch (IOException e) {
                    throw new IOException(e);
                } catch (RejectedExecutionException e) {
                    System.out.println("Сброшено подключение. Пул заполнен полностью.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // попытка закрыть клиентские потоки
        shutdownAndAwaitTermination();

        System.out.println("Сервер завершил работу.");
    }

    private void shutdownAndAwaitTermination() {
        threadPool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!threadPool.awaitTermination(6, TimeUnit.SECONDS)) {
                threadPool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!threadPool.awaitTermination(6, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            threadPool.shutdownNow();
            // Preserve interrupt status
            // Thread.currentThread().interrupt();
        }
    }

    public void broadcastMessage(String message) {
        for( Iterator<ClientHandler> iterator = clients.iterator(); iterator.hasNext(); ) {
            ClientHandler handler = iterator.next();
            if (handler.getSocket().isClosed())
                iterator.remove();
        }
        clients.forEach(o -> {
            o.sendMessage("Clients count = " + clients.size());
            o.sendMessage(message);
        });
    }
}


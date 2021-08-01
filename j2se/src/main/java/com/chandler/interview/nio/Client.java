package com.chandler.interview.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {

    private final static int SERVER_PORT = 8888;

    public static void main(String[] args) throws IOException, InterruptedException {
        ClientRunner client1 = new ClientRunner("aaa");
//        ClientRunner client2 = new ClientRunner("bbb");

        client1.start();
//        client2.start();
//
//        Thread.sleep(10000);
        Thread.currentThread().join();
    }

    public static class ClientRunner extends Thread {

        private String name;

        // 缓冲区的大小
        private final static int BUFFER_SIZE = 1024;
        // 缓冲区
        private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

        // 选择器
        private Selector selector = null;

        public ClientRunner(String name) {
            this.name = name;
        }

        public boolean init(String address) throws IOException {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            //发起连接
            return socketChannel.connect(new InetSocketAddress(address, SERVER_PORT));
        }

        public void handleConnect() throws IOException {
            while (true) {
                int readyChannels = selector.select();
                if (readyChannels <= 0) {
                    continue;
                }

                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator iterator = selectionKeySet.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = (SelectionKey) iterator.next();
                    handleKey(selectionKey);
                    iterator.remove();
                }
            }
        }

        private void handleKey(SelectionKey selectionKey) throws IOException {
            //是否可连接
            if (selectionKey.isConnectable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                //完成连接
                if (socketChannel.isConnectionPending()) {
                    socketChannel.finishConnect();
                    System.out.println("连接成功...");
                    String message = "Hello, Server, I am " + name;
                    buffer.clear();
                    buffer.put(message.getBytes());
                    buffer.flip();
                    socketChannel.write(buffer);
                    System.out.println(name + "发送数据给Server: " + message);
                    registerChannel(selector, socketChannel, SelectionKey.OP_READ);
                } else {
                    //连接失败 退出
                    System.exit(1);
                }
            }

            if (selectionKey.isReadable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                buffer.clear();
                int len = 0;
                while ((len = socketChannel.read(buffer)) > 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        byte[] data = new byte[len];
                        buffer.get(data, 0, len);
                        System.out.println(name + "收到服务端的数据: " + new String(data));
                    }
                }
                if (len < 0) {
                    socketChannel.close();
                }
                registerChannel(selector, socketChannel, SelectionKey.OP_WRITE);
            }

            if (selectionKey.isWritable()) {
                SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                buffer.clear();
                String message = "Hello, Server..." + socketChannel.getLocalAddress() + " I am " + name;
                buffer.put(message.getBytes());
                buffer.flip();
                System.out.println(name + "向服务端写出的数据: " + new String(buffer.array()));
                socketChannel.write(buffer);
                registerChannel(selector, socketChannel, SelectionKey.OP_READ);
            }

        }

        private void registerChannel(Selector selector, SelectableChannel channel, int ops) throws IOException {
            if (channel == null) {
                return;
            }
            channel.configureBlocking(false);
            // 注册通道
            channel.register(selector, ops);
        }

        public void run() {
            try {
                init("127.0.0.1");
                handleConnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}

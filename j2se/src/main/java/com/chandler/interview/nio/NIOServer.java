package com.chandler.interview.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * https://blog.csdn.net/weixin_36255893/article/details/114427410
 * 单线程模型的NIO Server实现
 * 所有工作放在一个线程中处理，很明显可靠性较低且性能不高。从事件属性上讲，包括：accept事件、read/write事件。
 *
 * 从任务属性上讲，包括io任务(r/w data)，read/write数据的处理(对data的业务处理)等事件任务(处理事件)accept新连接进来将新连接的socket注册到selector
 *
 * read读缓冲区有数据数据解码、进行业务处理
 *
 * write写缓冲区有空闲数据编码，写入socket send buffer
 *
 * 对于server端，连接建立得到socket后，要为新建立的socket的注册selector
 */
public class NIOServer {

    private static final int BUFFER_SIZE = 1024;

    private ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

    private static final int PORT = 8888;

    //选择器
    private Selector selector = null;

    //初始化工作
    public void init(int port) throws IOException {
        System.out.println("============ Listening On Port: " + port + "============");
        //打开服务器套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress("0.0.0.0", port));
        //打开一个选择器
        selector = Selector.open();
        // 服务器套接字注册到Selector中 并指定Selector监控连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void listen() throws IOException {
        while (true) {
            //阻塞, 返回就绪通道的个数
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }
            //返回已选择键的集合
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            //遍历键
            Iterator iterator = selectionKeySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = (SelectionKey) iterator.next();
                handleKey(selectionKey);
                iterator.remove();
            }

        }
    }

    private void handleKey(SelectionKey selectionKey) throws IOException {
        if (selectionKey.isAcceptable()) {
            //获取通道
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            //获取客户端socket
            SocketChannel socketChannel = server.accept();
            socketChannel.configureBlocking(false);
            registerChannel(selector, socketChannel, SelectionKey.OP_READ);
            if (socketChannel.isConnected()) {
                buffer.clear();
                buffer.put("I am Server...".getBytes());
                buffer.flip();
                socketChannel.write(buffer);
            }
        }
        //通道的可读事件就绪
        if (selectionKey.isReadable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            buffer.clear();
            int len = 0;
            while ((len = socketChannel.read(buffer)) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte[] data = new byte[len];
                    buffer.get(data, 0, len);
                    System.out.println("Server 读到数据：" + new String(data));
                }
            }
            if (len < 0) {
                //非法的selectionKey 关闭channel
                System.out.println("非法的selectionKey 关闭channel");
                socketChannel.close();
            }
            registerChannel(selector, socketChannel, SelectionKey.OP_WRITE);
        }
        //通道可写事件就绪
        if (selectionKey.isWritable()) {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            buffer.clear();

            String message = "Hello, Client..." + socketChannel.getLocalAddress();
            buffer.put(message.getBytes());
            buffer.flip();
            socketChannel.write(buffer);
            System.out.println("Server 发送数据：" + message);
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

    public static void main(String[] args) throws IOException {
        NIOServer server = new NIOServer();
        server.init(PORT);
        server.listen();
    }


}

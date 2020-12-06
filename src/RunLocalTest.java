import junit.framework.TestCase;
import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.util.*;
import java.lang.reflect.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

//TODO Implement Method Testing in Main
public class RunLocalTest {
    /**
     * Main Method to Run Tests
     */
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Excellent - Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }

    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        /**
         * Req 1: Each class must have a test verifying that it exists
         * and inherits from the correct superclass
         */
        @Test
        public void testMessageHandlerExists() {
            try {
                Class<?> clazz = Class.forName("MessageHandler");
                if (!Runnable.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `MessageHandler` implements Runnable");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `MessageHandler` class");

            }
        }

        @Test
        public void testMessageClientExists() {
            try {
                Class<?> clazz = Class.forName("MessageClient");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure that `MessageClient` extends `Object`");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `MessageClient` class");

            }
        }

        @Test
        public void testMessageServerExists() {
            try {
                Class<?> clazz = Class.forName("MessageServer");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `MessageServer` extends Object");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `MessageServer` class");

            }
        }

        @Test
        public void testLoginGUIExists() {
            try {
                Class<?> clazz = Class.forName("LoginGUI");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `LoginGUI` extends JFrame");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `LoginGUI` class");

            }
        }

        @Test
        public void testChatGUIExists() {
            try {
                Class<?> clazz = Class.forName("ChatGUI");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `ChatGUI` extends JFrame");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `ChatGUI` class");

            }
        }

        @Test
        public void testDisplayMessageGUIExists() {
            try {
                Class<?> clazz = Class.forName("DisplayMessageGUI");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `DisplayMessageGUI` extends JPanel");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `DisplayMessageGUI` class");

            }
        }

        @Test
        public void testClientManagerExists() {
            try {
                Class<?> clazz = Class.forName("ClientManager");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `ClientManager` extends Object");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `ClientManager` class");

            }
        }

        @Test
        public void testConversationExists() {
            try {
                Class<?> clazz = Class.forName("Conversation");
                if (!Object.class.isAssignableFrom(clazz)) {
                    Assert.fail("Make sure `Conversation` extends Object");
                }
            } catch (ClassNotFoundException e) {
                Assert.fail("Cannot find `Conversation` class");

            }
        }


        /**
         * Req 2: Each field in every class must have a test verifying that it exists,
         * along with verifying it has the correct type and access modifier.
         */

        //For MessageHandler
        @Test
        public void testMessageHandlerDeclarations() {
            // accountList
            try {
                Field field = MessageHandler.class.getDeclaredField("accountList");
                if (field.getType() != File.class) {
                    fail("The field `accountList` in MessageHandler is not a type of File");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `accountList` in MessageHandler is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `accountList` in MessageHandler");
                e.printStackTrace();
                return;
            }

            // CLIENT_SOCKET
            try {
                Field field = MessageHandler.class.getDeclaredField("CLIENT_SOCKET");
                if (field.getType() != Socket.class) {
                    fail("The field `CLIENT_SOCKET` in MessageHandler is not a type of Socket");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `CLIENT_SOCKET` in MessageHandler is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `CLIENT_SOCKET` in MessageHandler");
                e.printStackTrace();
                return;
            }

            // GATE_KEEPER
            try {
                Field field = MessageHandler.class.getDeclaredField("GATE_KEEPER");
                if (field.getType() != Object.class) {
                    fail("The field `GATE_KEEPER` in MessageHandler is not a type of Object");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `GATE_KEEPER` in MessageHandler is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `GATE_KEEPER` in MessageHandler");
                e.printStackTrace();
                return;
            }

            // clientWriter
            try {
                Field field = MessageHandler.class.getDeclaredField("clientWriter");
                if (field.getType() != BufferedWriter.class) {
                    fail("The field `clientWriter` in MessageHandler is not a type of BufferedWriter");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `clientWriter` in MessageHandler is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `clientWriter` in MessageHandler");
                e.printStackTrace();
                return;
            }

            //clientMessage
            try {
                Field field = MessageHandler.class.getDeclaredField("clientMessage");
                if (field.getType() != String.class) {
                    fail("The field `clientMessage` in MessageHandler is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `clientMessage` in MessageHandler is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `clientMessage` in MessageHandler");
                e.printStackTrace();
                return;
            }

            //currentClientUsername
            try {
                Field field = MessageHandler.class.getDeclaredField("currentClientUsername");
                if (field.getType() != String.class) {
                    fail("The field `currentClientUsername` in MessageHandler is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `currentClientUsername` in MessageHandler is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `currentClientUsername` in MessageHandler");
                e.printStackTrace();
                return;
            }
        }

        //For MessageClient
        @Test
        public void testMessageClientDeclarations() {
            //clientMessage
            try {
                Field field = MessageClient.class.getDeclaredField("clientMessage");
                if (field.getType() != String.class) {
                    fail("The field `clientMessage` in MessageClient is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `clientMessage` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `clientMessage` in MessageClient");
                e.printStackTrace();
                return;
            }

            //clientUsername
            try {
                Field field = MessageClient.class.getDeclaredField("clientUsername");
                if (field.getType() != String.class) {
                    fail("The field `clientUsername` in MessageClient is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `clientUsername` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `clientUsername` in MessageClient");
                e.printStackTrace();
                return;
            }

            //TITLE
            try {
                Field field = MessageClient.class.getDeclaredField("TITLE");
                if (field.getType() != String.class) {
                    fail("The field `TITLE` in MessageClient is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC + Modifier.FINAL) {
                    fail("The field `TITLE` in MessageClient is not private, static, and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `TITLE` in MessageClient");
                e.printStackTrace();
                return;
            }

            // chatGUI
            try {
                Field field = MessageClient.class.getDeclaredField("chatGUI");
                if (field.getType() != ChatGUI.class) {
                    fail("The field `chatGUI` in MessageClient is not a type of ChatGUI");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `chatGUI` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `chatGUI` in MessageClient");
                e.printStackTrace();
                return;
            }

            //socket
            try {
                Field field = MessageClient.class.getDeclaredField("socket");
                if (field.getType() != Socket.class) {
                    fail("The field `socket` in MessageClient is not a type of Socket");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `socket` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `socket` in MessageClient");
                e.printStackTrace();
                return;
            }

            //reader
            try {
                Field field = MessageClient.class.getDeclaredField("reader");
                if (field.getType() != BufferedReader.class) {
                    fail("The field `reader` in MessageClient is not a type of BufferedReader");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `reader` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `reader` in MessageClient");
                e.printStackTrace();
                return;
            }

            //writer
            try {
                Field field = MessageClient.class.getDeclaredField("writer");
                if (field.getType() != BufferedWriter.class) {
                    fail("The field `writer` in MessageClient is not a type of BufferedWriter");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `writer` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `writer` in MessageClient");
                e.printStackTrace();
                return;
            }

            //conversations
            try {
                Field field = MessageClient.class.getDeclaredField("conversations");
                if (field.getType() != ArrayList.class) {
                    fail("The field `conversations` in MessageClient is not a type of ArrayList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `conversations` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `conversations` in MessageClient");
                e.printStackTrace();
                return;
            }

            //loginOrRegister
            try {
                Field field = MessageClient.class.getDeclaredField("loginOrRegister");
                if (field.getType() != AtomicBoolean.class) {
                    fail("The field `loginOrRegister` in MessageClient is not a type of AtomicBoolean");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `loginOrRegister` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `loginOrRegister` in MessageClient");
                e.printStackTrace();
                return;
            }

            //loginRegisterClicked
            try {
                Field field = MessageClient.class.getDeclaredField("loginRegisterClicked");
                if (field.getType() != AtomicBoolean.class) {
                    fail("The field `loginRegisterClicked` in MessageClient is not a type of AtomicBoolean");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `loginRegisterClicked` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `loginRegisterClicked` in MessageClient");
                e.printStackTrace();
                return;
            }

            //sendMessageClicked
            try {
                Field field = MessageClient.class.getDeclaredField("sendMessageClicked");
                if (field.getType() != AtomicBoolean.class) {
                    fail("The field `sendMessageClicked` in MessageClient is not a type of AtomicBoolean");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `sendMessageClicked` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `sendMessageClicked` in MessageClient");
                e.printStackTrace();
                return;
            }

            //checkUserAccountsExisting
            try {
                Field field = MessageClient.class.getDeclaredField("checkUserAccountsExisting");
                if (field.getType() != AtomicBoolean.class) {
                    fail("The field `checkUserAccountsExisting` in MessageClient is not a type of AtomicBoolean");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `checkUserAccountsExisting` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `checkUserAccountsExisting` in MessageClient");
                e.printStackTrace();
                return;
            }

            //userAccountsExist
            try {
                Field field = MessageClient.class.getDeclaredField("userAccountsExist");
                if (field.getType() != AtomicBoolean.class) {
                    fail("The field `userAccountsExist` in MessageClient is not a type of AtomicBoolean");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `userAccountsExist` in MessageClient is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `userAccountsExist` in MessageClient");
                e.printStackTrace();
                return;
            }
        }

        //For MessageServer
        @Test
        public void testMessageServerDeclarations() {
            // SERVER_SOCKET
            try {
                Field field = MessageServer.class.getDeclaredField("SERVER_SOCKET");
                if (field.getType() != ServerSocket.class) {
                    fail("The field `SERVER_SOCKET` in MessageServer is not a type of ServerSocket");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `SERVER_SOCKET` in MessageServer is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `SERVER_SOCKET` in MessageServer");
                e.printStackTrace();
                return;
            }

            //identity
            try {
                Field field = MessageServer.class.getDeclaredField("identity");
                if (field.getType() != String.class) {
                    fail("The field `identity` in MessageServer is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `identity` in MessageServer is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `identity` in MessageServer");
                e.printStackTrace();
                return;
            }
        }

        //For LoginGUI
        @Test
        public void testLoginGUIDeclarations() {
            //CLIENT
            try {
                Field field = LoginGUI.class.getDeclaredField("CLIENT");
                if (field.getType() != MessageClient.class) {
                    fail("The field `CLIENT` in LoginGUI is not a type of MessageClient");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `CLIENT` in LoginGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `CLIENT` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //TITLE
            try {
                Field field = LoginGUI.class.getDeclaredField("TITLE");
                if (field.getType() != String.class) {
                    fail("The field `TITLE` in LoginGUI is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL + Modifier.STATIC) {
                    fail("The field `TITLE` in LoginGUI is not private and/or final and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `TITLE` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //frame
            try {
                Field field = LoginGUI.class.getDeclaredField("frame");
                if (field.getType() != JFrame.class) {
                    fail("The field `frame` in LoginGUI is not a type of JFrame");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `frame` in LoginGUI is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `frame` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //userLbl
            try {
                Field field = LoginGUI.class.getDeclaredField("userLbl");
                if (field.getType() != JLabel.class) {
                    fail("The field `userLbl` in LoginGUI is not a type of JLabel");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `frame` in LoginGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `frame` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //passLbl
            try {
                Field field = LoginGUI.class.getDeclaredField("passLbl");
                if (field.getType() != JLabel.class) {
                    fail("The field `passLbl` in LoginGUI is not a type of JLabel");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `passLbl` in LoginGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `passLbl` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //loginBtn
            try {
                Field field = LoginGUI.class.getDeclaredField("loginBtn");
                if (field.getType() != JButton.class) {
                    fail("The field `loginBtn` in LoginGUI is not a type of JButton");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `loginBtn` in LoginGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `loginBtn` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //userText
            try {
                Field field = LoginGUI.class.getDeclaredField("userText");
                if (field.getType() != JTextField.class) {
                    fail("The field `userText` in LoginGUI is not a type of JTextField");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `userText` in LoginGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `userText` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //passText
            try {
                Field field = LoginGUI.class.getDeclaredField("passText");
                if (field.getType() != JPasswordField.class) {
                    fail("The field `passText` in LoginGUI is not a type of JPasswordField");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `passText` in LoginGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `passText` in LoginGUI");
                e.printStackTrace();
                return;
            }

            //actionListener
            try {
                Field field = LoginGUI.class.getDeclaredField("actionListener");
                if (field.getType() != ActionListener.class) {
                    fail("The field `actionListener` in LoginGUI is not a type of ActionListener");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `actionListener` in LoginGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `actionListener` in LoginGUI");
                e.printStackTrace();
                return;
            }
        }

        //For ChatGUI
        @Test
        public void testChatGUIDeclarations() {
            //MESSAGE_CLIENT
            try {
                Field field = ChatGUI.class.getDeclaredField("MESSAGE_CLIENT");
                if (field.getType() != MessageClient.class) {
                    fail("The field `MESSAGE_CLIENT` in ChatGUI is not a type of MessageClient");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `MESSAGE_CLIENT` in ChatGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `MESSAGE_CLIENT` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //conversation
            try {
                Field field = ChatGUI.class.getDeclaredField("conversations");
                if (field.getType() != ArrayList.class) {
                    fail("The field `conversation` in ChatGUI is not a type of ArrayList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `conversation` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `conversation` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //CLIENT_USERNAME
            try {
                Field field = ChatGUI.class.getDeclaredField("CLIENT_USERNAME");
                if (field.getType() != String.class) {
                    fail("The field `CLIENT_USERNAME` in ChatGUI is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `CLIENT_USERNAME` in ChatGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `CLIENT_USERNAME` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //usersToSend
            try {
                Field field = ChatGUI.class.getDeclaredField("usersToSend");
                if (field.getType() != ArrayList.class) {
                    fail("The field `usersToSend` in ChatGUI is not a type of ArrayList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `usersToSend` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `usersToSend` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //allMessages
            try {
                Field field = ChatGUI.class.getDeclaredField("allMessages");
                if (field.getType() != Map.class) {
                    fail("The field `allMessages` in ChatGUI is not a type of Map");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `allMessages` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `allMessages` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //middlePanel
            try {
                Field field = ChatGUI.class.getDeclaredField("middlePanel");
                if (field.getType() != JPanel.class) {
                    fail("The field `middlePanel` in ChatGUI is not a type of JPanel");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `middlePanel` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `frame` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //sendButton
            try {
                Field field = ChatGUI.class.getDeclaredField("sendButton");
                if (field.getType() != JButton.class) {
                    fail("The field `sendButton` in ChatGUI is not a type of JButton");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `sendButton` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `sendButton` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //newChatButton
            try {
                Field field = ChatGUI.class.getDeclaredField("newChatButton");
                if (field.getType() != JButton.class) {
                    fail("The field `newChatButton` in ChatGUI is not a type of JButton");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `newChatButton` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `newChatButton` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //messageText
            try {
                Field field = ChatGUI.class.getDeclaredField("messageText");
                if (field.getType() != JTextArea.class) {
                    fail("The field `messageText` in ChatGUI is not a type of JTextArea");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `messageText` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `messageText` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //inboxList
            try {
                Field field = ChatGUI.class.getDeclaredField("inboxList");
                if (field.getType() != JList.class) {
                    fail("The field `inboxList` in ChatGUI is not a type of JList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `inboxList` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `inboxList` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //inboxes
            try {
                Field field = ChatGUI.class.getDeclaredField("inboxes");
                if (field.getType() != DefaultListModel.class) {
                    fail("The field `inboxes` in ChatGUI is not a type of DefaultListModel");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `inboxes` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `inboxes` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //messageFrame
            try {
                Field field = ChatGUI.class.getDeclaredField("messageFrame");
                if (field.getType() != JFrame.class) {
                    fail("The field `messageFrame` in ChatGUI is not a type of JFrame");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `messageFrame` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `messageFrame` in ChatGUI");
                e.printStackTrace();
                return;
            }
            //messageField
            try {
                Field field = ChatGUI.class.getDeclaredField("messageField");
                if (field.getType() != DisplayMessageGUI.class) {
                    fail("The field `messageField` in ChatGUI is not a type of DisplayMessageGUI");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `messageField` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `messageField` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //DELETE_INSTRUCTION
            try {
                Field field = ChatGUI.class.getDeclaredField("DELETE_INSTRUCTION");
                if (field.getType() != JLabel.class) {
                    fail("The field `DELETE_INSTRUCTION` in ChatGUI is not a type of JLabel");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `DELETE_INSTRUCTION` in ChatGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `DELETE_INSTRUCTION` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //actionListener
            try {
                Field field = ChatGUI.class.getDeclaredField("actionListener");
                if (field.getType() != ActionListener.class) {
                    fail("The field `actionListener` in ChatGUI is not a type of ActionListener");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `actionListener` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `actionListener` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //focusListener
            try {
                Field field = ChatGUI.class.getDeclaredField("focusListener");
                if (field.getType() != FocusListener.class) {
                    fail("The field `focusListener` in ChatGUI is not a type of FocusListener");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `focusListener` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `focusListener` in ChatGUI");
                e.printStackTrace();
                return;
            }

            //mouseListener
            try {
                Field field = ChatGUI.class.getDeclaredField("mouseListener");
                if (field.getType() != MouseListener.class) {
                    fail("The field `mouseListener` in ChatGUI is not a type of MouseListener");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `mouseListener` in ChatGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `mouseListener` in ChatGUI");
                e.printStackTrace();
                return;
            }
        }

        //For DisplayMessageGUI
        @Test
        public void testDisplayMessageGUIDeclarations() {
            // CLIENT
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("CLIENT");
                if (field.getType() != MessageClient.class) {
                    fail("The field `CLIENT` in DisplayMessageGUI is not a type of MessageClient");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `CLIENT` in DisplayMessageGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `CLIENT` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }

            // CLIENT_USERNAME
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("CLIENT_USERNAME");
                if (field.getType() != String.class) {
                    fail("The field `CLIENT_USERNAME` in DisplayMessageGUI is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `CLIENT_USERNAME` in DisplayMessageGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `CLIENT_USERNAME` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }

            // MESSAGE_LABEL
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("MESSAGE_LABEL");
                if (field.getType() != String.class) {
                    fail("The field `MESSAGE_LABEL` in DisplayMessageGUI is not a type of String");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
                    fail("The field `MESSAGE_LABEL` in DisplayMessageGUI is not private and/or final");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `MESSAGE_LABEL` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }

            // conversation
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("conversation");
                if (field.getType() != Conversation.class) {
                    fail("The field `conversation` in DisplayMessageGUI is not a type of Conversation");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `conversation` in DisplayMessageGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `conversation` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }

            // list
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("list");
                if (field.getType() != DefaultListModel.class) {
                    fail("The field `list` in DisplayMessageGUI is not a type of DefaultListModel");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `list` in DisplayMessageGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `list` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }

            // messages
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("messages");
                if (field.getType() != JList.class) {
                    fail("The field `messages` in DisplayMessageGUI is not a type of JList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `messages` in DisplayMessageGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `messages` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }

            // mouseListener
            try {
                Field field = DisplayMessageGUI.class.getDeclaredField("mouseListener");
                if (field.getType() != MouseListener.class) {
                    fail("The field `mouseListener` in DisplayMessageGUI is not a type of MouseListener");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `mouseListener` in DisplayMessageGUI is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `mouseListener` in DisplayMessageGUI");
                e.printStackTrace();
                return;
            }
        }

        //For ClientManager
        @Test
        public void testClientManagerDeclarations() {
            // deliverTo
            try {
                Field field = ClientManager.class.getDeclaredField("deliverTo");
                if (field.getType() != HashMap.class) {
                    fail("The field `deliverTo` in ClientManager is not a type of HashMap");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
                    fail("The field `deliverTo` in ClientManager is not private and/or static");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `deliverTo` in ClientManager");
                e.printStackTrace();
                return;
            }
        }

        //For Conversation
        @Test
        public void testConversationDeclarations() {
            // messages
            try {
                Field field = Conversation.class.getDeclaredField("messages");
                if (field.getType() != ArrayList.class) {
                    fail("The field `messages` in Conversation is not a type of ArrayList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `messages` in Conversation is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `messages` in Conversation");
                e.printStackTrace();
                return;
            }

            // members
            try {
                Field field = Conversation.class.getDeclaredField("members");
                if (field.getType() != ArrayList.class) {
                    fail("The field `members` in Conversation is not a type of ArrayList");
                    return;
                }
                if (field.getModifiers() != Modifier.PRIVATE) {
                    fail("The field `members` in Conversation is not private");
                    return;
                }
            } catch (NoSuchFieldException e) {
                fail("Cannot find the field `members` in Conversation");
                e.printStackTrace();
                return;
            }
        }


        /**
         * Req 3: Each method in every class must have a test verifying that it exists,
         * along with verifying it has the correct return type and access modifier.
         */

    }
}
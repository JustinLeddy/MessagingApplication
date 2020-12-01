import org.junit.*;

import javax.swing.*;
import java.net.ServerSocket;
import java.util.*;
import java.io.File;
import java.lang.reflect.*;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class TestCases {

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

	/**
	 * Req 2: Each field in every class must have a test verifying that it exists,
	 * along with verifying it has the correct type and access modifier.
	 */

	//For MessageHandler
	@Test
	public void testMessageHandlerDeclarations() {
		try {
			Field field = MessageHandler.class.getDeclaredField("accountList"); // accountList
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
		try {
			Field field = MessageHandler.class.getDeclaredField("clientSocket"); // clientSocket
			if (field.getType() != Socket.class) {
				fail("The field `clientSocket` in MessageHandler is not a type of Socket");
				return;
			}
			if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
				fail("The field `clientSocket` in MessageHandler is not private and/or final");
				return;
			}
		} catch (NoSuchFieldException e) {
			fail("Cannot find the field `clientSocket` in MessageHandler");
			e.printStackTrace();
			return;
		}

		try {
			Field field = MessageHandler.class.getDeclaredField("gateKeeper"); // gatekeeper? (dont know if needed)
			if (field.getType() != Object.class) {
				fail("The field `gateKeeper` in MessageHandler is not a type of Object");
				return;
			}
			if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
				fail("The field `gateKeeper` in MessageHandler is not private and/or final");
				return;
			}
		} catch (NoSuchFieldException e) {
			fail("Cannot find the field `gateKeeper` in MessageHandler");
			e.printStackTrace();
			return;
		}
	}

	//For MessageClient
	@Test
	public void testMessageClientDeclarations() {
		try {
			Field field = MessageClient.class.getDeclaredField("clientMessage"); // clientMessage
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

		try {
			Field field = MessageClient.class.getDeclaredField("clientUsername"); // clientUsername
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

		try {
			Field field = MessageClient.class.getDeclaredField("TITLE"); // title? (dont know if needed)
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

		try {
			Field field = MessageClient.class.getDeclaredField("messageSent"); //messageSent
			if (field.getType() != AtomicBoolean.class) {
				fail("The field `messageSent` in MessageClient is not a type of AtomicBoolean");
				return;
			}
			if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
				fail("The field `messageSent` in MessageClient is not private and/or static");
				return;
			}
		} catch (NoSuchFieldException e) {
			fail("Cannot find the field `messageSent` in MessageClient");
			e.printStackTrace();
			return;
		}
		try {
			Field field = MessageClient.class.getDeclaredField("frame"); //frame
			if (field.getType() != JFrame.class) {
				fail("The field `frame` in MessageClient is not a type of JFrame");
				return;
			}
			if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
				fail("The field `frame` in MessageClient is not private and/or static");
				return;
			}
		} catch (NoSuchFieldException e) {
			fail("Cannot find the field `JFrame` in MessageClient");
			e.printStackTrace();
			return;
		}
	}

	//For MessageServer
	@Test
	public void testMessageServerDeclarations() {
		try {
			Field field = MessageServer.class.getDeclaredField("serverSocket"); // serverSocket
			if (field.getType() != ServerSocket.class) {
				fail("The field `serverSocket` in MessageServer is not a type of ServerSocket");
				return;
			}
			if (field.getModifiers() != Modifier.PRIVATE + Modifier.FINAL) {
				fail("The field `serverSocket` in MessageServer is not private and/or final");
				return;
			}
		} catch (NoSuchFieldException e) {
			fail("Cannot find the field `serverSocket` in MessageServer");
			e.printStackTrace();
			return;
		}

		/**
		 * Req 3: Each method in every class must have a test verifying that it exists,
		 * along with verifying it has the correct return type and access modifier.
		 */

	}
}
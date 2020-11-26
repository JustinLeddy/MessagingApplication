import org.junit.*;
import java.util.*;
import java.io.File;
import java.lang.reflect.*;
import java.net.Socket;

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
			if (!clazz.isAssignableFrom(Runnable.class)) {
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
			if (!clazz.isAssignableFrom(Object.class)) {
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
			if (!clazz.isAssignableFrom(Object.class)) {
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
			Field field = MessageHandler.class.getDeclaredField("accountList"); // acountList
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
		// gatekeeper? (dont know if needed)
		try {
			Field field = MessageHandler.class.getDeclaredField("gateKeeper"); 
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
			Field field = MessageClient.class.getDeclaredField("clientMessage"); // acountList
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
			Field field = MessageClient.class.getDeclaredField("scenePhase"); // clientSocket
			if (field.getType() != int.class) {
	            fail("The field `scenePhase` in MessageClient is not a type of int");
	            return;
	        }
	        if (field.getModifiers() != Modifier.PRIVATE + Modifier.STATIC) {
	            fail("The field `scenePhase` in MessageClient is not private and/or static");
	            return;
	        }
	    } catch (NoSuchFieldException e) {
	        fail("Cannot find the field `scenePhase` in MessageClient");
	        e.printStackTrace();
	        return;
	    }
		// title? (dont know if needed)
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
	}
		
	//For MessageServer : no fields for now
		
	/**
	 * Req 3: Each method in every class must have a test verifying that it exists, 
	 * along with verifying it has the correct return type and access modifier. 
	 */
				
}

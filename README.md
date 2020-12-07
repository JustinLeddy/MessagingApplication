# Overview of the Project
This is a project about an instant messaging program. Users are able to create their own accounts and send messages to other users. Users can receive these messages no matter if they are online or offline and they can also edit the messages they receive or send.

# Features of the Project
* A complete GUI interaction with users
* Simultaneous connections
* Private individual and group chats
* Creating, deleting, and editing accounts
* Creating, deleting, and editing messages and conversations
* Notifying users about unread messages
* Account and chat backups through files
##Expected errors
* Delete conversations feature is not permanent (details in GUI testing below).
* Unreliable internet connection may cause new chats to be created with unregistered users

# Description of each class in the project

## MessageClient
A class to deal with the client and server interactions, handle client information and the integration of GUI classes  It also initializes and updates a conversations messages and members every time there are new changes for that conversation.

### setClientMessageLoginRegister
A method that takes in three parameters: a boolean variable for indicating whether to login or register, a String that represents a username, and an array of chars that represents the password. Using these parameters, the method sets the field clientMessage in the correct format to be sent to the server.


### setClientMessageMessaging
A method that takes in two parameters: a String message to send and an ArrayList of Strings that includes the members the message should be sent to. The method takes these parameters and sets the field clientMessage to the correct format when sending a message to a group.
### setClientMessageDeleteAccount
A method that sets a client message for deleting the account using the proper format. An important thing to keep in mind when using this method is to make sure to send a leave conversation request to the server for every conversation the user is in before sending this message. This will make sure they are removed from every chat they are a member of.
### setClientMessageChangePassword
A method that takes in a String as a parameter that represents the new password and sets the client message for changing the account password using the proper format.
### setClientMessageNewChat
A method that takes an ArrayList of Strings as a parameter that represents users to send to. This method allows the user to create a new chat as long as intended recipients are valid users. The method sets the clientMessage to the correct format and then checks if all the users (in the parameter ArrayList) have accounts and are valid users to send the message to. 
### setClientMessageDeleteUser
A method that allows the user to be deleted from the conversation. After making sure that the client message is set to the right command, using the current client, the method deletes the user from that conversation.
### setClientMessageUpdateChat
A method that takes in a Conversation as a parameter and uses it to send it to the server (after formatting it properly) where the conversation file can be updated there while notifying other members of the change.
### message
A method that takes in two parameters: a String that represents the methods and an int that represents the type of optionPane that should be displayed. Taking these parameters, the method shows a JOptionPane message dialog.
### connect
A method that is used to connect to the server hosted locally at port 8888; to connect to a remote server, change ‘localhost’ to the server’s IP address.
### main
The main method for MessageClient initializes the client GUI and handles all server communication. This includes checking whether the user wanted to login or register, checking whether an account exists or not, whether a proposed username for an account is available or not, etc, and then conveying that message to the server. Essentially, it ensures that according to the user’s interactions with the GUI, the server can then correctly execute the needed commands. Then it reads the information sent back from the server and sets up the conversation accordingly by calling the appropriate methods. The method also displays error messages when necessary.
### updateConversation
A method that takes in a String as a parameter which represents the message sent from the server. The method updates the conversation when a message has changed or a user has been deleted.
### initializeConversation
A method that takes in a String as a parameter that represents a line sent by the server. The method then uses it to initialize the array of conversations according to the proper formatting.
### getConversations
A method that returns the array of conversations.
### getClientUsername
A method that returns the client’s username.
### setClientUsername
A method to set the client username field using the String parameter the method takes in. 
### setLoginRegisterClicked
A method to set a boolean value indicating whether to login or register the user.
### setSendMessageClicked
A method that takes in a boolean as a parameter which is used to set a boolean value indicating that the user wants to send a message. 
### setLoginOrRegister
A method that takes in a boolean as a parameter which is used to set a boolean value indicating whether the user wants to login or register.


### setCheckUserAccountExisting
A method to set the field AtomicBoolean checkUserAccountExisting which tells the client to expect a message from the server, which will be used to set the field AtomicBoolean userAccountExist.
### getUserAccountExists
A method that returns the boolean value indicating whether the user account already exists or not.
### setUserAccountExists
A method that takes in a boolean as a parameter which is used to set a boolean value indicating whether the user account already exists or not.

## MessageHandler
A class that handles all communication strings from the client, processes them into the correct format, and broadcasts all the input strings to intended users if the input strings received count as a message. It also decides whether a user’s account exists or not in the server database and whether the client’s username and password match and gives out a corresponding order for the MessageClient class about its login status. It also handles editing and deleting messages, user account changes, various informational exchanges between the client, all conversation updates, and writes all necessary information to the file.
### MessageHandler Constructor
A constructor for the MessageHandler class that takes in a Socket as a parameter and initializes the Socket, as well as the input/output streams, the reader/writer using said socket.
### run
A run method for the Messagehandler class that is used every time a new Thread is started. It handles all the communication with the given client. This includes sending messages, handling files for storing conversations, deleting/updating accounts and passwords, processing logging in, registering and editing and deleting accounts.

### send
A method that takes in a String as a parameter and then sends the message to the client this handler is connected to.
### getClientSocket
A method that returns the client socket.
### getCurrentClientUsername
A method that returns the current client’s username.
### setCurrentClientUsername
A method that takes in String as a parameter that represents the current client’s username and initializes the currentClientUsername field.
## Testing
This method was tested using the debugging window and console printing to verify that inputs and its various outputs sent the correct Strings. For example, to test if the message was being sent to each user correctly I added breakpoints to the array and watched the message match each client and call their send method. I then had the client print the received message to the console so I could see that it was indeed being sent to the correct clients. I then had a breakpoint at the line where it updates the conversation array to verify that the message was added to the correct conversation in the correct format and order.

## MessageServer
A server that will always accept client socket connection requests. It adds the user identity as a key with a MessageHandler instance as a value into the ClientManager class HashMap every time a connection is made.
### MessageServer Constructor
A constructor for the MessageServer class that takes in an integer port and sets the server socket at the given port. It also throws an IOException exception when there is a connection error with the socket.
### serveClient
A method that spawns a Thread to serve each client to connect with the server. It also adds each new client to the hashmap of all the connected clients in the ClientManager class. It throws an InterruptedException exception when there is an interruption with the connection.
### main
The main method that runs the server. It throws an InterruptedException exception when there is an interruption with the connection.

## LoginGUI
Graphic User Interface that displays a login/register window for the users. It will be the first window that pops out to the user when the program launches. This class takes the information, including username and password, from the user and sends this information to the correct method in the MessageClient to process. A window will pop up after the user hits Login or Register to notify if their credentials are valid or not. 

[![Screen-Shot-2020-12-04-at-4-57-26-AM.png](https://i.postimg.cc/FRfwzkH3/Screen-Shot-2020-12-04-at-4-57-26-AM.png)](https://postimg.cc/JDLpdnJz)

## ChatGUI
This is the main window for this messaging app. All communications between the users and the server are handled through this GUI. 
The user will gain access to this window once they enter valid credentials in the login window. ChatGUI is unique for each user, which means that it only includes conversations sent from or to this user and group conversations this user is in. 
There are three main panels within this chat window: a message panel (displays the message for each conversation), an inbox list (includes all conversations the user is in), and a text field (where the user types in their message). There are also instructions on how to edit and delete messages, conversations, and account information. 
The ChatGUI class also has multiple methods devoted to executing commands once an event occurs; it uses an ActionListener (which takes care of when the send and new chat buttons are pressed), a FocusListener (which takes care of setting default text), and a MouseListener (which takes care of when the user clicks on a conversation or double clicks to edit/delete).

## DisplayMessageGUI
Graphic User Interface that displays messages for each conversation. This class is basically a JPanel using JList as a means to display messages. Each panel is unique to a conversation. During initialization, the class runs through the message array of a conversation and adds each message to the list. When the user sends a new chat, this chat will be added at the end of the list. The panel will auto-scroll to the end of the list to make sure the latest message is visible.
The DisplayMessageGUI class also has a method that is dedicated to executing commands once an event occurs; it uses a MouseListener which takes care of when a user wants to edit/delete messages so that the method can then prompt the user to follow through on said command and then the class sends a message to MessageClient to take note of this on file.

[![Screen-Shot-2020-12-07-at-1-35-16-AM.png](https://i.postimg.cc/sxWXY2wZ/Screen-Shot-2020-12-07-at-1-35-16-AM.png)](https://postimg.cc/30KY73MK)

## ClientManager
A hashmap that stores user identity and thread connection. It assigns user IP/port pair as a key to the value of a MessageHandler instance.
### addTrace
A method that assigns the user IP/port pair as the key to the value of the Messagehandler instance in the hashmap.
### getDeliverTo
A method that returns the hashmap field that is storing the values of the Messagehandler instances.

## Conversation
A class that is designed to store information for one conversation. It established an object to store members in each conversation and the messages and senders of those messages of that conversation.
### Conversation Constructor #1
A constructor that takes in an ArrayList of Strings - members - as a parameter, sorts it, and then assigns it the members field. It also initializes a new ArrayList of Strings called messages.
### Conversation Constructor #2
A constructor that takes in two ArrayLists of Strings as parameters - members and messages - as parameters. It calls the previous constructor to set up the members field and then overwrites the previous constructor and initializes the messages field with the given parameter.
### removeMessageAtIndex
A method that takes in an integer index as a parameter and is used to remove a message at that given index from the messages ArrayList.
### removeMemberWithName
A method that takes in a String name as a parameter that is used to remove a member from the member’s ArrayList.
### editMessageAtIndex
A method that takes in an integer index and String message as parameters to remove a message at a given index from the messages ArrayList.
### addMessage
A method that takes in a String message as a parameter and adds it to the messages ArrayList in the format of ‘username|message’. 
### getMessages
A method that returns the messages ArrayList.
### getMembers
A method that returns the members ArrayList.
### setMessages
A method that takes in an ArrayList of Strings as a parameter - messages - and initializes that messages field with it.
### setMembers
A method that takes in an ArrayList of Strings as a parameter - members - and initializes that members field with it.

# GUIs walkthrough
When you first run the code, a login/register window will pop up. Registered accounts are provided in the Accounts.txt file but you are welcome to create your own account (please note: accounts in the Accounts.txt file come with sample conversations, which can be helpful for testing later). Invalid login or registration should prompt a pop-up message dialogue.
After you enter valid credentials, the login window closes and the main chat window will appear. Previous conversations are preloaded to the inbox list on the right. The text field is blocked until you choose a specific conversation from the inbox list. You can choose to open any conversation by clicking on its label. Switching between conversations goes the same way: simply choose the conversation you want to view. Once you have a conversation, type in your message in the text field and click the send button. Messages should appear in real-time on all sides.
You can make a new chat using the new chat button on the right, below the inbox list. A pop-up window will guide you through the process. You can only send messages to users who already have accounts in the system and cannot start a new chat with users who you are already chatting with. Attempts to do either of those will prompt an error pop-up message.
To edit or delete a message or conversation, double click on the desired message/conversation. A window will pop up and guide you through the edit/delete process. Please note, you can only edit/delete the messages you sent and can only delete conversations (cannot change label or group member for now). Any edit or deletion should appear in real-time on all sides. Delete conversations will only delete the conversation from your inbox list. The remaining members of that conversation will receive a message “System|[username] has left this chat.”. 
(The delete conversations feature is not permanent. The conversation will not appear on your list but if you rejoin the conversation, you can still view the previous messages. The delete messages feature, however, is permanent and you cannot recover the message once you delete it.)
To edit your account, click on the clickable label above the message panel. You can only change your password or delete your account. A pop-up window will guide you through the process. Once you delete your account, the remaining members of all the chat you’re in will receive a message “System|[username] has left this chat.”. You also cannot log in or register with the same username after you delete your account.
## GUI Testing
### LoginGUI
* Login and Register Buttons: 
Tested various combinations of pressing the login and register buttons to see if the program crashes or not. Originally crashed because lost socket connection but after adding a newline character, any combination of buttons pressed worked. Also tried various combinations of “illegal inputs” to make sure that proper error messages were thrown (i.e. if a user tried to login but didn’t already have an account, if a user entered the wrong password, if the user tried to register with a username that already existed, etc).
* ActionListener: 
To test the ActionListener, used print lines to make sure when the button or a label was clicked, the action listener was called and the program entered the correct method and sent multiple messages to one chat and to different chats at the same time to check if the GUI updated in real-time. When the GUI did not update, used print lines to trace the path of the message to figure out where the message failed to update. 
### ChatGUI
* ActionListener: 
To test the ActionListener, used print lines to make sure when the button or a label was clicked, the action listener was called and the program entered the correct method and sent multiple messages to one chat and to different chats at the same time to check if the GUI updated in real-time. When the GUI did not update, used print lines to trace the path of the message to figure out where the message failed to update. 
* FocusListener: 
Tested by trying to type in the text field without entering a conversation.
* MouseListener:
Tested by printing out the label of the conversation to see if the message panel matched the label or not. Also trying to single-click on conversations to make sure that the edit/delete prompt did not pop up. 
### DisplayMessageGUI
* MouseListener:
Tested by printing out the message to see if the message correctly returned. Also trying to single-click on conversations to make sure that the edit/delete prompt did not pop up. Tried to edit/delete conversations sent by others. Monitored the changes in the message. 
## Other Testing
* Tested Other Classes using a RunLocalTest class coded with JUNIT4. The class tested the existence of all classes, fields, and methods. Furthermore, the RunLocalTest class tested the functionality of non-GUI methods. 
## Server-Client Communication formats
List of all client -> messageHandler formats:
* loginRegister: L|username|password, R|username|password
* sendMessage: M|clientUsername|arrayOfMembers|message
* Update message history in chat: U<\*>currentMember1|currentMember2|currentMember3<\*>allMessages
* Delete user from chat: U<\*>currentMember1|currentMember2|currentMember3<\*>memberToDelete<\*>allMessages
* delete account: D|username
* change password: P|username|newPassword
* check if users exist: C|user1,user2,user3
List of all messageHandler -> client formats:
* initialize conversation array:
* Member1|Member2|Member3<\*>Username|Message&%Username|Message<&*>conversation2<&*>conversation3
* loginOrRegister:
* login: true if logged in, false if not, Register: true if the account exists, false if register success
* Check if user exists: true if they all exists, false if there is one thats not.





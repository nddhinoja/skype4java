/*******************************************************************************
 * Copyright (c) 2006 Koji Hisano <hisano@gmail.com>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     Koji Hisano - initial API and implementation
 *******************************************************************************/
package jp.sf.skype;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jp.sf.skype.Call.Status;
import jp.sf.skype.connector.Connector;
import jp.sf.skype.connector.ConnectorException;
import jp.sf.skype.connector.ConnectorListener;
import jp.sf.skype.connector.MessageProcessor;

public final class Skype {
    public enum OptionsPage {
        GENERAL, PRIVACY, NOTIFICATIONS, SOUNDALERTS, SOUNDDEVICES, HOTKEYS, CONNECTION, VOICEMAIL, CALLFORWARD, VIDEO, ADVANCED;
    }

    public enum Button {
        KEY_0, KEY_1, KEY_2, KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, KEY_A, KEY_B, KEY_C, KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q, KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z, KEY_SHARP(
                "#"), KEY_ASTERISK("*"), KEY_PLUS("+"), KEY_UP, KEY_DOWN, KEY_YES, KEY_NO, KEY_PAGEUP, KEY_PAGEDOWN, KEY_SKYPE;
        private String key;

        private Button() {
        }

        private Button(String key) {
            this.key = key;
        }

        private String getKey() {
            if (key != null) {
                return key;
            } else {
                return name().substring(name().indexOf('_') + 1);
            }
        }
    }

    private static ContactList contactList;
    private static Profile profile;
    private static ConnectorListener messageListener;
    private static List<MessageReceivedListener> messageReceivedListeners = new ArrayList<MessageReceivedListener>();
    private static ConnectorListener callListener;
    private static List<CallReceivedListener> callReceivedListeners = new ArrayList<CallReceivedListener>();

    public static void setDebug(boolean on) throws SkypeException {
        try {
            Connector.getInstance().setDebug(on);
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
        }
    }

    public static String getVersion() throws SkypeException {
        return Utils.getProperty("SKYPEVERSION");
    }

    public static boolean isRunning() throws SkypeException {
        try {
            return Connector.getInstance().isRunning();
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
            return false;
        }
    }

    public static void showSkypeWindow() throws SkypeException {
        Utils.executeWithErrorCheck("FOCUS");
    }

    public static void hideSkypeWindow() throws SkypeException {
        Utils.executeWithErrorCheck("MINIMIZE");
    }

    public static void showAddFriendWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN ADDAFRIEND");
    }

    public static void showAddFriendWindow(String skypeId) throws SkypeException {
        Utils.checkNotNull("skypeId", skypeId);
        Utils.executeWithErrorCheck("OPEN ADDAFRIEND " + skypeId);
    }

    public static void showChatWindow(String skypeId) throws SkypeException {
        Utils.checkNotNull("skypeId", skypeId);
        Utils.executeWithErrorCheck("OPEN IM " + skypeId);
    }

    public static void showChatWindow(String skypeId, String message) throws SkypeException {
        Utils.checkNotNull("skypeId", skypeId);
        Utils.checkNotNull("message", message);
        Utils.executeWithErrorCheck("OPEN IM " + skypeId + " " + message);
    }

    public static void showFileTransferWindow(String skypeId) throws SkypeException {
        Utils.checkNotNull("skypeId", skypeId);
        Utils.executeWithErrorCheck("OPEN FILETRANSFER " + skypeId);
    }

    public static void showFileTransferWindow(String skypeId, File folder) throws SkypeException {
        Utils.checkNotNull("skypeId", skypeId);
        Utils.checkNotNull("folder", folder);
        Utils.executeWithErrorCheck("OPEN FILETRANSFER " + skypeId + " IN " + folder);
    }

    public static void showFileTransferWindow(String[] skypeIds) throws SkypeException {
        Utils.checkNotNull("skypeIds", skypeIds);
        Utils.executeWithErrorCheck("OPEN FILETRANSFER " + Skype.toCommaSeparatedString(skypeIds));
    }

    public static void showFileTransferWindow(String[] skypeIds, File folder) throws SkypeException {
        Utils.checkNotNull("skypeIds", skypeIds);
        Utils.checkNotNull("folder", folder);
        Utils.executeWithErrorCheck("OPEN FILETRANSFER " + Skype.toCommaSeparatedString(skypeIds) + " IN " + folder);
    }

    public static void showProfileWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN PROFILE");
    }

    public static void showUserInformationWindow(String skypeId) throws SkypeException {
        Utils.checkNotNull("skypeId", skypeId);
        Utils.executeWithErrorCheck("OPEN USERINFO " + skypeId);
    }

    public static void showConferenceWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN CONFERENCE");
    }

    public static void showSearchWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN SEARCH");
    }

    public static void showOptionsWindow(OptionsPage page) throws SkypeException {
        Utils.executeWithErrorCheck("OPEN OPTIONS " + page.toString().toLowerCase());
    }

    public static void showCallHistoryTab() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN CALLHISTORY");
    }

    public static void showContactsTab() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN CONTACTS");
    }

    public static void showDialPadTab() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN DIALPAD");
    }

    public static void showSendContactsWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN SENDCONTACTS");
    }

    public static void showBlockedUsersWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN BLOCKEDUSERS");
    }

    public static void showImportContactsWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN IMPORTCONTACTS");
    }

    public static void showGettingStartedWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN GETTINGSTARTED");
    }

    public static void showRequestAuthorizationWindow(String skypeId) throws SkypeException {
        Utils.executeWithErrorCheck("OPEN AUTHORIZATION " + skypeId);
    }

    public static void pressButton(Button button) throws SkypeException {
        Utils.executeWithErrorCheck("BTN_PRESSED " + button.getKey());
    }

    public static void releaseButton(Button button) throws SkypeException {
        Utils.executeWithErrorCheck("BTN_RELEASED " + button.getKey());
    }

    public static ContactList getContactList() throws SkypeException {
        if (contactList == null) {
            contactList = new ContactList();
        }
        return contactList;
    }

    public static Call call(String[] skypeIds) throws SkypeException {
        Utils.checkNotNull("skypeIds", skypeIds);
        return call(Skype.toCommaSeparatedString(skypeIds));
    }

    public static Call call(String skypeId) throws SkypeException {
        Utils.checkNotNull("skypeIds", skypeId);
        try {
            final Call[] call = new Call[1];
            final String[] error = new String[1];
            MessageProcessor processor = new MessageProcessor() {
                public void messageReceived(String message) {
                    if (message.startsWith("CALL ")) {
                        String response = message.substring("CALL ".length());
                        String id = response.substring(0, response.indexOf(' '));
                        if (call[0] == null) {
                            call[0] = new Call(id);
                        }
                        if (call[0].getId().equals(id)) {
                            processStatus(response.substring(id.length() + 1));
                            releaseLock();
                        }
                    } else if (message.startsWith("ERROR ")) {
                        if (call[0] == null) {
                            error[0] = message.substring("ERROR ".length());
                            releaseLock();
                        }
                    }
                }

                private void processStatus(String response) {
                    if (response.startsWith("STATUS ")) {
                        Status status = Status.valueOf(response.substring("STATUS ".length()));
                        Status[] endStatusList = new Status[] { Status.FAILED, Status.FINISHED, Status.MISSED, Status.REFUSED, Status.BUSY, Status.CANCELLED };
                        for (Status endStatus : endStatusList) {
                            if (status == endStatus) {
                                processedAllMessages();
                            }
                        }
                    }
                }
            };
            Connector.getInstance().execute("CALL " + skypeId, processor);
            Utils.checkError(error[0]);
            return call[0];
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
            return null;
        }
    }

    public static Chat chat(String target) throws SkypeException {
        try {
            String responseHeader = "CHAT ";
            String response = Connector.getInstance().execute("CHAT CREATE " + target, responseHeader);
            Utils.checkError(response);
            String id = response.substring(responseHeader.length(), response.indexOf(" STATUS "));
            return new Chat(id);
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
            return null;
        }
    }

    public static VoiceMail leaveVoiceMail(String target) throws SkypeException {
        try {
            String responseHeader = "VOICEMAIL ";
            String response = Connector.getInstance().execute("VOICEMAIL " + target, responseHeader);
            Utils.checkError(response);
            String id = response.substring(responseHeader.length(), response.indexOf(' ', responseHeader.length()));
            return new VoiceMail(id);
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
            return null;
        }
    }

    public static Application addApplication(String name) throws SkypeException {
        Application application = new Application(name);
        application.initalize();
        return application;
    }

    public static String getVideoDevice() throws SkypeException {
        try {
            String responseHeader = "VIDEO_IN ";
            String response = Connector.getInstance().execute("GET VIDEO_IN", responseHeader);
            Utils.checkError(response);
            String name = response.substring(responseHeader.length());
            if (!isDefaultVideoDevice(name)) {
                return name;
            } else {
                return null;
            }
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
            return null;
        }
    }

    public static void setVideoDevice(String name) throws SkypeException {
        try {
            if (isDefaultVideoDevice(name)) {
                name = "";
            }
            String responseHeader = "VIDEO_IN ";
            String response = Connector.getInstance().execute("SET VIDEO_IN " + name, responseHeader);
            Utils.checkError(response);
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
        }
    }

    private static boolean isDefaultVideoDevice(String name) {
        return name == null || "".equals(name);
    }

    public static void openVideoTestWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN VIDEOTEST");
    }

    public static void openVideoOptionsWindow() throws SkypeException {
        Utils.executeWithErrorCheck("OPEN OPTIONS VIDEO");
    }

    private Skype() {
    }

    public static synchronized Profile getProfile() {
        if (profile == null) {
            profile = new Profile();
        }
        return profile;
    }

    public static void addMessageReceivedListener(MessageReceivedListener listener) throws SkypeException {
        Utils.checkNotNull("listener", listener);
        messageReceivedListeners.add(listener);
        try {
            if (messageListener == null) {
                messageListener = new ConnectorListener() {
                    public void messageReceived(String message) {
                        if (message.startsWith("MESSAGE ")) {
                            String data = message.substring("MESSAGE ".length());
                            String id = data.substring(0, data.indexOf(' '));
                            if (message.endsWith(" STATUS RECEIVED")) {
                                fireMessageReceived(new Message(id));
                            }
                        }
                    }
                };
                Connector.getInstance().addConnectorListener(messageListener);
            }
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
        }
    }

    public static void removeMessageReceivedListener(MessageReceivedListener listener) {
        Utils.checkNotNull("listener", listener);
        messageReceivedListeners.remove(listener);
        if (!messageReceivedListeners.isEmpty()) {
            Connector.getInstance().removeConnectorListener(messageListener);
            messageListener = null;
        }
    }

    private static void fireMessageReceived(Message message) {
        assert message != null;
        MessageReceivedListener[] listeners = messageReceivedListeners.toArray(new MessageReceivedListener[0]); // �C�x���g�ʒm���Ƀ��X�g���ύX�����\�������邽��
        for (MessageReceivedListener listener : listeners) {
            listener.messageReceived(message);
        }
    }

    public static void addCallReceivedListener(CallReceivedListener listener) throws SkypeException {
        Utils.checkNotNull("listener", listener);
        callReceivedListeners.add(listener);
        try {
            if (callListener == null) {
                callListener = new ConnectorListener() {
                    public void messageReceived(String call) {
                        if (call.startsWith("CALL ")) {
                            String data = call.substring("CALL ".length());
                            String id = data.substring(0, data.indexOf(' '));
                            if (call.endsWith(" STATUS RINGING")) {
                                fireCallReceived(new Call(id));
                            }
                        }
                    }
                };
                Connector.getInstance().addConnectorListener(callListener);
            }
        } catch (ConnectorException e) {
            Utils.convertToSkypeException(e);
        }
    }

    public static void removeCallReceivedListener(CallReceivedListener listener) {
        Utils.checkNotNull("listener", listener);
        callReceivedListeners.remove(listener);
        if (!callReceivedListeners.isEmpty()) {
            Connector.getInstance().removeConnectorListener(callListener);
            callListener = null;
        }
    }

    private static void fireCallReceived(Call call) {
        assert call != null;
        CallReceivedListener[] listeners = callReceivedListeners.toArray(new CallReceivedListener[0]); // �C�x���g�ʒm���Ƀ��X�g���ύX�����\�������邽��
        for (CallReceivedListener listener : listeners) {
            listener.callReceived(call);
        }
    }

    private static String toCommaSeparatedString(String[] array) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }
}
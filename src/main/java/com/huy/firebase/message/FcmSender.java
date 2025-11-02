package com.huy.firebase.message;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.google.auth.oauth2.GoogleCredentials;
import com.huy.constant.AppConstants;
import com.huy.entity.SessionEntity;
import com.huy.entity.UserEntity;

public class FcmSender {
	private static String getAccessToken() throws Exception {
		GoogleCredentials googleCredentials = GoogleCredentials.getApplicationDefault()
				.createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
		googleCredentials.refresh();
		return googleCredentials.getAccessToken().getTokenValue();
	}

	public static void sendMessage(String title, String message, String senderUserName, String receiverUserName, String fcmToken)
			throws Exception {
		String accessToken = getAccessToken();

		URL url = new URL("https://fcm.googleapis.com/v1/projects/" + AppConstants.fcm_project_id + "/messages:send");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Bearer " + accessToken);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoOutput(true);

		String payload = """
				{
				         "message": {
				            "token": "%s",
				            "notification": {
				              "title": "%s",
				              "body": "%s"
				            },
				            "data": {
				              "chatId": "123",
				              "senderId": "%s"
				            }
				          }
				        }
				""".formatted(fcmToken, title, message, senderUserName);

		try (OutputStream os = conn.getOutputStream()) {
			os.write(payload.getBytes(StandardCharsets.UTF_8));
		}

		int responseCode = conn.getResponseCode();
		StringBuilder responseContent = new StringBuilder();
		
		if (responseCode == HttpStatus.OK.value()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			
			while ((inputLine = br.readLine()) != null) {
				responseContent.append(inputLine);
			}
			br.close();
		}
		System.out.println("Sent fcm notify. From: " + senderUserName + ". To: "
				+ receiverUserName + ". result: " + responseContent.toString());
	}
	
	public static void sendMultiMessage(String title, String message, String senderUserName, UserEntity receiver) throws Exception {
		if (receiver == null) {
			System.out.println("error send multipel fcm: receiver is null");
			return;
		}
		List<SessionEntity> sessions = receiver.getSessions();
		if (sessions == null || sessions.isEmpty()) {
			System.out.println("error send mutlple fcm_from: " + senderUserName + " - to: " +receiver.getUserName() + "_sessions is null or empty");
			return;
		}
		for (SessionEntity session : sessions) {
			String fcmToken = session.getFcmToken();
			sendMessage(title, message, senderUserName, receiver.getUserName(), fcmToken);
		}
	}
}
